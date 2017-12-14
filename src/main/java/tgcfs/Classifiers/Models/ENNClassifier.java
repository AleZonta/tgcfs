package tgcfs.Classifiers.Models;

import lgds.trajectories.Point;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import tgcfs.Classifiers.InputNetwork;
import tgcfs.NN.EvolvableModel;
import tgcfs.NN.InputsNetwork;
import tgcfs.Networks.DeepLearning4j.MultiLayerNetworkBis;

import java.util.List;
import java.util.stream.IntStream;

/**
 * Created by Alessandro Zonta on 24/10/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 *
 *
 * This method implements the classifier for the Touring system
 * The classifier is an Elman Neural Network
 *
 * The Elman NN is implement using the encog library
 * Heaton, Jeff. “Encog: Library of Interchangeable Machine Learning Models for Java and C#.” Journal of Machine Learning Research 16 (2015): 1243-47. Print.
 *
 * The Classier is offering the methods to evolve the NN using an evolutionary algorithm
 */
public class ENNClassifier implements EvolvableModel{
    private MultiLayerNetworkBis net; //neural network, brain of the agent
    private int arrayLength; //length of the weight array
    private int input;
    private int hiddenNeurons;
    private int output;


    /**
     * Constructor of the classifier. It generates the ElmanNetwork.
     *
     * @param input         number  of nodes used as input
     * @param HiddenNeurons number of hidden layer
     * @param output        number of nodes used as output
     */
    public ENNClassifier(int input, int HiddenNeurons, int output) {
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(12345)
                .weightInit(WeightInit.XAVIER)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .learningRate(0.01)
                .list()
                .layer(0, new DenseLayer.Builder().nIn(input + HiddenNeurons).nOut(HiddenNeurons)
                        .activation(Activation.TANH)
                        .build())
                .layer(1, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD).nIn(HiddenNeurons).nOut(output)
                        .activation(Activation.TANH)
                        .build())
                .pretrain(false).backprop(true)
                .build();

        this.net = new MultiLayerNetworkBis(conf);
        this.net.init();

        this.net.setListeners(new ScoreIterationListener(20));

        this.arrayLength = this.net.numParams();
        this.input = input;
        this.hiddenNeurons = HiddenNeurons;
        this.output = output;
    }

    /**
     * Method that sets the weights to the networks
     * @param weights list containing all the weights
     * @throws Exception if the length of the list passed as parameter is not correct
     */
    public void setWeights(INDArray weights) throws Exception {
        if (weights.columns() != this.arrayLength){
            throw new Exception("Length list weights is not correct.");
        }
        //transform list to vector
        //ouble[] weightsVector = weights.stream().mapToDouble(d -> d).toArray();
        //set the weights
        //this.elmanNetwork.decodeFromArray(weightsVector);
        this.net.setParameters(weights);
    }

    /**
     * @implNote Implementation from Interface
     */
    public INDArray getWeights(){
        return this.net.params();
    }


    /**
     * @implNote Implementation from Interface
     */
    public int getArrayLength() { return this.arrayLength; }

    /**
     * @implNote Implementation from Interface
     * @return deep copy of the model
     */
    public ENNClassifier deepCopy() {
        return new ENNClassifier(this.input, this.hiddenNeurons, this.output);
    }

    /**
     * Train the neural network
     * @param input input of the network
     */
    public void fit(List<InputsNetwork> input, List<Point> points) {
        //creation of a data set of data -> use to train the network
        INDArray array = Nd4j.create(input.get(0).serialise().columns(), input.size());
        INDArray outputs = Nd4j.create(1, points.size());
        this.createOutput(input,points,array,outputs);
        for(int i = 0; i < input.size(); i++){
            this.fit(Nd4j.toFlattened(array.getColumn(i)),Nd4j.toFlattened(outputs.getColumn(i)));
        }
    }

    /**
     * fit the network
     *
     * Need to fix the elman recurrent nodes for the network
     *
     * @param inputs input of the network
     * @param labels real point of the input
     */
    public void fit(INDArray inputs, INDArray labels) {
        INDArray pastInput = this.net.getInputFirstLayer();
        //load the context value of the hidden layer
        INDArray total = Nd4j.create(1,this.input + this.hiddenNeurons);
        IntStream.range(0, this.input).forEach(i -> total.putScalar(i, inputs.getDouble(i)));
        if(pastInput == null) {
            IntStream.range(this.input, this.input + this.hiddenNeurons).forEach(i -> total.putScalar(i,0.5));
        }else{
            IntStream.range(this.input, this.input + this.hiddenNeurons).forEach(i -> total.putScalar(i,pastInput.getDouble(i-this.input)));
        }

        this.net.fit(total, labels);
    }


    /**
     * Create the INDArray from normal vector
     * @param input inputsnetwork containing the input
     * @param points real points of the inputs
     * @param array INDArray input
     * @param outputs INDArray output
     */
    protected void createOutput(List<InputsNetwork> input, List<Point> points, INDArray array, INDArray outputs){
        for(int i = 0; i < input.size(); i++) {
            INDArray l = input.get(i).serialise();
            array.putColumn(i, l);
        }

        for(int i = 0; i < points.size(); i++){
            outputs.putScalar(i, points.get(i).getLatitude());
        }
    }


    /**
     * Compute output given the time series
     * @param input list of {@link InputNetwork}
     * @return {@link INDArray}
     */
    public INDArray computeOutput(List<InputsNetwork> input) {
        INDArray array = Nd4j.create(input.get(0).serialise().columns(), input.size());
        for(int i = 0; i < input.size(); i++) {
            INDArray l = input.get(i).serialise();
            array.putColumn(i, l);
        }
        INDArray result = Nd4j.create(1, this.output);
        for(int i = 0; i < input.size(); i++){
            result = this.computeOutput(Nd4j.toFlattened(array.getColumn(i)));
        }
        return result;
    }

    /**
     * Compute the output of the network given the input
     * @param in list value that are the input of the network
     * @return list of output of the network
     */
    public INDArray computeOutput(INDArray in) {
        //check if the input is in the correct range
        for(int i = 0; i < in.columns(); i++){
            if(in.getDouble(i) < -1.0 || in.getDouble(i) > 1.0){
                throw new Error("Generator input is not normalised correctly");
            }
        }

        INDArray pastInput = this.net.getLayer(1).input();

        //load the context value of the hidden layer
        INDArray total = Nd4j.create(1,this.input + this.hiddenNeurons);
        IntStream.range(0, this.input).forEach(i -> total.putScalar(i, in.getDouble(i)));
        if(pastInput == null) {
            IntStream.range(this.input, this.input + this.hiddenNeurons).forEach(i -> total.putScalar(i,0.5));
        }else{
            IntStream.range(this.input, this.input + this.hiddenNeurons).forEach(i -> total.putScalar(i,pastInput.getDouble(i-this.input)));
        }

        return this.net.rnnTimeStep(total);
        //return this.net.output(total);
    }

    /**
     * fit the network
     * @param dataSet dataSet input of the network
     */
    public void fit(DataSet dataSet){
        this.net.fit(dataSet);
    }

    /**
     * Get string containing description of the network
     * @return String
     */
    public String getSummary() {
        return this.net.summary();
    }

    @Override
    public int getId() {
        return 0;
    }

    /**
     * Get number parameter of the network
     * @return integer number
     */
    public int getNumPar() {
        return this.net.numParams();
    }


    /**
     * clear input parameters
     */
    public void cleanParam(){
        this.net.clear();
    }
}



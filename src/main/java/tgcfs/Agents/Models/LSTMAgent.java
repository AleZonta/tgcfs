package tgcfs.Agents.Models;

import lgds.Distance.Distance;
import lgds.trajectories.Point;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.layers.GravesLSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import tgcfs.NN.EvolvableModel;
import tgcfs.NN.InputsNetwork;

import java.util.List;
import java.util.stream.IntStream;


/**
 * Created by Alessandro Zonta on 22/05/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 *
 * Class representing a fake agent implemented using LSTM Neural Network
 * The LSMT NN is implemented using the deeplearning4j library
 * Deeplearning4j Development Team. Deeplearning4j: Open-source distributed deep learning for the JVM, Apache Software Foundation License 2.0. http://deeplearning4j.org
 *
 * The LSTMAgent is offering the methods to evolve the NN using an evolutionary algorithm
 */
public class LSTMAgent implements EvolvableModel {
    private MultiLayerNetwork net; //neural network, brain of the agent
    private int inputSize;
    private int hiddenLayers;
    private int hiddenNeurons;
    private int outputSize;

    /**
     * Building of the Recurrent Neural NN.Network
     *
     * @param inputSize     integer value containing the size of the input
     * @param hiddenLayers  integer value containing how many hidden layers the network will have
     * @param hiddenNeurons integer value containing how many neurons the hidden layers will have
     * @param outputSize    integer value containing how many output neurons the network will have
     */
    public LSTMAgent(int inputSize, int hiddenLayers, int hiddenNeurons, int outputSize) {
        // some common parameters
        NeuralNetConfiguration.Builder builder = new NeuralNetConfiguration.Builder();
        builder.biasInit(0);
        builder.learningRate(0.01);
        builder.regularization(true);
        builder.l2(0.001);
        builder.updater(Updater.RMSPROP);
        builder.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT);

        builder.miniBatch(false);
        builder.weightInit(WeightInit.XAVIER);

        NeuralNetConfiguration.ListBuilder listBuilder = builder.list();

        // input Layer
        GravesLSTM.Builder inputLayerBuilder = new GravesLSTM.Builder();
        inputLayerBuilder.nIn(inputSize);
        inputLayerBuilder.nOut(hiddenNeurons);
        inputLayerBuilder.activation(Activation.SOFTSIGN);
        listBuilder.layer(0, inputLayerBuilder.build());

        // hidden Layers
        IntStream.range(1, hiddenLayers).forEach(i -> {
            GravesLSTM.Builder hiddenLayerBuilder = new GravesLSTM.Builder();
            hiddenLayerBuilder.nIn(hiddenNeurons);
            hiddenLayerBuilder.nOut(hiddenNeurons);
            hiddenLayerBuilder.activation(Activation.SOFTSIGN);
            listBuilder.layer(i, hiddenLayerBuilder.build());
        });

        // we need to use RnnOutputLayer for our RNN
        RnnOutputLayer.Builder outputLayerBuilder = new RnnOutputLayer.Builder(LossFunctions.LossFunction.MCXENT);
        outputLayerBuilder.nIn(hiddenNeurons);
        outputLayerBuilder.nOut(outputSize);
        outputLayerBuilder.activation(Activation.SOFTSIGN);
        outputLayerBuilder.lossFunction(LossFunctions.LossFunction.MSE);
        listBuilder.layer(hiddenLayers, outputLayerBuilder.build());


        listBuilder.pretrain(Boolean.FALSE);
        listBuilder.backprop(Boolean.TRUE);

        // create network
        MultiLayerConfiguration conf = listBuilder.build();
        this.net = new MultiLayerNetwork(conf);
        this.net.init();

        this.inputSize = inputSize;
        this.hiddenLayers = hiddenLayers;
        this.hiddenNeurons = hiddenNeurons;
        this.outputSize = outputSize;
    }

    /**
     * @implNote Implementation from Interface
     * @return Integer value
     */
    public int getArrayLength() {
        return this.net.numParams();
    }

    /**
     * @implNote Implementation from Interface
     * @return list weights
     */
    public INDArray getWeights() {
        return  this.net.params();
    }

    /**
     * @implNote Implementation from Interface
     * @param weights list containing all the weights
     * @throws Exception if the length of the list is not correct
     */

    public void setWeights(INDArray weights) throws Exception {
        if (weights.columns() != this.net.numParams()){
            throw new Exception("Length list weights is not correct.");
        }
        this.net.setParameters(weights);
        //automatically clear the previous status
        this.net.rnnClearPreviousState();
    }


    public void clearPreviousState() {
        this.net.rnnClearPreviousState();
    }


    /**
     * @implNote Implementation from Interface
     * @return deep copy of the model
     */
    public LSTMAgent deepCopy() {
        return new LSTMAgent(this.inputSize, this.hiddenLayers, this.hiddenNeurons, this.outputSize);
    }

    /**
     * @implNote Implementation from Interface
     * @param input input of the network
     */
    public void fit(List<InputsNetwork> input, List<Point> points) {
        //creation of a data set of data -> use to train the network
        INDArray array = Nd4j.create(input.get(0).serialise().columns(), input.size());
        INDArray outputs = Nd4j.create(input.get(0).serialise().columns(), input.size());
        this.createOutput(input,points,array,outputs);
        //input.forEach(i -> dataSet.addFeatureVector(Nd4j.create(i.serialise().stream().mapToDouble(d -> d).toArray())));

        for(int i = 0; i < input.size() - 1; i++){
            this.net.fit(Nd4j.toFlattened(array.getColumn(i)),Nd4j.toFlattened(outputs.getColumn(i + 1)));
        }
    }

    public void fit(DataSet dataSet) {
        throw new NotImplementedException();
    }

    /**
     * Compute the output of the network given the input
     * @param input list value that are the input of the network
     * @return list of output of the network
     */
    public INDArray computeOutput(INDArray input) {
        //If this MultiLayerNetwork contains one or more RNN layers: conduct forward pass (prediction) but using previous stored state for any RNN layers.
        return this.net.rnnTimeStep(input);
    }

    /**
     * Create the INDArray from normal vector
     * @param input inputsnetwork containing the input
     * @param points real points of the inputs
     * @param array INDArray input
     * @param outputs INDArray output
     */
    protected void createOutput(List<InputsNetwork> input, List<Point> points, INDArray array, INDArray outputs){
        Distance d = new Distance();
        for(int i = 0; i < input.size() - 1; i++) {
            INDArray l = input.get(i).serialise();
            array.putColumn(i, l);
            INDArray o = Nd4j.zeros(3);
            o.putScalar(0, l.getDouble(0));
            o.putScalar(1, l.getDouble(1));
            o.putScalar(2, d.compute(points.get(i+1), points.get(i+2)));
            outputs.putColumn(i, o);
        }
    }

    /**
     * Get string containing description of the network
     * @return String
     */
    public String getSummary(){
        return this.net.summary();
    }

    public int getId() {
        return 0;
    }

}

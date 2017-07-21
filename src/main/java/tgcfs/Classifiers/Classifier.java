package tgcfs.Classifiers;

import lgds.trajectories.Point;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import tgcfs.NN.EvolvableNN;
import tgcfs.NN.InputsNetwork;
import tgcfs.NN.Models;

import java.util.List;
import java.util.stream.IntStream;

/**
 * Created by Alessandro Zonta on 18/05/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 *
 * This method implements the classifier for the Touring system
 * The classifier is an Elman Neural Network
 *
 * The Elman NN is implement using the encog library
 * Heaton, Jeff. “Encog: Library of Interchangeable Machine Learning Models for Java and C#.” Journal of Machine Learning Research 16 (2015): 1243-47. Print.
 *
 * The Classier is offering the methods to evolve the NN using an evolutionary algorithm
 */
public class Classifier extends Models implements EvolvableNN {
    //private BasicNetwork elmanNetwork; //the neural network used for the classifier
    private MultiLayerNetwork net; //neural network, brain of the agent
    private Integer arrayLength; //length of the weight array
    private Integer input;
    private Integer hiddenNeurons;
    private Integer output;

    /**
     * Constructor of the classifier. It generates the ElmanNetwork.
     * @param input number  of nodes used as input
     * @param HiddenNeurons number of hidden layer
     * @param output number of nodes used as output
     */
    public Classifier(Integer input, Integer HiddenNeurons, Integer output){
        super();

        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .weightInit(WeightInit.XAVIER)
                .activation(Activation.HARDTANH)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .learningRate(0.05)
                .list()
                .layer(0, new DenseLayer.Builder().nIn(input + HiddenNeurons).nOut(HiddenNeurons)
                        .build())
                .layer(1, new DenseLayer.Builder().nIn(HiddenNeurons).nOut(output)
                        .build())
                .build();

        this.net = new MultiLayerNetwork(conf);
        this.net.init();






//        ElmanPattern pattern = new ElmanPattern();
//        pattern.setActivationFunction(new ActivationTANH());
//        pattern.setInputNeurons(input);
//        pattern.addHiddenLayer(HiddenNeurons);
//        pattern.setOutputNeurons(output);
//        this.elmanNetwork = (BasicNetwork) pattern.generate();
//        this.arrayLength = this.elmanNetwork.encodedArrayLength(); //get the length of the array
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
    @Override
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
    @Override
    public INDArray getWeights(){
        return this.net.params();
    }


    /**
     * @implNote Implementation from Interface
     */
    @Override
    public INDArray computeOutput(INDArray input){
        //check if the input is in the correct range
        for(int i = 0; i < input.columns(); i++){
            if(input.getDouble(i) < -1.0 || input.getDouble(i) > 1.0){
                throw new Error("Generator input is not normalised correctly");
            }
        }

        INDArray pastInput = this.net.getLayer(1).input();

        //load the context value of the hidden layer
        INDArray total = Nd4j.create(1,this.input + this.hiddenNeurons);
        IntStream.range(0, this.input).forEach(i -> {
            total.putScalar(i,input.getDouble(i));
        });
        if(pastInput == null) {
            IntStream.range(this.input, this.input + this.hiddenNeurons).forEach(i -> {
                total.putScalar(i,0.0);
            });
        }else{
            IntStream.range(this.input, this.input + this.hiddenNeurons).forEach(i -> {
                total.putScalar(i,pastInput.getDouble(i-this.input));
            });
        }

        return this.net.rnnTimeStep(total);
    }

    /**
     * @implNote Implementation from Interface
     */
    @Override
    public Integer getArrayLength() { return this.arrayLength; }

    /**
     * @implNote Implementation from Interface
     * @return deep copy of the model
     */
    @Override
    public EvolvableNN deepCopy() {
        return new Classifier(this.input, this.hiddenNeurons, this.output);
    }

    /**
     * Train the neural network
     * @param input input of the network
     */
    @Override
    public void fit(List<InputsNetwork> input, List<Point> points) {
        throw new Error("Not Implemented - Not Necessary for the Classifier");
    }
}

package tgcfs.Agents;

import lgds.Distance.Distance;
import lgds.trajectories.Point;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.GravesLSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import tgcfs.NN.EvolvableNN;
import tgcfs.NN.InputsNetwork;
import tgcfs.NN.Models;

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
public class LSTMAgent extends Models implements EvolvableNN {
    private MultiLayerNetwork net; //neural network, brain of the agent
    private Integer inputSize;
    private Integer hiddenLayers;
    private Integer hiddenNeurons;
    private Integer outputSize;

    /**
     * Constructor that call the father class constructor
     */
    public LSTMAgent(){
        super();
    }


    /**
     * Constructor that calls the method to build the RNN
     * @param inputSize integer value containing the size of the input
     * @param hiddenLayers integer value containing how many hidden layers the network will have
     * @param hiddenNeurons integer value containing how many neurons the hidden layers will have
     * @param outputSize integer value containing how many output neurons the network will have
     */
    public LSTMAgent(Integer inputSize, Integer hiddenLayers, Integer hiddenNeurons, Integer outputSize){
        super();
        this.loadLSTM(inputSize,hiddenLayers,hiddenNeurons,outputSize);
        this.inputSize = inputSize;
        this.hiddenLayers = hiddenLayers;
        this.hiddenNeurons = hiddenNeurons;
        this.outputSize = outputSize;
    }

    /**
     * Building of the Recurrent Neural Network
     * @param inputSize integer value containing the size of the input
     * @param hiddenLayers integer value containing how many hidden layers the network will have
     * @param hiddenNeurons integer value containing how many neurons the hidden layers will have
     * @param outputSize integer value containing how many output neurons the network will have
     */
    private void loadLSTM(Integer inputSize, Integer hiddenLayers, Integer hiddenNeurons, Integer outputSize){

//        //Initialize the user interface backend
//        UIServer uiServer = UIServer.getInstance();
//        //Configure where the network information (gradients, score vs. time etc) is to be stored. Here: store in memory.
//        StatsStorage statsStorage = new InMemoryStatsStorage();
//        //Attach the StatsStorage instance to the UI: this allows the contents of the StatsStorage to be visualized
//        uiServer.attach(statsStorage);


        // some common parameters
        NeuralNetConfiguration.Builder builder = new NeuralNetConfiguration.Builder();
        builder.biasInit(0);
        builder.learningRate(0.001);
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
        listBuilder.layer(hiddenLayers, outputLayerBuilder.build());


        // create network
        MultiLayerConfiguration conf = listBuilder.build();
        this.net = new MultiLayerNetwork(conf);
        this.net.init();


//        this.net.setListeners(new StatsListener(statsStorage));
    }

    /**
     * @implNote Implementation from Interface
     * @return Integer value
     */
    @Override
    public Integer getArrayLength(){
        return this.net.numParams();
    }

    /**
     * @implNote Implementation from Interface
     * @return list weights
     */
    @Override
    public INDArray getWeights(){
        return  this.net.params();
    }


    /**
     * @implNote Implementation from Interface
     * @param weights list containing all the weights
     * @throws Exception if the length of the list is not correct
     */
    @Override
    public void setWeights(INDArray weights) throws Exception {
        if (weights.columns() != this.net.numParams()){
            throw new Exception("Length list weights is not correct.");
        }
        this.net.setParameters(weights);
        //automatically clear the previous status
        this.clearPreviousState();
    }

    /**
     * @implNote Implementation from Interface
     * @param input list value that are the input of the network
     * @return list of output of the network
     */
    @Override
    public INDArray computeOutput(INDArray input) {
        //check if the input is in the correct range
        for(int i = 0; i < input.columns(); i++){
            if(input.getDouble(i) < -1.0 || input.getDouble(i) > 1.0){
                throw new Error("Generator input is not normalised correctly");
            }
        }
        //If this MultiLayerNetwork contains one or more RNN layers: conduct forward pass (prediction) but using previous stored state for any RNN layers.
        return this.net.rnnTimeStep(input);
    }

    /**
     * clear the rnn state from the previous instance
     */
    public void clearPreviousState(){
        this.net.rnnClearPreviousState();
    }

    /**
     * @implNote Implementation from Interface
     * @return deep copy of the model
     */
    @Override
    public EvolvableNN deepCopy() {
        return new LSTMAgent(this.inputSize, this.hiddenLayers, this.hiddenNeurons, this.outputSize);
    }


    /**
     * @implNote Implementation from Interface
     * @param input input of the network
     */
    public void fit(List<InputsNetwork> input, List<Point> points){
        //creation of a data set of data -> use to train the network
        DataSet dataSet;

        Distance d = new Distance();

        INDArray array = Nd4j.create(input.get(0).serialise().columns());
        INDArray outputs = Nd4j.create(input.get(0).serialise().columns());
        for(int i = 0; i < input.size() - 1; i++) {
            INDArray l = input.get(i).serialise();
            array.putRow(i, l);
            INDArray o = Nd4j.zeros(3);
            o.putScalar(0, l.getDouble(0));
            o.putScalar(1, l.getDouble(1));
            o.putScalar(2, d.compute(points.get(i+1), points.get(i+2)));
            outputs.putRow(i, o);
        }
        dataSet = new DataSet(array,outputs);
        //input.forEach(i -> dataSet.addFeatureVector(Nd4j.create(i.serialise().stream().mapToDouble(d -> d).toArray())));

        this.net.fit(dataSet);

    }
}

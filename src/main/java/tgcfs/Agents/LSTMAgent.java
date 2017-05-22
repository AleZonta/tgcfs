package tgcfs.Agents;

import org.apache.commons.lang3.ArrayUtils;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.GravesLSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import tgcfs.NN.EvolvableNN;

import java.util.Arrays;
import java.util.List;


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
 *
 */
public class LSTMAgent extends BasicAgent implements EvolvableNN {
    private MultiLayerNetwork net; //neural network, brain of the agent

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
        this.loadLSTM(inputSize,hiddenLayers,hiddenNeurons,outputSize);
    }

    /**
     * Building of the Recurrent Neural Network
     * @param inputSize integer value containing the size of the input
     * @param hiddenLayers integer value containing how many hidden layers the network will have
     * @param hiddenNeurons integer value containing how many neurons the hidden layers will have
     * @param outputSize integer value containing how many output neurons the network will have
     */
    private void loadLSTM(Integer inputSize, Integer hiddenLayers, Integer hiddenNeurons, Integer outputSize){
        // some common parameters
        NeuralNetConfiguration.Builder builder = new NeuralNetConfiguration.Builder();
        builder.biasInit(0);
        builder.weightInit(WeightInit.XAVIER);

        NeuralNetConfiguration.ListBuilder listBuilder = builder.list();

        // input Layer
        GravesLSTM.Builder inputLayerBuilder = new GravesLSTM.Builder();
        inputLayerBuilder.nIn(inputSize);
        inputLayerBuilder.nOut(hiddenNeurons);
        inputLayerBuilder.activation(Activation.TANH);
        listBuilder.layer(0, inputLayerBuilder.build());

        // hidden Layers
        for (int i = 1; i < hiddenLayers + 1; i++){
            GravesLSTM.Builder hiddenLayerBuilder = new GravesLSTM.Builder();
            hiddenLayerBuilder.nIn(hiddenNeurons);
            hiddenLayerBuilder.nOut(hiddenNeurons);
            hiddenLayerBuilder.activation(Activation.TANH);
            listBuilder.layer(i, hiddenLayerBuilder.build());
        }

        // we need to use RnnOutputLayer for our RNN
        RnnOutputLayer.Builder outputLayerBuilder = new RnnOutputLayer.Builder(LossFunctions.LossFunction.MCXENT);
        outputLayerBuilder.nIn(hiddenNeurons);
        outputLayerBuilder.nOut(outputSize);
        listBuilder.layer(hiddenLayers + 1, outputLayerBuilder.build());

        // create network
        MultiLayerConfiguration conf = listBuilder.build();
        this.net = new MultiLayerNetwork(conf);
        this.net.init();

    }

    /**
     * @implNote Implementation from Interface
     */
    public Integer getArrayLength(){
        return this.net.numParams();
    }

    /**
     * @implNote Implementation from Interface
     */
    public List<Double> getWeights(){
        INDArray result = this.net.params();
        double[] res = result.data().asDouble();
        return Arrays.asList(ArrayUtils.toObject(res));
    }


    /**
     * @implNote Implementation from Interface
     */
    public void setWeights(List<Double> weights) throws Exception {
        if (weights.size() != this.net.numParams()){
            throw new Exception("Length list weights is not correct.");
        }
        this.net.setParameters(Nd4j.create(weights.stream().mapToDouble(d -> d).toArray()));
    }



}

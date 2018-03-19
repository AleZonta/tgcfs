package tgcfs.Networks;

import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import tgcfs.NN.Models;

/**
 * Created by Alessandro Zonta on 06/03/2018.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 */
public class NN extends Models implements Network{
    protected MultiLayerNetwork net; //neural network, brain of the agent
    protected int inputSize;
    protected int hiddenLayers;
    protected int hiddenNeurons;
    protected int outputSize;

    /**
     * Default constructor
     */
    public NN(){

    }

    /**
     * Building of the Neural NN.Network
     * @param inputSize integer value containing the size of the input
     * @param hiddenLayers integer value containing how many hidden layers the network will have
     * @param hiddenNeurons integer value containing how many neurons the hidden layers will have
     * @param outputSize integer value containing how many output neurons the network will have
     */
    public NN(int inputSize, int hiddenLayers, int hiddenNeurons, int outputSize){
        super();
        // some common parameters
        NeuralNetConfiguration.Builder builder = new NeuralNetConfiguration.Builder();
        builder.biasInit(0);
        builder.learningRate(0.01);
        builder.regularization(true);
        builder.l2(0.001);

        builder.weightInit(WeightInit.XAVIER);

        NeuralNetConfiguration.ListBuilder listBuilder = builder.list();

        // input Layer
        DenseLayer.Builder inputLayerBuilder = new DenseLayer.Builder();
        inputLayerBuilder.nIn(inputSize);
        inputLayerBuilder.nOut(hiddenNeurons);
        inputLayerBuilder.activation(Activation.TANH);
        listBuilder.layer(0, inputLayerBuilder.build());

        // hidden Layers
        for(int i = 1; i < hiddenLayers; i ++){
            DenseLayer.Builder hiddenLayerBuilder = new DenseLayer.Builder();
            hiddenLayerBuilder.nIn(hiddenNeurons);
            hiddenLayerBuilder.nOut(hiddenNeurons);
            hiddenLayerBuilder.activation(Activation.TANH);
            listBuilder.layer(i, hiddenLayerBuilder.build());
        }

        // we need to use RnnOutputLayer for our RNN
        OutputLayer.Builder outputLayerBuilder = new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD);
        outputLayerBuilder.nIn(hiddenNeurons);
        outputLayerBuilder.nOut(outputSize);
        outputLayerBuilder.activation(Activation.SIGMOID);
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

    @Override
    public INDArray computeOutput(INDArray input) {
        return this.net.output(input);
    }

    @Override
    public void fit(INDArray inputs, INDArray labels) {
        this.net.fit(inputs, labels);
    }

    @Override
    public String getSummary() {
        return this.net.summary();
    }

    @Override
    public int getNumPar() {
        return this.net.numParams();
    }

    public INDArray getParameters() {
        return this.net.params();
    }

    public void setWeight(INDArray par) {
        this.net.setParameters(par);
    }
}

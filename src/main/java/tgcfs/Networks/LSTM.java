package tgcfs.Networks;

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
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import tgcfs.NN.InputsNetwork;
import tgcfs.NN.Models;

import java.util.List;

/**
 * Created by Alessandro Zonta on 08/08/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 *
 * Class implementing the LSTM neural Networks
 */
public class LSTM extends Models implements Network{
    protected MultiLayerNetwork net; //neural network, brain of the agent
    protected int inputSize;
    protected int hiddenLayers;
    protected int hiddenNeurons;
    protected int outputSize;

    /**
     * Building of the Recurrent Neural NN.Network
     * @param inputSize integer value containing the size of the input
     * @param hiddenLayers integer value containing how many hidden layers the network will have
     * @param hiddenNeurons integer value containing how many neurons the hidden layers will have
     * @param outputSize integer value containing how many output neurons the network will have
     */
    public LSTM(int inputSize, int hiddenLayers, int hiddenNeurons, int outputSize){
        super();
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
        for(int i = 1; i < hiddenLayers; i ++){
            GravesLSTM.Builder hiddenLayerBuilder = new GravesLSTM.Builder();
            hiddenLayerBuilder.nIn(hiddenNeurons);
            hiddenLayerBuilder.nOut(hiddenNeurons);
            hiddenLayerBuilder.activation(Activation.SOFTSIGN);
            listBuilder.layer(i, hiddenLayerBuilder.build());
        }

        // we need to use RnnOutputLayer for our RNN
        RnnOutputLayer.Builder outputLayerBuilder = new RnnOutputLayer.Builder(LossFunctions.LossFunction.MCXENT);
        outputLayerBuilder.nIn(hiddenNeurons);
        outputLayerBuilder.nOut(outputSize);
        outputLayerBuilder.activation(Activation.IDENTITY);
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
     * Building of the Recurrent Neural NN.Network
     * @param inputSize integer value containing the size of the input
     * @param hiddenLayers integer value containing how many hidden layers the network will have
     * @param hiddenNeurons integer value containing how many neurons the hidden layers will have
     * @param outputSize integer value containing how many output neurons the network will have
     * @param activationFunctionOutput activation function output network
     */
    public LSTM(int inputSize, int hiddenLayers, int hiddenNeurons, int outputSize, Activation activationFunctionOutput){
        super();
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
        for(int i = 1; i < hiddenLayers; i ++){
            GravesLSTM.Builder hiddenLayerBuilder = new GravesLSTM.Builder();
            hiddenLayerBuilder.nIn(hiddenNeurons);
            hiddenLayerBuilder.nOut(hiddenNeurons);
            hiddenLayerBuilder.activation(Activation.SOFTSIGN);
            listBuilder.layer(i, hiddenLayerBuilder.build());
        }

        // we need to use RnnOutputLayer for our RNN
        RnnOutputLayer.Builder outputLayerBuilder = new RnnOutputLayer.Builder(LossFunctions.LossFunction.MCXENT);
        outputLayerBuilder.nIn(hiddenNeurons);
        outputLayerBuilder.nOut(outputSize);
        outputLayerBuilder.activation(activationFunctionOutput);
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
     * Compute the output of the network given the input
     * @param input list value that are the input of the network
     * @return list of output of the network
     */
    @Override
    public INDArray computeOutput(INDArray input) {
        //If this MultiLayerNetwork contains one or more RNN layers: conduct forward pass (prediction) but using previous stored state for any RNN layers.
        return this.net.rnnTimeStep(input);
    }


    /**
     * Get number parameter of the network
     * @return integer number
     */
    @Override
    public int getNumPar(){
        return this.net.numParams();
    }

    /**
     * fit the network
     * @param inputs input of the network
     * @param labels real point of the input
     */
    @Override
    public void fit(INDArray inputs, INDArray labels){
        this.net.fit(inputs, labels);
    }


    /**
     * Get string containing description of the network
     * @return String
     */
    @Override
    public String getSummary(){
        return this.net.summary();
    }


    /**
     * clear the rnn state from the previous instance
     */
    public void clearPreviousState(){
        this.net.rnnClearPreviousState();
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

}

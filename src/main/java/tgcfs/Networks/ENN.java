package tgcfs.Networks;

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
import tgcfs.NN.Models;
import tgcfs.Networks.DeepLearning4j.MultiLayerNetworkBis;

import java.util.stream.IntStream;

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
 * Class implementing the Elman Neural Network
 */
public class ENN extends Models implements Network {
    protected MultiLayerNetworkBis net; //neural network, brain of the agent
    protected int arrayLength; //length of the weight array
    protected int input;
    protected int hiddenNeurons;
    protected int output;

    /**
     * Default constructor
     */
    public ENN(){ }

    /**
     * Constructor of the classifier. It generates the ElmanNetwork.
     * @param input number  of nodes used as input
     * @param HiddenNeurons number of hidden layer
     * @param output number of nodes used as output
     */
    public ENN(int input, int HiddenNeurons, int output){
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
     * Compute the output of the network given the input
     * @param in list value that are the input of the network
     * @return list of output of the network
     */
    @Override
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
     *
     * Need to fix the elman recurrent nodes for the network
     *
     * @param inputs input of the network
     * @param labels real point of the input
     */
    @Override
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
    @Override
    public String getSummary() {
        return this.net.summary();
    }

    /**
     * Get number parameter of the network
     * @return integer number
     */
    @Override
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

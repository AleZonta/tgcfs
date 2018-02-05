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
import tgcfs.Config.ReadConfig;
import tgcfs.NN.Models;
import tgcfs.Networks.DeepLearning4j.MultiLayerNetworkBis;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;
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

    private final Logger logger = Logger.getLogger(ENN.class.getName()); //logger for this class

    /**
     * Default constructor
     */
    public ENN(){

    }

    /**
     * Constructor of the classifier. It generates the ElmanNetwork.
     * @param input number  of nodes used as input
     * @param HiddenNeurons number of hidden layer
     * @param output number of nodes used as output
     */
    public ENN(int input, int HiddenNeurons, int output){
        Handler consoleHandler = new ConsoleHandler();

        String finalPath = null;
        try {
            finalPath = ReadConfig.Configurations.getPath() + "/Experiment-" + ReadConfig.Configurations.getName();

            new File(finalPath).mkdirs();
            finalPath += "/" + ReadConfig.Configurations.getExperiment();
            new File(finalPath).mkdirs();
            finalPath += "/classifierENN.log";
            Handler fileHandler  = null;
            try {
                fileHandler = new FileHandler(finalPath);
                // Setting formatter to the handler
                // Creating SimpleFormatter
                Formatter simpleFormatter = new SimpleFormatter();
                fileHandler.setFormatter(simpleFormatter);
                this.logger.addHandler(consoleHandler);
                this.logger.addHandler(fileHandler);

                this.logger.removeHandler(consoleHandler);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }



        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(12345)
                .weightInit(WeightInit.XAVIER)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .learningRate(0.01)
                .biasInit(0.0)
                .list()
                .layer(0, new DenseLayer.Builder().nIn(input + HiddenNeurons).nOut(HiddenNeurons)
                        .activation(Activation.TANH)
                        .build())
                .layer(1, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD).nIn(HiddenNeurons).nOut(output)
                        .activation(Activation.SIGMOID)
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
        for (int i = 0; i < in.columns(); i++) {
            if (in.getDouble(i) < -1.0 || in.getDouble(i) > 1.0) {
                throw new Error("Generator input is not normalised correctly");
            }
        }

        List<Double> a = new ArrayList<>();
        for (int i = 0; i < in.columns(); i++) {
            a.add(in.getDouble(i));
        }
        logger.log(Level.INFO, "Input\n" + a.toString());

        INDArray pastInput = this.net.getLayer(1).input();


        a = new ArrayList<>();
        if (pastInput != null) {
            for (int i = 0; i < pastInput.columns(); i++) {
                a.add(pastInput.getDouble(i));
            }
            logger.log(Level.INFO, "Past Input\n" + a.toString());
        }


        //load the context value of the hidden layer
        INDArray total = Nd4j.create(1, this.input + this.hiddenNeurons);
        IntStream.range(0, this.input).forEach(i -> total.putScalar(i, in.getDouble(i)));
        int j = 0;
        for (int i = this.input; i < this.input + this.hiddenNeurons; i++) {
            if (pastInput == null) {
                total.putScalar(i, 0.5);
            }else{
                total.putScalar(i, pastInput.getDouble(j));
                j++;
            }
        }

        a = new ArrayList<>();
        for (int i = 0; i < total.columns(); i++) {
            a.add(total.getDouble(i));
        }
        logger.log(Level.INFO, "Total Input\n" + a.toString());

//        return this.net.rnnTimeStep(total);
        return this.net.output(total);
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

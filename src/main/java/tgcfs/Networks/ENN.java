package tgcfs.Networks;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import tgcfs.NN.Models;

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
    protected MultiLayerNetwork net; //neural network, brain of the agent
    protected Integer arrayLength; //length of the weight array
    protected Integer input;
    protected Integer hiddenNeurons;
    protected Integer output;

    /**
     * Constructor of the classifier. It generates the ElmanNetwork.
     * @param input number  of nodes used as input
     * @param HiddenNeurons number of hidden layer
     * @param output number of nodes used as output
     */
    public ENN(Integer input, Integer HiddenNeurons, Integer output){
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


        //elman neural network requires the weight of the recurrent connection fixed to 1
        INDArray weights = this.net.getLayer(0).paramTable().get("W");
        for(int i = input; i < input + HiddenNeurons; i++){
            if(HiddenNeurons == 1) {
                weights.putScalar(i, 1.0);
            }else{
                for(int j = 0; j < HiddenNeurons; j++){
                    weights.getColumn(j).putScalar(i, 1.0);
                }
            }
        }


        this.arrayLength = this.net.numParams();
        this.input = input;
        this.hiddenNeurons = HiddenNeurons;
        this.output = output;
    }


    /**
     * Compute the output of the network given the input
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
     * fit the network
     * @param inputs input of the network
     * @param labels real point of the input
     */
    @Override
    public void fit(INDArray inputs, INDArray labels) {
        throw new NoSuchMethodError("Method not implemented");
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
    public Integer getNumPar() {
        return this.net.numParams();
    }
}

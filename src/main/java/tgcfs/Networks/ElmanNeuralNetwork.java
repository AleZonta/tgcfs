package tgcfs.Networks;

import org.apache.commons.lang3.StringUtils;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import tgcfs.NN.Models;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Created by Alessandro Zonta on 05/02/2018.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 */
public class ElmanNeuralNetwork  extends Models implements Network {
    protected int arrayLength; //length of the weight array
    protected int input;
    protected int hiddenNeurons;
    protected int output;
    private int parameterFirstLayer;
    private int parameterHiddenLayer;
    private int realInputLength;
    private int realHiddenLength;
    private INDArray oldInput;
    private double biasValue;
    private INDArray parameters;


    /**
     * Default constructor
     */
    public ElmanNeuralNetwork() {}



    /**
     * Constructor of the classifier. It generates the ElmanNetwork.
     * @param input number  of nodes used as input
     * @param HiddenNeurons number of hidden layer
     * @param output number of nodes used as output
     */
    public ElmanNeuralNetwork(int input, int HiddenNeurons, int output){
        this.input = input;
        this.hiddenNeurons = HiddenNeurons;
        this.output = output;


        // size input layer = input + bias + context neurons
        this.realInputLength = this.input + 1 + this.hiddenNeurons;

        // size hidden layer = hidden neurons + bias
        this.realHiddenLength = this.hiddenNeurons + 1;
        // num parameter first layer = inputLayer * this.hiddenNeurons
        this.parameterFirstLayer = this.realInputLength * this.hiddenNeurons;
        // num parameter hidden layer = hiddenNeurons * output
        this.parameterHiddenLayer = this.realHiddenLength * output;

        // total length
        this.arrayLength = this.parameterFirstLayer + this.parameterHiddenLayer;

        //vector total parameter
        this.parameters = Nd4j.rand(1, this.arrayLength);

        //old Input is null
        this.oldInput = null;

        this.biasValue = 0.0;
    }

    /**
     * Compute the output of the network given the input
     * It compute all the steps of the network
     * @param input list value that are the input of the network
     * @return list of output of the network
     */
    @Override
    public INDArray computeOutput(INDArray input) {

        List<Double> a = new ArrayList<>();
        for (int i = 0; i < input.columns(); i++) {
            a.add(input.getDouble(i));
        }


        a = new ArrayList<>();
        if (oldInput != null) {
            for (int i = 0; i < oldInput.columns(); i++) {
                a.add(oldInput.getDouble(i));
            }
        }

        //create real input
        INDArray realInput = Nd4j.create(1, this.input + this.hiddenNeurons);
        IntStream.range(0, this.input).forEach(i -> realInput.putScalar(i, input.getDouble(i)));
        int j = 0;
        for (int i = this.input; i < this.input + this.hiddenNeurons; i++) {
            if (oldInput == null) {
                realInput.putScalar(i, 0.5);
            }else{
                realInput.putScalar(i, oldInput.getDouble(j));
                j++;
            }
        }


        a = new ArrayList<>();
        for (int i = 0; i < realInput.columns(); i++) {
            a.add(realInput.getDouble(i));
        }

        //compute the hidden input
        INDArray hiddenInput = Nd4j.create(1, this.hiddenNeurons);
        for (int i = 0; i < hiddenInput.columns(); i++){
            double value = 0.0;
            for(int k = 0; k < this.realInputLength; k++){
                double in;
                if(k >= realInput.columns()){
                    // bias
                    in = this.biasValue;
                }else{
                    in = realInput.getDouble(k);
                }
                value += in * this.parameters.getDouble(k + (this.hiddenNeurons * i));
            }
            hiddenInput.putScalar(i, this.tanhFunction(value));
        }
        //save the hidden values
        this.oldInput = hiddenInput.dup();


        //compute the output
        INDArray outputLayer = Nd4j.create(1, this.output);
        for (int i = 0; i < outputLayer.columns(); i++){
            double value = 0.0;
            for(int k = 0; k < this.realHiddenLength; k++){
                double in;
                if(k >= hiddenInput.columns()){
                    // bias
                    in = this.biasValue;
                }else{
                    in = hiddenInput.getDouble(k);
                }
                value += in * this.parameters.getDouble(k + (this.output * i) + this.parameterFirstLayer);
            }
            outputLayer.putScalar(i, this.sigmoidFunction(value));
        }

        return outputLayer;
    }

    @Override
    public void fit(INDArray inputs, INDArray labels) {
        throw new Error("Not implemented");
    }

    /**
     * Get string containing description of the network
     * @return String
     */
    @Override
    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append(StringUtils.repeat("=", 140));
        sb.append("\n");
        sb.append("input -> ");
        sb.append(this.input);
        sb.append("  |   ");
        sb.append("output -> ");
        sb.append(this.output);
        sb.append("  |   ");
        sb.append("hiddenNeurons -> ");
        sb.append(this.hiddenNeurons);
        sb.append("\n");
        sb.append(StringUtils.repeat("-", 140));
        sb.append("\n");
        sb.append("parameterFirstLayer -> ");
        sb.append(this.parameterFirstLayer);
        sb.append("  |   ");
        sb.append("parameterHiddenLayer -> ");
        sb.append(this.parameterHiddenLayer);
        sb.append("  |   ");
        sb.append("totalParameters -> ");
        sb.append(this.parameters.columns());
        sb.append("\n");
        sb.append(StringUtils.repeat("=", 140));
        return sb.toString();
    }

    /**
     * Get number parameter of the network
     * @return integer number
     */
    @Override
    public int getNumPar() {
        return this.parameters.columns();
    }


    /**
     * Implementation of the TANH activation function
     * @param val double val to activate
     * @return activated double value
     */
    private double tanhFunction(double val){
        return Math.tanh(val);
    }

    /**
     * Implementaiton of the Sigmoid Activation Function
     * @param val double val to activate
     * @return activated double value
     */
    private double sigmoidFunction(double val)
    {
        return 1 / (1 + Math.exp(-val));
    }

    /**
     * clear input parameters
     */
    public void cleanParam(){
        this.oldInput = null;
    }


    /**
     * Set the parameters of the network
     * @param parameters {@link INDArray} array
     */
    public void setParameters(INDArray parameters){
        this.parameters = parameters;
    }

    /**
     * Getter for the parameters
     * @return {@link INDArray} array
     */
    public INDArray getParameters() {
        return this.parameters;
    }
}

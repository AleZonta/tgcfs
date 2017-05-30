package tgcfs.Classifiers;

import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.pattern.ElmanPattern;
import tgcfs.NN.EvolvableNN;
import tgcfs.NN.Models;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

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
    private BasicNetwork elmanNetwork; //the neural network used for the classifier
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
        ElmanPattern pattern = new ElmanPattern();
        pattern.setActivationFunction(new ActivationSigmoid());
        pattern.setInputNeurons(input);
        pattern.addHiddenLayer(HiddenNeurons);
        pattern.setOutputNeurons(output);
        this.elmanNetwork = (BasicNetwork) pattern.generate();
        this.arrayLength = this.elmanNetwork.encodedArrayLength(); //get the length of the array
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
    public void setWeights(List<Double> weights) throws Exception {
        if (weights.size() != this.arrayLength){
            throw new Exception("Length list weights is not correct.");
        }
        //transform list to vector
        double[] weightsVector = weights.stream().mapToDouble(d -> d).toArray();
        //set the weights
        this.elmanNetwork.decodeFromArray(weightsVector);
    }


    /**
     * @implNote Implementation from Interface
     */
    @Override
    public List<Double> getWeights(){
        double[] weightsVector = new double[this.arrayLength];
        //get the weights
        this.elmanNetwork.encodeToArray(weightsVector);
        return DoubleStream.of(weightsVector).boxed().collect(Collectors.toCollection(ArrayList::new));
    }


    /**
     * @implNote Implementation from Interface
     */
    @Override
    public List<Double> computeOutput(List<Double> input){
        //transform list to vector
        double[] inputVector = input.stream().mapToDouble(d -> d).toArray();
        double[] outputVector = new double[this.arrayLength];
        //compute the output
        this.elmanNetwork.compute(inputVector, outputVector);
        return DoubleStream.of(outputVector).boxed().collect(Collectors.toCollection(ArrayList::new));
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
}

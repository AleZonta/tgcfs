package tgcfs.Classifiers.Models;

import lgds.trajectories.Point;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import tgcfs.Classifiers.InputNetwork;
import tgcfs.NN.EvolvableModel;
import tgcfs.NN.InputsNetwork;
import tgcfs.Networks.ElmanNeuralNetwork;

import java.util.List;

/**
 * Created by Alessandro Zonta on 24/10/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 *
 *
 * This method implements the classifier for the Touring system
 * The classifier is an Elman Neural Network
 *
 * The Elman NN is implement using the encog library
 * Heaton, Jeff. “Encog: Library of Interchangeable Machine Learning Models for Java and C#.” Journal of Machine Learning Research 16 (2015): 1243-47. Print.
 *
 * The Classier is offering the methods to evolve the NN using an evolutionary algorithm
 */
public class ENNClassifier extends ElmanNeuralNetwork implements EvolvableModel {
    /**
     * Default constructor
     */
    public ENNClassifier(){}


    /**
     * Constructor of the classifier. It generates the ElmanNetwork.
     *
     * @param input         number  of nodes used as input
     * @param HiddenNeurons number of hidden layer
     * @param output        number of nodes used as output
     */
    public ENNClassifier(int input, int HiddenNeurons, int output) {
        super(input, HiddenNeurons, output);
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
//        this.net.setParameters(weights);
        this.setParameters(weights);
    }

    /**
     * @implNote Implementation from Interface
     */
    @Override
    public INDArray getWeights(){
//        return this.net.params();
        return this.getParameters();
    }


    /**
     * @implNote Implementation from Interface
     */
    @Override
    public int getArrayLength() { return this.arrayLength; }

    /**
     * @implNote Implementation from Interface
     * @return deep copy of the model
     */
    @Override
    public EvolvableModel deepCopy() {
        return new ENNClassifier(this.input, this.hiddenNeurons, this.output);
    }

    /**
     * Train the neural network
     * @param input input of the network
     */
    @Override
    public void fit(List<InputsNetwork> input, List<Point> points) {
        //creation of a data set of data -> use to train the network
        INDArray array = Nd4j.create(input.get(0).serialise().columns(), input.size());
        INDArray outputs = Nd4j.create(1, points.size());
        this.createOutput(input,points,array,outputs);
        for(int i = 0; i < input.size(); i++){
            this.fit(Nd4j.toFlattened(array.getColumn(i)),Nd4j.toFlattened(outputs.getColumn(i)));
        }
    }

    @Override
    public void fit(DataSet dataSet) {
        throw new Error("Not Implemented");
    }


    /**
     * Create the INDArray from normal vector
     * @param input inputsnetwork containing the input
     * @param points real points of the inputs
     * @param array INDArray input
     * @param outputs INDArray output
     */
    protected void createOutput(List<InputsNetwork> input, List<Point> points, INDArray array, INDArray outputs){
        for(int i = 0; i < input.size(); i++) {
            INDArray l = input.get(i).serialise();
            array.putColumn(i, l);
        }

        for(int i = 0; i < points.size(); i++){
            outputs.putScalar(i, points.get(i).getLatitude());
        }
    }


    /**
     * Compute output given the time series
     * @param input list of {@link InputNetwork}
     * @return {@link INDArray}
     */
    public INDArray computeOutput(List<InputsNetwork> input) {
        INDArray array = Nd4j.create(input.get(0).serialise().columns(), input.size());
        for(int i = 0; i < input.size(); i++) {
            INDArray l = input.get(i).serialise();
            array.putColumn(i, l);
        }
        INDArray result = Nd4j.create(1, super.output);
        for(int i = 0; i < input.size(); i++){
            result = this.computeOutput(Nd4j.toFlattened(array.getColumn(i)));
        }
        return result;
    }


}

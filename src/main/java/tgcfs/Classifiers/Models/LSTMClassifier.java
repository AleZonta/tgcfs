package tgcfs.Classifiers.Models;

import lgds.trajectories.Point;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import tgcfs.NN.EvolvableModel;
import tgcfs.NN.InputsNetwork;
import tgcfs.Networks.LSTM;

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
 * Class representing a the classifier implemented using LSTM Neural Network
 * The LSMT NN is implemented using the deeplearning4j library
 * Deeplearning4j Development Team. Deeplearning4j: Open-source distributed deep learning for the JVM, Apache Software Foundation License 2.0. http://deeplearning4j.org
 *
 * The LSTMAgent is offering the methods to evolve the NN using an evolutionary algorithm
 */
public class LSTMClassifier extends LSTM implements EvolvableModel {

    /**
     * Building of the Recurrent Neural NN.Network
     *
     * since is used as a classifiers, we need the output function function be a SOFTMAX, to have the sum of the outputs be 1
     *
     * @param inputSize     integer value containing the size of the input
     * @param hiddenLayers  integer value containing how many hidden layers the network will have
     * @param hiddenNeurons integer value containing how many neurons the hidden layers will have
     * @param outputSize    integer value containing how many output neurons the network will have
     */
    public LSTMClassifier(int inputSize, int hiddenLayers, int hiddenNeurons, int outputSize) {
        super(inputSize, hiddenLayers, hiddenNeurons, outputSize, Activation.SOFTMAX);
    }

    /**
     * @implNote Implementation from Interface
     * @return Integer value
     */
    @Override
    public int getArrayLength() {
        return this.net.numParams();
    }

    /**
     * @implNote Implementation from Interface
     * @return list weights
     */
    @Override
    public INDArray getWeights() {
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
     * @return deep copy of the model
     */
    @Override
    public EvolvableModel deepCopy() {
        return new LSTMClassifier(this.inputSize, this.hiddenLayers, this.hiddenNeurons, this.outputSize);
    }

    /**
     * @implNote Implementation from Interface
     * @param input input of the network
     */
    @Override
    public void fit(List<InputsNetwork> input, List<Point> points) {
        //creation of a data set of data -> use to train the network
        INDArray array = Nd4j.create(input.get(0).serialise().columns(), input.size());
        INDArray outputs = Nd4j.create(input.get(0).serialise().columns(), input.size());
        this.createOutput(input,points,array,outputs);
        //input.forEach(i -> dataSet.addFeatureVector(Nd4j.create(i.serialise().stream().mapToDouble(d -> d).toArray())));

        for(int i = 0; i < input.size() - 1; i++){
            this.fit(Nd4j.toFlattened(array.getColumn(i)),Nd4j.toFlattened(outputs.getColumn(i + 1)));
        }
    }

    @Override
    public void fit(DataSet dataSet) {
        throw new NotImplementedException();
    }


    @Override
    public void evaluate(DataSetIterator dataSet){
        throw new NotImplementedException();
    }
}

package tgcfs.Agents;

import lgds.Distance.Distance;
import lgds.trajectories.Point;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import tgcfs.NN.EvolvableNN;
import tgcfs.NN.InputsNetwork;
import tgcfs.Networks.LSTM;

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
 * The LSMT NN is implemented using the deeplearning4j library
 * Deeplearning4j Development Team. Deeplearning4j: Open-source distributed deep learning for the JVM, Apache Software Foundation License 2.0. http://deeplearning4j.org
 *
 * The LSTMAgent is offering the methods to evolve the NN using an evolutionary algorithm
 */
public class LSTMAgent extends LSTM implements EvolvableNN {

    /**
     * Building of the Recurrent Neural NN.Network
     *
     * @param inputSize     integer value containing the size of the input
     * @param hiddenLayers  integer value containing how many hidden layers the network will have
     * @param hiddenNeurons integer value containing how many neurons the hidden layers will have
     * @param outputSize    integer value containing how many output neurons the network will have
     */
    public LSTMAgent(Integer inputSize, Integer hiddenLayers, Integer hiddenNeurons, Integer outputSize) {
        super(inputSize, hiddenLayers, hiddenNeurons, outputSize);
    }

    /**
     * @implNote Implementation from Interface
     * @return Integer value
     */
    @Override
    public Integer getArrayLength() {
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
    public EvolvableNN deepCopy() {
        return new LSTMAgent(this.inputSize, this.hiddenLayers, this.hiddenNeurons, this.outputSize);
    }

    /**
     * @implNote Implementation from Interface
     * @param input input of the network
     */
    @Override
    public void fit(List<InputsNetwork> input, List<Point> points) {
        //creation of a data set of data -> use to train the network
        Distance d = new Distance();

        INDArray array = Nd4j.create(input.get(0).serialise().columns(), input.size());
        INDArray outputs = Nd4j.create(input.get(0).serialise().columns(), input.size());
        this.createOutput(input,points,array,outputs);
        //input.forEach(i -> dataSet.addFeatureVector(Nd4j.create(i.serialise().stream().mapToDouble(d -> d).toArray())));

        for(int i = 0; i < input.size() - 1; i++){
            this.fit(Nd4j.toFlattened(array.getColumn(i)),Nd4j.toFlattened(outputs.getColumn(i + 1)));
        }
    }


    /**
     * Create the INDArray from normal vector
     * @param input inputsnetwork containing the input
     * @param points real points of the inputs
     * @param array INDArray input
     * @param outputs INDArray output
     */
    private void createOutput(List<InputsNetwork> input, List<Point> points, INDArray array, INDArray outputs){
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


    /**
     * clear the rnn state from the previous instance
     */
    public void clearPreviousState(){
        this.net.rnnClearPreviousState();
    }

}

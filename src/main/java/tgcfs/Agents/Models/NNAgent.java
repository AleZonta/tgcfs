package tgcfs.Agents.Models;

import lgds.trajectories.Point;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import tgcfs.NN.EvolvableModel;
import tgcfs.NN.InputsNetwork;
import tgcfs.Networks.NN;

import java.util.List;

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
public class NNAgent extends NN implements EvolvableModel {

    /**
     * Building of the Neural NN.Network
     *
     * @param inputSize     integer value containing the size of the input
     * @param hiddenLayers  integer value containing how many hidden layers the network will have
     * @param hiddenNeurons integer value containing how many neurons the hidden layers will have
     * @param outputSize    integer value containing how many output neurons the network will have
     */
    public NNAgent(int inputSize, int hiddenLayers, int hiddenNeurons, int outputSize) {
        super(inputSize, hiddenLayers, hiddenNeurons, outputSize);
    }

    @Override
    public int getArrayLength() {
        return this.net.numParams();
    }

    @Override
    public INDArray getWeights() {
        return this.net.params();
    }

    @Override
    public void setWeights(INDArray weights) throws Exception {
        if (weights.columns() != this.net.numParams()){
            throw new Exception("Length list weights is not correct.");
        }
        this.net.setParameters(weights);
    }

    @Override
    public EvolvableModel deepCopy() {
        return new NNAgent(this.inputSize, this.hiddenLayers, this.hiddenNeurons, this.outputSize);
    }

    @Override
    public void fit(List<InputsNetwork> input, List<Point> points) {
        throw new NotImplementedException();
    }

    @Override
    public void fit(DataSet dataSet) {
        throw new NotImplementedException();
    }

    public int getId(){
        return super.getId();
    }
}

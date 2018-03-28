package tgcfs.Classifiers.Models;

import lgds.trajectories.Point;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import tgcfs.NN.EvolvableModel;
import tgcfs.NN.InputsNetwork;
import tgcfs.Networks.NN;

import java.util.List;

/**
 * Created by Alessandro Zonta on 14/03/2018.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 */
public class NNClassifier  extends NN implements EvolvableModel {
    /**
     * Default constructor
     */
    public NNClassifier(){}


    /**
     * Constructor of the classifier. It generates the ElmanNetwork.
     *
     * @param input         number  of nodes used as input
     * @param HiddenNeurons number of hidden layer
     * @param output        number of nodes used as output
     */
    public NNClassifier(int input, int HiddenNeurons, int output) {
        super(input,1, HiddenNeurons, output);
    }


    @Override
    public int getArrayLength() {
        return this.getNumPar();
    }

    @Override
    public INDArray getWeights() {
        return this.getParameters();
    }

    @Override
    public void setWeights(INDArray weights) throws Exception {
        this.setWeight(weights);
    }

    @Override
    public EvolvableModel deepCopy() {
        return new NNClassifier(this.inputSize, this.hiddenNeurons, this.outputSize);
    }

    @Override
    public void fit(List<InputsNetwork> input, List<Point> points) {
        throw new Error("Not implemented yet");
    }

    @Override
    public void fit(DataSet dataSet) {
        throw new Error("Not implemented yet");
    }

    @Override
    public void evaluate(DataSetIterator dataSet){
        throw new NotImplementedException();
    }
}

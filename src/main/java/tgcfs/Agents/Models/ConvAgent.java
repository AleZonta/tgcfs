package tgcfs.Agents.Models;

import lgds.trajectories.Point;
import org.nd4j.linalg.api.ndarray.INDArray;
import tgcfs.NN.EvolvableModel;
import tgcfs.NN.InputsNetwork;
import tgcfs.Networks.Convolutionary;

import java.util.List;

/**
 * Created by Alessandro Zonta on 16/08/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 */
public class ConvAgent extends Convolutionary implements EvolvableModel {



    public ConvAgent(){
        super();
    }


    /**
     * @implNote Implementation from Interface
     * @return Integer value
     */
    @Override
    public Integer getArrayLength() {
        return null;
    }

    /**
     * @implNote Implementation from Interface
     * @return list weights
     */
    @Override
    public INDArray getWeights() {
        return null;
    }

    /**
     * @implNote Implementation from Interface
     * @param weights list containing all the weights
     * @throws Exception if the length of the list is not correct
     */
    @Override
    public void setWeights(INDArray weights) throws Exception {

    }

    /**
     * @implNote Implementation from Interface
     * @return deep copy of the model
     */
    @Override
    public EvolvableModel deepCopy() {
        return null;
    }

    /**
     * @implNote Implementation from Interface
     * @param input input of the network
     */
    @Override
    public void fit(List<InputsNetwork> input, List<Point> points) {

    }
}

package tgcfs.InputOutput;

import tgcfs.Loader.TrainReal;
import tgcfs.NN.InputsNetwork;

import java.util.List;

/**
 * Created by Alessandro Zonta on 30/05/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 *
 * This interface will represent the transformation needed from the output of one model to the input of the other model
 */
public interface Transformation {

    /**
     * Method that transform the output of one neural network to the input of a another network
     * @param trainReal all the data I need to transform and output the data
     * @return the input of the new network
     */
    List<InputsNetwork> transform(TrainReal trainReal);
}

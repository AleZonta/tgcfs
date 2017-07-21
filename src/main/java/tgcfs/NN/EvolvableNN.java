package tgcfs.NN;

import lgds.trajectories.Point;
import org.nd4j.linalg.api.ndarray.INDArray;

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
 * Interface for evolvable NN
 * With a Genetic Algorithm the NN is trained evolving the weights of the connection.
 * A class implementing a NN needs to implement the method to set and get the current set of weights
 */
public interface EvolvableNN {

    /**
     * Getter for the number of weight in the network
     * @return Integer value
     */
    Integer getArrayLength();

    /**
     * Method that returns the weights of the network
     * @return list of weights
     */
    INDArray getWeights();


    /**
     * Method that sets the weights to the networks
     * @param weights list containing all the weights
     * @throws Exception if the length of the list passed as parameter is not correct
     */
    void setWeights(INDArray weights) throws Exception;


    /**
     * Compute the output of the network
     * @param input list value that are the input of the network
     * @return list of output of the network
     */
    INDArray computeOutput(INDArray input);

    /**
     * Deep copy of the model
     * @return deep copy of the model used
     */
    EvolvableNN deepCopy();

    /**
     * Train the Network
     * @param input input of the network
     * @param points real point of the input
     */
    void fit(List<InputsNetwork> input, List<Point> points);
}

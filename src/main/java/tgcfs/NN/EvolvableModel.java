package tgcfs.NN;

import lgds.trajectories.Point;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;

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
 * Interface for evolvableModel
 * With a Genetic Algorithm a model is trained evolving an array ov value
 * Every model that is goign to be used as a agents need to have these method to be evolved by the system
 */
public interface EvolvableModel {

    /**
     * Getter for the number of weight in the network
     * @return Integer value
     */
    int getArrayLength();

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
     * Deep copy of the model
     * @return deep copy of the model used
     */
    EvolvableModel deepCopy();

    /**
     * Train the Network
     * @param input input of the network
     * @param points real point of the input
     */
    void fit(List<InputsNetwork> input, List<Point> points);

    /**
     * Train the Network
     * @param dataSet Dataset to train
     */
    void fit(DataSet dataSet);

    /**
     * Compute the output of the network given the input
     * @param input list value that are the input of the network
     * @return list of output of the network
     */
    INDArray computeOutput(INDArray input);

    /**
     * Get string containing description of the network
     * @return String
     */
    String getSummary();

    /**
     * Get Id model
     * @return int id
     */
    int getId();
}

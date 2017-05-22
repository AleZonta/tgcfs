package tgcfs.NN;

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
 * interface for evolvable NN
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
    List<Double> getWeights();


    /**
     * Method that sets the weights to the networks
     * @param weights list containing all the weights
     * @throws Exception if the length of the list passed as parameter is not correct
     */
    void setWeights(List<Double> weights) throws Exception;
}

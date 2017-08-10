package tgcfs.Networks;

import org.nd4j.linalg.api.ndarray.INDArray;

/**
 * Created by Alessandro Zonta on 08/08/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 *
 * interface implementing a neural network
 */
public interface Network {

    /**
     * Compute the output of the network given the input
     * @param input list value that are the input of the network
     * @return list of output of the network
     */
    INDArray computeOutput(INDArray input);

    /**
     * fit the network
     * @param inputs input of the network
     * @param labels real point of the input
     */
    void fit(INDArray inputs, INDArray labels);

    /**
     * Get string containing description of the network
     * @return String
     */
    String getSummary();

    /**
     * Get number parameter of the network
     * @return integer number
     */
    Integer getNumPar();

}

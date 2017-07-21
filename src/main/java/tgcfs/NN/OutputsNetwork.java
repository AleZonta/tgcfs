package tgcfs.NN;

import org.nd4j.linalg.api.ndarray.INDArray;

/**
 * Created by Alessandro Zonta on 29/05/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 *
 * Interface implementing the ouput of a neural network
 */
public interface OutputsNetwork {

    /**
     * De serialise the list into the fields
     * @param out list containing all the fields
     */
    void deserialise(INDArray out);
}

package tgcfs.EA.Recombination;

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
 * Interface for all the method recombination.
 */
public interface Recombination {

    /**
     * Method that performs the recombination
     * @return the list of value forming the new individual
     */
    INDArray recombination();

}

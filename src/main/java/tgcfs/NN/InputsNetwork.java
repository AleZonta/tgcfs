package tgcfs.NN;

import org.json.simple.JSONObject;
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
 * Interface implementing the input of a neural network
 */
public interface InputsNetwork {

    /**
     * Serialising the field into a list
     * @return list containing all the fields
     */
    INDArray serialise();


    /**
     * Deep copy of the element
     * @return copy of the object
     */
    InputsNetwork deepCopy();

    /**
     * Save the network to Json
     * @return Json object
     */
    JSONObject toJson();
}

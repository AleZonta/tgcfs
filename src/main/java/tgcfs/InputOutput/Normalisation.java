package tgcfs.InputOutput;

import tgcfs.Agents.InputNetwork;
import tgcfs.NN.InputsNetwork;

/**
 * Created by Alessandro Zonta on 01/06/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 *
 * Class that helps the normalisation of the input
 * Max and minimum hardcoded per classes
 *
 */
public class Normalisation {

    /**
     * Normalisation of the number
     * @param network class that needs the element normalised
     * @param data element to normalise
     * @return element normalised
     */
    public static Double convertData(InputsNetwork network, Double data){
        if(network.getClass().equals(InputNetwork.class)){
            return convertAgents(data);
        }
        return convertClassifier(data);

    }

    /**
     * Normalise value for the agent class
     * @param data element to normalise
     * @return element normalised
     */
    private static Double convertAgents(Double data){
        Double maxAgents = 100.0;
        Double minAgents = -100.0;
        return 2 * ((data - minAgents) / (maxAgents - minAgents)) - 1;
    }

    /**
     * Normalise value for the classifier class
     * @param data element to normalise
     * @return element normalised
     */
    private static Double convertClassifier(Double data){
        Double maxClassifier = 100.0;
        Double minClassifier = -100.0;
        return 2 * ((data - minClassifier) / (maxClassifier - minClassifier)) - 1;
    }

}

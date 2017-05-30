package tgcfs.InputOutput;

import tgcfs.Agents.OutputNetwork;
import tgcfs.Classifiers.InputNetwork;
import tgcfs.NN.InputsNetwork;
import tgcfs.NN.OutputsNetwork;

import java.util.ArrayList;
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
 * Implementation of the mapping process
 * From the ouput of the agents the indications need to be transformed into the input of the second
 * This is for test use, does not follow the real path on the graph of the routes
 */
public class Straightforward implements Transformation {

    /**
     * @implNote Implementation from Abstract class Algorithm
     * @param outputs data that we want to transform into input data
     * @return the input of the new network
     */
    @Override
    public List<InputsNetwork> transform(List<OutputsNetwork> outputs) {
        //the output of the agent's neural network now is:
        //speed and bearing

        //the input of the classifier's  neural network is:
        //speed
        //direction

        //In this case no transformation are needed, it is one to one
        //speed -> speed
        //direction -> direction

        List<InputsNetwork> inputNetworks = new ArrayList<>();
        outputs.forEach(element -> {
            InputNetwork input = new InputNetwork(((OutputNetwork)element).getSpeed(), ((OutputNetwork)element).getBearing());
            inputNetworks.add(input);
        });



        return inputNetworks;
    }
}

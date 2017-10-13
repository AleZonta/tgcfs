package tgcfs.EA;

import org.junit.Test;
import tgcfs.Agents.InputNetwork;
import tgcfs.Agents.Models.LSTMAgent;
import tgcfs.Agents.OutputNetwork;
import tgcfs.Config.ReadConfig;
import tgcfs.Loader.TrainReal;
import tgcfs.NN.EvolvableModel;
import tgcfs.NN.InputsNetwork;
import tgcfs.Utils.PointWithBearing;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

/**
 * Created by Alessandro Zonta on 30/05/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 */
public class AgentsTest {


    @Test
    public void trainNetwork() throws Exception {
        throw new Exception("Not tested");
    }

    @Test
    public void generatePopulation() throws Exception {
        new ReadConfig.Configurations();
        //Random LSTM
        EvolvableModel model = new LSTMAgent(InputNetwork.inputSize, 1, 5, OutputNetwork.outputSize);
        Agents agentsCompeting = new Agents();
        agentsCompeting.generatePopulation(model);
        assertNotNull(agentsCompeting.getPopulation());
    }

    @Test
    public void runIndividuals() throws Exception {
        new ReadConfig.Configurations();
        List<InputsNetwork> input = new ArrayList<>();
        input.add(new InputNetwork(15.0, 30.8, 15.0));
        input.add(new InputNetwork(16.0, 31.8, 15.0));
        input.add(new InputNetwork(17.0, 32.8, 15.0));
        input.add(new InputNetwork(18.0, 33.8, 15.0));
        input.add(new InputNetwork(19.0, 34.8, 15.0));

        EvolvableModel model = new LSTMAgent(InputNetwork.inputSize, 1, 5, OutputNetwork.outputSize);
        Agents agentsCompeting = new Agents();
        agentsCompeting.generatePopulation(model);


        List<TrainReal> i = new ArrayList<>();

        List<PointWithBearing> p = new ArrayList<>();
        p.add(new PointWithBearing(3d,5d));
        p.add(new PointWithBearing(3d,5d));
        p.add(new PointWithBearing(3d,5d));
        p.add(new PointWithBearing(3d,5d));


        TrainReal t = new TrainReal(input, p);
        i.add(t);

        agentsCompeting.runIndividuals(i);

    }

    @Test
    public void runIndividual() throws Exception {
        new ReadConfig.Configurations();
        Agents agentsCompeting = new Agents();
        try {
            agentsCompeting.runIndividual(null, null);
        }catch (Exception e){
            assertEquals("Method not usable for a Agent", e.getMessage());
        }
    }

    @Test
    public void evaluateIndividuals() throws Exception {
    }

}
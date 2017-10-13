package tgcfs.EA;

import org.junit.Test;
import tgcfs.Agents.InputNetwork;
import tgcfs.Agents.Models.LSTMAgent;
import tgcfs.Agents.OutputNetwork;
import tgcfs.Config.ReadConfig;
import tgcfs.NN.EvolvableModel;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

/**
 * Created by Alessandro Zonta on 02/06/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 */
public class AlgorithmTest {
    @Test
    public void getMaxFitnessAchievable() throws Exception {
        new ReadConfig.Configurations();
        //Random LSTM
        EvolvableModel model = new LSTMAgent(InputNetwork.inputSize, 1, 5, OutputNetwork.outputSize);
        Agents agentsCompeting = new Agents();
        agentsCompeting.generatePopulation(model);
        System.out.println("agent max fitness achievable -> " + agentsCompeting.getMaxFitnessAchievable());

        Classifiers classifiers = new Classifiers();
        classifiers.generatePopulation(model);
        System.out.println("classifier max fitness achievable -> " + classifiers.getMaxFitnessAchievable());

    }
    @Test
    public void getFittestIndividual() throws Exception {
        throw new Exception("Not tested");
    }

    @Test
    public void generatePopulation() throws Exception {
        throw new Exception("Not tested");
    }

    @Test
    public void getPopulation() throws Exception {
        throw new Exception("Not tested");
    }

    @Test
    public void addIndividual() throws Exception {
        throw new Exception("Not tested");
    }

    @Test
    public void generateOffspringOnlyWithMutation() throws Exception {
        throw new Exception("Not tested");
    }

    @Test
    public void runIndividuals() throws Exception {
        throw new Exception("Not tested");
    }

    @Test
    public void runIndividual() throws Exception {
        throw new Exception("Not tested");
    }

    @Test
    public void evaluateIndividuals() throws Exception {
        throw new Exception("Not tested");
    }

    @Test
    public void trainNetwork() throws Exception {
        throw new Exception("Not tested");
    }

    @Test
    public void resetFitness() throws Exception {
        throw new Exception("Not tested");
    }

    @Test
    public void retAllFitness() throws Exception {
        new ReadConfig.Configurations();
        Algorithm algorithm = new Agents();
        EvolvableModel evolvableModel = new LSTMAgent(1,1,1,1);
        algorithm.generatePopulation(evolvableModel);
        Integer pop = algorithm.getPopulation().size();
        algorithm.generateOffspring();
        List<Integer> fitness = algorithm.retAllFitness();
        assertNotNull(fitness);
        assertEquals(pop + pop, fitness.size());
        fitness.forEach(f -> {
            assertEquals(0, f.intValue());
        });
    }

    @Test
    public void retBestGenome() throws Exception {
        new ReadConfig.Configurations();
        Algorithm algorithm = new Agents();
        EvolvableModel evolvableModel = new LSTMAgent(1,1,1,1);
        algorithm.generatePopulation(evolvableModel);
        assertNotNull(algorithm.retBestGenome());
    }

    @Test
    public void selectParents() throws Exception {
        new ReadConfig.Configurations();
        Algorithm algorithm = new Agents();
        EvolvableModel evolvableModel = new LSTMAgent(1,1,1,1);
        algorithm.generatePopulation(evolvableModel);
        Integer pop = algorithm.getPopulation().size();
        algorithm.generateOffspring();
        assertEquals(pop*2,algorithm.getPopulation().size());
        algorithm.survivalSelections();
        assertEquals(pop.intValue(), algorithm.getPopulation().size());
    }

    @Test
    public void generateOffspring() throws Exception {
        new ReadConfig.Configurations();
        Algorithm algorithm = new Agents();
        EvolvableModel evolvableModel = new LSTMAgent(1,1,1,1);
        algorithm.generatePopulation(evolvableModel);
        Integer pop = algorithm.getPopulation().size();
        algorithm.generateOffspring();
        assertEquals(pop*2,algorithm.getPopulation().size());


    }

}
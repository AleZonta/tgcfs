package tgcfs.EA;

import org.junit.Test;
import tgcfs.Agents.LSTMAgent;
import tgcfs.NN.EvolvableNN;

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
    public void retAllFitness() throws Exception {
        Algorithm algorithm = new Agents();
        EvolvableNN evolvableNN = new LSTMAgent(1,1,1,1);
        algorithm.generatePopulation(evolvableNN);
        Integer pop = algorithm.getPopulation().size();
        algorithm.generateOffspring();
        List<Integer> fitness = algorithm.retAllFitness();
        assertNotNull(fitness);
        assertEquals(new Integer(100).intValue(), fitness.size());
        fitness.forEach(f -> {
            assertEquals(0, f.intValue());
        });
    }

    @Test
    public void retBestGenome() throws Exception {
        Algorithm algorithm = new Agents();
        EvolvableNN evolvableNN = new LSTMAgent(1,1,1,1);
        algorithm.generatePopulation(evolvableNN);
        assertNotNull(algorithm.retBestGenome());
    }

    @Test
    public void selectParents() throws Exception {
        Algorithm algorithm = new Agents();
        EvolvableNN evolvableNN = new LSTMAgent(1,1,1,1);
        algorithm.generatePopulation(evolvableNN);
        Integer pop = algorithm.getPopulation().size();
        algorithm.generateOffspring();
        assertEquals(pop*2,algorithm.getPopulation().size());
        algorithm.selectParents();
        assertEquals(pop.intValue(), algorithm.getPopulation().size());
    }

    @Test
    public void generateOffspring() throws Exception {
        Algorithm algorithm = new Agents();
        EvolvableNN evolvableNN = new LSTMAgent(1,1,1,1);
        algorithm.generatePopulation(evolvableNN);
        Integer pop = algorithm.getPopulation().size();
        algorithm.generateOffspring();
        assertEquals(pop*2,algorithm.getPopulation().size());
    }

}
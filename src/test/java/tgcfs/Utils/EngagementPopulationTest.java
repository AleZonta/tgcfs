package tgcfs.Utils;

import org.junit.Test;
import tgcfs.Agents.Models.LSTMAgent;
import tgcfs.Config.ReadConfig;
import tgcfs.EA.Agents;
import tgcfs.EA.Algorithm;
import tgcfs.EA.EngagementPopulation;
import tgcfs.EA.Individual;
import tgcfs.NN.EvolvableModel;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static junit.framework.TestCase.assertTrue;

/**
 * Created by Alessandro Zonta on 31/10/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 */
public class EngagementPopulationTest {
    @Test
    public void executeCountermeasuresAgainsDisengagement() throws Exception {
        new ReadConfig.Configurations();
        Logger log =  Logger.getLogger(EngagementPopulationTest.class.getName());

        EngagementPopulation en = new EngagementPopulation(log);
        en.setMaxFitness(16000, 16000);
        assertTrue(en.isUsingVirulenceAgents() || !en.isUsingVirulenceAgents());
        assertTrue(en.isUsingVirulenceClassifiers() || !en.isUsingVirulenceClassifiers());
        assertTrue(en.getVirulenceAgents() >= 0);
        assertTrue(en.getVirulenceClassifiers() >= 0);


        Algorithm algorithm = new Agents(log);
        EvolvableModel evolvableModel = new LSTMAgent(1,1,1,1);
        algorithm.generatePopulation(evolvableModel);

        algorithm.getPopulation().forEach(individual -> individual.setFitness(RandomGenerator.getNextInt(0, 16000)));

        List<Individual> ind = new ArrayList<>();
        algorithm.getPopulation().forEach(individual -> ind.add(individual.deepCopy()));

        //try the static virulence model
        en.executeCountermeasuresAgainstDisengagement(ind, IndividualStatus.AGENT);

        List<Individual> ind2 = new ArrayList<>();
        algorithm.getPopulation().forEach(individual -> ind2.add(individual.deepCopy()));
        en.executeCountermeasuresAgainstDisengagement(ind2, IndividualStatus.CLASSIFIER);


    }

}
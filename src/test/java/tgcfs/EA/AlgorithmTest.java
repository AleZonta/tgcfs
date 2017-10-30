package tgcfs.EA;

import org.junit.Test;
import tgcfs.Agents.InputNetwork;
import tgcfs.Agents.Models.LSTMAgent;
import tgcfs.Agents.OutputNetwork;
import tgcfs.Config.ReadConfig;
import tgcfs.NN.EvolvableModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

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
    public void reduceVirulence() throws Exception {
        new ReadConfig.Configurations();
        Logger log =  Logger.getLogger(AlgorithmTest.class.getName());

        Algorithm algorithm = new Agents(log);
        EvolvableModel evolvableModel = new LSTMAgent(1,1,1,1);
        algorithm.generatePopulation(evolvableModel);

        algorithm.getPopulation().forEach(individual -> individual.setFitness(ThreadLocalRandom.current().nextInt(16000)));


        double virulence = ReadConfig.Configurations.getVirulenceAgents();
        List<Integer> listFitnesses = new ArrayList<>();
        algorithm.getPopulation().forEach(individual -> listFitnesses.add(individual.getFitness()));
        System.out.println(listFitnesses.toString());


        List<Integer> listDeNormalisedFitnesses = new ArrayList<>();

        for(int i = 0; i < listFitnesses.size(); i++){
            int fit = listFitnesses.get(i);



            double maxAngularSpeed = Collections.max(listFitnesses);
            double minAngularSpeed = 0.0;
            double b = 1.0;
            double a = 0.0;
            double fitNorm =  (b - a) * ((fit - minAngularSpeed) / (maxAngularSpeed - minAngularSpeed)) + a;


            double first = (2 * fitNorm) / virulence;
            double second = Math.pow(fitNorm, 2) / Math.pow(virulence, 2);
            double third = first - second;
            double newfit = (((2 * fitNorm) / virulence) - (Math.pow(fitNorm, 2) / Math.pow(virulence, 2)));


            maxAngularSpeed = 1.0;
            minAngularSpeed = 0.0;
            b = Collections.max(listFitnesses);
            a = 0.0;
            fitNorm = (int) ((b - a) * ((newfit - minAngularSpeed) / (maxAngularSpeed - minAngularSpeed)) + a);

            listDeNormalisedFitnesses.add((int)fitNorm);
        }

        System.out.println(listDeNormalisedFitnesses.toString());

        algorithm.reduceVirulence();
        List<Integer> listSecondDeNormalisedFitnesses = new ArrayList<>();
        algorithm.getPopulation().forEach(individual -> listSecondDeNormalisedFitnesses.add(individual.getFitness()));

        System.out.println(listSecondDeNormalisedFitnesses.toString());

        assertEquals(listDeNormalisedFitnesses.size(), listSecondDeNormalisedFitnesses.size());
        for(int i = 0; i < listDeNormalisedFitnesses.size(); i++){
            assertEquals(listDeNormalisedFitnesses.get(i), listSecondDeNormalisedFitnesses.get(i));
        }


    }

    @Test
    public void getMaxFitnessAchievable() throws Exception {
        new ReadConfig.Configurations();
        //Random LSTM
        EvolvableModel model = new LSTMAgent(InputNetwork.inputSize, 1, 5, OutputNetwork.outputSize);
        Logger log =  Logger.getLogger(AlgorithmTest.class.getName());

        Agents agentsCompeting = new Agents(log);
        agentsCompeting.generatePopulation(model);
        System.out.println("agent max fitness achievable -> " + agentsCompeting.getMaxFitnessAchievable());

        Classifiers classifiers = new Classifiers(log);
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
        Logger log =  Logger.getLogger(AlgorithmTest.class.getName());

        Algorithm algorithm = new Agents(log);
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
        Logger log =  Logger.getLogger(AlgorithmTest.class.getName());

        Algorithm algorithm = new Agents(log);
        EvolvableModel evolvableModel = new LSTMAgent(1,1,1,1);
        algorithm.generatePopulation(evolvableModel);
        assertNotNull(algorithm.retBestGenome());
    }

    @Test
    public void selectParents() throws Exception {
        new ReadConfig.Configurations();
        Logger log =  Logger.getLogger(AlgorithmTest.class.getName());

        Algorithm algorithm = new Agents(log);
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
        Logger log =  Logger.getLogger(AlgorithmTest.class.getName());

        Algorithm algorithm = new Agents(log);
        EvolvableModel evolvableModel = new LSTMAgent(1,1,1,1);
        algorithm.generatePopulation(evolvableModel);
        Integer pop = algorithm.getPopulation().size();
        algorithm.generateOffspring();
        assertEquals(pop*2,algorithm.getPopulation().size());


    }

}
package tgcfs.EA;

import org.junit.Test;
import tgcfs.Agents.InputNetwork;
import tgcfs.Agents.Models.LSTMAgent;
import tgcfs.Agents.OutputNetwork;
import tgcfs.Classifiers.Models.ENNClassifier;
import tgcfs.Config.ReadConfig;
import tgcfs.Idsa.IdsaLoader;
import tgcfs.InputOutput.FollowingTheGraph;
import tgcfs.Loader.Feeder;
import tgcfs.Loader.TrainReal;
import tgcfs.NN.EvolvableModel;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static junit.framework.TestCase.*;

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
        Logger log =  Logger.getLogger(AgentsTest.class.getName());

        Agents agentsCompeting = new Agents(log);
        agentsCompeting.generatePopulation(model);
        assertNotNull(agentsCompeting.getPopulation());
    }

    @Test
    public void runIndividuals() throws Exception {
        new ReadConfig.Configurations();
        Logger log =  Logger.getLogger(AgentsTest.class.getName());
        Agents agents = new Agents(log);
        EvolvableModel agentModel = new LSTMAgent(InputNetwork.inputSize, ReadConfig.Configurations.getHiddenLayersAgent(), ReadConfig.Configurations.getHiddenNeuronsAgent(), OutputNetwork.outputSize);
        agents.generatePopulation(agentModel);

        Feeder feeder = new Feeder(log);
        feeder.loadSystem();
        IdsaLoader idsaLoader = new IdsaLoader(log);
        idsaLoader.InitPotentialField(feeder.getTrajectories());

        List<TrainReal> combineInputList = feeder.multiFeeder(idsaLoader, null);
        agents.runIndividuals(combineInputList);

        System.out.println("---------------------------");

        agents.getPopulation().forEach(p -> {
            List<TrainReal> t = p.getMyInputandOutput();
            t.forEach(trainReal -> System.out.println(trainReal.getOutputComputed().toString()));
        });
    }

    @Test
    public void runIndividual() throws Exception {
        new ReadConfig.Configurations();
        Logger log =  Logger.getLogger(AgentsTest.class.getName());

        Agents agentsCompeting = new Agents(log);
        try {
            agentsCompeting.runIndividual(null, null);
        }catch (Exception e){
            assertEquals("Method not usable for a Agent", e.getMessage());
        }
    }

    @Test
    public void evaluateIndividuals() throws Exception {
        new ReadConfig.Configurations();
        Logger log =  Logger.getLogger(AgentsTest.class.getName());
        Agents agents = new Agents(log);
        Classifiers classifiers = new Classifiers(log);
        EvolvableModel agentModel = new LSTMAgent(InputNetwork.inputSize, ReadConfig.Configurations.getHiddenLayersAgent(), ReadConfig.Configurations.getHiddenNeuronsAgent(), OutputNetwork.outputSize);
        EvolvableModel classifierModel = new ENNClassifier(tgcfs.Classifiers.InputNetwork.inputSize, ReadConfig.Configurations.getHiddenNeuronsClassifier(), tgcfs.Classifiers.OutputNetwork.outputSize);
        agents.generatePopulation(agentModel);
        classifiers.generatePopulation(classifierModel);

        List<Individual> agentPopulationSecond = new ArrayList<>();
        List<Individual> agentPopulationFirst = agents.getPopulation();
        agentPopulationFirst.forEach(p -> agentPopulationSecond.add(p.deepCopy()));

        List<Integer> fitnessFirst = new ArrayList<>();
        List<Integer> fitnessSecond = new ArrayList<>();
        List<Integer> idFirst = new ArrayList<>();
        List<Integer> idSecond = new ArrayList<>();
        agentPopulationFirst.forEach(p -> {
            fitnessFirst.add(p.getFitness());
            idFirst.add(p.getModel().getId());
        });
        agentPopulationSecond.forEach(p -> {
            fitnessSecond.add(p.getFitness());
            idSecond.add(p.getModel().getId());
        });

        System.out.println("Agent Fitness");
        System.out.println(fitnessFirst.toString());
        System.out.println("Agent DeepCopy Fitness");
        System.out.println(fitnessSecond.toString());
        System.out.println("Agent ID Model");
        System.out.println(idFirst.toString());
        System.out.println("Agent DeepCopy ID Model");
        System.out.println(idSecond.toString());

        for(int i = 0; i < agentPopulationFirst.size(); i++){
            assertTrue(agentPopulationFirst.get(i).getObjectiveParameters().equalsWithEps(agentPopulationSecond.get(i).getObjectiveParameters(), 0.0001));
        }



        List<Individual> classifierPopulationSecond = new ArrayList<>();
        List<Individual> classifierPopulationFirst = classifiers.getPopulation();
        classifierPopulationFirst.forEach(p -> classifierPopulationSecond.add(p.deepCopy()));

        List<Integer> fitnessFirstC = new ArrayList<>();
        List<Integer> fitnessSecondC= new ArrayList<>();
        List<Integer> idFirstC = new ArrayList<>();
        List<Integer> idSecondC = new ArrayList<>();
        classifierPopulationFirst.forEach(p -> {
            fitnessFirstC.add(p.getFitness());
            idFirstC.add(p.getModel().getId());
        });
        classifierPopulationSecond.forEach(p -> {
            fitnessSecondC.add(p.getFitness());
            idSecondC.add(p.getModel().getId());
        });

        System.out.println("Classifiers Fitness");
        System.out.println(fitnessFirstC.toString());
        System.out.println("Classifiers DeepCopy Fitness");
        System.out.println(fitnessSecondC.toString());
        System.out.println("Classifiers ID Model");
        System.out.println(idFirstC.toString());
        System.out.println("Classifiers DeepCopy ID Model");
        System.out.println(idSecondC.toString());

        for(int i = 0; i < agentPopulationFirst.size(); i++){
            assertTrue(classifierPopulationFirst.get(i).getObjectiveParameters().equalsWithEps(classifierPopulationSecond.get(i).getObjectiveParameters(), 0.0001));
        }

        Feeder feeder = new Feeder(log);
        feeder.loadSystem();
        IdsaLoader idsaLoader = new IdsaLoader(log);
        idsaLoader.InitPotentialField(feeder.getTrajectories());

        List<TrainReal> combineInputList = feeder.multiFeeder(idsaLoader, null);
        for(int i = 0; i < 5; i++) {
            System.out.println("------------------------------ " + i + " ------------------------------" );
            combineInputList = feeder.multiFeeder(idsaLoader, combineInputList);

            agents.runIndividuals(combineInputList);

            System.out.println("--------- parallel version ---------");

            agents.evaluateIndividuals(classifiers, new FollowingTheGraph(feeder,log));


            List<Integer> fitnessFirstA = new ArrayList<>();
            List<Integer> idFirstA = new ArrayList<>();
            agents.getPopulation().forEach(p -> {
                fitnessFirstA.add(p.getFitness());
                idFirstA.add(p.getModel().getId());
            });
            List<Integer> fitnessFirstCC = new ArrayList<>();
            List<Integer> idFirstCC = new ArrayList<>();
            classifiers.getPopulation().forEach(p -> {
                fitnessFirstCC.add(p.getFitness());
                idFirstCC.add(p.getModel().getId());
            });

            System.out.println("Agent Fitness");
            System.out.println(fitnessFirstA.toString());
            System.out.println("Classifiers Fitness");
            System.out.println(fitnessFirstCC.toString());
            System.out.println("Agent ID Model");
            System.out.println(idFirstA.toString());
            System.out.println("Classifiers ID Model");
            System.out.println(idFirstCC.toString());


            agents.resetFitness();
            classifiers.resetFitness();
            combineInputList = feeder.multiFeeder(idsaLoader, combineInputList);
            agents.runIndividuals(combineInputList);

            System.out.println("--------- parallel version ---------");

            agents.evaluateIndividuals(classifiers, new FollowingTheGraph(feeder,log));

            List<Integer> fitnessFirstAP = new ArrayList<>();
            List<Integer> idFirstAP = new ArrayList<>();
            agents.getPopulation().forEach(p -> {
                fitnessFirstAP.add(p.getFitness());
                idFirstAP.add(p.getModel().getId());
            });
            List<Integer> fitnessFirstCCP = new ArrayList<>();
            List<Integer> idFirstCCP = new ArrayList<>();
            classifiers.getPopulation().forEach(p -> {
                fitnessFirstCCP.add(p.getFitness());
                idFirstCCP.add(p.getModel().getId());
            });

            System.out.println("Agent Fitness");
            System.out.println(fitnessFirstAP.toString());
            System.out.println("Classifiers Fitness");
            System.out.println(fitnessFirstCCP.toString());
            System.out.println("Agent ID Model");
            System.out.println(idFirstAP.toString());
            System.out.println("Classifiers ID Model");
            System.out.println(idFirstCCP.toString());

            agents.resetFitness();
            classifiers.resetFitness();
        }
//        agents.evaluateIndividuals(classifiers, new FollowingTheGraph(feeder));
//
//
//
//        List<Integer> fitnessFirstAA = new ArrayList<>();
//        List<Integer> idFirstAA = new ArrayList<>();
//        agents.getPopulation().forEach(p -> {
//            fitnessFirstAA.add(p.getFitness());
//            idFirstAA.add(p.getModel().getId());
//        });
//        List<Integer> fitnessFirstCCA = new ArrayList<>();
//        List<Integer> idFirstCCA = new ArrayList<>();
//        classifiers.getPopulation().forEach(p -> {
//            fitnessFirstCCA.add(p.getFitness());
//            idFirstCCA.add(p.getModel().getId());
//        });
//
//        System.out.println("Agent Fitness");
//        System.out.println(fitnessFirstAA.toString());
//        System.out.println("Classifiers Fitness");
//        System.out.println(fitnessFirstCCA.toString());
//        System.out.println("Agent ID Model");
//        System.out.println(idFirstAA.toString());
//        System.out.println("Classifiers ID Model");
//        System.out.println(idFirstCCA.toString());





//        agents.resetFitness();
//        classifiers.resetFitness();
//        agents.evaluateIndividualsParallelVersion(classifiers, new FollowingTheGraph(feeder));
//
//
//        List<Integer> fitnessFirstAPS = new ArrayList<>();
//        List<Integer> idFirstAPS = new ArrayList<>();
//        agents.getPopulation().forEach(p -> {
//            fitnessFirstAPS.add(p.getFitness());
//            idFirstAPS.add(p.getModel().getId());
//        });
//        List<Integer> fitnessFirstCCPS = new ArrayList<>();
//        List<Integer> idFirstCCPS = new ArrayList<>();
//        classifiers.getPopulation().forEach(p -> {
//            fitnessFirstCCPS.add(p.getFitness());
//            idFirstCCPS.add(p.getModel().getId());
//        });
//
//        System.out.println("Agent Fitness");
//        System.out.println(fitnessFirstAPS.toString());
//        System.out.println("Classifiers Fitness");
//        System.out.println(fitnessFirstCCPS.toString());
//        System.out.println("Agent ID Model");
//        System.out.println(idFirstAPS.toString());
//        System.out.println("Classifiers ID Model");
//        System.out.println(idFirstCCPS.toString());


    }

}
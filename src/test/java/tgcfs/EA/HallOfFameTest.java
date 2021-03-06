package tgcfs.EA;

import org.junit.Test;
import tgcfs.Agents.Models.LSTMAgent;
import tgcfs.Config.ReadConfig;
import tgcfs.EA.Helpers.HallOfFame;
import tgcfs.EA.Mutation.NonUniformMutation;
import tgcfs.NN.EvolvableModel;
import tgcfs.Utils.IndividualStatus;
import tgcfs.Utils.RandomGenerator;

import java.util.logging.Logger;

import static org.junit.Assert.assertNotNull;

/**
 * Created by Alessandro Zonta on 15/01/2018.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 */
public class HallOfFameTest {

    @Test
    public void addIndividual() throws Exception {
        new ReadConfig.Configurations();
        new RandomGenerator();
        EvolvableModel m = new LSTMAgent(1,1,1,1);
        Logger log =  Logger.getLogger(HallOfFameTest.class.getName());
        HallOfFame hallOfFame = new HallOfFame(m,log);
        hallOfFame.getHallOfFame().forEach(individual1 -> System.out.println(individual1.getIndividualStatus().toString() + " " + individual1.getModel().getId()));
        System.out.println("-------");
        for(int i = 0; i < 20; i++) {
            Individual individual = new NonUniformMutation(5, IndividualStatus.AGENT);
            individual.setModel(m);
            hallOfFame.addIndividual(individual);
            hallOfFame.getHallOfFame().forEach(individual1 -> System.out.println(individual1.getIndividualStatus().toString() + " " + individual1.getModel().getId()));
            System.out.println("-------");
        }
    }

    @Test
    public void getSample() throws Exception {
        new ReadConfig.Configurations();
        new RandomGenerator();
        EvolvableModel m = new LSTMAgent(1,1,1,1);
        Logger log =  Logger.getLogger(HallOfFameTest.class.getName());
        HallOfFame hallOfFame = new HallOfFame(m,log);
        for(int i = 0; i < 5; i++) {
            Individual individual = new NonUniformMutation(5, IndividualStatus.AGENT);
            individual.setModel(m);
            hallOfFame.addIndividual(individual);
            hallOfFame.getHallOfFame().forEach(individual1 -> System.out.println(individual1.getIndividualStatus().toString() + " " + individual1.getModel().getId()));
            System.out.println("--------------------------");
        }
        hallOfFame.createSample();
        assertNotNull(hallOfFame.getSample());
        System.out.println("---------sample-----------");
        hallOfFame.getSample().forEach(individual1 -> System.out.println(individual1.getIndividualStatus().toString() + " " + individual1.getModel().getId()));
        System.out.println("--------------------------");
        for(int i = 0; i < 7; i++) {
            Individual individual = new NonUniformMutation(5, IndividualStatus.AGENT);
            individual.setModel(m);
            hallOfFame.addIndividual(individual);
            hallOfFame.getHallOfFame().forEach(individual1 -> System.out.println(individual1.getIndividualStatus().toString() + " " + individual1.getModel().getId()));
            System.out.println("--------------------------");
        }
        hallOfFame.createSample();
        assertNotNull(hallOfFame.getSample());
        System.out.println("---------sample-----------");
        hallOfFame.getSample().forEach(individual1 -> System.out.println(individual1.getIndividualStatus().toString() + " " + individual1.getModel().getId()));
        System.out.println("--------------------------");
        for(int i = 0; i < 14; i++) {
            Individual individual = new NonUniformMutation(5, IndividualStatus.AGENT);
            individual.setModel(m);
            hallOfFame.addIndividual(individual);
            hallOfFame.getHallOfFame().forEach(individual1 -> System.out.println(individual1.getIndividualStatus().toString() + " " + individual1.getModel().getId()));
            System.out.println("--------------------------");
        }
        hallOfFame.createSample();
        assertNotNull(hallOfFame.getSample());
        System.out.println("---------sample-----------");
        hallOfFame.getSample().forEach(individual1 -> System.out.println(individual1.getIndividualStatus().toString() + " " + individual1.getModel().getId()));
        System.out.println("--------------------------");


    }

    @Test
    public void getHallOfFame() throws Exception {
        new ReadConfig.Configurations();
        new RandomGenerator();
        EvolvableModel m = new LSTMAgent(1,1,1,1);
        Logger log =  Logger.getLogger(HallOfFameTest.class.getName());
        HallOfFame hallOfFame = new HallOfFame(m,log);
        for(int i = 0; i < 5; i++) {
            hallOfFame.addIndividual(new NonUniformMutation(5));
        }
        assertNotNull(hallOfFame.getHallOfFame());

    }

    @Test
    public void TestRandom() throws Exception {
        new ReadConfig.Configurations();
        new RandomGenerator();
        EvolvableModel m = new LSTMAgent(1,1,1,1);
        Logger log =  Logger.getLogger(HallOfFameTest.class.getName());
        HallOfFame hallOfFameOne = new HallOfFame(m,log, new Agents(log), 5);
        HallOfFame hallOfFameTwo = new HallOfFame(m,log, new Agents(log), 5);
        for(int i = 0; i < 10; i++) {
            Individual individual = new NonUniformMutation(5, IndividualStatus.AGENT);
            individual.setModel(m);
            hallOfFameOne.addIndividual(individual);
            Individual individual2 = new NonUniformMutation(5, IndividualStatus.AGENT);
            individual2.setModel(m);
            hallOfFameTwo.addIndividual(individual2);
            System.out.println("------- One -------");
            hallOfFameOne.getHallOfFame().forEach(individual1 -> System.out.println(individual1.getIndividualStatus().toString() + " " + individual1.getModel().getId()));
            System.out.println("------- Two -------");
            hallOfFameTwo.getHallOfFame().forEach(individual1 -> System.out.println(individual1.getIndividualStatus().toString() + " " + individual1.getModel().getId()));
        }

        hallOfFameOne.createSample();
        System.out.println("---------sample-----------");
        hallOfFameOne.getSample().forEach(individual1 -> System.out.println(individual1.getIndividualStatus().toString() + " " + individual1.getModel().getId()));
        System.out.println("--------------------------");


        hallOfFameTwo.createSample();
        System.out.println("---------sample-----------");
        hallOfFameTwo.getSample().forEach(individual1 -> System.out.println(individual1.getIndividualStatus().toString() + " " + individual1.getModel().getId()));
        System.out.println("--------------------------");
    }

}
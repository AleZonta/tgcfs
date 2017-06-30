package tgcfs.EA;

import org.junit.Test;
import tgcfs.Agents.LSTMAgent;
import tgcfs.Config.ReadConfig;
import tgcfs.EA.Mutation.RandomResetting;
import tgcfs.EA.Mutation.UncorrelatedMutation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import static junit.framework.TestCase.*;

/**
 * Created by Alessandro Zonta on 29/05/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 */
public class IndividualTest {
    @Test
    public void getObjectiveParameters() throws Exception {
        new ReadConfig.Configurations();
        Individual individual = new UncorrelatedMutation();
        assertNull(individual.getObjectiveParameters());
        individual = new UncorrelatedMutation(5);
        assertNotNull(individual.getObjectiveParameters());

        individual = new RandomResetting();
        assertNull(individual.getObjectiveParameters());
        individual = new RandomResetting(5);
        assertNotNull(individual.getObjectiveParameters());
    }

    @Test
    public void getMutationStrengths() throws Exception {
        new ReadConfig.Configurations();
        Individual individual = new UncorrelatedMutation();
        assertNull(((UncorrelatedMutation)individual).getMutationStrengths());
        individual = new UncorrelatedMutation(5);
        assertNotNull(((UncorrelatedMutation)individual).getMutationStrengths());
    }

    @Test
    public void getFitness() throws Exception {
        new ReadConfig.Configurations();
        Individual individual = new UncorrelatedMutation();
        assertNull(individual.getFitness());
        individual = new UncorrelatedMutation(5);
        assertNotNull(individual);
        individual.setFitness(10);
        assertNotNull(individual.getFitness());
        assertEquals(new Integer(10), individual.getFitness());

        individual = new RandomResetting();
        assertNull(individual.getFitness());
        individual = new RandomResetting(5);
        assertNotNull(individual);
        individual.setFitness(10);
        assertNotNull(individual.getFitness());
        assertEquals(new Integer(10), individual.getFitness());
    }

    @Test
    public void setFitness() throws Exception {
        new ReadConfig.Configurations();
        Individual individual = new UncorrelatedMutation(5);
        assertNotNull(individual);
        individual.setFitness(10);

        individual = new RandomResetting(5);
        assertNotNull(individual);
        individual.setFitness(10);
    }

    @Test
    public void getModel() throws Exception {
        new ReadConfig.Configurations();
        Individual individual = new UncorrelatedMutation();
        assertNull(individual.getModel());
        individual = new UncorrelatedMutation(5);
        individual.setModel(new LSTMAgent(1,1,1,1));
        assertEquals(LSTMAgent.class,individual.getModel().getClass());

        individual = new RandomResetting();
        assertNull(individual.getModel());
        individual = new RandomResetting(5);
        individual.setModel(new LSTMAgent(1,1,1,1));
        assertEquals(LSTMAgent.class,individual.getModel().getClass());
    }

    @Test
    public void setModel() throws Exception {
        new ReadConfig.Configurations();
        Individual individual = new UncorrelatedMutation(5);
        individual.setModel(new LSTMAgent(1,1,1,1));

        individual = new RandomResetting(5);
        individual.setModel(new LSTMAgent(1,1,1,1));
    }

    @Test
    public void increaseFitness() throws Exception {
        new ReadConfig.Configurations();
        Individual individual = new UncorrelatedMutation(5);
        assertEquals(new Integer(0), individual.getFitness());
        individual.increaseFitness();
        assertEquals(new Integer(1), individual.getFitness());

        individual = new UncorrelatedMutation();
        try{
            individual.increaseFitness();
        }catch (Exception e){
            assertEquals("Individual not correctly initialised", e.getMessage());
        }

        individual = new RandomResetting(5);
        assertEquals(new Integer(0), individual.getFitness());
        individual.increaseFitness();
        assertEquals(new Integer(1), individual.getFitness());

        individual = new RandomResetting();
        try{
            individual.increaseFitness();
        }catch (Exception e){
            assertEquals("Individual not correctly initialised", e.getMessage());
        }
    }

    @Test
    public void resetFitness() throws Exception {
        new ReadConfig.Configurations();
        Individual individual = new UncorrelatedMutation(5);
        individual.increaseFitness();
        individual.increaseFitness();
        individual.increaseFitness();
        assertEquals(new Integer(3), individual.getFitness());
        individual.resetFitness();
        assertEquals(new Integer(0), individual.getFitness());

        individual = new RandomResetting(5);
        individual.increaseFitness();
        individual.increaseFitness();
        individual.increaseFitness();
        assertEquals(new Integer(3), individual.getFitness());
        individual.resetFitness();
        assertEquals(new Integer(0), individual.getFitness());
    }

    @Test
    public void mutate() throws Exception {
        new ReadConfig.Configurations();
//        Individual individual = new UncorrelatedMutation();
//        assertNotNull(individual);
//        assertNull(individual.getFitness());
//        assertNull(((UncorrelatedMutation)individual).getMutationStrengths());
//        assertNull(individual.getObjectiveParameters());
//
//        individual = new UncorrelatedMutation(10);
//        assertNotNull(individual);
//        assertNotNull(individual.getFitness());
//        assertNotNull(((UncorrelatedMutation)individual).getMutationStrengths());
//        assertNotNull(individual.getObjectiveParameters());
//
//        System.out.println(individual.getObjectiveParameters());
//        System.out.println(((UncorrelatedMutation)individual).getMutationStrengths());
//
//        individual.mutate(10);
//
//        System.out.println(individual.getObjectiveParameters());
//        System.out.println(((UncorrelatedMutation)individual).getMutationStrengths());
//
//        Individual individual2 = new UncorrelatedMutation(10);
//        IntStream.range(0,100).forEach(i -> {
//            individual2.mutate(10);
//        });
//
//
//        individual2.getObjectiveParameters().forEach(val -> {
//            System.out.println(val);
//            assertTrue(val >= -4.0 && val <= 4.0);
//        });


        Individual individual = new RandomResetting();
        assertNotNull(individual);
        assertNull(individual.getFitness());
        assertNull(individual.getObjectiveParameters());

        individual = new UncorrelatedMutation(10);
        assertNotNull(individual);
        assertNotNull(individual.getFitness());
        assertNotNull(individual.getObjectiveParameters());

        System.out.println(individual.getObjectiveParameters());

        individual.mutate(10);

        System.out.println(individual.getObjectiveParameters());

        Individual individual3 = new RandomResetting(100);
        List<Double> t = new ArrayList<>();
        individual3.getObjectiveParameters().forEach(el -> t.add(new Double(el)));
        IntStream.range(0,1000).forEach(i -> {
            individual3.mutate(100);
        });


        individual3.getObjectiveParameters().forEach(val -> {
            System.out.println(val);
            assertTrue(val >= -4.0 && val <= 4.0);
        });

        final Integer[] count = {0};
        IntStream.range(0, t.size()).forEach(i ->{
            if(Objects.equals(t.get(i), individual3.getObjectiveParameters().get(i))){
                count[0]++;
            }
        });
        System.out.println(count[0]);
        assertTrue(count[0]!=individual3.getObjectiveParameters().size());

    }
}
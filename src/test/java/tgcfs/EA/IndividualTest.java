package tgcfs.EA;

import org.junit.Test;
import tgcfs.Agents.LSTMAgent;
import tgcfs.Agents.OutputNetwork;
import tgcfs.NN.OutputsNetwork;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;

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
        Individual individual = new Individual();
        assertNull(individual.getObjectiveParameters());
        individual = new Individual(5);
        assertNotNull(individual.getObjectiveParameters());
    }

    @Test
    public void getMutationStrengths() throws Exception {
        Individual individual = new Individual();
        assertNull(individual.getMutationStrengths());
        individual = new Individual(5);
        assertNotNull(individual.getMutationStrengths());
    }

    @Test
    public void getFitness() throws Exception {
        Individual individual = new Individual();
        assertNull(individual.getFitness());
        individual = new Individual(5);
        assertNotNull(individual);
        individual.setFitness(10);
        assertNotNull(individual.getFitness());
        assertEquals(new Integer(10), individual.getFitness());
    }

    @Test
    public void setFitness() throws Exception {
        Individual individual = new Individual(5);
        assertNotNull(individual);
        individual.setFitness(10);
    }

    @Test
    public void getOutput() throws Exception {
        Individual individual = new Individual(5);
        List<OutputsNetwork> output = new ArrayList<>();
        output.add(new OutputNetwork(10.0,30.0, 40.0));
        output.add(new OutputNetwork(20.0,40.0, 50.0));
        individual.setOutput(output);
        assertNotNull(individual.getOutput());

    }

    @Test
    public void setOutput() throws Exception {
        Individual individual = new Individual(5);
        List<OutputsNetwork> output = new ArrayList<>();
        output.add(new OutputNetwork(10.0,30.0,40.0));
        output.add(new OutputNetwork(20.0,40.0,50.0));
        individual.setOutput(output);
    }

    @Test
    public void getModel() throws Exception {
        Individual individual = new Individual();
        assertNull(individual.getModel());
        individual = new Individual(5);
        individual.setModel(new LSTMAgent(1,1,1,1));
        assertEquals(LSTMAgent.class,individual.getModel().getClass());
    }

    @Test
    public void setModel() throws Exception {
        Individual individual = new Individual(5);
        individual.setModel(new LSTMAgent(1,1,1,1));
    }

    @Test
    public void increaseFitness() throws Exception {
        Individual individual = new Individual(5);
        assertEquals(new Integer(0), individual.getFitness());
        individual.increaseFitness();
        assertEquals(new Integer(1), individual.getFitness());

        individual = new Individual();
        try{
            individual.increaseFitness();
        }catch (Exception e){
            assertEquals("Individual not correctly initialised", e.getMessage());
        }
    }

    @Test
    public void resetFitness() throws Exception {
        Individual individual = new Individual(5);
        individual.increaseFitness();
        individual.increaseFitness();
        individual.increaseFitness();
        assertEquals(new Integer(3), individual.getFitness());
        individual.resetFitness();
        assertEquals(new Integer(0), individual.getFitness());
    }

    @Test
    public void mutate() throws Exception {
        Individual individual = new Individual();
        assertNotNull(individual);
        assertNull(individual.getFitness());
        assertNull(individual.getMutationStrengths());
        assertNull(individual.getObjectiveParameters());

        individual = new Individual(10);
        assertNotNull(individual);
        assertNotNull(individual.getFitness());
        assertNotNull(individual.getMutationStrengths());
        assertNotNull(individual.getObjectiveParameters());

        System.out.println(individual.getObjectiveParameters());
        System.out.println(individual.getMutationStrengths());

        individual.mutate(10);

        System.out.println(individual.getObjectiveParameters());
        System.out.println(individual.getMutationStrengths());



    }
}
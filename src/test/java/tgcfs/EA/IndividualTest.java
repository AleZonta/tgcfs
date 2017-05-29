package tgcfs.EA;

import org.junit.Test;

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
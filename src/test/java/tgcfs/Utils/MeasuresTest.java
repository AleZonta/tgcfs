package tgcfs.Utils;

import org.junit.Test;
import tgcfs.EA.Individual;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.Assert.assertTrue;

/**
 * Created by Alessandro Zonta on 30/10/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 */
public class MeasuresTest {
    @Test
    public void getMean() throws Exception {
        List<Individual> population = this.createPopulation(15);
        Measures m = new Measures(population);
        assertTrue(m.getMean() >= 0);
        System.out.println("mean -> " + m.getMean());
    }

    @Test
    public void getStd() throws Exception {
        List<Individual> population = this.createPopulation(15);
        Measures m = new Measures(population);
        assertTrue(m.getStd() >=0);
        System.out.println("std -> " + m.getStd());
    }

    @Test
    public void getEngadgement() throws Exception {
        List<Individual> population = this.createPopulation(15);
        Measures m = new Measures(population);
        assertTrue(m.getEngadgement() >= 0);
        System.out.println("engagement -> " + m.getEngadgement());

    }

    /**
     * Create the population for test
     * @return list of all the individuals
     */
    private List<Individual> createPopulation(int num){
        List<Individual> population = new ArrayList<>();
        IntStream.range(0, num).forEach(i -> {
            Individual a = new Individual() {
                @Override
                public void mutate(int n) {
                }

                @Override
                public Individual deepCopy() {
                    return null;
                }
            };
            a.setFitness(RandomGenerator.getNextInt(0,16000));
            population.add(a);
        });

        population.forEach(individual -> System.out.println(individual.getFitness().toString()));

        return population;
    }

}
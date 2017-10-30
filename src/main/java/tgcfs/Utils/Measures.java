package tgcfs.Utils;

import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.util.FastMath;
import tgcfs.EA.Individual;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alessandro Zonta on 30/10/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 *
 *
 * This class implements the measures for computing the disengagement
 */
public class Measures {
    private double mean;
    private double std;
    private double engadgement;

    /**
     * Compute the statistics of the current population
     * The method computes the mean and the standard deviation of all the fitness
     * It is also computing the measure of disengagement explained in:
     *
     * Cartlidge, J. P. (2004). Rules of engagement: competitive coevolutionary dynamics in computational systems,
     * (June), 198. Retrieved from http://etheses.whiterose.ac.uk/1315/1/cartlidge.pdf
     *
     * @param population
     */
    public Measures(List<Individual> population){
        List<Double> fitness = new ArrayList<>();
        population.forEach(individual -> fitness.add(new Double(individual.getFitness())));
        double[] values = fitness.stream().mapToDouble(Double::doubleValue).toArray();
        this.mean = StatUtils.mean(values);
        this.std = FastMath.sqrt(StatUtils.variance(values));

        //compute the disengagement measure following what written in the paper
        long l = fitness.stream().distinct().count();
        this.engadgement = (double)(l - 1) / (fitness.size() - 1);
    }

    /**
     * Getter for the mean
     * @return double val
     */
    public double getMean() {
        return this.mean;
    }

    /**
     * Getter for the standard deviation
     * @return double val
     */
    public double getStd() {
        return this.std;
    }

    /**
     * Getter for the engagemebr
     * @return double val
     */
    public double getEngadgement() {
        return this.engadgement;
    }
}

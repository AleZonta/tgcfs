package tgcfs.Utils;

import org.apache.commons.math3.random.MersenneTwister;
import tgcfs.Config.ReadConfig;

import java.util.Random;

/**
 * Created by Alessandro Zonta on 17/11/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 */
public class RandomGenerator {
    private static MersenneTwister rand;
    private static Random random;

    /**
     * Initialise with the seed
     * @throws Exception error in reading the random
     */
    public RandomGenerator() throws Exception {
        //read seed and set it
//        rand = new MersenneTwister(ReadConfig.Configurations.getSeed());
        random = new Random(ReadConfig.Configurations.getSeed());
    }

    /**
     * Return next random integer
     * @return int value
     */
    public static int getNextInt(){
        return rand.nextInt();
    }

    /**
     * Return next random integer between boundaries
     * @param lowerBound lower bound
     * @param upperBound upper bound
     * @return int value
     */
    public static int getNextInt(int lowerBound, int upperBound) {
        return rand.nextInt(upperBound - lowerBound) + lowerBound;
    }

    /**
     * Return next random double
     * @return double value
     */
    public static double getNextDouble(){
        return rand.nextDouble();
    }

    /**
     * Return next random integer between boundaries
     * @param lowerBound lower bound
     * @param upperBound upper bound
     * @return int value
     */
    public static double getNextDouble(double lowerBound, double upperBound) {
        return  lowerBound + (upperBound - lowerBound) * rand.nextDouble();
    }
}

package tgcfs.Utils;

import org.apache.commons.math3.random.MersenneTwister;
import tgcfs.Config.ReadConfig;

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

    /**
     * Initialise with the seed
     * @throws Exception error in reading the random
     */
    public RandomGenerator() throws Exception {
        //read seed and set it
        rand = new MersenneTwister(ReadConfig.Configurations.getSeed());
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

    /**
     * Return next gaussian generator
     * @return double value
     */
    public static double getNextGaussian(){
        return rand.nextGaussian();
    }

    /**
     * Return next gaussian value with specific std and mean
     * @param std std
     * @param mean mean
     * @return double value
     */
    public static double getNextXavier(double std, double mean){
        return rand.nextGaussian()*std+mean;
    }


}

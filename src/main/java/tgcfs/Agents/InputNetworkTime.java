package tgcfs.Agents;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

/**
 * Created by Alessandro Zonta on 07/02/2018.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 */
public class InputNetworkTime extends InputNetwork {
    public static final int inputSize = 3; //the size of the input corresponding to the four fields

    /**
     * Constructor with three parameters. all the inputs
     * Matches the super constructor
     * @param directionAPF  Double number corresponding to the direction retrieved form the apf
     * @param speed  Double number corresponding to the speed
     * @param bearing Double number corresponding to the bearing
     */
    public InputNetworkTime(double directionAPF, double speed, double bearing) {
        super(directionAPF, speed, bearing);
    }

    /**
     * Constructor with four parameters. all the inputs
     * Mathes the super constructor but uses the space variable as time variable
     * @param directionAPF Double number corresponding to the direction retrieved form the apf
     * @param speed Double number corresponding to the speed
     * @param bearing Double number corresponding to the bearing
     * @param time time between two points
     */
    public InputNetworkTime(double directionAPF, double speed, double bearing, double time) {
        super(directionAPF, speed, bearing, time);
    }

    /**
     * @implNote Implementation from Interface
     * @return list containing all the fields
     */
    @Override
    public INDArray serialise(){
        INDArray array = Nd4j.zeros(inputSize);
        array.putScalar(0, this.getSpeed());
        array.putScalar(1, this.getBearing());
        array.putScalar(2, this.getDirectionAPF());
        array.putScalar(2, this.getSpace());
        return array;
    }

    /**
     * Return the time
     * @return double value
     */
    public double getTime() {
        return super.getSpace();
    }

    @Override
    public String toString() {
        return "{" + this.getSpeed() + ", " + this.getBearing() + ", " + this.getSpace() + "}";
    }
}

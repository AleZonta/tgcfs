package tgcfs.Agents;

import lgds.trajectories.Point;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import tgcfs.InputOutput.Normalisation;
import tgcfs.NN.InputsNetwork;

/**
 * Created by Alessandro Zonta on 29/05/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 *
 * This class Implements the input of the agents
 */
public class InputNetwork implements InputsNetwork {
    protected double directionAPF;
    protected double speed;
    protected double bearing;
    private Point targetPoint;
    public static final int inputSize = 3; //the size of the input corresponding to the three fields here

    /**
     * Constructor zero parameter
     */
    public InputNetwork(){}

    /**
     * Constructor with three parameters. all the inputs
     * It is also normalising the input in the range ±1 for the NN
     * @param directionAPF Double number corresponding to the direction retrieved form the apf
     * @param speed Double number corresponding to the speed
     * @param bearing Double number corresponding to the bearing
     */
    public InputNetwork(double directionAPF, double speed, double bearing){
        this.bearing = Normalisation.convertAngularSpeed(bearing);
        try {
            this.speed = Normalisation.convertSpeed(speed);
        } catch (Exception e) {
            throw new Error("Error with speed.");
        }
        this.directionAPF = Normalisation.convertDirectionData(directionAPF);
        this.targetPoint = null;
    }


    /**
     * Getter for the direction of the APF
     * @return Double value
     */
    public double getDirectionAPF() {
        return this.directionAPF;
    }

    /**
     * Getter for the speed
     * @return Double value
     */
    public double getSpeed() {
        return this.speed;
    }

    /**
     * Getter for the bearing
     * @return Double value
     */
    public double getBearing() {
        return this.bearing;
    }


    /**
     * @implNote Implementation from Interface
     * @return list containing all the fields
     */
    @Override
    public INDArray serialise(){
        INDArray array = Nd4j.zeros(inputSize);
        array.putScalar(0, this.speed);
        array.putScalar(1, this.bearing);
        array.putScalar(2, this.directionAPF);
        return array;
    }

    /**
     * Setter for the target point
     * @param targetPoint point
     */
    public void setTargetPoint(Point targetPoint) {
        this.targetPoint = targetPoint;
    }

    /**
     * Getter for the target point
     * @return target point
     */
    public Point getTargetPoint() {
        return targetPoint;
    }

    /**
     * Serialise object ready for the classifier
     * @return {@link INDArray} vector
     */
    public INDArray serialiseAsInputClassifier(){
        INDArray array = Nd4j.zeros(2);
        array.putScalar(0, this.speed);
        array.putScalar(1, this.bearing);
        return array;
    }

    @Override
    public String toString() {
        return "{" + this.speed + ", " + this.bearing + "}";
    }
}

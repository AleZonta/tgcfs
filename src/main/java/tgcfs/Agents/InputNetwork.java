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
    private double directionAPF;
    private double speed;
    private double bearing;
    private double time;
    private double angularSpeed;
    private Point targetPoint;
    public static final int inputSize = 4; //the size of the input corresponding to the three fields here

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
        this.bearing = Normalisation.convertDirectionData(bearing);
        try {
            this.speed = Normalisation.convertSpeed(speed);
        } catch (Exception e) {
            throw new Error("Error with speed.");
        }
        this.directionAPF = Normalisation.convertDirectionData(directionAPF);
        this.targetPoint = null;
        this.time = -99;
        this.angularSpeed = -99;
    }

    /**
     * Constructor with three parameters. all the inputs
     * It is also normalising the input in the range ±1 for the NN
     * @param directionAPF Double number corresponding to the direction retrieved form the apf
     * @param speed Double number corresponding to the speed
     * @param bearing Double number corresponding to the bearing
     * @param time time next prediction
     */
    public InputNetwork(double directionAPF, double speed, double bearing, double time){
        this.bearing = Normalisation.convertDirectionData(bearing);
        try {
            this.speed = Normalisation.convertSpeed(speed);
        } catch (Exception e) {
            throw new Error("Error with speed.");
        }
        this.directionAPF = Normalisation.convertDirectionData(directionAPF);
        this.targetPoint = null;
        this.time = Normalisation.convertTime(time);
        this.angularSpeed = -99;
    }

    /**
     * Constructor with three parameters. all the inputs
     * It is also normalising the input in the range ±1 for the NN
     * @param directionAPF Double number corresponding to the direction retrieved form the apf
     * @param speed Double number corresponding to the speed
     * @param bearing Double number corresponding to the bearing
     * @param time time next prediction
     * @param angularSpeed angular speed
     */
    public InputNetwork(double directionAPF, double speed, double bearing, double time, double angularSpeed){
        this.bearing = Normalisation.convertDirectionData(bearing);
        try {
            this.speed = Normalisation.convertSpeed(speed);
        } catch (Exception e) {
            throw new Error("Error with speed.");
        }
        this.directionAPF = Normalisation.convertDirectionData(directionAPF);
        this.targetPoint = null;
        this.time = Normalisation.convertTime(time);
        this.angularSpeed = angularSpeed;
    }

    /**
     * Constructor two parameters
     * @param indArray {@link INDArray} containing serialisation another {@link InputNetwork}
     * @param time time next prediction
     */
    public InputNetwork(INDArray indArray, double time){
        this.speed = indArray.getDouble(0);
        this.bearing = indArray.getDouble(1);
        this.directionAPF = indArray.getDouble(2);
        this.time = Normalisation.convertTime(time);
        this.angularSpeed = -99;
    }

    /**
     * Constructor two parameters
     * @param indArray {@link INDArray} containing serialisation another {@link InputNetwork}
     * @param time time next prediction
     */
    public InputNetwork(INDArray indArray, double time, double angularSpeed){
        this.speed = indArray.getDouble(0);
        this.bearing = indArray.getDouble(1);
        this.directionAPF = indArray.getDouble(2);
        this.time = Normalisation.convertTime(time);
        this.angularSpeed = angularSpeed;
    }

    /**
     * Deep Copy constructor with no conversions
     * @param directionAPF
     * @param speed
     * @param bearing
     * @param time
     * @param angularSpeed
     * @param targetPoint
     */
    public InputNetwork(double directionAPF, double speed, double bearing, double time, double angularSpeed, Point targetPoint) {
        this.directionAPF = directionAPF;
        this.speed = speed;
        this.bearing = bearing;
        this.time = time;
        this.angularSpeed = angularSpeed;
        this.targetPoint = targetPoint;
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
        array.putScalar(3, this.time);
        return array;
    }

    @Override
    public InputsNetwork deepCopy() {
        return new InputNetwork(this.directionAPF, this.speed, this.bearing, this.time, this.angularSpeed, this.targetPoint);
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
     * Getter for the angular speed
     * @return angular speed
     */
    public double getAngularSpeed() {
        return this.angularSpeed;
    }

    /**
     * Serialise object ready for the classifier
     * @return {@link INDArray} vector
     */
    public INDArray serialiseAsInputClassifier(){
        INDArray array = Nd4j.zeros(2);
        array.putScalar(0, this.speed);
        array.putScalar(1, this.angularSpeed);
        return array;
    }

    /**
     * Return the time
     * @return double value
     */
    public double getTime() {
        return this.time;
    }

    @Override
    public String toString() {
        return "{" + this.speed + ", " + this.bearing + ", " + this.time + ", " + this.angularSpeed + "}";
    }

}

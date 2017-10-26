package tgcfs.Agents;

import lgds.trajectories.*;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import tgcfs.InputOutput.Normalisation;
import tgcfs.NN.InputsNetwork;

import java.lang.reflect.Field;

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
    private Point targetPoint;
    private double space;
    public static final int inputSize = 3; //the size of the input corresponding to the three fields here

    /**
     * Constructor with three parameters. all the inputs
     * It is also normalising the input in the range ±1 for the NN
     * @param directionAPF Double number corresponding to the direction retrieved form the apf
     * @param speed Double number corresponding to the speed
     * @param bearing Double number corresponding to the bearing
     */
    public InputNetwork(double directionAPF, double speed, double bearing){
        this.bearing = Normalisation.convertAngularSpeed(bearing);
        this.speed = Normalisation.convertSpeed(speed);
        this.directionAPF = Normalisation.convertDirectionData(directionAPF);
        this.targetPoint = null;

        Field[] allFields = InputNetwork.class.getDeclaredFields();
        if (allFields.length != inputSize + 3){
            throw new Error("Number of fields and variable expressing that do not correspond.");
        }

    }

    /**
     * Constructor with three parameters. all the inputs
     * It is also normalising the input in the range ±1 for the NN
     * @param directionAPF Double number corresponding to the direction retrieved form the apf
     * @param speed Double number corresponding to the speed
     * @param bearing Double number corresponding to the bearing
     * @param space distance between the two points
     */
    public InputNetwork(double directionAPF, double speed, double bearing, double space){
        this.bearing = Normalisation.convertAngularSpeed(bearing);
        this.speed = Normalisation.convertSpeed(speed);
        this.directionAPF = Normalisation.convertDirectionData(directionAPF);
        this.space = Normalisation.convertDistance(space);
        this.targetPoint = null;

        Field[] allFields = InputNetwork.class.getDeclaredFields();
        if (allFields.length != inputSize + 3){
            throw new Error("Number of fields and variable expressing that do not correspond.");
        }

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
        INDArray array = Nd4j.zeros(3);
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
     * Getter for the distance between points
     * @return Double value
     */
    public double getSpace() {
        return space;
    }


    /**
     * Serialise object ready for the classifier
     * @return {@link INDArray} vector
     */
    public INDArray serialiaseAsInputClassifier(){
        INDArray array = Nd4j.zeros(2);
        array.putScalar(0, this.speed);
        array.putScalar(1, this.bearing);
        return array;
    }
}

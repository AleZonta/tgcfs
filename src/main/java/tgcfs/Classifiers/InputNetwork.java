package tgcfs.Classifiers;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import tgcfs.InputOutput.Normalisation;
import tgcfs.NN.InputsNetwork;

import java.lang.reflect.Field;

/**
 * Created by Alessandro Zonta on 24/05/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 *
 * This class will implement the object speed and direction that will be the input of the classifier network
 */
public class InputNetwork implements InputsNetwork{
    private Double speed; //speed of the movement
    private Double direction; //direction of the movement
    public static final Integer inputSize = 2; //the size of the input corresponding to the two fields here

    /**
     * Constructor zero parameter = everything to null
     */
    public InputNetwork(){
        this.direction = null;
        this.speed = null;

        Field[] allFields = InputNetwork.class.getDeclaredFields();
        if (allFields.length != inputSize + 1){
            throw new Error("Number of fields and variable expressing that do not correspond.");
        }
    }

    /**
     * Constructor two parameters
     * @param speed speed parameter
     * @param direction direction parameter
     */
    public InputNetwork(Double speed, Double direction){
        this.speed = Normalisation.convertSpeed(speed);
        this.direction = Normalisation.convertDirectionData(direction);

        Field[] allFields = InputNetwork.class.getDeclaredFields();
        if (allFields.length != inputSize + 1){
            throw new Error("Number of fields and variable expressing that do not correspond.");
        }
    }

    /**
     * Getter for the speed variable
     * @return Double number
     */
    public double getSpeed() {
        return this.speed;
    }

    /**
     * Getter for the direction variable
     * @return Double variable
     */
    public double getDirection() {
        return this.direction;
    }


    /**
     * @implNote Implementation from Interface
     * @return list containing all the fields
     */
    @Override
    public INDArray serialise(){
        INDArray array = Nd4j.zeros(2);
        array.putScalar(0, this.speed);
        array.putScalar(1, this.direction);
        return array;
    }

    /**
     * Override to string method
     * @return the string version of the data
     */
    @Override
    public String toString() {
        return "InputNetwork{" + " " +
                "speed=" + this.speed + ", " +
                "bearing=" + this.direction + " " +
                '}';
    }
}

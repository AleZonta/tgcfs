package tgcfs.Classifiers;

import tgcfs.NN.InputsNetwork;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

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
        this.speed = speed;
        this.direction = direction;

        Field[] allFields = InputNetwork.class.getDeclaredFields();
        if (allFields.length != inputSize + 1){
            throw new Error("Number of fields and variable expressing that do not correspond.");
        }
    }

    /**
     * Getter for the speed variable
     * @return Double number
     */
    public Double getSpeed() {
        return this.speed;
    }

    /**
     * Getter for the direction variable
     * @return Double variable
     */
    public Double getDirection() {
        return this.direction;
    }


    /**
     * @implNote Implementation from Interface
     * @return list containing all the fields
     */
    @Override
    public List<Double> serialise(){
        List<Double> res = new ArrayList<>();
        res.add(this.speed);
        res.add(this.direction);
        return res;
    }

}

package tgcfs.Agents;

import tgcfs.NN.InputsNetwork;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

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
    private Double directionAPF;
    private Double speed;
    private Double bearing;
    public static final Integer inputSize = 3; //the size of the input corresponding to the three fields here

    /**
     * Constructor with three parameters. all the inputs
     * @param directionAPF Double number corresponding to the direction retrieved form the apf
     * @param speed Double number corresponding to the speed
     * @param bearing Double number corresponding to the bearing
     */
    public InputNetwork(Double directionAPF, Double speed, Double bearing){
        this.bearing = bearing;
        this.speed = speed;
        this.directionAPF = directionAPF;

        Field[] allFields = InputNetwork.class.getDeclaredFields();
        if (allFields.length != inputSize + 1){
            throw new Error("Number of fields and variable expressing that do not correspond.");
        }

    }

    /**
     * Getter for the direction of the APF
     * @return Double value
     */
    public Double getDirectionAPF() {
        return this.directionAPF;
    }

    /**
     * Getter for the speed
     * @return Double value
     */
    public Double getSpeed() {
        return this.speed;
    }

    /**
     * Getter for the bearing
     * @return Double value
     */
    public Double getBearing() {
        return this.bearing;
    }


    /**
     * @implNote Implementation from Interface
     * @return list containing all the fields
     */
    @Override
    public List<Double> serialise(){
        List<Double> res = new ArrayList<>();
        res.add(this.speed);
        res.add(this.bearing);
        res.add(this.directionAPF);
        return res;
    }


}

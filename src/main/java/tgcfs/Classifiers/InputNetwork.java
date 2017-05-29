package tgcfs.Classifiers;

import tgcfs.NN.InputsNetwork;

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

    /**
     * Constructor zero parameter = everything to null
     */
    public InputNetwork(){
        this.direction = null;
        this.speed = null;
    }

    /**
     * Constructor two parameters
     * @param speed speed parameter
     * @param direction direction parameter
     */
    public InputNetwork(Double speed, Double direction){
        this.direction = speed;
        this.speed = direction;
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

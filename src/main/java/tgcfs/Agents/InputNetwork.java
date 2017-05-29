package tgcfs.Agents;

import tgcfs.NN.InputsNetwork;

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

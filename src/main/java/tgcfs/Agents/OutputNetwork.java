package tgcfs.Agents;

import tgcfs.InputOutput.Normalisation;
import tgcfs.NN.OutputsNetwork;

import java.lang.reflect.Field;
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
 */
public class OutputNetwork implements OutputsNetwork {
    private Double speed;
    private Double bearing;
    private Double distance;
    public static final Integer outputSize = 3; //the size of the output corresponding to the two fields here

    /**
     * Constructor zero parameter
     * Check if the size is correct
     */
    public OutputNetwork(){
        Field[] allFields = OutputNetwork.class.getDeclaredFields();
        if (allFields.length != outputSize + 1){
            throw new Error("Number of fields and variable expressing that do not correspond.");
        }
    }

    /**
     * Constructor two parameters
     * @param speed speed parameter
     * @param bearing bearing parameter
     */
    public OutputNetwork(Double speed, Double bearing, Double distance){
        this.speed = speed;
        this.bearing = bearing;
        this.distance = distance;
        Field[] allFields = OutputNetwork.class.getDeclaredFields();
        if (allFields.length != outputSize + 1){
            throw new Error("Number of fields and variable expressing that do not correspond.");
        }
    }

    /**
     * Getter for speed
     * @return Double value of speed
     */
    public Double getSpeed() {
        return speed;
    }

    /**
     * Getter for bearing
     * @return Double value of bearing
     */
    public Double getBearing() {
        return bearing;
    }

    /**
     * Getter for distance
     * @return Double value of distance
     */
    public Double getDistance() { return this.distance;}

    /**
     * @implNote Implementation from Abstract class Algorithm
     * If the list in input does not have the right length an error is thrown
     * @param out list containing all the fields
     */
    @Override
    public void deserialise(List<Double> out) {
        if (out.size() != outputSize) throw new Error("List size is not correct");
        this.speed = Normalisation.decodeSpeed(out.get(0));
        this.bearing = Normalisation.decodeDirectionData(out.get(1));
        this.distance = Normalisation.decodeDistance(out.get(2));
    }
}

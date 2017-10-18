package tgcfs.Agents;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import tgcfs.InputOutput.Normalisation;
import tgcfs.NN.InputsNetwork;
import tgcfs.NN.OutputsNetwork;

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
 */
public class OutputNetwork implements OutputsNetwork {
    private double speed;
    private double bearing;
//    private double distance;
    public static final int outputSize = 2; //the size of the output corresponding to the two fields here

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
    public OutputNetwork(double speed, double bearing, double distance){
        this.speed = speed;
        this.bearing = bearing;
//        this.distance = distance;
        Field[] allFields = OutputNetwork.class.getDeclaredFields();
        if (allFields.length != outputSize + 1){
            throw new Error("Number of fields and variable expressing that do not correspond.");
        }
    }

    /**
     * Constructor two parameters
     * @param out {@link InputsNetwork} network to transform
     */
    public OutputNetwork(InputsNetwork out){
        if(!out.getClass().equals(InputNetwork.class)) throw new Error("Only InputNetwork Are accepted");
        InputNetwork net = (InputNetwork)out;
        this.speed = Normalisation.decodeSpeed(net.getSpeed());
        this.bearing = Normalisation.decodeDirectionData(net.getBearing());
//        this.distance = Normalisation.decodeDistance(net.getSpace());
        Field[] allFields = OutputNetwork.class.getDeclaredFields();
        if (allFields.length != outputSize + 1){
            throw new Error("Number of fields and variable expressing that do not correspond.");
        }
    }

    /**
     * Getter for speed
     * @return Double value of speed
     */
    public double getSpeed() {
        return speed;
    }

    /**
     * Getter for bearing
     * @return Double value of bearing
     */
    public double getBearing() {
        return bearing;
    }

//    /**
//     * Getter for distance
//     * @return Double value of distance
//     */
//    public double getDistance() { return this.distance;}

    /**
     * @implNote Implementation from Abstract class Algorithm
     * If the list in input does not have the right length an error is thrown
     * @param out list containing all the fields
     */
    @Override
    public void deserialise(INDArray out) {

        if (out.columns() != outputSize) {
            if (out.rows() != outputSize) {
                throw new Error("List size is not correct");
            }
        }
        if(out.columns() == outputSize) {
            this.speed = Normalisation.decodeSpeed(out.getDouble(0));
            this.bearing = Normalisation.decodeDirectionData(out.getDouble(1));
//            this.distance = Normalisation.decodeDistance(out.getDouble(2));
        }else{
            this.speed = Normalisation.decodeSpeed(out.getRow(0).getDouble(0));
            this.bearing = Normalisation.decodeDirectionData(out.getRow(1).getDouble(0));
//            this.distance = Normalisation.decodeDistance(out.getRow(2).getDouble(0));
        }
    }

    /**
     * Override to string method
     * @return the string version of the data
     */
    @Override
    public String toString() {
        return "OutputNetwork{" + " " +
                "speed=" + this.speed + ", " +
                "bearing=" + this.bearing + ", " +
//                "distance=" + this.distance + " " +
                '}';
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

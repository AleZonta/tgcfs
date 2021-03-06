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
    protected double speed;
    protected double bearing;
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
    public OutputNetwork(double speed, double bearing){
        this.speed = speed;
        this.bearing = bearing;
        Field[] allFields = OutputNetwork.class.getDeclaredFields();
        if (allFields.length != outputSize + 1){
            throw new Error("Number of fields and variable expressing that do not correspond.");
        }
    }

    /**
     * Constructor one parameters
     * @param out {@link InputsNetwork} network to transform
     */
    public OutputNetwork(InputsNetwork out){
        if(!out.getClass().equals(InputNetwork.class)) throw new Error("Only InputNetwork Are accepted");
        InputNetwork net = (InputNetwork)out;
        try {
            this.speed = Normalisation.decodeSpeed(net.getSpeed());
        } catch (Exception e) {
            throw new Error("Erro with speed.");
        }
        this.bearing = Normalisation.decodeDirectionData(net.getBearing());
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

    @Override
    public INDArray serialise() {
        INDArray array = Nd4j.zeros(outputSize);
        try {
            array.putScalar(0, Normalisation.convertSpeed(this.speed));
        } catch (Exception e) {
            e.printStackTrace();
        }
        array.putScalar(1, Normalisation.convertDirectionData(this.bearing));
        return array;
    }

    /**
     * @implNote Implementation from Abstract class Algorithm
     * If the list in input does not have the right length an error is thrown
     * @param out list containing all the fields
     */
    @Override
    public void deserialise(INDArray out) {
        if (out.columns() != outputSize) {
            throw new Error("List size is not correct");
        }
        try {
//                this.speed = out.getDouble(0);
            //linear activation lets keep the speed as normal
            //this.speed = Normalisation.decodeSpeed(out.getDouble(0));
            this.speed = Normalisation.decodeSpeed(out.getDouble(0));
        } catch (Exception e) {
            throw new Error("Erro with speed.");
        }
        this.bearing = Normalisation.decodeDirectionData(out.getDouble(1));
    }

    /**
     * Deep copy
     * @return {@link OutputNetwork} object
     */
    @Override
    public OutputNetwork deepCopy() {
        return new OutputNetwork(this.speed, this.bearing);
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

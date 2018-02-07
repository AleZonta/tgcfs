package tgcfs.Agents;

import org.nd4j.linalg.api.ndarray.INDArray;
import tgcfs.InputOutput.Normalisation;
import tgcfs.NN.InputsNetwork;

/**
 * Created by Alessandro Zonta on 07/02/2018.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 */
public class OutputNetworkTime extends OutputNetwork {
    public static final int outputSize = 3; //the size of the output corresponding to the two fields here

    /**
     * Constructor zero parameter
     * Check if the size is correct
     */
    public OutputNetworkTime(){
        super();
    }


    /**
     * Constructor two parameters
     * @param speed speed parameter
     * @param bearing bearing parameter
     * @param time time parameter
     */
    public OutputNetworkTime(double speed, double bearing, double time){
        super(speed, bearing, time);
    }


    /**
     * Constructor two parameters
     * @param speed speed parameter
     * @param bearing bearing parameter
     */
    public OutputNetworkTime(double speed, double bearing){
        super(speed, bearing);
    }

    /**
     * Constructor one parameters
     * @param out {@link InputsNetwork} network to transform
     */
    public OutputNetworkTime(InputsNetwork out){
        super(out);
        if(out.getClass().equals(InputNetworkTime.class)){
            this.distance = (((InputNetworkTime)out).getTime());
        }
    }

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
            try {
//                this.speed = out.getDouble(0);
                //linear activation lets keep the speed as normal
                //this.speed = Normalisation.decodeSpeed(out.getDouble(0));
                this.speed = Normalisation.decodeSpeed(out.getDouble(0));
            } catch (Exception e) {
                throw new Error("Erro with speed.");
            }
            this.bearing = Normalisation.decodeDirectionData(out.getDouble(1));
            this.distance = out.getDouble(2);

        }else{
            try {
                this.speed = Normalisation.decodeSpeed(out.getDouble(0));
            } catch (Exception e) {
                throw new Error("Erro with speed.");
            }

            this.bearing = Normalisation.decodeDirectionData(out.getRow(1).getDouble(0));
            this.distance = out.getRow(2).getDouble(0);
        }
    }

    /**
     * Deep copy
     * @return {@link OutputNetwork} object
     */
    @Override
    public OutputNetworkTime deepCopy() {
        return new OutputNetworkTime(this.speed, this.bearing, this.distance);
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
                "time=" + this.distance + ", " +
                '}';
    }


}

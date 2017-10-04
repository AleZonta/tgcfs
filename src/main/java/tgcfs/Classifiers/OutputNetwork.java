package tgcfs.Classifiers;

import org.nd4j.linalg.api.ndarray.INDArray;
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
public class OutputNetwork implements OutputsNetwork{
    private boolean real;
    public static final int outputSize = 1; //the size of the output corresponding to the field here

    /**
     * Contructor zero parameter
     * check if the numebr express as output size is correct
     */
    public OutputNetwork(){
        Field[] allFields = OutputNetwork.class.getDeclaredFields();
        if (allFields.length != outputSize + 1){
            throw new Error("Number of fields and variable expressing that do not correspond.");
        }
    }


    /**
     * Getter for real
     * @return Boolean value
     */
    public boolean getReal() {
        return real;
    }

    /**
     * @implNote Implementation from Abstract class Algorithm
     * If the list in input does not have the right length an error is thrown
     * @param out list containing all the fields
     */
    @Override
    public void deserialise(INDArray out) {
        if (out.columns() != outputSize) throw new Error("List size is not correct");
        if(out.getDouble(0) >= 0.0){
            this.real = Boolean.TRUE;
        }else{
            this.real = Boolean.FALSE;
        }
    }


}

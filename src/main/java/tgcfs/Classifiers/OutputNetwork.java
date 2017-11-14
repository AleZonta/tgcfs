package tgcfs.Classifiers;

import org.nd4j.linalg.api.ndarray.INDArray;
import tgcfs.Config.ReadConfig;
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
    private double realValue;
    public static int outputSize = 1; //the size of the output corresponding to the field here
    private int kindOfClassifier;


    public static void setOutputSize(int number){
        outputSize = number;
    }

    /**
     * Constructor for deep copy
     * @param value boolean if it is real
     * @param realValue real value obtained as a result
     */
    public OutputNetwork(boolean value, double realValue){
        this.realValue = realValue;
        this.real = value;
    }
    /**
     * Contructor zero parameter
     * check if the numebr express as output size is correct
     */
    public OutputNetwork(){
        Field[] allFields = OutputNetwork.class.getDeclaredFields();

        this.kindOfClassifier = 0; //0 means ENN
        try {
            this.kindOfClassifier = ReadConfig.Configurations.getValueClassifier();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(this.kindOfClassifier == 1){ //1 means LSTM
            if (allFields.length != outputSize + 2){
                throw new Error("Number of fields and variable expressing that do not correspond.");
            }
        }else{
            if (allFields.length != outputSize + 3){
                throw new Error("Number of fields and variable expressing that do not correspond.");
            }
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
        if (out.columns() != outputSize) {
            if (out.rows() != outputSize) {
                throw new Error("List size is not correct");
            }
        }
        //enn
        if(this.kindOfClassifier == 0){
            this.realValue = out.getDouble(0);
            if(out.getDouble(0) >= 0.0){
                this.real = Boolean.TRUE;
            }else{
                this.real = Boolean.FALSE;
            }
        }else{
            //lstm
            if(out.getDouble(0) >= out.getDouble(1)){
                this.real = Boolean.TRUE;
            }else{
                this.real = Boolean.FALSE;
            }
        }

    }

    /**
     * Deep copy
     * @return new {@link OutputNetwork} object
     */
    @Override
    public OutputNetwork deepCopy() {
        return new OutputNetwork(this.real, this.realValue);
    }

    /**
     * Getter for the real value obtained as a result
     * @return double var
     */
    public double getRealValue() {
        return realValue;
    }
}

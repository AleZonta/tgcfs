package tgcfs.Classifiers;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import tgcfs.Config.ReadConfig;
import tgcfs.InputOutput.Normalisation;
import tgcfs.NN.InputsNetwork;

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
 * This class will implement the object linearSpeed and angularSpeed that will be the input of the classifier network
 */
public class InputNetwork implements InputsNetwork{
    private Double linearSpeed; //linearSpeed of the movement
    private Double angularSpeed; //angularSpeed of the movement
    private Double averageLinearSpeed;
    private Double averageAngularSpeed;
    public static Integer inputSize = 2; //the size of the input corresponding to the two fields here

    /**
     * Constructor zero parameter = everything to null
     */
    public InputNetwork(){
        this.angularSpeed = null;
        this.linearSpeed = null;
        this.averageLinearSpeed = null;
        this.averageAngularSpeed = null;
        if(ReadConfig.tryNNclassifier) inputSize = 4;

//        Field[] allFields = InputNetwork.class.getDeclaredFields();
//        if (allFields.length != inputSize + 1){
//            throw new Error("Number of fields and variable expressing that do not correspond.");
//        }
    }

    /**
     * Constructor two parameters
     * @param linearSpeed linearSpeed parameter
     * @param angularSpeed angularSpeed parameter
     */
    public InputNetwork(double linearSpeed, double angularSpeed){
        try {
            this.linearSpeed = Normalisation.convertSpeed(linearSpeed);
        } catch (Exception e) {
            throw new Error("Error with linearSpeed.");
        }
        this.angularSpeed = Normalisation.convertAngularSpeed(angularSpeed);

        this.averageLinearSpeed = 0.0;
        this.averageAngularSpeed = 0.0;
        if(ReadConfig.tryNNclassifier) inputSize = 4;
//        Field[] allFields = InputNetwork.class.getDeclaredFields();
//        if (allFields.length != inputSize + 1){
//            throw new Error("Number of fields and variable expressing that do not correspond.");
//        }
    }

    /**
     * Constructor two parameters
     * @param linearSpeed linearSpeed parameter
     * @param angularSpeed angularSpeed parameter
     * @param translation if the data needs to be translated
     */
    public InputNetwork(double linearSpeed, double angularSpeed, boolean translation){
        if(translation){
            try {
                this.linearSpeed = Normalisation.convertSpeed(linearSpeed);
            } catch (Exception e) {
                throw new Error("Error with linearSpeed.");
            }
            this.angularSpeed = Normalisation.convertAngularSpeed(angularSpeed);
        }else {
            this.linearSpeed = linearSpeed;
            this.angularSpeed = angularSpeed;
        }
        this.averageLinearSpeed = 0.0;
        this.averageAngularSpeed = 0.0;
        if(ReadConfig.tryNNclassifier) inputSize = 4;
//        Field[] allFields = InputNetwork.class.getDeclaredFields();
//        if (allFields.length != inputSize + 1){
//            throw new Error("Number of fields and variable expressing that do not correspond.");
//        }
    }

    /**
     * Constructor two parameters
     * @param linearSpeed linearSpeed parameter
     * @param angularSpeed angularSpeed parameter
     */
    public InputNetwork(double linearSpeed, double angularSpeed, double averageLinearSpeed, double averageAngularSpeed){
        try {
            this.linearSpeed = Normalisation.convertSpeed(linearSpeed);
            this.averageLinearSpeed = Normalisation.convertSpeed(averageLinearSpeed);
        } catch (Exception e) {
            throw new Error("Error with linearSpeed.");
        }
        this.angularSpeed = Normalisation.convertAngularSpeed(angularSpeed);
        this.averageAngularSpeed = Normalisation.convertAngularSpeed(averageAngularSpeed);
        if(ReadConfig.tryNNclassifier) inputSize = 4;
    }

    /**
     * Constructor two parameters
     * @param linearSpeed linearSpeed parameter
     * @param angularSpeed angularSpeed parameter
     */
    public InputNetwork(double linearSpeed, double angularSpeed, double averageLinearSpeed, double averageAngularSpeed, boolean translation){
        if(translation){
            try {
                this.linearSpeed = Normalisation.convertSpeed(linearSpeed);
                this.averageLinearSpeed = Normalisation.convertSpeed(averageLinearSpeed);
            } catch (Exception e) {
                throw new Error("Error with linearSpeed.");
            }
            this.angularSpeed = Normalisation.convertAngularSpeed(angularSpeed);
            this.averageAngularSpeed = Normalisation.convertAngularSpeed(averageAngularSpeed);
        }else{
            this.linearSpeed = linearSpeed;
            this.averageLinearSpeed = averageLinearSpeed;
            this.angularSpeed = angularSpeed;
            this.averageAngularSpeed = averageAngularSpeed;

        }

        if(ReadConfig.tryNNclassifier) inputSize = 4;
    }


    /**
     * Getter for the linearSpeed variable
     * @return Double number
     */
    public double getLinearSpeed() {
        return this.linearSpeed;
    }

    /**
     * Getter for the angularSpeed variable
     * @return Double variable
     */
    public double getAngularSpeed() {
        return this.angularSpeed;
    }


    /**
     * @implNote Implementation from Interface
     * @return list containing all the fields
     */
    @Override
    public INDArray serialise(){
        if(inputSize == 2) {
            INDArray array = Nd4j.zeros(2);
            array.putScalar(0, this.linearSpeed);
            array.putScalar(1, this.angularSpeed);
            return array;
        }else{
            INDArray array = Nd4j.zeros(4);
            array.putScalar(0, this.linearSpeed);
            array.putScalar(1, this.angularSpeed);
            array.putScalar(2, this.averageLinearSpeed);
            array.putScalar(3, this.averageAngularSpeed);
            return array;
        }
    }

    @Override
    public InputsNetwork deepCopy() {
        return new InputNetwork(this.linearSpeed, this.angularSpeed, this.averageLinearSpeed, this.averageAngularSpeed, false);
    }

    /**
     * Override to string method
     * @return the string version of the data
     */
    @Override
    public String toString() {
        return "InputNetwork{" + " " +
                "linearSpeed=" + this.linearSpeed + ", " +
                "angularSpeed=" + this.angularSpeed + " " +
                '}';
    }

    public Double getAverageLinearSpeed() {
        return averageLinearSpeed;
    }

    public Double getAverageAngularSpeed() {
        return averageAngularSpeed;
    }
}

package tgcfs.InputOutput;

import tgcfs.Agents.InputNetwork;
import tgcfs.NN.InputsNetwork;

/**
 * Created by Alessandro Zonta on 01/06/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 *
 * Class that helps the normalisation of the input
 * Max and minimum hardcoded per classes
 *
 */
public class Normalisation {

    /**
     * Normalisation of the number
     * @param network class that needs the element normalised
     * @param data element to normalise
     * @return element normalised
     */
    public static double convertData(InputsNetwork network, double data){
        if(network.getClass().equals(InputNetwork.class)){
            return convertAgents(data);
        }
        return convertClassifier(data);

    }

    /**
     * Normalise value for the agent class
     * @param data element to normalise
     * @return element normalised
     */
    private static double convertAgents(double data){
        double maxAgents = 100.0;
        double minAgents = -100.0;
        return 2 * ((data - minAgents) / (maxAgents - minAgents)) - 1;
    }

    /**
     * Normalise value for the classifier class
     * @param data element to normalise
     * @return element normalised
     */
    private static double convertClassifier(double data){
        double maxClassifier = 100.0;
        double minClassifier = -100.0;
        return 2 * ((data - minClassifier) / (maxClassifier - minClassifier)) - 1;
    }


    /**
     * Normalise value for the direction/attraction data.
     * Since It is an angle the maximum and minimum values possible are ±180 degrees
     * @param toBeConverted angle that needs to be normalised
     * @return normalised angle between ±1
     */
    public static double convertDirectionData(double toBeConverted) {
        double maxAngle = 180.0;
        double minAngle = -180.0;
        if (toBeConverted > maxAngle || toBeConverted < minAngle) {
            throw new Error("Wrong range in input");
        }
        return 2 * ((toBeConverted - minAngle) / (maxAngle - minAngle)) - 1;
    }


    /**
     * Normalise value for angular speed
     * @param toBeConverted speed that needs to be normalised
     * @return normalised speed between ±1
     */
    public static double convertAngularSpeed(double toBeConverted){
        double maxAngularSpeed = 300.0;
        double minAngularSpeed = -10.0;
        if (toBeConverted > maxAngularSpeed) {
            toBeConverted = maxAngularSpeed;
        }
        if (toBeConverted < minAngularSpeed) {
            toBeConverted = minAngularSpeed;
        }
        return 2 * ((toBeConverted - minAngularSpeed) / (maxAngularSpeed - minAngularSpeed)) - 1;
    }


    /**
     * Denormalise the angular speed
     * @param toBeConverted  speed that needs to be denormalised
     * @return original speed
     */
    public static double decodeAngularSpeed(double toBeConverted){
        double maxAngularSpeed = 1.0;
        double minAngularSpeed = -1.0;
        double b = 10.0;
        double a = -10.0;
        return convertToSomething(maxAngularSpeed, minAngularSpeed, b, a, toBeConverted);
    }

    /**
     * Normalise value for the speed value.
     * Maximum speed available is ~200km/h
     * If the speed is faster than the maximum allowed the agent get a fine.
     * Kidding.
     * The value is set to the maximum allowed
     * If the speed is lower than zero, something is wrong or the physics has to be rewritten.
     * In this case th espeed is set to 0
     * @param toBeConverted speed that needs to be normalised
     * @return normalised speed between ±1
     */
    public static double convertSpeed(double toBeConverted) {
        double maxSpeed = 10.0; //55.5 metres/seconds means maximum speed of 200 km/h
        double minSpeed = 0.0;
        if (toBeConverted > maxSpeed) {
            toBeConverted = maxSpeed;
        }
        if (toBeConverted < minSpeed) {
            toBeConverted = minSpeed;
        }
        return 2 * ((toBeConverted - minSpeed) / (maxSpeed - minSpeed)) - 1;
    }

    /**
     * Recover value speed from normalised value
     * @param toBeConverted speed that has to be de normalised
     * @return double real value
     */
    public static double decodeSpeed(double toBeConverted){
        double maxSpeed = 1.0;
        double minSpeed = -1.0;
        double b = 10.0;
        double a = 0.0;
        return convertToSomething(maxSpeed, minSpeed, b, a, toBeConverted);
    }

    /**
     * Recover direction data from normalised value
     * @param toBeConverted direction data that has to be de - normalised
     * @return double real value
     */
    public static double decodeDirectionData(double toBeConverted){
        double maxAngle = 1.0;
        double minAngle = -1.0;
        double b = 180.0;
        double a = -180.0;
        return convertToSomething(maxAngle, minAngle, b, a, toBeConverted);
    }

    /**
     * Convert from ±180 angle to 0->360 angle
     * @param toBeConverted angle that has to be converted
     * @return converted angle
     */
    public static double fromHalfPItoTotalPI(double toBeConverted){
        if(toBeConverted >= 0 && toBeConverted<=180){
            return toBeConverted;
        }else{
            return 360 + toBeConverted;
        }
    }

    /**
     * Convert from 0->360 angle to ±180 angle
     * @param toBeConverted angle that has to be converted
     * @return converted angle
     */
    public static double fromTotalPItoHalfPI(double toBeConverted){
        if(toBeConverted<=180){
            return toBeConverted;
        }else{
            return toBeConverted - 360;
        }
    }


    /**
     * Recover Distance from normalised distance
     * Need to define a maximum distance
     * Maximum speed is 55.5m/s
     * Let's define a maximum transfer of 10 seconds
     * This means 555 metres maximum
     * @param toBeConverted distance that has to be de normalised
     * @return double real value
     */
    public static double decodeDistance(double toBeConverted){
        double maxSpeed = 1.0;
        double minSpeed = -1.0;
        double b = 555.0;
        double a = 0.0;
        return convertToSomething(maxSpeed, minSpeed, b, a, toBeConverted);
    }

    /**
     * Convert Distance
     * Need to define a maximum distance
     * Maximum speed is 55.5m/s
     * Let's define a maximum transfer of 10 seconds
     * This means 555 metres maximum
     * @param toBeConverted distance that has to be de normalised
     * @return double real value
     */
    public static double convertDistance(double toBeConverted){
        double maxSpeed = 555.0;
        double minSpeed = 0.0;
        double b = 1.0;
        double a = -1.0;
        return convertToSomething(maxSpeed, minSpeed, b, a, toBeConverted);
    }

    /**
     * Normalise a number to a new range
     * @param maxStart max of the range of the original value
     * @param minStart min of the range of the original value
     * @param maxEnd max of the range of the converted value
     * @param minEnd min of the range of the converted value
     * @param value value to convert
     * @return converted value
     */
    public static double convertToSomething(double maxStart, double minStart, double maxEnd, double minEnd, double value){
        if(value > maxStart) value = maxStart;
        if(value < minStart) value = minStart;
        return  (maxEnd - minEnd) * ((value - minStart) / (maxStart - minStart)) + minEnd;
    }

}

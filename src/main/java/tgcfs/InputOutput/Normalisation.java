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
    private static double max_speed = 3.0; //metres per second
    private static double min_speed = 0.0;
    private static double max_angular_speed = 500.0; //radiant per second
    private static double min_angular_speed = -500.0;
    private static double max_distance = 555.0;
    private static double min_distance = 0.0;

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
        double maxAngle = 360.0;
        double minAngle = 0.0;
        if (toBeConverted > maxAngle || toBeConverted < minAngle) {
            throw new Error("Wrong range in input");
        }

        return convertToSomething(maxAngle, minAngle, 1.0,-1.0, toBeConverted);
    }


    /**
     * Normalise value for angular speed
     * @param toBeConverted speed that needs to be normalised
     * @return normalised speed between ±1
     */
    public static double convertAngularSpeed(double toBeConverted){
        return convertToSomething(max_angular_speed, min_angular_speed, 1.0,-1.0, toBeConverted);
    }


    /**
     * Denormalise the angular speed
     * @param toBeConverted  speed that needs to be denormalised
     * @return original speed
     */
    public static double decodeAngularSpeed(double toBeConverted){
        return convertToSomething(1.0, -1.0, max_angular_speed, min_angular_speed, toBeConverted);
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
        return convertToSomething(max_speed, min_speed, 1.0,-1.0, toBeConverted);
    }

    /**
     * Recover value speed from normalised value
     * @param toBeConverted speed that has to be de normalised
     * @return double real value
     */
    public static double decodeSpeed(double toBeConverted){
        return convertToSomething(1.0, -1.0, max_speed, min_speed, toBeConverted);
    }

    /**
     * Recover direction data from normalised value
     * @param toBeConverted direction data that has to be de - normalised
     * @return double real value
     */
    public static double decodeDirectionData(double toBeConverted){
        return convertToSomething(1.0, -1.0, 360.0,0.0, toBeConverted);
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
        return convertToSomething(1.0, -1.0, max_distance, min_distance, toBeConverted);
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
        return convertToSomething(max_distance, min_distance, 1.0, -1.0, toBeConverted);
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


    /**
     * Convert direction data into bearing data
     * @param angle direction
     * @return double value with bearing
     */
    public static double fromDirectionToBearing(double angle){
        if(0 <= angle && angle <= 90){
            return 90 - angle;
        }else if(0 > angle && angle >= -180){
            return 90 + (-angle);
        }else if(90 < angle && angle <= 180){
            return 360 - (angle - 90);
        }
        return angle;
    }

}

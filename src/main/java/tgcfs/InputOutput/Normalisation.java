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
    public static Double convertData(InputsNetwork network, Double data){
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
    private static Double convertAgents(Double data){
        Double maxAgents = 100.0;
        Double minAgents = -100.0;
        return 2 * ((data - minAgents) / (maxAgents - minAgents)) - 1;
    }

    /**
     * Normalise value for the classifier class
     * @param data element to normalise
     * @return element normalised
     */
    private static Double convertClassifier(Double data){
        Double maxClassifier = 100.0;
        Double minClassifier = -100.0;
        return 2 * ((data - minClassifier) / (maxClassifier - minClassifier)) - 1;
    }


    /**
     * Normalise value for the direction/attraction data.
     * Since It is an angle the maximum and minimum values possible are ±180 degrees
     * @param toBeConverted angle that needs to be normalised
     * @return normalised angle between ±1
     */
    public static Double convertDirectionData(Double toBeConverted) {
        Double maxAngle = 180.0;
        Double minAngle = -180.0;
        if (toBeConverted > maxAngle || toBeConverted < minAngle) throw new Error("Wrong range in input");
        return 2 * ((toBeConverted - minAngle) / (maxAngle - minAngle)) - 1;
    }


    /**
     * Normalise value for the speed value.
     * Maximum speed available is ~200km/h
     * If the speed is faster than the maximum allowed the agent get a fined.
     * Kidding.
     * The value is set to the maximum allowed
     * If the speed is lower than zero, something is wrong or the physics has to be rewritten.
     * In this case th espeed is set to 0
     * @param toBeConverted speed that needs to be normalised
     * @return normalised speed between ±1
     */
    public static Double convertSpeed(Double toBeConverted) {
        Double maxSpeed = 55.5; //55.5 metres/seconds means maximum speed of 200 km/h
        Double minSpeed = 0.0;
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
    public static Double decodeSpeed(Double toBeConverted){
        Double maxSpeed = 1.0;
        Double minSpeed = -1.0;
        Double b = 55.5;
        Double a = 0.0;
        return (b - a) * ((toBeConverted - minSpeed) / (maxSpeed - minSpeed)) + a;
    }

    /**
     * Recover direction data from normalised value
     * @param toBeConverted direction data that has to be de - normalised
     * @return double real value
     */
    public static Double decodeDirectionData(Double toBeConverted){
        Double maxAngle = 1.0;
        Double minAngle = -1.0;
        Double b = 180.0;
        Double a = -180.0;
        return (b - a) * ((toBeConverted - minAngle) / (maxAngle - minAngle)) + a;
    }

    /**
     * Convert from ±180 angle to 0->360 angle
     * @param toBeConverted angle that has to be converted
     * @return converted angle
     */
    public static Double fromHalfPItoTotalPI(Double toBeConverted){
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
    public static Double fromTotalPItoHalfPI(Double toBeConverted){
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
    public static Double decodeDistance(Double toBeConverted){
        Double maxSpeed = 1.0;
        Double minSpeed = -1.0;
        Double b = 555.0;
        Double a = 0.0;
        return (b - a) * ((toBeConverted - minSpeed) / (maxSpeed - minSpeed)) + a;
    }

}

package tgcfs.InputOutput;

import lgds.trajectories.Point;
import tgcfs.Agents.OutputNetwork;
import tgcfs.Agents.OutputNetworkTime;
import tgcfs.Classifiers.InputNetwork;
import tgcfs.Config.ReadConfig;
import tgcfs.Loader.Feeder;
import tgcfs.Loader.TrainReal;
import tgcfs.NN.InputsNetwork;
import tgcfs.NN.OutputsNetwork;
import tgcfs.Utils.PointWithBearing;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Alessandro Zonta on 02/06/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 *
 * Implementation of the mapping process
 * From the output of the agents the indications need to be transformed into the input of the second
 * The output has to be translated into a point in the graph to compute the real bearing
 */
public class FollowingTheGraph implements Transformation {
    private PointWithBearing lastPoint; //last point of the trajectory
    private Feeder feeder; //system that will translate from output to new input using graph config
    protected static Logger logger;


    /**
     * Constructor zero parameter
     * Set to null everything
     */
    public FollowingTheGraph(){
        this.lastPoint = null;
        this.feeder = null;
    }

    /**
     * Constructor one parameter
     * @param log log
     */
    public FollowingTheGraph(Logger log){
        this.lastPoint = null;
        this.feeder = null;
        logger = log;
    }

    /**
     * Constructor one parameter
     * @param feeder feeder object
     * @param log log
     */
    public FollowingTheGraph(Feeder feeder, Logger log){
        this.feeder = feeder;
        this.lastPoint = null;
        logger = log;
    }

    /**
     * Constructor two parameters
     * @param feeder feeder object
     * @param lastPoint last point object
     */
    public FollowingTheGraph(Feeder feeder, PointWithBearing lastPoint){
        this.feeder = feeder;
        this.lastPoint = lastPoint;
    }

    /**
     * @implNote Implementation from Abstract class Algorithm
     * Every points in the output has to be located in the real word routing system
     * Using the graph I will find next position and with that position I will compute real bearing and speed
     * The method throws two errors. If the graph or the last point are not instantiate, the error is raised.
     * @param trainReal all the data I need to transform and output the data
     * @return the input of the new network (classifier)
     */
    @Override
    public List<InputsNetwork> transform(TrainReal trainReal) {
        if (this.feeder == null) throw new NullPointerException("System with the graph not instantiate");
        if (this.lastPoint == null) throw new NullPointerException("Last Point not instantiate");

        List<OutputsNetwork> outputs = trainReal.getOutputComputed();

        List<InputsNetwork> convertedInput = new ArrayList<>();
        List<InputsNetwork> convertedInputReal = new ArrayList<>();

        PointToSpeedSpeed convertToAgularSpeed = new PointToSpeedSpeed();

        //If I am also checking the first part I am adding that to the result to compute
        try {
            if(ReadConfig.Configurations.getCheckAlsoPast()){
                convertedInput.addAll(trainReal.getTrainingPointSettedForTheClassifier());
                convertedInputReal.addAll(trainReal.getTrainingPointSettedForTheClassifier());
            }
        } catch (Exception ignored) {}

        //remember the last point
        PointWithBearing lastp = this.lastPoint.deepCopy();

        boolean time = false;
        try {
            time= ReadConfig.Configurations.getTimeAsInput();
        } catch (Exception ignored) { }

        int i = 0;
        //this is for the fake part
        for(OutputsNetwork outputsNetwork: outputs){

            //Point position = this.feeder.getNextLocation(this.lastPoint, output.getSpeed(), output.getDistance(), output.getBearing());
            Point position = null;
            try {
                if(ReadConfig.Configurations.getConversionWithGraph()){
                    if(!time){
                        OutputNetwork output = (OutputNetwork) outputsNetwork;
                        position = this.feeder.getNextLocationDifferentMethod(this.lastPoint, output.getSpeed(), output.getBearing());
                    }else{
                        OutputNetworkTime output = (OutputNetworkTime) outputsNetwork;
                        throw new Exception("Conversion following the graph and using the time is not yet implemented");
                    }
                }else{
                    if(!time){
                        OutputNetwork output = (OutputNetwork) outputsNetwork;
                        position = this.feeder.getNextLocationNoGraph(this.lastPoint, output.getSpeed(), output.getBearing());
                    }else{
                        OutputNetwork output = (OutputNetwork) outputsNetwork;
                        //OutputNetworkTime output = (OutputNetworkTime) outputsNetwork;
                        position = this.feeder.getNextLocationNoGraph(this.lastPoint, output.getSpeed(), output.getBearing(), trainReal.getLastTime());
                    }
                }
            } catch (Exception e) {
                 logger.log(Level.INFO, " -> " + e.getMessage() );
            }
            if(ReadConfig.debug) logger.log(Level.INFO,outputsNetwork.toString() + " -> " + position );
            //this input network has speed and bearing
            //InputNetwork inputNetwork = new InputNetwork(converterPointSB.obtainSpeed(this.lastPoint, position), converterPointSB.obtainBearing(this.lastPoint, position));
            //the new one has velocity and angular speed


//            InputNetwork inputNetwork = new InputNetwork(converterPointSB.obtainSpeed(this.lastPoint, position), convertToAgularSpeed.obtainAngularSpeed(this.lastPoint, converterPointSB.obtainBearing(this.lastPoint, position)));
            double angularSpeed;
            if(!time){
                angularSpeed = convertToAgularSpeed.obtainAngularSpeed(this.lastPoint, outputsNetwork.getBearing());
            }else {
//                OutputNetworkTime output = (OutputNetworkTime) outputsNetwork;
                OutputNetwork output = (OutputNetwork) outputsNetwork;
                angularSpeed = convertToAgularSpeed.obtainAngularSpeedTime(this.lastPoint, output.getBearing(), trainReal.getLastTime());
            }
            InputNetwork inputNetwork = new InputNetwork(outputsNetwork.getSpeed(), angularSpeed);
            convertedInput.add(inputNetwork);

            //upgrade position
            this.lastPoint = new PointWithBearing(position);
            i++;
        }
        //save the entire trajectory for future works
        trainReal.setAllThePartTransformedFake(convertedInput);
        if(ReadConfig.debug) logger.log(Level.INFO, "fake ->" + trainReal.getAllThePartTransformedFake().get(trainReal.getAllThePartTransformedFake().size() -1).toString());


        //this is for the real part
        this.lastPoint = lastp;
        List<OutputsNetwork> out = trainReal.getRealOutput();

        for(int j = 0; j<i; j++){
            OutputsNetwork output = out.get(j);
            Point position;
            if(!time) {
                //Point position = this.feeder.getNextLocation(this.lastPoint, output.getSpeed(), output.getDistance(), output.getBearing());
                position = this.feeder.getNextLocationNoGraph(this.lastPoint, output.getSpeed(), output.getBearing());
//            InputNetwork inputNetwork = new InputNetwork(converterPointSB.obtainSpeed(this.lastPoint, position), convertToAgularSpeed.obtainAngularSpeed(this.lastPoint, converterPointSB.obtainBearing(this.lastPoint, position)));
                InputNetwork inputNetwork = new InputNetwork(output.getSpeed(), convertToAgularSpeed.obtainAngularSpeed(this.lastPoint, output.getBearing()));

                convertedInputReal.add(inputNetwork);
            }else{
//                OutputNetworkTime outputTime = (OutputNetworkTime) output;
                OutputNetwork outputTime = (OutputNetwork) output;
                position = this.feeder.getNextLocationNoGraph(this.lastPoint, outputTime.getSpeed(), outputTime.getBearing(), trainReal.getLastTime());
                InputNetwork inputNetwork = new InputNetwork(output.getSpeed(), convertToAgularSpeed.obtainAngularSpeedTime(this.lastPoint, output.getBearing(), trainReal.getLastTime()));

                convertedInputReal.add(inputNetwork);
            }
            //upgrade position
            this.lastPoint = new PointWithBearing(position);
        }
        trainReal.setAllThePartTransformedReal(convertedInputReal);

        return convertedInput;
    }

    /**
     * Setter for the last point needed for the transformation process
     * @param lastPoint Point
     */
    public void setLastPoint(PointWithBearing lastPoint) {
        this.lastPoint = lastPoint;
    }

    /**
     * Setter for the feeder system
     * @param feeder feeder object
     */
    public void setFeeder(Feeder feeder) {
        this.feeder = feeder;
    }


    /**
     * Method that converts the output of the network to a real world point
     * @param outputsNetwork output network to convert
     * @return point in the real world corresponding to the displacement
     */
    public Point singlePointConversion(OutputsNetwork outputsNetwork){
        if (this.feeder == null) throw new NullPointerException("System with the graph not instantiate");
        if (this.lastPoint == null) throw new NullPointerException("Last Point not instantiate");
        if(outputsNetwork.getClass().equals(OutputNetwork.class)) {
            OutputNetwork output = (OutputNetwork) outputsNetwork;
            return this.feeder.getNextLocationNoGraph(this.lastPoint, output.getSpeed(), output.getBearing());
        }else{
            OutputNetworkTime output = (OutputNetworkTime) outputsNetwork;
            return this.feeder.getNextLocationNoGraph(this.lastPoint, output.getSpeed(), output.getBearing(), output.getTime());
        }
//        return this.feeder.getNextLocation(this.lastPoint, output.getSpeed(), output.getDistance(), output.getBearing());
    }
}

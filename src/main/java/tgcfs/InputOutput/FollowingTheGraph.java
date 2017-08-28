package tgcfs.InputOutput;

import lgds.trajectories.Point;
import tgcfs.Agents.OutputNetwork;
import tgcfs.Classifiers.InputNetwork;
import tgcfs.Loader.Feeder;
import tgcfs.NN.InputsNetwork;
import tgcfs.NN.OutputsNetwork;

import java.util.ArrayList;
import java.util.List;

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
    private Point lastPoint; //last point of the trajectory
    private Feeder feeder; //system that will translate from output to new input using graph config

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
     * @param feeder feeder object
     */
    public FollowingTheGraph(Feeder feeder){
        this.feeder = feeder;
    }

    /**
     * Constructor two parameters
     * @param feeder feeder object
     * @param lastPoint last point object
     */
    public FollowingTheGraph(Feeder feeder, Point lastPoint){
        this.feeder = feeder;
        this.lastPoint = lastPoint;
    }

    /**
     * @implNote Implementation from Abstract class Algorithm
     * Every points in the output has to be located in the real word routing system
     * Using the graph I will find next position and with that position I will compute real bearing and speed
     * The method throws two errors. If the graph or the last point are not instantiate, the error is raised.
     * @param outputs data that we want to transform into input data.
     * @return the input of the new network (classifier)
     */
    @Override
    public List<InputsNetwork> transform(List<OutputsNetwork> outputs) {
        if (this.feeder == null) throw new NullPointerException("System with the graph not instantiate");
        if (this.lastPoint == null) throw new NullPointerException("Last Point not instantiate");

        List<InputsNetwork> convertedInput = new ArrayList<>();
        PointToSpeedBearing converterPointSB = new PointToSpeedBearing();

        outputs.forEach(outputsNetwork -> {
            OutputNetwork output = (OutputNetwork) outputsNetwork;
            Point position = this.feeder.getNextLocation(this.lastPoint, output.getSpeed(), output.getDistance(), output.getBearing());
            InputNetwork inputNetwork = new InputNetwork(converterPointSB.obtainSpeed(this.lastPoint, position), converterPointSB.obtainBearing(this.lastPoint, position));
//            System.out.println(inputNetwork.toString());
            convertedInput.add(inputNetwork);

            //upgrade position
            this.lastPoint = position;
        });

        return convertedInput;
    }

    /**
     * Setter for the last point needed for the transformation process
     * @param lastPoint Point
     */
    public void setLastPoint(Point lastPoint) {
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
        OutputNetwork output = (OutputNetwork) outputsNetwork;
        return this.feeder.getNextLocation(this.lastPoint, output.getSpeed(), output.getDistance(), output.getBearing());
    }
}

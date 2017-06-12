package tgcfs.Agents;

import lgds.Distance.Distance;
import lgds.trajectories.Point;
import tgcfs.InputOutput.PointToSpeedBearing;
import tgcfs.NN.Models;
import tgcfs.NN.OutputsNetwork;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Created by Alessandro Zonta on 17/05/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 *
 * Class representing the real agent in the system
 */
public class Agent extends Models {
    private List<Point> realOutput;

    /**
     * Constructor that calls the father class constructor
     */
    public Agent(){
        super();
    }


    /**
     * Setter for the Real Output
     * @param realOutput real output for the test
     */
    public void setRealOutput(List<Point> realOutput) {
        this.realOutput = realOutput;
    }

    /**
     * Obtain the output from the agent that has the real movement of the person
     * @return list of outputNetwork
     */
    public List<OutputsNetwork> realOutput(){
        //class that compute the conversion point -> speed/bearing
        PointToSpeedBearing convertitor = new PointToSpeedBearing();
        List<OutputsNetwork> totalList = new ArrayList<>();
        Distance distance = new Distance();

        IntStream.range(0, this.realOutput.size() - 1).forEach(i -> {
            //bearing from this point to next point
            Point actualPoint = this.realOutput.get(i);
            Point nextPoint = this.realOutput.get(i+1);
            Double bearing = convertitor.obtainBearing(actualPoint,nextPoint);
            //speed is the speed I arrived here from previous point
            Double speed;
            Double dist;
            if(i > 0){
                Point previousPoint = this.realOutput.get(i - 1);
                speed = convertitor.obtainSpeed(previousPoint, actualPoint);

                dist = distance.compute(previousPoint,actualPoint);
            }else{
                speed = 0.0;
                dist = 0.0;
            }
            //compute the distance

            totalList.add(new OutputNetwork(speed, bearing, dist));
        });
        return totalList;
    }

    /**
     * Return last point of the real trajectory
     * @return point object
     */
    public Point getLastPoint(){
        return this.realOutput.get(this.realOutput.size() - 1);
    }
}

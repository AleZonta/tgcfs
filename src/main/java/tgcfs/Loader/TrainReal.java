package tgcfs.Loader;

import lgds.trajectories.Point;
import tgcfs.NN.InputsNetwork;
import tgcfs.NN.OutputsNetwork;

import java.util.List;

/**
 * Created by Alessandro Zonta on 30/06/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 *
 * This class will represent the match between part of the trajectory used as a train and the part of the trajectory used as a real part
 */
public class TrainReal {
    private List<InputsNetwork> trainingPoint;
    private List<Point> firstPart;
    private List<Point>  followingPart;
    private List<OutputsNetwork> outputComputed;

    /**
     * Constructor with two parameters
     * @param trainingPoint list of inputNetwork
     * @param followingPart list of points
     */
    public TrainReal(List<InputsNetwork> trainingPoint, List<Point>  followingPart){
        this.trainingPoint = trainingPoint;
        this.followingPart = followingPart;
    }

    /**
     * Getter for the input part of the trajectory
     * @return list of inputNetworks
     */
    public List<InputsNetwork> getTrainingPoint() {
        return this.trainingPoint;
    }

    /**
     * Getter for the real part after the training part
     * @return list of points
     */
    public List<Point> getFollowingPart() {
        return this.followingPart;
    }

    /**
     * Getter for output computed by the network
     * @return List of OutputNetworks
     */
    public List<OutputsNetwork> getOutputComputed() {
        return this.outputComputed;
    }

    /**
     * Setter for the output computed by the NN
     * @param outputComputed output computed by the NN
     */
    public void setOutputComputed(List<OutputsNetwork> outputComputed) {
        this.outputComputed = outputComputed;
    }

    /**
     * Getter for last point of source
     * @return Position
     */
    public Point getLastPoint() {
        return this.firstPart.get(this.firstPart.size() - 1);
    }

    /**
     * Getter for the points on the first part of trajectories
     * @return Position
     */
    public List<Point> getPoints() {
        return this.firstPart;
    }

    /**
     * Setter for first part of the source
     * @param firstPart point
     */
    public void setPoints(List<Point> firstPart) {
        this.firstPart = firstPart;
    }
}

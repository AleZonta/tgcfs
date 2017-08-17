package tgcfs.Loader;

import lgds.trajectories.Point;
import tgcfs.NN.InputsNetwork;
import tgcfs.NN.OutputsNetwork;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private final List<InputsNetwork> trainingPoint;
    private final List<String> trainingImage;
    private List<Point> firstPart;
    private final List<Point>  followingPart;
    private final String conditionalImage;
    private List<OutputsNetwork> outputComputed;

    /**
     * Constructor with two parameters
     * @param trainingPoint list of inputNetwork
     * @param followingPart list of points
     */
    public TrainReal(List<InputsNetwork> trainingPoint, List<Point>  followingPart){
        this.trainingPoint = trainingPoint;
        this.followingPart = followingPart;
        this.trainingImage = null;
        this.firstPart = null;
        this.conditionalImage = null;
    }

    /**
     * Constructor with two parameters
     * @param trainingPoint list of inputNetwork
     * @param followingPart list of points
     * @param trainingImage path of the training images
     */
    public TrainReal(List<InputsNetwork> trainingPoint, List<Point>  followingPart, List<String> trainingImage, String conditionalImage){
        this.trainingPoint = trainingPoint;
        this.followingPart = followingPart;
        this.trainingImage = trainingImage;
        this.firstPart = null;
        this.conditionalImage = conditionalImage;
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
        Point p = this.firstPart.get(this.firstPart.size() - 1);
        Point ret;
        if(p.getTime() == null){
            ret = new Point(p.getLatitude(), p.getLongitude(), 0.0, 0d, LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")), LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        }else{
            ret = new Point(p.getLatitude(), p.getLongitude(), p.getAltitude(), p.getDated(), p.getTime(), p.getDates());
        }
        return ret;
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

    /**
     * Getter for the list of the training images
     * @return list of string containing the path
     */
    public List<String> getTrainingImage() {
        return trainingImage;
    }

    /**
     * Getter fot the conditional image
     * @return
     */
    public String getConditionalImage() {
        return this.conditionalImage;
    }
}

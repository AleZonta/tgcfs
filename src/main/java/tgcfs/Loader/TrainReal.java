package tgcfs.Loader;

import lgds.Distance.Distance;
import lgds.trajectories.Point;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import tgcfs.Agents.OutputNetwork;
import tgcfs.Idsa.IdsaLoader;
import tgcfs.InputOutput.PointToSpeedBearing;
import tgcfs.NN.InputsNetwork;
import tgcfs.NN.OutputsNetwork;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

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
    private List<Point> firstPart;
    private final List<Point>  followingPart;
    private List<InputsNetwork> followingPartTransformed;
    private List<OutputsNetwork> realOutput;
    private List<InputsNetwork> allThePartTransformedFake;
    private List<InputsNetwork> allThePartTransformedReal;
    private final String conditionalImage;
    private final String normalImage;
    private List<OutputsNetwork> outputComputed;
    private List<Point> realPointsOutputComputed;
    private IdsaLoader idsaLoader;
    private List<Point> totalPoints;

    /**
     * Constructor with two parameters
     * @param trainingPoint list of inputNetwork
     * @param followingPart list of points
     */
    public TrainReal(List<InputsNetwork> trainingPoint, List<Point> followingPart){
        this.trainingPoint = trainingPoint;
        this.followingPart = followingPart;
        this.firstPart = null;
        //the conditional image path is hardcoded -> has to be in the same directory of the program
        this.conditionalImage = null;
        this.normalImage = null;
        this.idsaLoader = null;
        this.outputComputed = null;
        this.realPointsOutputComputed = null;
        this.totalPoints = null;
        this.followingPartTransformed = null;
        this.allThePartTransformedFake = null;
        this.allThePartTransformedReal = null;
        this.realOutput = null;
    }

    /**
     * Constructor with four parameters
     * @param trainingPoint list of inputNetwork
     * @param followingPart list of points
     * @param conditionalImage path of the conditional image
     */
    public TrainReal(List<InputsNetwork> trainingPoint, List<Point>  followingPart, String conditionalImage, String normalImage){
        this.trainingPoint = trainingPoint;
        this.followingPart = followingPart;
        this.firstPart = null;
        this.conditionalImage = conditionalImage;
        this.normalImage = normalImage;
        this.idsaLoader = null;
        this.outputComputed = null;
        this.realPointsOutputComputed = null;
        this.totalPoints = null;
        this.followingPartTransformed = null;
        this.allThePartTransformedFake = null;
        this.allThePartTransformedReal = null;
        this.realOutput = null;

    }


    /**
     * Constructor with three parameter
     * @param trainingPoint the training point
     * @param followingPart the following part
     * @param idsaLoader idsa loader reference
     */
    public TrainReal(List<InputsNetwork> trainingPoint, List<Point>  followingPart, IdsaLoader idsaLoader){
        this.trainingPoint = trainingPoint;
        this.followingPart = followingPart;
        this.firstPart = null;
        this.conditionalImage = Paths.get(".").toAbsolutePath().normalize().toString() + "/cond.png";
        this.normalImage = Paths.get(".").toAbsolutePath().normalize().toString() + "/image.png";
        this.idsaLoader = idsaLoader;
        this.outputComputed = null;
        this.realPointsOutputComputed = null;
        this.totalPoints = null;
        this.followingPartTransformed = null;
        this.allThePartTransformedFake = null;
        this.allThePartTransformedReal = null;
        this.realOutput = null;

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
     * Getter fot the conditional image
     * @return String address conditional image
     */
    public String getConditionalImage() {
        return this.conditionalImage;
    }

    /**
     * Getter for the idsa loader reference
     * @return idsa loader reference {@link IdsaLoader}
     */
    public IdsaLoader getIdsaLoader() {
        return idsaLoader;
    }

    /**
     * setter for the idsaloader reference
     * @param idsaLoader the idsa loader reference
     */
    public void setIdsaLoader(IdsaLoader idsaLoader) {
        this.idsaLoader = idsaLoader;
    }

    /**
     * Getter for the location of the normal image
     * @return string value with the path
     */
    public String getNormalImage() {
        return this.normalImage;
    }

    /**
     * Getter fot the real point output computed
     * @return List<Point>
     */
    public List<Point> getRealPointsOutputComputed() {
        return this.realPointsOutputComputed;
    }

    /**
     * Setter fot the list of the real output computed
     * @param realPointsOutputComputed List<Point>
     */
    public void setRealPointsOutputComputed(List<Point> realPointsOutputComputed) {
        this.realPointsOutputComputed = realPointsOutputComputed;
    }

    /**
     * Return the total list of points present in this object
     * if it is already computed return that one, otherwise compute it and save it
     * @param real if real True take the real point, if False take the generated one
     * @return List {@link Point} objects
     */
    public List<Point> getTotalList(Boolean real){
        if(this.totalPoints == null) {
            this.totalPoints = new ArrayList<>();
            this.totalPoints.addAll(this.firstPart);
            //I want the original list
            if (real) {
                //check size real output computed
                int size = this.realPointsOutputComputed.size();
                IntStream.range(0, size).forEach(i -> this.totalPoints.add(this.followingPart.get(i)));
            } else {
                this.totalPoints.addAll(this.realPointsOutputComputed);
            }
        }
        return this.totalPoints;
    }

    /**
     * Get number of features from
     * @return
     */
    public int getSingleInputSize(){
        return this.trainingPoint.get(0).serialise().columns();
    }

    /**
     * Get the following part transformed into {@link InputsNetwork}
     * @return list of {@link InputsNetwork}
     */
    public List<InputsNetwork> getFollowingPartTransformed() {
        //return number of outputcomputed position
        throw new NotImplementedException();
       // return this.followingPartTransformed;
    }

    /**
     * Getter for all the trajectory transformed (The fake trajectory)
     * @return List of {@link InputsNetwork}
     */
    public List<InputsNetwork> getAllThePartTransformedFake() {
        return this.allThePartTransformedFake;
    }

    /**
     * Setter for the trajectory transformed  (The fake trajectory)
     * @param allThePartTransformedFake List of {@link InputsNetwork}
     */
    public void setAllThePartTransformedFake(List<InputsNetwork> allThePartTransformedFake) {
        this.allThePartTransformedFake = allThePartTransformedFake;
    }

    /**
     * Getter for all the trajectory transformed (The Real trajectory)
     * @return List of {@link InputsNetwork}
     */
    public List<InputsNetwork> getAllThePartTransformedReal() {
        return this.allThePartTransformedReal;
    }

    /**
     * Setter for the trajectory transformed  (The real trajectory)
     * @param allThePartTransformedFake List of {@link InputsNetwork}
     */
    public void setAllThePartTransformedReal(List<InputsNetwork> allThePartTransformedFake) {
        this.allThePartTransformedReal = allThePartTransformedFake;
    }

    /**
     * Getter for the real output
     * @return list {@link OutputNetwork}
     */
    public List<OutputsNetwork> getRealOutput() {
        if(this.realOutput == null) throw new NullPointerException("Need to compute the real output before using it");
        return this.realOutput;
    }

    /**
     * Transform the point given by output into an outputnetwork
     */
    public void createOutput(){
        //class that compute the conversion point -> speed/bearing
        PointToSpeedBearing convertitor = new PointToSpeedBearing();
        List<OutputsNetwork> totalList = new ArrayList<>();
        Distance distance = new Distance();

        //add the last point to the end to enable the computation of the output
        List<Point> herePoint = new ArrayList<>();
        herePoint.add(this.firstPart.get(this.firstPart.size() - 1));
        herePoint.addAll(this.followingPart);

        IntStream.range(0, herePoint.size() - 1).forEach(i -> {
            //bearing from this point to next point
            Point actualPoint = herePoint.get(i);
            Point nextPoint = herePoint.get(i+1);
            Double bearing = convertitor.obtainBearing(actualPoint,nextPoint);
            //speed is the speed I arrived here from previous point
            Double speed;
            Double dist;
            if(i > 0){
                Point previousPoint = herePoint.get(i - 1);
                speed = convertitor.obtainSpeed(previousPoint, actualPoint);

                dist = distance.compute(previousPoint,actualPoint);
            }else{
                speed = 0.0;
                dist = 0.0;
            }
            //compute the distance

            totalList.add(new OutputNetwork(speed, bearing, dist));
        });
        this.realOutput = new ArrayList<>();
        this.realOutput.addAll(totalList);

    }
}


package tgcfs.Loader;

import lgds.trajectories.Point;
import org.nd4j.linalg.api.ndarray.INDArray;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import tgcfs.Agents.Models.RealAgent;
import tgcfs.Agents.OutputNetwork;
import tgcfs.Classifiers.InputNetwork;
import tgcfs.Config.ReadConfig;
import tgcfs.Idsa.IdsaLoader;
import tgcfs.InputOutput.PointToSpeedBearing;
import tgcfs.NN.InputsNetwork;
import tgcfs.NN.OutputsNetwork;
import tgcfs.Performances.Statistics;
import tgcfs.Utils.PointWithBearing;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    private UUID id;
    private final List<InputsNetwork> trainingPoint;
    private List<PointWithBearing> firstPart;
    private final List<PointWithBearing>  followingPart;
    private List<InputsNetwork> followingPartTransformed;
    private List<OutputsNetwork> realOutput;
    private List<InputsNetwork> allThePartTransformedFake;
    private List<InputsNetwork> allThePartTransformedReal;
    private final String conditionalImage;
    private final String normalImage;
    private List<OutputsNetwork> outputComputed;
    private List<PointWithBearing> realPointsOutputComputed;
    private IdsaLoader idsaLoader;
    private List<Point> totalPoints;
    private double fitnessGivenByTheClassifier;
    private RealAgent idRealPoint;
    private double lastTime;
    private Statistics statistics;

    /**
     * Constructor with two parameters
     * @param trainingPoint list of inputNetwork
     * @param followingPart list of points
     */
    public TrainReal(List<InputsNetwork> trainingPoint, List<PointWithBearing> followingPart){
        this.id = UUID.randomUUID();
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
        this.idRealPoint = null;
        this.fitnessGivenByTheClassifier = 0;
        this.statistics = null;
    }

    /**
     * Constructor with three parameters
     * @param trainingPoint list of inputNetwork
     * @param followingPart list of points
     */
    public TrainReal(List<InputsNetwork> trainingPoint, List<PointWithBearing> followingPart, double lastTime){
        this.id = UUID.randomUUID();
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
        this.idRealPoint = null;
        this.fitnessGivenByTheClassifier = 0;
        this.lastTime = lastTime;
        this.statistics = null;
    }

    /**
     * Constructor with three parameters
     * @param trainingPoint list of inputNetwork
     * @param followingPart list of points
     */
    public TrainReal(List<InputsNetwork> trainingPoint, List<PointWithBearing> followingPart, double lastTime, UUID id, List<OutputsNetwork> realOutput){
        this.id = id;
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
        this.realOutput = realOutput;
        this.idRealPoint = null;
        this.fitnessGivenByTheClassifier = 0;
        this.lastTime = lastTime;
        this.statistics = null;
    }

    /**
     * Constructor with four parameters
     * @param trainingPoint list of inputNetwork
     * @param followingPart list of points
     * @param conditionalImage path of the conditional image
     */
    public TrainReal(List<InputsNetwork> trainingPoint, List<PointWithBearing>  followingPart, String conditionalImage, String normalImage){
        this.id = UUID.randomUUID();
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
        this.idRealPoint = null;
        this.fitnessGivenByTheClassifier = 0;
        this.statistics = null;
    }


    /**
     * Constructor with three parameter
     * @param trainingPoint the training point
     * @param followingPart the following part
     * @param idsaLoader idsa loader reference
     */
    public TrainReal(List<InputsNetwork> trainingPoint, List<PointWithBearing>  followingPart, IdsaLoader idsaLoader){
        this.id = UUID.randomUUID();
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
        this.idRealPoint = null;
        this.fitnessGivenByTheClassifier = 0;
        this.statistics = null;
    }


    /**
     * Deep copy constructor
     * @param trainingPoint
     * @param firstPart
     * @param followingPart
     * @param followingPartTransformed
     * @param realOutput
     * @param allThePartTransformedFake
     * @param allThePartTransformedReal
     * @param conditionalImage
     * @param normalImage
     * @param outputComputed
     * @param realPointsOutputComputed
     * @param idsaLoader
     * @param totalPoints
     * @param idRealPoint
     */
    public TrainReal(List<InputsNetwork> trainingPoint, List<PointWithBearing> firstPart, List<PointWithBearing> followingPart, List<InputsNetwork> followingPartTransformed, List<OutputsNetwork> realOutput, List<InputsNetwork> allThePartTransformedFake, List<InputsNetwork> allThePartTransformedReal, String conditionalImage, String normalImage, List<OutputsNetwork> outputComputed, List<PointWithBearing> realPointsOutputComputed, IdsaLoader idsaLoader, List<Point> totalPoints, UUID id, RealAgent idRealPoint, double fitnessGivenByTheClassifier, double time, Statistics statistics) {
        this.id = id;
        this.trainingPoint = trainingPoint;
        this.firstPart = firstPart;
        this.followingPart = followingPart;
        this.followingPartTransformed = followingPartTransformed;
        this.realOutput = realOutput;
        this.allThePartTransformedFake = allThePartTransformedFake;
        this.allThePartTransformedReal = allThePartTransformedReal;
        this.conditionalImage = conditionalImage;
        this.normalImage = normalImage;
        this.outputComputed = outputComputed;
        this.realPointsOutputComputed = realPointsOutputComputed;
        this.idsaLoader = idsaLoader;
        this.totalPoints = totalPoints;
        this.idRealPoint = idRealPoint;
        this.fitnessGivenByTheClassifier = fitnessGivenByTheClassifier;
        this.lastTime = time;
        this.statistics = statistics;
    }


    /**
     * Getter for the input part of the trajectory
     * deepCopy of the list
     * @return list of inputNetworks
     */
    public List<InputsNetwork> getTrainingPoint() {
        List<InputsNetwork> copyOfTheList = new ArrayList<>();
        for(InputsNetwork in: this.trainingPoint){
            copyOfTheList.add(in.deepCopy());
        }
        return copyOfTheList;
    }

    /**
     *  Getter for the input part of the trajectory
     *  setted to be used as a classifier input network
     * @return {@link InputNetwork} list
     */
    public List<InputsNetwork> getTrainingPointSettedForTheClassifier(){
        List<InputsNetwork> newlist = new ArrayList<>();
        for(InputsNetwork tr: this.trainingPoint){
            INDArray ind = ((tgcfs.Agents.InputNetwork) tr).serialiseAsInputClassifier();
            InputNetwork in = new InputNetwork(ind.getDouble(0), ind.getDouble(1), false);
            newlist.add(in);
        }
        return newlist;
    }

    /**
     * Getter for the real part after the training part
     * @return list of points
     */
    public List<PointWithBearing> getFollowingPart() {
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
        this.outputComputed = new ArrayList<>();
        for(OutputsNetwork outputsNetwork: outputComputed){
            this.outputComputed.add(outputsNetwork.deepCopy());
        }
    }

    /**
     * Getter for last point of source
     * @return Position
     */
    public PointWithBearing getLastPoint() {
        PointWithBearing p = this.firstPart.get(this.firstPart.size() - 1);
        PointWithBearing ret;
        if(p.getTime() == null){
            ret = new PointWithBearing(p.getLatitude(), p.getLongitude(), 0.0, 0d, LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")), LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")),p.getBearing());
        }else{
            ret = new PointWithBearing(p.getLatitude(), p.getLongitude(), 0.0, p.getDated(),p.getDates(), p.getTime(),p.getBearing());
        }
        return ret;
    }

    /**
     * Getter for the points on the first part of trajectories
     * @return Position
     */
    public List<PointWithBearing> getPoints() {
        return this.firstPart;
    }

    /**
     * Setter for first part of the source
     * @param firstPart point
     */
    public void setPoints(List<PointWithBearing> firstPart) throws Exception {
        //check how many points I want to analise
        this.firstPart = firstPart;

        int timesteps = ReadConfig.Configurations.getNumberOfTimestepConsidered();
        //if it is zero I do not care and use all the timesteps
        if(timesteps != 0){
            //keep only the timesteps that I need
            while(this.firstPart.size() > timesteps){
                //remove the first one till I reach the size I want
                this.firstPart.remove(0);
            }
        }
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
    public List<PointWithBearing> getRealPointsOutputComputed() {
        return this.realPointsOutputComputed;
    }

    /**
     * Setter fot the list of the real output computed
     * @param realPointsOutputComputed List<Point>
     */
    public void setRealPointsOutputComputed(List<PointWithBearing> realPointsOutputComputed) {
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
                for(int i = 0; i< size; i ++){
                    this.totalPoints.add(this.followingPart.get(i));
                }
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
        //need to set a new ID for this real element
        this.idRealPoint = new RealAgent();
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
     * Transform the real point given as real output into an {@link OutputNetwork} for the classifier
     */
    public void createRealOutputConverted(){
        if(this.realOutput == null) {
            //class that compute the conversion point -> speed/bearing
            PointToSpeedBearing conversion = new PointToSpeedBearing();
            List<OutputsNetwork> totalList = new ArrayList<>();

            //add the last point to the end to enable the computation of the output
            List<Point> herePoint = new ArrayList<>();
            herePoint.add(this.firstPart.get(this.firstPart.size() - 1));
            herePoint.addAll(this.followingPart);

            for (int i = 1; i < herePoint.size(); i++) {

                Point previousPoint = herePoint.get(i - 1);
                Point actualPoint = herePoint.get(i);

                double bearing = conversion.obtainBearing(previousPoint, actualPoint);
                double speed = conversion.obtainSpeed(previousPoint, actualPoint, this.lastTime);
                if(ReadConfig.isETH) speed = conversion.obtainSpeedEuclideanDistance(previousPoint, actualPoint, this.lastTime);
                totalList.add(new OutputNetwork(speed, bearing));
//                totalList.add(new OutputNetworkTime(speed, bearing, time));

            }

            this.realOutput = new ArrayList<>();
            this.realOutput.addAll(totalList);
        }
    }

    /**
     * Soft copy of the object -> reset it
     * @return {@link TrainReal} object
     */
    public TrainReal softCopy(){
        return new TrainReal(this.trainingPoint,this.followingPart, this.lastTime, this.id, this.realOutput);
    }

    /**
     * Deep Copy
     * @return {@link TrainReal} object
     */
    public TrainReal deepCopy(){
        return new TrainReal(this.trainingPoint, this.firstPart, this.followingPart, this.followingPartTransformed, this.realOutput, this.allThePartTransformedFake, this.allThePartTransformedReal, this.conditionalImage, this.normalImage, this.outputComputed, this.realPointsOutputComputed, this.idsaLoader, this.totalPoints, this.id, this.idRealPoint, this.fitnessGivenByTheClassifier, this.lastTime, this.statistics);
    }

    /**
     * Return the fitnessGivenByTheClassifier result from this input
     * @return boolean value
     */
    public double getFitnessGivenByTheClassifier() {
        return this.fitnessGivenByTheClassifier;
    }

    /**
     * Set the fitnessGivenByTheClassifier from the classifier to this input
     * @param getFitnessGivenByTheClassifier double value
     */
    public void setFitnessGivenByTheClassifier(double getFitnessGivenByTheClassifier) {
        this.fitnessGivenByTheClassifier = getFitnessGivenByTheClassifier;
    }

    /**
     * return the ID of this class
     * @return {@link UUID} id
     */
    public UUID getId() {
        return this.id;
    }

    /**
     * Return real agent corresponding to this trajectory
     * @return {@link RealAgent}
     */
    public RealAgent getIdRealPoint() {
        return idRealPoint;
    }

    /**
     * Getter for the last time computed (the one that I am requesting to the network)
     * @return double value
     */
    public double getLastTime() {
        return this.lastTime;
    }


    /**
     * Compute the statistics for this point
     * - distance between generated point and real point
     * - difference between original bearing and generated bearing
     * TODO generalise for more than one point
     */
    public void computeStatistic() {
        //compute MSE for the point to the real point
        //compute distance real point to generated point
        //* 1000 so it is going to be in metres
        double euclideanDistance = this.followingPart.get(0).euclideanDistance(this.realPointsOutputComputed.get(0)) * 100000;
        this.statistics = new Statistics(this.id, Math.pow(euclideanDistance, 2));
    }

    /**
     * Getter for the statistics of this point
     * @return {@link Statistics object}
     */
    public Statistics getStatistics() {
        return statistics;
    }
}


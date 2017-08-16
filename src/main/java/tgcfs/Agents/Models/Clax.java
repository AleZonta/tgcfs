package tgcfs.Agents.Models;

import freemarker.template.utility.NullArgumentException;
import gms.ClaxPreload.Preload;
import gms.GraphML.InfoEdge;
import gms.GraphML.InfoNode;
import gms.LoadingSystem.WeightedGraph;
import lgds.map.OsmosisLoader;
import lgds.trajectories.Point;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.NDArrayIndex;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import tgcfs.Idsa.IdsaLoader;
import tgcfs.InputOutput.PointToSpeedBearing;
import tgcfs.Loader.Feeder;
import tgcfs.NN.EvolvableModel;
import tgcfs.NN.InputsNetwork;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

/**
 * Created by Alessandro Zonta on 10/08/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 *
 * Implementation model that gives to every class a weight and than uses these weight to compute the total weight of
 * the arc.
 *
 * It loads the classes from db
 * the classes are part of the genome
 *
 * from the point where the trajectory just ended we use the APF to find the target
 * we have a value that controls how important is the distance between two nodes in the final equation
 * we have a value that control how important is the attraction of the middle point of the arc
 * we evolve all the list of this value and then we compute the trajectory ending with that target
 *
 */
public class Clax implements EvolvableModel {
    private List<String> classes;
    private INDArray weights;
    private final OsmosisLoader db;
    private Point target;
    private Point start;
    private final Feeder feeder; //system that will translate from output to new input using graph config
    private final IdsaLoader idsaLoader;
    private static final Logger logger = Logger.getLogger(Clax.class.getName()); //logger for this class

    /**
     * Normal Constructor
     * it loads the db and the information from the db
     * @param feeder reference to the graph
     * @param loader reference to idsa
     * @throws FileNotFoundException if the db is not available
     */
    public Clax(Feeder feeder, IdsaLoader loader) throws FileNotFoundException {
        //ask the db how many classes I have
        this.db = new OsmosisLoader();
        this.classes = db.obtainValues();

        //from 0 to classes.size
        //plus one position for pacman width
        //plus two control values
        //equals size + 3
        this.weights = Nd4j.zeros(this.classes.size() + 3);
        this.target = null;
        this.start = null;
        this.feeder = feeder;
        this.idsaLoader = loader;
    }

    /**
     * Constructor with all the parameters
     * used for the deep copy method
     * @param classes list of classes
     * @param db reference to the db
     * @param feeder reference to the graph
     * @param loader reference to idsa
     */
    public Clax(List<String> classes, OsmosisLoader db, Feeder feeder, IdsaLoader loader){
        this.classes = classes;
        this.weights = Nd4j.zeros(this.classes.size() + 3);
        this.db = db;
        this.target = null;
        this.start = null;
        this.feeder = feeder;
        this.idsaLoader = loader;
    }

    /**
     * @implNote Implementation from Interface
     * @return Integer value
     */
    @Override
    public Integer getArrayLength() {
        return this.weights.columns();
    }

    /**
     * @implNote Implementation from Interface
     * @return list weights
     */
    @Override
    public INDArray getWeights() {
        return this.weights;
    }

    /**
     * @implNote Implementation from Interface
     * @param weights list containing all the weights
     * @throws Exception if the length of the list is not correct
     */
    @Override
    public void setWeights(INDArray weights) throws Exception {
        if (weights.columns() != this.weights.columns()){
            throw new Exception("Length list weights is not correct.");
        }
        this.weights = weights;
    }

    /**
     * @implNote Implementation from Interface
     * @return deep copy of the model
     */
    @Override
    public EvolvableModel deepCopy() {
        return new Clax(this.classes, this.db, this.feeder, this.idsaLoader);
    }

    /**
     * @implNote Implementation from Interface
     * @param input input of the network
     */
    @Override
    public void fit(List<InputsNetwork> input, List<Point> points) {
        throw new NotImplementedException();
    }

    /**
     * Compute the output for the network
     * first time that it is called it set the start position
     * @param input list value that are the input of the network
     * @return array with next position
     */
    @Override
    public INDArray computeOutput(INDArray input) {
        throw new NotImplementedException();
    }

    /**
     * Return list of the weight of the classes
     * @return list of double
     */
    private List<Double> retWeightclasses(){
        INDArray weight = this.weights.get(NDArrayIndex.interval(0, this.getArrayLength() - 3), NDArrayIndex.all());
        List<Double> list = new ArrayList<>();
        IntStream.range(0, weight.columns()).forEach(i -> {
            list.add(weight.getDouble(i));
        });
        return list;
    }

    /**
     * Return double value of the distance importance
     * @return Double value
     */
    private Double getDistanceValue(){
        return this.weights.getDouble(this.getArrayLength() - 2);
    }

    /**
     * Return double value of the attraction importance
     * @return Double value
     */
    private Double getAttractionValue(){
        return this.weights.getDouble(this.getArrayLength() - 1);
    }

    /**
     * Return double value of the pacman width
     * @return Double value
     */
    private Double getPacmanValue(){
        return this.weights.getDouble(this.getArrayLength());
    }


    /**
     * Compute the trajectory
     * @return list of nodes
     * @throws Exception if something goes wrong in the computation
     */
    public List<INDArray> computeTrajectory() throws Exception {
        //i have first point
        if(this.start == null) throw new NullArgumentException("Kind of impossible but something really wrong happened");
        //compute the output point using the idsaAPF
        if(this.target == null) throw new NullArgumentException("Target non loaded");
        //need the connection with the graph
        if (this.feeder == null) throw new NullArgumentException("System with the graph not instantiate");
        //obtain the result
        List<InfoNode> result = this.getUsefulArc();
        //time to convert the result into something readable for the tl
        List<INDArray> arrayOut = new ArrayList<>();

        PointToSpeedBearing conversion = new PointToSpeedBearing();

        IntStream.range(0, result.size() - 1).forEach(i -> {
            //bearing from this point to next point
            Point actualPoint = new Point(result.get(i).getLat(), result.get(i).getLon());
            Point nextPoint = new Point(result.get(i + 1).getLat(), result.get(i + 1 ).getLon());
            Double bearing = conversion.obtainBearing(actualPoint,nextPoint);
            //speed is the speed I arrived here from previous point
            Double speed;
            if(i > 0){
                Point previousPoint =  new Point(result.get(i - 1).getLat(), result.get(i - 1).getLon());
                speed = conversion.obtainSpeed(previousPoint, actualPoint);
            }else{
                speed = 0.0;
            }
            Double distance = conversion .obtainDistance(actualPoint,nextPoint);

            INDArray arr = Nd4j.zeros(3);
            arr.putScalar(0, bearing);
            arr.putScalar(1, speed);
            arr.putScalar(2, distance);
            arrayOut.add(arr);
        });

        return arrayOut;
    }

    /**
     * Set the target
     * @param target input to set
     */
    public void setTarget(Point target){
        this.target = target;
    }


    /**
     * Compute the weights of the new arcs
     * it generates on the fly a new graph with the new weights and it computes the shortest path with it
     * to compute the weights now the procedure is:
     * for every arc
     * check what there is
     * match the description with the list we have
     * multiplication between the number of occurrences and the value on the genome
     * then the multiplication between the distance and the value on the genome is added to the result
     * then the attraction value is retrieved from idsa and the multiplication with the value in the genome is added to the result.
     * @return list of {@link InfoNode} containing the shortest path
     * @throws Exception errors occur
     */
    private List<InfoNode> getUsefulArc() throws Exception {
        //from start need to obtain all the arc that goes from it
        //need to check the direction of them and see if it is inside or outside the angle set/evolved
        //now maybe I will check all of them -> the entire graph
        //basically i do not need the start point
        //should I create a new graph with the new weights?
        //should I modify the weight of the one already loaded and compute the trajectory over it?

        //return all the edge of the graph
        Set<InfoEdge> edges = this.feeder.retAllEdges();
        //lets create a new graph with the weight computed with the formula decided
        WeightedGraph graphW = new WeightedGraph();
        //add all the nodes
        this.feeder.retAllNodes().forEach(graphW::addNode);

        //preload object to retrieve the information of the edge
        Preload pr = new Preload();

        //for every edges
        this.feeder.retAllEdges().forEach(infoEdge -> {
            //return what I have found in this edge
            Map<String, Integer> positions = pr.getElementEdge(infoEdge.getSource().getLat(), infoEdge.getSource().getLon(), infoEdge.getTarget().getLat(), infoEdge.getTarget().getLon());
            //counter
            final Double[] totalImportance = {0d};

            //check the index in the array of the thing in the edge
            positions.forEach((element, counting) -> {
                Integer positionOnArray = this.classes.indexOf(element);
                Double importance = this.retWeightclasses().get(positionOnArray);
                totalImportance[0] += (importance * counting);
            });
            totalImportance[0] += (new Double(infoEdge.retDistance()) * this.getDistanceValue());
            try {
                totalImportance[0] += this.getAttractionValue() * this.computeMidValuePoint(infoEdge.getSource().getLat(), infoEdge.getSource().getLon(), infoEdge.getTarget().getLat(), infoEdge.getTarget().getLon());
            } catch (Exception e) {
                logger.log(Level.WARNING, "Problems with the computation of the potential -> " + e.getMessage());
            }

            //add the edge to the graph
            graphW.addEdge(infoEdge.getSource(),infoEdge.getTarget(), totalImportance[0]);

        });
        //this is the shortest path with the weights just computed
        return graphW.findPathBetweenNodes(this.start, this.target);
    }


    /**
     * Compute middle point of the arc and return potential attraction (with IDSA) from that position
     * @param lati latitude start
     * @param loni longitude start
     * @param late latitude end
     * @param lone longitude end
     * @return double potential position
     * @throws Exception problems obtain the potential
     */
    private Double computeMidValuePoint(Double lati, Double loni, Double late, Double lone) throws Exception {
        Double middleLat = (lati + late) / 2;
        Double middleLong = (loni + lone) / 2;
        return this.idsaLoader.returnAttraction(new Point(middleLat, middleLong));
    }

    /**
     * Set start parameter
     * @param start Point start
     */
    public void setStart(Point start){
        this.start = start;
    }


}

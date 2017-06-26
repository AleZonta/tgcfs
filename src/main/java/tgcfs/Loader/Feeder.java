package tgcfs.Loader;

import gms.GraphML.InfoEdge;
import gms.GraphML.InfoNode;
import gms.Loader;
import gms.Point.Coord;
import lgds.trajectories.Point;
import lgds.trajectories.Trajectories;
import lgds.trajectories.Trajectory;
import tgcfs.Config.ReadConfig;
import tgcfs.Idsa.IdsaLoader;
import tgcfs.InputOutput.Normalisation;
import tgcfs.InputOutput.PointToSpeedBearing;
import tgcfs.NN.InputsNetwork;
import tgcfs.Routing.Routes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

/**
 * Created by Alessandro Zonta on 16/05/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 */
public class Feeder {
    private final Loader graph; //loader of the graph
    private final Routes routes; //loader of the trajectories
    private Integer position; //I need to remember the position where I am now
    private Boolean finished; //If the current trajectory is ended
    private Trajectory currentTrajectory; //current trajectory under investigation
    private Integer maximumNumberOfTrajectories;
    private Integer actualNumberOfTrajectory;
    private static final Logger logger = Logger.getLogger(Feeder.class.getName()); //logger for this class

    /**
     * Constructor with zero parameter
     * Config file is read
     * The graph system and the trajectories system are initialised
     * @throws Exception Exception raised if there are problems with the files
     */
    public Feeder() throws Exception{
        logger.log(Level.INFO, "Initialising system...");
        this.graph = new Loader();
        this.routes = new Routes();
        this.position = 0;
        this.finished = Boolean.FALSE;
        this.actualNumberOfTrajectory = 0;
        this.maximumNumberOfTrajectories = ReadConfig.Configurations.getHowManyTrajectories();
    }

    /**
     * Loading trajectories and graph system.
     * @throws Exception Exception raised if there are problems with the files
     */
    public void loadSystem() throws Exception {
        logger.log(Level.INFO, "Loading system...");
        this.routes.readTrajectories();
        this.graph.loadGraph();
        if(this.maximumNumberOfTrajectories == 99999){
            this.maximumNumberOfTrajectories = this.routes.getTra().getTrajectories().size();
        }
        logger.log(Level.INFO, "System Online!!!");
    }


    /**
     * Method that returns every how many timestep I am providing the trajectory
     * Is reading from setting how many time split a trajectory
     * @param tra trajectory under analysis
     * @return number of timestep to wait before providing the new part of trajectory
     * @throws Exception raising an exception if the setting file is not available or not correct
     */
    private Integer selectPositionInTrajectory(Trajectory tra) throws Exception{
        //decide how many time to analise the trajectory
        Integer split = ReadConfig.Configurations.getHowManySplitting() + 1;
        //need to check if the trajectory is shorter than the split number
        if(tra.getSize() <= split){
            split = 2; //I only split in half
        }
        return (int) Math.floor(tra.getSize() / split);
    }

    //need to convert the trajectory into nodes of the graph
    //checking every point of the trajectory and computing the subgraph between them

    /**
     * Method that transform a trajectory into a list of nodes of the graph.
     * It computes the node path per every two points of the trajectory
     * @param tra trajectory that I need to translate
     * @return a list of nodes corresponding to the trajectory
     * @throws Exception an exception is raised if no path is found between two nodes
     */
    public List<InfoNode> fromTrajectoryToNodesInGraph(Trajectory tra) throws Exception {
        //list containing all the nodes of the trajectory
        List<InfoNode> listAllTheNodesOfThePath = new ArrayList<>();

        //first node
        Point pointA = tra.getFirstPoint();

        Coord coordA = new Coord(pointA.getLatitude(), pointA.getLongitude());
        InfoNode initialNode = this.graph.findNodes(coordA);

        //second node
        Point pointB = this.routes.getNextPosition(tra);

        //until pointB is null -> ended the trajectory
        while(pointB != null){

            Coord coordB = new Coord(pointB.getLatitude(), pointB.getLongitude());
            //checking the closest node to the coordinate
            InfoNode finalNode = this.graph.findNodes(coordB);

            if (listAllTheNodesOfThePath.stream().noneMatch(infoNode -> infoNode.getId().equals(finalNode.getId()))) {
                //It is a new node. Is the right one?
                //check also the edge from the last node
                //checking the closest edge to the point.
                InfoEdge closerEdgeToNode = this.graph.findClosestEdge(coordB, new Coord(initialNode.getLat(), initialNode.getLon()));
                //who is the closer one? The edge to endNode or the final node?
                if (this.graph.isEdgeCloser(closerEdgeToNode, finalNode, coordB)) {
                    InfoNode endNode = closerEdgeToNode.getTarget();

                    listAllTheNodesOfThePath.add(endNode);
                    //swap second node with first node
                    initialNode = endNode.deepCopy();
                } else {
                    listAllTheNodesOfThePath.add(finalNode);
                    //swap second node with first node
                    initialNode = finalNode.deepCopy();
                }

            }
            //find new second node
            pointB = this.routes.getNextPosition(tra);
        }

        return listAllTheNodesOfThePath;
    }

    /**
     * Method that returns next usable trajectory
     * @return trajectory
     */
    public Trajectory getTrajectory(){
        this.actualNumberOfTrajectory++;
        return this.routes.getNextTrajectory();
    }


    /**
     * Method that returns all the trajectories
     * @return all the trajectories
     */
    public Trajectories getTrajectories(){
        return this.routes.getTra();
    }

    /**
     * Return the maximum number ot trajectories to analise
     * @return Integer number with maximum number
     */
    public Integer getMaximumNumberOfTrajectories() {
        return this.maximumNumberOfTrajectories;
    }

    /**
     * Method that returns next point of the trajectory
     * @param tra trajectory where I need next point
     * @return next point
     */
    public Point getNextPoint(Trajectory tra){
        return this.routes.getNextPosition(tra);
    }


    /**
     * From a list of points to a list of speedirecitons
     * It computes the speed of the agent
     * The speed is computed using the distance between the previous step and this step and the time between them
     * The bearing is computed checking the future direction where the agent is moving
     * The bearing is how the crow flies, no consideration about real routing
     * @param points points to transform
     * @return list of speeddirection objects
     */
    public List<InputsNetwork> obtainInput(List<Point> points, Double attraction){
        //class that compute the conversion point -> speed/bearing
        PointToSpeedBearing conversion = new PointToSpeedBearing();
        List<InputsNetwork> totalList = new ArrayList<>();
        IntStream.range(0, points.size() - 1).forEach(i -> {
            //bearing from this point to next point
            Point actualPoint = points.get(i);
            Point nextPoint = points.get(i+1);
            Double bearing = conversion.obtainBearing(actualPoint,nextPoint);
            //speed is the speed I arrived here from previous point
            Double speed;
            if(i > 0){
                Point previousPoint = points.get(i - 1);
                speed = conversion.obtainSpeed(previousPoint, actualPoint);
            }else{
                speed = 0.0;
            }
            totalList.add(new tgcfs.Agents.InputNetwork(attraction, speed, bearing));
        });

        return totalList;
    }


    /**
     * Return the number of step required from the current trajectory
     * @param trajectory trajectory under evaluation
     * @return list of coordinate
     * @throws Exception if there are errors reading the config
     */
    public List<Point> obtainSectionTrajectory(Trajectory trajectory) throws Exception {
        List<Point> point = new ArrayList<>();
        //Every how many time step I return the trajectory
        Integer count = this.selectPositionInTrajectory(trajectory);
        IntStream.range(this.position, this.position + count).forEach(i -> {
            Point p = this.getNextPoint(trajectory);
            //If it not null I add the element to the list
            if(p != null){
                point.add(p);
            }else{
                //if it is null I have finished the trajectory
                this.finished = Boolean.TRUE;
            }
        });
        this.position += count;
        return point;
    }

    /**
     * Getter if the trajectory under evaluation is finished
     * @return boolean value
     */
    public Boolean getFinished() {
        return finished;
    }


    /**
     * Feeder method
     * If I have to use a new trajectory it load it
     * It obtains the section of the trajectory that I need right now
     * It translate the trajectory' points into the input format for the framework
     * @param idsaLoader reference IDSA system
     * @return list of input formats
     * @throws Exception if there are problems with the config file or we reached the maximum number of trajectories usable
     */
    public List<InputsNetwork> feeder(IdsaLoader idsaLoader) throws Exception {
        //retrieve trajectory
        //if the trajectory is yet not ended I do not need to load the next one
        if (!this.finished) {
            //If I have reached the maximum number of trajectory usable raise an exception
            if(Objects.equals(this.actualNumberOfTrajectory, this.maximumNumberOfTrajectories)){
                throw new ReachedMaximumNumberException("Reached Maximum Number Of trajectories");
            }
            this.currentTrajectory = this.getTrajectory();
            //new trajectory new apf
            idsaLoader.resetAPF();
        }
        //retrieve section form the trajectory
        List<Point> actualPoint = this.obtainSectionTrajectory(this.currentTrajectory);
        while(actualPoint.size() == 0){
            actualPoint = this.obtainSectionTrajectory(this.currentTrajectory);
        }

        //compute the potential field for the actualPoint
        actualPoint.forEach(idsaLoader::compute);

        //return the list of input network
        return  this.obtainInput(actualPoint, idsaLoader.returnAttraction(actualPoint.get(actualPoint.size() - 1)));

    }

    /**
     * Method to return the real part of the trajectory under analysis for the real agent
     * @return the list of points
     * @throws Exception if there are problems with the conf
     */
    public List<Point> obtainRealAgentSectionTrajectory() throws Exception {
        //I have trajectory and I have current position.
        //I just need to retrieve next n position
        List<Point> realPoint = new ArrayList<>();
        IntStream.range(this.position, this.position + ReadConfig.Configurations.getAgentTimeSteps()).forEach(i -> {
            Point nextPoint = this.getNextPoint(this.currentTrajectory);
            if (nextPoint != null){
                realPoint.add(nextPoint);
            }
        });
        return realPoint;
    }


    /**
     * Find the next location given actual position, distance and direction
     * @param whereIam position where I am
     * @param speed speed I am moving
     * @param distance distance I have travelled
     * @param direction direction I am moving
     * @return next point
     */
    public Point getNextLocation(Point whereIam, Double speed, Double distance, Double direction){
        //find position where I am
        Coord coordA = new Coord(whereIam.getLatitude(), whereIam.getLongitude());
        InfoNode initialNode = this.graph.findNodes(coordA);

        InfoNode closestNode = this.getClosestNode(initialNode, whereIam, direction);

        if(!closestNode.equals(initialNode)) {
            //need to find edge and its distance
            Double dis = this.graph.findDistanceBetweenNodesConnected(initialNode, closestNode);
            if (distance < dis) {
                //the new point is in the middle somewhere in this edge
                Coord position = this.graph.findPointInEdge(initialNode, closestNode, distance);
                //find time from previous point
                //distance / speed
                Double plusTime = distance / speed;
                return new Point(position.getLat(), position.getLon(), whereIam.getAltitude(), whereIam.getDated(), whereIam.getDates(), whereIam.addTimeToPoint(plusTime));
            } else {
                //loop until I am close enough -> better than recursive, Lisa has problems
                Point nextPosition = new Point(closestNode.getLat(), closestNode.getLon(), whereIam.getAltitude(), whereIam.getDated(), whereIam.getDates(), whereIam.getTime());

                InfoNode secondInitialNode = null;
                InfoNode closestSecondNode = null;
                Double dist = 0d;
                Double updatedDistance = distance - dis;
                while (updatedDistance > dis) {
                    Coord coordB = new Coord(nextPosition.getLatitude(), nextPosition.getLongitude());
                    secondInitialNode = this.graph.findNodes(coordB);
                    closestSecondNode = this.getClosestNode(secondInitialNode, nextPosition, direction);

                    if(secondInitialNode.equals(closestSecondNode)) {
                        return nextPosition;
                    }


                    //need to find edge and its distance
                    dist = this.graph.findDistanceBetweenNodesConnected(secondInitialNode, closestSecondNode);
                    updatedDistance -= dist;
                    nextPosition = new Point(closestSecondNode.getLat(), closestSecondNode.getLon(), whereIam.getAltitude(), whereIam.getDated(), whereIam.getDates(), whereIam.getTime());
                }

                //the new point is in the middle somewhere in this edge
                Coord position = this.graph.findPointInEdge(secondInitialNode, closestSecondNode, dist);
                //find time from previous point
                //distance / speed
                Double plusTime = distance / speed;
                return new Point(position.getLat(), position.getLon(), whereIam.getAltitude(), whereIam.getDated(), whereIam.getDates(), whereIam.addTimeToPoint(plusTime));
            }
        }else{
            //If I am returning the same point I have to return the initial coordinates. We did not move anywhere
            return whereIam;
        }
    }


    /**
     * Get initialNode closest node
     * @param initialNode node where I am
     * @param whereIam exactly point where I am
     * @param direction direction I am moving
     * @return closest point
     */
    private InfoNode getClosestNode(InfoNode initialNode, Point whereIam, Double direction){
        //find all the edge connected to this node
        List<InfoNode> endNodes = this.graph.retAllEndEdges(initialNode);
        List<Double> angles = new ArrayList<>();
        Coord IamHere = new Coord(whereIam.getLatitude(),whereIam.getLongitude());
        //compute all the angles
        endNodes.forEach(node -> angles.add(IamHere.angleWith(node.getCoord())));
        //find id closest ending node
        final Integer[] index = {0};
        final Double[] minDifference = {Double.MAX_VALUE};
        IntStream.range(0, angles.size()).forEach(i -> {
            Double difference = Math.abs(Normalisation.fromHalfPItoTotalPI(direction) - Normalisation.fromHalfPItoTotalPI(angles.get(i)));
            if(difference < minDifference[0]){
                minDifference[0] = difference;
                index[0] = i;
            }
        });
        //now I now index closest nodes
//        if (Math.abs(Normalisation.fromHalfPItoTotalPI(direction) - Normalisation.fromHalfPItoTotalPI(angles.get(index[0]))) < 10){
//            //if im am going in the direction suggested by the nn I will move there
//            return endNodes.get(index[0]);
//        }
        //not correct direction? I am staying here
        return endNodes.get(index[0]);
    }
}

package tgcfs.Loader;

import Connections.DatabaseCoordNode;
import gms.GraphML.InfoEdge;
import gms.GraphML.InfoNode;
import gms.LoadingSystem.Loader;
import gms.LoadingSystem.System;
import gms.Point.Coord;
import lgds.trajectories.Point;
import lgds.trajectories.Trajectories;
import lgds.trajectories.Trajectory;
import tgcfs.Agents.InputNetwork;
import tgcfs.Config.ReadConfig;
import tgcfs.Idsa.IdsaLoader;
import tgcfs.InputOutput.Normalisation;
import tgcfs.InputOutput.PointToSpeedBearing;
import tgcfs.NN.InputsNetwork;
import tgcfs.Routing.Routes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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
    private final System graph; //loader of the graph
    private final Routes routes; //loader of the trajectories
    private Integer position; //I need to remember the position where I am now
    private Boolean finished; //If the current trajectory is ended
    private Trajectory currentTrajectory; //current trajectory under investigation
    private Integer maximumNumberOfTrajectories;
    private Integer actualNumberOfTrajectory;
    private List<Point> points;
    private final DatabaseCoordNode db; //database saving all the already visited nodes
    private Boolean isNewTrajectory;
    private Point lastTimeUsed;
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
        this.finished = Boolean.TRUE;
        this.actualNumberOfTrajectory = 0;
        this.maximumNumberOfTrajectories = ReadConfig.Configurations.getHowManyTrajectories();
        this.points = null;
        this.db = new DatabaseCoordNode();
        this.isNewTrajectory = null;
        this.lastTimeUsed = null;

        //set KdTree search
        this.graph.setOptimisedGraph(Boolean.TRUE);
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
     * @return number of time step to wait before providing the new part of trajectory
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
     * @param attraction attraction of the potential field
     * @param possibleTarget possible target of this trajectory -> highest point charged
     * @return list of speeddirection objects
     */
    public List<InputsNetwork> obtainInput(List<Point> points, Double attraction, Point possibleTarget){
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
            Double space;
            if(i > 0){
                Point previousPoint = points.get(i - 1);
                speed = conversion.obtainSpeed(previousPoint, actualPoint);
                space = conversion.obtainDistance(previousPoint, actualPoint);
            }else{
                speed = 0.0;
                space = 0.0;
            }

            InputNetwork inputNetwork = new InputNetwork(attraction, speed, bearing, space);
            inputNetwork.setTargetPoint(possibleTarget);
            totalList.add(inputNetwork);
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
//        if(this.position > trajectory.getSize()) {
//            this.finished = Boolean.TRUE;
//            this.position = 0;
//        }
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
        if(this.finished){
            this.position = 0;
        }else {
            this.position += count;
        }
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
        //set info for the time
        this.isNewTrajectory = Boolean.FALSE;
        //retrieve trajectory
        //if the trajectory is yet not ended I do not need to load the next one
        if (this.finished) {
            //If I have reached the maximum number of trajectory usable raise an exception
//            if(Objects.equals(this.actualNumberOfTrajectory, this.maximumNumberOfTrajectories)){
//                throw new ReachedMaximumNumberException("Reached Maximum Number Of trajectories");
//            }
            if(this.currentTrajectory != null) {
                //if I know the trajectory is ended I have to reset it for future usage
                this.currentTrajectory.softResetTrajectory();
            }
            this.currentTrajectory = this.getTrajectory();
            //new trajectory new apf
            idsaLoader.resetAPF();
            //init potential field with new elements from the current trajectory
            idsaLoader.InitPotentialField(this.getTrajectories());
            //reset finished
            this.finished = Boolean.FALSE;
            //set info for the time
            this.isNewTrajectory = Boolean.TRUE;
            this.lastTimeUsed = null;
        }
        //retrieve section form the trajectory
        List<Point> actualPoint = this.obtainSectionTrajectory(this.currentTrajectory);
        List<Point> pointWithTime = new ArrayList<>();
        for(int i = 0; i< actualPoint.size(); i++){
            Point actualSinglePoint = actualPoint.get(i);
            Point toAdd = null;
            if(i == 0 && this.isNewTrajectory){
                toAdd = new Point(actualSinglePoint.getLatitude(), actualSinglePoint.getLongitude(), actualSinglePoint.getAltitude(),0d, LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")), LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                this.lastTimeUsed = toAdd;
            }else{
                toAdd = new Point(actualSinglePoint.getLatitude(), actualSinglePoint.getLongitude(), actualSinglePoint.getAltitude(),0d, this.lastTimeUsed.addTimeToPoint(0.2), this.lastTimeUsed.addTimeToPoint(0.2));
                this.lastTimeUsed = toAdd;
            }
            pointWithTime.add(toAdd);
        }

        //hardcoded low bound for the size
        if(actualPoint.size() < 10){

            //need to find a new trajectory
            //it can happen only if the current one is finished
            this.finished = Boolean.TRUE;
            return this.feeder(idsaLoader);
        }

        //save points
        this.points = new ArrayList<>();
        pointWithTime.forEach(point -> this.points.add(point.deepCopy()));
        //compute the potential field for the actualPoint
        actualPoint.forEach(idsaLoader::compute);

        //return the list of input network
        return this.obtainInput(actualPoint, idsaLoader.returnAttraction(actualPoint.get(actualPoint.size() - 1)), idsaLoader.retPossibleTarget());
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

        InfoNode initialNode = null;
        //If I am using the db system
        if(this.db.getEnable()){
            //Check if I have already visited the node
            String id = this.db.readData(coordA.getLat(),coordA.getLon());
            //if it is null I have not visited it

            if(id == null){
                //i find the node
                initialNode = this.graph.findNodes(coordA);
                //add the node to the list
                this.db.insertData(coordA.getLat(),coordA.getLon(), initialNode.getId());
            }else{
                //I've already visited it, I can retrieve it from the db
                initialNode = this.graph.getNodeFromId(id);
            }
        }else{
            initialNode = this.graph.findNodes(coordA);
        }

        InfoNode closestNode = null;
        try {
            closestNode = this.getClosestNode(initialNode, whereIam, direction);
        }catch (Exception e){
            //some problems in finding the closest node?
            //If there is no closest node just return where I am
            return whereIam;
        }
        //distance in kilometers
        distance = distance / 1000;

        Double earthRadious = 6378.14;
        Double latRad = Math.toRadians(whereIam.getLatitude());
        Double lonRad = Math.toRadians(whereIam.getLongitude());
        Double bearRad = Math.toRadians(direction);

        Double lat2 = Math.asin(Math.sin(latRad) * Math.cos(distance/earthRadious) + Math.cos(latRad)*Math.sin(distance/earthRadious) * Math.cos(bearRad));
        Double long2 = lonRad + Math.atan2(Math.sin(bearRad)*Math.sin(distance/earthRadious)*Math.cos(latRad), Math.cos(distance/earthRadious)-Math.sin(latRad)*Math.sin(lat2));

        Double latDeg = Math.toDegrees(lat2);
        Double longDeg = Math.toDegrees(long2);

        Coord coordTest = new Coord(latDeg, longDeg);

        //Is the new Point inside the border of the area in interest?
        //need some test on loading the graph and see what is happening if I chose an external Point
        //in that case I should return the starting point.
        //Checked, In that case the closest node in the graph is returned and used. I think I can use this system in any case
        //or load a bigger map
        InfoNode testNode = this.graph.findNodes(coordTest);


        distance = distance * 1000;
        final Double[] dis = {0.0};
        List<InfoNode> list = null;
        try {
            list = this.graph.findPathBetweenNodes(closestNode, testNode);


            List<InfoNode> finalList = list;
            IntStream.range(1, list.size()).forEach(i -> dis[0] += this.graph.findDistanceBetweenNodesConnected(finalList.get(i-1), finalList.get(i)));

            Double dist = dis[0];

            //is possible that the distance is already shorter than distance
            if(dist > distance){
                Integer val = 1;
                //check the distance
                while (dist > distance){
                    final Double[] dis2 = {0.0};
                    IntStream.range(1, list.size() - val).forEach(i -> dis2[0] += this.graph.findDistanceBetweenNodesConnected(finalList.get(i-1), finalList.get(i)));
                    dist = dis2[0];
                    val++;
                }


                //now I am in the middle of two nodes and I need to find the right point at the right distance
                Double realDistance = distance - dist;
                Coord position = this.graph.findPointInEdge(finalList.get(list.size() - val), finalList.get(list.size() - val + 1), realDistance);
                Double plusTime = distance / speed;
                return new Point(position.getLat(), position.getLon(), whereIam.getAltitude(), whereIam.getDated(), whereIam.getDates(), whereIam.addTimeToPoint(plusTime));
            }
            Double plusTime = distance / speed;
            return new Point(finalList.get(list.size() - 1).getLat(), finalList.get(list.size() - 1).getLon(), whereIam.getAltitude(), whereIam.getDated(), whereIam.getDates(), whereIam.addTimeToPoint(plusTime));
        } catch (Exception e) {
            //If there is no path I am returning the first point, if there are other errors I am returning something different
            if(!Objects.equals(e.getMessage(), "No path is found in the graph")) {
                logger.log(Level.WARNING, "Error with " + e.getMessage());
                e.printStackTrace();
            }
        }
        //No path between the two points, returning the first point
        return whereIam;
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


    /**
     * Return a list of (train-control) trajectories
     * It calls {@link Feeder#feeder(IdsaLoader)} to obtain the single train trajectory and {@link Feeder#obtainRealAgentSectionTrajectory()}
     * to find the ral part of the trajectory
     *
     * @param idsaLoader reference IDSA system
     * @return list of {@link TrainReal} object
     * @throws Exception if there are problems with the config file or we reached the maximum number of trajectories usable
     */
    public List<TrainReal> multiFeeder(IdsaLoader idsaLoader) throws Exception {
        //need to load the number of trajectories given by settings
        List<TrainReal> totalList = new ArrayList<>();
        IntStream.range(0, ReadConfig.Configurations.getTrajectoriesTrained()).forEach(i -> {
            this.feedTheEater(idsaLoader,totalList);
        });

        //it is possible total list is not the right size. Check it
        while(totalList.size() < ReadConfig.Configurations.getTrajectoriesTrained()){
            this.feedTheEater(idsaLoader,totalList);
            logger.log(Level.INFO, "TrainReal sit size not correct, adding one more example");
        }
        return totalList;
    }


    /**
     * Feed the list of data.
     * It retrieves and translate the point into the correct input and it adds the real point to the
     * object used to store them
     * @param idsaLoader  reference IDSA system
     * @param totalList total list of {@link TrainReal}
     */
    private void feedTheEater(IdsaLoader idsaLoader, List<TrainReal> totalList){
        try {
            TrainReal tr = new TrainReal(this.feeder(idsaLoader),this.obtainRealAgentSectionTrajectory());
            tr.setPoints(this.points);
            //last splitting does not have the realsection, I am not adding it to the total list
            if(!tr.getFollowingPart().isEmpty()) totalList.add(tr);
            if(Objects.equals(ReadConfig.Configurations.getValueModel(), ReadConfig.Configurations.Convolution)) tr.setIdsaLoader(idsaLoader);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error in loading the trajectories" + e.getMessage());
        }
    }

    /**
     * Return all the edges of the graph
     * @return list of edges
     */
    public Set<InfoEdge> retAllEdges(){
        return this.graph.getEdgeSet();
    }

    /**
     * Return all the nodes of the graph
     * @return list of nodes
     */
    public Set<InfoNode> retAllNodes(){
        return this.graph.getNodesSet();
    }


}

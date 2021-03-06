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
import tgcfs.InputOutput.PointToSpeedSpeed;
import tgcfs.NN.InputsNetwork;
import tgcfs.Routing.Routes;
import tgcfs.Utils.PointWithBearing;
import tgcfs.Utils.RandomGenerator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    protected int position; //I need to remember the position where I am now
    protected boolean finished; //If the current trajectory is ended
    protected Trajectory currentTrajectory; //current trajectory under investigation
    private int maximumNumberOfTrajectories;
    private int actualNumberOfTrajectory;
    protected List<PointWithBearing> points;
    private final DatabaseCoordNode db; //database saving all the already visited nodes
    protected Boolean isNewTrajectory;
    protected Point lastTimeUsed;
    protected static Logger logger; //logger for this class
    protected double lastTime;

    /**
     * Constructor with zero parameter
     * Config file is read
     * The graph system and the trajectories system are initialised
     * @param log logger
     * @throws Exception Exception raised if there are problems with the files
     */
    public Feeder(Logger log) throws Exception{
        logger = log;
        logger.log(Level.INFO, "Initialising system...");
        this.graph = new Loader(logger);
        this.routes = new Routes(logger);
        this.position = 0;
        this.finished = Boolean.TRUE;
        this.actualNumberOfTrajectory = 0;
        this.maximumNumberOfTrajectories = ReadConfig.Configurations.getHowManyTrajectories();
        this.points = new ArrayList<>();
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
        boolean loadGraph = ReadConfig.Configurations.getConversionWithGraph();
        logger.log(Level.CONFIG, "Am I loading the graph? -> " + loadGraph);
        if (loadGraph) this.graph.loadGraph();
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
    private int selectPositionInTrajectory(Trajectory tra) throws Exception{
        //decide how many time to analise the trajectory
        int split = ReadConfig.Configurations.getHowManySplitting();
        if(split == 0) return tra.getSize();
        //need to check if the trajectory is shorter than the split number
        if(tra.getSize() <= split){
            split = 2; //I only split in half
        }
        if(ReadConfig.Configurations.getTrajectoriesType() == 5){
            split = 1;
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
    public int getMaximumNumberOfTrajectories() {
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
     * @param realPoints list real points
     * @return list of speeddirection objects
     */
    public List<InputsNetwork> obtainInput(List<Point> points, double attraction, Point possibleTarget, List<PointWithBearing> realPoints) throws Exception {
        //class that compute the conversion point -> speed/bearing
        PointToSpeedBearing conversion = new PointToSpeedBearing();

        //Am I using the time in the generator?
        boolean isDdsa = false;
        try {
            if(ReadConfig.Configurations.getTrajectoriesType() == 0 || ReadConfig.Configurations.getTrajectoriesType() == 3)
                isDdsa = true;
        } catch (Exception ignored) { }


        //total refactor of the method
        //this list has all the velocity and angular speed of the points
        List<InputsNetwork> totalList = new ArrayList<>();

        List<Double> allTheTimes = new ArrayList<>();

        //points updated with bearing
        List<PointWithBearing> updatedPoints = new ArrayList<>();

        //add the first point. No speed, no bearing and no space since it was still
        InputNetwork firstInputNetwork = new InputNetwork(attraction, 0d, 0d, 0d, 0d);
        firstInputNetwork.setTargetPoint(possibleTarget);
        totalList.add(firstInputNetwork);
        updatedPoints.add(new PointWithBearing(points.get(0), 0.0));

        double previousBearing = 0.0;
        for(int i = 1; i <  points.size(); i++){
            //bearing from this point to next point
            Point previousPoint = points.get(i - 1);
            Point actualPoint = points.get(i);

            double bearing = conversion.obtainBearing(previousPoint, actualPoint);
            updatedPoints.add(new PointWithBearing(points.get(i), bearing));


            //speed is the speed I arrived here from previous point
            PointToSpeedSpeed convertitor = new PointToSpeedSpeed();
            double time = 0.0;
            if(isDdsa) {
                time = Routes.timeBetweenIDSATimesteps;
            }else if (ReadConfig.isETH) {
                time = Routes.timeBetweenETHTimesteps;
            }else{
                time = conversion.obtainTime(previousPoint,actualPoint);
            }
            allTheTimes.add(time);

            double speed = conversion.obtainSpeed(previousPoint, actualPoint, time);

            double angularSpeed = convertitor.obtainAngularSpeed(previousBearing, bearing, time);
            logger.log(Level.FINER, "angularSpeed = " + angularSpeed);


//            double space = conversion.obtainDistance(previousPoint, actualPoint);

            InputNetwork inputNetwork = new InputNetwork(attraction, speed, bearing, time, angularSpeed);
            inputNetwork.setTargetPoint(possibleTarget);
            totalList.add(inputNetwork);
            previousBearing = bearing;
        }

        //update the points
        this.points.clear();
        this.points.addAll(updatedPoints);

        //If I am using the time I am going to shift all the time in the networks
        //TODO manage to add all the other times if I am going to use more than 1 timestep after the target
        int futureTimesteps = 1;
        try {
            futureTimesteps = ReadConfig.Configurations.getMoreTimeAhead();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(isDdsa) {
            this.lastTime = Routes.timeBetweenIDSATimesteps * futureTimesteps;
        }else if (ReadConfig.isETH) {
            this.lastTime = Routes.timeBetweenETHTimesteps * futureTimesteps;
        }else{
            this.lastTime = conversion.obtainTime(points.get(points.size() - 1), realPoints.get(0));
        }
        allTheTimes.add(lastTime);

        //check the time.
        //now maximum time allowed is 21 seconds
        if(allTheTimes.stream().anyMatch(d -> d > Normalisation.max_time)){
            throw new Exception("Trajectory with time bigger than the one allowed by normalisation methods");
        }


        logger.log(Level.INFO, allTheTimes.toString());

        List<InputsNetwork> hereNetwork = new ArrayList<>();
        int i = 0;
        for(InputsNetwork inputsNetwork: totalList){
            InputNetwork inputNetworkTime = new InputNetwork(inputsNetwork.serialise(), allTheTimes.get(i), ((InputNetwork)inputsNetwork).getAngularSpeed());
            hereNetwork.add(inputNetworkTime);
            i+=1;
        }

        totalList.clear();
        totalList = hereNetwork;

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
        int count = this.selectPositionInTrajectory(trajectory);
//        if(this.position > trajectory.getSize()) {
//            this.finished = Boolean.TRUE;
//            this.position = 0;
//        }
        try{
            for(int i = this.position; i < this.position + count; i++){
                Point p = this.getNextPoint(trajectory);
                //If it not null I add the element to the list
                if(p != null){
                    point.add(p);
                }else{
                    //if it is null I have finished the trajectory
                    this.finished = Boolean.TRUE;
                }
            }
        }catch (Exception e){
            this.finished = Boolean.TRUE;
            this.position = 0;
            throw new Exception(e);
        }
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
    public boolean getFinished() {
        return finished;
    }

    /**
     * Feeder method
     * If I have to use a new trajectory it load it
     * It obtains the section of the trajectory that I need right now
     * It translate the trajectory' points into the input format for the framework
     * @param idsaLoader reference IDSA system
     * @return {@link TrainReal} object with list of input formats
     * @throws Exception if there are problems with the config file or we reached the maximum number of trajectories usable
     */
    public TrainReal feeder(IdsaLoader idsaLoader) throws Exception {
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
        //TODO check this part for the point and the timing
        //retrieve section form the trajectory
        List<Point> actualPoint = this.obtainSectionTrajectory(this.currentTrajectory);


        List<Point> pointWithTime = new ArrayList<>();
        for(int i = 0; i< actualPoint.size(); i++){
            Point actualSinglePoint = actualPoint.get(i);
            Point toAdd;
            if(ReadConfig.Configurations.getTrajectoriesType() == 1 || ReadConfig.Configurations.getTrajectoriesType() == 4 || ReadConfig.Configurations.getTrajectoriesType() == 5){
                toAdd = new Point(actualSinglePoint.getLatitude(), actualSinglePoint.getLongitude(), actualSinglePoint.getAltitude(),actualSinglePoint.getDated(), actualSinglePoint.getDates(), actualSinglePoint.getTime());
                this.lastTimeUsed = toAdd;
            }else{
                if(i == 0 && this.isNewTrajectory){
                    toAdd = new Point(actualSinglePoint.getLatitude(), actualSinglePoint.getLongitude(), actualSinglePoint.getAltitude(),0d, LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")), LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                    this.lastTimeUsed = toAdd;
                }else{
                    toAdd = new Point(actualSinglePoint.getLatitude(), actualSinglePoint.getLongitude(), actualSinglePoint.getAltitude(),0d, this.lastTimeUsed.addTimeToPoint(0.2), this.lastTimeUsed.addTimeToPoint(0.2));
                    this.lastTimeUsed = toAdd;
                }
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
        //log the name of the trajectory
        logger.log(Level.SEVERE, "Loaded trajectory named: " + this.currentTrajectory.getPath());


        //save points
        this.points = new ArrayList<>();
        for(Point point: pointWithTime){
            this.points.add(new PointWithBearing(point.deepCopy()));
        }
        //compute the potential field for the actualPoint
        for(Point point: actualPoint){
            idsaLoader.compute(point);
        }

        //check how many points I want to analise
        int timesteps = ReadConfig.Configurations.getNumberOfTimestepConsidered();
        int futureTimeSteps = ReadConfig.Configurations.getAgentTimeSteps();
        int howManyTimestepsInFuture = ReadConfig.Configurations.getMoreTimeAhead();
        //if it is zero I do not care and use all the timesteps
        if(timesteps != 0){
            //keep only the timesteps that I need + the next ones
            while(actualPoint.size() > timesteps + futureTimeSteps * howManyTimestepsInFuture){
                //remove the first one till I reach the size I want
                actualPoint.remove(0);
            }

            //if howManyTimestepsInFuture bigger than one, I have to erase the point I do not need
            if (howManyTimestepsInFuture > 1){
                int startTimeStep = timesteps;
                for(int i = 0; i < futureTimeSteps; i ++ ) {
                    int endTimesteps = timesteps + i + howManyTimestepsInFuture;
                    int countTimesteps = endTimesteps;
                    while (countTimesteps > startTimeStep + futureTimeSteps) {
                        actualPoint.remove(startTimeStep);
                        countTimesteps -= 1;
                    }
                }
            }




        }

        //I have trajectory and I have current position.
        //I just need to retrieve next n position
        List<PointWithBearing> realPoint = new ArrayList<>();
        //If I have already the points, remove them from the list and place in the next point list
        if(actualPoint.size() == timesteps + futureTimeSteps){
            for(int i = 0; i < futureTimeSteps; i++){
                realPoint.add(new PointWithBearing(actualPoint.remove(actualPoint.size() - 1)));
            }
        }

        if(realPoint.size() < futureTimeSteps) {
            for (int i = this.position; i < this.position + futureTimeSteps; i++) {
                Point nextPoint = this.getNextPoint(this.currentTrajectory);
                if (nextPoint != null) {
                    realPoint.add(new PointWithBearing(nextPoint));
                } else {
                    throw new Exception("Next Point is missing");
                }
            }
        }


        //return the list of input network
        //obtain input transforms the points into inputnetworks
        List<InputsNetwork> inputsNetworks = this.obtainInput(actualPoint, idsaLoader.returnAttraction(actualPoint.get(actualPoint.size() - 1)), idsaLoader.retPossibleTarget(), realPoint);

        return new TrainReal(inputsNetworks, realPoint, this.lastTime);
    }

    /**
     * Method to return the real part of the trajectory under analysis for the real agent
     * @return the list of points
     * @throws Exception if there are problems with the conf
     */
    public List<PointWithBearing> obtainRealAgentSectionTrajectory() throws Exception {
        //I have trajectory and I have current position.
        //I just need to retrieve next n position
        List<PointWithBearing> realPoint = new ArrayList<>();
        for(int i = this.position; i < this.position + ReadConfig.Configurations.getAgentTimeSteps(); i++){
            Point nextPoint = this.getNextPoint(this.currentTrajectory);
            if (nextPoint != null){
                realPoint.add(new PointWithBearing(nextPoint));
            }
        }

        return realPoint;
    }


    /**
     * Find the next location given actual position, distance and direction
     * @param whereIam position where I am
     * @param speed speed I am moving
     * @param direction direction I am moving
     * @return next point
     */
    public Point getNextLocation(Point whereIam, Double speed, Double direction){
        //find position where I am
        Coord coordA = new Coord(whereIam.getLatitude(), whereIam.getLongitude());
        java.lang.System.out.println("coordA -> " + coordA.getLat().toString() + ", " + coordA.getLon().toString());

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

        java.lang.System.out.println("initialNode -> " + initialNode.getLat().toString() + ", " + initialNode.getLon().toString());
        InfoNode closestNode = null;
        try {
            closestNode = this.getClosestNode(initialNode, whereIam, direction);
            java.lang.System.out.println("closestNode -> " + closestNode.getLat().toString() + ", " + closestNode.getLon().toString());
        }catch (Exception e){
            //some problems in finding the closest node?
            //If there is no closest node just return where I am
            return whereIam;
        }

        //time fixed for idsa
        double time = Routes.timeBetweenIDSATimesteps;
        double distance = speed * time;
        java.lang.System.out.println("distance - > " + distance + ", speed is = " + speed + ", time is = " + time);


        //distance in kilometers
        distance = distance / 1000;
        java.lang.System.out.println("distance km - > " + distance);


        double earthRadious = 6378.14;
        double latRad = Math.toRadians(whereIam.getLatitude());
        double lonRad = Math.toRadians(whereIam.getLongitude());
        double bearRad = Math.toRadians(direction);

        double lat2 = Math.asin(Math.sin(latRad) * Math.cos(distance/earthRadious) + Math.cos(latRad)*Math.sin(distance/earthRadious) * Math.cos(bearRad));
        double long2 = lonRad + Math.atan2(Math.sin(bearRad)*Math.sin(distance/earthRadious)*Math.cos(latRad), Math.cos(distance/earthRadious)-Math.sin(latRad)*Math.sin(lat2));

        double latDeg = Math.toDegrees(lat2);
        double longDeg = Math.toDegrees(long2);

        Coord coordTest = new Coord(latDeg, longDeg);
        java.lang.System.out.println("coordTest - > " + coordTest.getLat().toString() + ", " + coordTest.getLon().toString());


        //Is the new Point inside the border of the area in interest?
        //need some test on loading the graph and see what is happening if I chose an external Point
        //in that case I should return the starting point.
        //Checked, In that case the closest node in the graph is returned and used. I think I can use this system in any case
        //or load a bigger map
        InfoNode testNode = this.graph.findNodes(coordTest);
        java.lang.System.out.println("testNode -> " + testNode.getLat().toString() + ", " + testNode.getLon().toString());


        //distance in metres
        distance = distance * 1000;
        final double[] dis = {0.0};
        List<InfoNode> list = null;
        try {
            java.lang.System.out.println(" (" + closestNode.getLat().toString() + " " + closestNode.getLon().toString()  + ")  -  (" + testNode.getLat().toString() + " " + testNode.getLon().toString() + ")");
            list = this.graph.findPathBetweenNodes(closestNode, testNode);


            List<InfoNode> finalList = list;
            for(int i = 1; i < list.size(); i++){
                dis[0] += this.graph.findDistanceBetweenNodesConnected(finalList.get(i-1), finalList.get(i));
            }

            double dist = dis[0];

            if(dist == 0){
                String aaa = "stop";
            }

            java.lang.System.out.println("dist = " + dist + ", vs distance = " + distance );
            //is possible that the distance is already shorter than distance
            if(dist > distance){
                int val = 1;
                //check the distance
                while (dist > distance){
                    final double[] dis2 = {0.0};
                    for(int i = 1; i < list.size(); i++){
                        dis2[0] += this.graph.findDistanceBetweenNodesConnected(finalList.get(i-1), finalList.get(i));
                    }
                    dist = dis2[0];
                    val++;
                }


                //now I am in the middle of two nodes and I need to find the right point at the right distance
                double realDistance = distance - dist;
                Coord position = this.graph.findPointInEdge(finalList.get(list.size() - val), finalList.get(list.size() - val + 1), realDistance);
                double plusTime = distance / speed;
                return new Point(position.getLat(), position.getLon(), whereIam.getAltitude(), whereIam.getDated(), whereIam.getDates(), whereIam.addTimeToPoint(plusTime));
            }
            double plusTime = distance / speed;
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
     * Find the next location given actual position, speed and direction but in a different way than the method before
     * @param whereIam position where I am
     * @param speed speed I am moving
     * @param direction direction I am moving
     * @return next point
     */
    public Point getNextLocationDifferentMethod(Point whereIam, Double speed, Double direction){
        //I know where I am
        Coord coordWhereIam = new Coord(whereIam.getLatitude(), whereIam.getLongitude());
        java.lang.System.out.println("Where I am -> " + coordWhereIam.getLat() + ", " + coordWhereIam.getLon());
        InfoNode source = this.graph.findNodes(coordWhereIam);
        java.lang.System.out.println("Source -> " + source.getLat() + ", " + source.getLon());

        //find position where I am going using speed and direction
        //time fixed for idsa
        double time = Routes.timeBetweenIDSATimesteps;
        double distance = speed * time;

        //distance in kilometers
        distance = distance / 1000;

        //compute next point where I am going in the plane (not in the graph)
        double earthRadious = 6378.14;
        double latRad = Math.toRadians(whereIam.getLatitude());
        double lonRad = Math.toRadians(whereIam.getLongitude());
        double bearRad = Math.toRadians(direction);

        double lat2 = Math.asin(Math.sin(latRad) * Math.cos(distance/earthRadious) + Math.cos(latRad)*Math.sin(distance/earthRadious) * Math.cos(bearRad));
        double long2 = lonRad + Math.atan2(Math.sin(bearRad)*Math.sin(distance/earthRadious)*Math.cos(latRad), Math.cos(distance/earthRadious)-Math.sin(latRad)*Math.sin(lat2));

        double latDeg = Math.toDegrees(lat2);
        double longDeg = Math.toDegrees(long2);

        Coord nextPoint = new Coord(latDeg, longDeg);
        java.lang.System.out.println("nextPoint - > " + nextPoint.getLat().toString() + ", " + nextPoint.getLon().toString());

        //now I can try to find this point in the graph
        InfoNode destination = this.graph.findNodes(nextPoint);
        java.lang.System.out.println("Destination -> " + destination.getLat() + ", " + destination.getLon());


        //distance in metres again
        distance = distance * 1000;
        List<InfoNode> list = null;
        try {
            list = this.graph.findPathBetweenNodes(source, destination);

            //check distance of the path
            double distanceFollowingThePath = 0d;
            for (int i = 1; i < list.size(); i++){
                distanceFollowingThePath += this.graph.findDistanceBetweenNodesConnected(list.get(i-1), list.get(i));
            }

            java.lang.System.out.println("distanceFollowingThePath = " + distanceFollowingThePath + ", vs distance = " + distance );

            //is possible that the distance is already shorter than distance
            if(distanceFollowingThePath > distance){
                int val = 1;
                //check the distance
                while (distanceFollowingThePath > distance){
                    distanceFollowingThePath = 0d;
                    for (int i = 1; i < list.size() - val; i++){
                        distanceFollowingThePath += this.graph.findDistanceBetweenNodesConnected(list.get(i-1), list.get(i));
                    }
                    val++;
                }


                //now I am in the middle of two nodes and I need to find the right point at the right distance
                double realDistance = distance - distanceFollowingThePath;
                Coord position = this.graph.findPointInEdge(list.get(list.size() - val), list.get(list.size() - val + 1), realDistance);
                double plusTime = distance / speed;
                return new Point(position.getLat(), position.getLon(), whereIam.getAltitude(), whereIam.getDated(), whereIam.getDates(), whereIam.addTimeToPoint(plusTime));
            }
            double plusTime = distance / speed;
            return new Point(list.get(list.size() - 1).getLat(), list.get(list.size() - 1).getLon(), whereIam.getAltitude(), whereIam.getDated(), whereIam.getDates(), whereIam.addTimeToPoint(plusTime));
        } catch (Exception e) {
            //If there is no path I am returning the first point, if there are other errors I am returning something different
            if (!Objects.equals(e.getMessage(), "No path is found in the graph")) {
                logger.log(Level.WARNING, "Error with " + e.getMessage());
                e.printStackTrace();
            }
        }
        //No path between the two points, returning the first point
        return whereIam;
    }

    /**
     * Find the next location given actual position, speed and direction but not using the graph
     * @param whereIam position where I am
     * @param speed speed I am moving
     * @param direction direction I am moving
     * @return next point
     */
    public Point getNextLocationNoGraph(Point whereIam, Double speed, Double direction){
        //I know where I am
        //find position where I am going using speed and direction
        //time fixed for idsa
        double time = Routes.timeBetweenIDSATimesteps;
        return this.getNextLocation(whereIam, speed, direction, time);
    }

    /**
     * Find the next location given actual position, speed and direction but not using the graph
     * @param whereIam position where I am
     * @param speed speed I am moving
     * @param direction direction I am moving
     * @param time time given by output
     * @return next point
     */
    public Point getNextLocationNoGraph(Point whereIam, Double speed, Double direction, double time){
        //I know where I am
        //find position where I am going using speed and direction
        return this.getNextLocation(whereIam, speed, direction, time);
    }


    private Point getNextLocation(Point whereIam, double speed, double direction, double time){
        if (speed == 0.0) return whereIam;
        double distance = speed * time;

        //distance in kilometers
        distance = distance / 1000;

        //compute next point where I am going in the plane (not in the graph)
        double earthRadious = 6378.14;
        double latRad = Math.toRadians(whereIam.getLatitude());
        double lonRad = Math.toRadians(whereIam.getLongitude());
        double bearRad = Math.toRadians(direction);

        double lat2 = Math.asin(Math.sin(latRad) * Math.cos(distance/earthRadious) + Math.cos(latRad)*Math.sin(distance/earthRadious) * Math.cos(bearRad));
        double long2 = lonRad + Math.atan2(Math.sin(bearRad)*Math.sin(distance/earthRadious)*Math.cos(latRad), Math.cos(distance/earthRadious)-Math.sin(latRad)*Math.sin(lat2));

        double latDeg = Math.toDegrees(lat2);
        double longDeg = Math.toDegrees(long2);

        distance = distance * 1000;
        double plusTime = distance / speed;
        return new Point(latDeg, longDeg, whereIam.getAltitude(), whereIam.getDated(), whereIam.getDates(), whereIam.addTimeToPoint(plusTime));
    }


    private Point getNextLocationPlane(Point whereIam, double speed, double direction, double time){
        if (speed == 0.0) return whereIam;
        double distance = speed * time;

        //x1=x+ncosθ
        //y1=y+nsinθ
        double x = whereIam.getLatitude() + distance * Math.cos(direction);
        double y = whereIam.getLongitude() + distance * Math.sin(direction);
//        if(whereIam.getLatitude() < 0) x = -x;
//        if(whereIam.getLongitude() < 0) y = -y;
        return new Point(x, y, whereIam.getAltitude(), whereIam.getDated(), whereIam.getDates(), whereIam.addTimeToPoint(time));
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
        for(InfoNode node: endNodes){
            angles.add(IamHere.angleWith(node.getCoord()));
        }
        //find id closest ending node
        final int[] index = {0};
        final double[] minDifference = {Double.MAX_VALUE};
        for(int i = 0; i < angles.size(); i++){
            Double difference = Math.abs(Normalisation.fromHalfPItoTotalPI(direction) - Normalisation.fromHalfPItoTotalPI(angles.get(i)));
            if(difference < minDifference[0]){
                minDifference[0] = difference;
                index[0] = i;
            }
        }
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
     * @param oldList old list of input that we need to keep
     * @return list of {@link TrainReal} object
     * @throws Exception if there are problems with the config file or we reached the maximum number of trajectories usable
     */
    public List<TrainReal> multiFeeder(IdsaLoader idsaLoader, List<TrainReal> oldList) throws Exception {

        //need to load the number of trajectories given by settings
        List<TrainReal> totalList = new ArrayList<>();
        int start = 0;

        if(oldList != null) {
            //for now I am keeping half of the old trajectories, random selectionsystem.
            int total = oldList.size();
            int percent = (int)((total / 100f) * ReadConfig.Configurations.getHowManyAmIChangingBetweenGeneration());
            int kept = total - percent;
            while (oldList.size() > kept) {
                int rand = RandomGenerator.getNextInt(0, oldList.size());
                oldList.remove(rand);
            }
            //now the old list contains only the one I want to be there

            //deep copy so i reset their status on the new list
            for(TrainReal trainReal: oldList){
                TrainReal newOne = trainReal.softCopy();
                try {
                    newOne.setPoints(trainReal.getPoints());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                totalList.add(newOne);
            }
            start = totalList.size();
        }



        for(int i = start; i < ReadConfig.Configurations.getTrajectoriesTrained(); i++){
            this.feedTheEater(idsaLoader,totalList);
        }

        //it is possible total list is not the right size. Check it
        while(totalList.size() < ReadConfig.Configurations.getTrajectoriesTrained()){
            this.feedTheEater(idsaLoader,totalList);
            //logger.log(Level.INFO, "TrainReal sit size not correct, adding one more example");
        }

        for(TrainReal tr: totalList){
            //create the output already computed
            tr.createRealOutputConverted();
            logger.log(Level.FINE, tr.getId() + " Real point transformed ->" + tr.getRealOutput().toString());
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
            //this.feeder
            //It obtains the section of the trajectory that I need right now
            // It translate the trajectory' points into the input format for the framework
            TrainReal tr = this.feeder(idsaLoader);
            tr.setPoints(this.points);
            //last splitting does not have the realsection, I am not adding it to the total list
            if(!tr.getFollowingPart().isEmpty()) totalList.add(tr);
            if(Objects.equals(ReadConfig.Configurations.getValueModel(), ReadConfig.Configurations.Convolution)) tr.setIdsaLoader(idsaLoader);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error in loading the trajectories :" + e.getMessage());
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



    public void createDatasetWithTrajectories(IdsaLoader idsaLoader){

    }


}

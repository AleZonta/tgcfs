package tgcfs.Loader;

import gms.GraphML.InfoEdge;
import gms.GraphML.InfoNode;
import gms.Loader;
import gms.Point.Coord;
import lgds.Distance.Distance;
import lgds.trajectories.Point;
import lgds.trajectories.Trajectory;
import tgcfs.Classifiers.InputNetwork;
import tgcfs.Config.ReadConfig;
import tgcfs.Routing.Routes;

import java.util.ArrayList;
import java.util.List;
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
    private final Loader graph; //loader of the graph
    private final Routes routes; //loader of the trajectories
    private final ReadConfig conf; //configuration object containing location where to read the graph
    private static final Logger logger = Logger.getLogger(Feeder.class.getName()); //logger for this class

    /**
     * Constructor with zero parameter
     * Config file is read
     * The graph system and the trajectories system are initialised
     * @throws Exception Exception raised if there are problems with the files
     */
    public Feeder() throws Exception{
        logger.log(Level.INFO, "Initialising system...");
        this.conf = new ReadConfig();
        this.conf.readFile();
        this.graph = new Loader();
        this.routes = new Routes(this.conf);
    }

    /**
     * Loading trajectories and graph system.
     * @throws Exception Exception raised if there are problems with the files
     */
    public void loadSystem() throws Exception {
        logger.log(Level.INFO, "Loading system...");
        this.routes.readTrajectories();
        this.graph.loadGraph();
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
        Integer split = this.conf.getHowManySplitting() + 1;
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
        return this.routes.getNextTrajectory();
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
    public List<InputNetwork> obtainInput(List<Point> points){
        List<InputNetwork> totalList = new ArrayList<>();
        for(int i = 0; i < points.size() - 1; i++){
            //bearing from this point to next point
            Point actualPoint = points.get(i);
            Point nextPoint = points.get(i+1);
            Double bearing = this.bearing(actualPoint.getLatitude(), actualPoint.getLongitude(), nextPoint.getLatitude(), nextPoint.getLongitude());
            //speed is the speed I arrived here from previous point
            Double speed;
            if(i > 0){
                Point previousPoint = points.get(i - 1);
                //speed = distance / time
                Distance dis = new Distance();
                Double distance = dis.compute(previousPoint, actualPoint);
                Double time = 0.2; //TODO check this element
                try {
                    time = new Double(actualPoint.differenceInTime(previousPoint));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                speed = distance / time;
            }else{
                speed = 0.0;
            }
            totalList.add(new InputNetwork(speed, bearing));
        }
        return totalList;
    }


    /**
     * Compute the bearing between two points
     * @param lat1 latitude first point
     * @param lon1 longitude first point
     * @param lat2 latitude second point
     * @param lon2 longitude second point
     * @return Double value indicating the bearing
     */
    private Double bearing(Double lat1, Double lon1, Double lat2, Double lon2){
        Double latitude1 = Math.toRadians(lat1);
        Double latitude2 = Math.toRadians(lat2);
        Double longDiff= Math.toRadians(lon2 - lon1);
        Double y= Math.sin(longDiff)*Math.cos(latitude2);
        Double x=Math.cos(latitude1)*Math.sin(latitude2)-Math.sin(latitude1)*Math.cos(latitude2)*Math.cos(longDiff);

        return (Math.toDegrees(Math.atan2(y, x))+360)%360;
    }

}

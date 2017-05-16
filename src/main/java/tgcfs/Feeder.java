package tgcfs;

import gms.Loader;
import tgcfs.Routing.Routes;

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
    private static final Logger logger = Logger.getLogger(Feeder.class.getName()); //logger for this class

    /**
     * Constructor with zero parameter
     * The graph system and the trajectories system are initialised
     * @throws Exception Exception raised if there are problems with the files
     */
    public Feeder() throws Exception{
        logger.log(Level.INFO, "Initialising system...");
        this.graph = new Loader();
        this.routes = new Routes();
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


}

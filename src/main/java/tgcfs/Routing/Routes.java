package tgcfs.Routing;

import lgds.load_track.LoadIDSATrack;
import lgds.load_track.LoadTrack;
import lgds.load_track.Traces;
import lgds.trajectories.Point;
import lgds.trajectories.Trajectories;
import lgds.trajectories.Trajectory;
import tgcfs.Config.ReadConfig;

import java.util.concurrent.ThreadLocalRandom;
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
 *
 *
 * This class will load the trajectories from file using lgds
 */
public class Routes {
    private Traces storage; //class that loads the track from file
    private Trajectories tra; //keep track of all the trajectories
    private static final Logger logger = Logger.getLogger(Routes.class.getName()); //logger for this class
    private static int number; //number of trajectories analysed
    public static double timeBetweenIDSATimesteps = 0.2;

    /**
     * Constructor with one parameter
     */
    public Routes(){
        number = 0;
    }


    /**
     * Method that loads trajectories from file.
     * It is reading from a config file which type of trajectories I want.
     * After the loading phase it shuffle them and call the method to analise them (to eliminate the shorter one)
     * @throws Exception Loading a trajectory can raise an exception
     */
    public void readTrajectories() throws Exception{
        logger.log(Level.INFO, "Loading Trajectories...");
        if (ReadConfig.Configurations.getTrajectoriesType() == 0){
            this.storage = new LoadIDSATrack();
        }else{
            this.storage = new LoadTrack();
        }
        //retrieve all the tracks from file
        this.tra = this.storage.loadTrajectories();
        logger.log(Level.INFO, "Trajectories Loaded");
        //shuffle it
        this.tra.shuffle();
        //analysing the trajectories
        //this.tra.analiseAndCheckTrajectory();

    }

    /**
     * Return the trajectories loaded
     * @return Trajectories object
     */
    public Trajectories getTra() {
        return this.tra;
    }


    /**
     * Return next trajectory to analise
     * @return trajectory
     */
    public Trajectory getNextTrajectory(){
        Trajectory trajectory = null;
        //check if the number is greater than the size of trajectories
        if (number >= this.tra.getTrajectories().size()){
            Integer rand = ThreadLocalRandom.current().nextInt(0, this.tra.getTrajectories().size());
            //get Trajectory
            trajectory = this.tra.getTrajectories().get(rand);
        }else{
            //get Trajectory
            trajectory = this.tra.getTrajectories().get(number);
        }
        //increase the number
        number++;


        return trajectory;
    }

    /**
     * Return next GPS coordinate of the Trajectory
     * @param trajectory trajectory object from where retrieve next position
     * @return next coordinate of the trajectory
     */
    public Point getNextPosition(Trajectory trajectory){
       return trajectory.getNextPoint(this.storage);
    }

}

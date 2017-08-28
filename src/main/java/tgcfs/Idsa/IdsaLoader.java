package tgcfs.Idsa;

import lgds.trajectories.Point;
import lgds.trajectories.Trajectories;
import lgds.trajectories.Trajectory;
import nl.tno.idsa.framework.config.ConfigFile;
import nl.tno.idsa.framework.potential_field.PotentialField;
import nl.tno.idsa.framework.potential_field.points_of_interest.POI;
import nl.tno.idsa.framework.potential_field.save_to_file.LoadParameters;
import nl.tno.idsa.framework.simulator.TrajectorySim;
import nl.tno.idsa.framework.world.World;
import org.json.simple.parser.ParseException;
import tgcfs.Performances.SaveToFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Created by Alessandro Zonta on 23/05/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 *
 * This class will load IDSA project and use the method that is offering to compute the potential field
 */
public class IdsaLoader {
    private static final Logger logger = Logger.getLogger(IdsaLoader.class.getName()); //logger for this class
    private PotentialField pot; //This is the base instance of the pot
    private ConfigFile configFile; //file containing configuration for IDSA
    private LoadParameters par; //parameter for potential field
    private Boolean computed; //I can obtain direction only if I computed it before it

    /**
     * Constructor that loads all the configurations for the potential field
     * @throws IOException Error in reading the file
     * @throws ParseException Error in parsing the file
     */
    public IdsaLoader() throws IOException, ParseException {
        logger.log(Level.INFO, "Loading PF Config...");
        //loading config file for Idsa
        this.configFile = new ConfigFile();
        this.configFile.loadFile();
        SaveToFile.Saver.dumpSetting(this.configFile);
        //load parameter for Potential Field
        //the name of the file is hardcoded
        this.par = new LoadParameters("/par", this.configFile.getSelectPerson(), this.configFile.getSelectUR());
        //set update rule read by parameter files
        this.configFile.setUpdateRules(par.getUpdateRule());
        this.computed = Boolean.FALSE;
        logger.log(Level.INFO, "Config PF Loaded.");
    }

    /**
     * Constructor that loads all the configurations for the potential field
     * It is also checking if the number set as maximum number of trajectory is the same that are in the IDSA config
     * If not is overriding that number
     * @param totalTrack total number of trajectories tested
     * @throws IOException Error in reading the file
     * @throws ParseException Error in parsing the file
     */
    public IdsaLoader(Integer totalTrack) throws IOException, ParseException {
        logger.log(Level.INFO, "Loading PF Config...");
        //loading config file for Idsa
        this.configFile = new ConfigFile();
        this.configFile.loadFile();
        //load parameter for Potential Field
        //the name of the file is hardcoded
        this.par = new LoadParameters("/par", this.configFile.getSelectPerson(), this.configFile.getSelectUR());
        //set update rule read by parameter files
        this.configFile.setUpdateRules(par.getUpdateRule());
        //check If the number of trajectory requested from the framework is the same requested for the potential field
        //the number has to be the same for the generation of POIs
        if(!Objects.equals(totalTrack, this.configFile.getMaxNumberOfTrackedPeople())) {
            logger.log(Level.WARNING, "Number of total trajectories does not fit. Overriding config file (" + this.configFile.getMaxNumberOfTrackedPeople().toString() + ") with " + totalTrack.toString());
            this.configFile.setMaxNumberOfTrackedPeople(totalTrack);
        }
        logger.log(Level.INFO, "Config PF Loaded.");
    }


    /**
     * Function that loads the potential field.
     * It creates a fake World with the dimension from the trajectories loaded.
     * Then it computes the POI from the subset of trajectory considered
     * @param trajectories trajectory object containing all the trajectory
     */
    public void InitPotentialField(Trajectories trajectories){

        //I need the class world. But I load it empty and I pass only the data I need
        World world = new World();

        world.applyGeoRoot(trajectories.getUtmRoot().getLatitude(),trajectories.getUtmRoot().getLongitude(),trajectories.getWhWorld().getLatitude(),trajectories.getWhWorld().getLongitude());
        logger.log(Level.FINE, "Loading Potential Field...");
        this.pot = new PotentialField(world, this.configFile, this.par.getH(), this.par.getZ1(), this.par.getZ2(), this.par.getS2(), this.par.getW2(), this.par.getName(), this.par.getExperiment());

        logger.log(Level.INFO, "Potential Field Online.");
        //what About the POI for the potential Field?
        //I should generate all the one I have it and then pick it up
        logger.log(Level.FINE, "Computing POIs...");
        Boolean morePOIsInTotal = Boolean.FALSE;
        if(this.configFile.getMorePOIs() != 0){
            //also negative numbers are okay, not too negative though
            morePOIsInTotal = Boolean.TRUE;
        }
        List<Trajectory> actualBigTrajectories = trajectories.getTrajectories().stream().limit(this.configFile.getMaxNumberOfTrackedPeople()).collect(Collectors.toList());
        trajectories.computePOIs(actualBigTrajectories, morePOIsInTotal, this.configFile.getMorePOIs());

        List<POI> pois;
        //check if I clustered the POI
        if(this.configFile.getPOIsAreClustered()){
            //they are clustered
            pois = TrajectorySim.translateClusterPOI(trajectories.getListOfPOIsClustered());
        }else{
            //they are not clustered
            pois = TrajectorySim.translatePOI(trajectories.getListOfPOIs());
        }
        //if morePois is smaller than zero i need to eliminate some of them
        if (this.configFile.getMorePOIs() < 0) {
            Integer numberToRemove = Math.abs(this.configFile.getMorePOIs());

            if(pois.size() < numberToRemove){
                numberToRemove = numberToRemove - pois.size();
            }

            for(int i = 0; i < numberToRemove; i ++ ){
                Random rn = new Random();
                int numb = rn.nextInt(pois.size());
                pois.remove(numb);
                //TODO remember to check If I have the end point of the trajectory in the group of POI
            }
        }
        this.pot.setPointsOfInterest(pois);
        this.pot.setPreviousPoint(new nl.tno.idsa.framework.world.Point(0.0,0.0));
        logger.log(Level.FINE, "POIs are loaded.");
    }

    /**
     * Function that loads the potential field.
     * It creates a fake World with the dimension from the trajectories loaded.
     * Then it computes the POI from the subset of trajectory considered
     * it checks if the trajectory loaded has the end point in the subset of POIS
     * @param trajectory used to check if the endPoint is there
     */
    public void InitPotentialField(Trajectory trajectory){
        nl.tno.idsa.framework.world.Point endPoint = new nl.tno.idsa.framework.world.Point(trajectory.getLastPoint().getLatitude(),trajectory.getLastPoint().getLongitude());
        Boolean isPresent = this.pot.getPointsOfInterest().stream().anyMatch(poi -> poi.contains(endPoint));
        if(isPresent){
            this.pot.getPointsOfInterest().add(new POI(endPoint));
        }
        this.pot.setPreviousPoint(new nl.tno.idsa.framework.world.Point(0.0,0.0));
    }


    /**
     * Method that updates the potential field
     * @param point current position
     */
    public void compute(Point point){
        //update the potential field
        //need to convert Point from lgds to idsa
        nl.tno.idsa.framework.world.Point translatedPoint = new nl.tno.idsa.framework.world.Point(point.getLatitude(), point.getLongitude());
        this.pot.trackAndUpdate(translatedPoint);
        this.computed = Boolean.TRUE;
    }


    /**
     * Retrieve direction attraction of the Potential Field
     * @return Double value meaning the direction
     * @throws Exception exception is raised if the apf is not computed
     */
    public Double returnAttraction(Point lastPoint) throws Exception {
        if(!this.computed) throw new Exception("The computation of the APF is needed to retrieve the direction of attraction");
        this.computed = Boolean.FALSE;
        return this.pot.returnDirectionAttraction(new nl.tno.idsa.framework.world.Point(lastPoint.getLatitude(), lastPoint.getLongitude()));
    }


    /**
     * Reset the APF setting al the POIs' charge to zero
     */
    public void resetAPF(){
        this.pot.resetPOIs();
        this.pot.setPreviousPoint(new nl.tno.idsa.framework.world.Point(0.0,0.0));
    }


    /**
     * Return the possible target computed with the IDSA framework
     * @return center point of the target
     * @throws Exception if the potential field is not loaded
     */
    public Point retPossibleTarget() throws Exception {
        nl.tno.idsa.framework.world.Point p = this.pot.retPossibleTarget();
        return new Point(p.getX(), p.getY());
    }

    /**
     * Generate the picture using the python connection in IDSA
     * @param points current trajectory
     * @return boolean value stating the success or not of the conversion
     */
    public Boolean generatePicture(List<Point> points) throws IOException {
        List<nl.tno.idsa.framework.world.Point> tra = new ArrayList<>();
        points.forEach(p -> tra.add(new nl.tno.idsa.framework.world.Point(p.getLatitude(), p.getLongitude())));
        return this.pot.generatePictureWithPython(tra);
    }

}

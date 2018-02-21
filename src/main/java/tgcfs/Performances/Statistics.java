package tgcfs.Performances;

import java.util.UUID;

/**
 * Created by Alessandro Zonta on 21/02/2018.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 */
public class Statistics {
    private double distance;
    private double bearing;
    private UUID idTra;

    /**
     * Contructor two parameters
     * @param distance double var for the distance
     * @param bearing double var for the bearing difference
     * @param uuid id trajectory
     */
    public Statistics(double distance, double bearing, UUID uuid){
        this.bearing = bearing;
        this.distance = distance;
        this.idTra = uuid;
    }

    /**
     * To string override
     * @return print the two values
     */
    @Override
    public String toString() {
        return "{ Trajectory: " + this.idTra.toString() + " DistanceBetweenPoints: " + distance + "; DifferenceInBearing: " + bearing + " }";
    }
}

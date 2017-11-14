package tgcfs.Utils;

import lgds.trajectories.Point;

/**
 * Created by Alessandro Zonta on 11/10/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 *
 * extending point with the bearing of the point
 */
public class PointWithBearing extends Point {
    private double bearing;

    /**
     * Constructor
     * @param latitude latitude point
     * @param longitude longitude point
     * @param altitude altitude point
     * @param dated date of the point in double
     * @param dates date of the point in string
     * @param time time of the point
     * @param bearing bearing of the point
     */
    public PointWithBearing(double latitude, double longitude, double altitude, double dated, String dates, String time, double bearing) {
        super(latitude, longitude, altitude, dated, dates, time);
        this.bearing = bearing;
    }

    /**
     * Constructor
     * @param latitude latitude point
     * @param longitude longitude point
     * @param bearing bearing of the point
     */
    public PointWithBearing(double latitude, double longitude, double bearing) {
        super(latitude, longitude);
        this.bearing = bearing;
    }

    /**
     * Constructor
     * @param latitude latitude point
     * @param longitude longitude point
     */
    public PointWithBearing(double latitude, double longitude) {
        super(latitude, longitude);
        this.bearing = 0.0;
    }

    /**
     * Constructor
     * @param point {@link Point} point object
     * @param bearing bearing of the point
     */
    public PointWithBearing(Point point, double bearing){
        super(point.getLatitude(), point.getLongitude(), point.getAltitude(), point.getDated(), point.getDates(), point.getTime());
        this.bearing = bearing;
    }


    /**
     * Constructor
     * @param point {@link Point} point object
     */
    public PointWithBearing(Point point){
        super(point.getLatitude(), point.getLongitude(), point.getAltitude(), point.getDated(), point.getDates(), point.getTime());
        this.bearing = 0.0;
    }

    /**
     * Getter for the bearing
     * @return
     */
    public double getBearing() {
        return this.bearing;
    }


    @Override
    public PointWithBearing deepCopy() {
        return new PointWithBearing(this.getLatitude(),this.getLongitude(), this.getAltitude(), this.getDated(), this.getDates(), this.getTime(), this.bearing);
    }


    @Override
    public String toString() {
        return "(" + this.getLatitude() + ", " + this.getLongitude()  + ", " + this.bearing+ ")";
    }
}

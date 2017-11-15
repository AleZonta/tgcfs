package tgcfs.InputOutput;

import lgds.Distance.Distance;
import lgds.trajectories.Point;
import tgcfs.Config.ReadConfig;
import tgcfs.Routing.Routes;

/**
 * Created by Alessandro Zonta on 31/05/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 */
public class PointToSpeedBearing {

    /**
     * Compute speed between two points
     * @param firstPoint first position
     * @param secondPoint second position
     * @return speed of the movement between the two points
     */
    public double obtainSpeed(Point firstPoint, Point secondPoint){
        //speed = distance / time
        Distance dis = new Distance();
        double time = Routes.timeBetweenIDSATimesteps; //fixed value for IDSA (checked on IDSA)
        double distance = dis.compute(firstPoint, secondPoint);
        try {
            if (ReadConfig.Configurations.getTrajectoriesType() != 0) {
                try {
                    time = new Double(secondPoint.differenceInTime(firstPoint));
                } catch (Exception e) {
                    //I do not have time, so time will be the one I set before. I do not print the stack trace
                    //e.printStackTrace();
                }
            }
        }catch (Exception e) {
            //e.printStackTrace();
        }

        return distance / time;
    }

    /**
     * Compute the bearing between two points
     * @param firstPoint first position
     * @param secondPoint second position
     * @return bearing of the movement
     */
    public double obtainBearing(Point firstPoint, Point secondPoint){
        return Math.toDegrees(Math.atan2(firstPoint.getLatitude() - secondPoint.getLatitude(), firstPoint.getLongitude() - secondPoint.getLongitude()));
    }


    /**
     * Compute distance between two points
     * @param firstPoint first position
     * @param secondPoint second position
     * @return distance
     */
    public double obtainDistance(Point firstPoint, Point secondPoint){
        //speed = distance / time
        Distance dis = new Distance();
        return dis.compute(firstPoint, secondPoint);
    }

    /**
     * Compute the bearing between two points
     * @param lat1 latitude first point
     * @param lon1 longitude first point
     * @param lat2 latitude second point
     * @param lon2 longitude second point
     * @return Double value indicating the bearing
     */
    private double bearing(double lat1, double lon1, double lat2, double lon2){
        double latitude1 = Math.toRadians(lat1);
        double latitude2 = Math.toRadians(lat2);
        double longDiff= Math.toRadians(lon2 - lon1);
        double y= Math.sin(longDiff)*Math.cos(latitude2);
        double x=Math.cos(latitude1)*Math.sin(latitude2)-Math.sin(latitude1)*Math.cos(latitude2)*Math.cos(longDiff);

        return (Math.toDegrees(Math.atan2(y, x))+360)%360;
    }







}

package tgcfs.InputOutput;

import tgcfs.Routing.Routes;
import tgcfs.Utils.PointWithBearing;

/**
 * Created by Alessandro Zonta on 11/10/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 */
public class PointToSpeedSpeed {

    /**
     * Obtain angular speed current second point
     * @param previousPoint previousPoint
     * @param actualBearing bearing just computed of last point
     * @return double value with speed
     */
    public double obtainAngularSpeed(PointWithBearing previousPoint, double actualBearing){
        double previousBearing = previousPoint.getBearing();
        return this.obtainAngularSpeed(previousBearing,actualBearing);
    }

    /**
     * Obtain angular speed current second point
     *
     * https://gamedev.stackexchange.com/questions/4467/comparing-angles-and-working-out-the-difference
     *
     *
     * @param previousBearing previous bearing
     * @param actualBearing bearing just computed of last point
     * @return double value with speed
     */
    public double obtainAngularSpeed(double previousBearing, double actualBearing){
        double time = Routes.timeBetweenIDSATimesteps;

        double angle = 180 - Math.abs(Math.abs(previousBearing - actualBearing) - 180);

        return Math.toRadians(angle) / time;
    }
}

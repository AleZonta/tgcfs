package tgcfs.InputOutput;

import lgds.trajectories.Point;
import org.junit.Test;
import tgcfs.Utils.PointWithBearing;

import static junit.framework.TestCase.assertNotNull;

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
public class PointToSpeedBearingTest {
    @Test
    public void obtainDistance() throws Exception {
        Point start = new Point(52.320961, 4.869281, 0.0, 12345.6, "123456", "14:20:10");
        Point end = new Point(52.324658, 4.869103, 0.0, 12345.6, "123456", "14:25:00");
        PointToSpeedBearing convertitor = new PointToSpeedBearing();
        Double result = convertitor.obtainDistance(start, end);
        assertNotNull(result);
        System.out.println(result);
    }

    @Test
    public void obtainSpeed() throws Exception {
        Point start = new Point(52.320961, 4.869281, 0.0, 12345.6, "123456", "14:20:10");
        Point end = new Point(52.324658, 4.869103, 0.0, 12345.6, "123456", "14:25:00");
        PointToSpeedBearing convertitor = new PointToSpeedBearing();
        Double result = convertitor.obtainSpeed(start, end);
        assertNotNull(result);
        System.out.println(result);
        //I obtain metres per second
    }

    @Test
    public void obtainBearing() throws Exception {
        Point start = new Point(52.103683, 4.317695, 10.0, 123245.6, "1223456", "14:20:10");
        Point end = new Point(52.100625, 4.321214, 10.0, 123425.6, "1234256", "14:25:00");
        PointToSpeedBearing convertitor = new PointToSpeedBearing();
        double result = convertitor.obtainBearing(start, end);
        assertNotNull(result);
//        assertEquals(358.31, result, 0.01);
        // https://www.sunearthtools.com/tools/distance.php
        // tested with this website
        System.out.println(result);


        PointToSpeedSpeed a = new PointToSpeedSpeed();


        start = new Point(52.103683, 4.317695, 10.0, 123425.6, "1234256", "14:25:00");
        end = new Point(52.105976, 4.314905, 10.0, 123245.6, "1223456", "14:20:10");
        double result2 = convertitor.obtainBearing(start, end);
        System.out.println(result2);


        System.out.println(a.obtainAngularSpeedTime(new PointWithBearing(start,result), result2, 5.0 ));





        Point  start1 = new Point(52.103683, 4.317695, 10.0, 123425.6, "1234256", "14:25:00");
        Point end1 = new Point(52.098859, 4.323102, 10.0, 123245.6, "1223456", "14:20:10");
        double result1 = convertitor.obtainBearing(start1, end1);
        System.out.println(result1);


        System.out.println(a.obtainAngularSpeedTime(new PointWithBearing(start1,result2), result1, 5.0 ));


    }

}
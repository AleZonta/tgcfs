package tgcfs.InputOutput;

import lgds.trajectories.Point;
import org.junit.Test;

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
        Point start = new Point(52.320961, 4.869281, 10.0, 123245.6, "1223456", "14:20:10");
        Point end = new Point(52.324658, 4.869103, 10.0, 123425.6, "1234256", "14:25:00");
        PointToSpeedBearing convertitor = new PointToSpeedBearing();
        Double result = convertitor.obtainBearing(start, end);
        assertNotNull(result);
        System.out.println(result);
    }

}
package tgcfs.InputOutput;

import org.junit.Test;
import tgcfs.Utils.PointWithBearing;

/**
 * Created by Alessandro Zonta on 22/01/2018.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 */
public class PointToSpeedSpeedTest {

    @Test
    public void obtainAngularSpeed() {
        PointWithBearing end = new PointWithBearing(52.324658, 4.869103, 358.31445019581855);
        double a = 358.31445019581855;
        double b = 17.233891990210964;
        PointToSpeedSpeed p = new PointToSpeedSpeed();
        System.out.println(p.obtainAngularSpeed(end,b));

    }

    @Test
    public void obtainAngularSpeed1() {
        PointToSpeedSpeed p = new PointToSpeedSpeed();
        System.out.println(p.obtainAngularSpeed(359,357));
    }
}
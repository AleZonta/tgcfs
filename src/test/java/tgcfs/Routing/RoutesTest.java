package tgcfs.Routing;

import lgds.trajectories.Point;
import lgds.trajectories.Trajectory;
import org.junit.Assert;
import org.junit.Test;
import tgcfs.Config.ReadConfig;

import java.util.stream.IntStream;

import static junit.framework.TestCase.assertNotNull;

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
public class RoutesTest {
    @Test
    public void getNextTrajectory() throws Exception {
        new ReadConfig.Configurations();

        Routes routes = new Routes();
        routes.readTrajectories();
        Trajectory tra = routes.getNextTrajectory();
        Trajectory tra1 = routes.getNextTrajectory();
        assertNotNull(tra);
        assertNotNull(tra1);
        Assert.assertNotEquals(tra, tra1);


        IntStream.range(0,1000).forEach(i -> {
            Trajectory t = routes.getNextTrajectory();
            assertNotNull(t);
        });
    }

    @Test
    public void getNextPosition() throws Exception {

        new ReadConfig.Configurations();

        Routes routes = new Routes();
        routes.readTrajectories();
        Trajectory tra = routes.getNextTrajectory();
        Point p1 = routes.getNextPosition(tra);
        Point p2 = routes.getNextPosition(tra);
        assertNotNull(p1);
        assertNotNull(p2);
        Assert.assertNotEquals(p1, p2);

        System.out.println(p1.getLongitude() + " " + p1.getLatitude());
        System.out.println(p2.getLongitude() + " " + p2.getLatitude());
        Point p3 = routes.getNextPosition(tra);
        while(p3 != null) {
            assertNotNull(p3);
            Assert.assertNotEquals(p3, p1);
            System.out.println(p3.getLongitude() + " " + p3.getLatitude());
            p3 = routes.getNextPosition(tra);
        }
    }

    @Test
    public void readTrajectories() throws Exception {
        new ReadConfig.Configurations();

        Routes routes = new Routes();
        routes.readTrajectories();
    }

}
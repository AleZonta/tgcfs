package tgcfs.Routing;

import lgds.trajectories.Point;
import lgds.trajectories.Trajectory;
import org.junit.Assert;
import org.junit.Test;
import tgcfs.Config.ReadConfig;

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
        ReadConfig conf = new ReadConfig();
        conf.readFile();
        Routes routes = new Routes(conf);
        routes.readTrajectories();
        Trajectory tra = routes.getNextTrajectory();
        Trajectory tra1 = routes.getNextTrajectory();
        assertNotNull(tra);
        assertNotNull(tra1);
        Assert.assertNotEquals(tra, tra1);
    }

    @Test
    public void getNextPosition() throws Exception {
        ReadConfig conf = new ReadConfig();
        conf.readFile();
        Routes routes = new Routes(conf);
        routes.readTrajectories();
        Trajectory tra = routes.getNextTrajectory();
        Point p1 = routes.getNextPosition(tra);
        Point p2 = routes.getNextPosition(tra);
        assertNotNull(p1);
        assertNotNull(p2);
        Assert.assertNotEquals(p1, p2);
    }

    @Test
    public void readTrajectories() throws Exception {
        ReadConfig conf = new ReadConfig();
        conf.readFile();
        Routes routes = new Routes(conf);
        routes.readTrajectories();
    }

}
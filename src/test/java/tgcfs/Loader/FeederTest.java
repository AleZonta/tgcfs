package tgcfs.Loader;

import gms.GraphML.InfoNode;
import lgds.trajectories.Trajectory;
import org.junit.Test;

import java.util.List;

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
public class FeederTest {
    @Test
    public void fromTrajectoryToNodesInGraph() throws Exception {
        Feeder system = new Feeder();
        system.loadSystem();
        Trajectory trajectory = system.getTrajectory();
        List<InfoNode> nodes = system.fromTrajectoryToNodesInGraph(trajectory);
        assertNotNull(nodes);
    }

    @Test
    public void loadSystem() throws Exception {
        Feeder system = new Feeder();
        system.loadSystem();
    }

}
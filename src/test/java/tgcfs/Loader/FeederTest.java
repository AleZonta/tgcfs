package tgcfs.Loader;

import gms.GraphML.InfoNode;
import lgds.trajectories.Point;
import lgds.trajectories.Trajectory;
import org.junit.Test;
import tgcfs.Classifiers.InputNetwork;

import java.util.ArrayList;
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
    public void getTrajectory() throws Exception {
        Feeder system = new Feeder();
        system.loadSystem();
        assertNotNull(system.getTrajectory());
    }

    @Test
    public void obtainInput() throws Exception {
        Feeder system = new Feeder();
        system.loadSystem();
        Trajectory a = system.getTrajectory();
        List<Point> po = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            po.add(system.getNextPoint(a));
        }
        List<InputNetwork> res = system.obtainInput(po);
        assertNotNull(res);
    }

    @Test
    public void fromTrajectoryToNodesInGraph() throws Exception {
        Feeder system = new Feeder();
        system.loadSystem();
        Trajectory trajectory = system.getTrajectory();
        List<InfoNode> nodes = system.fromTrajectoryToNodesInGraph(trajectory);
        assertNotNull(nodes);
        nodes.forEach(node -> {
            System.out.println(node.retLat() + ", " + node.retLon());
        });
    }

    @Test
    public void loadSystem() throws Exception {
        Feeder system = new Feeder();
        system.loadSystem();
    }

}
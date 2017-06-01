package tgcfs.Loader;

import gms.GraphML.InfoNode;
import lgds.trajectories.Point;
import lgds.trajectories.Trajectory;
import org.junit.Test;
import tgcfs.Idsa.IdsaLoader;
import tgcfs.NN.InputsNetwork;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static junit.framework.TestCase.*;

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
    public void getMaximumNumberOfTrajectories() throws Exception {
        Feeder system = new Feeder();
        assertNotNull(system.getMaximumNumberOfTrajectories());
    }

    @Test
    public void feeder() throws Exception {
        Feeder system = new Feeder();
        system.loadSystem();

        IdsaLoader idsaLoader = new IdsaLoader();
        idsaLoader.InitPotentialField(system.getTrajectories());

        try {
            while (true) {
                List<InputsNetwork> res = system.feeder(idsaLoader);
                assertNotNull(res);
            }
        }catch (ReachedMaximumNumberException e){
            assertEquals("Reached Maximum Number Of trajectories", e.getMessage());
        }

    }

    @Test
    public void getFinished() throws Exception {
        Feeder system = new Feeder();
        system.loadSystem();
        Trajectory trajectory = system.getTrajectory();
        List<Point> points = system.obtainSectionTrajectory(trajectory);
        assertFalse(system.getFinished());

        IntStream.range(0,20).forEach(i -> {
            try {
                List<Point> points2 = system.obtainSectionTrajectory(trajectory);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        assertTrue(system.getFinished());

    }

    @Test
    public void getNextPoint() throws Exception {
        Feeder system = new Feeder();
        system.loadSystem();
        Trajectory trajectory = system.getTrajectory();
        assertNotNull(system.getNextPoint(trajectory));
    }

    @Test
    public void obtainSectionTrajectory() throws Exception {
        Feeder system = new Feeder();
        system.loadSystem();
        Trajectory trajectory = system.getTrajectory();
        List<Point> points = system.obtainSectionTrajectory(trajectory);
        assertNotNull(points);
        List<Point> points2 = system.obtainSectionTrajectory(trajectory);

        assertNotNull(points2);
        assertNotSame(points,points2);
        List<Point> points3 = system.obtainSectionTrajectory(trajectory);
        assertNotNull(points3);
        assertNotSame(points,points3);
        assertNotSame(points3,points2);

        System.out.println(points.toString());
        System.out.println(points2.toString());
        System.out.println(points3.toString());

    }

    @Test
    public void getTrajectories() throws Exception {
        Feeder system = new Feeder();
        system.loadSystem();
        assertNotNull(system.getTrajectories());
    }

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
        List<InputsNetwork> res = system.obtainInput(po, 10.0);
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
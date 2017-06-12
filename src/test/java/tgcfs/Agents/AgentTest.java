package tgcfs.Agents;

import lgds.trajectories.Point;
import org.junit.Test;
import tgcfs.NN.OutputsNetwork;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;

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
public class AgentTest {
    @Test
    public void getLastPoint() throws Exception {
        throw new Exception("Not implemented");
    }

    @Test
    public void setRealOutput() throws Exception {
        List<Point> points = new ArrayList<>();
        points.add(new Point(5d,5d));
        points.add(new Point(6d,5d));
        points.add(new Point(7d,6d));
        points.add(new Point(8d,7d));
        points.add(new Point(9d,8d));

        Agent agent = new Agent();
        agent.setRealOutput(points);

    }

    @Test
    public void realOutput() throws Exception {
        List<Point> points = new ArrayList<>();
        points.add(new Point(5d,5d));
        points.add(new Point(6d,5d));
        points.add(new Point(7d,6d));
        points.add(new Point(8d,7d));
        points.add(new Point(9d,8d));

        Agent agent = new Agent();
        agent.setRealOutput(points);
        List<OutputsNetwork> realOutput = agent.realOutput();
        assertNotNull(realOutput);
    }

}
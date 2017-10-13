package tgcfs.Agents;

import lgds.trajectories.Point;
import org.junit.Test;
import tgcfs.NN.OutputsNetwork;
import tgcfs.Utils.PointWithBearing;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
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
    public void getRealFirstPart() throws Exception {
        List<PointWithBearing> points = new ArrayList<>();
        points.add(new PointWithBearing(5d,5d));
        points.add(new PointWithBearing(6d,5d));
        points.add(new PointWithBearing(7d,6d));
        points.add(new PointWithBearing(8d,7d));
        points.add(new PointWithBearing(9d,8d));
        Agent agent = new Agent();
        agent.setRealFirstPart(points);
        List<PointWithBearing> pointsout = agent.getRealFirstPart();
        assertEquals(points.size(), pointsout.size());
        assertEquals(points, pointsout);


    }

    @Test
    public void setRealFirstPart() throws Exception {
        List<PointWithBearing> points = new ArrayList<>();
        points.add(new PointWithBearing(5d,5d));
        points.add(new PointWithBearing(6d,5d));
        points.add(new PointWithBearing(7d,6d));
        points.add(new PointWithBearing(8d,7d));
        points.add(new PointWithBearing(9d,8d));
        Agent agent = new Agent();
        agent.setRealFirstPart(points);
    }

    @Test
    public void getLastPoint() throws Exception {
        List<PointWithBearing> points = new ArrayList<>();
        points.add(new PointWithBearing(5d,5d));

        Agent agent = new Agent();
        agent.setRealOutput(points);

        Point p = agent.getLastPoint();
        assertNotNull(p);
    }

    @Test
    public void setRealOutput() throws Exception {
        List<PointWithBearing> points = new ArrayList<>();
        points.add(new PointWithBearing(5d,5d));
        points.add(new PointWithBearing(6d,5d));
        points.add(new PointWithBearing(7d,6d));
        points.add(new PointWithBearing(8d,7d));
        points.add(new PointWithBearing(9d,8d));

        Agent agent = new Agent();
        agent.setRealOutput(points);

    }

    @Test
    public void realOutput() throws Exception {
        List<PointWithBearing> points = new ArrayList<>();
        points.add(new PointWithBearing(5d,5d));
        points.add(new PointWithBearing(6d,5d));
        points.add(new PointWithBearing(7d,6d));
        points.add(new PointWithBearing(8d,7d));
        points.add(new PointWithBearing(9d,8d));

        Agent agent = new Agent();
        agent.setRealOutput(points);
        List<OutputsNetwork> realOutput = agent.realOutput();
        assertNotNull(realOutput);
    }

}
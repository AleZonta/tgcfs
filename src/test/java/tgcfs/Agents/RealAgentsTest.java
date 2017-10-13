package tgcfs.Agents;

import org.junit.Test;
import tgcfs.Agents.Models.RealAgents;
import tgcfs.Loader.TrainReal;
import tgcfs.NN.InputsNetwork;
import tgcfs.Utils.PointWithBearing;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;

/**
 * Created by Alessandro Zonta on 03/07/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 */
public class RealAgentsTest {
    @Test
    public void newAgent() throws Exception {
        RealAgents real = new RealAgents();
        List<PointWithBearing> pointList = new ArrayList<>();
        pointList.add(new PointWithBearing(1d,2d));
        pointList.add(new PointWithBearing(1d,2d));
        pointList.add(new PointWithBearing(1d,2d));
        real.newAgent(pointList);
    }

    @Test
    public void createAgent() throws Exception {
        List<InputsNetwork> list = new ArrayList<>();
        list.add(new InputNetwork(10d,10d,10d));
        list.add(new InputNetwork(150d,150d,150d));
        list.add(new InputNetwork(150d,150d,150d));
        List<PointWithBearing> pointList = new ArrayList<>();
        pointList.add(new PointWithBearing(1d,2d));
        pointList.add(new PointWithBearing(1d,2d));
        pointList.add(new PointWithBearing(1d,2d));



        List<TrainReal> t = new ArrayList<>();
        t.add(new TrainReal(list,pointList));
        RealAgents real = new RealAgents();
        real.createAgent(t);
    }

    @Test
    public void getRealAgents() throws Exception {
        RealAgents real = new RealAgents();
        List<PointWithBearing> pointList = new ArrayList<>();
        pointList.add(new PointWithBearing(1d,2d));
        pointList.add(new PointWithBearing(1d,2d));
        pointList.add(new PointWithBearing(1d,2d));
        real.newAgent(pointList);
        assertNotNull(real.getRealAgents());
    }

}
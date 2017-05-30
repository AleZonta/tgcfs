package tgcfs.Agents;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by Alessandro Zonta on 30/05/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 */
public class OutputNetworkTest {
    @Test
    public void getSpeed() throws Exception {
        OutputNetwork outputNetwork = new OutputNetwork();
        List<Double> list = new ArrayList<>();
        list.add(10.0); // speed
        list.add(50.0); // bearing
        outputNetwork.deserialise(list);
        assertEquals(new Double(10.0),outputNetwork.getSpeed());
    }

    @Test
    public void getBearing() throws Exception {
        OutputNetwork outputNetwork = new OutputNetwork();
        List<Double> list = new ArrayList<>();
        list.add(30.0); // speed
        list.add(60.0); // bearing
        outputNetwork.deserialise(list);
        assertEquals(new Double(60.0),outputNetwork.getBearing());
    }

    @Test
    public void deserialise() throws Exception {
        OutputNetwork outputNetwork = new OutputNetwork();
        List<Double> list = new ArrayList<>();
        list.add(10.0); // speed
        list.add(50.0); // bearing
        outputNetwork.deserialise(list);
    }

}
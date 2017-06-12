package tgcfs.Agents;

import org.junit.Test;
import tgcfs.InputOutput.Normalisation;

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
    public void getDistance() throws Exception {
        OutputNetwork outputNetwork = new OutputNetwork();
        List<Double> list = new ArrayList<>();
        list.add(1.0); // speed
        list.add(0.5); // bearing
        try {
            outputNetwork.deserialise(list);
        }catch (Error e){
            assertEquals("List size is not correct",e.getMessage());
        }
        list.add(0.7); // distance
        outputNetwork.deserialise(list);
        assertEquals(Normalisation.decodeDistance(0.7),outputNetwork.getDistance());
    }

    @Test
    public void getSpeed() throws Exception {
        OutputNetwork outputNetwork = new OutputNetwork();
        List<Double> list = new ArrayList<>();
        list.add(1.0); // speed
        list.add(0.5); // bearing
        try {
            outputNetwork.deserialise(list);
        }catch (Error e){
            assertEquals("List size is not correct",e.getMessage());
        }
        list.add(0.7); // distance
        outputNetwork.deserialise(list);
        assertEquals(Normalisation.decodeSpeed(1.0),outputNetwork.getSpeed());
    }

    @Test
    public void getBearing() throws Exception {
        OutputNetwork outputNetwork = new OutputNetwork();
        List<Double> list = new ArrayList<>();
        list.add(1.0); // speed
        list.add(0.5); // bearing
        try {
            outputNetwork.deserialise(list);
        }catch (Error e){
            assertEquals("List size is not correct",e.getMessage());
        }
        list.add(0.7); // distance
        outputNetwork.deserialise(list);
        assertEquals(Normalisation.decodeDirectionData(0.5),outputNetwork.getBearing());
    }

    @Test
    public void deserialise() throws Exception {
        OutputNetwork outputNetwork = new OutputNetwork();
        List<Double> list = new ArrayList<>();
        list.add(1.0); // speed
        list.add(0.5); // bearing
        try {
            outputNetwork.deserialise(list);
        }catch (Error e){
            assertEquals("List size is not correct",e.getMessage());
        }
        list.add(0.7); // distance
        outputNetwork.deserialise(list);
    }

}
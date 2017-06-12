package tgcfs.Agents;

import org.junit.Test;
import tgcfs.InputOutput.Normalisation;

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
public class InputNetworkTest {
    @Test
    public void getDirectionAPF() throws Exception {
        InputNetwork inputNetwork = new InputNetwork(5.0,10.0,30.0);
        assertEquals(Normalisation.convertDirectionData(5.0), inputNetwork.getDirectionAPF());
    }

    @Test
    public void getSpeed() throws Exception {
        InputNetwork inputNetwork = new InputNetwork(5.0,10.0,30.0);
        assertEquals(Normalisation.convertSpeed(10.0), inputNetwork.getSpeed());
    }

    @Test
    public void getBearing() throws Exception {
        InputNetwork inputNetwork = new InputNetwork(5.0,10.0,30.0);
        assertEquals(Normalisation.convertDirectionData(30.0), inputNetwork.getBearing());
    }

    @Test
    public void serialise() throws Exception {
        InputNetwork inputNetwork = new InputNetwork(5.0,10.0,30.0);
        List<Double> result = inputNetwork.serialise();
        assertEquals(Normalisation.convertSpeed(10.0), result.get(0));
        assertEquals(Normalisation.convertDirectionData(30.0), result.get(1));
        assertEquals(Normalisation.convertDirectionData(5.0), result.get(2));
    }

}
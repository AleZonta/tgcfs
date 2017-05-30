package tgcfs.Classifiers;

import org.junit.Test;

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
    public void getSpeed() throws Exception {
        InputNetwork inputNetwork = new InputNetwork(10.0,30.0);
        assertEquals(new Double(10.0), inputNetwork.getSpeed());
    }

    @Test
    public void getDirection() throws Exception {
        InputNetwork inputNetwork = new InputNetwork(10.0,30.0);
        assertEquals(new Double(30.0), inputNetwork.getDirection());
    }

    @Test
    public void serialise() throws Exception {
        InputNetwork inputNetwork = new InputNetwork(10.0,30.0);
        List<Double> list = inputNetwork.serialise();
        assertEquals(new Double(10.0),list.get(0));
        assertEquals(new Double(30.0),list.get(1));

    }

}
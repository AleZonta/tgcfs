package tgcfs.Classifiers;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
    public void getReal() throws Exception {
        OutputNetwork outputNetwork = new OutputNetwork();
        List<Double> list = new ArrayList<>();
        list.add(0.5);
        outputNetwork.deserialise(list);
        assertNotNull(outputNetwork.getReal());
        assertTrue(outputNetwork.getReal());

        list = new ArrayList<>();
        list.add(0.6);
        outputNetwork.deserialise(list);
        assertNotNull(outputNetwork.getReal());
        assertTrue(outputNetwork.getReal());

        list = new ArrayList<>();
        list.add(0.3);
        outputNetwork.deserialise(list);
        assertNotNull(outputNetwork.getReal());
        assertFalse(outputNetwork.getReal());
    }

    @Test
    public void deserialise() throws Exception {
        OutputNetwork outputNetwork = new OutputNetwork();
        List<Double> list = new ArrayList<>();
        list.add(0.5);
        outputNetwork.deserialise(list);
    }

}
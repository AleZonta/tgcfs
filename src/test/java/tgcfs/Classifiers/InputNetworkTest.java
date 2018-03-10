package tgcfs.Classifiers;

import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import tgcfs.InputOutput.Normalisation;

import static org.junit.Assert.assertEquals;
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
public class InputNetworkTest {
    @Test
    public void getSpeed() throws Exception {
        InputNetwork inputNetwork = new InputNetwork(10.0,30.0);
        assertTrue(Normalisation.convertSpeed(10.0) == inputNetwork.getLinearSpeed());
    }

    @Test
    public void getDirection() throws Exception {
        InputNetwork inputNetwork = new InputNetwork(10.0,30.0);
        assertTrue(Normalisation.convertDirectionData(30.0) == inputNetwork.getAngularSpeed());
    }

    @Test
    public void serialise() throws Exception {
        InputNetwork inputNetwork = new InputNetwork(10.0,30.0);
        INDArray list = inputNetwork.serialise();
        assertEquals(Normalisation.convertSpeed(10.0),list.getDouble(0), 0.000001);
        assertEquals(Normalisation.convertDirectionData(30.0),list.getDouble(1), 0.000001);

    }

}
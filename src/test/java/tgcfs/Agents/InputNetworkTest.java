package tgcfs.Agents;

import lgds.trajectories.Point;
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
    public void setTargetPoint() throws Exception {
        InputNetwork inputNetwork = new InputNetwork(5.0,10.0,30.0);
        inputNetwork.setTargetPoint(new Point(2d,2d));
    }

    @Test
    public void getTargetPoint() throws Exception {
        InputNetwork inputNetwork = new InputNetwork(5.0,10.0,30.0);
        Point p = new Point(2d,2d);
        inputNetwork.setTargetPoint(p);
        assertEquals(p, inputNetwork.getTargetPoint());
    }

    @Test
    public void getSpace() throws Exception {
        InputNetwork inputNetwork = new InputNetwork(5.0,10.0,30.0, 80d);
        assertTrue(Normalisation.convertDistance(80d) == inputNetwork.getSpace());
    }

    @Test
    public void serialiaseAsInputClassifier() throws Exception {
        InputNetwork inputNetwork = new InputNetwork(5.0,10.0,30.0, 80d);
        INDArray result = inputNetwork.serialiaseAsInputClassifier();
        assertEquals(Normalisation.convertSpeed(10.0), result.getDouble(0), 0.000001);
        assertEquals(Normalisation.convertDirectionData(30.0), result.getDouble(1), 0.000001);
    }

    @Test
    public void getDirectionAPF() throws Exception {
        InputNetwork inputNetwork = new InputNetwork(5.0,10.0,30.0);
        assertTrue(Normalisation.convertDirectionData(5.0) == inputNetwork.getDirectionAPF());

    }

    @Test
    public void getSpeed() throws Exception {
        InputNetwork inputNetwork = new InputNetwork(5.0,10.0,30.0);
        assertTrue(Normalisation.convertSpeed(10.0) == inputNetwork.getSpeed());
    }

    @Test
    public void getBearing() throws Exception {
        InputNetwork inputNetwork = new InputNetwork(5.0,10.0,30.0);
        assertTrue(Normalisation.convertDirectionData(30.0) == inputNetwork.getBearing());
    }

    @Test
    public void serialise() throws Exception {
        InputNetwork inputNetwork = new InputNetwork(5.0,10.0,30.0);
        INDArray result = inputNetwork.serialise();
        assertEquals(Normalisation.convertSpeed(10.0), result.getDouble(0), 0.000001);
        assertEquals(Normalisation.convertDirectionData(30.0), result.getDouble(1), 0.000001);
        assertEquals(Normalisation.convertDirectionData(5.0), result.getDouble(2), 0.000001);
    }

}
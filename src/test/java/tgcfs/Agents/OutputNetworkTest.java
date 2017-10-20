package tgcfs.Agents;

import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
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
public class OutputNetworkTest {
    @Test
    public void serialiaseAsInputClassifier() throws Exception {
        OutputNetwork outputNetwork = new OutputNetwork();
        INDArray array2 = Nd4j.create(3);
        array2.putScalar(0, 1.0);
        array2.putScalar(1, 0.5);
        array2.putScalar(2, 0.7);
        outputNetwork.deserialise(array2);

        INDArray res = outputNetwork.serialiaseAsInputClassifier();
        assertEquals(Normalisation.decodeSpeed(1.0), res.getDouble(0), 0.000001);
        assertEquals(Normalisation.decodeDirectionData(0.5), res.getDouble(1), 0.000001);
    }

    @Test
    public void getDistance() throws Exception {
//        OutputNetwork outputNetwork = new OutputNetwork();
//        INDArray array = Nd4j.create(2);
//        array.putScalar(0, 1.0);
//        array.putScalar(1, 0.5);
//        try {
//            outputNetwork.deserialise(array);
//        }catch (Error e){
//            assertEquals("List size is not correct",e.getMessage());
//        }
//        INDArray array2 = Nd4j.create(3);
//        array2.putScalar(0, 1.0);
//        array2.putScalar(1, 0.5);
//        array2.putScalar(2, 0.7);
//        outputNetwork.deserialise(array2);
//        assertEquals(Normalisation.decodeDistance(0.7),outputNetwork.getDistance(), 0.01);
    }

    @Test
    public void getSpeed() throws Exception {
        OutputNetwork outputNetwork = new OutputNetwork();
        INDArray array = Nd4j.create(2);
        array.putScalar(0, 1.0);
        array.putScalar(1, 0.5);
        try {
            outputNetwork.deserialise(array);
        }catch (Error e){
            assertEquals("List size is not correct",e.getMessage());
        }
        INDArray array2 = Nd4j.create(3);
        array2.putScalar(0, 1.0);
        array2.putScalar(1, 0.5);
        array2.putScalar(2, 0.7);
        outputNetwork.deserialise(array2);
        assertTrue(Normalisation.decodeSpeed(1.0) ==outputNetwork.getSpeed());
    }

    @Test
    public void getBearing() throws Exception {
        OutputNetwork outputNetwork = new OutputNetwork();
        INDArray array = Nd4j.create(2);
        array.putScalar(0, 1.0);
        array.putScalar(1, 0.5);
        try {
            outputNetwork.deserialise(array);
        }catch (Error e){
            assertEquals("List size is not correct",e.getMessage());
        }
        INDArray array2 = Nd4j.create(3);
        array2.putScalar(0, 1.0);
        array2.putScalar(1, 0.5);
        array2.putScalar(2, 0.7);
        outputNetwork.deserialise(array2);
        assertTrue(Normalisation.decodeDirectionData(0.5) == outputNetwork.getBearing());
    }

    @Test
    public void deserialise() throws Exception {
        OutputNetwork outputNetwork = new OutputNetwork();
        INDArray array = Nd4j.create(2);
        array.putScalar(0, 1.0);
        array.putScalar(1, 0.5);
        try {
            outputNetwork.deserialise(array);
        }catch (Error e){
            assertEquals("List size is not correct",e.getMessage());
        }
        INDArray array2 = Nd4j.create(3);
        array2.putScalar(0, 1.0);
        array2.putScalar(1, 0.5);
        array2.putScalar(2, 0.7);
        outputNetwork.deserialise(array2);
    }

}
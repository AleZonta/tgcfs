package tgcfs.Classifiers;

import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import tgcfs.Config.ReadConfig;

import static org.junit.Assert.*;

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
        new ReadConfig.Configurations();

        OutputNetwork outputNetwork = new OutputNetwork();
        INDArray array = Nd4j.create(1);
        array.putScalar(0,0.5);


        outputNetwork.deserialise(array);
        assertTrue(outputNetwork.getReal());

        array.putScalar(0,0.6);
        outputNetwork.deserialise(array);
        assertTrue(outputNetwork.getReal());

        array.putScalar(0,-0.5);
        outputNetwork.deserialise(array);
        assertFalse(outputNetwork.getReal());
    }

    @Test
    public void deserialise() throws Exception {
        new ReadConfig.Configurations();

        OutputNetwork outputNetwork = new OutputNetwork();


        INDArray array = Nd4j.create(1);
        array.putScalar(0,0.5);
        outputNetwork.deserialise(array);
    }

}
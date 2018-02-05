package tgcfs.Networks;

import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by Alessandro Zonta on 05/02/2018.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 */
public class ElmanNeuralNetworkTest {

    @Test
    public void computeOutput() {
        ElmanNeuralNetwork enn = new ElmanNeuralNetwork(2,3,1);
        for(int i = 0; i < 10000; i++) {
            INDArray array = Nd4j.rand(1, 2);
            INDArray out = enn.computeOutput(array);
            System.out.println("");
            System.out.println(out);
            assertNotNull(out);
            assertTrue(out.columns() == 1);
            assertTrue((out.getDouble(0) >= 0.0) && (out.getDouble(0) <= 1.0));
        }
    }

    @Test
    public void getSummary() {
        ElmanNeuralNetwork enn = new ElmanNeuralNetwork(2,3,1);
        System.out.println(enn.getSummary());
    }

    @Test
    public void getNumPar() {
    }

    @Test
    public void cleanParam() {
    }
}
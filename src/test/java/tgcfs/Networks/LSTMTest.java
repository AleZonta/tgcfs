package tgcfs.Networks;

import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.util.stream.IntStream;

import static org.junit.Assert.*;

/**
 * Created by Alessandro Zonta on 08/08/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 */
public class LSTMTest {
    @Test
    public void computeOutput() throws Exception {
        LSTM agent = new LSTM(2,1,1,1);

        INDArray array = Nd4j.rand(1, 2);
        array.putScalar(0,5.0);
        INDArray out = null;
        try {
            out = agent.computeOutput(array);
        }catch (Error e){
            assertEquals("Generator input is not normalised correctly", e.getMessage());
        }
        INDArray real = Nd4j.rand(1, 2);
        out = agent.computeOutput(real);
        assertNotNull(out);
        assertFalse(real.equals(out));
        assertTrue(out.columns() == 1);
        assertTrue((out.getDouble(0) >= -1.0)  && (out.getDouble(0) <= 1.0) );
    }

    @Test
    public void getNumPar() throws Exception {
        LSTM net = new LSTM(2,1,1,1);
        assertEquals(21L, net.getNumPar().longValue());
    }

    @Test
    public void fit() throws Exception {
        LSTM agent = new LSTM(3,1,1,3);
        INDArray input = Nd4j.rand(1, 3);
        INDArray label = Nd4j.rand(1, 3);

        IntStream.range(0, 10).forEach(i -> agent.fit(input,label));

    }

    @Test
    public void getSummary() throws Exception {
        LSTM agent = new LSTM(3,1,1,3);
        assertNotNull(agent.getSummary());
        System.out.println(agent.getSummary());
    }

}
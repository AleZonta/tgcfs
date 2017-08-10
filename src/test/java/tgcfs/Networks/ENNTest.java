package tgcfs.Networks;

import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

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
public class ENNTest {
    @Test
    public void computeOutput() throws Exception {
        ENN agent = new ENN(2,1,1);

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
    public void fit() throws Exception {
        ENN agent = new ENN(3,1,1);
        INDArray input = Nd4j.rand(1, 3);
        INDArray label = Nd4j.rand(1, 1);

        try {
            agent.fit(input, label);
        }catch (Error e){
            assertEquals("Method not implemented", e.getMessage());
        }
    }


    @Test
    public void getSummary() throws Exception {
        ENN agent = new ENN(2,1,1);
        assertNotNull(agent.getSummary());
        System.out.println(agent.getSummary());
    }

    @Test
    public void getNumPar() throws Exception {
        ENN net = new ENN(2,1,1);
        assertEquals(6, net.getNumPar().longValue());
    }

}
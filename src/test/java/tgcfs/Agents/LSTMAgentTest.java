package tgcfs.Agents;

import lgds.trajectories.Point;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import tgcfs.Agents.Models.LSTMAgent;
import tgcfs.NN.InputsNetwork;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

/**
 * Created by Alessandro Zonta on 22/05/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 */
public class LSTMAgentTest {
    @Test
    public void fit() throws Exception {
        LSTMAgent agent = new LSTMAgent(3,1,1,3);
        List<InputsNetwork> input = new ArrayList<>();
        input.add(new InputNetwork(10d,10d,80d));
        input.add(new InputNetwork(20d,30d,90d));
        input.add(new InputNetwork(30d,40d,10d));
        input.add(new InputNetwork(40d,50d,120d));
        input.add(new InputNetwork(50d,60d,130d));
        input.add(new InputNetwork(60d,70d,140d));

        List<Point> output = new ArrayList<>();
        output.add(new Point(1d,6d));
        output.add(new Point(2d,7d));
        output.add(new Point(3d,8d));
        output.add(new Point(4d,9d));
        output.add(new Point(5d,10d));
        output.add(new Point(3d,8d));
        output.add(new Point(4d,9d));
        output.add(new Point(5d,10d));

        IntStream.range(0, 1000000).forEach(i -> agent.fit(input,output));



    }

    @Test
    public void computeOutput() throws Exception {
        LSTMAgent agent = new LSTMAgent(2,1,1,1);

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
    public void clearPreviousState() throws Exception {
        LSTMAgent agent = new LSTMAgent(2,1,1,1);
        agent.clearPreviousState();
    }

    @Test
    public void deepCopy() throws Exception {
        LSTMAgent agent = new LSTMAgent(2,1,1,1);
        LSTMAgent secondAgent = (LSTMAgent) agent.deepCopy();
        assertFalse(agent.equals(secondAgent));
    }

    @Test
    public void setWeights() throws Exception {
        INDArray array = Nd4j.rand(1, 21);
        LSTMAgent agent = new LSTMAgent(2,1,1,1);
        agent.setWeights(array);
        INDArray lsit = agent.getWeights();
        assertEquals(array,lsit);
    }

    @Test
    public void getWeights() throws Exception {
        LSTMAgent agent = new LSTMAgent(3,1,5,3);
        INDArray lsit = agent.getWeights();
        assertNotNull(lsit);
        System.out.println(lsit.columns());
    }

    @Test
    public void getArrayLength() throws Exception {
        LSTMAgent agent = new LSTMAgent(2,1,1,1);
        /*
        int nL = layerConf.getNOut(); //i.e., n neurons in this layer
        int nLast = layerConf.getNIn(); //i.e., n neurons in previous layer

        int nParams = nLast * (4 * nL) //"input" weights
                + nL * (4 * nL + 3) //recurrent weights
                + 4 * nL; //bias
        */
        assertEquals(21L, agent.getArrayLength());
    }

}
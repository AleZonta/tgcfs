package tgcfs.Agents;

import lgds.trajectories.Point;
import org.junit.Test;
import tgcfs.NN.InputsNetwork;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

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

        agent.fit(input,output);

    }

    @Test
    public void computeOutput() throws Exception {
        LSTMAgent agent = new LSTMAgent(2,1,1,1);
        List<Double> list = new ArrayList<>();
        list.add(5.0);
        list.add(6.0);
        List<Double> out = null;
        try {
            out = agent.computeOutput(list);
            assertNotNull(out);
            assertFalse(list.equals(out));
            assertTrue(out.size() == 1);
            assertTrue((out.get(0) >= -1.0)  && (out.get(0) <= 1.0) );
        }catch (Error e){
            assertEquals("Generator input is not normalised correctly", e.getMessage());
        }
        list = new ArrayList<>();
        list.add(1.0);
        list.add(-1.0);
        out = agent.computeOutput(list);
        assertNotNull(out);
        assertFalse(list.equals(out));
        assertTrue(out.size() == 1);
        assertTrue((out.get(0) >= -1.0)  && (out.get(0) <= 1.0) );

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
        Random random = new Random();
        DoubleStream doubleStream = random.doubles(-1, 1);
        List<Double> numbers = doubleStream.limit(36L).boxed().collect(Collectors.toList());
        List<Double> brandNewNumbers = new ArrayList<>();
        numbers.forEach(num -> brandNewNumbers.add(Math.round(num * 1000.0) / 1000.0));

        LSTMAgent agent = new LSTMAgent(2,1,1,1);
        agent.setWeights(brandNewNumbers);
        List<Double> lsit = agent.getWeights();
        List<Double> brandNewlsit = new ArrayList<>();
        lsit.forEach(num -> brandNewlsit.add(Math.round(num * 1000.0) / 1000.0));
        assertEquals(brandNewNumbers,brandNewlsit);

    }

    @Test
    public void getWeights() throws Exception {
        LSTMAgent agent = new LSTMAgent(3,1,5,3);
        List<Double> lsit = agent.getWeights();
        assertNotNull(lsit);
        System.out.println(lsit.size());
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
        assertEquals(36L, agent.getArrayLength().longValue());
    }

}
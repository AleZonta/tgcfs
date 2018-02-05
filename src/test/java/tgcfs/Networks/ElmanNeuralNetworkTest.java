package tgcfs.Networks;

import org.junit.Test;
import org.nd4j.linalg.api.buffer.DataBuffer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.util.ArrayList;
import java.util.List;

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

        ElmanNeuralNetwork enn = new ElmanNeuralNetwork(1,1,1);

//        double[] aa = {0.2971605607102013, 0.6755714958750237, 0.3755320166321959, 0.3597828181197713, 0.5518673054832557, 0.6986917891755237, 0.36136201721717603, 0.1940267576730745, 0.5647269131947381, 0.4080161998177687, 0.41062439303024134, 0.44334368570945104, 0.7065754655163782, 0.42783252064407323, 0.49657762916011655, 0.5734785358221872, 0.17901600326387013, 0.03725626268616709, 0.7053495649179214, 0.1691643154357169, 0.2633226170989881, 0.03893115639779029};
//
//        INDArray weights = Nd4j.create(aa);
        System.out.println(enn.getNumPar());
        INDArray weights = Nd4j.rand(1,enn.getNumPar());
        List<Double> a = new ArrayList<>();
        for (int i = 0; i < weights.columns(); i++) {
            a.add(weights.getDouble(i));
        }
        System.out.println(a);


        enn.setParameters(weights);

//        double[] bb = {0.7242639508807651, 0.8898628357174725};
//        INDArray array = Nd4j.create(bb);
        INDArray array = Nd4j.rand(1,1);
        a = new ArrayList<>();
        for (int i = 0; i < array.columns(); i++) {
            a.add(array.getDouble(i));
        }
        System.out.println(a);
        INDArray out = enn.computeOutput(array);

        a = new ArrayList<>();
        for (int i = 0; i < out.columns(); i++) {
            a.add(out.getDouble(i));
        }
        System.out.println(a);

        assertNotNull(out);
        assertTrue(out.columns() == 1);
        assertTrue((out.getDouble(0) >= 0.0) && (out.getDouble(0) <= 1.0));
        System.out.println("-------");
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
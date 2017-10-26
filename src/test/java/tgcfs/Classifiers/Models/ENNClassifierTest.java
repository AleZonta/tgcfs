package tgcfs.Classifiers.Models;

import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * Created by Alessandro Zonta on 24/10/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 */
public class ENNClassifierTest {
    @Test
    public void setWeights() throws Exception {
        ENNClassifier test = new ENNClassifier(2,1,1);

        INDArray array = Nd4j.rand(1, test.getArrayLength());

        test.setWeights(array);
        assertEquals(array, test.getWeights());
    }

    @Test
    public void getWeights() throws Exception {
        //tested underneath
    }

    @Test
    public void getArrayLength() throws Exception {
        ENNClassifier test = new ENNClassifier(3,4,1);

        assertEquals(37, test.getArrayLength());
    }

    @Test
    public void deepCopy() throws Exception {
        ENNClassifier test = new ENNClassifier(2,5,1);
        ENNClassifier secondAgent = (ENNClassifier) test.deepCopy();
        assertFalse(test.equals(secondAgent));
    }

    @Test
    public void fit() throws Exception {
        throw new Exception("Not tested");
    }

    @Test
    public void createOutput() throws Exception {
        throw new Exception("Not tested");
    }

    @Test
    public void computeOutput() throws Exception {
        for(int w=0; w<15; w++) {
            ENNClassifier test = new ENNClassifier(3, 4, 1);

            INDArray array = Nd4j.rand(1, test.getArrayLength());
            for (int j = 0; j < test.getArrayLength(); j++) {
                array.putScalar(j, ThreadLocalRandom.current().nextDouble(-1, 1));

            }


            test.setWeights(array);
            final INDArray[] out = {null};
            IntStream.range(0, 1000).forEach(i -> {
                INDArray arrayy = Nd4j.rand(1, 3);
                for (int j = 0; j < 3; j++) {
                    arrayy.putScalar(j, ThreadLocalRandom.current().nextDouble(-1, 1));
                }


                out[0] = test.computeOutput(arrayy);
                assertNotNull(out[0]);



                assertEquals(1, out[0].columns());
                assertTrue((out[0].getDouble(0) >= -1.0) && (out[0].getDouble(0) <= 1.0));

            });
            System.out.println(out[0].getDouble(0));
            System.out.println("----");
        }
    }

}
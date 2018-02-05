package tgcfs.Classifiers.Models;

import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import tgcfs.Config.ReadConfig;
import tgcfs.Utils.RandomGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static junit.framework.TestCase.*;
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
        new ReadConfig.Configurations();
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
        new ReadConfig.Configurations();

        new RandomGenerator();
        for(int w=0; w<1; w++) {
            ENNClassifier test = new ENNClassifier(2, 4, 1);

            INDArray array = Nd4j.rand(1, test.getArrayLength());
            for (int j = 0; j < test.getArrayLength(); j++) {
                array.putScalar(j, RandomGenerator.getNextDouble(-1,1));

            }


            test.setWeights(array);



            IntStream.range(0, 1000).forEach(i -> {
                INDArray arrayy = Nd4j.rand(1, 2);
                for (int j = 0; j < 2; j++) {
                    arrayy.putScalar(j, RandomGenerator.getNextDouble(-1,1));
                }


                INDArray out = test.computeOutput(arrayy);
                assertNotNull(out);
                System.out.println(out.getDouble(0));




                assertEquals(1, out.columns());
                assertTrue((out.getDouble(0) >= -1.0) && (out.getDouble(0) <= 1.0));

            });
//            System.out.println(out[0].getDouble(0));
            System.out.println("----");


            List<INDArray> arrays = new ArrayList<>();
            for(int i = 0; i < 10; i++){
                INDArray arrayy = Nd4j.rand(1, 2);
                for (int j = 0; j < 3; j++) {
                    arrayy.putScalar(j, RandomGenerator.getNextDouble(-1,1));
                }
                arrays.add(arrayy);
            }

            for(int i = 0; i < 100; i++){
                List<INDArray> appo = new ArrayList<>(arrays);
                INDArray arrayy = Nd4j.rand(1, 2);
                for (int j = 0; j < 2; j++) {
                    arrayy.putScalar(j, RandomGenerator.getNextDouble(-1,1));
                }
                appo.add(arrayy);
                INDArray out = null;
                System.out.println(appo);
                for(INDArray arr: appo){
                    out = test.computeOutput(arr);

                }
                System.out.println(out.getDouble(0));
                System.out.println("----");

            }





        }
    }

}
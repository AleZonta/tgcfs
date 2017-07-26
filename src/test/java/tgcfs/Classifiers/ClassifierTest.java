package tgcfs.Classifiers;

import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.util.stream.IntStream;

import static org.junit.Assert.*;

/**
 * Created by Alessandro Zonta on 18/05/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 */
public class ClassifierTest {
    @Test
    public void deepCopy() throws Exception {
        Classifier test = new Classifier(2,5,1);
        Classifier secondAgent = (Classifier) test.deepCopy();
        assertFalse(test.equals(secondAgent));
    }

    @Test
    public void getArrayLength() throws Exception {
        Classifier test = new Classifier(2,1,1);

        assertEquals(6, test.getArrayLength().longValue());
    }

    @Test
    public void setWeights() throws Exception {
        Classifier test = new Classifier(2,1,1);

        INDArray array = Nd4j.rand(1, test.getArrayLength());

        test.setWeights(array);
        assertEquals(array, test.getWeights());
    }

    @Test
    public void getWeights() throws Exception {
        //tested before
    }

    @Test
    public void computeOutput() throws Exception {
        Classifier test = new Classifier(3,4,1);

        INDArray array = Nd4j.rand(1, test.getArrayLength());
        test.setWeights(array);

        IntStream.range(0,100).forEach(i ->{
            INDArray arrayy = Nd4j.rand(1, 2);


            INDArray out = test.computeOutput(arrayy);
            assertNotNull(out);

            assertEquals(1, out.columns());
            assertTrue((out.getDouble(0) >= -1.0)  && (out.getDouble(0) <= 1.0) );
        });

    }

}
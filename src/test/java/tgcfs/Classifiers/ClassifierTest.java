package tgcfs.Classifiers;

import org.junit.Test;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
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
        Classifier test = new Classifier(2,1,1);
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

        Random random = new Random();
        DoubleStream doubleStream = random.doubles(-1, 1);
        List<Double> numbers = doubleStream.limit(test.getArrayLength()).boxed().collect(Collectors.toList());
        test.setWeights(numbers);
        assertEquals(numbers, test.getWeights());
    }

    @Test
    public void getWeights() throws Exception {
        //tested before
    }

    @Test
    public void computeOutput() throws Exception {
        Classifier test = new Classifier(2,1,1);

        Random random = new Random();
        DoubleStream doubleStream = random.doubles(-1, 1);
        List<Double> numbers = doubleStream.limit(test.getArrayLength()).boxed().collect(Collectors.toList());
        test.setWeights(numbers);

        IntStream.range(0,100).forEach(i ->{
            Random randomm = new Random();
            DoubleStream doubleStreamHere = randomm.doubles(-1, 1);
            List<Double> numberss = doubleStreamHere.limit(2).boxed().collect(Collectors.toList());
            List<Double> out = test.computeOutput(numberss);
            assertNotNull(out);

            assertEquals(1, out.size());
            assertTrue((out.get(0) >= -1.0)  && (out.get(0) <= 1.0) );
        });

    }

}
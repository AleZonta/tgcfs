package tgcfs.Classifiers;

import org.junit.Test;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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

        doubleStream = random.doubles(-1, 1);
        numbers = doubleStream.limit(2).boxed().collect(Collectors.toList());
        List<Double> out = test.computeOutput(numbers);
        assertNotNull(out);
    }

}
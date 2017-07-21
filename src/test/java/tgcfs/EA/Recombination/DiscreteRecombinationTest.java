package tgcfs.EA.Recombination;

import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.util.ArrayUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static junit.framework.TestCase.*;

/**
 * Created by Alessandro Zonta on 29/05/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 */
public class DiscreteRecombinationTest {
    @Test
    public void recombination() throws Exception {
        List<Double> mother = new Random().doubles(20, -4.0, 4.0).collect(ArrayList::new,ArrayList::add, ArrayList::addAll);
        List<Double> father = new Random().doubles(20, -4.0, 4.0).collect(ArrayList::new,ArrayList::add, ArrayList::addAll);

        INDArray realMother = Nd4j.create(ArrayUtil.flattenDoubleArray(mother));
        INDArray realFather = Nd4j.create(ArrayUtil.flattenDoubleArray(father));

        Recombination rec = new DiscreteRecombination(realMother, realFather);
        INDArray son = rec.recombination();
        assertNotNull(son);
        assertEquals(mother.size(), son.columns());
        for (int i = 0; i < son.columns(); i++){
            assertTrue(son.getDouble(i) == realMother.getDouble(i) || son.getDouble(i) == realFather.getDouble(i));
        }
        Integer count = 0;
        for (int i = 0; i < son.columns(); i++){
            if(son.getDouble(i) == realMother.getDouble(i)) count+=1;
        }
        Integer count1 = 0;
        for (int i = 0; i < son.columns(); i++){
            if(son.getDouble(i) == realFather.getDouble(i)) count1+=1;
        }
        assertTrue(count < realMother.columns());
        assertTrue(count1 < realFather.columns());
    }

}
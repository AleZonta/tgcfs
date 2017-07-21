package tgcfs.EA.Recombination;

import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.util.ArrayUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

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
public class IntermediateRecombinationTest {
    @Test
    public void recombination() throws Exception {
        List<Double> mother = new Random().doubles(20, -8.0, 8.0).collect(ArrayList::new,ArrayList::add, ArrayList::addAll);
        System.out.println(mother.toString());
        List<Double> father = new Random().doubles(20, -8.0, 8.0).collect(ArrayList::new,ArrayList::add, ArrayList::addAll);
        System.out.println(father.toString());

        INDArray realMother = Nd4j.create(ArrayUtil.flattenDoubleArray(mother));
        INDArray realFather = Nd4j.create(ArrayUtil.flattenDoubleArray(father));

        Recombination rec = new IntermediateRecombination(realMother, realFather, 0.5);
        INDArray son = rec.recombination();
        assertNotNull(son);
        assertEquals(realMother.columns(), son.columns());
        System.out.println(son.toString());

    }

}
package tgcfs.EA.Recombination;

import org.junit.Test;

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

        Recombination rec = new IntermediateRecombination(mother, father, 0.5);
        List<Double> son = rec.recombination();
        assertNotNull(son);
        assertEquals(mother.size(), son.size());
        System.out.println(son.toString());

    }

}
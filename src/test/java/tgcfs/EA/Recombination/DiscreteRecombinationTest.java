package tgcfs.EA.Recombination;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

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
        List<Double> mother = new Random().doubles(20, -8.0, 8.0).collect(ArrayList::new,ArrayList::add, ArrayList::addAll);
        List<Double> father = new Random().doubles(20, -8.0, 8.0).collect(ArrayList::new,ArrayList::add, ArrayList::addAll);
        Recombination rec = new DiscreteRecombination(mother, father);
        List<Double> son = rec.recombination();
        assertNotNull(son);
        assertEquals(mother.size(), son.size());
        for (int i = 0; i < son.size(); i++){
            assertTrue(son.get(i).equals(mother.get(i)) || son.get(i).equals(father.get(i)));
        }

    }

}
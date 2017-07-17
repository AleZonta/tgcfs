package tgcfs.EA.Mutation;

import org.junit.Test;
import tgcfs.Config.ReadConfig;
import tgcfs.EA.Individual;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNotSame;
import static junit.framework.TestCase.assertTrue;

/**
 * Created by Alessandro Zonta on 30/06/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 */
public class RandomResettingTest {
    @Test
    public void mutate() throws Exception {
        new ReadConfig.Configurations();
        Individual ind = new RandomResetting(2000);
        List<Double> original = ind.getObjectiveParameters();
        List<Double> realOriginal = new ArrayList<>();
        original.forEach(el -> realOriginal.add(new Double(el)));
        IntStream.range(0, 100000).forEach(i -> {
            ind.mutate(2000);
            List<Double> mutw = ind.getObjectiveParameters();
            Integer d = 0;
            for(int z = 0; z < original.size(); z++){
                if(!Objects.equals(realOriginal.get(z), mutw.get(z))){
                    d++;
                }
            }
            assertNotSame(0,d);
            mutw.forEach(el -> assertTrue(el>=-4 && el<=4));
            mutw.forEach(el -> assertFalse(el.isNaN()));
        });

    }

}
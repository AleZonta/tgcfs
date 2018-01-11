package tgcfs.EA.Mutation;

import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import tgcfs.Config.ReadConfig;
import tgcfs.EA.Individual;
import tgcfs.Utils.RandomGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import static junit.framework.TestCase.assertNotSame;
import static junit.framework.TestCase.assertTrue;

/**
 * Created by Alessandro Zonta on 13/10/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 */
public class NonUniformMutationTest {
    @Test
    public void mutate() throws Exception {
        new ReadConfig.Configurations();
        new RandomGenerator();
        new StepSize();

        Individual ind = new NonUniformMutation(10);
        INDArray original = ind.getObjectiveParameters();

        List<Double> list = new ArrayList<>();
        for(int j=0; j< original.columns(); j++){
            list.add(original.getDouble(j));
        }
        System.out.println(list.toString());

        INDArray realOriginal = original.dup();

        IntStream.range(0, 1000).forEach(i -> {
            ind.mutate(i);
            INDArray mutw = ind.getObjectiveParameters();

            List<Double> listt = new ArrayList<>();
            for(int j=0; j< mutw.columns(); j++){
                listt.add(mutw.getDouble(j));
            }
            System.out.println(listt.toString());


            Integer d = 0;
            for(int z = 0; z < original.columns(); z++){
                if(!Objects.equals(realOriginal.getDouble(z), mutw.getDouble(z))){
                    d++;
                }
            }
            assertNotSame(0,d);
            for(int j = 0; j < mutw.columns(); j++){
                assertTrue(mutw.getDouble(j)>=-4 && mutw.getDouble(j)<=4);
            }
            for(int j = 0; j < mutw.columns(); j++){
                assertTrue(mutw.getDouble(j)>=-4 && mutw.getDouble(j)<=4);
            }

        });
    }

}
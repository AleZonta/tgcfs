package tgcfs.EA.Mutation;

import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import tgcfs.Config.ReadConfig;
import tgcfs.EA.Individual;
import tgcfs.Utils.RandomGenerator;

import java.util.Objects;
import java.util.stream.IntStream;

import static junit.framework.TestCase.*;

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
public class UncorrelatedMutationTest {
    @Test
    public void getMutationStrengths() throws Exception {
        new ReadConfig.Configurations();
        Individual ind = new UncorrelatedMutation(5);
        assertNotNull(((UncorrelatedMutation)ind).getMutationStrengths());
        assertEquals(5,((UncorrelatedMutation)ind).getMutationStrengths().columns());
    }

    @Test
    public void mutate() throws Exception {

        new ReadConfig.Configurations();
        new RandomGenerator();

        IntStream.range(0,10).forEach(q -> {
            Individual ind = null;
            try {
                ind = new UncorrelatedMutation(5);
            } catch (Exception e) {
                e.printStackTrace();
            }

            INDArray original = ind.getObjectiveParameters();
            INDArray realOriginal = original.dup();


            Individual finalInd = ind;
            IntStream.range(0, 10).forEach(i -> {
                finalInd.mutate(10);
                INDArray mutw = finalInd.getObjectiveParameters();
                Integer d = 0;
                for(int z = 0; z < original.columns(); z++){
                    if(!Objects.equals(realOriginal.getDouble(z), mutw.getDouble(z))){
                        d++;
                    }
                }
                assertNotSame(0,d);

                System.out.println(mutw.toString());
                for(int j = 0; j < mutw.columns(); j++){

                    assertTrue(mutw.getDouble(j)>=-4 && mutw.getDouble(j)<=4);
                }
            });

            System.out.println("-------------------");
        });



    }

}
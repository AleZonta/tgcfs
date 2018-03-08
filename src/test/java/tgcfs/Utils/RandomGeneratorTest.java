package tgcfs.Utils;

import org.junit.Test;
import tgcfs.Config.ReadConfig;

import static junit.framework.TestCase.assertTrue;

/**
 * Created by Alessandro Zonta on 17/11/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 */
public class RandomGeneratorTest {
    @Test
    public void getNextInt() throws Exception {
        new ReadConfig.Configurations();
        RandomGenerator gen = new RandomGenerator();
        for (int i = 0; i < 100; i++){
            int num = gen.getNextInt();
            System.out.println(num);
        }
    }

    @Test
    public void getNextInt1() throws Exception {
        new ReadConfig.Configurations();
        RandomGenerator gen = new RandomGenerator();
        for (int i = 0; i < 100; i++){
            int num = gen.getNextInt(0,10);
            assertTrue(num>=0 && num<=10);
            System.out.println(num);
        }
    }

    @Test
    public void getNextDouble() throws Exception {
        new ReadConfig.Configurations();
        RandomGenerator gen = new RandomGenerator();
        for (int i = 0; i < 100; i++){
            double num = gen.getNextDouble();
            System.out.println(num);
        }
    }

    @Test
    public void getNextDouble1() throws Exception {
        new ReadConfig.Configurations();
        RandomGenerator gen = new RandomGenerator();
        for (int i = 0; i < 100; i++){
            double num = gen.getNextDouble(0,12);
            assertTrue(num>=0 && num<=12);
            System.out.println(num);
        }
    }

    @Test
    public void getNextGaussian() throws Exception {
        new ReadConfig.Configurations();
        new RandomGenerator();
        for (int i = 0; i < 100; i++){
            double num = RandomGenerator.getNextGaussian();
            System.out.println(num);
        }
    }

    @Test
    public void getNextDouble2() throws Exception {
        new ReadConfig.Configurations();
        new RandomGenerator();
        for (int i = 0; i < 100; i++){
            double num = RandomGenerator.getNextDouble(-4,+4);
            System.out.println(num);
        }
    }
}
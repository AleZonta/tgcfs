package tgcfs.Performances;

import lgds.trajectories.Point;
import nl.tno.idsa.framework.config.ConfigFile;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import tgcfs.Agents.InputNetwork;
import tgcfs.Config.ReadConfig;
import tgcfs.EA.Individual;
import tgcfs.EA.Mutation.UncorrelatedMutation;
import tgcfs.Loader.TrainReal;
import tgcfs.NN.InputsNetwork;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by Alessandro Zonta on 01/06/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 */
public class SaverTest {
    @Test
    public void dumpTrajectoryAndGeneratedPart() throws Exception {
        new ReadConfig.Configurations();
        List<InputsNetwork> input = new ArrayList<>();
        input.add(new InputNetwork(15.0, 30.8, 15.0));
        input.add(new InputNetwork(16.0, 31.8, 15.0));
        input.add(new InputNetwork(17.0, 32.8, 15.0));
        input.add(new InputNetwork(18.0, 33.8, 15.0));
        input.add(new InputNetwork(19.0, 34.8, 15.0));
        List<Point> p = new ArrayList<>();
        p.add(new Point(3d,5d));
        p.add(new Point(3d,5d));
        p.add(new Point(3d,5d));
        p.add(new Point(3d,5d));
        TrainReal t = new TrainReal(input, p);
        t.setRealPointsOutputComputed(p);
        t.setPoints(p);

        TrainReal tt = new TrainReal(input, p);
        tt.setRealPointsOutputComputed(p);
        tt.setPoints(p);

        List<TrainReal> list = new ArrayList<>();
        list.add(t);
        list.add(tt);

        IntStream.range(0, 1000).forEach(i -> {
            TrainReal ttt = new TrainReal(input, p);
            ttt.setRealPointsOutputComputed(p);
            ttt.setPoints(p);
            list.add(ttt);
        });

        SaveToFile.Saver saver = new SaveToFile.Saver("test", "1");
        SaveToFile.Saver.dumpTrajectoryAndGeneratedPart(list);
    }

    @Test
    public void dumpPopulation() throws Exception {
        new ReadConfig.Configurations();

        List<Individual> list = new ArrayList<>();
        list.add(new UncorrelatedMutation(5));
        list.add(new UncorrelatedMutation(3));
        list.add(new UncorrelatedMutation(7));
        list.add(new UncorrelatedMutation(1));
        try {
            SaveToFile.Saver.dumpPopulation("a", list);
        }catch (Exception e){
            assertEquals("Cannot save, the class is not instantiate",e.getMessage());
        }
        SaveToFile.Saver saver = new SaveToFile.Saver("test", "1");
        SaveToFile.Saver.dumpPopulation("a", list);
        SaveToFile.Saver.dumpPopulation("a", list);
        List<Individual> list3 = new ArrayList<>();
        list3.add(new UncorrelatedMutation(50));
        list3.add(new UncorrelatedMutation(30));
        list3.add(new UncorrelatedMutation(70));
        list3.add(new UncorrelatedMutation(10));
        SaveToFile.Saver.dumpPopulation("a", list3);

    }

    @Test
    public void saveFitness() throws Exception {

        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        try {
            SaveToFile.Saver.saveFitness("a", list);
        }catch (Exception e){
            assertEquals("Cannot save, the class is not instantiate",e.getMessage());
        }


        SaveToFile.Saver saver = new SaveToFile.Saver("test", "1");

        SaveToFile.Saver.saveFitness("a", list);
        SaveToFile.Saver.saveFitness("a", list);
        SaveToFile.Saver.saveFitness("a", list);

        saver = new SaveToFile.Saver("test", "1", "/Users/alessandrozonta/Desktop");
        SaveToFile.Saver.saveFitness("a", list);
        SaveToFile.Saver.saveFitness("a", list);
        SaveToFile.Saver.saveFitness("a", list);
        SaveToFile.Saver.saveFitness("a", list);

    }

    @Test
    public void dumpSetting() throws Exception {
        new SaveToFile.Saver("test", "1", "/Users/alessandrozonta/Desktop");


        new ReadConfig.Configurations();

        SaveToFile.Saver.dumpSetting(ReadConfig.Configurations.getConfig());

        gms.Config.ReadConfig config1= new gms.Config.ReadConfig();
        config1.readFile();
        SaveToFile.Saver.dumpSetting(config1);

        ConfigFile configFile = new ConfigFile();
        configFile.loadFile();
        SaveToFile.Saver.dumpSetting(configFile);

    }

    @Test
    public void saveBestGenoma() throws Exception {

        INDArray list = Nd4j.rand(1,10);

        try {
            SaveToFile.Saver.saveBestGenoma("a", list);
        }catch (Exception e){
            assertEquals("Cannot save, the class is not instantiate",e.getMessage());
        }

        new SaveToFile.Saver("test", "1", "/Users/alessandrozonta/Desktop");
        SaveToFile.Saver.saveBestGenoma("a",list);

    }




}
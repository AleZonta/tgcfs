package tgcfs.Loader;

import lgds.trajectories.Point;
import lgds.trajectories.Trajectory;
import org.junit.Test;
import tgcfs.Config.ReadConfig;
import tgcfs.Idsa.IdsaLoader;
import tgcfs.NN.InputsNetwork;
import tgcfs.Performances.SaveToFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static junit.framework.TestCase.*;

/**
 * Created by Alessandro Zonta on 16/05/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 */
public class FeederTest {
    @Test
    public void multiFeeder() throws Exception {
        new ReadConfig.Configurations();

        new SaveToFile.Saver(ReadConfig.Configurations.getName(), ReadConfig.Configurations.getExperiment(), ReadConfig.Configurations.getPath());

        Feeder system = new Feeder();
        system.loadSystem();

        IdsaLoader idsaLoader = new IdsaLoader();
        idsaLoader.InitPotentialField(system.getTrajectories());

        List<TrainReal> res = system.multiFeeder(idsaLoader);
        assertNotNull(res);
    }

    @Test
    public void obtainRealAgentSectionTrajectory() throws Exception {
    }

    @Test
    public void getNextLocation() throws Exception {
        Point myLocation = new Point(52.038615372493936, 4.29910204641889, 0d,12345.6, "123456", "14:20:10");
        Double speed = 23d;
        Double direction = 170d;
        Double distance = 500d;

        new ReadConfig.Configurations();

        new SaveToFile.Saver(ReadConfig.Configurations.getName(), ReadConfig.Configurations.getExperiment(), ReadConfig.Configurations.getPath());

        Feeder system = new Feeder();
        system.loadSystem();
        Point result = system.getNextLocation(myLocation,speed,distance,direction);
        System.out.println(result);
        System.out.println(result.getTime());

        myLocation = new Point(52.01912786045312, 4.307028977339069, 0d,12345.6, "123456", "14:20:10");
        speed = 32d;
        direction = 171d;
        distance = 520d;


        result = system.getNextLocation(myLocation,speed,distance,direction);
        System.out.println(result);
        System.out.println(result.getTime());

    }

    @Test
    public void getMaximumNumberOfTrajectories() throws Exception {
        //initialise the saving class
        new ReadConfig.Configurations();

        new SaveToFile.Saver(ReadConfig.Configurations.getName(), ReadConfig.Configurations.getExperiment(), ReadConfig.Configurations.getPath());

        Feeder system = new Feeder();
        assertNotNull(system.getMaximumNumberOfTrajectories());
    }

    @Test
    public void feeder() throws Exception {
        //initialise the saving class
        new ReadConfig.Configurations();

        new SaveToFile.Saver(ReadConfig.Configurations.getName(), ReadConfig.Configurations.getExperiment(), ReadConfig.Configurations.getPath());

        Feeder system = new Feeder();
        system.loadSystem();

        IdsaLoader idsaLoader = new IdsaLoader();
        idsaLoader.InitPotentialField(system.getTrajectories());


        IntStream.range(0,10).forEach(i -> {
            try {
                List<InputsNetwork> res = system.feeder(idsaLoader);
                assertNotNull(res);
            }catch (Exception e){
                assertEquals("Reached Maximum Number Of trajectories", e.getMessage());
            }
        });


    }

    @Test
    public void getFinished() throws Exception {
        //initialise the saving class
        new ReadConfig.Configurations();

        new SaveToFile.Saver(ReadConfig.Configurations.getName(), ReadConfig.Configurations.getExperiment(), ReadConfig.Configurations.getPath());

        Feeder system = new Feeder();
        system.loadSystem();
        Trajectory trajectory = system.getTrajectory();
        List<Point> points = system.obtainSectionTrajectory(trajectory);
        assertFalse(system.getFinished());

        IntStream.range(0,20).forEach(i -> {
            try {
                List<Point> points2 = system.obtainSectionTrajectory(trajectory);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        assertTrue(system.getFinished());

    }

    @Test
    public void getNextPoint() throws Exception {
        //initialise the saving class
        new ReadConfig.Configurations();

        new SaveToFile.Saver(ReadConfig.Configurations.getName(), ReadConfig.Configurations.getExperiment(), ReadConfig.Configurations.getPath());

        Feeder system = new Feeder();
        system.loadSystem();
        Trajectory trajectory = system.getTrajectory();
        assertNotNull(system.getNextPoint(trajectory));
    }

    @Test
    public void obtainSectionTrajectory() throws Exception {
        //initialise the saving class
        new ReadConfig.Configurations();

        new SaveToFile.Saver(ReadConfig.Configurations.getName(), ReadConfig.Configurations.getExperiment(), ReadConfig.Configurations.getPath());

        Feeder system = new Feeder();
        system.loadSystem();
        Trajectory trajectory = system.getTrajectory();
        List<Point> points = system.obtainSectionTrajectory(trajectory);
        assertNotNull(points);
        List<Point> points2 = system.obtainSectionTrajectory(trajectory);

        assertNotNull(points2);
        assertNotSame(points,points2);
        List<Point> points3 = system.obtainSectionTrajectory(trajectory);
        assertNotNull(points3);
        assertNotSame(points,points3);
        assertNotSame(points3,points2);

        System.out.println(points.toString());
        System.out.println(points2.toString());
        System.out.println(points3.toString());

    }

    @Test
    public void getTrajectories() throws Exception {//initialise the saving class
        new ReadConfig.Configurations();

        new SaveToFile.Saver(ReadConfig.Configurations.getName(), ReadConfig.Configurations.getExperiment(), ReadConfig.Configurations.getPath());

        Feeder system = new Feeder();
        system.loadSystem();
        assertNotNull(system.getTrajectories());
    }

    @Test
    public void getTrajectory() throws Exception {
        //initialise the saving class
        new ReadConfig.Configurations();

        new SaveToFile.Saver(ReadConfig.Configurations.getName(), ReadConfig.Configurations.getExperiment(), ReadConfig.Configurations.getPath());

        Feeder system = new Feeder();
        system.loadSystem();
        assertNotNull(system.getTrajectory());
    }

    @Test
    public void obtainInput() throws Exception {
        //initialise the saving class
        new ReadConfig.Configurations();

        new SaveToFile.Saver(ReadConfig.Configurations.getName(), ReadConfig.Configurations.getExperiment(), ReadConfig.Configurations.getPath());

        Feeder system = new Feeder();
        system.loadSystem();
        Trajectory a = system.getTrajectory();
        List<Point> po = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            po.add(system.getNextPoint(a));
        }
        List<InputsNetwork> res = system.obtainInput(po, 10.0);
        assertNotNull(res);
    }

    @Test
    public void fromTrajectoryToNodesInGraph() throws Exception {
        //initialise the saving class
//        ReadConfig configFile = new ReadConfig();
//        configFile.readFile();
//
//        new SaveToFile.Saver(configFile.getName(), configFile.getExperiment(), configFile.getPath());
//
//        Feeder system = new Feeder();
//        system.loadSystem();
//        Trajectory trajectory = system.getTrajectory();
//        List<InfoNode> nodes = system.fromTrajectoryToNodesInGraph(trajectory);
//        assertNotNull(nodes);
//        nodes.forEach(node -> {
//            System.out.println(node.retLat() + ", " + node.retLon());
//        });
        //not used
    }

    @Test
    public void loadSystem() throws Exception {
        //initialise the saving class
        new ReadConfig.Configurations();

        new SaveToFile.Saver(ReadConfig.Configurations.getName(), ReadConfig.Configurations.getExperiment(), ReadConfig.Configurations.getPath());

        Feeder system = new Feeder();
        system.loadSystem();
    }

}
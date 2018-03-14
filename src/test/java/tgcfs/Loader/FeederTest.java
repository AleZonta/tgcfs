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
import java.util.logging.Logger;
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
    public void retAllEdges() throws Exception {
        new ReadConfig.Configurations();
        Logger log =  Logger.getLogger(FeederTest.class.getName());
        new SaveToFile.Saver(ReadConfig.Configurations.getName(), ReadConfig.Configurations.getExperiment(), ReadConfig.Configurations.getPath(), log);
        Feeder system = new Feeder(log);
        system.loadSystem();

        assertEquals(26047,system.retAllEdges().size());
        System.out.println(system.retAllEdges().size());

    }

    @Test
    public void retAllNodes() throws Exception {
        new ReadConfig.Configurations();

        Logger log =  Logger.getLogger(FeederTest.class.getName());
        new SaveToFile.Saver(ReadConfig.Configurations.getName(), ReadConfig.Configurations.getExperiment(), ReadConfig.Configurations.getPath(), log);
        Feeder system = new Feeder(log);
        system.loadSystem();

        assertEquals(12714,system.retAllNodes().size());
        System.out.println(system.retAllNodes().size());

    }

    @Test
    public void multiFeeder() throws Exception {
        new ReadConfig.Configurations();

        Logger log =  Logger.getLogger(FeederTest.class.getName());
        new SaveToFile.Saver(ReadConfig.Configurations.getName(), ReadConfig.Configurations.getExperiment(), ReadConfig.Configurations.getPath(), log);
        Feeder system = new Feeder(log);
        system.loadSystem();

        IdsaLoader idsaLoader = new IdsaLoader(log);
        idsaLoader.InitPotentialField(system.getTrajectories());

        List<TrainReal> res = system.multiFeeder(idsaLoader, null);
        res = system.multiFeeder(idsaLoader, res);
        assertNotNull(res);
    }

    @Test
    public void obtainRealAgentSectionTrajectory() throws Exception {
    }

    @Test
    public void getNextLocation() throws Exception {
//        Point myLocation = new Point(52.038615372493936, 4.29910204641889, 0d,12345.6, "123456", "14:20:10");
//        Double speed = 23d;
//        Double direction = 170d;
//        Double distance = 500d;
//
//        new ReadConfig.Configurations();
//
//        Logger log =  Logger.getLogger(FeederTest.class.getName());
//        new SaveToFile.Saver(ReadConfig.Configurations.getName(), ReadConfig.Configurations.getExperiment(), ReadConfig.Configurations.getPath(), log);
//        Feeder system = new Feeder(log);
//        system.loadSystem();
////        Point result = system.getNextLocation(myLocation,speed,distance,direction);
//        Point result = system.getNextLocation(myLocation,speed,direction);
//        System.out.println(result);
//        System.out.println(result.getTime());
//
//        myLocation = new Point(52.01912786045312, 4.307028977339069, 0d,12345.6, "123456", "14:20:10");
//        speed = 32d;
//        direction = 171d;
//        distance = 520d;
//
//
//       // result = system.getNextLocation(myLocation,speed,distance,direction);
//        result = system.getNextLocation(myLocation,speed,direction);
//        System.out.println(result);
//        System.out.println(result.getTime());

//        Point myLocation = new Point(52.320961, 4.869281, 0d,12345.6, "123456", "14:20:10");
//        double speed = 10;
//        double bearing = 358.31;
//        new ReadConfig.Configurations();
//
//        Logger log =  Logger.getLogger(FeederTest.class.getName());
//        Feeder system = new Feeder(log);
//        system.loadSystem();
//        Point result = system.getNextLocationNoGraph(myLocation,speed,bearing);
//        System.out.println(result);
//
//        result = system.getNextLocationNoGraph(myLocation, -10.0,bearing);
//        System.out.println(result);

        Point myLocation = new Point(1.6049956, -8.0676865, 0d,12345.6, "123456", "14:20:10");
        double speed = 1.6713278182451559;
        double bearing = 274.83141497299533;
        new ReadConfig.Configurations();

        Logger log =  Logger.getLogger(FeederTest.class.getName());
        Feeder system = new Feeder(log);
        system.loadSystem();
//        Point result = system.getNextLocationNoGraph(myLocation,speed,bearing, 0.4);
//        System.out.println(result);

        Point result = system.getNextLocationNoGraph(myLocation, 1.4405680847816191,259.1645363810223, 0.4);
        System.out.println(result);
        result = system.getNextLocationNoGraph(myLocation, 1.4405680847816191,3.0574673069991687, 0.4);
        System.out.println(result);
        result = system.getNextLocationNoGraph(myLocation, 1.5,3.0574673069991687, 0.4);
        System.out.println(result);
        result = system.getNextLocationNoGraph(myLocation, 1.6713278182451559,3.0574673069991687, 0.4);
        System.out.println(result);

//        OutputNetwork{ speed=100.58245211839676, bearing=-37.51176595687866, } -> (52.0461027, 4.3312338)
//
//        OutputNetwork{ speed=53.486526012420654, bearing=-77.73454785346985, } -> (52.0461027, 4.3312338)
//
//        OutputNetwork{ speed=94.09392401576042, bearing=-34.8400604724884, } -> (52.0461027, 4.3312338)
//
//        OutputNetwork{ speed=84.79715287685394, bearing=-52.23324179649353, } -> (52.0461027, 4.3312338)
//
//        OutputNetwork{ speed=96.64435349404812, bearing=-51.37709677219391, } -> (52.0461027, 4.3312338)

    }

    @Test
    public void getMaximumNumberOfTrajectories() throws Exception {
        //initialise the saving class
        new ReadConfig.Configurations();

        Logger log =  Logger.getLogger(FeederTest.class.getName());
        new SaveToFile.Saver(ReadConfig.Configurations.getName(), ReadConfig.Configurations.getExperiment(), ReadConfig.Configurations.getPath(), log);
        Feeder system = new Feeder(log);
        assertNotNull(system.getMaximumNumberOfTrajectories());
    }

    @Test
    public void feeder() throws Exception {
        //initialise the saving class
        new ReadConfig.Configurations();

        Logger log =  Logger.getLogger(FeederTest.class.getName());
        new SaveToFile.Saver(ReadConfig.Configurations.getName(), ReadConfig.Configurations.getExperiment(), ReadConfig.Configurations.getPath(), log);
        Feeder system = new Feeder(log);
        system.loadSystem();

        IdsaLoader idsaLoader = new IdsaLoader(log);
        idsaLoader.InitPotentialField(system.getTrajectories());


//        IntStream.range(0,1000).forEach(i -> {
//            try {
//                List<InputsNetwork> res = system.feeder(idsaLoader);
//                assertNotNull(res);
//            }catch (Exception e){
//                System.out.println(e.getMessage());
//            }
//        });


    }

    @Test
    public void getFinished() throws Exception {
        //initialise the saving class
        new ReadConfig.Configurations();

        Logger log =  Logger.getLogger(FeederTest.class.getName());
        new SaveToFile.Saver(ReadConfig.Configurations.getName(), ReadConfig.Configurations.getExperiment(), ReadConfig.Configurations.getPath(), log);
        Feeder system = new Feeder(log);
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

        Logger log =  Logger.getLogger(FeederTest.class.getName());
        new SaveToFile.Saver(ReadConfig.Configurations.getName(), ReadConfig.Configurations.getExperiment(), ReadConfig.Configurations.getPath(), log);
        Feeder system = new Feeder(log);
        system.loadSystem();
        Trajectory trajectory = system.getTrajectory();
        assertNotNull(system.getNextPoint(trajectory));
    }

    @Test
    public void obtainSectionTrajectory() throws Exception {
        //initialise the saving class
        new ReadConfig.Configurations();

        Logger log =  Logger.getLogger(FeederTest.class.getName());
        new SaveToFile.Saver(ReadConfig.Configurations.getName(), ReadConfig.Configurations.getExperiment(), ReadConfig.Configurations.getPath(), log);
        Feeder system = new Feeder(log);
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

        Logger log =  Logger.getLogger(FeederTest.class.getName());
        new SaveToFile.Saver(ReadConfig.Configurations.getName(), ReadConfig.Configurations.getExperiment(), ReadConfig.Configurations.getPath(), log);
        Feeder system = new Feeder(log);
        system.loadSystem();
        assertNotNull(system.getTrajectories());
    }

    @Test
    public void getTrajectory() throws Exception {
        //initialise the saving class
        new ReadConfig.Configurations();

        Logger log =  Logger.getLogger(FeederTest.class.getName());
        new SaveToFile.Saver(ReadConfig.Configurations.getName(), ReadConfig.Configurations.getExperiment(), ReadConfig.Configurations.getPath(), log);
        Feeder system = new Feeder(log);
        system.loadSystem();
        assertNotNull(system.getTrajectory());
    }

    @Test
    public void obtainInput() throws Exception {
        //initialise the saving class
        new ReadConfig.Configurations();

        Logger log =  Logger.getLogger(FeederTest.class.getName());
        new SaveToFile.Saver(ReadConfig.Configurations.getName(), ReadConfig.Configurations.getExperiment(), ReadConfig.Configurations.getPath(), log);
        Feeder system = new Feeder(log);;
        system.loadSystem();
        Trajectory a = system.getTrajectory();
        List<Point> po = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            po.add(system.getNextPoint(a));
        }
        List<InputsNetwork> res = system.obtainInput(po, 10.0, null, null);
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

        Logger log =  Logger.getLogger(FeederTest.class.getName());
        new SaveToFile.Saver(ReadConfig.Configurations.getName(), ReadConfig.Configurations.getExperiment(), ReadConfig.Configurations.getPath(), log);
        Feeder system = new Feeder(log);
        system.loadSystem();
    }

}
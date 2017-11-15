package tgcfs.InputOutput;

import org.junit.Test;
import tgcfs.Agents.OutputNetwork;
import tgcfs.Config.ReadConfig;
import tgcfs.EA.AgentsTest;
import tgcfs.Loader.Feeder;
import tgcfs.Loader.TrainReal;
import tgcfs.NN.OutputsNetwork;
import tgcfs.Utils.PointWithBearing;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Alessandro Zonta on 14/11/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 */
public class FollowingTheGraphTest {
    @Test
    public void transform() throws Exception {
        new ReadConfig.Configurations();
        Logger log =  Logger.getLogger(AgentsTest.class.getName());
        Feeder feeder = new Feeder(log);
        feeder.loadSystem();

        FollowingTheGraph followingTheGraph = new FollowingTheGraph();
        followingTheGraph.setFeeder(feeder);

        PointWithBearing oldPoint = new PointWithBearing(52.0459872, 4.3314804,0d,0d,"123456", "14:20:10", -30.96375653207352);
        followingTheGraph.setLastPoint(oldPoint);

        OutputNetwork o1r = new OutputNetwork(1.48283620794667213, -29.357753538261107);
        List<OutputsNetwork> realOutputNetworkList = new ArrayList<>();
        realOutputNetworkList.add(o1r);


        OutputNetwork o1 = new OutputNetwork(2.875218838453293, -20.186743587255478);
        List<OutputsNetwork> outputNetworkList = new ArrayList<>();
        outputNetworkList.add(o1);
        TrainReal t1 = new TrainReal(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), null, realOutputNetworkList, new ArrayList<>(),new ArrayList<>(), null, null, outputNetworkList, new ArrayList<>(), null, new ArrayList<>());
        System.out.println(followingTheGraph.transform(t1));
        System.out.println("---------------");


        followingTheGraph.setLastPoint(oldPoint);
        OutputNetwork o2 = new OutputNetwork(2.7596619725227356, 89.50664520263672);
        outputNetworkList = new ArrayList<>();
        outputNetworkList.add(o2);
        TrainReal t2 = new TrainReal(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), null, realOutputNetworkList, new ArrayList<>(),new ArrayList<>(), null, null, outputNetworkList, new ArrayList<>(), null, new ArrayList<>());
        System.out.println(followingTheGraph.transform(t2));
        System.out.println("---------------");

        followingTheGraph.setLastPoint(oldPoint);
        OutputNetwork o3 = new OutputNetwork(7.145455032587051, -70.59308588504791);
        outputNetworkList = new ArrayList<>();
        outputNetworkList.add(o3);
        TrainReal t3 = new TrainReal(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), null, realOutputNetworkList, new ArrayList<>(),new ArrayList<>(), null, null, outputNetworkList, new ArrayList<>(), null, new ArrayList<>());
        System.out.println(followingTheGraph.transform(t3));
        System.out.println("---------------");

        followingTheGraph.setLastPoint(oldPoint);
        OutputNetwork o4 = new OutputNetwork(5.469728335738182, -7.98716314136982);
        outputNetworkList = new ArrayList<>();
        outputNetworkList.add(o4);
        TrainReal t4 = new TrainReal(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), null, realOutputNetworkList, new ArrayList<>(),new ArrayList<>(), null, null, outputNetworkList, new ArrayList<>(), null, new ArrayList<>());
        System.out.println(followingTheGraph.transform(t4));
        System.out.println("---------------");

        followingTheGraph.setLastPoint(oldPoint);
        OutputNetwork o5 = new OutputNetwork(7.2166842222213745, 33.61255556344986);
        outputNetworkList = new ArrayList<>();
        outputNetworkList.add(o5);
        TrainReal t5 = new TrainReal(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), null, realOutputNetworkList, new ArrayList<>(),new ArrayList<>(), null, null, outputNetworkList, new ArrayList<>(), null, new ArrayList<>());
        System.out.println(followingTheGraph.transform(t5));
        System.out.println("---------------");

    }

}
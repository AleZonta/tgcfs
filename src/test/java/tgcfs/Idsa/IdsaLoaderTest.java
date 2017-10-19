package tgcfs.Idsa;

import lgds.trajectories.Point;
import lgds.trajectories.Trajectory;
import org.junit.Test;
import tgcfs.Config.ReadConfig;
import tgcfs.Performances.SaveToFile;
import tgcfs.Routing.Routes;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static junit.framework.TestCase.assertNotNull;

/**
 * Created by Alessandro Zonta on 23/05/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 */
public class IdsaLoaderTest {

    @Test
    public void initPotentialField1() throws Exception {
        throw new Exception("Not tested");
    }

    @Test
    public void retPossibleTarget() throws Exception {
        throw new Exception("Not tested");
    }

    @Test
    public void generatePicture() throws Exception {
        new ReadConfig.Configurations();

        new SaveToFile.Saver(ReadConfig.Configurations.getName(), ReadConfig.Configurations.getExperiment(), ReadConfig.Configurations.getPath(),Logger.getLogger(IdsaLoaderTest.class.getName()));

        IdsaLoader loader = new IdsaLoader(200, Logger.getLogger(IdsaLoaderTest.class.getName()));

        Routes routes = new Routes(Logger.getLogger(IdsaLoaderTest.class.getName()));
        routes.readTrajectories();

        loader.InitPotentialField(routes.getTra());

        Trajectory tra = routes.getNextTrajectory();
        tra = routes.getNextTrajectory();
        List<Point> p = new ArrayList<>();
        for(int t = 0; t < 200; t++){
            p.add(routes.getNextPosition(tra));
        }

        loader.generatePicture(p);
    }


    @Test
    public void returnAttraction() throws Exception {
        //initialise the saving class
        new ReadConfig.Configurations();
        Logger log =  Logger.getLogger(IdsaLoaderTest.class.getName());
        new SaveToFile.Saver(ReadConfig.Configurations.getName(), ReadConfig.Configurations.getExperiment(), ReadConfig.Configurations.getPath(), log);


        IdsaLoader loader = new IdsaLoader(log);

        loader = new IdsaLoader(20, log);

        Routes routes = new Routes(log);
        routes.readTrajectories();

        loader.InitPotentialField(routes.getTra());

        Trajectory tra = routes.getNextTrajectory();

        loader.compute(routes.getNextPosition(tra));
        loader.compute(routes.getNextPosition(tra));
        loader.compute(routes.getNextPosition(tra));
        loader.compute(routes.getNextPosition(tra));
        Point lastPoint = routes.getNextPosition(tra);
        loader.compute(lastPoint);

        assertNotNull(loader.returnAttraction(lastPoint));
    }

    @Test
    public void resetAPF() throws Exception {
        //initialise the saving class
        new ReadConfig.Configurations();

        Logger log =  Logger.getLogger(IdsaLoaderTest.class.getName());
        new SaveToFile.Saver(ReadConfig.Configurations.getName(), ReadConfig.Configurations.getExperiment(), ReadConfig.Configurations.getPath(), log);
        IdsaLoader loader = new IdsaLoader(log);

        loader = new IdsaLoader(20, log);


        Routes routes = new Routes(log);
        routes.readTrajectories();

        loader.InitPotentialField(routes.getTra());
        loader.resetAPF();
    }

    @Test
    public void compute() throws Exception {
        //initialise the saving class
        new ReadConfig.Configurations();

        Logger log =  Logger.getLogger(IdsaLoaderTest.class.getName());
        new SaveToFile.Saver(ReadConfig.Configurations.getName(), ReadConfig.Configurations.getExperiment(), ReadConfig.Configurations.getPath(), log);
        IdsaLoader loader = new IdsaLoader(log);

        loader = new IdsaLoader(20, log);


        Routes routes = new Routes(log);
        routes.readTrajectories();

        loader.InitPotentialField(routes.getTra());

        Trajectory tra = routes.getNextTrajectory();

        loader.compute(routes.getNextPosition(tra));
        loader.compute(routes.getNextPosition(tra));
        loader.compute(routes.getNextPosition(tra));
        loader.compute(routes.getNextPosition(tra));
        loader.compute(routes.getNextPosition(tra));

    }

    @Test
    public void initPotentialField() throws Exception {
        //initialise the saving class
        new ReadConfig.Configurations();

        Logger log =  Logger.getLogger(IdsaLoaderTest.class.getName());
        new SaveToFile.Saver(ReadConfig.Configurations.getName(), ReadConfig.Configurations.getExperiment(), ReadConfig.Configurations.getPath(), log);
        IdsaLoader loader = new IdsaLoader(log);

        loader = new IdsaLoader(20, log);


        Routes routes = new Routes(log);
        routes.readTrajectories();

        loader.InitPotentialField(routes.getTra());
    }

}
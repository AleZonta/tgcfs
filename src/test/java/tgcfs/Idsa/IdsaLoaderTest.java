package tgcfs.Idsa;

import lgds.trajectories.Point;
import lgds.trajectories.Trajectory;
import org.junit.Test;
import tgcfs.Config.ReadConfig;
import tgcfs.Performances.SaveToFile;
import tgcfs.Routing.Routes;

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
    public void returnAttraction() throws Exception {
        //initialise the saving class
        ReadConfig configFile = new ReadConfig();
        configFile.readFile();

        new SaveToFile.Saver(configFile.getName(), configFile.getExperiment(), configFile.getPath());

        IdsaLoader loader = new IdsaLoader();

        loader = new IdsaLoader(20);


        ReadConfig conf = new ReadConfig();
        conf.readFile();
        Routes routes = new Routes(conf);
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
        ReadConfig configFile = new ReadConfig();
        configFile.readFile();

        new SaveToFile.Saver(configFile.getName(), configFile.getExperiment(), configFile.getPath());

        IdsaLoader loader = new IdsaLoader();
        loader = new IdsaLoader(20);
        ReadConfig conf = new ReadConfig();
        conf.readFile();
        Routes routes = new Routes(conf);
        routes.readTrajectories();

        loader.InitPotentialField(routes.getTra());
        loader.resetAPF();
    }

    @Test
    public void compute() throws Exception {
        //initialise the saving class
        ReadConfig configFile = new ReadConfig();
        configFile.readFile();

        new SaveToFile.Saver(configFile.getName(), configFile.getExperiment(), configFile.getPath());

        IdsaLoader loader = new IdsaLoader();

        loader = new IdsaLoader(20);


        ReadConfig conf = new ReadConfig();
        conf.readFile();
        Routes routes = new Routes(conf);
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
        ReadConfig configFile = new ReadConfig();
        configFile.readFile();

        new SaveToFile.Saver(configFile.getName(), configFile.getExperiment(), configFile.getPath());

        IdsaLoader loader = new IdsaLoader();

        loader = new IdsaLoader(20);


        ReadConfig conf = new ReadConfig();
        conf.readFile();
        Routes routes = new Routes(conf);
        routes.readTrajectories();

        loader.InitPotentialField(routes.getTra());
    }

}
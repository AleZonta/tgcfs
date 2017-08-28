package tgcfs.Idsa;

import lgds.trajectories.Point;
import lgds.trajectories.Trajectory;
import org.junit.Test;
import tgcfs.Config.ReadConfig;
import tgcfs.Performances.SaveToFile;
import tgcfs.Routing.Routes;

import java.util.ArrayList;
import java.util.List;

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
    }

    @Test
    public void retPossibleTarget() throws Exception {
    }

    @Test
    public void generatePicture() throws Exception {
        new ReadConfig.Configurations();

        new SaveToFile.Saver(ReadConfig.Configurations.getName(), ReadConfig.Configurations.getExperiment(), ReadConfig.Configurations.getPath());

        IdsaLoader loader = new IdsaLoader();

        loader = new IdsaLoader(20);

        Routes routes = new Routes();
        routes.readTrajectories();

        loader.InitPotentialField(routes.getTra());

        Trajectory tra = routes.getNextTrajectory();
        List<Point> p = new ArrayList<>();
        for(int t = 0; t < 20; t++){
            p.add(routes.getNextPosition(tra));
        }

        loader.generatePicture(p);
    }


    @Test
    public void returnAttraction() throws Exception {
        //initialise the saving class
        new ReadConfig.Configurations();

        new SaveToFile.Saver(ReadConfig.Configurations.getName(), ReadConfig.Configurations.getExperiment(), ReadConfig.Configurations.getPath());

        IdsaLoader loader = new IdsaLoader();

        loader = new IdsaLoader(20);

        Routes routes = new Routes();
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

        new SaveToFile.Saver(ReadConfig.Configurations.getName(), ReadConfig.Configurations.getExperiment(), ReadConfig.Configurations.getPath());

        IdsaLoader loader = new IdsaLoader();
        loader = new IdsaLoader(20);


        Routes routes = new Routes();
        routes.readTrajectories();

        loader.InitPotentialField(routes.getTra());
        loader.resetAPF();
    }

    @Test
    public void compute() throws Exception {
        //initialise the saving class
        new ReadConfig.Configurations();

        new SaveToFile.Saver(ReadConfig.Configurations.getName(), ReadConfig.Configurations.getExperiment(), ReadConfig.Configurations.getPath());

        IdsaLoader loader = new IdsaLoader();

        loader = new IdsaLoader(20);


        Routes routes = new Routes();
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

        new SaveToFile.Saver(ReadConfig.Configurations.getName(), ReadConfig.Configurations.getExperiment(), ReadConfig.Configurations.getPath());

        IdsaLoader loader = new IdsaLoader();

        loader = new IdsaLoader(20);


        Routes routes = new Routes();
        routes.readTrajectories();

        loader.InitPotentialField(routes.getTra());
    }

}
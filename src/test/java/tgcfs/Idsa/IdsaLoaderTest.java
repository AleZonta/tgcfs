package tgcfs.Idsa;

import lgds.trajectories.Trajectory;
import org.junit.Test;
import tgcfs.Config.ReadConfig;
import tgcfs.Routing.Routes;

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
    public void compute() throws Exception {
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
        IdsaLoader loader = new IdsaLoader();

        loader = new IdsaLoader(20);


        ReadConfig conf = new ReadConfig();
        conf.readFile();
        Routes routes = new Routes(conf);
        routes.readTrajectories();

        loader.InitPotentialField(routes.getTra());
    }

}
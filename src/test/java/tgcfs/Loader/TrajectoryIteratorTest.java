package tgcfs.Loader;

import org.junit.Test;
import org.nd4j.linalg.dataset.DataSet;
import tgcfs.Config.ReadConfig;
import tgcfs.Idsa.IdsaLoader;
import tgcfs.Performances.SaveToFile;
import tgcfs.Routing.Routes;

import java.util.logging.Logger;

/**
 * Created by Alessandro Zonta on 26/03/2018.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 */
public class TrajectoryIteratorTest {

    @Test
    public void next() throws Exception {
        //initialise the saving class
        new ReadConfig.Configurations();

        Logger log =  Logger.getLogger(FeederTest.class.getName());
        new SaveToFile.Saver(ReadConfig.Configurations.getName(), ReadConfig.Configurations.getExperiment(), ReadConfig.Configurations.getPath(), log);
        Feeder system = new Feeder(log);

        system.loadSystem();
        system.getTrajectories().createTrainingAndTest();


        Routes routes = new Routes(log);
        routes.readTrajectories();

        IdsaLoader loader = new IdsaLoader(routes.getTra().getTrajectories().size(), log);

        TrajectoryIterator iterator = new TrajectoryIterator(20,15,loader,system, true);
        DataSet nextDataset = iterator.next();

        TrajectoryIterator iteratorTest = new TrajectoryIterator(10,15,loader,system, false);
        DataSet nextDatasetTest = iteratorTest.next();

    }
}
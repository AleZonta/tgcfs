package tgcfs.Agents;

import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import tgcfs.Agents.Models.Clax;
import tgcfs.Config.ReadConfig;
import tgcfs.Idsa.IdsaLoader;
import tgcfs.Loader.Feeder;
import tgcfs.Performances.SaveToFile;

import java.util.logging.Logger;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertFalse;

/**
 * Created by Alessandro Zonta on 15/08/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 */
public class ClaxTest {
    @Test
    public void getArrayLength() throws Exception {
        new ReadConfig.Configurations();

        Logger log =  Logger.getLogger(ClaxTest.class.getName());
        new SaveToFile.Saver(ReadConfig.Configurations.getName(), ReadConfig.Configurations.getExperiment(), ReadConfig.Configurations.getPath(), log);



        Feeder system = new Feeder(log);
        system.loadSystem();

        IdsaLoader idsaLoader = new IdsaLoader(log);
        idsaLoader.InitPotentialField(system.getTrajectories());

        Clax clax = new Clax(system,idsaLoader);
        Integer num = clax.getArrayLength();
        assertNotNull(num);
        assertEquals(93, num.longValue());

    }

    @Test
    public void getWeights() throws Exception {
        new ReadConfig.Configurations();

        Logger log =  Logger.getLogger(ClaxTest.class.getName());
        new SaveToFile.Saver(ReadConfig.Configurations.getName(), ReadConfig.Configurations.getExperiment(), ReadConfig.Configurations.getPath(), log);

        Feeder system = new Feeder(log);
        system.loadSystem();

        IdsaLoader idsaLoader = new IdsaLoader(log);
        idsaLoader.InitPotentialField(system.getTrajectories());

        Clax clax = new Clax(system,idsaLoader);
        INDArray res = clax.getWeights();
        assertNotNull(res);
        System.out.println(res);
    }

    @Test
    public void setWeights() throws Exception {
        new ReadConfig.Configurations();

        Logger log =  Logger.getLogger(ClaxTest.class.getName());
        new SaveToFile.Saver(ReadConfig.Configurations.getName(), ReadConfig.Configurations.getExperiment(), ReadConfig.Configurations.getPath(), log);

        Feeder system = new Feeder(log);
        system.loadSystem();

        IdsaLoader idsaLoader = new IdsaLoader(log);
        idsaLoader.InitPotentialField(system.getTrajectories());

        Clax clax = new Clax(system,idsaLoader);

        INDArray indArray = Nd4j.ones(clax.getArrayLength());
        clax.setWeights(indArray);

        INDArray res = clax.getWeights();
        assertNotNull(res);
        assertEquals(1.0, res.getDouble(36));
        System.out.println(res);
    }

    @Test
    public void deepCopy() throws Exception {
        new ReadConfig.Configurations();

        Logger log =  Logger.getLogger(ClaxTest.class.getName());
        new SaveToFile.Saver(ReadConfig.Configurations.getName(), ReadConfig.Configurations.getExperiment(), ReadConfig.Configurations.getPath(), log);
        Feeder system = new Feeder(log);
        system.loadSystem();

        IdsaLoader idsaLoader = new IdsaLoader(log);
        idsaLoader.InitPotentialField(system.getTrajectories());

        Clax clax = new Clax(system,idsaLoader);
        Clax secpmd = (Clax) clax.deepCopy();
        assertFalse(clax.equals(secpmd));
    }

    @Test
    public void fit() throws Exception {
        new ReadConfig.Configurations();

        Logger log =  Logger.getLogger(ClaxTest.class.getName());
        new SaveToFile.Saver(ReadConfig.Configurations.getName(), ReadConfig.Configurations.getExperiment(), ReadConfig.Configurations.getPath(), log);
        Feeder system = new Feeder(log);
        system.loadSystem();

        IdsaLoader idsaLoader = new IdsaLoader(log);
        idsaLoader.InitPotentialField(system.getTrajectories());

        Clax clax = new Clax(system,idsaLoader);
        try{
            clax.fit(null,null);
        }catch (NotImplementedException e){

        }
    }

    @Test
    public void computeOutput() throws Exception {
        new ReadConfig.Configurations();

        Logger log =  Logger.getLogger(ClaxTest.class.getName());
        new SaveToFile.Saver(ReadConfig.Configurations.getName(), ReadConfig.Configurations.getExperiment(), ReadConfig.Configurations.getPath(), log);

        Feeder system = new Feeder(log);
        system.loadSystem();

        IdsaLoader idsaLoader = new IdsaLoader(log);
        idsaLoader.InitPotentialField(system.getTrajectories());

        Clax clax = new Clax(system,idsaLoader);
        try{
            clax.computeOutput(null);
        }catch (NotImplementedException e){

        }
    }

    @Test
    public void computeTrajectory() throws Exception {
        throw new Exception("Need to wait the db");
    }


}
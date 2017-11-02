package tgcfs.InputOutput;

import org.junit.Test;
import tgcfs.Config.ReadConfig;

import java.util.logging.Logger;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by Alessandro Zonta on 02/11/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 */
public class LoadExternalPopulationTest {
    @Test
    public void readFile() throws Exception {
        new ReadConfig.Configurations();

        Logger log =  Logger.getLogger(LoadExternalPopulationTest.class.getName());

        LoadExternalPopulation load = new LoadExternalPopulation(log);
        try {
            load.readFile();
        }catch (Exception e){
            assertEquals("Agent population size required is not the same as the one loaded", e.getLocalizedMessage());
        }
    }

}
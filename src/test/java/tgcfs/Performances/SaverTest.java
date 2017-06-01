package tgcfs.Performances;

import nl.tno.idsa.framework.config.ConfigFile;
import org.junit.Test;
import tgcfs.Config.ReadConfig;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by Alessandro Zonta on 01/06/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 */
public class SaverTest {
    @Test
    public void saveFitness() throws Exception {

        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        try {
            SaveToFile.Saver.saveFitness("a", list);
        }catch (Exception e){
            assertEquals("Cannot save, the class is not instantiate",e.getMessage());
        }


        SaveToFile.Saver saver = new SaveToFile.Saver("test", "1");

        SaveToFile.Saver.saveFitness("a", list);
        SaveToFile.Saver.saveFitness("a", list);
        SaveToFile.Saver.saveFitness("a", list);

        saver = new SaveToFile.Saver("test", "1", "/Users/alessandrozonta/Desktop");
        SaveToFile.Saver.saveFitness("a", list);
        SaveToFile.Saver.saveFitness("a", list);
        SaveToFile.Saver.saveFitness("a", list);
        SaveToFile.Saver.saveFitness("a", list);

    }

    @Test
    public void dumpSetting() throws Exception {
        new SaveToFile.Saver("test", "1", "/Users/alessandrozonta/Desktop");


        ReadConfig config = new ReadConfig();
        config.readFile();
        SaveToFile.Saver.dumpSetting(config);

        gms.Config.ReadConfig config1= new gms.Config.ReadConfig();
        config1.readFile();
        SaveToFile.Saver.dumpSetting(config1);

        ConfigFile configFile = new ConfigFile();
        configFile.loadFile();
        SaveToFile.Saver.dumpSetting(configFile);

    }

    @Test
    public void saveBestGenoma() throws Exception {

        List<Double> list = new ArrayList<>();
        list.add(1.0);
        list.add(2.0);
        list.add(3.0);
        list.add(4.0);
        list.add(4.0);
        list.add(4.0);
        list.add(4.0);
        list.add(4.0);
        list.add(4.0);
        list.add(4.0);

        try {
            SaveToFile.Saver.saveBestGenoma("a", list);
        }catch (Exception e){
            assertEquals("Cannot save, the class is not instantiate",e.getMessage());
        }

        new SaveToFile.Saver("test", "1", "/Users/alessandrozonta/Desktop");
        SaveToFile.Saver.saveBestGenoma("a",list);

    }

}
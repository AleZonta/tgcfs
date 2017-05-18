package tgcfs.Config;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.TestCase.*;

/**
 * Created by Alessandro Zonta on 11/05/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 */
public class ReadConfigTest {
    @Test
    public void getHowManySplitting() throws Exception {
        //test if I return a location -> that is not null
        ReadConfig conf = new ReadConfig();
        try {
            conf.getHowManySplitting();
        } catch (Exception e) {
            assertTrue(e.getMessage().equals("Try to access config file before reading it.") );
        }
        try {
            conf.readFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(conf.getHowManySplitting());
    }

    @Test
    public void getTrajectoriesType() throws Exception {
        //test if I return a location -> that is not null
        ReadConfig conf = new ReadConfig();
        try {
            conf.getTrajectoriesType();
        } catch (Exception e) {
            assertTrue(e.getMessage().equals("Try to access config file before reading it.") );
        }
        try {
            conf.readFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(conf.getTrajectoriesType());
    }


    @Test
    public void readFile() throws Exception {
        //test if I read the file without exception
        //the name of the file is hardcoded
        ReadConfig conf = new ReadConfig();
        try {
            conf.readFile();
        } catch (Exception e) {
            assertTrue(e.getMessage().equals("TrajectoriesType is wrong or missing.") || e.getMessage().equals("FileLocation is missing.") || e.getMessage().equals("Config file not found.") || e.getMessage().equals("JSON file not well formatted."));
        }

    }

}


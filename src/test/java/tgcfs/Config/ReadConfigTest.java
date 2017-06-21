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
    public void getDumpPop() throws Exception {
        //test if I return a location -> that is not null
        ReadConfig conf = new ReadConfig();
        try {
            conf.getDumpPop();
        } catch (Exception e) {
            assertTrue(e.getMessage().equals("Try to access config file before reading it.") );
        }
        try {
            conf.readFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(conf.getDumpPop());
    }

    @Test
    public void getLoadDumpPop() throws Exception {
        //test if I return a location -> that is not null
        ReadConfig conf = new ReadConfig();
        try {
            conf.getLoadDumpPop();
        } catch (Exception e) {
            assertTrue(e.getMessage().equals("Try to access config file before reading it.") );
        }
        try {
            conf.readFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(conf.getLoadDumpPop());
    }

    @Test
    public void toStringTest() throws Exception {
        ReadConfig conf = new ReadConfig();
        assertNotNull(conf.toString());
    }

    @Test
    public void getName() throws Exception {
        //test if I return a location -> that is not null
        ReadConfig conf = new ReadConfig();
        try {
            conf.getName();
        } catch (Exception e) {
            assertTrue(e.getMessage().equals("Try to access config file before reading it.") );
        }
        try {
            conf.readFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(conf.getName());
    }

    @Test
    public void getExperiment() throws Exception {
        //test if I return a location -> that is not null
        ReadConfig conf = new ReadConfig();
        try {
            conf.getExperiment();
        } catch (Exception e) {
            assertTrue(e.getMessage().equals("Try to access config file before reading it.") );
        }
        try {
            conf.readFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(conf.getExperiment());
    }

    @Test
    public void getPath() throws Exception {
        //test if I return a location -> that is not null
        ReadConfig conf = new ReadConfig();
        try {
            conf.getPath();
        } catch (Exception e) {
            assertTrue(e.getMessage().equals("Try to access config file before reading it.") );
        }
        try {
            conf.readFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(conf.getPath());
    }

    @Test
    public void getMaxGenerations() throws Exception {
        //test if I return a location -> that is not null
        ReadConfig conf = new ReadConfig();
        try {
            conf.getMaxGenerations();
        } catch (Exception e) {
            assertTrue(e.getMessage().equals("Try to access config file before reading it.") );
        }
        try {
            conf.readFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(conf.getMaxGenerations());
    }

    @Test
    public void getHowManyTrajectories() throws Exception {
        //test if I return a location -> that is not null
        ReadConfig conf = new ReadConfig();
        try {
            conf.getHowManyTrajectories();
        } catch (Exception e) {
            assertTrue(e.getMessage().equals("Try to access config file before reading it.") );
        }
        try {
            conf.readFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(conf.getHowManyTrajectories());
    }

    @Test
    public void getAgentTimeSteps() throws Exception {
        //test if I return a location -> that is not null
        ReadConfig conf = new ReadConfig();
        try {
            conf.getAgentTimeSteps();
        } catch (Exception e) {
            assertTrue(e.getMessage().equals("Try to access config file before reading it.") );
        }
        try {
            conf.readFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(conf.getAgentTimeSteps());
    }

    @Test
    public void getClassifierTimeSteps() throws Exception {
        //test if I return a location -> that is not null
        ReadConfig conf = new ReadConfig();
        try {
            conf.getClassifierTimeSteps();
        } catch (Exception e) {
            assertTrue(e.getMessage().equals("Try to access config file before reading it.") );
        }
        try {
            conf.readFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(conf.getClassifierTimeSteps());
    }

    @Test
    public void getHiddenLayersAgent() throws Exception {
        //test if I return a location -> that is not null
        ReadConfig conf = new ReadConfig();
        try {
            conf.getHiddenLayersAgent();
        } catch (Exception e) {
            assertTrue(e.getMessage().equals("Try to access config file before reading it.") );
        }
        try {
            conf.readFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(conf.getHiddenLayersAgent());
    }

    @Test
    public void getHiddenNeuronsAgent() throws Exception {
        //test if I return a location -> that is not null
        ReadConfig conf = new ReadConfig();
        try {
            conf.getHiddenNeuronsAgent();
        } catch (Exception e) {
            assertTrue(e.getMessage().equals("Try to access config file before reading it.") );
        }
        try {
            conf.readFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(conf.getHiddenNeuronsAgent());
    }

    @Test
    public void getHiddenNeuronsClassifier() throws Exception {
        //test if I return a location -> that is not null
        ReadConfig conf = new ReadConfig();
        try {
            conf.getHiddenNeuronsClassifier();
        } catch (Exception e) {
            assertTrue(e.getMessage().equals("Try to access config file before reading it.") );
        }
        try {
            conf.readFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(conf.getHiddenNeuronsClassifier());
    }

    @Test
    public void getAgentOffspringSize() throws Exception {
        //test if I return a location -> that is not null
        ReadConfig conf = new ReadConfig();
        try {
            conf.getAgentOffspringSize();
        } catch (Exception e) {
            assertTrue(e.getMessage().equals("Try to access config file before reading it.") );
        }
        try {
            conf.readFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(conf.getAgentOffspringSize());
    }

    @Test
    public void getAgentPopulationSize() throws Exception {
        //test if I return a location -> that is not null
        ReadConfig conf = new ReadConfig();
        try {
            conf.getAgentPopulationSize();
        } catch (Exception e) {
            assertTrue(e.getMessage().equals("Try to access config file before reading it.") );
        }
        try {
            conf.readFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(conf.getAgentPopulationSize());
    }

    @Test
    public void getAgentAlpha() throws Exception {
        //test if I return a location -> that is not null
        ReadConfig conf = new ReadConfig();
        try {
            conf.getAgentAlpha();
        } catch (Exception e) {
            assertTrue(e.getMessage().equals("Try to access config file before reading it.") );
        }
        try {
            conf.readFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(conf.getAgentAlpha());
    }

    @Test
    public void getClassifierOffspringSize() throws Exception {
        //test if I return a location -> that is not null
        ReadConfig conf = new ReadConfig();
        try {
            conf.getClassifierOffspringSize();
        } catch (Exception e) {
            assertTrue(e.getMessage().equals("Try to access config file before reading it.") );
        }
        try {
            conf.readFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(conf.getClassifierOffspringSize());
    }

    @Test
    public void getClassifierPopulationSize() throws Exception {
        //test if I return a location -> that is not null
        ReadConfig conf = new ReadConfig();
        try {
            conf.getClassifierPopulationSize();
        } catch (Exception e) {
            assertTrue(e.getMessage().equals("Try to access config file before reading it.") );
        }
        try {
            conf.readFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(conf.getClassifierPopulationSize());
    }

    @Test
    public void getClassifierAlpha() throws Exception {
        //test if I return a location -> that is not null
        ReadConfig conf = new ReadConfig();
        try {
            conf.getClassifierAlpha();
        } catch (Exception e) {
            assertTrue(e.getMessage().equals("Try to access config file before reading it.") );
        }
        try {
            conf.readFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(conf.getClassifierAlpha());
    }

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


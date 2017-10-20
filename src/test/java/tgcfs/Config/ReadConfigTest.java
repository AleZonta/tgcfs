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
    public void getStepSizeAgents() throws Exception {
        //test if I return a location -> that is not null
        ReadConfig conf = new ReadConfig();
        try {
            conf.getStepSizeAgents();
        } catch (Exception e) {
            assertTrue(e.getMessage().equals("Try to access config file before reading it.") );
        }
        try {
            conf.readFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertTrue(conf.getStepSizeAgents() >= 0);

        new ReadConfig.Configurations();
        assertEquals(conf.getStepSizeAgents(), ReadConfig.Configurations.getStepSizeAgents());
    }

    @Test
    public void getStepSizeClassifiers() throws Exception {
        //test if I return a location -> that is not null
        ReadConfig conf = new ReadConfig();
        try {
            conf.getStepSizeClassifiers();
        } catch (Exception e) {
            assertTrue(e.getMessage().equals("Try to access config file before reading it.") );
        }
        try {
            conf.readFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertTrue(conf.getStepSizeClassifiers() >= 0);

        new ReadConfig.Configurations();
        assertEquals(conf.getStepSizeClassifiers(), ReadConfig.Configurations.getStepSizeClassifiers());
    }

    @Test
    public void getTournamentSize() throws Exception {
        //test if I return a location -> that is not null
        ReadConfig conf = new ReadConfig();
        try {
            conf.getTournamentSize();
        } catch (Exception e) {
            assertTrue(e.getMessage().equals("Try to access config file before reading it.") );
        }
        try {
            conf.readFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertTrue(conf.getTournamentSize() >= 0);

        new ReadConfig.Configurations();
        assertEquals(conf.getTournamentSize(), ReadConfig.Configurations.getTournamentSize());
    }

    @Test
    public void getNumberOfTimestepConsidered() throws Exception {
        //test if I return a location -> that is not null
        ReadConfig conf = new ReadConfig();
        try {
            conf.getNumberOfTimestepConsidered();
        } catch (Exception e) {
            assertTrue(e.getMessage().equals("Try to access config file before reading it.") );
        }
        try {
            conf.readFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertTrue(conf.getNumberOfTimestepConsidered() >= 0);

        new ReadConfig.Configurations();
        assertEquals(conf.getNumberOfTimestepConsidered(), ReadConfig.Configurations.getNumberOfTimestepConsidered());
    }

    @Test
    public void getAutomaticCalibration() throws Exception {
        //test if I return a location -> that is not null
        ReadConfig conf = new ReadConfig();
        try {
            conf.getAutomaticCalibration();
        } catch (Exception e) {
            assertTrue(e.getMessage().equals("Try to access config file before reading it.") );
        }
        try {
            conf.readFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertTrue(conf.getAutomaticCalibration() || !conf.getAutomaticCalibration());

        new ReadConfig.Configurations();
        assertEquals(conf.getAutomaticCalibration(), ReadConfig.Configurations.getAutomaticCalibration());
    }

    @Test
    public void getDumpTrajectoryPointAndMeaning() throws Exception {
        //test if I return a location -> that is not null
        ReadConfig conf = new ReadConfig();
        try {
            conf.getDumpTrajectoryPointAndMeaning();
        } catch (Exception e) {
            assertTrue(e.getMessage().equals("Try to access config file before reading it.") );
        }
        try {
            conf.readFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(conf.getDumpTrajectoryPointAndMeaning());

        new ReadConfig.Configurations();
        assertEquals(conf.getDumpTrajectoryPointAndMeaning(), ReadConfig.Configurations.getDumpTrajectoryPointAndMeaning());
    }

    @Test
    public void getCheckAlsoPast() throws Exception {
        //test if I return a location -> that is not null
        ReadConfig conf = new ReadConfig();
        try {
            conf.getCheckAlsoPast();
        } catch (Exception e) {
            assertTrue(e.getMessage().equals("Try to access config file before reading it.") );
        }
        try {
            conf.readFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(conf.getCheckAlsoPast());

        new ReadConfig.Configurations();
        assertEquals(conf.getCheckAlsoPast(), ReadConfig.Configurations.getCheckAlsoPast());
    }

    @Test
    public void getPictureSize() throws Exception {
        //test if I return a location -> that is not null
        ReadConfig conf = new ReadConfig();
        try {
            conf.getPictureSize();
        } catch (Exception e) {
            assertTrue(e.getMessage().equals("Try to access config file before reading it.") );
        }
        try {
            conf.readFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(conf.getPictureSize());

        new ReadConfig.Configurations();
        assertEquals(conf.getPictureSize(), ReadConfig.Configurations.getPictureSize());
    }

    @Test
    public void getValueModel() throws Exception {
        //test if I return a location -> that is not null
        ReadConfig conf = new ReadConfig();
        try {
            conf.getValueModel();
        } catch (Exception e) {
            assertTrue(e.getMessage().equals("Try to access config file before reading it.") );
        }
        try {
            conf.readFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(conf.getValueModel());

        new ReadConfig.Configurations();
        assertEquals(conf.getValueModel(), ReadConfig.Configurations.getValueModel());
    }

    @Test
    public void getLSTM() throws Exception {
        //test if I return a location -> that is not null
        ReadConfig conf = new ReadConfig();
        try {
            conf.getLSTM();
        } catch (Exception e) {
            assertTrue(e.getMessage().equals("Try to access config file before reading it.") );
        }
        try {
            conf.readFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(conf.getLSTM());

        new ReadConfig.Configurations();
        assertEquals(conf.getLSTM(), ReadConfig.Configurations.getLSTM());
    }

    @Test
    public void getConvolution() throws Exception {
        //test if I return a location -> that is not null
        ReadConfig conf = new ReadConfig();
        try {
            conf.getConvolution();
        } catch (Exception e) {
            assertTrue(e.getMessage().equals("Try to access config file before reading it.") );
        }
        try {
            conf.readFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(conf.getConvolution());

        new ReadConfig.Configurations();
        assertEquals(conf.getConvolution(), ReadConfig.Configurations.getConvolution());
    }

    @Test
    public void getClax() throws Exception {
        //test if I return a location -> that is not null
        ReadConfig conf = new ReadConfig();
        try {
            conf.getClax();
        } catch (Exception e) {
            assertTrue(e.getMessage().equals("Try to access config file before reading it.") );
        }
        try {
            conf.readFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(conf.getClax());

        new ReadConfig.Configurations();
        assertEquals(conf.getClax(), ReadConfig.Configurations.getClax());
    }

    @Test
    public void getTrain() throws Exception {
        //test if I return a location -> that is not null
        ReadConfig conf = new ReadConfig();
        try {
            conf.getTrain();
        } catch (Exception e) {
            assertTrue(e.getMessage().equals("Try to access config file before reading it.") );
        }
        try {
            conf.readFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(conf.getTrain());

        new ReadConfig.Configurations();
        assertEquals(conf.getTrain(), ReadConfig.Configurations.getTrain());
    }

    @Test
    public void getTimestepEvolveAgentOverClassifier() throws Exception {
        //test if I return a location -> that is not null
        ReadConfig conf = new ReadConfig();
        try {
            conf.getTimestepEvolveAgentOverClassifier();
        } catch (Exception e) {
            assertTrue(e.getMessage().equals("Try to access config file before reading it.") );
        }
        try {
            conf.readFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(conf.getTimestepEvolveAgentOverClassifier());

        new ReadConfig.Configurations();
        assertEquals(conf.getTimestepEvolveAgentOverClassifier(), ReadConfig.Configurations.getTimestepEvolveAgentOverClassifier());
    }

    @Test
    public void isRecombination() throws Exception {
        //test if I return a location -> that is not null
        ReadConfig conf = new ReadConfig();
        try {
            conf.isRecombination();
        } catch (Exception e) {
            assertTrue(e.getMessage().equals("Try to access config file before reading it.") );
        }
        try {
            conf.readFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(conf.isRecombination());

        new ReadConfig.Configurations();
        assertEquals(conf.isRecombination(), ReadConfig.Configurations.isRecombination());
    }

    @Test
    public void getTrajectoriesTrained() throws Exception {
        //test if I return a location -> that is not null
        ReadConfig conf = new ReadConfig();
        try {
            conf.getTrajectoriesTrained();
        } catch (Exception e) {
            assertTrue(e.getMessage().equals("Try to access config file before reading it.") );
        }
        try {
            conf.readFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(conf.getTrajectoriesTrained());

        new ReadConfig.Configurations();
        assertEquals(conf.getTrajectoriesTrained(), ReadConfig.Configurations.getTrajectoriesTrained());
    }

    @Test
    public void getMutation() throws Exception {
        //test if I return a location -> that is not null
        ReadConfig conf = new ReadConfig();
        try {
            conf.getMutation();
        } catch (Exception e) {
            assertTrue(e.getMessage().equals("Try to access config file before reading it.") );
        }
        try {
            conf.readFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(conf.getMutation());

        new ReadConfig.Configurations();
        assertEquals(conf.getMutation(), ReadConfig.Configurations.getMutation());
    }

    @Test
    public void getSeed() throws Exception {
        //test if I return a location -> that is not null
        ReadConfig conf = new ReadConfig();
        try {
            conf.getSeed();
        } catch (Exception e) {
            assertTrue(e.getMessage().equals("Try to access config file before reading it.") );
        }
        try {
            conf.readFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(conf.getSeed());

        new ReadConfig.Configurations();
        assertEquals(conf.getSeed(), ReadConfig.Configurations.getSeed());
    }


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

        new ReadConfig.Configurations();
        assertEquals(conf.getDumpPop(), ReadConfig.Configurations.getDumpPop());
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

        new ReadConfig.Configurations();
        assertEquals(conf.getLoadDumpPop(), ReadConfig.Configurations.getLoadDumpPop());
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

        new ReadConfig.Configurations();
        assertEquals(conf.getName(), ReadConfig.Configurations.getName());
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

        new ReadConfig.Configurations();
        assertEquals(conf.getExperiment(), ReadConfig.Configurations.getExperiment());
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

        new ReadConfig.Configurations();
        assertEquals(conf.getPath(), ReadConfig.Configurations.getPath());
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

        new ReadConfig.Configurations();
        assertEquals(conf.getMaxGenerations(), ReadConfig.Configurations.getMaxGenerations());
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
        try {
            conf.getHowManyTrajectories();
        } catch (Exception e) {
            assertTrue(e.getMessage().equals("HowManySplitting must be even!") );
        }

        assertNotNull(conf.getHowManyTrajectories());



        new ReadConfig.Configurations();
        assertEquals(conf.getHowManyTrajectories(), ReadConfig.Configurations.getHowManyTrajectories());
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

        new ReadConfig.Configurations();
        assertEquals(conf.getAgentTimeSteps(), ReadConfig.Configurations.getAgentTimeSteps());
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

        new ReadConfig.Configurations();
        assertEquals(conf.getClassifierTimeSteps(), ReadConfig.Configurations.getClassifierTimeSteps());
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

        new ReadConfig.Configurations();
        assertEquals(conf.getHiddenLayersAgent(), ReadConfig.Configurations.getHiddenLayersAgent());
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

        new ReadConfig.Configurations();
        assertEquals(conf.getHiddenNeuronsAgent(), ReadConfig.Configurations.getHiddenNeuronsAgent());
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

        new ReadConfig.Configurations();
        assertEquals(conf.getHiddenNeuronsClassifier(), ReadConfig.Configurations.getHiddenNeuronsClassifier());
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

        new ReadConfig.Configurations();
        assertEquals(conf.getAgentOffspringSize(), ReadConfig.Configurations.getAgentOffspringSize());
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

        new ReadConfig.Configurations();
        assertEquals(conf.getAgentPopulationSize(), ReadConfig.Configurations.getAgentPopulationSize());
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

        new ReadConfig.Configurations();
        assertEquals(conf.getAgentAlpha(), ReadConfig.Configurations.getAgentAlpha());
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

        new ReadConfig.Configurations();
        assertEquals(conf.getClassifierOffspringSize(), ReadConfig.Configurations.getClassifierOffspringSize());
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

        new ReadConfig.Configurations();
        assertEquals(conf.getClassifierPopulationSize(), ReadConfig.Configurations.getClassifierPopulationSize());
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

        new ReadConfig.Configurations();
        assertEquals(conf.getClassifierAlpha(), ReadConfig.Configurations.getClassifierAlpha());
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

        new ReadConfig.Configurations();
        assertEquals(conf.getHowManySplitting(), ReadConfig.Configurations.getHowManySplitting());
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

        new ReadConfig.Configurations();
        assertEquals(conf.getTrajectoriesType(), ReadConfig.Configurations.getTrajectoriesType());
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


package tgcfs.EA.Mutation;

import org.junit.Test;
import tgcfs.Config.ReadConfig;

import static junit.framework.TestCase.assertTrue;

/**
 * Created by Alessandro Zonta on 31/10/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 */
public class StepSizeTest {
    @Test
    public void getStepSizeAgents() throws Exception {
        new ReadConfig.Configurations();
        StepSize stepSize = new StepSize();
        assertTrue(StepSize.getStepSizeAgents() >= 0);

    }

    @Test
    public void setStepSizeAgents() throws Exception {
        new ReadConfig.Configurations();
        StepSize stepSize = new StepSize();
        StepSize.setStepSizeAgents(5000d);
        assertTrue(StepSize.getStepSizeAgents() == 5000);
    }

    @Test
    public void getStepSizeClassifiers() throws Exception {
        new ReadConfig.Configurations();
        StepSize stepSize = new StepSize();
        assertTrue(StepSize.getStepSizeClassifiers() >= 0);
    }

    @Test
    public void setStepSizeClassifiers() throws Exception {
        new ReadConfig.Configurations();
        StepSize stepSize = new StepSize();
        StepSize.setStepSizeClassifiers(5000d);
        assertTrue(StepSize.getStepSizeClassifiers() == 5000);
    }

}
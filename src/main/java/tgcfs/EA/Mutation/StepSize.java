package tgcfs.EA.Mutation;

import tgcfs.Config.ReadConfig;

/**
 * Created by Alessandro Zonta on 31/10/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 *
 *
 * class that reads the step size of the mutation
 */
public class StepSize {
    private static double stepSizeAgents;
    private static double stepSizeClassifiers;


    /**
     * Read the step size form file
     * @throws Exception if there are problem in reading the file
     */
    public StepSize() throws Exception {
        stepSizeAgents = ReadConfig.Configurations.getStepSizeAgents();
        stepSizeClassifiers = ReadConfig.Configurations.getStepSizeClassifiers();
    }

    /**
     * Getter for the step size of the agent
     * @return double value
     */
    public static double getStepSizeAgents() {
        return stepSizeAgents;
    }

    /**
     * Setter for the step size of the agent
     * @param stepSizeAgents double new step size
     */
    public static void setStepSizeAgents(double stepSizeAgents) {
        StepSize.stepSizeAgents = stepSizeAgents;
    }

    /**
     * Getter for the step size of the classifier
     * @return double value
     */
    public static double getStepSizeClassifiers() {
        return stepSizeClassifiers;
    }

    /**
     * Setter for the step size of the classifier
     * @param stepSizeClassifiers double new step size
     */
    public static void setStepSizeClassifiers(double stepSizeClassifiers) {
        StepSize.stepSizeClassifiers = stepSizeClassifiers;
    }
}

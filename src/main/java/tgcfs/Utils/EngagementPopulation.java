package tgcfs.Utils;

import tgcfs.Config.ReadConfig;
import tgcfs.EA.Individual;

import java.util.List;

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
 * This class is an helper class with all the system for combat the disengagement problem
 *
 */
public class EngagementPopulation {
    private double virulenceAgents;
    private double virulenceClassifiers;
    private boolean usingVirulenceAgents;
    private boolean usingVirulenceClassifiers;
    private AutomaticSystems method;
    private MeasureUseAgainstDisengagement measure;
    private PopulationEngaged pop;


    /**
     * Constructor class. It loads all the parameter used and necessary to combat che disengagement
     * @throws Exception if there are problems in reading the files
     */
    public EngagementPopulation() throws Exception {
        this.virulenceAgents = ReadConfig.Configurations.getVirulenceAgents();
        this.virulenceClassifiers = ReadConfig.Configurations.getVirulenceClassifiers();
        this.usingVirulenceAgents = ReadConfig.Configurations.getUsingReducedVirulenceMethodOnAgents();
        this.usingVirulenceClassifiers = ReadConfig.Configurations.getUsingReducedVirulenceMethodOnClassifiers();
        switch (ReadConfig.Configurations.getAutomaticEvolutionDisengagementSystem()){
            case 0:
                this.method = AutomaticSystems.NONE;
                break;
            case 1:
                this.method = AutomaticSystems.STEP_SIZE;
                break;
            case 2:
                this.method = AutomaticSystems.VIRULENCE;
                break;
            case 3:
                this.method = AutomaticSystems.BOTH;
                break;
            default:
                throw new Exception("Error in value selected for the automatic disengagement system");
        }
        switch (ReadConfig.Configurations.getMeasureUsedForAutomaticDisengagement()) {
            case 0:
                this.measure = MeasureUseAgainstDisengagement.ENGAGEMENT;
                break;
            case 1:
                this.measure = MeasureUseAgainstDisengagement.STD;
                break;
            default:
                throw new Exception("Error in value selected for the measure used for the automatic disengagement system");
        }
        switch (ReadConfig.Configurations.getPopulationWillUseTheAutomaticDisengagementSystem()) {
            case 0:
                this.pop = PopulationEngaged.BOTH;
                break;
            case 1:
                this.pop = PopulationEngaged.AGENTS;
                break;
            case 2:
                this.pop = PopulationEngaged.CLASSIFIERS;
                break;
            default:
                throw new Exception("Error in value selected for the population chosen for the automatic disengagement system");

        }
    }

    /**
     * Getter for the value of the virulence for the agent
     * @return double value
     */
    public double getVirulenceAgents() {
        return this.virulenceAgents;
    }

    /**
     * Getter for the value of the virulence for the classifier
     * @return double value
     */
    public double getVirulenceClassifiers() {
        return this.virulenceClassifiers;
    }

    /**
     * Getter if the virulence system is used for the agent
     * @return boolean value
     */
    public boolean isUsingVirulenceAgents() {
        return this.usingVirulenceAgents;
    }

    /**
     * Getter if the virulence system is used for the classifier
     * @return boolean value
     */
    public boolean isUsingVirulenceClassifiers() {
        return this.usingVirulenceClassifiers;
    }

    /**
     * Getter for the method used to cure the disengagement
     * @return {@link AutomaticSystems} enum
     */
    public AutomaticSystems getMethod() {
        return this.method;
    }

    /**
     * Getter for the measure used to cure the disengagement
     * @return {@link MeasureUseAgainstDisengagement} enum
     */
    public MeasureUseAgainstDisengagement getMeasure() {
        return this.measure;
    }

    /**
     * Getter for the population chosen to cure
     * @return {@link PopulationEngaged} enum
     */
    public PopulationEngaged getPop() {
        return this.pop;
    }


    /**
     * This method execute all the countermeasures selected to combat the disengagement
     *
     * This method will be called two times, one per population.
     * First think to do, am I doing something against the disengagement?
     * - check the variable of automatic disengagement.
     * -- none
     *      nothing is going automatic, Am I still using virulence system?
     *      check variable with virulence in the population I am checking now
     *      --- yes
     *          use the static virulence variable read from the file
     *      --- no
     *          do nothing
     * -- automatic step-size
     *      check witch measure I am using to automatic evolve the stepsize
     *      --- STD
     *      --- Engagement
     *      compute the measure and update the step size following the number
     *      TODO define relationship between step size and measure of engagement
     * -- automatic virulence
     *      check witch measure I am using to automatic evolve the virulence
     *      --- STD
     *      --- Engagement
     *      compute the measure and update the virulence following the number
     *      TODO define relationship between step size and measure of engagement
     * -- both
     *      check witch measure I am using to automatic evolve the virulence
     *      --- STD
     *      --- Engagement
     *      compute the measure and update the virulence and the step size following the number
     *      TODO define relationship between step size / virulence and measure of engagement
     *
     *
     * @param population population analysed
     * @param className name of the class analysed (Agent, Classifier)
     */
    public void executeCountermeasuresAgainsDisengagement(List<Individual> population, String className){

    }
}

package tgcfs.EA.Helpers;

import tgcfs.Config.ReadConfig;
import tgcfs.EA.Individual;
import tgcfs.EA.Mutation.StepSize;
import tgcfs.Framework.TuringLearning;
import tgcfs.InputOutput.Normalisation;
import tgcfs.Utils.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private final double virulenceAgents;
    private final double virulenceClassifiers;
    private final boolean usingVirulenceAgents;
    private final boolean usingVirulenceClassifiers;
    private final AutomaticSystems method;
    private final MeasureUseAgainstDisengagement measure;
    private final PopulationEngaged pop;
    private Measures measures;
    private int maxFitnessAgent;
    private int maxFitnessClassifier;
    private static Logger logger;
    private final boolean autocalibration;
    private boolean evolveAgent;
    private boolean evolveClassifier;

    /**
     * Constructor class. It loads all the parameter used and necessary to combat che disengagement
     * @throws Exception if there are problems in reading the files
     * @param log log
     */
    public EngagementPopulation(Logger log) throws Exception {
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
        this.measures = null;
        this.maxFitnessClassifier = 0;
        this.maxFitnessAgent = 0;
        logger = log;
        this.autocalibration = ReadConfig.Configurations.getAutomaticCalibration();
        this.evolveAgent = Boolean.TRUE;
        this.evolveClassifier = Boolean.TRUE;
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
     * Set max fintess
     * @param maxFitnessAgent int max fitness achievable for the agent
     * @param maxFitnessClassifier  int max fitness achievable for the classifier
     */
    public void setMaxFitness(int maxFitnessAgent, int maxFitnessClassifier) {
        this.maxFitnessAgent = maxFitnessAgent;
        this.maxFitnessClassifier = maxFitnessClassifier;
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
     *      for now linear relationship
     * -- automatic virulence
     *      check witch measure I am using to automatic evolve the virulence
     *      --- STD
     *      --- Engagement
     *      compute the measure and update the virulence following the number
     *      for now linear relationship
     * -- both
     *      check witch measure I am using to automatic evolve the virulence
     *      --- STD
     *      --- Engagement
     *      compute the measure and update the virulence and the step size following the number
     *     for now linear relationship
     *
     * @param population population analysed
     * @param status of the population analysed (Agent, Classifier)
     */
    public void executeCountermeasuresAgainstDisengagement(List<Individual> population, IndividualStatus status) throws Exception {
        //check the variable of automatic disengagement
        double meas;
        double virulence;
        double stepsize;
        switch(this.method){
            case NONE:
                //nothing is going automatic, Am I still using virulence system?
                //check variable with virulence in the population I am checking now
                if(status == IndividualStatus.AGENT){
                    if(this.usingVirulenceAgents){
                        //reduce virulence for the agent
                        this.reduceVirulence(this.virulenceAgents, population);
                    }else {
                        logger.log(Level.FINE, "Disengagement countermeasures not enabled");
                    }
                }else {
                    if(this.usingVirulenceClassifiers){
                        //reduce virulence for the classifier
                        this.reduceVirulence(this.virulenceClassifiers, population);
                    }else {
                        logger.log(Level.FINE, "Disengagement countermeasures not enabled");
                    }
                }
                break;
            case STEP_SIZE:
                meas = this.computeMeasure(population, status);
                stepsize = this.dynamicStepSizeOne(meas);
                logger.log(Level.FINE, status.toString() + " new step size -> " + stepsize);
                //now I have val, need to update the step size following some direction.
                switch (this.pop){
                    case AGENTS:
                        if(status == IndividualStatus.AGENT) StepSize.setStepSizeAgents(stepsize);
                        break;
                    case CLASSIFIERS:
                        if(status == IndividualStatus.CLASSIFIER) StepSize.setStepSizeClassifiers(stepsize);
                        break;
                    case BOTH:
                        if(status == IndividualStatus.AGENT) StepSize.setStepSizeAgents(stepsize);
                        if(status == IndividualStatus.CLASSIFIER) StepSize.setStepSizeClassifiers(stepsize);
                        break;
                    default:
                        throw new Exception("Something is wrong with the population selected");
                }
                break;
            case VIRULENCE:
                meas = this.computeMeasure(population, status);
                virulence = this.dynamicVirulenceOne(meas);
                logger.log(Level.FINE, status.toString() + " new virulence -> " + virulence);
                switch (this.pop){
                    case AGENTS:
                        if(status == IndividualStatus.AGENT) this.reduceVirulence(virulence, population);
                        break;
                    case CLASSIFIERS:
                        if(status == IndividualStatus.CLASSIFIER) this.reduceVirulence(virulence, population);
                        break;
                    case BOTH:
                        this.reduceVirulence(virulence, population);
                        break;
                    default:
                        throw new Exception("Something is wrong with the population selected");
                }
                break;
            case BOTH:
                meas = this.computeMeasure(population, status);
                virulence = this.dynamicVirulenceOne(meas);
                stepsize = this.dynamicStepSizeOne(meas);
                logger.log(Level.FINE, status.toString() + " new step size -> " + stepsize);
                logger.log(Level.FINE, status.toString() + " new virulence -> " + virulence);
                switch (this.pop){
                    case AGENTS:
                        if(status == IndividualStatus.AGENT){
                            this.reduceVirulence(virulence, population);
                            StepSize.setStepSizeAgents(stepsize);
                        }
                        break;
                    case CLASSIFIERS:
                        if(status == IndividualStatus.CLASSIFIER){
                            this.reduceVirulence(virulence, population);
                            StepSize.setStepSizeClassifiers(stepsize);
                        }
                        break;
                    case BOTH:
                        this.reduceVirulence(virulence, population);
                        if(status == IndividualStatus.AGENT) StepSize.setStepSizeAgents(stepsize);
                        if(status == IndividualStatus.CLASSIFIER) StepSize.setStepSizeClassifiers(stepsize);
                        break;
                    default:
                        throw new Exception("Something is wrong with the population selected");
                }
                break;
            default:
                throw new Exception("Something is wrong with the method of automatic evolution of disengagement countermeasure");
        }

    }


    /**
     * Implementation of the method explained in:
     * Cartlidge, J., & Bullock, S. (2004). Combating coevolutionary disengagement by reducing parasite virulence.
     * Evolutionary Computation, 12(2), 193–222. http://doi.org/10.1162/106365604773955148
     *
     *
     * The scores are normalised with respect to the maximum score achieved that generation such that the best current parasite always achieves a score of 1
     *
     * It needs a parameter (virulence) from outside
     * Maximum virulence (1.0) normal situation
     * Moderate virulence (0.75) win rate three quarters that of the highest scoring current parasite
     * Null virulence (0.5) half the win rate
     * < 0.5 encourage cooperation between populations
     *
     * reducing virulence can be thought of as maintaining a gradient for selection, forcing paeasites to evolve in difficulty at a similar speed to hosts
     *
     * @param virulence virulence
     * @param population population I am acting on
     *
     */
    private void reduceVirulence(double virulence, List<Individual> population){
        double maxFitness = Collections.max(population, Comparator.comparing(Individual::getFitness)).getFitness();

        population.forEach(individual -> {
            double normalisedFitness = Normalisation.convertToSomething(maxFitness, 0.0, 1.0,0.0, individual.getFitness());
            double virulencedFitness = this.functionFitness(normalisedFitness, virulence);
            individual.setFitness((int) Normalisation.convertToSomething(1.0, 0.0, maxFitness,0.0, virulencedFitness));
        });

    }

    /**
     * Compute the new fitness following the reducing virulence method
     * @param fitness old fitness
     * @param virulence virulence parameter
     * @return new fitness
     */
    private double functionFitness(double fitness, double virulence){
        return (((2 * fitness) / virulence) - (Math.pow(fitness, 2) / Math.pow(virulence, 2)));
    }


    /**
     * Compute the measure for the current population
     * @param population population in analysis
     * @param status {@link IndividualStatus} type of population under analysis.
     * @return double val
     */
    private double computeMeasure(List<Individual> population, IndividualStatus status){
        double maxValue;
        if(status == IndividualStatus.AGENT){
            maxValue = this.maxFitnessAgent;
        }else{
            maxValue = this.maxFitnessClassifier;
        }
        //check witch measure I am using to automatic evolve the step size
        //default is std
        this.measures = new Measures(population);
        double val = 0d;
        switch (this.measure){
            case STD:
                val = this.measures.getStd();
                val = Normalisation.convertToSomething(maxValue / 2, 0d, 1d,0d, val);
                break;
            case ENGAGEMENT:
                val = this.measures.getEngadgement();
                break;
            default:
                val = this.measures.getStd();
                val = Normalisation.convertToSomething(maxValue / 2, 0d, 1d,0d, val);
                break;
        }
        return val;
    }


    /**
     * Compute dynamic virulence 1 found in
     * Cartlidge, J. P. (2004). Rules of engagement: competitive coevolutionary dynamics in computational systems,
     * (June), 198. Retrieved from http://etheses.whiterose.ac.uk/1315/1/cartlidge.pdf
     * @param epsilon the measure of engagement of the population
     * @return virulence value
     */
    private double dynamicVirulenceOne(double epsilon){
        if(epsilon <= 0.5){
            return 0.5;
        }else{
            return epsilon;
        }
    }


    /**
     * Compute the new step size following the measurement used
     *
     * it transform the epsilon into a step size
     * minimum of step size is 0.001
     * maximum is 0.5
     *
     * if measure of engagement is low, step size is gonna be very high
     * otherwise very low
     *
     * @param epsilon the measure of engagement of the population
     * @return step size value
     */
    private double dynamicStepSizeOne(double epsilon){
        return Normalisation.convertToSomething(1d, 0d, 0.001d,0.3d, epsilon);
    }


    /**
     * Check if I need to evolve one population more than another one
     *
     * if AutomaticCalibration is TRUE:
     * - getTimestepEvolveAgentOverClassifier is not considered
     * - it checks the fitness of the fittest member of the populations
     *   if it is under a certain threshold and the second population is over, the first population need an evolution more
     *   than the second one
     *
     * if AutomaticCalibration is False:
     * - getTimestepEvolveAgentOverClassifier is considered
     *
     *  int value as a result, they are then transformed into two boolean variables
     * -> 0 if the calibration is not needed
     * -> 1 if more evolution of agents are needed
     * -> 2 if more evolution of classifier are needed
     *
     * @param caller reference caller of the method
     * @param fittestAgent value of the fittest agent
     * @param fittestClassifier value of the fittest classifier
     * @param maxFitnessPossibleAgent max possible fitness for the agent
     * @param maxFitnessPossibleClassifier max possible fitness for the classifier
     */
    public void checkEvolutionOnlyOnePopulation(double fittestAgent, double fittestClassifier, double maxFitnessPossibleAgent, double maxFitnessPossibleClassifier, TuringLearning caller) throws Exception {
        if(this.autocalibration){
            int output = 0;
            this.evolveAgent = Boolean.TRUE;
            this.evolveClassifier = Boolean.TRUE;

            if(fittestAgent <= (maxFitnessPossibleAgent * 1 / 2)){
                //fitness agent needs more evolution
                //only if classifier is over the threshold, otherwise not
                if(fittestClassifier >= (maxFitnessPossibleClassifier * 1 / 2)){
                    this.evolveAgent = Boolean.TRUE;
                    this.evolveClassifier = Boolean.FALSE;
                    output = 1;
                }
                //both are under threshold, no calibration needed -> no 'else' needed since 0 is the default value
            }else {
                //fitness agent are okay, but what about the classifier?
                //if the fitness is below the threshold I need to evaluate more the classifier
                if (fittestClassifier <= (maxFitnessPossibleClassifier * 1 / 2)) {
                    this.evolveAgent = Boolean.FALSE;
                    this.evolveClassifier = Boolean.TRUE;
                    output = 2;
                }
                //both are over threshold, no calibration needed -> no 'else' needed since 0 is the default value
            }
            StringBuilder sb = new StringBuilder();
            sb.append("Fittest agent: ");
            sb.append(fittestAgent);
            sb.append(" over ");
            sb.append(maxFitnessPossibleAgent);
            sb.append("; fittest classifier:  ");
            sb.append(fittestClassifier);
            sb.append(" over ");
            sb.append(maxFitnessPossibleClassifier);
            switch (output){
                case 0:
                    sb.append("; No Calibration Needed");
                    break;
                case 1:
                    sb.append("; Agents need more evolutions");
                    break;
                case 2:
                    sb.append("; Classifiers need more evolutions");
                    break;
                default:
                    sb.append("; Something really wrong happened");
                    break;
            }
            logger.log(Level.FINE, sb.toString());
            //reset counting time
            caller.setCountingTime(0);
        }else{
            int number = ReadConfig.Configurations.getTimestepEvolveAgentOverClassifier();
            if(number == 0){
                //no calibration needed
                logger.log(Level.FINE, "No Calibration Needed");
                this.evolveAgent = Boolean.TRUE;
                this.evolveClassifier = Boolean.TRUE;
            }else{
                caller.setCountingTime(caller.getCountingTime() + 1);
                if(caller.getCountingTime() == number){
                    caller.setCountingTime(0);
                    //if I evolved the agent the times required return okay to evolve both
                    logger.log(Level.FINE, "Finished Fixed Calibration");
                    this.evolveAgent = Boolean.TRUE;
                    this.evolveClassifier = Boolean.TRUE;
                }else {
                    //otherwise return evolve only Agents
                    logger.log(Level.FINE, "Agents need more evolutions");
                    this.evolveAgent = Boolean.TRUE;
                    this.evolveClassifier = Boolean.FALSE;
                }
            }
        }
    }

    /**
     * Getter for boolean value of evolving agent more than classifier
     * @return boolean value
     */
    public boolean isEvolveAgent() {
        return this.evolveAgent;
    }

    /**
     * Getter for boolean value of evolving classifier more than agent
     * @return boolean value
     */
    public boolean isEvolveClassifier() {
        return this.evolveClassifier;
    }
}



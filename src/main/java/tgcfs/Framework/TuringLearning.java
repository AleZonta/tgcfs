package tgcfs.Framework;

import lgds.trajectories.Point;
import org.nd4j.linalg.factory.Nd4j;
import tgcfs.Agents.InputNetwork;
import tgcfs.Agents.Models.Clax;
import tgcfs.Agents.Models.ConvAgent;
import tgcfs.Agents.Models.LSTMAgent;
import tgcfs.Agents.OutputNetwork;
import tgcfs.Classifiers.Classifier;
import tgcfs.Config.ReadConfig;
import tgcfs.EA.Agents;
import tgcfs.EA.Classifiers;
import tgcfs.Idsa.IdsaLoader;
import tgcfs.InputOutput.FollowingTheGraph;
import tgcfs.Loader.Feeder;
import tgcfs.Loader.ReachedMaximumNumberException;
import tgcfs.Loader.TrainReal;
import tgcfs.NN.EvolvableModel;
import tgcfs.Performances.SaveToFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Alessandro Zonta on 14/09/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 *
 * Implements The Turing learning framework
 *
 * Li, W., Gauci, M., & Groß, R. (2016). Turing learning: a metric-free approach to inferring behavior and its
 * application to swarms. Swarm Intelligence, 10(3), 211–243. http://doi.org/10.1007/s11721-016-0126-1
 *
 */
public class TuringLearning implements Framework{
    private Agents agents;
    private Classifiers classifiers;
    private Feeder feeder;
    private IdsaLoader idsaLoader;
    private int countingTime;
    private static final Logger logger = Logger.getLogger(TuringLearning.class.getName()); //logger for this class


    /**
     * Constructor zero parameter
     * Loading the config files
     * @throws Exception if there are problems with the reading procedure
     */
    public TuringLearning() throws Exception {
        //initialising the config file class
        new ReadConfig.Configurations();

        //Creating the agents
        this.agents = new Agents();
        this.classifiers = new Classifiers();

        this.feeder = null;
        this.idsaLoader = null;

        //initialise the saving class
        new SaveToFile.Saver(ReadConfig.Configurations.getName(), ReadConfig.Configurations.getExperiment(), ReadConfig.Configurations.getPath());
        SaveToFile.Saver.dumpSetting(ReadConfig.Configurations.getConfig());

        //back up for convolution, in java there are some problems
        if(Objects.equals(ReadConfig.Configurations.getValueModel(), ReadConfig.Configurations.Convolution)) Nd4j.enableFallbackMode(Boolean.TRUE);

        this.countingTime = 0;
    }


    /**
     * Method that loads the neural network and the population of the Turing Learning
     * It loads also the graph, the trajectory system, and the potential field system
     * @throws Exception something is wrong with the loading
     */
    @Override
    public void load() throws Exception {
        logger.log(Level.INFO, "Starting Turing Learning...");
        //loading graph and trajectories
        this.feeder = new Feeder();
        this.feeder.loadSystem();
        //loading potential field
        //idsa loader I can also add the total number of tracks
        //now all the trajectories are loading
        this.idsaLoader = new IdsaLoader();
        this.idsaLoader.InitPotentialField(this.feeder.getTrajectories());
        //loading models
        EvolvableModel agentModel;
        //decide which model to implement here
        switch (ReadConfig.Configurations.getValueModel()){
            case 0:
                agentModel = new LSTMAgent(InputNetwork.inputSize, ReadConfig.Configurations.getHiddenLayersAgent(), ReadConfig.Configurations.getHiddenNeuronsAgent(), OutputNetwork.outputSize);
                break;
            case 1:
                // fixed size for now
                agentModel = new ConvAgent(ReadConfig.Configurations.getPictureSize());
                ((ConvAgent)agentModel).setFeeder(this.feeder);
                break;
            case 2:
                agentModel = new Clax(this.feeder, this.idsaLoader);
                break;
            default:
                throw new NoSuchMethodError("Model not yet implemented");
        }
        EvolvableModel classifierModel = new Classifier(tgcfs.Classifiers.InputNetwork.inputSize, ReadConfig.Configurations.getHiddenNeuronsClassifier(), tgcfs.Classifiers.OutputNetwork.outputSize);
        //generate population
        //INITIALISE population EA with random candidate solution
        this.agents.generatePopulation(agentModel);
        this.classifiers.generatePopulation(classifierModel);
        logger.log(Level.INFO, "Framework online!");
    }

    /**
     * Main loop for the evolutionary algorithm
     * The algorithm is following the scheme:
     * EVALUATE each candidate
     * REPEAT until TERMINAL CONDITION (errors, reach number maximum of generations, finished the trajectories)
     *  SELECT parent
     *  RECOMBINE parents
     *  MUTATE offspring
     *  EVALUATE new candidate
     *  SELECT individuals next generation
     *
     * @throws Exception if some errors occurs
     */
    @Override
    public void run() throws Exception {
        logger.log(Level.INFO, "Starting Evolution...");
        int generationAgent = 0;
        int generationClassifier = 0;
        /* { EVALUATE each candidate } */
        logger.log(Level.INFO, "Evaluation agent generation " + generationAgent + " and classifier generation " + generationClassifier);

        //load several pieces of trajectory
        List<TrainReal> combineInputList = this.feeder.multiFeeder(this.idsaLoader);
        //execution agents
        logger.log(Level.INFO,"Run Agents...");
        //train the agents
        this.agents.trainNetwork(combineInputList);
        //run the agents
        this.agents.runIndividuals(combineInputList);
        //classifier are executed and evaluated during agents evaluations
        logger.log(Level.INFO,"Run Classifiers...");
        this.agents.evaluateIndividuals(this.classifiers, new FollowingTheGraph(this.feeder));
        //save the fitness of all the population and the best genome
        SaveToFile.Saver.saveFitness(this.agents.getClass().getName(),this.agents.retAllFitness());
        SaveToFile.Saver.saveFitness(this.classifiers.getClass().getName(),this.classifiers.retAllFitness());
        SaveToFile.Saver.saveBestGenoma(this.agents.getClass().getName(),this.agents.retBestGenome());
        SaveToFile.Saver.saveBestGenoma(this.classifiers.getClass().getName(),this.classifiers.retBestGenome());


        /* { REPEAT until TERMINAL CONDITION } */
        boolean reachedEndTrajectory = Boolean.FALSE;
        boolean randomError = Boolean.FALSE;
        boolean evolveAgent = Boolean.TRUE;
        boolean evolveClassifier = Boolean.TRUE;
        Integer maxGeneration = ReadConfig.Configurations.getMaxGenerations();
        while(!reachedEndTrajectory && !randomError && (generationAgent <= maxGeneration || generationClassifier <= maxGeneration)) {
            if(evolveAgent) generationAgent++;
            if(evolveClassifier) generationClassifier++;
            /* { SELECT parent }
               { RECOMBINE parents }
               { MUTATE offspring } */
            logger.log(Level.INFO,"Generating Offspring...");

            if(ReadConfig.Configurations.isRecombination()) {
                if(evolveAgent) this.agents.generateOffspring();
                if(evolveClassifier) this.classifiers.generateOffspring();
            }else{
                if(evolveAgent) this.agents.generateOffspringOnlyWithMutation();
                if(evolveClassifier) this.classifiers.generateOffspringOnlyWithMutation();
            }

            logger.log(Level.INFO, "Evaluation agent generation " + generationAgent + " and classifier generation " + generationClassifier);
            /* { EVALUATE new candidate } */
            try {
                logger.log(Level.INFO,"Loading new trajectories...");
                combineInputList = this.feeder.multiFeeder(this.idsaLoader);
                //execution agents
                logger.log(Level.INFO,"Run Agents...");
                this.agents.runIndividuals(combineInputList);
                logger.log(Level.INFO,"Run Classifiers...");
                //classifier are executed and evaluated during agents evaluations
                this.agents.evaluateIndividuals(this.classifiers, new FollowingTheGraph(this.feeder));

                //I need to generate this dataset for testing the classifiers and understand visually what is happening
                //this is happening only in the last generation
                if (ReadConfig.Configurations.getDumpTrajectoryPointAndMeaning() && (generationAgent == maxGeneration - 1 || generationClassifier == maxGeneration - 1)){
                    logger.log(Level.INFO, "Dump agent generation and real");
                    this.saveTrajectoryAndGeneratedPoints(combineInputList, new FollowingTheGraph(this.feeder));
                }

                //check if I have to evolve or not someone next generation
                int caseEvolution = this.checkEvolutionOnlyOnePopulation();
                switch (caseEvolution){
                    case 0:
                        evolveAgent = Boolean.TRUE;
                        evolveClassifier = Boolean.TRUE;
                        break;
                    case 1:
                        evolveAgent = Boolean.TRUE;
                        evolveClassifier = Boolean.FALSE;
                        this.classifiers.resetFitness();
                        break;
                    case 2:
                        evolveAgent = Boolean.FALSE;
                        evolveClassifier = Boolean.TRUE;
                        this.agents.resetFitness();
                        break;
                    default:
                        //should never happen but in any case lets put it here.
                        evolveAgent = Boolean.TRUE;
                        evolveClassifier = Boolean.TRUE;
                        break;
                }

            /* { SELECT individuals next generation } */
                logger.log(Level.INFO,"Parent Selection...");
                if(evolveAgent) this.agents.selectParents();
                if(evolveClassifier) this.classifiers.selectParents();

                //save the fitness of all the population and best genome
                logger.log(Level.INFO,"Saving Statistics...");
                if(evolveAgent) SaveToFile.Saver.saveFitness(this.agents.getClass().getName(), this.agents.retAllFitness());
                if(evolveClassifier) SaveToFile.Saver.saveFitness(this.classifiers.getClass().getName(), this.classifiers.retAllFitness());
                if(evolveAgent) SaveToFile.Saver.saveBestGenoma(this.agents.getClass().getName(),this.agents.retBestGenome());
                if(evolveClassifier) SaveToFile.Saver.saveBestGenoma(this.classifiers.getClass().getName(),this.classifiers.retBestGenome());
                if(ReadConfig.Configurations.getDumpPop()) {
                    logger.log(Level.INFO,"Dump Population...");
                    if(evolveAgent) SaveToFile.Saver.dumpPopulation(this.agents.getClass().getName(), this.agents.getPopulation());
                    if(evolveClassifier) SaveToFile.Saver.dumpPopulation(this.classifiers.getClass().getName(), this.classifiers.getPopulation());
                }



            } catch (ReachedMaximumNumberException e) {
                logger.log(Level.INFO, e.getMessage());
                reachedEndTrajectory = Boolean.TRUE;
            } catch (Exception e){
                logger.log(Level.WARNING, "Error concluded the main loop -> " + e.getMessage());
                e.printStackTrace();
                randomError = Boolean.TRUE;
            }
        }
        logger.log(Level.INFO, "Coevolution ended");
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
     * @return int value
     * -> 0 if the calibration is not needed
     * -> 1 if more evolution of agents are needed
     * -> 2 if more evolution of classifier are needed
     *
     * @exception Exception if there are problems reading the configfile
     */
    private int checkEvolutionOnlyOnePopulation() throws Exception {
        if(ReadConfig.Configurations.getAutomaticCalibration()){
            int output = 0;
            double fittestAgent = this.agents.getFittestIndividual().getFitness();
            double fittestClassifier = this.classifiers.getFittestIndividual().getFitness();

            double maxFitnessPossibleAgent = this.agents.getMaxFitnessAchievable();
            double maxFitnessPossibleClassifier = this.classifiers.getMaxFitnessAchievable();

            if(fittestAgent <= (maxFitnessPossibleAgent * 2 / 3)){
                //fitness agent needs more evolution
                //only if classifier is over the threshold, otherwise not
                if(fittestClassifier >= (maxFitnessPossibleClassifier * 2 / 3)){
                    output = 1;
                }
                //both are under threshold, no calibration needed -> no 'else' needed since 0 is the default value
            }else {
                //fitness agent are okay, but what about the classifier?
                //if the fitness is below the threshold I need to evaluate more the classifier
                if (fittestClassifier <= (maxFitnessPossibleClassifier * 2 / 3)) {
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
            logger.log(Level.INFO, sb.toString());
            //reset counting time
            this.countingTime = 0;
            return output;
        }else{
            int number = ReadConfig.Configurations.getTimestepEvolveAgentOverClassifier();
            if(number == 0){
                //no calibration needed
                logger.log(Level.INFO, "No Calibration Needed");
                return 0;
            }else{
                this.countingTime++;
                if(this.countingTime == number){
                    this.countingTime = 0;
                    //if I evolved the agent the times required return okay to evolve both
                    logger.log(Level.INFO, "Finished Fixed Calibration");
                    return 0;
                }else {
                    //otherwise return evolve only Agents
                    logger.log(Level.INFO, "Agents need more evolutions");
                    return 1;
                }
            }
        }
    }


    /**
     * run the generative part more time than the discriminative part
     * @param number number of time to run
     * @throws Exception something wrong happens
     */
    public void generateMoreThanDiscriminate(Integer number) throws Exception {
        for(int i = 0; i < number - 1; i++) {
            //I have to evolve for this number of time-step -1 only the  agents and not the classifiers
            //The last timestep I am evolving both
            logger.log(Level.INFO, "Evaluation only agents' generation " + i);
                /* { EVALUATE new candidate } */
            List<TrainReal> combineInputList = this.feeder.multiFeeder(this.idsaLoader);
            //train the agents
            this.agents.trainNetwork(combineInputList);
            //execution agents
            logger.log(Level.INFO, "Run Agents...");
            this.agents.runIndividuals(combineInputList);
            logger.log(Level.INFO, "Evaluate Agents...");
            //classifier are executed and evaluated during agents evaluations
            this.agents.evaluateIndividuals(this.classifiers, new FollowingTheGraph(this.feeder));
            //reset classifier, I am not evolving them now
            this.classifiers.resetFitness();
                /* { SELECT individuals next generation } */
            logger.log(Level.INFO, "Parent Selection...");
            this.agents.selectParents();
            //save the fitness of all the population and best genome
            SaveToFile.Saver.saveFitness(this.agents.getClass().getName(), this.agents.retAllFitness());
            SaveToFile.Saver.saveBestGenoma(this.agents.getClass().getName(), this.agents.retBestGenome());
            if (ReadConfig.Configurations.getDumpPop()) {
                logger.log(Level.INFO, "Dump Population...");
                SaveToFile.Saver.dumpPopulation(this.agents.getClass().getName(), this.agents.getPopulation());
            }
            //generate new offspring for new evolution
            if(ReadConfig.Configurations.isRecombination()) {
                this.agents.generateOffspring();
            }else{
                this.agents.generateOffspringOnlyWithMutation();
            }
        }
    }

    /**
     * From the list of {@link TrainReal} element, it computes the real point/points in the map that follow/s the trajectory
     * @param combineInputList all the input used in this session
     * @param transformation {@link FollowingTheGraph} transformation reference to transform the output in real point //TODO generalise this
     * @throws Exception If something goes wrong
     */
    private void saveTrajectoryAndGeneratedPoints(List<TrainReal> combineInputList, FollowingTheGraph transformation) throws Exception {
        //compute the real point.
        combineInputList.forEach(trainReal -> {
            if(trainReal.getRealPointsOutputComputed() == null) {
                List<Point> generatedPoint = new ArrayList<>();
                transformation.setLastPoint(trainReal.getLastPoint());
                trainReal.getOutputComputed().forEach(outputsNetwork -> generatedPoint.add(transformation.singlePointConversion(outputsNetwork)));
                trainReal.setRealPointsOutputComputed(generatedPoint);
            }
        });
        SaveToFile.Saver.dumpTrajectoryAndGeneratedPart(combineInputList);
    }

    /**
     * Getter for the agents
     * @return {@link Agents}
     */
    public Agents getAgents(){
        return this.agents;
    }
}

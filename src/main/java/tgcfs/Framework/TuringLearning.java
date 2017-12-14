package tgcfs.Framework;

import org.nd4j.linalg.factory.Nd4j;
import tgcfs.Agents.InputNetwork;
import tgcfs.Agents.Models.LSTMAgent;
import tgcfs.Agents.OutputNetwork;
import tgcfs.Classifiers.Models.ENNClassifier;
import tgcfs.Config.PropertiesFileReader;
import tgcfs.Config.ReadConfig;
import tgcfs.EA.Agents;
import tgcfs.EA.Classifiers;
import tgcfs.EA.EngagementPopulation;
import tgcfs.EA.Mutation.StepSize;
import tgcfs.Idsa.IdsaLoader;
import tgcfs.InputOutput.FollowingTheGraph;
import tgcfs.InputOutput.LoadExternalPopulation;
import tgcfs.Loader.Feeder;
import tgcfs.Loader.ReachedMaximumNumberException;
import tgcfs.Loader.TrainReal;
import tgcfs.Performances.SaveToFile;
import tgcfs.Utils.IndividualStatus;
import tgcfs.Utils.LogSystem;
import tgcfs.Utils.RandomGenerator;

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
    private static Logger logger; //logger for this class
    private EngagementPopulation countermeasures;


    /**
     * Constructor zero parameter
     * Loading the config files
     * @throws Exception if there are problems with the reading procedure
     */
    public TuringLearning() throws Exception {
        //initialising the config file class
        new ReadConfig.Configurations();

        LogSystem logSystem = new LogSystem(this.getClass(), ReadConfig.Configurations.getName(), ReadConfig.Configurations.getExperiment(), ReadConfig.Configurations.getPath());
        logger = logSystem.getLogger();

        //Creating the agents
        this.agents = new Agents(logger);
        this.classifiers = new Classifiers(logger);

        this.feeder = null;
        this.idsaLoader = null;

        //initialise the saving class
        new SaveToFile.Saver(ReadConfig.Configurations.getName(), ReadConfig.Configurations.getExperiment(), ReadConfig.Configurations.getPath(), logger);
        SaveToFile.Saver.dumpSetting(ReadConfig.Configurations.getConfig());
        logger.log(Level.INFO, PropertiesFileReader.getGitSha1());

        //back up for convolution, in java there are some problems
        if(Objects.equals(ReadConfig.Configurations.getValueModel(), ReadConfig.Configurations.Convolution)) Nd4j.enableFallbackMode(Boolean.TRUE);

        this.countingTime = 0;
        //load countermeasures
        this.countermeasures = new EngagementPopulation(logger);

        //load random number generator
        new RandomGenerator();
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
        this.feeder = new Feeder(logger);
        this.feeder.loadSystem();
        //loading potential field
        //idsa loader I can also add the total number of tracks
        //now all the trajectories are loading
        this.idsaLoader = new IdsaLoader(logger);
        this.idsaLoader.InitPotentialField(this.feeder.getTrajectories());
        //loading models
        LSTMAgent agentModel = new LSTMAgent(InputNetwork.inputSize, ReadConfig.Configurations.getHiddenLayersAgent(), ReadConfig.Configurations.getHiddenNeuronsAgent(), OutputNetwork.outputSize);
        //decide  model to implement here
        ENNClassifier classifierModel = new ENNClassifier(tgcfs.Classifiers.InputNetwork.inputSize, ReadConfig.Configurations.getHiddenNeuronsClassifier(), tgcfs.Classifiers.OutputNetwork.outputSize);
        //generate population
        //INITIALISE population EA with random candidate solution
        //check if I am loading the population from file or not
        if(ReadConfig.Configurations.getLoadDumpPop()){
            LoadExternalPopulation load = new LoadExternalPopulation(logger);
            load.readFile();
            this.agents.generatePopulation(agentModel, load.getAgents());
            this.classifiers.generatePopulation(classifierModel, load.getClassifiers());
        }else{
            this.agents.generatePopulation(agentModel);
            this.classifiers.generatePopulation(classifierModel);
        }

        logger.log(Level.INFO, agentModel.getSummary());
        logger.log(Level.INFO, classifierModel.getSummary());
        logger.log(Level.INFO, "Framework online!");

        //save the sha-1 info in the output files
        SaveToFile.Saver.initialiseFitnessFile(this.agents.getClass().getName());
        SaveToFile.Saver.initialiseGenomaFile(this.agents.getClass().getName());
        SaveToFile.Saver.initialiseFitnessFile(this.classifiers.getClass().getName());
        SaveToFile.Saver.initialiseGenomaFile(this.classifiers.getClass().getName());

        //load the stepsize
        new StepSize();

        //load max fitness on countermeasures class
        this.countermeasures.setMaxFitness(this.agents.getMaxFitnessAchievable(), this.classifiers.getMaxFitnessAchievable());
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
        List<TrainReal> combineInputList = this.feeder.multiFeeder(this.idsaLoader, null);
        //execution agents
        logger.log(Level.INFO,"Run Agents...");
        //run the agents
        this.agents.runIndividuals(combineInputList);
        //classifier are executed and evaluated during agents evaluations
        logger.log(Level.INFO,"Run Classifiers...");
        this.agents.evaluateIndividuals(this.classifiers, new FollowingTheGraph(this.feeder, logger));
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
            this.agents.resetScore();
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
                combineInputList = this.feeder.multiFeeder(this.idsaLoader, combineInputList);
                //execution agents
                logger.log(Level.INFO,"Run Agents...");
                this.agents.runIndividuals(combineInputList);
                logger.log(Level.INFO,"Run Classifiers...");
                //classifier are executed and evaluated during agents evaluations
                this.agents.evaluateIndividuals(this.classifiers, new FollowingTheGraph(this.feeder, logger));

                //I need to generate this dataset for testing the classifiers and understand visually what is happening
                //this is happening only in the last generation
                if (ReadConfig.Configurations.getDumpTrajectoryPointAndMeaning()){
                    logger.log(Level.INFO, "Dump agent generation and real");
//                    this.saveTrajectoryAndGeneratedPoints(combineInputList, new FollowingTheGraph(this.feeder), generationAgent, generationClassifier);
                    this.agents.saveTrajectoriesAndPointGenerated(generationAgent, generationClassifier, new FollowingTheGraph(this.feeder, logger));
                    this.agents.saveScoresBattle(generationAgent, generationClassifier);
                }

                //countermeasures system against disengagement
                this.countermeasures.checkEvolutionOnlyOnePopulation(this.agents.getFittestIndividual().getFitness(), this.classifiers.getFittestIndividual().getFitness(), this.agents.getMaxFitnessAchievable(), this.classifiers.getMaxFitnessAchievable(), this);
                evolveAgent = this.countermeasures.isEvolveAgent();
                evolveClassifier = this.countermeasures.isEvolveClassifier();
                this.countermeasures.executeCountermeasuresAgainstDisengagement(this.agents.getPopulation(), IndividualStatus.AGENT);
                this.countermeasures.executeCountermeasuresAgainstDisengagement(this.classifiers.getPopulation(), IndividualStatus.CLASSIFIER);


            /* { SELECT individuals next generation } */
                logger.log(Level.INFO,"Parent Selection...");
                if(evolveAgent) this.agents.survivalSelections();
                if(evolveClassifier) this.classifiers.survivalSelections();

                //save the fitness of all the population and best genome
                logger.log(Level.INFO,"Saving Statistics...");
                SaveToFile.Saver.saveFitness(this.agents.getClass().getName(), this.agents.retAllFitness());
                SaveToFile.Saver.saveFitness(this.classifiers.getClass().getName(), this.classifiers.retAllFitness());
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
     * run the generative part more time than the discriminative part
     * @param number number of time to run
     * @throws Exception something wrong happens
     */
    public void generateMoreThanDiscriminate(Integer number) throws Exception {
        List<TrainReal> combineInputList = null;
        for(int i = 0; i < number - 1; i++) {
            //I have to evolve for this number of time-step -1 only the  agents and not the classifiers
            //The last timestep I am evolving both
            logger.log(Level.INFO, "Evaluation only agents' generation " + i);
                /* { EVALUATE new candidate } */
            combineInputList = this.feeder.multiFeeder(this.idsaLoader, combineInputList);
            //train the agents
            this.agents.trainNetwork(combineInputList);
            //execution agents
            logger.log(Level.INFO, "Run Agents...");
            this.agents.runIndividuals(combineInputList);
            logger.log(Level.INFO, "Evaluate Agents...");
            //classifier are executed and evaluated during agents evaluations
            this.agents.evaluateIndividuals(this.classifiers, new FollowingTheGraph(this.feeder, logger));
            //reset classifier, I am not evolving them now
            this.classifiers.resetFitness();
                /* { SELECT individuals next generation } */
            logger.log(Level.INFO, "Parent Selection...");
            this.agents.survivalSelections();
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
     * @param generationAgent number of generation for the agent population
     * @param generationClassifier number of generation for the classifier population
     * @throws Exception If something goes wrong
     */
//    private void saveTrajectoryAndGeneratedPoints(List<TrainReal> combineInputList, FollowingTheGraph transformation, int generationAgent, int generationClassifier) throws Exception {
//        //compute the real point.
//        combineInputList.forEach(trainReal -> {
//            if(trainReal.getRealPointsOutputComputed() == null) {
//                List<PointWithBearing> generatedPoint = new ArrayList<>();
//                transformation.setLastPoint(trainReal.getLastPoint());
//                trainReal.getOutputComputed().forEach(outputsNetwork -> generatedPoint.add(new PointWithBearing(transformation.singlePointConversion(outputsNetwork))));
//                trainReal.setRealPointsOutputComputed(generatedPoint);
//            }
//        });
//        SaveToFile.Saver.dumpTrajectoryAndGeneratedPart(combineInputList, generationAgent, generationClassifier);
//    }


    /**
     * Getter for the agents
     * @return {@link Agents}
     */
    public Agents getAgents(){
        return this.agents;
    }

    /**
     * Modify the counting time value for this class
     * @param countingTime new int value
     */
    public void setCountingTime(int countingTime) {
        this.countingTime = countingTime;
    }

    /**
     * Getter for the counting time value
     * @return int value
     */
    public int getCountingTime() {
        return this.countingTime;
    }
}

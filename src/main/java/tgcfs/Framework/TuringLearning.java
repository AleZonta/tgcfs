package tgcfs.Framework;

import org.nd4j.linalg.api.buffer.DataBuffer;
import org.nd4j.linalg.factory.Nd4j;
import tgcfs.Agents.InputNetwork;
import tgcfs.Agents.InputNetworkTime;
import tgcfs.Agents.Models.Clax;
import tgcfs.Agents.Models.ConvAgent;
import tgcfs.Agents.Models.LSTMAgent;
import tgcfs.Agents.OutputNetwork;
import tgcfs.Classifiers.Models.ENNClassifier;
import tgcfs.Classifiers.Models.LSTMClassifier;
import tgcfs.Config.PropertiesFileReader;
import tgcfs.Config.ReadConfig;
import tgcfs.EA.Agents;
import tgcfs.EA.Algorithm;
import tgcfs.EA.Classifiers;
import tgcfs.EA.Helpers.EngagementPopulation;
import tgcfs.EA.Mutation.StepSize;
import tgcfs.Idsa.IdsaLoader;
import tgcfs.InputOutput.FollowingTheGraph;
import tgcfs.InputOutput.LoadExternalPopulation;
import tgcfs.Loader.Feeder;
import tgcfs.Loader.ReachedMaximumNumberException;
import tgcfs.Loader.TrainReal;
import tgcfs.NN.EvolvableModel;
import tgcfs.Performances.SaveToFile;
import tgcfs.Utils.IndividualStatus;
import tgcfs.Utils.LogSystem;
import tgcfs.Utils.RandomGenerator;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
        Nd4j.setDataType(DataBuffer.Type.DOUBLE);

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
        EvolvableModel agentModel;
        //decide which model to implement here
        switch (ReadConfig.Configurations.getValueModel()){
            case 0:
                if(ReadConfig.Configurations.getTimeAsInput()) {
                    agentModel = new LSTMAgent(InputNetworkTime.inputSize, ReadConfig.Configurations.getHiddenLayersAgent(), ReadConfig.Configurations.getHiddenNeuronsAgent(), OutputNetwork.outputSize);
                }else{
                    agentModel = new LSTMAgent(InputNetwork.inputSize, ReadConfig.Configurations.getHiddenLayersAgent(), ReadConfig.Configurations.getHiddenNeuronsAgent(), OutputNetwork.outputSize);
                }
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
        EvolvableModel classifierModel;
        //decide which model to implement here
        switch (ReadConfig.Configurations.getValueClassifier()){
            case 0:
                classifierModel = new ENNClassifier(tgcfs.Classifiers.InputNetwork.inputSize, ReadConfig.Configurations.getHiddenNeuronsClassifier(), tgcfs.Classifiers.OutputNetwork.outputSize);
                break;
            case 1:
                classifierModel = new LSTMClassifier(tgcfs.Classifiers.InputNetwork.inputSize, ReadConfig.Configurations.getHiddenLayersAgent(), ReadConfig.Configurations.getHiddenNeuronsClassifier(), tgcfs.Classifiers.OutputNetwork.outputSize);
                break;
            default:
                throw new NoSuchMethodError("Model not yet implemented");
        }
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
        int fitnessTypology = ReadConfig.Configurations.getFitnessFunction();
        switch (fitnessTypology) {
            case 0:
                logger.log(Level.INFO, "Original Fitness Function Selected");
                break;
            case 1:
                logger.log(Level.INFO, "Selmar Fitness Function Selected");
                break;
            default:
                throw new Exception("Fitness Function Not implemented");
        }

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
        if(ReadConfig.Configurations.getMutation() == 0) SaveToFile.Saver.saveStepSize(this.agents.getClass().getName(), this.agents.retStepSizeBestGenome());
        SaveToFile.Saver.saveBestGenoma(this.classifiers.getClass().getName(),this.classifiers.retBestGenome());
        if(ReadConfig.Configurations.getMutation() == 0) SaveToFile.Saver.saveStepSize(this.classifiers.getClass().getName(), this.classifiers.retStepSizeBestGenome());


        /* { REPEAT until TERMINAL CONDITION } */
        boolean reachedEndTrajectory = Boolean.FALSE;
        boolean randomError = Boolean.FALSE;
        boolean evolveAgent = Boolean.TRUE;
        boolean evolveClassifier = Boolean.TRUE;
        Integer maxGeneration = ReadConfig.Configurations.getMaxGenerations();
        while(!reachedEndTrajectory && !randomError && generationAgent <= maxGeneration && generationClassifier <= maxGeneration) {
            if(evolveAgent) generationAgent++;
            if(evolveClassifier) generationClassifier++;
            this.agents.resetScore();
            /* { SELECT parent }
               { RECOMBINE parents }
               { MUTATE offspring } */


            class ComputeGenerationOffspring implements Runnable {
                private CountDownLatch latch;
                private Algorithm population;
                private boolean evolve;
                private int gen;

                private ComputeGenerationOffspring(Algorithm population, boolean evolve, int gen){
                    this.population = population;
                    this.evolve = evolve;
                    this.gen = gen;
                }

                /**
                 * CountDownLatch is a java class in the java.util.concurrent package. It is a mechanism to safely handle
                 * counting the number of completed tasks. You should call latch.countDown() whenever the run method competes.
                 * @param latch {@link CountDownLatch}
                 */
                private void setLatch(CountDownLatch latch) {
                    this.latch = latch;
                }

                @Override
                public void run() {
                    logger.log(Level.INFO,"[" + this.population.getClass().getName() + "] Generating Offspring...");
                    try {
                        if (ReadConfig.Configurations.isRecombination()) {
                            if (this.evolve) this.population.generateOffspring();
                        } else {
                            //lets start checking If I am going to use the Hall Of Fame
                            boolean hallOfFame = this.isTimeForHallOfFame();
                            if (this.evolve) {
                                this.population.generateOffspringOnlyWithMutation(this.gen, hallOfFame);
                            } else {
                                //If I am not evolving the classifier I still have to reset their fitness
                                this.population.resetFitness();
                            }
                        }
                    } catch (Exception e) {
                        logger.log(Level.WARNING, "Error in generation offspring -> " + e.getMessage());
                    }
                    latch.countDown();
                }

                /**
                 * Return if it is time to use the Hall Of Fame
                 * @return boolean value
                 */
                private boolean isTimeForHallOfFame(){
                    return RandomGenerator.getNextDouble() < 0.1;
                }
            }

            ExecutorService execOffspring = Executors.newFixedThreadPool(2);
            CountDownLatch latchOffspring = new CountDownLatch(2);

            ComputeGenerationOffspring[] runnablesOffspring = new ComputeGenerationOffspring[2];
            //create all the runnables
            runnablesOffspring[0] = new ComputeGenerationOffspring(this.agents, evolveAgent, generationAgent);
            runnablesOffspring[1] = new ComputeGenerationOffspring(this.classifiers, evolveClassifier, generationClassifier);

            //execute them and wait them till they have finished
            for(ComputeGenerationOffspring r : runnablesOffspring) {
                r.setLatch(latchOffspring);
                execOffspring.execute(r);
            }
            try {
                latchOffspring.await();
                execOffspring.shutdown();
            }catch (InterruptedException e) {
                e.printStackTrace();
            }


            logger.log(Level.INFO, "Evaluation agent generation " + generationAgent + " and classifier generation " + generationClassifier);
            /* { EVALUATE new candidate } */
            try {
                logger.log(Level.INFO,"Loading new trajectories...");
                combineInputList = this.feeder.multiFeeder(this.idsaLoader, combineInputList);
                this.agents.requestSampleHoF();
                this.classifiers.requestSampleHoF();
                //execution agents
                logger.log(Level.INFO,"Run Agents...");
                this.agents.runIndividuals(combineInputList);
                this.agents.generateRealPoints(new FollowingTheGraph(this.feeder, logger));
                logger.log(Level.INFO,"Run Classifiers...");
                //classifier are executed and evaluated during agents evaluations
                this.agents.evaluateIndividuals(this.classifiers, new FollowingTheGraph(this.feeder, logger));

                //I need to generate this dataset for testing the classifiers and understand visually what is happening
                //this is happening only in the last generation
                if (ReadConfig.Configurations.getDumpTrajectoryPointAndMeaning()){
                    logger.log(Level.INFO, "Dump agent generation and real");
//                    this.saveTrajectoryAndGeneratedPoints(combineInputList, new FollowingTheGraph(this.feeder), generationAgent, generationClassifier);
                    this.agents.saveTrajectoriesAndPointGenerated(generationAgent, generationClassifier);
                    if(ReadConfig.Configurations.getScore()) this.agents.saveScoresBattle(generationAgent, generationClassifier);
                }

                //countermeasures system against disengagement
                this.countermeasures.checkEvolutionOnlyOnePopulation(this.agents.getFittestIndividual().getFitness(), this.classifiers.getFittestIndividual().getFitness(), this.agents.getMaxFitnessAchievable(), this.classifiers.getMaxFitnessAchievable(), this);

                /**
                 * Run the last part of the evolution in a parallel way
                 * - checking for enable countermeasures
                 * - checking and returning if population is evolving in this generation
                 * - survival selection
                 * - save statistics
                 * - dump population
                 */
                class ComputeSurvivalSelection implements Runnable{
                    private CountDownLatch latch;
                    private Algorithm population;
                    private EngagementPopulation countermeasures;
                    private boolean evolve;

                    private ComputeSurvivalSelection(Algorithm population, EngagementPopulation contermeasures){
                        this.population = population;
                        this.countermeasures = contermeasures;
                    }

                    private boolean getEvolve(){
                        return this.evolve;
                    }

                    /**
                     * CountDownLatch is a java class in the java.util.concurrent package. It is a mechanism to safely handle
                     * counting the number of completed tasks. You should call latch.countDown() whenever the run method competes.
                     * @param latch {@link CountDownLatch}
                     */
                    private void setLatch(CountDownLatch latch) {
                        this.latch = latch;
                    }

                    /**
                     * Run
                     */
                    @Override
                    public void run() {
                        try {

                            if(this.population.getClass().equals(Agents.class)){
                                this.countermeasures.executeCountermeasuresAgainstDisengagement(this.population.getPopulation(), IndividualStatus.AGENT);
                                this.evolve = this.countermeasures.isEvolveAgent();
                            }else{
                                this.countermeasures.executeCountermeasuresAgainstDisengagement(this.population.getPopulation(), IndividualStatus.CLASSIFIER);
                                this.evolve = this.countermeasures.isEvolveClassifier();
                            }
                            logger.log(Level.INFO,"[" + this.population.getClass().getName() + "] Parent Selection...");
                            /* { SELECT individuals next generation } */
                            if (this.evolve) this.population.survivalSelections();
                            //save the fitness of all the population and best genome
                            logger.log(Level.INFO,"[" + this.population.getClass().getName() + "] Saving Statistics...");
                            SaveToFile.Saver.saveFitness(this.population.getClass().getName(), this.population.retAllFitness());
                            if (this.evolve) {
                                SaveToFile.Saver.saveBestGenoma(this.population.getClass().getName(),this.population.retBestGenome());
                                if(ReadConfig.Configurations.getMutation() == 0) SaveToFile.Saver.saveStepSize(this.population.getClass().getName(), this.population.retStepSizeBestGenome());
                            }
                            if(ReadConfig.Configurations.getDumpPop()) {
                                logger.log(Level.INFO,"[" + this.population.getClass().getName() + "] Dump Population...");
                                if(this.evolve) SaveToFile.Saver.dumpPopulation(this.population.getClass().getName(), this.population.getPopulation());
                            }


                        } catch (Exception e) {
                            logger.log(Level.WARNING, "Error in survival selection -> " + e.getMessage());
                        }

                        latch.countDown();
                    }
                }

                ExecutorService execSelection = Executors.newFixedThreadPool(2);
                CountDownLatch latchSelection = new CountDownLatch(2);
                ComputeSurvivalSelection[] runnablesSelection = new ComputeSurvivalSelection[2];
                //create all the runnables
                runnablesSelection[0] = new ComputeSurvivalSelection(this.agents, this.countermeasures);
                runnablesSelection[1] = new ComputeSurvivalSelection(this.classifiers, this.countermeasures);

                //execute them and wait them till they have finished
                for(ComputeSurvivalSelection r : runnablesSelection) {
                    r.setLatch(latchSelection);
                    execSelection.execute(r);
                }
                try {
                    latchSelection.await();
                    execSelection.shutdown();

                    //collecting the results
                    evolveAgent = runnablesSelection[0].getEvolve();
                    evolveClassifier = runnablesSelection[1].getEvolve();
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }


                //I need to generate this dataset for testing the classifiers and understand visually what is happening
                //this is happening only in the last generation
                if (ReadConfig.Configurations.getDumpTrajectoryPointAndMeaning()){
                    logger.log(Level.INFO, "Dump agent after survival selection");
                    this.agents.saveTrajectoriesAfterSelection(generationAgent);
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
                this.agents.generateOffspringOnlyWithMutation(0, false);
            }
        }
    }



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


    /**
     * Return if it is time to use the Hall Of Fame
     * @return boolean value
     */
    public boolean isTimeForHallOfFame(){
        return RandomGenerator.getNextDouble() < 0.1;
    }
}

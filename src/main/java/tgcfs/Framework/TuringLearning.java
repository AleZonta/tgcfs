package tgcfs.Framework;

import lgds.trajectories.Point;
import org.nd4j.linalg.factory.Nd4j;
import tgcfs.Agents.InputNetwork;
import tgcfs.Agents.Models.Clax;
import tgcfs.Agents.Models.ConvAgent;
import tgcfs.Agents.Models.LSTMAgent;
import tgcfs.Agents.Models.RealAgents;
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
    private RealAgents realAgent;
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

        this.realAgent = null;
        this.feeder = null;
        this.idsaLoader = null;

        //initialise the saving class
        new SaveToFile.Saver(ReadConfig.Configurations.getName(), ReadConfig.Configurations.getExperiment(), ReadConfig.Configurations.getPath());
        SaveToFile.Saver.dumpSetting(ReadConfig.Configurations.getConfig());

        //back up for convolution, in java there are some problems
        if(Objects.equals(ReadConfig.Configurations.getValueModel(), ReadConfig.Configurations.Convolution)) Nd4j.enableFallbackMode(Boolean.TRUE);
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
        this.realAgent = new RealAgents();
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
        Integer generation = 0;
        /* { EVALUATE each candidate } */
        logger.log(Level.INFO, "Evaluation generation " + generation.toString());

        //load several pieces of trajectory
        List<TrainReal> combineInputList = this.feeder.multiFeeder(this.idsaLoader);
        //create the real agents for this session
        this.realAgent.createAgent(combineInputList);
        //execution agents
        logger.log(Level.INFO,"Run Agents...");
        //train the agents
        this.agents.trainNetwork(combineInputList);
        //run the agents
        this.agents.runIndividuals(combineInputList);
        //classifier are executed and evaluated during agents evaluations
        logger.log(Level.INFO,"Run Classifiers...");
        this.agents.evaluateIndividuals(this.classifiers, new FollowingTheGraph(this.feeder));
        this.classifiers.evaluateRealAgent(this.realAgent, new FollowingTheGraph(this.feeder));
        //save the fitness of all the population and the best genome
        SaveToFile.Saver.saveFitness(this.agents.getClass().getName(),this.agents.retAllFitness());
        SaveToFile.Saver.saveFitness(this.classifiers.getClass().getName(),this.classifiers.retAllFitness());
        SaveToFile.Saver.saveBestGenoma(this.agents.getClass().getName(),this.agents.retBestGenome());
        SaveToFile.Saver.saveBestGenoma(this.classifiers.getClass().getName(),this.classifiers.retBestGenome());


        /* { REPEAT until TERMINAL CONDITION } */
        Boolean reachedEndTrajectory = Boolean.FALSE;
        Boolean randomError = Boolean.FALSE;
        Integer maxGeneration = ReadConfig.Configurations.getMaxGenerations();
        while(!reachedEndTrajectory && !randomError && generation <= maxGeneration) {
            generation++;
            logger.log(Level.INFO, "Evaluation generation " + generation.toString());
            /* { SELECT parent }
               { RECOMBINE parents }
               { MUTATE offspring } */
            logger.log(Level.INFO,"Generating Offspring...");

            if(ReadConfig.Configurations.isRecombination()) {
                this.agents.generateOffspring();
                this.classifiers.generateOffspring();
            }else{
                this.agents.generateOffspringOnlyWithMutation();
                this.classifiers.generateOffspringOnlyWithMutation();
            }


            Integer number = ReadConfig.Configurations.getTimestepEvolveAgentOverClassifier();
            if(number > 0){
                this.generateMoreThanDiscriminate(number);
            }

            /* { EVALUATE new candidate } */
            try {
                combineInputList = this.feeder.multiFeeder(this.idsaLoader);
                this.realAgent.createAgent(combineInputList);
                //train the agents
                this.agents.trainNetwork(combineInputList);
                //execution agents
                logger.log(Level.INFO,"Run Agents...");
                this.agents.runIndividuals(combineInputList);
                logger.log(Level.INFO,"Run Classifiers...");
                //classifier are executed and evaluated during agents evaluations
                this.agents.evaluateIndividuals(this.classifiers, new FollowingTheGraph(this.feeder));
                this.classifiers.evaluateRealAgent(this.realAgent, new FollowingTheGraph(this.feeder));


                //I need to generate this dataset for testing the classifiers and understand visually what is happening
                //this is happening only in the last generation
                if (ReadConfig.Configurations.getDumpTrajectoryPointAndMeaning() && generation == maxGeneration - 1){
                    logger.log(Level.INFO, "Dump agent generation and real");
                    this.saveTrajectoryAndGeneratedPoints(combineInputList, new FollowingTheGraph(this.feeder));
                }


            /* { SELECT individuals next generation } */
                logger.log(Level.INFO,"Parent Selection...");
                this.agents.selectParents();
                this.classifiers.selectParents();

                //save the fitness of all the population and best genome
                SaveToFile.Saver.saveFitness(this.agents.getClass().getName(), this.agents.retAllFitness());
                SaveToFile.Saver.saveFitness(this.classifiers.getClass().getName(), this.classifiers.retAllFitness());
                SaveToFile.Saver.saveBestGenoma(this.agents.getClass().getName(),this.agents.retBestGenome());
                SaveToFile.Saver.saveBestGenoma(this.classifiers.getClass().getName(),this.classifiers.retBestGenome());
                if(ReadConfig.Configurations.getDumpPop()) {
                    logger.log(Level.INFO,"Dump Population...");
                    SaveToFile.Saver.dumpPopulation(this.agents.getClass().getName(), this.agents.getPopulation());
                    SaveToFile.Saver.dumpPopulation(this.classifiers.getClass().getName(), this.classifiers.getPopulation());
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
        logger.log(Level.INFO, "Co-evolution ended");
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
            this.realAgent.createAgent(combineInputList);
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
        //compute the real point. //TODO check it it is faster to do this for every points directly in computation and then use the converted one as imput
        combineInputList.forEach(trainReal -> {
            List<Point> generatedPoint = new ArrayList<>();
            transformation.setLastPoint(trainReal.getLastPoint());
            trainReal.getOutputComputed().forEach(outputsNetwork -> generatedPoint.add(transformation.singlePointConversion(outputsNetwork)));
            trainReal.setRealPointsOutputComputed(generatedPoint);
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

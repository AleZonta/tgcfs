package tgcfs;

import tgcfs.Agents.InputNetwork;
import tgcfs.Agents.LSTMAgent;
import tgcfs.Agents.OutputNetwork;
import tgcfs.Agents.RealAgents;
import tgcfs.Classifiers.Classifier;
import tgcfs.Config.ReadConfig;
import tgcfs.EA.Agents;
import tgcfs.EA.Classifiers;
import tgcfs.Idsa.IdsaLoader;
import tgcfs.InputOutput.FollowingTheGraph;
import tgcfs.Loader.Feeder;
import tgcfs.Loader.ReachedMaximumNumberException;
import tgcfs.Loader.TrainReal;
import tgcfs.NN.EvolvableNN;
import tgcfs.Performances.SaveToFile;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Alessandro Zonta on 16/05/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 *
 * Loader class For everything.
 * This class will launch the experiment.
 * It loads all the agents, all the classifiers and lunch the experiment
 *
 */
public class App {
    private Agents agents;
    private Classifiers classifiers;
    private Feeder feeder;
    private IdsaLoader idsaLoader;
    private RealAgents realAgent;
    private static final Logger logger = Logger.getLogger(App.class.getName()); //logger for this class


    /**
     * Constructor zero parameter
     * Loading the config files
     * @throws Exception if there are problems with the reading procedure
     */
    public App() throws Exception {
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
    }


    /**
     * Method that loads the neural network and the population of the Turing Learning
     * It loads also the graph, the trajectory system, and the potential field system
     * @throws Exception something is wrong with the loading
     */
    public void load() throws Exception {
        logger.log(Level.INFO, "Starting App...");
        //loading models
        EvolvableNN agentModel = new LSTMAgent(InputNetwork.inputSize, ReadConfig.Configurations.getHiddenLayersAgent(), ReadConfig.Configurations.getHiddenNeuronsAgent(), OutputNetwork.outputSize);
        EvolvableNN classifierModel = new Classifier(tgcfs.Classifiers.InputNetwork.inputSize, ReadConfig.Configurations.getHiddenNeuronsClassifier(), tgcfs.Classifiers.OutputNetwork.outputSize);
        //generate population
        //INITIALISE population EA with random candidate solution
        this.agents.generatePopulation(agentModel);
        this.classifiers.generatePopulation(classifierModel);
        this.realAgent = new RealAgents();
        //loading graph and trajectories
        this.feeder = new Feeder();
        this.feeder.loadSystem();
        //loading potential field
        //idsa loader I can also add the total number of tracks
        //now all the trajectories are loading
        this.idsaLoader = new IdsaLoader();
        this.idsaLoader.InitPotentialField(this.feeder.getTrajectories());
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
                for(int i = 0; i < number - 1; i++) {
                    //I have to evolve for this number of time-step -1 only the  agents and not the classifiers
                    //The last timestep I am evolving both
                    logger.log(Level.INFO, "Evaluation only agents' generation " + i);
                /* { EVALUATE new candidate } */
                    combineInputList = this.feeder.multiFeeder(this.idsaLoader);
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


    public static void main( String[] args )
    {
        App app = null;
        try {
            app = new App();
            app.load();
            app.run();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

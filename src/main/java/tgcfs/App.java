package tgcfs;

import tgcfs.Agents.Agent;
import tgcfs.Agents.InputNetwork;
import tgcfs.Agents.LSTMAgent;
import tgcfs.Agents.OutputNetwork;
import tgcfs.Classifiers.Classifier;
import tgcfs.Config.ReadConfig;
import tgcfs.EA.Agents;
import tgcfs.EA.Classifiers;
import tgcfs.Idsa.IdsaLoader;
import tgcfs.InputOutput.FollowingTheGraph;
import tgcfs.Loader.Feeder;
import tgcfs.Loader.ReachedMaximumNumberException;
import tgcfs.NN.EvolvableNN;
import tgcfs.NN.InputsNetwork;
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
    private ReadConfig configFile; //file containing configuration
    private Agents agents;
    private Classifiers classifiers;
    private Feeder feeder;
    private IdsaLoader idsaLoader;
    private Agent realAgent;
    private static final Logger logger = Logger.getLogger(App.class.getName()); //logger for this class


    /**
     * Constructor zero parameter
     * Loading the config files
     * @throws Exception if there are problems with the reading procedure
     */
    public App() throws Exception {
        //Creating the agents
        this.configFile = new ReadConfig();
        this.configFile.readFile();

        this.agents = new Agents();
        this.classifiers = new Classifiers();

        this.realAgent = null;
        this.feeder = null;
        this.idsaLoader = null;

        //initialise the saving class
        new SaveToFile.Saver(this.configFile.getName(), this.configFile.getExperiment(), this.configFile.getPath());
        SaveToFile.Saver.dumpSetting(this.configFile);
    }


    /**
     * Method that loads the neural network and the population of the Turing Learning
     * It loads also the graph, the trajectory system, and the potential field system
     * @throws Exception something is wrong with the loading
     */
    public void load() throws Exception {
        logger.log(Level.INFO, "Starting App...");
        //loading models
        EvolvableNN agentModel = new LSTMAgent(InputNetwork.inputSize, this.configFile.getHiddenLayersAgent(), this.configFile.getHiddenNeuronsAgent(), OutputNetwork.outputSize);
        EvolvableNN classifierModel = new Classifier(tgcfs.Classifiers.InputNetwork.inputSize, this.configFile.getHiddenNeuronsClassifier(), tgcfs.Classifiers.OutputNetwork.outputSize);
        //generate population
        //INITIALISE population EA with random candidate solution
        this.agents.generatePopulation(agentModel);
        this.classifiers.generatePopulation(classifierModel);
        this.realAgent = new Agent();
        //loading graph and trajectories
        this.feeder = new Feeder();
        this.feeder.loadSystem();
        //loading potential field
        //idsa loader I can also add the toatl number of tracks
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
        //load new trajectory, transform the trajectory into the input wanted
        List<InputsNetwork> inputList = this.feeder.feeder(this.idsaLoader);
        this.realAgent.setRealOutput(this.feeder.obtainRealAgentSectionTrajectory());
        //execution agents
        logger.log(Level.INFO,"Run Agents...");
        this.agents.runIndividuals(inputList);
        //classifier are executed and evaluated during agents evaluations
        logger.log(Level.INFO,"Run Classifiers...");
        this.agents.evaluateIndividuals(this.classifiers, new FollowingTheGraph(this.feeder, this.realAgent.getLastPoint()));
        this.classifiers.evaluateRealAgent(this.realAgent, new FollowingTheGraph(this.feeder, this.realAgent.getLastPoint()));
        //save the fitness of all the population and the best genome
        SaveToFile.Saver.saveFitness(this.agents.getClass().getName(),this.agents.retAllFitness());
        SaveToFile.Saver.saveFitness(this.classifiers.getClass().getName(),this.classifiers.retAllFitness());
        SaveToFile.Saver.saveBestGenoma(this.agents.getClass().getName(),this.agents.retBestGenome());
        SaveToFile.Saver.saveBestGenoma(this.classifiers.getClass().getName(),this.classifiers.retBestGenome());


        /* { REPEAT until TERMINAL CONDITION } */
        Boolean reachedEndTrajectory = Boolean.FALSE;
        Boolean randomError = Boolean.FALSE;
        Integer maxGeneration = this.configFile.getMaxGenerations();
        while(!reachedEndTrajectory || !randomError || generation <= maxGeneration) {
            generation++;
            logger.log(Level.INFO, "Evaluation generation " + generation.toString());
            /* { SELECT parent }
               { RECOMBINE parents }
               { MUTATE offspring } */
            logger.log(Level.INFO,"Generating Offspring...");
            this.agents.generateOffspring();
            this.classifiers.generateOffspring();

            /* { EVALUATE new candidate } */
            try {
                inputList = this.feeder.feeder(this.idsaLoader);
                this.realAgent.setRealOutput(this.feeder.obtainRealAgentSectionTrajectory());
                //execution agents
                logger.log(Level.INFO,"Run Agents...");
                this.agents.runIndividuals(inputList);
                logger.log(Level.INFO,"Run Classifiers...");
                //classifier are executed and evaluated during agents evaluations
                this.agents.evaluateIndividuals(this.classifiers, new FollowingTheGraph(this.feeder, this.realAgent.getLastPoint()));
                this.classifiers.evaluateRealAgent(this.realAgent, new FollowingTheGraph(this.feeder, this.realAgent.getLastPoint()));

            /* { SELECT individuals next generation } */
                logger.log(Level.INFO,"Parent Selection...");
                this.agents.selectParents();
                this.classifiers.selectParents();

                //save the fitness of all the population and best genome
                SaveToFile.Saver.saveFitness(this.agents.getClass().getName(), this.agents.retAllFitness());
                SaveToFile.Saver.saveFitness(this.classifiers.getClass().getName(), this.classifiers.retAllFitness());
                SaveToFile.Saver.saveBestGenoma(this.agents.getClass().getName(),this.agents.retBestGenome());
                SaveToFile.Saver.saveBestGenoma(this.classifiers.getClass().getName(),this.classifiers.retBestGenome());

            } catch (ReachedMaximumNumberException e) {
                logger.log(Level.INFO, e.getMessage());
                reachedEndTrajectory = Boolean.TRUE;
            } catch (Exception e){
                logger.log(Level.WARNING, "Error concluded the main loop -> " + e.getMessage());
                e.printStackTrace();
                randomError = Boolean.TRUE;
            }
        }

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

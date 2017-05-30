package tgcfs;

import tgcfs.Agents.InputNetwork;
import tgcfs.Agents.LSTMAgent;
import tgcfs.Agents.OutputNetwork;
import tgcfs.Classifiers.Classifier;
import tgcfs.Config.ReadConfig;
import tgcfs.EA.Agents;
import tgcfs.EA.Classifiers;
import tgcfs.NN.EvolvableNN;

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
    private EvolvableNN agentModel;
    private EvolvableNN classifierModel;
    private Agents agents;
    private Classifiers classifiers;
    protected static final Logger logger = Logger.getLogger(App.class.getName()); //logger for this class


    /**
     * Constructor zero parameter
     * Loading the config files
     * @throws Exception if there are problems with the reading procedure
     */
    public App() throws Exception {
        //Creating the agents
        this.configFile = new ReadConfig();
        this.configFile.readFile();
    }


    /**
     * Method that loads the neural network and the population of the Turing Learning
     * @throws Exception something is wrong with the loading
     */
    public void load() throws Exception {
        logger.log(Level.INFO, "Loading Models...");
        this.agentModel = new LSTMAgent(InputNetwork.inputSize, this.configFile.getHiddenLayersAgent(), this.configFile.getHiddenNeuronsAgent(), OutputNetwork.outputSize);
        this.classifierModel = new Classifier(tgcfs.Classifiers.InputNetwork.inputSize, this.configFile.getHiddenNeuronsClassifier(), tgcfs.Classifiers.OutputNetwork.outputSize);

        this.agents.generatePopulation(this.agentModel);
        this.classifiers.generatePopulation(this.classifierModel);
        logger.log(Level.INFO, "Models online!");
    }







    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
    }
}

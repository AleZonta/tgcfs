package tgcfs.Framework;

import org.nd4j.linalg.factory.Nd4j;
import tgcfs.Agents.InputNetwork;

import tgcfs.Agents.Models.LSTMAgent;
import tgcfs.Agents.OutputNetwork;
import tgcfs.Classifiers.Models.ENNClassifier;
import tgcfs.Classifiers.Models.LSTMClassifier;
import tgcfs.Config.ReadConfig;
import tgcfs.EA.Agents;
import tgcfs.EA.Classifiers;
import tgcfs.Idsa.IdsaLoader;
import tgcfs.Loader.Feeder;
import tgcfs.Loader.TrainReal;
import tgcfs.NN.EvolvableModel;
import tgcfs.Performances.SaveToFile;

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
 * Implements The Generative Adversarial Network framework
 *
 * Goodfellow, I. (2016). NIPS 2016 Tutorial: Generative Adversarial Networks.
 * http://doi.org/10.1001/jamainternmed.2016.8245
 *
 *
 */
public class GAN implements Framework{
    private EvolvableModel agent;
    private EvolvableModel classifier;
    private Feeder feeder;
    private IdsaLoader idsaLoader;
    private static final Logger logger = Logger.getLogger(GAN.class.getName()); //logger for this class


    /**
     * Constructor zero parameter
     * Loading the config files
     * @throws Exception if there are problems with the reading procedure
     */
    public GAN() throws Exception {
        //initialising the config file class
        new ReadConfig.Configurations();

        //Creating the agents
        this.agent = null;
        this.classifier = null;

        this.feeder = null;
        this.idsaLoader = null;

        //initialise the saving class
        new SaveToFile.Saver(ReadConfig.Configurations.getName(), ReadConfig.Configurations.getExperiment(), ReadConfig.Configurations.getPath(), logger);
        SaveToFile.Saver.dumpSetting(ReadConfig.Configurations.getConfig());

        //back up for convolution, in java there are some problems
        if(Objects.equals(ReadConfig.Configurations.getValueModel(), ReadConfig.Configurations.Convolution)) Nd4j.enableFallbackMode(Boolean.TRUE);
    }


    /**
     * Method that loads the models that will compete
     * It loads also the graph, the trajectory system, and the potential field system
     * @throws Exception something is wrong with the loading
     */
    @Override
    public void load() throws Exception {
        logger.log(Level.INFO, "Starting GAN...");
        //loading graph and trajectories
        this.feeder = new Feeder(logger);
        this.feeder.loadSystem();
        //loading potential field
        //idsa loader I can also add the total number of tracks
        //now all the trajectories are loading
        this.idsaLoader = new IdsaLoader(logger);
        this.idsaLoader.InitPotentialField(this.feeder.getTrajectories());
        //loading models
        //decide which model to implement here
        switch (ReadConfig.Configurations.getValueModel()){
            case 0:
                this.agent = new LSTMAgent(InputNetwork.inputSize, ReadConfig.Configurations.getHiddenLayersAgent(), ReadConfig.Configurations.getHiddenNeuronsAgent(), OutputNetwork.outputSize);
                break;
            default:
                throw new NoSuchMethodError("Model not yet implemented");
        }
        //decide which model to implement here
        switch (ReadConfig.Configurations.getValueClassifier()){
            case 0:
                this.classifier = new ENNClassifier(tgcfs.Classifiers.InputNetwork.inputSize, ReadConfig.Configurations.getHiddenNeuronsClassifier(), tgcfs.Classifiers.OutputNetwork.outputSize);
                break;
            case 1:
                this.classifier = new LSTMClassifier(tgcfs.Classifiers.InputNetwork.inputSize, ReadConfig.Configurations.getHiddenLayersAgent(), ReadConfig.Configurations.getHiddenNeuronsClassifier(), tgcfs.Classifiers.OutputNetwork.outputSize);
                break;
            default:
                throw new NoSuchMethodError("Model not yet implemented");
        }
        logger.log(Level.INFO, "Framework online!");
    }

    /**
     * Main loop of the GAN
     *
     * 1 -> training Discriminator on Real Data vs. Fake Data, with accurate labels
     * 2 -> train Generator to fool Discriminator, with inaccurate labels
     *
     * @throws Exception
     */
    @Override
    public void run() throws Exception {
        Integer generation = 0;
        Boolean reachedEndTrajectory = Boolean.FALSE;
        Boolean randomError = Boolean.FALSE;
        Integer maxGeneration = ReadConfig.Configurations.getMaxGenerations();


        //generate population of one
        Agents agents = new Agents(logger);
        if(ReadConfig.Configurations.getAgentPopulationSize() > 1) throw new Exception("Population in GAN must be one");
        agents.generatePopulation(this.agent);

        Classifiers classifier = new Classifiers(logger);
        if(ReadConfig.Configurations.getClassifierPopulationSize() > 1) throw new Exception("Population in GAN must be one");
        classifier.generatePopulation(this.classifier);

        //main loop
        while(!reachedEndTrajectory && !randomError && generation <= maxGeneration) {
            generation++;

            //load several pieces of trajectory
            List<TrainReal> combineInputList = this.feeder.multiFeeder(this.idsaLoader, null);
            //generate the output of the agent
            agents.runIndividuals(combineInputList);

            //train the Discriminator
            logger.log(Level.INFO,"Train Discriminator...");
            classifier.trainNetwork(combineInputList);


            //train the Generator
            logger.log(Level.INFO,"Train Generator...");
            throw new Exception("Not yet completed");
            //agents.trainNetwork(combineInputList);


        }
    }


}

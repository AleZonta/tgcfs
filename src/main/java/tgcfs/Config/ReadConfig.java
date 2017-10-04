package tgcfs.Config;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * Created by Alessandro Zonta on 11/05/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 *
 * This class reads the config file with the info needed by the program
 */
public class ReadConfig {
    private Integer trajectoriesType;
    private Integer howManySplitting;
    private Integer howManyTrajectories;
    //EA config
    private Integer agentPopulationSize;
    private Integer agentOffspringSize;
    private Double agentAlpha;
    private Integer agentTimeSteps;

    private Integer classifierPopulationSize;
    private Integer classifierOffspringSize;
    private Double classifierAlpha;
    private Integer classifierTimeSteps;

    private Integer maxGenerations;
    private Integer seed;
    private Integer mutation;
    private Boolean recombination;
    private Integer timestepEvolveAgentOverClassifier;

    //model config
    private Integer hiddenLayersAgent;
    private Integer hiddenNeuronsAgent;
    private Integer hiddenNeuronsClassifier;
    private Integer trajectoriesTrained;
    private Boolean train;


    //experiment config
    private String name;
    private String experiment;
    private String path;
    private Boolean dumpPop;
    private Boolean loadDumpPop;
    private Boolean dumpTrajectoryPointAndMeaning;

    //section type of model
    private Integer valueModel;
    private Boolean LSTM;
    private Boolean convolution;
    private Boolean clax;

    private Integer pictureSize;

    private Boolean checkAlsoPast;
    private Boolean automaticCalibration;


    /**
     * Constructor with zero parameter
     * Everything is set to null.
     */
    protected ReadConfig(){
        this.trajectoriesType = null;
        this.howManySplitting = null;
        this.howManyTrajectories = null;

        this.agentPopulationSize = null;
        this.agentOffspringSize = null;
        this.agentAlpha = null;
        this.agentTimeSteps = null;

        this.classifierPopulationSize = null;
        this.classifierOffspringSize = null;
        this.classifierAlpha = null;
        this.classifierTimeSteps = null;

        this.maxGenerations = null;
        this.seed = null;
        this.mutation = null;
        this.recombination = null;
        this.timestepEvolveAgentOverClassifier = null;

        this.hiddenLayersAgent = null;
        this.hiddenNeuronsAgent = null;
        this.hiddenNeuronsClassifier = null;
        this.trajectoriesTrained = null;
        this.train = null;

        this.name = null;
        this.experiment = null;
        this.path = null;
        this.dumpPop = null;
        this.loadDumpPop = null;


        this.LSTM = null;
        this.convolution = null;
        this.clax = null;
        this.valueModel = null;


        this.pictureSize = null;
        this.checkAlsoPast = null;
        this.dumpTrajectoryPointAndMeaning = null;

        this.automaticCalibration = null;
    }

    /**
     * Method that returns the location of the file containing the graph
     * @return String with the path
     * @throws Exception if I am trying to access it before reading it
     */
    public int getTrajectoriesType() throws Exception {
        if(this.trajectoriesType == null) throw new Exception("Try to access config file before reading it.");
        return this.trajectoriesType;
    }

    /**
     * Method that returns how many time I split the trajectory
     * @return Integer numberh
     * @throws Exception if I am trying to access it before reading it
     */
    public int getHowManySplitting() throws Exception {
        if(this.howManySplitting == null) throw new Exception("Try to access config file before reading it.");
        return this.howManySplitting;
    }

    /**
     * Getter for the offspring size for the agents
     * @return Integer number with the size
     * @throws Exception if I am trying to access it before reading it
     */
    public int getAgentOffspringSize() throws Exception {
        if(this.agentOffspringSize == null) throw new Exception("Try to access config file before reading it.");
        return this.agentOffspringSize;
    }

    /**
     * Getter for the population size for the agents
     * @return Integer number with the size
     * @throws Exception if I am trying to access it before reading it
     */
    public int getAgentPopulationSize() throws Exception {
        if(this.agentPopulationSize == null) throw new Exception("Try to access config file before reading it.");
        return this.agentPopulationSize;
    }

    /**
     * Getter for alpha for the agents
     * @return Double number
     * @throws Exception if I am trying to access it before reading it
     */
    public double getAgentAlpha() throws Exception {
        if(this.agentAlpha == null) throw new Exception("Try to access config file before reading it.");
        return this.agentAlpha;
    }

    /**
     * Getter for time steps for the agents
     * @return Integer number
     * @throws Exception if I am trying to access it before reading it
     */
    public int getAgentTimeSteps() throws Exception {
        if(this.agentTimeSteps == null) throw new Exception("Try to access config file before reading it.");
        return this.agentTimeSteps;
    }

    /**
     * Getter for the offspring size for the classifiers
     * @return Integer number with the size
     * @throws Exception if I am trying to access it before reading it
     */
    public int getClassifierOffspringSize() throws Exception {
        if(this.classifierOffspringSize == null) throw new Exception("Try to access config file before reading it.");
        return this.classifierOffspringSize;
    }

    /**
     * Getter for the population size for the classifiers
     * @return Integer number with the size
     * @throws Exception if I am trying to access it before reading it
     */
    public int getClassifierPopulationSize() throws Exception {
        if(this.classifierPopulationSize == null) throw new Exception("Try to access config file before reading it.");
        return this.classifierPopulationSize;
    }

    /**
     * Getter for alpha for the classifiers
     * @return Double number
     * @throws Exception if I am trying to access it before reading it
     */
    public double getClassifierAlpha() throws Exception {
        if(this.classifierAlpha == null) throw new Exception("Try to access config file before reading it.");
        return this.classifierAlpha;
    }

    /**
     * Getter for time steps for the classifiers
     * @return Integer number
     * @throws Exception if I am trying to access it before reading it
     */
    public int getClassifierTimeSteps() throws Exception {
        if(this.classifierTimeSteps == null) throw new Exception("Try to access config file before reading it.");
        return this.classifierTimeSteps;
    }

    /**
     * Method that reads the file with all the settings.
     * The file's name is hardcoded as "graph_setting.json".
     * @throws Exception If the file is not available, not well formatted or the settings are not all coded an exception
     * is raised
     */
    protected void readFile() throws Exception {
        //config file has to be located in the same directory as the program is
        String currentPath = Paths.get(".").toAbsolutePath().normalize().toString() + "/settings.json";
        //file is a json file, need to parse it and than I can read it
        FileReader reader;
        try {
            reader = new FileReader(currentPath);
        } catch (FileNotFoundException e) {
            throw new Exception("Config file not found.");
        }
        JSONParser parser = new JSONParser();
        JSONObject jsonObject;
        try {
            jsonObject = (JSONObject) parser.parse(reader);
        } catch (IOException | ParseException e) {
            throw new Exception("JSON file not well formatted.");
        }
        //reading the settings
        try {
            // 0 means IDSA, 1 means Geosat
            this.trajectoriesType = ((Long) jsonObject.get("trajectoriesType")).intValue();
        }catch (ClassCastException | NullPointerException e) {
            throw new Exception("TrajectoriesType is wrong or missing.");
        }
        try {
            // time I split the trajectory
            this.howManySplitting = ((Long) jsonObject.get("HowManySplitting")).intValue();
            if(this.howManySplitting % 2 != 0){
                throw new Exception("HowManySplitting must be even!");
            }
        }catch (ClassCastException | NullPointerException e) {
            throw new Exception("HowManySplitting is wrong or missing.");
        }

        try {
            // population size
            this.agentPopulationSize = ((Long) jsonObject.get("AgentPopulationSize")).intValue();
        }catch (ClassCastException | NullPointerException e) {
            throw new Exception("AgentPopulationSize is wrong or missing.");
        }
        try {
            // offspring size
            this.agentOffspringSize = ((Long) jsonObject.get("AgentOffspringSize")).intValue();
        }catch (ClassCastException | NullPointerException e) {
            throw new Exception("AgentOffspringSize is wrong or missing.");
        }
        try {
            // alpha
            this.agentAlpha = ((Double) jsonObject.get("AgentAlphaIntermediateRecombination"));
        }catch (ClassCastException | NullPointerException e) {
            throw new Exception("AgentAlpha is wrong or missing.");
        }
        try {
            // timestep
            this.agentTimeSteps = ((Long) jsonObject.get("AgentTimeSteps")).intValue();
        }catch (ClassCastException | NullPointerException e) {
            throw new Exception("AgentTimeSteps is wrong or missing.");
        }

        try {
            // population size
            this.classifierPopulationSize = ((Long) jsonObject.get("ClassifierPopulationSize")).intValue();
        }catch (ClassCastException | NullPointerException e) {
            throw new Exception("ClassifierPopulationSize is wrong or missing.");
        }
        try {
            // offspring size
            this.classifierOffspringSize = ((Long) jsonObject.get("ClassifierOffspringSize")).intValue();
        }catch (ClassCastException | NullPointerException e) {
            throw new Exception("ClassifierOffspringSize is wrong or missing.");
        }
        try {
            // alpha
            this.classifierAlpha = ((Double) jsonObject.get("ClassifierAlphaIntermediateRecombination"));
        }catch (ClassCastException | NullPointerException e) {
            throw new Exception("ClassifierAlpha is wrong or missing.");
        }
        try {
            // classifierTimeSteps
            this.classifierTimeSteps = ((Long) jsonObject.get("ClassifierTimeSteps")).intValue();
        }catch (ClassCastException | NullPointerException e) {
            throw new Exception("ClassifierTimeSteps is wrong or missing.");
        }
        try {
            // hiddenLayersAgent
            this.hiddenLayersAgent = ((Long) jsonObject.get("HiddenLayersAgent")).intValue();
        }catch (ClassCastException | NullPointerException e) {
            throw new Exception("HiddenLayersAgent is wrong or missing.");
        }
        try {
            // hiddenNeuronsAgent
            this.hiddenNeuronsAgent = ((Long) jsonObject.get("HiddenNeuronsAgent")).intValue();
        }catch (ClassCastException | NullPointerException e) {
            throw new Exception("HiddenNeuronsAgent is wrong or missing.");
        }
        try {
            // hiddenNeuronsClassifier
            this.hiddenNeuronsClassifier = ((Long) jsonObject.get("HiddenNeuronsClassifier")).intValue();
        }catch (ClassCastException | NullPointerException e) {
            throw new Exception("HiddenNeuronsClassifier is wrong or missing.");
        }
        try {
            // howManyTrajectories
            this.howManyTrajectories = ((Long) jsonObject.get("HowManyTrajectories")).intValue();
        }catch (ClassCastException | NullPointerException e) {
            throw new Exception("HowManyTrajectories is wrong or missing.");
        }
        try {
            // MaxGenerations
            this.maxGenerations = ((Long) jsonObject.get("MaxGenerations")).intValue();
        }catch (ClassCastException | NullPointerException e) {
            throw new Exception("MaxGenerations is wrong or missing.");
        }

        try {
            // MaxGenerations
            this.name = (String) jsonObject.get("Name");
        }catch (ClassCastException | NullPointerException e) {
            throw new Exception("Name is wrong or missing.");
        }
        try {
            // MaxGenerations
            this.experiment = (String) jsonObject.get("Experiment");
        }catch (ClassCastException | NullPointerException e) {
            throw new Exception("Experiment is wrong or missing.");
        }
        try {
            // MaxGenerations
            this.path = (String) jsonObject.get("Path");
        }catch (ClassCastException | NullPointerException e) {
            throw new Exception("MaxGenerations is wrong or missing.");
        }
        try {
            // dumpPop
            this.dumpPop = (Boolean) jsonObject.get("DumpPopulation");
        }catch (ClassCastException | NullPointerException e) {
            throw new Exception("dumpPop is wrong or missing.");
        }
        try {
            // loadDumpPop
            this.loadDumpPop = (Boolean) jsonObject.get("LoadDumpedPop");
        }catch (ClassCastException | NullPointerException e) {
            throw new Exception("loadDumpPop is wrong or missing.");
        }
        try {
            // seed
            this.seed = ((Long) jsonObject.get("Seed")).intValue();
        }catch (ClassCastException | NullPointerException e) {
            throw new Exception("Seed is wrong or missing.");
        }
        try {
            // seed
            this.mutation = ((Long) jsonObject.get("Mutation")).intValue();
        }catch (ClassCastException | NullPointerException e) {
            throw new Exception("Mutation is wrong or missing.");
        }
        try {
            // trajectoriesTrained
            this.trajectoriesTrained = ((Long) jsonObject.get("TrajectoriesTrained")).intValue();
        }catch (ClassCastException | NullPointerException e) {
            throw new Exception("TrajectoriesTrained is wrong or missing.");
        }
        try {
            // recombination
            this.recombination = (Boolean) jsonObject.get("Recombination");
        }catch (ClassCastException | NullPointerException e) {
            throw new Exception("recombination is wrong or missing.");
        }
        try {
            // timestepEvolveAgentOverClassifier
            this.timestepEvolveAgentOverClassifier = ((Long) jsonObject.get("EvolveAgentMoreThanClassifierTimesteps")).intValue();
        }catch (ClassCastException | NullPointerException e) {
            throw new Exception("EvolveAgentMoreThanClassifierTimesteps is wrong or missing.");
        }
        try {
            // train
            this.train = ((Boolean) jsonObject.get("Train"));
        }catch (ClassCastException | NullPointerException e) {
            throw new Exception("Train is wrong or missing.");
        }

        try {
            // LSTM
            this.LSTM = ((Boolean) jsonObject.get("LSTM"));
        }catch (ClassCastException | NullPointerException e) {
            throw new Exception("LSTM is wrong or missing.");
        }
        try {
            // convolution
            this.convolution = ((Boolean) jsonObject.get("Convolution"));
        }catch (ClassCastException | NullPointerException e) {
            throw new Exception("Convolution is wrong or missing.");
        }
        try {
            // clax
            this.clax = ((Boolean) jsonObject.get("Clax"));
        }catch (ClassCastException | NullPointerException e) {
            throw new Exception("Clax is wrong or missing.");
        }

        //check that only one between LSTM / Convolution / Clax can be true
        int countTrue = this.LSTM ? 1 : 0;
        countTrue += this.convolution ? 1 : 0;
        countTrue += this.clax ? 1 : 0;

        if(this.LSTM) this.valueModel = 0;
        if(this.convolution) this.valueModel = 1;
        if(this.clax) this.valueModel = 2;
        if(countTrue > 1) throw new Exception("More models are set as true, only one is allowed");
        if (this.clax && this.train) throw new Exception("Training is not allowed with clax system");

        try {
            // picturesize
            this.pictureSize = ((Long) jsonObject.get("PictureSize")).intValue();
        }catch (ClassCastException | NullPointerException e) {
            throw new Exception("PictureSize is wrong or missing.");
        }
        try {
            // CheckAlsoPast
            this.checkAlsoPast = ((Boolean) jsonObject.get("CheckAlsoPast"));
        }catch (ClassCastException | NullPointerException e) {
            throw new Exception("CheckAlsoPast is wrong or missing.");
        }
        try {
            // DumpTrajectoryPointAndMeaning
            this.dumpTrajectoryPointAndMeaning = ((Boolean) jsonObject.get("DumpTrajectoryPointAndMeaning"));
        }catch (ClassCastException | NullPointerException e) {
            throw new Exception("DumpTrajectoryPointAndMeaning is wrong or missing.");
        }try {
            // AutomaticCalibration
            this.automaticCalibration = ((Boolean) jsonObject.get("AutomaticCalibration"));
        }catch (ClassCastException | NullPointerException e) {
            throw new Exception("AutomaticCalibration is wrong or missing.");
        }
    }


    /**
     * getter for the number of hidden layer in the agent model
     * @return Integer number
     * @throws Exception  if I am trying to access it before reading it
     */
    public int getHiddenLayersAgent() throws Exception {
        if(this.hiddenLayersAgent == null) throw new Exception("Try to access config file before reading it.");
        return this.hiddenLayersAgent;
    }

    /**
     * getter for the number of hidden nodes in the hidden layers in the agent model
     * @return Integer number
     * @throws Exception  if I am trying to access it before reading it
     */
    public int getHiddenNeuronsAgent() throws Exception {
        if(this.hiddenNeuronsAgent == null) throw new Exception("Try to access config file before reading it.");
        return this.hiddenNeuronsAgent;
    }

    /**
     * getter for the number of hidden nodes in the hidden layer in the classifier model
     * @return Integer number
     * @throws Exception  if I am trying to access it before reading it
     */
    public int getHiddenNeuronsClassifier() throws Exception {
        if(this.hiddenNeuronsClassifier == null) throw new Exception("Try to access config file before reading it.");
        return this.hiddenNeuronsClassifier;
    }

    /**
     * getter for the number of trajectory that I am going to analise
     * @return Integer number
     * @throws Exception  if I am trying to access it before reading it
     */
    public int getHowManyTrajectories() throws Exception {
        if(this.howManyTrajectories == null) throw new Exception("Try to access config file before reading it.");
        return this.howManyTrajectories;
    }

    /**
     * getter for the number of maximum generations allowed
     * @return Integer number
     * @throws Exception  if I am trying to access it before reading it
     */
    public int getMaxGenerations() throws Exception {
        if(this.maxGenerations == null) throw new Exception("Try to access config file before reading it.");
        return this.maxGenerations;
    }

    /**
     * getter for the name of the experiment
     * @return String value
     * @throws Exception  if I am trying to access it before reading it
     */
    public String getName() throws Exception {
        if(this.name == null) throw new Exception("Try to access config file before reading it.");
        return this.name;
    }

    /**
     * getter for the number of the experiment
     * @return String value
     * @throws Exception  if I am trying to access it before reading it
     */
    public String getExperiment() throws Exception {
        if(this.experiment == null) throw new Exception("Try to access config file before reading it.");
        return this.experiment;
    }

    /**
     * getter for the path where to save the files
     * @return string value
     * @throws Exception  if I am trying to access it before reading it
     */
    public String getPath() throws Exception {
        if(this.path == null) throw new Exception("Try to access config file before reading it.");
        return this.path;
    }


    /**
     * Am I saving all the population on a file?
     * @return Boolean Value
     * @throws Exception if I am trying to access it before reading it
     */
    public boolean getDumpPop() throws Exception {
        if(this.dumpPop == null) throw new Exception("Try to access config file before reading it.");
        return this.dumpPop;
    }

    /**
     * Am i loading the population from a file?
     * @return Boolean Value
     * @throws Exception if I am trying to access it before reading it
     */
    public boolean getLoadDumpPop() throws Exception {
        if(this.loadDumpPop == null) throw new Exception("Try to access config file before reading it.");
        return this.loadDumpPop;
    }

    /**
     * Loading the seed for the random initialisation of the population
     * @return Integer Value
     * @throws Exception if I am trying to access it before reading it
     */
    public int getSeed() throws Exception {
        if(this.seed == null) throw new Exception("Try to access config file before reading it.");
        return this.seed;
    }

    /**
     * Return which version of mutation I want to use
     * @return Integer number
     * @throws Exception if I am trying to access it before reading it
     */
    public int getMutation() throws Exception {
        if(this.mutation == null) throw new Exception("Try to access config file before reading it.");
        return this.mutation;
    }


    /**
     * Am i using the LSTM?
     * @return Boolean Value
     * @throws Exception if I am trying to access it before reading it
     */
    public boolean getLSTM() throws Exception {
        if(this.LSTM == null) throw new Exception("Try to access config file before reading it.");
        return this.LSTM;
    }
    /**
     * Am i using the convolution system?
     * @return Boolean Value
     * @throws Exception if I am trying to access it before reading it
     */
    public boolean getConvolution() throws Exception {
        if(this.convolution == null) throw new Exception("Try to access config file before reading it.");
        return this.convolution;
    }
    /**
     * Am i using the classification way?
     * @return Boolean Value
     * @throws Exception if I am trying to access it before reading it
     */
    public boolean getClax() throws Exception {
        if(this.clax == null) throw new Exception("Try to access config file before reading it.");
        return this.clax;
    }

    /**
     * Get the model used as a number
     * @return Integer Value
     * @throws Exception if I am trying to access it before reading it
     */
    public int getValueModel() throws Exception {
        if(this.valueModel == null) throw new Exception("Try to access config file before reading it.");
        return this.valueModel;
    }

    /**
     * Override toString Method in order to print all the setting here
     * @return String containing all the setting
     */
    @Override
    public String toString() {
        return "ReadConfig{" + ",\n" +
                "trajectoriesType=" + trajectoriesType + ",\n" +
                "howManySplitting=" + howManySplitting + ",\n" +
                "howManyTrajectories=" + howManyTrajectories + ",\n" +
                "agentPopulationSize=" + agentPopulationSize + ",\n" +
                "agentOffspringSize=" + agentOffspringSize + ",\n" +
                "agentAlpha=" + agentAlpha + ",\n" +
                "agentTimeSteps=" + agentTimeSteps + ",\n" +
                "classifierPopulationSize=" + classifierPopulationSize + ",\n" +
                "classifierOffspringSize=" + classifierOffspringSize + ",\n" +
                "classifierAlpha=" + classifierAlpha + ",\n" +
                "classifierTimeSteps=" + classifierTimeSteps + ",\n" +
                "maxGenerations=" + maxGenerations + ",\n" +
                "seed=" + seed + ",\n" +
                "mutation=" + mutation + ",\n" +
                "recombination=" + recombination + ",\n" +
                "EvolveAgentMoreThanClassifierTimesteps=" + timestepEvolveAgentOverClassifier + ",\n" +
                "hiddenLayersAgent=" + hiddenLayersAgent + ",\n" +
                "hiddenNeuronsAgent=" + hiddenNeuronsAgent + ",\n" +
                "hiddenNeuronsClassifier=" + hiddenNeuronsClassifier + ",\n" +
                "trajectoriesTrained=" + trajectoriesTrained + ",\n" +
                "train=" + train + ",\n" +
                "pictureSize=" + pictureSize  + ",\n" +
                "CheckAlsoPast=" + checkAlsoPast + ",\n" +
                "AutomaticCalibration=" + automaticCalibration + ",\n" +
                "name='" + name + '\'' + ",\n" +
                "experiment='" + experiment + '\'' + ",\n" +
                "path='" + path + '\'' + ",\n" +
                "dumpPop=" + dumpPop + ",\n" +
                "loadDumpPop=" + loadDumpPop + ",\n" +
                "DumpTrajectoryPointAndMeaning=" + dumpTrajectoryPointAndMeaning + ",\n" +
                "LSTM=" + LSTM + ",\n" +
                "Convolution=" + convolution + ",\n" +
                "Clax=" + clax + ",\n" +
                '}';
    }

    /**
     * Return number of trajectories that are going to be used by the LSTM for its training
     * @return Integer number
     * @throws Exception if I am trying to access it before reading it
     */
    public int getTrajectoriesTrained() throws Exception {
        if(this.trajectoriesTrained == null) throw new Exception("Try to access config file before reading it.");
        return this.trajectoriesTrained;
    }

    /**
     * Am I using recombination?
     * @return Boolean value
     * @throws Exception if I am trying to access it before reading it
     */
    public boolean isRecombination() throws Exception {
        if(this.recombination == null) throw new Exception("Try to access config file before reading it.");
        return this.recombination;
    }

    /**
     * Evolve the Agents more than the classifier for the number of timesteps returned from this method
     * @return Integer number
     * @throws Exception if I am trying to access it before reading it
     */
    public int getTimestepEvolveAgentOverClassifier() throws Exception {
        if(this.timestepEvolveAgentOverClassifier == null) throw new Exception("Try to access config file before reading it.");
        return this.timestepEvolveAgentOverClassifier;
    }

    /**
     * Am I training the network before evolving?
     * @return Boolean number
     * @throws Exception if I am trying to access it before reading it
     */
    public boolean getTrain() throws Exception {
        if(this.train == null) throw new Exception("Try to access config file before reading it.");
        return train;
    }

    /**
     * Get picture size. Since is square only one measure is returned
     * @return Integer value of the size
     * @throws Exception if I am trying to access it before reading it
     */
    public int getPictureSize() throws Exception {
        if(this.pictureSize == null) throw new Exception("Try to access config file before reading it.");
        return pictureSize;
    }

    /**
     * Get the flag if it is checking also the past of the trajectory
     * @return Boolean value
     * @throws Exception if I am trying to access it before reading it
     */
    public boolean getCheckAlsoPast() throws Exception {
        if(this.checkAlsoPast == null) throw new Exception("Try to access config file before reading it.");
        return checkAlsoPast;
    }

    /**
     * Getter for dumping the file with trajectory and the point generated by the network
     * @return Boolean value
     * @throws Exception if I am trying to access it before reading it
     */
    public boolean getDumpTrajectoryPointAndMeaning() throws Exception {
        if(this.dumpTrajectoryPointAndMeaning == null) throw new Exception("Try to access config file before reading it.");
        return this.dumpTrajectoryPointAndMeaning;
    }

    /**
     * Getter for the automatic calibration of the two population
     * @return Boolean value
     * @throws Exception if I am trying to access it before reading it
     */
    public boolean getAutomaticCalibration() throws Exception {
        if(this.automaticCalibration == null) throw new Exception("Try to access config file before reading it.");
        return this.automaticCalibration;
    }


    /**
     * Static class offering all the info read from file
     */
    public static class Configurations{
        private static ReadConfig config;
        public static int LSTM = 0;
        public static int Convolution = 1;
        public static int Clax = 2;

        /**
         * Initialise and read the settings from file
         * @throws Exception if something goes wrong during the reading procedure
         */
        public Configurations() throws Exception {
            config = new ReadConfig();
            config.readFile();
        }

        /**
         * Return seed setting
         * @return integer value
         * @throws Exception if I am trying to access it before reading it
         */
        public static int getSeed() throws Exception {
            return config.getSeed();
        }

        /**
         * Return if I want to save the population on file
         * @return Boolean Value
         * @throws Exception if I am trying to access it before reading it
         */
        public static boolean getDumpPop() throws Exception {
            return config.getDumpPop();
        }

        /**
         * Return the path of the experiment
         * @return String value
         * @throws Exception if I am trying to access it before reading it
         */
        public static String getPath() throws Exception {
            return config.getPath();
        }

        /**
         * Return the number of the experiment
         * @return String value
         * @throws Exception if I am trying to access it before reading it
         */
        public static String getExperiment() throws Exception {
            return config.getExperiment();
        }

        /**
         *  Return the name of the experiment
         * @return String value
         * @throws Exception if I am trying to access it before reading it
         */
        public static String getName() throws Exception {
            return config.getName();
        }

        /**
         * Return the number of trajectories to test
         * @return int value
         * @throws Exception if I am trying to access it before reading it
         */
        public static int getHowManyTrajectories() throws Exception {
            return config.getHowManyTrajectories();
        }

        /**
         * Return number of the classifier's hidden neurons
         * @return int value
         * @throws Exception if I am trying to access it before reading it
         */
        public static int getHiddenNeuronsClassifier() throws Exception {
            return config.getHiddenNeuronsClassifier();
        }

        /**
         * Return number of the agent's hidden neurons
         * @return int value
         * @throws Exception if I am trying to access it before reading it
         */
        public static int getHiddenNeuronsAgent() throws Exception {
            return config.getHiddenNeuronsAgent();
        }

        /**
         * Return number of the agent's hidden layers number
         * @return int value
         * @throws Exception if I am trying to access it before reading it
         */
        public static int getHiddenLayersAgent() throws Exception {
            return config.getHiddenLayersAgent();
        }

        /**
         * Return number of the classifier's timesteps
         * @return int value
         * @throws Exception if I am trying to access it before reading it
         */
        public static int getClassifierTimeSteps() throws Exception {
            return config.getClassifierTimeSteps();
        }

        /**
         * Return number of the classifier's alpha value
         * @return double value
         * @throws Exception if I am trying to access it before reading it
         */
        public static double getClassifierAlpha() throws Exception {
            return config.getClassifierAlpha();
        }

        /**
         * Return number of the classifier's population size
         * @return int value
         * @throws Exception if I am trying to access it before reading it
         */
        public static int getClassifierPopulationSize() throws Exception {
            return config.getClassifierPopulationSize();
        }

        /**
         * Return number of the agent's time steps
         * @return int value
         * @throws Exception if I am trying to access it before reading it
         */
        public static int getAgentTimeSteps() throws Exception {
            return config.getAgentTimeSteps();
        }

        /**
         * Return number of the agent's alpha value
         * @return double value
         * @throws Exception if I am trying to access it before reading it
         */
        public static double getAgentAlpha() throws Exception {
            return config.getAgentAlpha();
        }

        /**
         * Return number of the agent's population size
         * @return int value
         * @throws Exception if I am trying to access it before reading it
         */
        public static int getAgentPopulationSize() throws Exception {
            return config.getAgentPopulationSize();
        }

        /**
         * Return number of the agent's offspring size
         * @return int value
         * @throws Exception if I am trying to access it before reading it
         */
        public static int getAgentOffspringSize() throws Exception {
            return config.getAgentOffspringSize();
        }

        /**
         * Return how many time split the trajectory
         * @return int value
         * @throws Exception if I am trying to access it before reading it
         */
        public static int getHowManySplitting() throws Exception {
            return config.getHowManySplitting();
        }

        /**
         * Return the trajectory's type
         * @return int value
         * @throws Exception if I am trying to access it before reading it
         */
        public static int getTrajectoriesType() throws Exception {
            return config.getTrajectoriesType();
        }

        /**
         * Return number of the classifier's offspring size
         * @return int value
         * @throws Exception if I am trying to access it before reading it
         */
        public static int getClassifierOffspringSize() throws Exception {
            return config.getClassifierOffspringSize();
        }

        /**
         * Return max number of generation
         * @return int value
         * @throws Exception if I am trying to access it before reading it
         */
        public static int getMaxGenerations() throws Exception {
            return config.getMaxGenerations();
        }

        /**
         * Return if I want to load the saved population
         * @return boolean value
         * @throws Exception if I am trying to access it before reading it
         */
        public static boolean getLoadDumpPop() throws Exception {
            return config.getLoadDumpPop();
        }

        /**
         * Return the class
         * @return Class reference
         */
        public static ReadConfig getConfig(){
            return config;
        }

        /**
         * Return which kind of mutation I want to use now
         * @return int value
         * @throws Exception if I am trying to access it before reading it
         */
        public static int getMutation() throws Exception {
            return config.getMutation();
        }
        /**
         * Return number of trajectories that are going to be used by the LSTM for its training
         * @return int number
         * @throws Exception if I am trying to access it before reading it
         */
        public static int getTrajectoriesTrained() throws Exception {
            return config.getTrajectoriesTrained();
        }

        /**
         * Am I using recombination?
         * @return boolean value
         * @throws Exception if I am trying to access it before reading it
         */
        public static boolean isRecombination() throws Exception {
            return config.isRecombination();
        }

        /**
         * Evolve the Agents more than the classifier for the number of timesteps returned from this method
         * @return int value
         * @throws Exception if I am trying to access it before reading it
         */
        public static int getTimestepEvolveAgentOverClassifier() throws Exception {
            return config.getTimestepEvolveAgentOverClassifier();
        }

        /**
         * Am I training the network before evolving?
         * @return boolean number
         * @throws Exception if I am trying to access it before reading it
         */
        public static boolean getTrain() throws Exception {
            return config.getTrain();
        }

        /**
         * Am I using the LSTM?
         * @return boolean number
         * @throws Exception if I am trying to access it before reading it
         */
        public static boolean getLSTM() throws Exception {
            return config.getLSTM();
        }
        /**
         * Am I sing the convolutionary network?
         * @return boolean number
         * @throws Exception if I am trying to access it before reading it
         */
        public static boolean getConvolution() throws Exception {
            return config.getConvolution();
        }
        /**
         * Am I using the classificator way?
         * @return boolean number
         * @throws Exception if I am trying to access it before reading it
         */
        public static boolean getClax() throws Exception {
            return config.getClax();
        }

        /**
         * Get the model used as a number
         * @return int Value
         * @throws Exception if I am trying to access it before reading it
         */
        public static int getValueModel() throws Exception {
            return config.getValueModel();
        }

        /**
         * Get picture size. Since is square only one measure is returned
         * @return int value of the size
         * @throws Exception if I am trying to access it before reading it
         */
        public static int getPictureSize() throws Exception {
            return config.getPictureSize();
        }

        /**
         * Get the flag if it is checking also the past of the trajectory
         * @return boolean value
         * @throws Exception if I am trying to access it before reading it
         */
        public static boolean getCheckAlsoPast() throws Exception {
            return config.getCheckAlsoPast();
        }

        /**
         * Getter for dumping the file with trajectory and the point generated by the network
         * @return boolean value
         * @throws Exception if I am trying to access it before reading it
         */
        public static boolean getDumpTrajectoryPointAndMeaning() throws Exception {
            return config.getDumpTrajectoryPointAndMeaning();
        }

        /**
         * Getter for the automatic calibration of the two population
         * @return Boolean value
         * @throws Exception if I am trying to access it before reading it
         */
        public static boolean getAutomaticCalibration() throws Exception {
            return config.getAutomaticCalibration();
        }
    }

}

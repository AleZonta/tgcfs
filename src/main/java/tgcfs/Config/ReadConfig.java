package tgcfs.Config;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import tgcfs.Classifiers.InputNetwork;
import tgcfs.Classifiers.OutputNetwork;

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
    private Integer tournamentSizeAgents;
    private Integer tournamentSizeClassifiers;
    private Double stepSizeAgents;
    private Double stepSizeClassifiers;
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

    private Integer valueClassifier;
    private Boolean LSTMClassifier;
    private Boolean ENN;
    private Boolean NN;

    private Integer pictureSize;

    private Boolean checkAlsoPast;
    private Boolean automaticCalibration;

    private Integer numberOfTimestepConsidered;

    //drift
    private Boolean usingReducedVirulenceMethodOnAgents;
    private Double virulenceAgents;
    private Boolean usingReducedVirulenceMethodOnClassifiers;
    private Double virulenceClassifiers;

    private Integer automaticEvolutionDisengagementSystem;
    private Integer measureUsedForAutomaticDisengagement;
    private Integer populationWillUseTheAutomaticDisengagementSystem;

    private Integer differentSelectionForClassifiers;
    private Integer differentSelectionForAgent;
    private Integer keepBestNElement;
    private Integer howManyAmIChangingBetweenGeneration;
    public static boolean isETH;
    public static boolean tryNNclassifier = true;
    private Boolean score;

    private Double maxSpeed;

    private Boolean conversionWithGraph;
    private String uncorrelatedMutationStep;

    private Boolean hallOfFame;
    private Integer hallOfFameMemory;
    private Integer hallOfFameSample;

    private Integer fitnessFunction;
    private Boolean timeAsInput;

    private Integer debugLevel;
    private Boolean incrementalLearningPoints;
    private Integer howManyGenBeforeNewPoint;

    private Integer moreTimeAhead;

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
        this.tournamentSizeAgents = null;
        this.tournamentSizeClassifiers = null;
        this.mutation = null;
        this.stepSizeAgents = null;
        this.stepSizeClassifiers = null;
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
        this.valueClassifier = null;
        this.LSTMClassifier = null;
        this.ENN = null;
        this.NN = null;

        this.pictureSize = null;
        this.checkAlsoPast = null;
        this.dumpTrajectoryPointAndMeaning = null;

        this.automaticCalibration = null;

        this.numberOfTimestepConsidered = null;

        this.usingReducedVirulenceMethodOnAgents = null;
        this.usingReducedVirulenceMethodOnClassifiers = null;
        this.virulenceAgents = null;
        this.virulenceClassifiers = null;

        this.automaticEvolutionDisengagementSystem = null;
        this.measureUsedForAutomaticDisengagement = null;
        this.populationWillUseTheAutomaticDisengagementSystem = null;

        this.differentSelectionForClassifiers = null;
        this.differentSelectionForAgent = null;
        this.keepBestNElement = null;
        this.howManyAmIChangingBetweenGeneration = null;

        this.maxSpeed = null;
        this.conversionWithGraph = null;
        this.score = null;
        this.uncorrelatedMutationStep = null;

        this.hallOfFame = null;
        this.hallOfFameMemory = null;
        this.hallOfFameSample = null;

        this.fitnessFunction = null;
        this.timeAsInput = null;
        isETH = false;

        this.debugLevel = null;
        this.incrementalLearningPoints = null;
        this.howManyGenBeforeNewPoint = null;

        this.moreTimeAhead = null;
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
            // "0 idsa, 1 geolife, 2 both, 3 idsaJSON, 4 geolifeJSON, 5 ETH"
            this.trajectoriesType = ((Long) jsonObject.get("trajectoriesType")).intValue();
            if(this.trajectoriesType == 5) isETH = true;
        }catch (ClassCastException | NullPointerException e) {
            throw new Exception("TrajectoriesType is wrong or missing.");
        }
        try {
            // time I split the trajectory
            this.howManySplitting = ((Long) jsonObject.get("HowManySplitting")).intValue();
            if(this.howManySplitting % 2 != 0){
                throw new Exception("HowManySplitting must be even!");
            }
            if(this.trajectoriesType == 5 && this.howManySplitting != 0) {
                throw new Exception("With ETH the number of splitting must be 0!");
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
        try {
            // LSTMClassifier
            this.LSTMClassifier = ((Boolean) jsonObject.get("LSTMClassifier"));
        }catch (ClassCastException | NullPointerException e) {
            throw new Exception("LSTMClassifier is wrong or missing.");
        }
        try {
            // Elman neural network
            this.ENN = ((Boolean) jsonObject.get("ENN"));
        }catch (ClassCastException | NullPointerException e) {
            throw new Exception("ENN is wrong or missing.");
        }
        try {
            // neural network
            this.NN = ((Boolean) jsonObject.get("NN"));
        }catch (ClassCastException | NullPointerException e) {
            throw new Exception("NN is wrong or missing.");
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


        //check that only one between LSTM / Convolution / Clax can be true
        int countTrueClassifier = this.LSTMClassifier ? 1 : 0;
        countTrueClassifier += this.ENN ? 1 : 0;
        countTrueClassifier += this.NN ? 1 : 0;

        if(this.ENN) {
            this.valueClassifier = 0;
            tryNNclassifier = false;
        }
        if(this.LSTMClassifier) {
            this.valueClassifier = 1;
            //set a differnet output size if I am using the LSTM as an output
            OutputNetwork.setOutputSize(2);
            tryNNclassifier = false;
        }
        if(this.NN) {
            this.valueClassifier = 2;
            InputNetwork.inputSize = 4;
            tryNNclassifier = true;
        }

        if(countTrueClassifier > 1) throw new Exception("More models are set as true, only one is allowed");

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
        try {
            // TimestepsOfTheRealTrajectory
            this.numberOfTimestepConsidered = ((Long) jsonObject.get("TimestepsOfTheRealTrajectory")).intValue();
        }catch (ClassCastException | NullPointerException e) {
            throw new Exception("TimestepsOfTheRealTrajectory is wrong or missing.");
        }try {
            // tournamentSizeAgents
            this.tournamentSizeAgents = ((Long) jsonObject.get("TournamentSizeAgents")).intValue();
        }catch (ClassCastException | NullPointerException e) {
            throw new Exception("TournamentSize is wrong or missing.");
        }try {
            // tournamentSizeClassifiers
            this.tournamentSizeClassifiers = ((Long) jsonObject.get("TournamentSizeClassifiers")).intValue();
        }catch (ClassCastException | NullPointerException e) {
            throw new Exception("TournamentSize is wrong or missing.");
        }try {
            // tournamentSize
            this.stepSizeClassifiers = ((Double) jsonObject.get("StepSizeClassifiers"));
        }catch (ClassCastException | NullPointerException e) {
            throw new Exception("StepSizeClassifiers is wrong or missing.");
        }try {
            // tournamentSize
            this.stepSizeAgents = ((Double) jsonObject.get("StepSizeAgents"));
        }catch (ClassCastException | NullPointerException e) {
            throw new Exception("StepSizeAgents is wrong or missing.");
        }try {
            // virulence
            this.virulenceAgents = ((Double) jsonObject.get("VirulenceAgents"));
        }catch (ClassCastException | NullPointerException e) {
            throw new Exception("VirulenceAgents is wrong or missing.");
        }try {
            // AutomaticCalibration
            this.usingReducedVirulenceMethodOnAgents = ((Boolean) jsonObject.get("UsingReducedVirulenceMethodOnAgents"));
        }catch (ClassCastException | NullPointerException e) {
            throw new Exception("UsingReducedVirulenceMethodOnAgents is wrong or missing.");
        }try {
            // virulence
            this.virulenceClassifiers = ((Double) jsonObject.get("VirulenceClassifiers"));
        }catch (ClassCastException | NullPointerException e) {
            throw new Exception("VirulenceClassifiers is wrong or missing.");
        }try {
            // AutomaticCalibration
            this.usingReducedVirulenceMethodOnClassifiers = ((Boolean) jsonObject.get("UsingReducedVirulenceMethodOnClassifiers"));
        }catch (ClassCastException | NullPointerException e) {
            throw new Exception("UsingReducedVirulenceMethodOnClassifiers is wrong or missing.");
        }try {
            // automaticEvolutionDisengagementSystem
            this.automaticEvolutionDisengagementSystem = ((Long) jsonObject.get("AutomaticEvolutionDisengagement")).intValue();
        }catch (ClassCastException | NullPointerException e) {
            throw new Exception("AutomaticEvolutionDisengagementn is wrong or missing.");
        }try {
            // measureUsedForAutomaticDisengagement
            this.measureUsedForAutomaticDisengagement = ((Long) jsonObject.get("MeasureUsedForAutomaticDisengagement")).intValue();
        }catch (ClassCastException | NullPointerException e) {
            throw new Exception("MeasureUsedForAutomaticDisengagement is wrong or missing.");
        }try {
            // populationWillUseTheAutomaticDisengagementSystem
            this.populationWillUseTheAutomaticDisengagementSystem = ((Long) jsonObject.get("PopulationWithAutomaticDisengagement")).intValue();
        }catch (ClassCastException | NullPointerException e) {
            throw new Exception("PopulationWithAutomaticDisengagement is wrong or missing.");
        }try {
            // DifferentSelectionForClassifiers
            double value = (Double) jsonObject.get("DifferentSelectionForClassifiers");
            double fractionalPart = value % 1;
            double integralPart = value - fractionalPart;
            fractionalPart *= 10 ;
            fractionalPart += 0.1 ;

            this.differentSelectionForClassifiers = (int) fractionalPart;
            this.differentSelectionForAgent = (int) integralPart;
        }catch (ClassCastException | NullPointerException e) {
            throw new Exception("DifferentSelectionForClassifiers is wrong or missing.");
        }try {
            // HowManyAmIChangingBetweenGeneration
            this.howManyAmIChangingBetweenGeneration = ((Long) jsonObject.get("HowManyAmIChangingBetweenGeneration")).intValue();
        }catch (ClassCastException | NullPointerException e) {
            throw new Exception("HowManyAmIChangingBetweenGeneration is wrong or missing.");
        }

        if(this.automaticEvolutionDisengagementSystem == 1){
            if(this.usingReducedVirulenceMethodOnClassifiers || this.usingReducedVirulenceMethodOnAgents){
                throw new Exception("If using the automatic evolution of step size, the usage of the virulence method is not allowed.");
            }
        }

        try {
            // MaxSpeed
            this.maxSpeed = ((Double) jsonObject.get("MaxSpeed"));
        }catch (ClassCastException | NullPointerException e) {
            throw new Exception("MaxSpeed is wrong or missing.");
        }
        try {
            // AutomaticCalibration
            this.score = ((Boolean) jsonObject.get("Score"));
        }catch (ClassCastException | NullPointerException e) {
            throw new Exception("Score is wrong or missing.");
        }


        //ConversionOutputWithGraph
        try {
            // AutomaticCalibration
            this.conversionWithGraph = ((Boolean) jsonObject.get("ConversionOutputWithGraph"));
        }catch (ClassCastException | NullPointerException e) {
            throw new Exception("ConversionOutputWithGraph is wrong or missing.");
        }try {
            // KeepBestNElement
            this.keepBestNElement = ((Long) jsonObject.get("KeepBestNElement")).intValue();
        }catch (ClassCastException | NullPointerException e) {
            throw new Exception("KeepBestNElement is wrong or missing.");
        }

        try {
            // UncorrelatedMutationStep
            this.uncorrelatedMutationStep = (String) jsonObject.get("UncorrelatedMutationStep");
        }catch (ClassCastException | NullPointerException e) {
            throw new Exception("UncorrelatedMutationStep is wrong or missing.");
        }

        try {
            // this.hallOfFame
            this.hallOfFame = ((Boolean) jsonObject.get("HallOfFame"));
        }catch (ClassCastException | NullPointerException e) {
            throw new Exception("HallOfFame is wrong or missing.");
        }
        try {
            // this.hallOfFameMemory
            this.hallOfFameMemory = ((Long) jsonObject.get("HallOfFameMemory")).intValue();
        }catch (ClassCastException | NullPointerException e) {
            throw new Exception("HallOfFameMemory is wrong or missing.");
        }try {
            // this.hallOfFameSample
            this.hallOfFameSample = ((Long) jsonObject.get("HallOfFameSample")).intValue();
        }catch (ClassCastException | NullPointerException e) {
            throw new Exception("HallOfFameSample is wrong or missing.");
        }

        try {
            // this.fitnessFunction
            this.fitnessFunction = ((Long) jsonObject.get("FitnessFunction")).intValue();
        }catch (ClassCastException | NullPointerException e) {
            throw new Exception("FitnessFunction is wrong or missing.");
        }try {
            // this.timeAsInput
            this.timeAsInput = ((Boolean) jsonObject.get("TimeAsInput"));

            if(this.trajectoriesType == 1 || this.trajectoriesType == 4){
                this.timeAsInput = true;
            }else{
                this.timeAsInput = false;
            }

        }catch (ClassCastException | NullPointerException e) {
            throw new Exception("TimeAsInput is wrong or missing.");
        }

        try {
            // debugLevel
            this.debugLevel = ((Long) jsonObject.get("LevelDebug")).intValue();
        }catch (ClassCastException | NullPointerException e) {
            throw new Exception("LevelDebug is wrong or missing.");
        }

        try {
            // this.incrementalLearningPoints
            this.incrementalLearningPoints = ((Boolean) jsonObject.get("IncrementalLearningPoints"));
        }catch (ClassCastException | NullPointerException e) {
            throw new Exception("IncrementalLearningPoints is wrong or missing.");
        }
        try {
            // howManyGenBeforeNewPoint
            this.howManyGenBeforeNewPoint = ((Long) jsonObject.get("HowManyGenBeforeNewPoint")).intValue();
        }catch (ClassCastException | NullPointerException e) {
            throw new Exception("HowManyGenBeforeNewPoint is wrong or missing.");
        }

        try {
            // MoreTimeAhead
            this.moreTimeAhead = ((Long) jsonObject.get("MoreTimeAhead")).intValue();
            if (this.moreTimeAhead <= 0) throw new Exception("MoreTimeAhead can only be positive");
        }catch (ClassCastException | NullPointerException e) {
            this.moreTimeAhead = 1;
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
                "tgcfs git-sha-1=" + PropertiesFileReader.getGitSha1() + ",\n" +
                "trajectoriesType=" + trajectoriesType + ",\n" +
                "howManySplitting=" + howManySplitting + ",\n" +
                "howManyTrajectories=" + howManyTrajectories + ",\n" +
                "numberOfTimestepConsidered=" + numberOfTimestepConsidered + ",\n" +
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
                "tournamentSizeAgents=" + tournamentSizeAgents + ",\n" +
                "tournamentSizeClassifiers=" + tournamentSizeClassifiers + ",\n" +
                "mutation=" + mutation + ",\n" +
                "stepSizeAgents=" + stepSizeAgents + ",\n" +
                "stepSizeClassifiers=" + stepSizeClassifiers + ",\n" +
                "recombination=" + recombination + ",\n" +
                "EvolveAgentMoreThanClassifierTimesteps=" + timestepEvolveAgentOverClassifier + ",\n" +
                "hiddenLayersAgent=" + hiddenLayersAgent + ",\n" +
                "hiddenNeuronsAgent=" + hiddenNeuronsAgent + ",\n" +
                "hiddenNeuronsClassifier=" + hiddenNeuronsClassifier + ",\n" +
                "trajectoriesTrained=" + trajectoriesTrained + ",\n" +
                "HowManyAmIKeepingBetweenGeneration=" + howManyAmIChangingBetweenGeneration + ",\n" +
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
                "LSTMClassifier=" + LSTMClassifier + ",\n" +
                "ENN=" + ENN + ",\n" +
                "NN=" + NN + ",\n" +
                "VirulenceAgents=" + virulenceAgents + ",\n" +
                "VirulenceClassifiers=" + virulenceClassifiers + ",\n" +
                "UsingReducedVirulenceMethodOnAgents=" + usingReducedVirulenceMethodOnAgents + ",\n" +
                "UsingReducedVirulenceMethodOnClassifiers=" + usingReducedVirulenceMethodOnClassifiers + ",\n" +
                "AutomaticEvolutionDisengagement=" + automaticEvolutionDisengagementSystem + ",\n" +
                "MeasureUsedForAutomaticDisengagement=" + measureUsedForAutomaticDisengagement + ",\n" +
                "PopulationWithAutomaticDisengagement=" + populationWillUseTheAutomaticDisengagementSystem + ",\n" +
                "DifferentSelectionForClassifiers=" + differentSelectionForClassifiers + ",\n" +
                "DifferentSelectionForAgent=" + differentSelectionForAgent + ",\n" +
                "MaxSpeed=" + maxSpeed + ",\n" +
                "conversionWithGraph=" + conversionWithGraph + ",\n" +
                "Score=" + score + ",\n" +
                "KeepBestNElement=" + keepBestNElement + ",\n" +
                "UncorrelatedMutationStep=" + uncorrelatedMutationStep + ",\n" +
                "HallOfFame=" + hallOfFame + ",\n" +
                "HallOfFameMemory=" + hallOfFameMemory + ",\n" +
                "HallOfFameSample=" + hallOfFameSample + ",\n" +
                "FitnessFunction=" + fitnessFunction + ",\n" +
                "TimeAsInput=" + timeAsInput + ",\n" +
                "LevelDebug=" + debugLevel + ",\n" +
                "IncrementalLearningPoints=" + incrementalLearningPoints + ",\n" +
                "HowManyGenBeforeNewPoint=" + howManyGenBeforeNewPoint + ",\n" +
                "MoreTimeAhead=" + moreTimeAhead + ",\n" +
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
     * Getter for the number of timestep considered in the trajectory
     * @return int number with the number of timesteps
     * @throws Exception  if I am trying to access it before reading it
     */
    public int getNumberOfTimestepConsidered() throws Exception {
        if(this.numberOfTimestepConsidered == null) throw new Exception("Try to access config file before reading it.");
        return numberOfTimestepConsidered;
    }

    /**
     * Getter for the agent tournament size
     * @return integer value for tournament size
     * @throws Exception  if I am trying to access it before reading it
     */
    public int getTournamentSizeAgents() throws Exception {
        if(this.tournamentSizeAgents == null) throw new Exception("Try to access config file before reading it.");
        return this.tournamentSizeAgents;
    }

    /**
     * Getter for the classifier tournament size
     * @return integer value for tournament size
     * @throws Exception  if I am trying to access it before reading it
     */
    public int getTournamentSizeClassifiers() throws Exception {
        if(this.tournamentSizeClassifiers == null) throw new Exception("Try to access config file before reading it.");
        return this.tournamentSizeClassifiers;
    }

    /**
     * Getter for the step size
     * @return double value for tournament size
     * @throws Exception  if I am trying to access it before reading it
     */
    public double getStepSizeAgents() throws Exception {
        if(this.stepSizeAgents == null) throw new Exception("Try to access config file before reading it.");
        return this.stepSizeAgents;
    }

    /**
     * Getter for the step size
     * @return double value for tournament size
     * @throws Exception  if I am trying to access it before reading it
     */
    public double getStepSizeClassifiers() throws Exception {
        if(this.stepSizeClassifiers == null) throw new Exception("Try to access config file before reading it.");
        return this.stepSizeClassifiers;
    }

    /**
     * Getter if I am using the reduced virulence way
     * @return boolean value
     * @throws Exception  if I am trying to access it before reading it
     */
    public boolean getUsingReducedVirulenceMethodOnAgents() throws Exception {
        if(this.usingReducedVirulenceMethodOnAgents == null) throw new Exception("Try to access config file before reading it.");
        return this.usingReducedVirulenceMethodOnAgents;
    }

    /**
     * Getter of the value of the virulence
     * @return double value
     * @throws Exception  if I am trying to access it before reading it
     */
    public double getVirulenceAgents() throws Exception {
        if(this.virulenceAgents == null) throw new Exception("Try to access config file before reading it.");
        return this.virulenceAgents;
    }

    /**
     * Getter if I am using the reduced virulence way
     * @return boolean value
     * @throws Exception  if I am trying to access it before reading it
     */
    public boolean getUsingReducedVirulenceMethodOnClassifiers() throws Exception {
        if(this.usingReducedVirulenceMethodOnClassifiers == null) throw new Exception("Try to access config file before reading it.");
        return this.usingReducedVirulenceMethodOnClassifiers;
    }

    /**
     * Getter of the value of the virulence
     * @return double value
     * @throws Exception  if I am trying to access it before reading it
     */
    public double getVirulenceClassifiers() throws Exception {
        if(this.virulenceClassifiers == null) throw new Exception("Try to access config file before reading it.");
        return this.virulenceClassifiers;
    }

    /**
     * Getter if I am using the LSTM also for the classifier
     * @return boolena value
     * @throws Exception  if I am trying to access it before reading it
     */
    public boolean getLSTMClassifier() throws Exception {
        if(this.LSTMClassifier == null) throw new Exception("Try to access config file before reading it.");
        return this.LSTMClassifier;
    }

    /**
     * Getter if I am using the ENN for the classifier
     * @return boolena value
     * @throws Exception  if I am trying to access it before reading it
     */
    public boolean getENN() throws Exception {
        if(this.ENN == null) throw new Exception("Try to access config file before reading it.");
        return this.ENN;
    }

    /**
     * Get the model used as a number
     * @return int value
     * @throws Exception  if I am trying to access it before reading it
     */
    public int getValueClassifier() throws Exception {
        if(this.valueClassifier == null) throw new Exception("Try to access config file before reading it.");
        return this.valueClassifier;
    }

    /**
     * Getter for the selector if using the automatic disengagement system
     * @return int value
     * @throws Exception  if I am trying to access it before reading it
     */
    public int getAutomaticEvolutionDisengagementSystem() throws Exception {
        if(this.automaticEvolutionDisengagementSystem == null) throw new Exception("Try to access config file before reading it.");
        return automaticEvolutionDisengagementSystem;
    }

    /**
     * Getter for the measure used in the automatic system
     * @return int value
     * @throws Exception  if I am trying to access it before reading it
     */
    public int getMeasureUsedForAutomaticDisengagement() throws Exception {
        if(this.measureUsedForAutomaticDisengagement == null) throw new Exception("Try to access config file before reading it.");
        return measureUsedForAutomaticDisengagement;
    }

    /**
     * Getter for the population that will use the automatic disengagement system
     * @return int value
     * @throws Exception  if I am trying to access it before reading it
     */
    public int getPopulationWillUseTheAutomaticDisengagementSystem() throws Exception {
        if(this.populationWillUseTheAutomaticDisengagementSystem == null) throw new Exception("Try to access config file before reading it.");
        return populationWillUseTheAutomaticDisengagementSystem;
    }

    /**
     * Getter for the property if the classifiers has a different selection method
     * @return int
     * @throws Exception if I am trying to access it before reading it
     */
    public int getDifferentSelectionForClassifiers() throws Exception {
        if(this.differentSelectionForClassifiers == null) throw new Exception("Try to access config file before reading it.");
        return differentSelectionForClassifiers;
    }
    /**
     * Getter for the property if the agent has a different selection method
     * @return int
     * @throws Exception if I am trying to access it before reading it
     */
    public int getDifferentSelectionForAgent() throws Exception {
        if(this.differentSelectionForAgent == null) throw new Exception("Try to access config file before reading it.");
        return differentSelectionForAgent;
    }

    /**
     * Getter for the percentage of trajectory kept between generations
     * @return int number
     * @throws Exception if I am trying to access it before reading it
     */
    public int getHowManyAmIChangingBetweenGeneration() throws Exception {
        if(this.howManyAmIChangingBetweenGeneration == null) throw new Exception("Try to access config file before reading it.");
        return this.howManyAmIChangingBetweenGeneration;
    }

    /**
     * Getter of the value of the maxSpeed
     * @return double value
     * @throws Exception  if I am trying to access it before reading it
     */
    public double getMaxSpeed() throws Exception {
        if(this.maxSpeed == null) throw new Exception("Try to access config file before reading it.");
        return this.maxSpeed;
    }


    /**
     * Getter if I am using the conversion with graph
     * @return boolena value
     * @throws Exception  if I am trying to access it before reading it
     */
    public boolean getConversionWithGraph() throws Exception {
        if(this.conversionWithGraph == null) throw new Exception("Try to access config file before reading it.");
        return this.conversionWithGraph;
    }

    /**
     * Getter if I am using the score
     * @return boolean value
     * @throws Exception  if I am trying to access it before reading it
     */
    public boolean getScore() throws Exception {
        if(this.score == null) throw new Exception("Try to access config file before reading it.");
        return this.score;
    }

    /**
     * Getter for how many I want to keep with specific survival selection
     * @return int number
     * @throws Exception  if I am trying to access it before reading it
     */
    public int getKeepBestNElement() throws Exception {
        if(this.keepBestNElement == null) throw new Exception("Try to access config file before reading it.");
        return this.keepBestNElement;
    }

    /**
     * Getter for how many timesteps the uncorrelated mutation has
     * @return string value
     * @throws Exception if I am trying to access it before reading it
     */
    public String getUncorrelatedMutationStep() throws Exception {
        if(this.uncorrelatedMutationStep == null) throw new Exception("Try to access config file before reading it.");
        return this.uncorrelatedMutationStep;
    }

    /**
     * Getter for the hall of fame, If I am using it
     * @return boolean value
     * @throws Exception if I am trying to access it before reading it
     */
    public boolean getHallOfFame() throws Exception {
        if(this.uncorrelatedMutationStep == null) throw new Exception("Try to access config file before reading it.");
        return hallOfFame;
    }

    /**
     * Getter for the hall of fame memory
     * @return int value
     * @throws Exception if I am trying to access it before reading it
     */
    public int getHallOfFameMemory() throws Exception {
        if(this.uncorrelatedMutationStep == null) throw new Exception("Try to access config file before reading it.");
        return hallOfFameMemory;
    }

    /**
     * Getter for the hall of fame sample size
     * @return int value
     * @throws Exception if I am trying to access it before reading it
     */
    public int getHallOfFameSample() throws Exception {
        if(this.uncorrelatedMutationStep == null) throw new Exception("Try to access config file before reading it.");
        return hallOfFameSample;
    }

    /**
     * Getter for the type of Fitness Function I am using
     * @return int value
     * @throws Exception if I am trying to access it before reading it
     */
    public int getFitnessFunction() throws Exception {
        if(this.fitnessFunction == null) throw new Exception("Try to access config file before reading it.");
        return this.fitnessFunction;
    }

    /**
     * Getter if I am using the time as Input for the generator
     * @return boolean value
     * @throws Exception if I am trying to access it before reading it
     */
    public boolean getTimeAsInput() throws Exception {
        if(this.timeAsInput == null) throw new Exception("Try to access config file before reading it.");
        return this.timeAsInput;
    }

    /**
     * Getter if I am using the simple NN as an output
     * @return boolean value
     * @throws Exception if I am trying to access it before reading it
     */
    public boolean getNN() throws Exception {
        if(this.NN == null) throw new Exception("Try to access config file before reading it.");
        return this.NN;
    }

    /**
     * Getter if I am using the simple NN as an output
     * @return boolean value
     * @throws Exception if I am trying to access it before reading it
     */
    public int getDebugLevel() throws Exception {
        if(this.debugLevel == null) throw new Exception("Try to access config file before reading it.");
        return this.debugLevel;
    }

    /**
     * Setter for the number of agent timesteps
     * used only if I am doing the incremental learning
     * @param agentTimeSteps number of timesteps
     */
    public void setAgentTimeSteps(Integer agentTimeSteps) {
        this.agentTimeSteps = agentTimeSteps;
    }

    /**
     * Getter for the value if I am using incrementing learning for the points
     * @return boolean value
     * @throws Exception if I am trying to access it before reading it
     */
    public boolean getIncrementalLearningPoints() throws Exception {
        if(this.incrementalLearningPoints == null) throw new Exception("Try to access config file before reading it.");
        return this.incrementalLearningPoints;
    }

    /**
     * Getter for after how many generation I will increase the number of points
     * @return int value
     * @throws Exception if I am trying to access it before reading it
     */
    public int getHowManyGenBeforeNewPoint() throws Exception {
        if(this.incrementalLearningPoints == null) throw new Exception("Try to access config file before reading it.");
        return this.howManyGenBeforeNewPoint;
    }

    /**
     * Getter for how many time steps ahead I am to generate
     * @return int value
     * @throws Exception if I am trying to access it before reading it
     */
    public int getMoreTimeAhead() throws Exception {
        if(this.moreTimeAhead == null) throw new Exception("Try to access config file before reading it.");
        return this.moreTimeAhead;
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

        /**
         * Getter for the number of timestep considered in the trajectory
         * @return int number with the number of timesteps
         * @throws Exception  if I am trying to access it before reading it
         */
        public static int getNumberOfTimestepConsidered() throws Exception {
            return config.getNumberOfTimestepConsidered();
        }

        /**
         * Getter for the agents tournament size
         * @return integer value for tournament size
         * @throws Exception  if I am trying to access it before reading it
         */
        public static int getTournamentSizeAgents() throws Exception {
            return config.getTournamentSizeAgents();
        }

        /**
         * Getter for the classifiers tournament size
         * @return integer value for tournament size
         * @throws Exception  if I am trying to access it before reading it
         */
        public static int getTournamentSizeClassifiers() throws Exception {
            return config.getTournamentSizeClassifiers();
        }

        /**
         * Getter for the step size
         * @return double value for tournament size
         * @throws Exception  if I am trying to access it before reading it
         */
        public static double getStepSizeAgents() throws Exception {
            return config.getStepSizeAgents();
        }

        /**
         * Getter for the step size
         * @return double value for tournament size
         * @throws Exception  if I am trying to access it before reading it
         */
        public static double getStepSizeClassifiers() throws Exception {
            return config.getStepSizeClassifiers();
        }

        /**
         * Getter if I am using the reduced virulence way
         * @return boolean value
         * @throws Exception  if I am trying to access it before reading it
         */
        public static boolean getUsingReducedVirulenceMethodOnAgents() throws Exception {
            return config.getUsingReducedVirulenceMethodOnAgents();
        }

        /**
         * Getter of the value of the virulence
         * @return double value
         * @throws Exception  if I am trying to access it before reading it
         */
        public static double getVirulenceAgents() throws Exception {
            return config.getVirulenceAgents();
        }
        /**
         * Getter if I am using the reduced virulence way
         * @return boolean value
         * @throws Exception  if I am trying to access it before reading it
         */
        public static boolean getUsingReducedVirulenceMethodOnClassifiers() throws Exception {
            return config.getUsingReducedVirulenceMethodOnClassifiers();
        }

        /**
         * Getter of the value of the virulence
         * @return double value
         * @throws Exception  if I am trying to access it before reading it
         */
        public static double getVirulenceClassifiers() throws Exception {
            return config.getVirulenceClassifiers();
        }
        /**
         * Getter if I am using the LSTM also for the classifier
         * @return boolena value
         * @throws Exception  if I am trying to access it before reading it
         */
        public static boolean getLSTMClassifier() throws Exception {
            return config.getLSTMClassifier();
        }

        /**
         * Getter if I am using the ENN for the classifier
         * @return boolena value
         * @throws Exception  if I am trying to access it before reading it
         */
        public static boolean getENN() throws Exception {
            return config.getENN();
        }

        /**
         * Get the model used as a number
         * @return int value
         * @throws Exception  if I am trying to access it before reading it
         */
        public static int getValueClassifier() throws Exception {
            return config.getValueClassifier();
        }


        /**
         * Getter for the selector if using the automatic disengagement system
         * @return int value
         * @throws Exception  if I am trying to access it before reading it
         */
        public static int getAutomaticEvolutionDisengagementSystem() throws Exception {
            return config.getAutomaticEvolutionDisengagementSystem();
        }

        /**
         * Getter for the measure used in the automatic system
         * @return int value
         * @throws Exception  if I am trying to access it before reading it
         */
        public static int getMeasureUsedForAutomaticDisengagement() throws Exception {
            return config.getMeasureUsedForAutomaticDisengagement();
        }

        /**
         * Getter for the population that will use the automatic disengagement system
         * @return int value
         * @throws Exception  if I am trying to access it before reading it
         */
        public static int getPopulationWillUseTheAutomaticDisengagementSystem() throws Exception {
            return config.getPopulationWillUseTheAutomaticDisengagementSystem();
        }

        /**
         * Getter for the property if the classifiers has a different selection method
         * @return int
         * @throws Exception if I am trying to access it before reading it
         */
        public static int getDifferentSelectionForClassifiers() throws Exception {
            return config.getDifferentSelectionForClassifiers();
        }
        /**
         * Getter for the property if the classifiers has a different selection method
         * @return int
         * @throws Exception if I am trying to access it before reading it
         */
        public static int getDifferentSelectionForAgent() throws Exception {
            return config.getDifferentSelectionForAgent();
        }

        /**
         * Getter for the percentage of trajectory kept between generations
         * @return int number
         * @throws Exception if I am trying to access it before reading it
         */
        public static int getHowManyAmIChangingBetweenGeneration() throws Exception {
            return config.getHowManyAmIChangingBetweenGeneration();
        }

        /**
         * Getter of the value of the maxSpeed
         * @return double value
         * @throws Exception  if I am trying to access it before reading it
         */
        public static double getMaxSpeed() throws Exception {
            return config.getMaxSpeed();
        }

        /**
         * Getter if I am using the conversion with graph
         * @return boolena value
         * @throws Exception  if I am trying to access it before reading it
         */
        public static boolean getConversionWithGraph() throws Exception {
            return config.getConversionWithGraph();
        }

        /**
         * Getter if I am using the score
         * @return boolean value
         * @throws Exception  if I am trying to access it before reading it
         */
        public static boolean getScore() throws Exception {
            return config.getScore();
        }

        /**
         * Getter for how many I want to keep with specific survival selection
         * @return int number
         * @throws Exception  if I am trying to access it before reading it
         */
        public static int getKeepBestNElement() throws Exception {
            return config.getKeepBestNElement();
        }

        /**
         * Getter for how many timesteps the uncorrelated mutation has
         * @return string value
         * @throws Exception if I am trying to access it before reading it
         */
        public static String getUncorrelatedMutationStep() throws Exception {
            return config.getUncorrelatedMutationStep();
        }

        /**
         * Getter for the hall of fame, If I am using it
         * @return boolean value
         * @throws Exception if I am trying to access it before reading it
         */
        public static boolean getHallOfFame() throws Exception {
            return config.getHallOfFame();
        }

        /**
         * Getter for the hall of fame memory
         * @return int value
         * @throws Exception if I am trying to access it before reading it
         */
        public static int getHallOfFameMemory() throws Exception {
            return config.getHallOfFameMemory();
        }

        /**
         * Getter for the hall of fame sample size
         * @return int value
         * @throws Exception if I am trying to access it before reading it
         */
        public static int getHallOfFameSample() throws Exception {
            return config.getHallOfFameSample();
        }

        /**
         * Getter for the type of Fitness Function I am using
         * @return int value
         * @throws Exception if I am trying to access it before reading it
         */
        public static int getFitnessFunction() throws Exception {
            return config.getFitnessFunction();
        }

        /**
         * Getter if I am using the time as Input for the generator
         * @return boolean value
         * @throws Exception if I am trying to access it before reading it
         */
        public static boolean getTimeAsInput() throws Exception {
            return config.getTimeAsInput();
        }

        /**
         * Getter if I am using the simple NN as an output
         * @return boolean value
         * @throws Exception if I am trying to access it before reading it
         */
        public static boolean getNN() throws Exception {
            return config.getNN();
        }

        /**
         * Setter for the number of agent timesteps
         * used only if I am doing the incremental learning
         * @param agentTimeSteps number of timesteps
         */
        public static void setAgentTimeSteps(int agentTimeSteps) {
            config.setAgentTimeSteps(agentTimeSteps);
        }

        /**
         * Getter if I am using the simple NN as an output
         * @return boolean value
         * @throws Exception if I am trying to access it before reading it
         */
        public static int getDebugLevel() throws Exception {
            return config.getDebugLevel();
        }

        /**
         * Getter for the value if I am using incrementing learning for the points
         * @return boolean value
         * @throws Exception if I am trying to access it before reading it
         */
        public static boolean getIncrementalLearningPoints() throws Exception {
            return config.getIncrementalLearningPoints();
        }

        /**
         * Getter for after how many generation I will increase the number of points
         * @return int value
         * @throws Exception if I am trying to access it before reading it
         */
        public static int getHowManyGenBeforeNewPoint() throws Exception {
            return config.getHowManyGenBeforeNewPoint();
        }

        /**
         * Getter for how many time steps ahead I am to generate
         * @return int value
         * @throws Exception if I am trying to access it before reading it
         */
        public static int getMoreTimeAhead() throws Exception {
            return config.getMoreTimeAhead();
        }
    }

}

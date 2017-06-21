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

    //model config
    private Integer hiddenLayersAgent;
    private Integer hiddenNeuronsAgent;
    private Integer hiddenNeuronsClassifier;

    //experiment config
    private String name;
    private String experiment;
    private String path;
    private Boolean dumpPop;
    private Boolean loadDumpPop;

    /**
     * Constructor with zero parameter
     * Everything is set to null.
     */
    public ReadConfig(){
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

        this.hiddenLayersAgent = null;
        this.hiddenNeuronsAgent = null;
        this.hiddenNeuronsClassifier = null;
    }

    /**
     * Method that returns the location of the file containing the graph
     * @return String with the path
     * @throws Exception if I am trying to access it before reading it
     */
    public Integer getTrajectoriesType() throws Exception {
        if(this.trajectoriesType == null) throw new Exception("Try to access config file before reading it.");
        return this.trajectoriesType;
    }

    /**
     * Method that returns how many time I split the trajectory
     * @return Integer numberh
     * @throws Exception if I am trying to access it before reading it
     */
    public Integer getHowManySplitting() throws Exception {
        if(this.howManySplitting == null) throw new Exception("Try to access config file before reading it.");
        return this.howManySplitting;
    }

    /**
     * Getter for the offspring size for the agents
     * @return Integer number with the size
     * @throws Exception if I am trying to access it before reading it
     */
    public Integer getAgentOffspringSize() throws Exception {
        if(this.agentOffspringSize == null) throw new Exception("Try to access config file before reading it.");
        return agentOffspringSize;
    }

    /**
     * Getter for the population size for the agents
     * @return Integer number with the size
     * @throws Exception if I am trying to access it before reading it
     */
    public Integer getAgentPopulationSize() throws Exception {
        if(this.agentPopulationSize == null) throw new Exception("Try to access config file before reading it.");
        return agentPopulationSize;
    }

    /**
     * Getter for alpha for the agents
     * @return Double number
     * @throws Exception if I am trying to access it before reading it
     */
    public Double getAgentAlpha() throws Exception {
        if(this.agentAlpha == null) throw new Exception("Try to access config file before reading it.");
        return agentAlpha;
    }

    /**
     * Getter for time steps for the agents
     * @return Integer number
     * @throws Exception if I am trying to access it before reading it
     */
    public Integer getAgentTimeSteps() throws Exception {
        if(this.agentTimeSteps == null) throw new Exception("Try to access config file before reading it.");
        return agentTimeSteps;
    }

    /**
     * Getter for the offspring size for the classifiers
     * @return Integer number with the size
     * @throws Exception if I am trying to access it before reading it
     */
    public Integer getClassifierOffspringSize() throws Exception {
        if(this.classifierOffspringSize == null) throw new Exception("Try to access config file before reading it.");
        return classifierOffspringSize;
    }

    /**
     * Getter for the population size for the classifiers
     * @return Integer number with the size
     * @throws Exception if I am trying to access it before reading it
     */
    public Integer getClassifierPopulationSize() throws Exception {
        if(this.classifierPopulationSize == null) throw new Exception("Try to access config file before reading it.");
        return classifierPopulationSize;
    }

    /**
     * Getter for alpha for the classifiers
     * @return Double number
     * @throws Exception if I am trying to access it before reading it
     */
    public Double getClassifierAlpha() throws Exception {
        if(this.classifierAlpha == null) throw new Exception("Try to access config file before reading it.");
        return classifierAlpha;
    }

    /**
     * Getter for time steps for the classifiers
     * @return Integer number
     * @throws Exception if I am trying to access it before reading it
     */
    public Integer getClassifierTimeSteps() throws Exception {
        if(this.classifierTimeSteps == null) throw new Exception("Try to access config file before reading it.");
        return classifierTimeSteps;
    }

    /**
     * Method that reads the file with all the settings.
     * The file's name is hardcoded as "graph_setting.json".
     * @throws Exception If the file is not available, not well formatted or the settings are not all coded an exception
     * is raised
     */
    public void readFile() throws Exception {
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
    }


    /**
     * getter for the number of hidden layer in the agent model
     * @return Integer number
     * @throws Exception  if I am trying to access it before reading it
     */
    public Integer getHiddenLayersAgent() throws Exception {
        if(this.hiddenLayersAgent == null) throw new Exception("Try to access config file before reading it.");
        return this.hiddenLayersAgent;
    }

    /**
     * getter for the number of hidden nodes in the hidden layers in the agent model
     * @return Integer number
     * @throws Exception  if I am trying to access it before reading it
     */
    public Integer getHiddenNeuronsAgent() throws Exception {
        if(this.hiddenNeuronsAgent == null) throw new Exception("Try to access config file before reading it.");
        return this.hiddenNeuronsAgent;
    }

    /**
     * getter for the number of hidden nodes in the hidden layer in the classifier model
     * @return Integer number
     * @throws Exception  if I am trying to access it before reading it
     */
    public Integer getHiddenNeuronsClassifier() throws Exception {
        if(this.hiddenNeuronsClassifier == null) throw new Exception("Try to access config file before reading it.");
        return this.hiddenNeuronsClassifier;
    }

    /**
     * getter for the number of trajectory that I am going to analise
     * @return Integer number
     * @throws Exception  if I am trying to access it before reading it
     */
    public Integer getHowManyTrajectories() throws Exception {
        if(this.howManyTrajectories == null) throw new Exception("Try to access config file before reading it.");
        return this.howManyTrajectories;
    }

    /**
     * getter for the number of maximum generations allowed
     * @return Integer number
     * @throws Exception  if I am trying to access it before reading it
     */
    public Integer getMaxGenerations() throws Exception {
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
     * Override toString Method in order to print all the setting here
     * @return String containing all the setting
     */
    @Override
    public String toString() {
        return "ReadConfig{" + "\n" +
                "trajectoriesType=" + trajectoriesType + ",\n" +
                "howManySplitting=" + howManySplitting + ",\n" +
                "howManyTrajectories=" + howManyTrajectories + ",\n" +
                "agentPopulationSize=" + agentPopulationSize + ",\n" +
                "agentOffspringSize=" + agentOffspringSize + ",\n" +
                "agentAlpha=" + agentAlpha + ",\n" +
                "agentTimeSteps=" + agentTimeSteps + ",\n" +
                "classifierPopulationSize=" + classifierPopulationSize + ",\n" +
                "classifierOffspringSize=" + classifierOffspringSize + ",\n" +
                "classifierAlpha=" + classifierAlpha + ",n" +
                "classifierTimeSteps=" + classifierTimeSteps + ",\n" +
                "maxGenerations=" + maxGenerations + ",\n" +
                "hiddenLayersAgent=" + hiddenLayersAgent + ",\n" +
                "hiddenNeuronsAgent=" + hiddenNeuronsAgent + ",\n" +
                "hiddenNeuronsClassifier=" + hiddenNeuronsClassifier + ",\n" +
                "name='" + name + '\'' + ",\n" +
                "experiment='" + experiment + '\'' + ",\n" +
                "path='" + path + '\'' + "\n" +
                '}';
    }

    /**
     * Am I saving all the population on a file?
     * @return Boolean Value
     * @throws Exception if I am trying to access it before reading it
     */
    public Boolean getDumpPop() throws Exception {
        if(this.dumpPop == null) throw new Exception("Try to access config file before reading it.");
        return this.dumpPop;
    }

    /**
     * Am i loading the population from a file?
     * @return Boolean Value
     * @throws Exception if I am trying to access it before reading it
     */
    public Boolean getLoadDumpPop() throws Exception {
        if(this.loadDumpPop == null) throw new Exception("Try to access config file before reading it.");
        return this.loadDumpPop;
    }
}

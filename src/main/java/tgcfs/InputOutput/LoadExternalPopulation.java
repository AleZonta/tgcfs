package tgcfs.InputOutput;

import tgcfs.Config.ReadConfig;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by Alessandro Zonta on 02/11/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 */
public class LoadExternalPopulation {
    private String currentPath;
    private static Logger logger; //logger for this class
    private String nameAgents;
    private String nameClassifier;
    private List<List<Double>> agents;
    private List<List<Double>> classifiers;

    /**
     * Constructor one parameter
     * It requires the path to be the one where the app is and it checks if the two file are present
     * @param log logger of the app
     * @throws Exception if the file does not exist
     */
    public LoadExternalPopulation(Logger log) throws Exception {
        logger = log;
        //hardcoded name and position for the population to load
        this.nameAgents = "/LoadPopulation/tgcfs.EA.Agents-population.zip";
        this.nameClassifier = "/LoadPopulation/tgcfs.EA.Classifiers-population.zip";
        this.currentPath = Paths.get(".").toAbsolutePath().normalize().toString();
        File f = new File(this.currentPath + this.nameAgents);
        if(!f.exists()) throw new Exception("Agents Population file does not exist");
        f = new File(this.currentPath + this.nameClassifier);
        if(!f.exists()) throw new Exception("Classifiers Population file does not exist");
    }


    /**
     * Read the files
     * it also checks if the sizes corresponds
     * @throws Exception if there are problem reading the file or the size does not correspond
     */
    public void readFile() throws Exception {
        //read file into stream, try-with-resources
        logger.log(Level.INFO, "Reading Agents Population");
        this.agents = this.readZip(this.currentPath + this.nameAgents);
        logger.log(Level.INFO, "Reading Classifier Population");
        this.classifiers = this.readZip(this.currentPath + this.nameClassifier);
        //check if the sizes correspond to the settings
        if (this.agents.size() != ReadConfig.Configurations.getAgentPopulationSize()) throw new Exception("Agent population size required is not the same as the one loaded");
        if (this.classifiers.size() != ReadConfig.Configurations.getClassifierPopulationSize()) throw new Exception("Classifiers population size required is not the same as the one loaded");
    }


    /**
     * Read the zip file and save the content in a list of string.
     * It also removes the sha-1 check from the file
     * @param name name of the file to read
     * @return list of double
     * @throws Exception if there are problems in reading the file
     */
    private List<List<Double>> readZip(String name) throws Exception {
        List<String> res = new ArrayList<>();

        ZipFile zipFile = new ZipFile(name);

        Enumeration<? extends ZipEntry> entries = zipFile.entries();

        while(entries.hasMoreElements()){
            ZipEntry entry = entries.nextElement();
            InputStream stream = zipFile.getInputStream(entry);
            try (BufferedReader buffer = new BufferedReader(new InputStreamReader(stream))) {
                res =  buffer.lines().collect(Collectors.toList());
            }catch (IOException e) {
                throw new Exception("Error in reading " + name);
            }
        }

        //need to check if in the first element there is the sha-1 element before the [
        int pos = res.get(0).indexOf("[");
        res.set(0, res.get(0).substring(pos));

        List<List<Double>> population = new ArrayList<>();
        res.forEach(line -> {
            List<String> ind =  Arrays.asList(line.replace("[","").replace("]","").split(","));
            List<Double> individual = new ArrayList<>();
            ind.forEach(weight -> individual.add(Double.parseDouble(weight)));
            population.add(individual);
        });

        return population;
    }

    /**
     * Getter for the list of the weights for the agents
     * @return list of list of doubles
     */
    public List<List<Double>> getAgents() {
        return agents;
    }

    /**
     * Getter for the list of the weights for the classifiers
     * @return list of list of doubles
     */
    public List<List<Double>> getClassifiers() {
        return classifiers;
    }
}

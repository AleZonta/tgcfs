package tgcfs.Performances;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.nd4j.linalg.api.ndarray.INDArray;
import tgcfs.Config.PropertiesFileReader;
import tgcfs.EA.Individual;
import tgcfs.Loader.TrainReal;
import tgcfs.Utils.Scores;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by Alessandro Zonta on 01/06/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 *
 * This class will handle the saving to file of the performances
 */
public class SaveToFile {
    private String currentPath;
    private static Logger logger; //logger for this class


    /**
     * Static class.
     * This class will be the only one instantiating SaveToFile class and offering
     * the method to save info
     */
    public static class Saver {
        private static SaveToFile instance = null;

        /**
         * Constructor with two parameters
         * It calls the private constructor of the SaveToFile class
         * @param name name of the experiment
         * @param experiment number of the experiment
         * @param log logger instance
         */
        public Saver(String name, String experiment, Logger log){
            instance = new SaveToFile(name, experiment, log);
        }

        /**
         * Constructor with three parameters
         * It calls the private constructor of the SaveToFile class
         * @param name name of the experiment
         * @param experiment number of the experiment
         * @param path path where to save
         * @param log logger instance
         */
        public Saver(String name, String experiment, String path, Logger log){
            instance = new SaveToFile(name, experiment, path, log);
        }

        /**
         * Append fitness line to the file saving the finesses
         * It calls the private method to do that of the SaveToFile class
         * @param name name of the class/file I am saving
         * @param listFitness list with the integer value of the fitness
         * @throws Exception if the class is not instantiate
         */
        public static void saveFitness(String name, List<Integer> listFitness) throws Exception {
            if(instance == null) throw new Exception("Cannot save, the class is not instantiate");
            instance.saveFitness(name, listFitness);
        }

        /**
         * Initialise the fitness file adding the sha-1 code of the github verion of the framework
         * @param name  name of the class/file I am saving
         * @throws Exception if the class is not instantiate
         */
        public static void initialiseFitnessFile(String name) throws Exception {
            if(instance == null) throw new Exception("Cannot save, the class is not instantiate");
            instance.initialiseFitness(name);
        }

        /**
         * Save all the configuration in the same location where all the results are
         * @param setting config files
         */
        public static void dumpSetting(Object setting){
            if(instance!=null) {
                instance.dumpConfig(setting);
            }
        }

        /**
         * Append  line to the file saving the best genome
         * It calls the private method to do that of the SaveToFile class
         * @param name name of the class/file I am saving
         * @param genoma list with the double value of the genoma
         * @throws Exception  if the class is not instantiate
         */
        public static void saveBestGenoma(String name, INDArray genoma) throws Exception {
            if(instance == null) throw new Exception("Cannot save, the class is not instantiate");
            instance.saveGenome(name, genoma);
        }

        /**
         * Initialise the genoma file adding the sha-1 code of the github verion of the framework
         * @param name  name of the class/file I am saving
         * @throws Exception if the class is not instantiate
         */
        public static void initialiseGenomaFile(String name) throws Exception {
            if(instance == null) throw new Exception("Cannot save, the class is not instantiate");
            instance.initialiseGenome(name);
        }

        /**
         * Save the entire population in a csv file inside a zip file to save space
         * @param name name of the population
         * @param population individual of the population
         * @throws Exception  if the class is not instantiate
         */
        public static void dumpPopulation(String name, List<Individual> population) throws Exception {
            if(instance == null) throw new Exception("Cannot save, the class is not instantiate");
            instance.dumpEverything(name, population);
        }


        /**
         * Save in JSON format the trajectory and the generated part of it
         * @param combineInputList List of {@link TrainReal}
         * @param generationAgent number of generation for the agent population
         * @param generationClassifier number of generation for the classifier population
         * @throws Exception  if the class is not instantiate
         */
        public static void dumpTrajectoryAndGeneratedPart(List<TrainReal> combineInputList, int generationAgent, int generationClassifier) throws Exception {
            if(instance == null) throw new Exception("Cannot save, the class is not instantiate");
            instance.dumpTrajectoryAndGeneratedPart(combineInputList, generationAgent, generationClassifier);
        }

        /**
         * Save in JSON format the scores
         * @param generationAgent number of generation for the agent population
         * @param generationClassifier number of generation for the classifier population
         * @param scores List of {@link Scores}
         * @throws Exception  if the class is not instantiate
         */
        public static void saveScoresBattle(List<Scores> scores, int generationAgent, int generationClassifier) throws Exception {
            if(instance == null) throw new Exception("Cannot save, the class is not instantiate");
            instance.saveScoresBattle(scores,generationAgent,generationClassifier);
        }
    }




    /**
     * Constructor with two parameters
     * It creates a folder called Experiment + name and then inside folder related to experiments
     * This method localises the path where the program is located
     * @param name name of the experiment
     * @param experiment number of the experiment
     * @param log logger instance
     */
    private SaveToFile(String name, String experiment, Logger log){
        logger = log;
        this.currentPath = Paths.get(".").toAbsolutePath().normalize().toString() + "/Experiment-" + name;
        new File(this.currentPath).mkdirs();
        this.currentPath += "/" + experiment;
        new File(this.currentPath).mkdirs();
        this.currentPath += "/";
    }


    /**
     * Constructor with three parameters
     * It creates a folder called Experiment + name and then inside folder related to experiments
     * @param name name of the experiment
     * @param experiment number of the experiment
     * @param path path where to save
     * @param log logger instance
     */
    private SaveToFile(String name, String experiment, String path, Logger log){
        logger = log;
        this.currentPath = path + "/Experiment-" + name;
        new File(this.currentPath).mkdirs();
        this.currentPath += "/" + experiment;
        new File(this.currentPath).mkdirs();
        this.currentPath += "/";
    }


    /**
     * Initialise the file with the sha-1 code of the commit
     * @param name  name of the class/file I am saving
     */
    private void initialiseFitness(String name){
        try {
            BufferedWriter outputWriter = new BufferedWriter(new FileWriter(this.currentPath + name + "-fitness.csv", true));
            outputWriter.write("git-sha-1=" + PropertiesFileReader.getGitSha1());
            outputWriter.newLine();

            logger.log(Level.INFO, "Successfully Added git-sha-1 to " + name + " CSV File");

            outputWriter.flush();
            outputWriter.close();
        }catch (Exception e){
            logger.log(Level.WARNING, "Error with " + name + " CSV File " + e.getMessage());
        }
    }

    /**
     * Append fitness line to the file saving the finesses
     * @param name name of the class/file I am saving
     * @param listFitness list with the Integer value of the fitness
     */
    private void saveFitness(String name, List<Integer> listFitness){
        try {
            BufferedWriter outputWriter = new BufferedWriter(new FileWriter(this.currentPath + name + "-fitness.csv", true));
            listFitness.forEach(fitness -> {
                try {
                    outputWriter.write(Integer.toString(fitness) + ", ");
                } catch (IOException e) {
                    logger.log(Level.WARNING, "Error appending line to" + name + " CSV File " + e.getMessage());
                }
            });
            outputWriter.newLine();

            logger.log(Level.INFO, "Successfully Added Line to " + name + " CSV File");

            outputWriter.flush();
            outputWriter.close();
        }catch (Exception e){
            logger.log(Level.WARNING, "Error with " + name + " CSV File " + e.getMessage());
        }
    }

    /**
     * Save all the configuration in the same location where all the results are
     * @param config config files
     */
    private void dumpConfig(Object config){
        String name = config.getClass().getName();
        String path = this.currentPath + name + ".txt";
        try( PrintWriter out = new PrintWriter( path )  ){
            out.println(config.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        logger.log(Level.INFO, "Successfully saved " + name + " config File");
    }



    private void initialiseGenome(String name){
        try {
            BufferedWriter outputWriter = new BufferedWriter(new FileWriter(this.currentPath + name + "-genome.csv", true));
            outputWriter.write("git-sha-1=" + PropertiesFileReader.getGitSha1());
            outputWriter.newLine();

            logger.log(Level.INFO, "Successfully Added git-sha-1 to " + name + " CSV File");

            outputWriter.flush();
            outputWriter.close();
        }catch (Exception e){
            logger.log(Level.WARNING, "Error with " + name + " CSV File " + e.getMessage());
        }
    }


    /**
     * Save the genome of the best individual
     * @param name name of the class/file I am saving
     * @param genome list with the double value of the genome
     */
    private void saveGenome(String name, INDArray genome){
        try {
            BufferedWriter outputWriter = new BufferedWriter(new FileWriter(this.currentPath + name + "-genome.csv", true));
            outputWriter.write(genome.data().toString());
            outputWriter.newLine();
            logger.log(Level.INFO, "Successfully Added Line to " + name + " CSV File");

            outputWriter.flush();
            outputWriter.close();
        }catch (Exception e){
            logger.log(Level.WARNING, "Error with " + name + " CSV File " + e.getMessage());
        }
    }


    /**
     * Save the entire population in a csv file inside a zip file to save space
     * If the file already exists, it is going to be erased
     * @param name name of the population
     * @param population individual of the population
     */
    private void dumpEverything(String name, List<Individual> population){
        String path = this.currentPath + name + "-population" + ".zip";
        File f = new File(path);
        if(f.exists() && !f.isDirectory()) {
            try {
                Files.delete(Paths.get(path));
            } catch (IOException e) {
                logger.log(Level.WARNING, "Error deleting file " + name + " CSV File " + e.getMessage());
                e.printStackTrace();
            }
        }
        //check if file exist
        try (FileOutputStream zipFile = new FileOutputStream(new File(path));
             ZipOutputStream zos = new ZipOutputStream(zipFile);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(zos, "UTF-8"))
        ){
            ZipEntry csvFile = new ZipEntry( name + "-population.csv");
            zos.putNextEntry(csvFile);
            writer.write("git-sha-1=" + PropertiesFileReader.getGitSha1());
            population.forEach(individual -> {
                try {
                    writer.write(individual.getObjectiveParameters().data().toString());
                    writer.newLine();
                } catch (IOException e) {
                    logger.log(Level.WARNING, "Error appending line to " + name + " CSV File " + e.getMessage());
                    e.printStackTrace();
                }
            });
        }catch (Exception e){
            logger.log(Level.WARNING, "Error with " + name + " Zip File " + e.getMessage());
        }
    }


    /**
     * Save in JSON format the trajectory and the generated part of it
     * @param generationAgent number of generation for the agent population
     * @param generationClassifier number of generation for the classifier population
     * @param combineInputList List of {@link TrainReal}
     */
    private void dumpTrajectoryAndGeneratedPart(List<TrainReal> combineInputList, int generationAgent, int generationClassifier){
        String path = this.currentPath + "trajectory-generatedPoints-" + generationAgent + "-" + generationClassifier + ".zip";
        try (FileOutputStream zipFile = new FileOutputStream(new File(path));
             ZipOutputStream zos = new ZipOutputStream(zipFile);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(zos, "UTF-8"))
        ){
            ZipEntry csvFile = new ZipEntry(  "trajectory-generatedPoints-" + generationAgent + "-" + generationClassifier + ".json");
            zos.putNextEntry(csvFile);

            JSONObject totalObj = new JSONObject();

            IntStream.range(0, combineInputList.size()).forEach(i -> {

                TrainReal el = combineInputList.get(i);

                JSONObject obj = new JSONObject();
                JSONArray trajectory = new JSONArray();
                //put the trajectory
                trajectory.addAll(el.getPoints());
                obj.put("trajectory", trajectory);

                JSONArray generated = new JSONArray();
                generated.addAll(el.getRealPointsOutputComputed());
                obj.put("generated", generated);

                JSONArray real = new JSONArray();
                real.addAll(el.getFollowingPart());
                obj.put("real", real);

                String name = "trajectory-" + i;
                totalObj.put(name, obj);
            });

            totalObj.put("size", combineInputList.size());
            try {
                writer.write("git-sha-1=" + PropertiesFileReader.getGitSha1());
                writer.write(totalObj.toJSONString());
                writer.newLine();
            } catch (IOException e) {
                logger.log(Level.WARNING, "Error appending line to trajectory-generatedPoints CSV File-" + generationAgent + "-" + generationClassifier + " " + e.getMessage());
                e.printStackTrace();
            }

        }catch (Exception e){
            logger.log(Level.WARNING, "Error with trajectory-generatedPoints Zip File-" + generationAgent + "-" + generationClassifier + " " + e.getMessage());
        }
    }


    /**
     * Save in JSON format the scores
     * @param generationAgent number of generation for the agent population
     * @param generationClassifier number of generation for the classifier population
     * @param scores List of {@link Scores}
     */
    private void saveScoresBattle(List<Scores> scores, int generationAgent, int generationClassifier){
        String path = this.currentPath + "scores-" + generationAgent + "-" + generationClassifier + ".zip";
        try (FileOutputStream zipFile = new FileOutputStream(new File(path));
             ZipOutputStream zos = new ZipOutputStream(zipFile);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(zos, "UTF-8"))
        ){
            ZipEntry csvFile = new ZipEntry(  "scores-" + generationAgent + "-" + generationClassifier + ".json");
            zos.putNextEntry(csvFile);

            JSONObject obj = new JSONObject();

            JSONArray allTheScores = new JSONArray();
            scores.forEach(s -> allTheScores.add(s.toString()));
            obj.put("scores", allTheScores);

            try {
                writer.write("git-sha-1=" + PropertiesFileReader.getGitSha1());
                writer.write(obj.toJSONString());
                writer.newLine();
            } catch (IOException e) {
                logger.log(Level.WARNING, "Error appending line to scores CSV File-" + generationAgent + "-" + generationClassifier + " " + e.getMessage());
                e.printStackTrace();
            }

        }catch (Exception e){
            logger.log(Level.WARNING, "Error with scores Zip File-" + generationAgent + "-" + generationClassifier + " " + e.getMessage());
        }
    }
}

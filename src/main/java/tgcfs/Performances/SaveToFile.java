package tgcfs.Performances;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.nd4j.linalg.api.ndarray.INDArray;
import tgcfs.Config.PropertiesFileReader;
import tgcfs.EA.Individual;
import tgcfs.Loader.TrainReal;
import tgcfs.NN.InputsNetwork;
import tgcfs.Utils.PointWithBearing;
import tgcfs.Utils.Scores;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private static int counter;
    private List<TrainReal> uuids;


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
        public static void saveFitness(String name, List<Double> listFitness) throws Exception {
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
         * Append  line to the file saving the stepSize of the best element
         * It calls the private method to do that of the SaveToFile class
         * @param name name of the class/file I am saving
         * @param stepSize list with the double value of the stepSize
         * @throws Exception  if the class is not instantiate
         */
        public static void saveStepSize(String name, List<INDArray> stepSize) throws Exception {
            if(instance == null) throw new Exception("Cannot save, the class is not instantiate");
            instance.saveStepSize(name, stepSize);
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
         * Save in JSON format the trajectory and the generated part of it
         * @param combineInputList List of {@link TrainReal}
         * @param gen number of generation for the agent population
         * @throws Exception  if the class is not instantiate
         */
        public static void dumpTrajectoryAndGeneratedPart(List<TrainReal> combineInputList, int gen) throws Exception {
            if(instance == null) throw new Exception("Cannot save, the class is not instantiate");
            instance.dumpTrajectoryAndGeneratedPart(combineInputList, gen);
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

        /**
         * Save the max fitness achievable by the agent and the classifier
         * @param fitness value of the fitness
         * @param name agent or classfier
         * @throws Exception  if the class is not instantiate
         */
        public static void saveMaxFitnessAchievable(int fitness, String name) throws Exception {
            if(instance == null) throw new Exception("Cannot save, the class is not instantiate");
            instance.saveMaxFitnessAchievable(fitness,name);
        }

        /**
         * Save what the classifiers does with the real trajectories
         * @param resultsRealTrajectories real trajectories results
         */
        public static void saveResultRealClassifier(HashMap<Integer, Map<Integer, Map<UUID, Double>>> resultsRealTrajectories) throws Exception {
            if(instance == null) throw new Exception("Cannot save, the class is not instantiate");
            instance.saveResultRealClassifier(resultsRealTrajectories);
        }

        /**
         * Save only the trace of the trajectories
         * @param trajectories {@link TrainReal} object with the info of the trajectories
         */
        public static void saveTrajectory(List<TrainReal> trajectories) throws Exception {
            if(instance == null) throw new Exception("Cannot save, the class is not instantiate");
            instance.saveTrajectory(trajectories);
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
        counter = 0;
        this.uuids = new ArrayList<>();
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
        counter = 0;
        this.uuids = new ArrayList<>();
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

            logger.log(Level.FINE, "Successfully Added git-sha-1 to " + name + " CSV File");

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
    private void saveFitness(String name, List<Double> listFitness){
        try {
            BufferedWriter outputWriter = new BufferedWriter(new FileWriter(this.currentPath + name + "-fitness.csv", true));
            listFitness.forEach(fitness -> {
                try {
                    outputWriter.write(Double.toString(fitness) + ", ");
                } catch (IOException e) {
                    logger.log(Level.SEVERE, "Error appending line to" + name + " CSV File " + e.getMessage());
                }
            });
            outputWriter.newLine();

            logger.log(Level.FINE, "Successfully Added Line to " + name + " CSV File");

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
        logger.log(Level.FINE, "Successfully saved " + name + " config File");
    }



    private void initialiseGenome(String name){
        try {
            BufferedWriter outputWriter = new BufferedWriter(new FileWriter(this.currentPath + name + "-genome.csv", true));
            outputWriter.write("git-sha-1=" + PropertiesFileReader.getGitSha1());
            outputWriter.newLine();

            logger.log(Level.FINE, "Successfully Added git-sha-1 to " + name + " CSV File");

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
            //print all the value of the double
            List<Double> list = new ArrayList<>();
            for(int i=0; i< genome.columns(); i++){
                list.add(genome.getDouble(i));
            }
            outputWriter.write(list.toString());
            outputWriter.newLine();
            logger.log(Level.FINE, "Successfully Added Line to " + name + " CSV File");

            outputWriter.flush();
            outputWriter.close();
        }catch (Exception e){
            logger.log(Level.WARNING, "Error with " + name + " CSV File " + e.getMessage());
        }
    }

    /**
     * Save the stepsize of the best individual
     * @param name name of the class/file I am saving
     * @param stepSize list with the double value of the stepsizes
     */
    private void saveStepSize(String name, List<INDArray> stepSize){
        try {
            BufferedWriter outputWriter = new BufferedWriter(new FileWriter(this.currentPath + name + "-stepsize.csv", true));
            //print all the value of the double
            List<Double> list = new ArrayList<>();
            for(INDArray array: stepSize){
                List<Double> ss = new ArrayList<>();
                for(int i=0; i< array.columns(); i++){
                    list.add(array.getDouble(i));
                }
            }

            outputWriter.write(list.toString());
            outputWriter.newLine();
            logger.log(Level.FINE, "Successfully Added Line to " + name + " CSV File");

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

                //print all the value of the double
                INDArray data = individual.getObjectiveParameters();
                List<Double> list = new ArrayList<>();
                for(int i=0; i< data.columns(); i++){
                    list.add(data.getDouble(i));
                }

                try {
                    writer.write(list.toString());
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
     * @param combineInputList List of {@link TrainReal}
     * @param gen number of generation
     */
    private void dumpTrajectoryAndGeneratedPart(List<TrainReal> combineInputList, int gen){
        String path = this.currentPath + "trajectory-generate-aSs-" + gen + ".zip";
        this.dumpInfo(combineInputList, path);
        path = this.currentPath + "statistic.csv";
        this.saveStatistics(combineInputList, path);
    }


    /**
     * Save all the statistic related the points generated
     * @param combineInputList  List of {@link TrainReal}
     * @param path path where to save
     */
    private void saveStatistics(List<TrainReal> combineInputList, String path){
        try {
            BufferedWriter outputWriter = new BufferedWriter(new FileWriter(path, true));
            for(TrainReal trainReal: combineInputList){
                String stat = trainReal.getStatistics().toString();
                outputWriter.write(stat);
                outputWriter.write(", ");
            }
            outputWriter.newLine();

            logger.log(Level.FINE, "Successfully Added Line to " + path + " CSV File");

            outputWriter.flush();
            outputWriter.close();
        }catch (Exception e){
            logger.log(Level.WARNING, "Error with " + path + " CSV File " + e.getMessage());
        }
    }

    /**
     * Save in JSON format the trajectory and the generated part of it
     * @param combineInputList List of {@link TrainReal}
     * @param path path where to save
     */
    private void dumpInfo(List<TrainReal> combineInputList, String path){

        try (FileOutputStream zipFile = new FileOutputStream(new File(path));
             ZipOutputStream zos = new ZipOutputStream(zipFile);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(zos, "UTF-8"))
        ){
            ZipEntry csvFile = new ZipEntry(  "trajectory-generatedPoints.json");
            zos.putNextEntry(csvFile);

            JSONObject totalObj = new JSONObject();

            for(int i = 0; i < combineInputList.size(); i ++){

                TrainReal el = combineInputList.get(i);
                JSONObject obj = new JSONObject();

                JSONArray generated = new JSONArray();
                for(PointWithBearing p: el.getRealPointsOutputComputed()){
                    generated.add(p.toJson());
                }
                obj.put("generated", generated);

                JSONArray real = new JSONArray();
                for(PointWithBearing p: el.getFollowingPart()){
                    real.add(p.toJson());
                }
                obj.put("real", real);

                JSONArray computed = new JSONArray();
                for(InputsNetwork p: el.getFollowingPartTransformed()){
                    computed.add(p.toJson());
                }
                obj.put("following", computed);

                obj.put("classification", el.getFitnessGivenByTheClassifier());

                obj.put("id", el.getId().toString());

                String name = "trajectory-" + i;
                totalObj.put(name, obj);
            }
            totalObj.put("git-sha-1=", PropertiesFileReader.getGitSha1());
            totalObj.put("size", combineInputList.size());
            try {
                writer.write(totalObj.toJSONString());
                writer.newLine();
            } catch (IOException e) {
                logger.log(Level.WARNING, "Error appending line to trajectory-generatedPoints CSV File-" + path + " " + e.getMessage());
                e.printStackTrace();
            }

        }catch (Exception e){
            logger.log(Level.WARNING, "Error with trajectory-generatedPoints Zip File-" + path + " " + e.getMessage());
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
        this.dumpInfo(combineInputList, path);
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
            for(Scores s: scores){
                allTheScores.add(s.toString());
            }
            obj.put("git-sha-1=", PropertiesFileReader.getGitSha1());
            obj.put("scores", allTheScores);

            try {
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


    /**
     * Save the max fitness achievable by the agent and the classifier
     * @param fitness value of the fitness
     * @param name agent or classfier
     */
    private void saveMaxFitnessAchievable(int fitness, String name){
        String data = name + " -> " + fitness + "; \n";
        String path = this.currentPath + "maxFitnessAchievable.txt";
        File file = new File(path);

        try {
            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }
            // true = append file
            FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(data);
            bw.close();

            fw.close();
        } catch (IOException e) {
            logger.log(Level.WARNING, "Error in saving the max fitness achievable " + e.getMessage());
        }
    }


    /**
     * Save what the classifiers does with the real trajectories
     * @param resultsRealTrajectories real trajectories results
     */
    private void saveResultRealClassifier(HashMap<Integer, Map<Integer, Map<UUID, Double>>> resultsRealTrajectories){
        String path = this.currentPath + "classificationRealTrajectories.txt";
        File file = new File(path);

        try {
            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }
            // true = append file
            FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(resultsRealTrajectories.toString());
            bw.write("\n");
            bw.close();

            fw.close();
        } catch (IOException e) {
            logger.log(Level.WARNING, "Error in saving the classificationRealTrajectories " + e.getMessage());
        }
    }


    /**
     * Save only the trajectories
     * @param trajectories all the {@link TrainReal} files
     */
    private void saveTrajectory(List<TrainReal> trajectories) {
        //do I have to save it?
        boolean save = false;
        if (this.uuids.isEmpty()) {
            this.uuids.addAll(trajectories);
            save = true;
        } else {
            for (TrainReal tr : trajectories) {
                UUID uuid = tr.getId();
                if (this.uuids.stream().noneMatch(id -> id.getId().equals(uuid))) {
                    save = true;
                    this.uuids.add(tr);
                }
            }
        }
        if (save) {
            String path = this.currentPath + "trajectory.zip";
            File f = new File(path);
            //if exist erase
            if (f.exists() && !f.isDirectory()) {
                try {
                    Files.delete(Paths.get(path));
                } catch (IOException e) {
                    logger.log(Level.WARNING, "Error deleting file 'trajectory.zip' " + e.getMessage());
                    e.printStackTrace();
                }
            }

            try (FileOutputStream zipFile = new FileOutputStream(new File(path));
                 ZipOutputStream zos = new ZipOutputStream(zipFile);
                 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(zos, "UTF-8"))
            ){
                ZipEntry csvFile = new ZipEntry(  "trajectory.json");
                zos.putNextEntry(csvFile);



                JSONObject obj = new JSONObject();
                JSONArray allTheTra = new JSONArray();

                for(TrainReal tr: this.uuids){
                    JSONObject subObj = new JSONObject();
                    subObj.put("id", tr.getId().toString());
                    JSONArray allThePoints = new JSONArray();
                    for(PointWithBearing p: tr.getPoints()){
                        allThePoints.add(p.toJson());
                    }
                    subObj.put("points", allThePoints);
                    JSONArray allTheInputs = new JSONArray();
                    for(InputsNetwork in: tr.getTrainingPoint()){
                        allTheInputs.add(in.toJson());
                    }
                    subObj.put("inputs", allTheInputs);
                    allTheTra.add(subObj);
                }
                obj.put("git-sha-1=", PropertiesFileReader.getGitSha1());
                obj.put("trajectories", allTheTra);


                try {
                    writer.write(obj.toJSONString());
                    writer.newLine();
                } catch (IOException e) {
                    logger.log(Level.WARNING, "Error appending line to trajectory.json-" + path + " " + e.getMessage());
                    e.printStackTrace();
                }

            }catch (Exception e){
                logger.log(Level.WARNING, "Error with trajectory.json Zip File-" + path + " " + e.getMessage());
            }
        }
    }


}

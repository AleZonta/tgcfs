package tgcfs.Performances;

import java.io.*;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private static final Logger logger = Logger.getLogger(SaveToFile.class.getName()); //logger for this class


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
         */
        public Saver(String name, String experiment){
            instance = new SaveToFile(name, experiment);
        }

        /**
         * Constructor with three parameters
         * It calls the private constructor of the SaveToFile class
         * @param name name of the experiment
         * @param experiment number of the experiment
         * @param path path where to save
         */
        public Saver(String name, String experiment, String path){
            instance = new SaveToFile(name, experiment, path);
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
         * Save all the configuration in the same location where all the results are
         * @param setting config files
         */
        public static void dumpSetting(Object setting){
            instance.dumpConfig(setting);
        }

        /**
         * Append  line to the file saving the best genome
         * It calls the private method to do that of the SaveToFile class
         * @param name name of the class/file I am saving
         * @param genoma list with the double value of the genoma
         * @throws Exception  if the class is not instantiate
         */
        public static void saveBestGenoma(String name, List<Double> genoma) throws Exception {
            if(instance == null) throw new Exception("Cannot save, the class is not instantiate");
            instance.saveGenome(name, genoma);
        }
    }




    /**
     * Constructor with two parameters
     * It creates a folder called Experiment + name and then inside folder related to experiments
     * This method localises the path where the program is located
     * @param name name of the experiment
     * @param experiment number of the experiment
     */
    private SaveToFile(String name, String experiment){
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
     */
    private SaveToFile(String name, String experiment, String path){
        this.currentPath = path + "/Experiment-" + name;
        new File(this.currentPath).mkdirs();
        this.currentPath += "/" + experiment;
        new File(this.currentPath).mkdirs();
        this.currentPath += "/";
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

    /**
     * Save the genome of the best individual
     * @param name name of the class/file I am saving
     * @param genome list with the double value of the genome
     */
    private void saveGenome(String name, List<Double> genome){
        try {
            BufferedWriter outputWriter = new BufferedWriter(new FileWriter(this.currentPath + name + ".-genome.csv", true));
            genome.forEach(gene -> {
                try {
                    outputWriter.write(Double.toString(gene) + ", ");
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

}

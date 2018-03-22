package tgcfs.EA.Helpers;

import org.apache.commons.math3.random.MersenneTwister;
import tgcfs.Config.ReadConfig;
import tgcfs.EA.Algorithm;
import tgcfs.EA.Individual;
import tgcfs.EA.Mutation.NonUniformMutation;
import tgcfs.EA.Mutation.RandomResetting;
import tgcfs.EA.Mutation.UncorrelatedMutation;
import tgcfs.NN.EvolvableModel;
import tgcfs.Utils.IndividualStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

/**
 * Created by Alessandro Zonta on 15/01/2018.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 *
 *
 * Class dealing with the hall of fame §
 * Rosin, C.D. and Belew, R.K. (1997). New methods for competitive coevolution, Evolutionary Computation 5(1): 1–29.
 */
public class HallOfFame {
    private List<Individual> hallOfFame; //save the best individual per every generation
    private int samplingSize; //how many element of the hall of fame do I use
    private static Logger logger;
    private List<Individual> sample;
    private Algorithm caller;
    private MersenneTwister rand;

    /**
     * Constructor
     * Reads max number of individual allowed from config file
     * Reads also sampling size
     * @param model {@link EvolvableModel} to use in initialisation random individual
     * @param log logger
     * @param caller {@link Algorithm} that calls the Hall Of Fame
     * @param seedRnd seed for the random generator for the HoF
     * @exception Exception if there are problem in reading the file
     */
    public HallOfFame(EvolvableModel model, Logger log, Algorithm caller, int seedRnd) throws Exception {
        logger = log;
        this.rand = new MersenneTwister(seedRnd);
        this.caller = caller;
        this.hallOfFame = new ArrayList<>();
        int memory = ReadConfig.Configurations.getHallOfFameMemory();
        this.samplingSize = ReadConfig.Configurations.getHallOfFameSample();
        //fill it up with random element
        for(int i = 0; i < memory; i++){
            Individual newBorn;
            switch(ReadConfig.Configurations.getMutation()){
                case 0:
                    newBorn = new UncorrelatedMutation(model.getArrayLength(), IndividualStatus.RANDOM);
                    break;
                case 1:
                    newBorn = new RandomResetting(model.getArrayLength(), IndividualStatus.RANDOM);
                    break;
                case 2:
                    newBorn = new NonUniformMutation(model.getArrayLength(), IndividualStatus.RANDOM);
                    break;
                default:
                    newBorn = new NonUniformMutation(model.getArrayLength(), IndividualStatus.RANDOM);
                    break;
            }
            newBorn.setModel(model.deepCopy());
            this.hallOfFame.add(newBorn);
        }
        this.sample = new ArrayList<>();
        logger.log(Level.FINE, "--- Creation Hall of Fame (" + this.caller.toString() + ") ---");
    }

    /**
     * Constructor
     * Reads max number of individual allowed from config file
     * Reads also sampling size
     * @param model {@link EvolvableModel} to use in initialisation random individual
     * @param log logger
     * @exception Exception if there are problem in reading the file
     */
    public HallOfFame(EvolvableModel model, Logger log) throws Exception {
        logger = log;
        this.caller = caller;
        this.hallOfFame = new ArrayList<>();
        int memory = ReadConfig.Configurations.getHallOfFameMemory();
        this.samplingSize = ReadConfig.Configurations.getHallOfFameSample();
        //fill it up with random element
        for(int i = 0; i < memory; i++){
            Individual newBorn;
            switch(ReadConfig.Configurations.getMutation()){
                case 0:
                    newBorn = new UncorrelatedMutation(model.getArrayLength(), IndividualStatus.RANDOM);
                    break;
                case 1:
                    newBorn = new RandomResetting(model.getArrayLength(), IndividualStatus.RANDOM);
                    break;
                case 2:
                    newBorn = new NonUniformMutation(model.getArrayLength(), IndividualStatus.RANDOM);
                    break;
                default:
                    newBorn = new NonUniformMutation(model.getArrayLength(), IndividualStatus.RANDOM);
                    break;
            }
            newBorn.setModel(model.deepCopy());
            this.hallOfFame.add(newBorn);
        }
        this.sample = new ArrayList<>();
        logger.log(Level.FINE, "--- Creation Hall of Fame (" + this.caller.toString() + ") ---");
    }

    /**
     * Add a new individual in the hall of fame
     * If the hall of fame is full remove one to make room for the new one
     * The room is made by drawing a random individual to replace
     * @param individual {@link Individual} to add to the hall of fame
     */
    public void addIndividual(Individual individual){
        //remove all the random firs
        int i = 0;
        if(this.hallOfFame.stream().anyMatch(ind -> ind.getIndividualStatus().equals(IndividualStatus.RANDOM))){
            while(!this.hallOfFame.get(i).getIndividualStatus().equals(IndividualStatus.RANDOM)){
                i++;
            }
        }else {
            i = this.rand.nextInt(this.hallOfFame.size());
        }
        this.hallOfFame.set(i, individual.deepCopy());
        logger.log(Level.FINE, "--- Individual added to the Hall of Fame (" + this.caller.toString() + ") ---");
    }

    /**
     * Create the sample of the Hall of Fame
     */
    public void createSample() {
        List<Integer> id = new ArrayList<>();
        //optimise the generation of random positions
        //if the sampling size is smaller than half of the size, just create different random position
        if (this.samplingSize <= this.hallOfFame.size() / 2) {
            while (id.size() < this.samplingSize) {
                int value = this.rand.nextInt(this.hallOfFame.size());
                if (id.stream().noneMatch(integer -> integer.equals(value))) {
                    id.add(value);
                }
            }
        } else {
            //if sampling size is more than half of the size but smaller the the total size, just remove random id
            if (this.hallOfFame.size() / 2 < this.samplingSize && this.samplingSize <= this.hallOfFame.size()) {
                IntStream.range(0, this.hallOfFame.size()).forEach(id::add);
                while (id.size() > this.samplingSize) {
                    int value = this.rand.nextInt(this.hallOfFame.size());
                    if (id.stream().anyMatch(t -> t == value)) {
                        id.remove(value);
                    }
                }
            }
        }
        List<Individual> returnList = new ArrayList<>();
        id.forEach(integer -> returnList.add(this.hallOfFame.get(integer)));
        this.sample = new ArrayList<>(returnList);

        List<Integer> ids = new ArrayList<>();
        returnList.forEach(individual -> ids.add(individual.getModel().getId()));
        logger.log(Level.FINE, "--- Hall of Fame sample created (" + this.caller.toString() + ") ---" + "\n" + ids.toString());
    }

    /**
     * Get all the Hall of Fame
     * @return list of {@link Individual}
     */
    public List<Individual> getHallOfFame() {
        return this.hallOfFame;
    }

    /**
     * Getter for the last sample used
     * @return  list of {@link Individual}
     */
    public List<Individual> getSample() {
        logger.log(Level.FINE, "--- Hall of Fame sample requested (" + this.caller.toString() + ") ---");
        if(this.sample.isEmpty()){
            this.createSample();
        }
        return this.sample;
    }

    /**
     * Getter for an {@link Individual} random chosen from the sample
     * @return {@link Individual} random chosen
     */
    public Individual getRandomIndividualFromSample(){
        int value = this.rand.nextInt(samplingSize);
        return this.sample.get(value);
    }

}

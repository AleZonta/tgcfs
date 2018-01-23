package tgcfs.EA;

import tgcfs.Config.ReadConfig;
import tgcfs.EA.Mutation.NonUniformMutation;
import tgcfs.EA.Mutation.RandomResetting;
import tgcfs.EA.Mutation.UncorrelatedMutation;
import tgcfs.NN.EvolvableModel;
import tgcfs.Utils.IndividualStatus;
import tgcfs.Utils.RandomGenerator;

import java.util.ArrayList;
import java.util.List;
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
 * Class dealing with the hall of fame system
 * Rosin, C.D. and Belew, R.K. (1997). New methods for competitive coevolution, Evolutionary Computation 5(1): 1–29.
 */
public class HallOfFame {
    private List<Individual> hallOfFame; //save the best individual per every generation
    private int samplingSize; //how many element of the hall of fame do I use
    private List<Individual> sample;

    /**
     * Constructor
     * Reads max number of individual allowed from config file
     * Reads also sampling size
     * @param model {@link EvolvableModel} to use in initialisation random individual
     * @exception Exception if there are problem in reading the file
     */
    public HallOfFame(EvolvableModel model) throws Exception {
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
            i = RandomGenerator.getNextInt(0, this.hallOfFame.size());
        }
        this.hallOfFame.set(i, individual.deepCopy());
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
                int value = RandomGenerator.getNextInt(0, this.hallOfFame.size());
                if (id.stream().noneMatch(integer -> integer.equals(value))) {
                    id.add(value);
                }
            }
        } else {
            //if sampling size is more than half of the size but smaller the the total size, just remove random id
            if (this.hallOfFame.size() / 2 < this.samplingSize && this.samplingSize <= this.hallOfFame.size()) {
                IntStream.range(0, this.hallOfFame.size()).forEach(id::add);
                while (id.size() > this.samplingSize) {
                    int value = RandomGenerator.getNextInt(0, this.hallOfFame.size());
                    if (id.stream().anyMatch(t -> t == value)) {
                        id.remove(value);
                    }
                }
            }
        }
        List<Individual> returnList = new ArrayList<>();
        id.forEach(integer -> returnList.add(this.hallOfFame.get(integer)));
        this.sample = new ArrayList<>(returnList);
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
        if(this.sample.isEmpty()){
            this.createSample();
        }
        return this.sample;
    }
}

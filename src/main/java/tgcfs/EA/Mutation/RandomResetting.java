package tgcfs.EA.Mutation;

import org.nd4j.linalg.api.ndarray.INDArray;
import tgcfs.EA.Individual;
import tgcfs.Loader.TrainReal;
import tgcfs.NN.EvolvableNN;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

/**
 * Created by Alessandro Zonta on 26/06/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 *
 * implements a random resetting mutation for the individual
 */
public class RandomResetting extends Individual {
    /**
     * Zero parameter constructor
     * Everything goes to null
     */
    public RandomResetting(){
        super();
    }

    /**
     * Two parameter constructor and set to 0 the fitness
     * @param objPar objectiveParameters list
     */
    public RandomResetting(INDArray objPar){
        super(objPar);
    }

    /**
     * One parameter constructor
     * It is loading the objective parameters list with random number
     * and the mutation strengths list with 1.0
     * @param size size of the objectiveParameter
     * @exception Exception if there are problems with the reading of the seed information
     */
    public RandomResetting(Integer size) throws Exception {
        super(size);
    }

    /**
     * One parameter constructor
     * It is loading the objective parameters list with random number
     * and the mutation strengths list with 1.0
     * @param size size of the objectiveParameter
     * @param model model to assign to the individual
     * @exception Exception if there are problems with the reading of the seed information
     */
    public RandomResetting(Integer size, EvolvableNN model) throws Exception {
        super(size, model);
    }

    /**
     * four parameters constructor
     * @param objPar objectiveParameters list
     * @param fitness fitness
     * @param model model to assign to the individual
     * @param myInputandOutput input output last
     */
    public RandomResetting(INDArray objPar, Integer fitness, EvolvableNN model, List<TrainReal> myInputandOutput){
        super(objPar, fitness, model, myInputandOutput);
    }


    /**
     * Implementation abstract method mutate from individual
     *
     * Random Resetting is an extension of the bit flip for the integer representation.
     * In this, a random value from the set of permissible values is assigned to a randomly chosen gene.
     *
     * @param n is the length of the genome
     */
    @Override
    public void mutate(Integer n) {
        //generate random number of gene that I will mutate
        Integer top = n / 4;
        Integer rand = ThreadLocalRandom.current().nextInt(1, top + 1);

        IntStream.range(0,rand).forEach(el -> {
            //generate random position to mutate
            Integer pos = ThreadLocalRandom.current().nextInt(super.getObjectiveParameters().columns());
            Double newValue = ThreadLocalRandom.current().nextDouble(-4,4);
            super.getObjectiveParameters().putScalar(pos,newValue);
        });
    }


    /**
     * Deep copy function
     * @return RandomResetting object
     */
    public RandomResetting deepCopy(){
        return new RandomResetting(this.getObjectiveParameters(), new Integer(this.getFitness()), this.getModel().deepCopy(), this.getMyInputandOutput());
    }
}

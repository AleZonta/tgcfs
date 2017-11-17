package tgcfs.EA.Mutation;

import org.nd4j.linalg.api.ndarray.INDArray;
import tgcfs.EA.Individual;
import tgcfs.Loader.TrainReal;
import tgcfs.NN.EvolvableModel;
import tgcfs.Utils.IndividualStatus;
import tgcfs.Utils.RandomGenerator;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
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
     * @param ind kind of individual I am creating
     */
    public RandomResetting(INDArray objPar, IndividualStatus ind){
        super(objPar, ind);
    }

    /**
     * Three parameter constructor
     * @param objPar objectiveParameters list
     * @param ind kind of individual I am creating
     * @param isSon boolean variable if the individual is a son
     * @exception Exception if there are problems with the reading of the seed information
     */
    public RandomResetting(INDArray objPar, IndividualStatus ind, boolean isSon) throws Exception {
        super(objPar, ind, isSon);
    }

    /**
     * One parameter constructor
     * It is loading the objective parameters list with random number
     * and the mutation strengths list with 1.0
     * @param size size of the objectiveParameter
     * @exception Exception if there are problems with the reading of the seed information
     */
    public RandomResetting(int size) throws Exception {
        super(size);
    }

    /**
     * One parameter constructor
     * It is loading the objective parameters list with random number
     * and the mutation strengths list with 1.0
     * @param size size of the objectiveParameter
     * @param ind kind of individual I am creating
     * @exception Exception if there are problems with the reading of the seed information
     */
    public RandomResetting(int size, IndividualStatus ind) throws Exception {
        super(size, ind);
    }

    /**
     * One parameter constructor
     * It is loading the objective parameters list with random number
     * and the mutation strengths list with 1.0
     * @param size size of the objectiveParameter
     * @param model model to assign to the individual
     * @param ind kind of individual I am creating
     * @exception Exception if there are problems with the reading of the seed information
     */
    public RandomResetting(int size, EvolvableModel model, IndividualStatus ind) throws Exception {
        super(size, model, ind);
    }

    /**
     * four parameters constructor
     * @param objPar objectiveParameters list
     * @param fitness fitness
     * @param model model to assign to the individual
     * @param myInputandOutput input output last
     * @param ind kind of individual I am creating
     */
    public RandomResetting(INDArray objPar, AtomicInteger fitness, EvolvableModel model, List<TrainReal> myInputandOutput, IndividualStatus ind){
        super(objPar, fitness, model, myInputandOutput, ind);
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
    public void mutate(int n) {
        //generate random number of gene that I will mutate
        int top = n / 4;
        int rand = RandomGenerator.getNextInt(1, top+1);

        IntStream.range(0,rand).forEach(el -> {
            //generate random position to mutate
            int pos = RandomGenerator.getNextInt(0,super.getObjectiveParameters().columns());
            double newValue = RandomGenerator.getNextDouble(-4,4);
            super.getObjectiveParameters().putScalar(pos,newValue);
        });
    }


    /**
     * Deep copy function
     * @return RandomResetting object
     */
    public RandomResetting deepCopy(){
        return new RandomResetting(this.getObjectiveParameters(), new AtomicInteger(this.getFitness()), this.getModel().deepCopy(), this.getMyInputandOutput(), this.ind);
    }
}

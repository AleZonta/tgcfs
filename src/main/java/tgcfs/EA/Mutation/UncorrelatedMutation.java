package tgcfs.EA.Mutation;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
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
 * Implementation of the uncorrelated mutation of an individual
 */
public class UncorrelatedMutation extends Individual {
    private INDArray mutationStrengths;

    /**
     * Getter fot the mutation strengths
     * @return list of double
     */
    public INDArray getMutationStrengths() {
        return this.mutationStrengths;
    }

    /**
     * Zero parameter constructor
     * Everything goes to null
     */
    public UncorrelatedMutation(){
        super();
        this.mutationStrengths = null;
    }

    /**
     * three parameter constructor and set to 0 the fitness
     * @param objPar objectiveParameters list
     * @param mutStr mutationStrengths list
     * @param ind kind of individual I am creating
     */
    public UncorrelatedMutation(INDArray objPar,INDArray mutStr, IndividualStatus ind){
        super(objPar, ind);
        this.mutationStrengths = mutStr;
    }

    /**
     * four parameter constructor
     * @param objPar objectiveParameters list
     * @param mutStr mutationStrengths list
     * @param ind kind of individual I am creating
     * @param isSon boolean variable if the individual is a son
     * @exception Exception if there are problems with the reading of the seed information
     */
    public UncorrelatedMutation(INDArray objPar,INDArray mutStr, IndividualStatus ind, boolean isSon) throws Exception {
        super(objPar, ind, isSon);
        this.mutationStrengths = mutStr;
    }

    /**
     * One parameter constructor
     * It is loading the objective parameters list with random number
     * and the mutation strengths list with 1.0
     * @param size size of the objectiveParameter
     * @exception Exception if there are problems with the reading of the seed information
     */
    public UncorrelatedMutation(int size) throws Exception {
        super(size);
        this.mutationStrengths = Nd4j.ones(size);
    }

    /**
     * One parameter constructor
     * It is loading the objective parameters list with random number
     * and the mutation strengths list with 1.0
     * @param size size of the objectiveParameter
     * @param ind kind of individual I am creating
     * @exception Exception if there are problems with the reading of the seed information
     */
    public UncorrelatedMutation(int size, IndividualStatus ind) throws Exception {
        super(size, ind);
        this.mutationStrengths = Nd4j.ones(size);
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
    public UncorrelatedMutation(int size, EvolvableModel model, IndividualStatus ind) throws Exception {
        super(size, model, ind);
        this.mutationStrengths = Nd4j.ones(size);
    }

    /**
     * four parameters constructor
     * @param objPar objectiveParameters list
     * @param fitness fitness
     * @param model model to assign to the individual
     * @param myInputandOutput input output last
     * @param ind kind of individual I am creating
     * @param isSon boolean variable if the individual is a son
     */
    public UncorrelatedMutation(INDArray objPar, AtomicInteger fitness, EvolvableModel model, List<TrainReal> myInputandOutput, IndividualStatus ind, boolean isSon){
        super(objPar, fitness, model, myInputandOutput, ind, isSon);
    }

    /**
     * Implementation abstract method mutate from individual
     *
     * Method that mutates the individual
     *
     * Since I am using the Uncorrelated mutation with n σ’s, I have two learning rate parameters:
     * τ’∝1/(2n)1⁄2 and τ∝1/(2n1⁄2)1⁄2
     *
     * firstly I generate the perturbed mutation strength from the original one according to a log-normal distribution.
     * secondly I mutate the objective parameter according to a normal distribution having the perturbed mutation strength as its variance.
     *
     * @param n is the length of the genome
     */
    @Override
    public void mutate(int n) {
        //two learning rate parameters
        double p1 = 1 / (2 * Math.sqrt( 2 * n));
        double p2 = 1 / (2 * Math.sqrt( 2 * Math.sqrt(n)));

        //random Double general per each individual
        double rand1 = RandomGenerator.getNextDouble();

        //first mutate the list of mutation strengths
        IntStream.range(0, this.mutationStrengths.columns()).forEach(i -> {
            //random Double generated separately for each element within each individual
            double randw = RandomGenerator.getNextDouble();
            //obtain the new mutation value
            double newMutation = this.mutationStrengths.getDouble(i) * Math.exp(p1 * rand1 + p2 * randw);
            //substitute the old one with the new one
            this.mutationStrengths.putScalar(i, newMutation);
        });

        //after having mutate all the mutation strengths it is time to mutate the actual objective parameters
        IntStream.range(0, super.getObjectiveParameters().columns()).forEach(i -> {
            //random Double generated separately for each element within each individual
            double randw = RandomGenerator.getNextDouble();
            double newObj = super.getObjectiveParameters().getDouble(i) + this.mutationStrengths.getDouble(i) * randw;
            super.getObjectiveParameters().putScalar(i, newObj);
        });
    }

    /**
     * Deep copy function
     * @return UncorrelatedMutation object
     */
    public UncorrelatedMutation deepCopy(){
        return new UncorrelatedMutation(this.getObjectiveParameters(), new AtomicInteger(this.getFitness()), this.getModel().deepCopy(), this.getMyInputandOutput(), this.ind, this.isSon());
    }

}

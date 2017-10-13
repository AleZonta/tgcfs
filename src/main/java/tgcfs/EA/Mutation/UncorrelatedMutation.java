package tgcfs.EA.Mutation;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import tgcfs.EA.Individual;
import tgcfs.Loader.TrainReal;
import tgcfs.NN.EvolvableModel;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
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
     * Two parameter constructor and set to 0 the fitness
     * @param objPar objectiveParameters list
     * @param mutStr mutationStrengths list
     */
    public UncorrelatedMutation(INDArray objPar,INDArray mutStr){
        super(objPar);
        this.mutationStrengths = mutStr;
    }

    /**
     * One parameter constructor
     * It is loading the objective parameters list with random number
     * and the mutation strengths list with 1.0
     * @param size size of the objectiveParameter
     * @exception Exception if there are problems with the reading of the seed information
     */
    public UncorrelatedMutation(Integer size) throws Exception {
        super(size);
        this.mutationStrengths = Nd4j.ones(size);
    }

    /**
     * One parameter constructor
     * It is loading the objective parameters list with random number
     * and the mutation strengths list with 1.0
     * @param size size of the objectiveParameter
     * @param model model to assign to the individual
     * @exception Exception if there are problems with the reading of the seed information
     */
    public UncorrelatedMutation(Integer size, EvolvableModel model) throws Exception {
        super(size, model);
        this.mutationStrengths = Nd4j.ones(size);
    }

    /**
     * four parameters constructor
     * @param objPar objectiveParameters list
     * @param fitness fitness
     * @param model model to assign to the individual
     * @param myInputandOutput input output last
     */
    public UncorrelatedMutation(INDArray objPar, AtomicInteger fitness, EvolvableModel model, List<TrainReal> myInputandOutput){
        super(objPar, fitness, model, myInputandOutput);
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
    public void mutate(Integer n)
        {
        //two learning rate parameters
        Double p1 = 1 / (2 * Math.sqrt( 2 * n));
        Double p2 = 1 / (2 * Math.sqrt( 2 * Math.sqrt(n)));

        //random Double general per each individual
        Double rand1 = ThreadLocalRandom.current().nextDouble();

        //first mutate the list of mutation strengths
        IntStream.range(0, this.mutationStrengths.columns()).forEach(i -> {
            //random Double generated separately for each element within each individual
            Double randw = ThreadLocalRandom.current().nextDouble();
            //obtain the new mutation value
            Double newMutation = this.mutationStrengths.getDouble(i) * Math.exp(p1 * rand1 + p2 * randw);
            //substitute the old one with the new one
            this.mutationStrengths.putScalar(i, newMutation);
        });

        //after having mutate all the mutation strengths it is time to mutate the actual objective parameters
        IntStream.range(0, super.getObjectiveParameters().columns()).forEach(i -> {
            //random Double generated separately for each element within each individual
            Double randw = ThreadLocalRandom.current().nextDouble();
            Double newObj = super.getObjectiveParameters().getDouble(i) + this.mutationStrengths.getDouble(i) * randw;
            super.getObjectiveParameters().putScalar(i, newObj);
        });
    }

    /**
     * Deep copy function
     * @return UncorrelatedMutation object
     */
    public UncorrelatedMutation deepCopy(){
        return new UncorrelatedMutation(this.getObjectiveParameters(), new AtomicInteger(this.getFitness()), this.getModel().deepCopy(), this.getMyInputandOutput());
    }

}

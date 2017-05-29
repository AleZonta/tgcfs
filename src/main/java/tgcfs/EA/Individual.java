package tgcfs.EA;

import tgcfs.NN.OutputsNetwork;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Alessandro Zonta on 29/05/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 *
 * This class implements an individual
 */
public class Individual {
    private List<Double> objectiveParameters;
    private List<Double> mutationStrengths;
    private Double fitness;
    private List<OutputsNetwork> output;

    /**
     * Getter fot the objective parameter
     * @return list of double
     */
    public List<Double> getObjectiveParameters() {
        return this.objectiveParameters;
    }

    /**
     * Getter fot the mutation strengths
     * @return list of double
     */
    public List<Double> getMutationStrengths() {
        return this.mutationStrengths;
    }

    /**
     * Getter fot the fitness
     * @return double value
     */
    public Double getFitness() {
        return this.fitness;
    }

    /**
     * Setter for fitness
     * @param fitness the value to assign to fitness
     */
    public void setFitness(Double fitness) {
        this.fitness = fitness;
    }


    /**
     * Zero parameter constructor
     * Everything goes to null
     */
    public Individual(){
        this.objectiveParameters = null;
        this.mutationStrengths = null;
        this.fitness = null;
    }

    /**
     * Two parameter constructor and set to 0 the fitness
     * @param objPar objectiveParameters list
     * @param mutStr mutationStrengths list
     */
    public Individual(List<Double> objPar, List<Double> mutStr){
        this.objectiveParameters = objPar;
        this.mutationStrengths = mutStr;
        this.fitness = 0.0;
    }

    /**
     * Getter for the list of Output
     * @return list of output
     */
    public List<OutputsNetwork> getOutput() {
        return this.output;
    }

    /**
     * Setter for the list of outputs
     * @param output the outputs
     */
    public void setOutput(List<OutputsNetwork> output) {
        this.output = output;
    }

    /**
     * One parameter constructor
     * It is loading the objective parameters list with random number
     * and the mutation strengths list with 1.0
     * @param size
     */
    public Individual(Integer size){
        this.objectiveParameters = new Random().doubles(size, -8.0, 8.0).collect(ArrayList::new,ArrayList::add, ArrayList::addAll);
        this.mutationStrengths = new ArrayList<>();
        for(int i=0; i<size; i++ ){
            this.mutationStrengths.add(1.0);
        }
        this.fitness = 0.0;
    }

    /**
     * Method to mutate the individual.
     *
     * Since I am using the Uncorrelated mutation with n σ’s, I have two learning rate parameters:
     * τ’∝1/(2n)1⁄2 and τ∝1/(2n1⁄2)1⁄2
     *
     * firstly I generate the perturbed mutation strength from the original one according to a log-normal distribution.
     * secondly I mutate the objective parameter according to a normal distribution having the perturbed mutation strength as its variance.
     *
     * @param n is the population size
     */
    public void mutate(Integer n){
        //two learning rate parameters
        Double p1 = 1 / (2 * Math.sqrt( 2 * n));
        Double p2 = 1 / (2 * Math.sqrt( 2 * Math.sqrt(n)));

        //random Double general per each individual
        Double rand1 = new Random().nextDouble();

        //first mutate the list of mutation strengths
        for(int i = 0; i < this.mutationStrengths.size(); i++){
            //random Double generated separately for each element within each individual
            Double randw = new Random().nextDouble();
            //obtain the new mutation value
            Double newMutation = this.mutationStrengths.get(i) * Math.exp(p1 * rand1 + p2 * randw);
            //substitute the old one with the new one
            this.mutationStrengths.set(i, newMutation);
        }

        //after having mutate all the mutation strengths it is time to mutate the actual objective parameters
        for(int i = 0; i < this.objectiveParameters.size(); i++){
            //random Double generated separately for each element within each individual
            Double randw = new Random().nextDouble();
            Double newObj = this.objectiveParameters.get(i) + this.mutationStrengths.get(i) * randw;
            this.objectiveParameters.set(i, newObj);
        }
    }


}

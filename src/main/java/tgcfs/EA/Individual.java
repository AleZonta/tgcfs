package tgcfs.EA;

import lgds.trajectories.Point;
import tgcfs.Config.ReadConfig;
import tgcfs.Loader.TrainReal;
import tgcfs.NN.EvolvableNN;
import tgcfs.NN.InputsNetwork;

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
public abstract class Individual {
    private List<Double> objectiveParameters;
    private List<TrainReal> myInputandOutput;
    private Integer fitness;
    private EvolvableNN model;

    /**
     * Getter fot the objective parameter
     * @return list of double
     */
    public List<Double> getObjectiveParameters() {
        return this.objectiveParameters;
    }

    /**
     * Getter fot the fitness
     * @return Integer value
     */
    public Integer getFitness() {
        return this.fitness;
    }

    /**
     * Setter for fitness
     * @param fitness the value to assign to fitness
     */
    public void setFitness(Integer fitness) {
        this.fitness = fitness;
    }


    /**
     * Zero parameter constructor
     * Everything goes to null
     */
    public Individual(){
        this.objectiveParameters = null;
        this.fitness = null;
        this.model = null;
        this.myInputandOutput = new ArrayList<>();
    }

    /**
     * Two parameter constructor and set to 0 the fitness
     * @param objPar objectiveParameters list
     */
    public Individual(List<Double> objPar){
        this.objectiveParameters = objPar;
        this.fitness = 0;
        this.model = null;
        this.myInputandOutput = new ArrayList<>();
    }

    /**
     * One parameter constructor
     * It is loading the objective parameters list with random number
     * and the mutation strengths list with 1.0
     * @param size size of the objectiveParameter
     * @exception Exception if there are problems with the reading of the seed information
     */
    public Individual(Integer size) throws Exception {
        this.objectiveParameters = new Random(ReadConfig.Configurations.getSeed()).doubles(size, -4.0, 4.0).collect(ArrayList::new,ArrayList::add, ArrayList::addAll);
        this.fitness = 0;
        this.model = null;
        this.myInputandOutput = new ArrayList<>();
    }

    /**
     * One parameter constructor
     * It is loading the objective parameters list with random number
     * and the mutation strengths list with 1.0
     * @param size size of the objectiveParameter
     * @param model model to assign to the individual
     * @exception Exception if there are problems with the reading of the seed information
     */
    public Individual(Integer size, EvolvableNN model) throws Exception {
        this.objectiveParameters = new Random(ReadConfig.Configurations.getSeed()).doubles(size, -4.0, 4.0).collect(ArrayList::new,ArrayList::add, ArrayList::addAll);
        this.fitness = 0;
        this.model = model;
        this.myInputandOutput = new ArrayList<>();
    }

    /**
     * Method to mutate the individual.
     * @param n is the population size
     */
    public abstract void mutate(Integer n);

    /**
     * Getter for the model of the individual
     * @return model
     */
    public EvolvableNN getModel() {
        return this.model;
    }

    /**
     * Setter for the model of the individual
     * @param model model to assign
     */
    public void setModel(EvolvableNN model) {
        this.model = model;
    }

    /**
     * Increase Fitness by one
     * @exception Exception if the individual is not initialised
     */
    public void increaseFitness() throws Exception {
        if(this.fitness == null) throw new Exception("Individual not correctly initialised");
        this.fitness++;
    }

    /**
     * Reset the fitness to zero
     */
    public void resetFitness(){
        this.fitness = 0;
    }

    /**
     * Train the model
     * @param input input needed to train the model
     * @param points the real points of the first part of the model
     * @exception Exception error in setting the weights
     */
    public void fitModel(List<InputsNetwork> input, List<Point> points) throws Exception {
        this.model.setWeights(this.objectiveParameters);
        this.model.fit(input, points);
        //return the weights
        this.objectiveParameters = this.model.getWeights();
    }

    /**
     * Getter for my input and output
     * @return  return the list of {@link TrainReal} objects
     */
    public List<TrainReal> getMyInputandOutput() {
        return this.myInputandOutput;
    }

    /**
     * Add one input output to the individual
     * @param myInputandOutput {@link TrainReal} object
     */
    public void addMyInputandOutput(TrainReal myInputandOutput) {
        this.myInputandOutput.add(myInputandOutput);
    }
}

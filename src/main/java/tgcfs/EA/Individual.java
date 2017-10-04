package tgcfs.EA;

import lgds.trajectories.Point;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import tgcfs.Loader.TrainReal;
import tgcfs.NN.EvolvableModel;
import tgcfs.NN.InputsNetwork;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

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
    private INDArray objectiveParameters;
    private List<TrainReal> myInputandOutput;
    private AtomicInteger fitness;
    private EvolvableModel model;

    /**
     * Getter fot the objective parameter
     * @return list of double
     */
    public INDArray getObjectiveParameters() {
        return this.objectiveParameters;
    }

    /**
     * Getter fot the fitness
     * @return Integer value
     */
    public Integer getFitness() {
        return this.fitness.intValue();
    }

    /**
     * Setter for fitness
     * @param fitness the value to assign to fitness
     */
    public void setFitness(Integer fitness) {
        this.fitness = new AtomicInteger(fitness);
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
    public Individual(INDArray objPar){
        this.objectiveParameters = objPar;
        this.fitness = new AtomicInteger(0);
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
    public Individual(int size) throws Exception {
        //this.objectiveParameters = ThreadLocalRandom.current().doubles(size, -4.0, 4.0).collect(ArrayList::new,ArrayList::add, ArrayList::addAll);
        this.objectiveParameters = Nd4j.rand(1, size);
        for(int j = 0; j< size; j++){
            this.objectiveParameters.putScalar(j, ThreadLocalRandom.current().nextDouble(-1,1));
        }
        this.fitness = new AtomicInteger(0);
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
    public Individual(int size, EvolvableModel model) throws Exception {
        this.objectiveParameters = Nd4j.rand(1, size);
        for(int j = 0; j< size; j++){
            this.objectiveParameters.putScalar(j, ThreadLocalRandom.current().nextDouble(-1,1));
        }
        this.fitness = new AtomicInteger(0);
        this.model = model;
        this.myInputandOutput = new ArrayList<>();
    }

    /**
     * four parameters constructor
     * @param objPar objectiveParameters list
     * @param fitness fitness
     * @param model model to assign to the individual
     * @param myInputandOutput input output last
     */
    public Individual(INDArray objPar, AtomicInteger fitness, EvolvableModel model, List<TrainReal> myInputandOutput){
        this.objectiveParameters = objPar;
        this.fitness = fitness;
        this.model = model.deepCopy();
        this.myInputandOutput = myInputandOutput;
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
    public EvolvableModel getModel() {
        return this.model;
    }

    /**
     * Setter for the model of the individual
     * @param model model to assign
     */
    public void setModel(EvolvableModel model) {
        this.model = model;
    }

    /**
     * Increase Fitness by one
     * @exception Exception if the individual is not initialised
     */
    public void increaseFitness() throws Exception {
        if(this.fitness == null) throw new Exception("Individual not correctly initialised");
        this.fitness.incrementAndGet();
    }

    /**
     * Reset the fitness to zero
     */
    public void resetFitness(){
        this.fitness.set(0);
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
     * Train the model
     * @param dataSet dataset as input
     */
    public void fitModel(DataSet dataSet) throws Exception {
        this.model.setWeights(this.objectiveParameters);
        this.model.fit(dataSet);//return the weights
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

    /**
     * Reset the list of Input Output of the individual
     */
    public void resetInputOutput(){
        this.myInputandOutput = new ArrayList<>();
    }

    /**
     * Deep copy function
     * @return Individual object
     */
    public abstract Individual deepCopy();

}

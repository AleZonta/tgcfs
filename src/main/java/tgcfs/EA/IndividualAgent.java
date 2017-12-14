package tgcfs.EA;

import lgds.trajectories.Point;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.primitives.AtomicDouble;
import tgcfs.Agents.Models.LSTMAgent;
import tgcfs.EA.Mutation.StepSize;
import tgcfs.Loader.TrainReal;
import tgcfs.NN.InputsNetwork;
import tgcfs.Utils.IndividualStatus;
import tgcfs.Utils.RandomGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alessandro Zonta on 14/12/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 */
public class IndividualAgent {
    private INDArray objectiveParameters;
    private List<TrainReal> myInputandOutput;
    private AtomicDouble fitness;
    private LSTMAgent model;
    protected final IndividualStatus ind;
    private boolean isSon;


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
    public double getFitness() {
        return this.fitness.doubleValue();
    }

    /**
     * Setter for fitness
     * @param fitness the value to assign to fitness
     */
    public void setFitness(double fitness) {
        this.fitness = new AtomicDouble(fitness);
    }


    /**
     * Zero parameter constructor
     * Everything goes to null
     */
    public IndividualAgent(){
        this.objectiveParameters = null;
        this.fitness = null;
        this.model = null;
        this.myInputandOutput = new ArrayList<>();
        this.ind = null;
        this.isSon = false;
    }

    /**
     * Two parameter constructor and set to 0 the fitness
     * @param objPar objectiveParameters list
     * @param ind kind of individual I am creating
     */
    public IndividualAgent(INDArray objPar, IndividualStatus ind){
        this.objectiveParameters = objPar;
        this.fitness = new AtomicDouble(0);
        this.model = null;
        this.myInputandOutput = new ArrayList<>();
        this.ind = ind;
        this.isSon = false;
    }

    /**
     * Three parameter constructor and set to 0 the fitness
     * @param objPar objectiveParameters list
     * @param ind kind of individual I am creating
     * @param isSon boolean variable if the individual is a son
     */
    public IndividualAgent(INDArray objPar, IndividualStatus ind, boolean isSon){
        this.objectiveParameters = objPar;
        this.fitness = new AtomicDouble(0);
        this.model = null;
        this.myInputandOutput = new ArrayList<>();
        this.ind = ind;
        this.isSon = isSon;
    }

    /**
     * One parameter constructor
     * It is loading the objective parameters list with random number
     * and the mutation strengths list with 1.0
     * @param size size of the objectiveParameter
     * @exception Exception if there are problems with the reading of the seed information
     */
    public IndividualAgent(int size) throws Exception {
        //this.objectiveParameters = ThreadLocalRandom.current().doubles(size, -4.0, 4.0).collect(ArrayList::new,ArrayList::add, ArrayList::addAll);
        this.objectiveParameters = Nd4j.rand(1, size);
        for(int j = 0; j< size; j++){
            this.objectiveParameters.putScalar(j, RandomGenerator.getNextDouble(-1,1));
        }
        this.fitness = new AtomicDouble(0);
        this.model = null;
        this.myInputandOutput = new ArrayList<>();
        this.ind = null;
        this.isSon = false;
    }

    /**
     * One parameter constructor
     * It is loading the objective parameters list with random number
     * and the mutation strengths list with 1.0
     * @param size size of the objectiveParameter
     * @param ind kind of individual I am creating
     * @exception Exception if there are problems with the reading of the seed information
     */
    public IndividualAgent(int size, IndividualStatus ind) throws Exception {
        //this.objectiveParameters = ThreadLocalRandom.current().doubles(size, -4.0, 4.0).collect(ArrayList::new,ArrayList::add, ArrayList::addAll);
        this.objectiveParameters = Nd4j.rand(1, size);
        for(int j = 0; j< size; j++){
            this.objectiveParameters.putScalar(j, RandomGenerator.getNextDouble(-1,1));
        }
        this.fitness = new AtomicDouble(0);
        this.model = null;
        this.myInputandOutput = new ArrayList<>();
        this.ind = ind;
        this.isSon = false;
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
    public IndividualAgent(int size, LSTMAgent model, IndividualStatus ind) throws Exception {
        this.objectiveParameters = Nd4j.rand(1, size);
        for(int j = 0; j< size; j++){
            this.objectiveParameters.putScalar(j, RandomGenerator.getNextDouble(-1,1));
        }
        this.fitness = new AtomicDouble(0);
        this.model = model;
        this.myInputandOutput = new ArrayList<>();
        this.ind = ind;
        this.isSon = false;
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
    public IndividualAgent(INDArray objPar, AtomicDouble fitness, LSTMAgent model, List<TrainReal> myInputandOutput, IndividualStatus ind, boolean isSon){
        this.objectiveParameters = objPar;
        this.fitness = fitness;
        this.model = model.deepCopy();
        this.myInputandOutput = myInputandOutput;
        this.ind = ind;
        this.isSon = isSon;
    }

    /**
     * Getter for the model of the individual
     * @return model
     */
    public LSTMAgent getModel() {
        return this.model;
    }

    /**
     * Setter for the model of the individual
     * @param model model to assign
     */
    public void setModel(LSTMAgent model) {
        this.model = model;
    }


    /**
     * Increase Fitness by one
     * @exception Exception if the individual is not initialised
     */
    public synchronized void increaseFitness() throws Exception {
        if(this.fitness == null) throw new Exception("Individual not correctly initialised");
        this.fitness.addAndGet(1);
    }

    /**
     * Increase Fitness by ovalue
     * @param value value
     * @throws Exception if the individual is not initialised
     */
    public synchronized void increaseFitness(double value) throws Exception {
        if(this.fitness == null) throw new Exception("Individual not correctly initialised");
        this.fitness.addAndGet(value);
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
    public IndividualAgent deepCopy(){
        return new IndividualAgent(this.getObjectiveParameters(), new AtomicDouble(this.getFitness()), this.getModel().deepCopy(), this.getMyInputandOutput(), this.ind, this.isSon());
    }

    /**
     * getter for the property if the individual is a son
     * @return boolean value
     */
    public boolean isSon() {
        return this.isSon;
    }

    /**
     * The element is not son anymore
     */
    public void isParent() {
        this.isSon = false;
    }

    /**
     * Implementation abstract method mutate from individual
     *
     * normal practice to apply this operator with probability one per gene
     *
     * It needs a mutation step size parameter
     *
     * @param n is the genome length
     */
    public void mutate(int n) {
        double stepSize = 0d;
        try {
            if(this.ind == IndividualStatus.AGENT) {
                stepSize = StepSize.getStepSizeAgents();
            }else {
                stepSize = StepSize.getStepSizeClassifiers();
            }
        } catch (Exception ignored) { }
        for(int i = 0; i < this.objectiveParameters.columns(); i++){
            double newValue = this.objectiveParameters.getDouble(i) + stepSize * RandomGenerator.getNextDouble();
            //elastic bound
            if(newValue > 4d){
                double difference = newValue - 4d;
                newValue = 4d - difference;
            }
            if(newValue < -4){
                double difference = newValue - (-4d);
                newValue = -4 - difference;
            }
            this.objectiveParameters.putScalar(i, newValue);
        }
    }
}
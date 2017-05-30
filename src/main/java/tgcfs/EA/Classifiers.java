package tgcfs.EA;

import tgcfs.Classifiers.OutputNetwork;
import tgcfs.InputOutput.Transformation;
import tgcfs.NN.EvolvableNN;
import tgcfs.NN.InputsNetwork;
import tgcfs.NN.Models;
import tgcfs.NN.OutputsNetwork;

import java.util.List;
import java.util.logging.Level;
import java.util.stream.IntStream;

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
 * Class implementing the algorithm for the classifiers.
 */
public class Classifiers extends Algorithm {

    /**
     * Constructor zero parameter
     * Call the super constructor
     * @throws Exception if the super constructor has problem in reading the config files
     */
    public Classifiers() throws Exception {
        super();
    }

    /**
     * @implNote Implementation from Abstract class Algorithm
     * @param model the model of the population
     * @throws Exception if the reading of the config file goes wrong
     */
    @Override
    public void generatePopulation(EvolvableNN model) throws Exception {
        logger.log(Level.INFO, "Generating Classifiers Population...");
        IntStream.range(0, super.getConfigFile().getClassifierPopulationSize()).forEach(i ->{
            Individual newBorn = new Individual(model.getArrayLength());
            //assign the model to the classifier
            newBorn.setModel(model.deepCopy());
            super.addIndividual(newBorn);
        });
    }

    /**
     * @implNote Implementation from Abstract class Algorithm
     * @param input the input of the model
     * @throws Exception if there are problems in reading the info
     */
    @Override
    public void runIndividuals(List<InputsNetwork> input) throws Exception {
        throw new Exception("Method not usable for a Classifier");
    }


    /**
     * @implNote Implementation from Abstract class Algorithm
     * @param individual individual with the parameter of the classifier
     * @param input input to assign to the classifier
     * @return the output of the classifier
     * @throws Exception if the nn has problem an exception is raised
     */
    public OutputsNetwork runIndividual(Individual individual, List<InputsNetwork> input) throws Exception {
        //retrive model from the individual
        EvolvableNN model = individual.getModel();
        //set the weights
        model.setWeights(individual.getObjectiveParameters());
        //compute Output of the network
        List<Double> lastOutput = null;
        for (InputsNetwork inputsNetwork : input) {
            lastOutput = model.computeOutput(inputsNetwork.serialise());
        }
        //I am interested only in the last output of this network
        OutputNetwork out = new OutputNetwork();
        out.deserialise(lastOutput);
        return out;
    }


    /**
     * @implNote Implementation from Abstract class Algorithm
     * @param opponent competing population
     * @param transformation the class that will transform from one output to the new input
     */
    public void evaluateIndividuals(Algorithm opponent, Transformation transformation){
        //The fitness of each classifier is obtained by using it to evaluate each model in the competing population
        //For each correct judgement, the classifier’s fitness increases by one
        //I can do this directly in the other method
        throw new Error("Method not usable for a Classifier");
    }

    /**
     * Evaluate the classifier with the real agent
     * @param agent the real agent
     */
    public void evaluateRealAgent(Models agent){
        super.getPopulation().parallelStream().forEach(individual -> {
            //TODO transform output to input otherwise this will not work
            try {
                OutputNetwork result = (tgcfs.Classifiers.OutputNetwork) this.runIndividual(individual, null);
                //if the classifier is saying true -> it is correctly judging the agent
                if(result.getReal()){
                    individual.increaseFitness();
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Errors with the neural network" + e.getMessage());
            }
        });
    }

}

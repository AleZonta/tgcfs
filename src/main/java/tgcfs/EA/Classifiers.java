package tgcfs.EA;

import org.nd4j.linalg.api.ndarray.INDArray;
import tgcfs.Agents.Agent;
import tgcfs.Agents.Models.RealAgents;
import tgcfs.Classifiers.OutputNetwork;
import tgcfs.InputOutput.FollowingTheGraph;
import tgcfs.InputOutput.Transformation;
import tgcfs.Loader.TrainReal;
import tgcfs.NN.EvolvableModel;
import tgcfs.NN.InputsNetwork;
import tgcfs.NN.OutputsNetwork;

import java.util.List;
import java.util.logging.Level;

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
     * @param input the input of the model
     * @throws Exception if there are problems in reading the info
     */
    @Override
    public void runIndividuals(List<TrainReal> input) throws Exception {
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
        EvolvableModel model = individual.getModel();
        //set the weights
        model.setWeights(individual.getObjectiveParameters());
        //compute Output of the network
        INDArray lastOutput = null;
        for (InputsNetwork inputsNetwork : input) {
            //System.out.println("------- input ------");
            //System.out.println(inputsNetwork.serialise());
            lastOutput = model.computeOutput(inputsNetwork.serialise());
            //System.out.println("------- output ------");
            //System.out.println(lastOutput);
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
     * Train the network
     * @param combineInputList where to find the input to train
     */
    @Override
    public void trainNetwork(List<TrainReal> combineInputList) {
        throw new Error("Method not usable for a Classifier");
    }

    /**
     * Evaluate the classifier on the real agent
     * Each classifier is evaluated on the real agent oer "agent_population" times
     * @param agents the real agent
     */
    public void evaluateRealAgent(RealAgents agents, Transformation transformation){
        super.getPopulation().parallelStream().forEach(individual -> {
            try {
                //evaluate classifier with real agents
                agents.getRealAgents().forEach(agent -> {
                    try {
                        ((FollowingTheGraph)transformation).setLastPoint(agent.getLastPoint());
                        OutputNetwork result = (tgcfs.Classifiers.OutputNetwork) this.runIndividual(individual, transformation.transform(((Agent)agent).realOutput()));
                        //if the classifier is saying true -> it is correctly judging the agent
                        if(result.getReal()){
                            individual.increaseFitness();
                        }
                    } catch (Exception e) {
                        logger.log(Level.SEVERE, "Errors with the neural network" + e.getMessage());
                    }
                });
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error with the file" + e.getMessage());
            }
        });
    }

}

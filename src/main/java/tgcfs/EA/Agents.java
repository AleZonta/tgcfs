package tgcfs.EA;

import lgds.trajectories.Point;
import tgcfs.Agents.InputNetwork;
import tgcfs.Agents.OutputNetwork;
import tgcfs.Config.ReadConfig;
import tgcfs.InputOutput.FollowingTheGraph;
import tgcfs.InputOutput.Transformation;
import tgcfs.Loader.TrainReal;
import tgcfs.NN.EvolvableNN;
import tgcfs.NN.InputsNetwork;
import tgcfs.NN.OutputsNetwork;

import java.util.ArrayList;
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
 * Class implementing the algorithm for the agents.
 */
public class Agents extends Algorithm {

    /**
     * Constructor zero parameter
     * Call the super constructor
     * @throws Exception if the super constructor has problem in reading the config files
     */
    public Agents() throws Exception {
        super();
    }

    /**
     * @implNote Implementation from Abstract class Algorithm
     * @param input the input of the model
     * @throws Exception if there are problems in reading the info
     */
    @Override
    public void runIndividuals(List<TrainReal> input) throws Exception {
        logger.log(Level.INFO, "Running Agents...");

        //reset input
        super.getPopulation().forEach(Individual::resetInputOutput);

        super.getPopulation().parallelStream().forEach(individual -> {
            try {
                //retrieve model from the individual
                EvolvableNN model = individual.getModel();
                //set the weights
                model.setWeights(individual.getObjectiveParameters());

                //compute Output of the network
                List<Double> lastOutput = null;
                for (TrainReal inputsNetwork : input) {
                    for(InputsNetwork in : inputsNetwork.getTrainingPoint()){
                        lastOutput = model.computeOutput(in.serialise());
                    }

                    //now for the number of time step that I want to check save the output
                    List<OutputsNetwork> outputsNetworks = new ArrayList<>();

                    OutputNetwork out = new OutputNetwork();
                    out.deserialise(lastOutput);
                    outputsNetworks.add(out);

                    //output has only two fields, input needs three
                    //I am using the last direction present into input I am adding that one to the last output

                    Double directionAPF = ((InputNetwork)inputsNetwork.getTrainingPoint().get(inputsNetwork.getTrainingPoint().size() - 1)).getDirectionAPF();
                    for (int i = 0; i < ReadConfig.Configurations.getAgentTimeSteps(); i++) {
                        //transform output into input and add the direction
                        OutputNetwork outLocal = new OutputNetwork();
                        outLocal.deserialise(lastOutput);
                        InputNetwork inputLocal = new InputNetwork(directionAPF, outLocal.getSpeed(), outLocal.getBearing());
                        lastOutput = model.computeOutput(inputLocal.serialise());

                        out = new OutputNetwork();
                        out.deserialise(lastOutput);
                        outputsNetworks.add(out);
                    }
                    //assign the output to this individual
                    inputsNetwork.setOutputComputed(outputsNetworks);
                    individual.addMyInputandOutput(inputsNetwork);
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Errors with the neural network " + e.getMessage());
                e.printStackTrace();
            }

        });
    }


    /**
     * @implNote Implementation from Abstract class Algorithm
     * @param individual individual with the parameter of the classifier
     * @param input input to assign to the classifier
     * @return the output of the classifier
     * @throws Exception if the nn has problem an exception is raised
     */
    public OutputsNetwork runIndividual(Individual individual, List<InputsNetwork> input) throws Exception {
        throw new Exception("Method not usable for a Agent");
    }


    /**
     * @implNote Implementation from Abstract class Algorithm
     * Method to evaluate the agent using the classifiers
     * The fitness of each model is obtained by evaluating it with each of the classifiers in the competing population
     * For every classifier that wrongly judges the model as being the real agent, the model’s fitness increases by one.
     *
     * At the same time I can evaluate the classifier
     * The fitness of each classifier is obtained by using it to evaluate each model in the competing population
     * For each correct judgement, the classifier’s fitness increases by one
     *
     * @param opponent competing population
     * @param transformation the class that will transform from one output to the new input
     */
    public void evaluateIndividuals(Algorithm opponent, Transformation transformation){
        //I need to evaluate the agent using the classifiers
        super.getPopulation().parallelStream().forEach(individual -> {
//            System.out.println(LocalDateTime.now().toString()  + "  Evaluation individual--------------");
            //The fitness of each model is obtained by evaluating it with each of the classifiers in the competing population
            //For every classifier that wrongly judges the model as being the real agent, the model’s fitness increases by one.
            opponent.getPopulation().forEach(classifier -> {
                //I need to check for every output for every individual
                individual.getMyInputandOutput().forEach(trainReal -> {
                    ((FollowingTheGraph)transformation).setLastPoint(trainReal.getLastPoint());
                    try {
                        tgcfs.Classifiers.OutputNetwork result = (tgcfs.Classifiers.OutputNetwork) opponent.runIndividual(classifier, transformation.transform(trainReal.getOutputComputed()));
                        //if the classifier is saying true -> it is wrongly judging the agent
                        if(result.getReal()){
                            individual.increaseFitness();
                        }else{
                            //The fitness of each classifier is obtained by using it to evaluate each model in the competing population
                            //For each correct judgement, the classifier’s fitness increases by one
                            classifier.increaseFitness();
                        }
                    } catch (Exception e) {
                        logger.log(Level.SEVERE, "Error " + e.getMessage());
                        e.printStackTrace();
                    }
                });
            });
        });
    }

    /**
     * Method to train the network with the input selected
     * @param combineInputList where to find the input to train
     */
    @Override
    public void trainNetwork(List<TrainReal> combineInputList) {
        //obtain list of inputs
        combineInputList.forEach(trainReal -> {
            List<InputsNetwork> inputsNetworks = trainReal.getTrainingPoint();
            List<Point> points = trainReal.getPoints();
            //I have to train all the population with the same inputs
            super.getPopulation().parallelStream().forEach(individual -> {
                //train the model
                try {
                    individual.fitModel(inputsNetworks, points);
                } catch (Exception e) {
                    throw new Error("Error in training the model" + e.getMessage());
                }
            });
        });
    }




}


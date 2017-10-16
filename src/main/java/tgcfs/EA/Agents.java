package tgcfs.EA;

import lgds.trajectories.Point;
import org.datavec.image.loader.NativeImageLoader;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;
import tgcfs.Agents.InputNetwork;
import tgcfs.Agents.Models.Clax;
import tgcfs.Agents.Models.ConvAgent;
import tgcfs.Agents.Models.LSTMAgent;
import tgcfs.Agents.OutputNetwork;
import tgcfs.Config.ReadConfig;
import tgcfs.Idsa.IdsaLoader;
import tgcfs.InputOutput.FollowingTheGraph;
import tgcfs.InputOutput.Transformation;
import tgcfs.Loader.TrainReal;
import tgcfs.NN.EvolvableModel;
import tgcfs.NN.InputsNetwork;
import tgcfs.NN.OutputsNetwork;
import tgcfs.Networks.Convolutionary;
import tgcfs.Utils.PointWithBearing;

import java.io.File;
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
     * Generate the population for the EA
     * set the max fitness achievable by an agent
     * @param model the model of the population
     * @throws Exception exception
     */
    @Override
    public void generatePopulation(EvolvableModel model) throws Exception {
        super.generatePopulation(model);
        this.maxFitnessAchievable = (ReadConfig.Configurations.getClassifierPopulationSize() + ReadConfig.Configurations.getClassifierOffspringSize()) * ReadConfig.Configurations.getTrajectoriesTrained();
    }

    /**
     * @implNote Implementation from Abstract class Algorithm
     * @param input the input of the model
     * @throws Exception if there are problems in reading the info
     */
    @Override
    public void runIndividuals(List<TrainReal> input) throws Exception {
        //reset input
        super.getPopulation().forEach(Individual::resetInputOutput);

        //every individual in parallel
        super.getPopulation().parallelStream().forEach(individual -> {
            try {
                //retrieve model from the individual
                EvolvableModel model = individual.getModel();
                //set the weights
                model.setWeights(individual.getObjectiveParameters());

                //select which model I am using
                if(model.getClass().equals(LSTMAgent.class)){
                    this.runLSTM(input, model, individual);
                }else if(model.getClass().equals(Clax.class)){
                    this.runClax(input, model, individual);
                }else if(model.getClass().equals(ConvAgent.class)){
                    this.runConvol(input, model, individual);
                }

            } catch (Exception e) {
                logger.log(Level.SEVERE, "Errors with the neural network " + e.getMessage());
                e.printStackTrace();
            }

        });
    }

    /**
     * run the convolutional model
     * @param input the input of the model
     * @param model the model Convolutional used
     * @param individual the individual under evaluation
     * @throws Exception if something bad happened
     */
    private void runConvol(List<TrainReal> input, EvolvableModel model, Individual individual) throws Exception {
        NativeImageLoader imageLoader = new NativeImageLoader();
        INDArray lastOutput = null;
        INDArray conditionalInputConverted = null;

        IdsaLoader refLoader = input.get(0).getIdsaLoader();

        for (TrainReal inputsNetwork : input) {
            //need to genereta a picture for every timestep of the trajectory
            List<Point> growingTrajectory = new ArrayList<>();
            for(Point p : inputsNetwork.getPoints()){
                growingTrajectory.add(p);
                Boolean res = inputsNetwork.getIdsaLoader().generatePicture(growingTrajectory);
                if(!res) throw new Exception("Creation of the pictures did not work out");
                //erase image
                //((ConvAgent)model).erasePictureCreated(Paths.get(inputsNetwork.getNormalImage()));
                //if the conversion worked correctly I know where the actual picture is stored
                File realInput = new File(inputsNetwork.getNormalImage());
                INDArray realInputConverted = imageLoader.asMatrix(realInput);

                //since it is always the same lets compute it only the first time
                if(conditionalInputConverted == null) {
                    File conditionalInput = new File(inputsNetwork.getConditionalImage());
                    conditionalInputConverted = imageLoader.asMatrix(conditionalInput);
                    ((Convolutionary)model).setConditionalPicture(conditionalInputConverted);
                }

                //compute output
                lastOutput = model.computeOutput(realInputConverted);


                //now for the number of time step that I want to check save the output
                List<OutputsNetwork> outputsNetworks = new ArrayList<>();

                OutputNetwork out = new OutputNetwork();
                out.deserialise(lastOutput);
                outputsNetworks.add(out);

                //need this to transform the the value into point
                FollowingTheGraph transformation = new FollowingTheGraph();
                //I need to set the feeder
                transformation.setFeeder(((ConvAgent)model).getFeeder());
                //and the last point
                transformation.setLastPoint(new PointWithBearing(growingTrajectory.get(growingTrajectory.size() - 1)));

                for (int i = 0; i < ReadConfig.Configurations.getAgentTimeSteps(); i++) {
                    //transform output into input and add the direction
                    OutputNetwork outLocal = new OutputNetwork();
                    outLocal.deserialise(lastOutput);

                    Point toAdd = transformation.singlePointConversion(outLocal);
                    transformation.setLastPoint(new PointWithBearing(toAdd));
                    growingTrajectory.add(toAdd);

                    res = inputsNetwork.getIdsaLoader().generatePicture(growingTrajectory);
                    if(!res) throw new Exception("Creation of the pictures did not work out");
                    //erase image
                    //((ConvAgent)model).erasePictureCreated(Paths.get(inputsNetwork.getNormalImage()));

                    realInput = new File(inputsNetwork.getNormalImage());
                    realInputConverted = imageLoader.asMatrix(realInput);
                    //compute output
                    lastOutput = model.computeOutput(realInputConverted);

                    out = new OutputNetwork();
                    out.deserialise(lastOutput);
                    outputsNetworks.add(out);
                }
                //assign the output to this individual
                inputsNetwork.setOutputComputed(outputsNetworks);
                individual.addMyInputandOutput(inputsNetwork);
            }

        }
    }

    /**
     * run the Clax model
     * @param input the input of the model
     * @param model the model Clax used
     * @param individual the individual under evaluation
     * @throws Exception if something bad happened
     */
    private void runClax(List<TrainReal> input, EvolvableModel model, Individual individual) throws Exception {
        //with clax is slightly different
        Clax m = (Clax) model; //cast to clax for the proprietary method

        //I have more training input in the list
        for (TrainReal inputsNetwork : input) {
            m.setStart(inputsNetwork.getLastPoint());
            m.setTarget(((InputNetwork)inputsNetwork.getTrainingPoint().get(inputsNetwork.getTrainingPoint().size())).getTargetPoint());

            //need to compute the trajectory
            List<INDArray> out = m.computeTrajectory();
            //now for the number of time step that I want to check save the output
            List<OutputsNetwork> outputsNetworks = new ArrayList<>();
            out.forEach(o -> {
                OutputNetwork outClax = new OutputNetwork();
                outClax.deserialise(o);
                outputsNetworks.add(outClax);
            });
            //assign the output to this individual
            inputsNetwork.setOutputComputed(outputsNetworks);
            individual.addMyInputandOutput(inputsNetwork);
        }
    }

    /**
     * Run the LSTM agent
     * @param input the input of the model
     * @param model the model LSTM used
     * @param individual the individual under evaluation
     * @throws Exception if something bad happened
     */
    private void runLSTM(List<TrainReal> input, EvolvableModel model, Individual individual) throws Exception {
        //compute Output of the network
        INDArray lastOutput = null;

        int number;
        try{
            number = ReadConfig.Configurations.getAgentTimeSteps();
        }catch (Exception e){
            number = 1;
        }


        for (TrainReal inputsNetwork : input) {
            //now for the number of time step that I want to check save the output
            List<OutputsNetwork> outputsNetworks = new ArrayList<>();

            List<InputsNetwork> in = inputsNetwork.getTrainingPoint();
            int size = in.size();
            INDArray features = Nd4j.create(new int[]{1, InputNetwork.inputSize, size}, 'f');
            for (int j = 0; j < size; j++) {
                INDArray vector = in.get(j).serialise();
                features.put(new INDArrayIndex[]{NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.point(j)}, vector);
            }
            lastOutput = model.computeOutput(features);


            int timeSeriesLength = lastOutput.size(2);		//Size of time dimension
            INDArray realLastOut = lastOutput.get(NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.point(timeSeriesLength-1));

            OutputNetwork out = new OutputNetwork();
            out.deserialise(realLastOut);
            outputsNetworks.add(out);

            //output has only two fields, input needs three
            //I am using the last direction present into input I am adding that one to the last output

            Double directionAPF = ((InputNetwork) inputsNetwork.getTrainingPoint().get(inputsNetwork.getTrainingPoint().size() - 1)).getDirectionAPF();
            for (int i = 0; i < number - 1; i++) {
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
            //create the output already computed
            inputsNetwork.createRealOutputConverted();
            individual.addMyInputandOutput(inputsNetwork);
        }
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
     * It is evaluating the false trajectory and also the real one
     *
     * @param model competing population
     * @param transformation the class that will transform from one output to the new input
     */
    public void evaluateIndividuals(Algorithm model, Transformation transformation){
        //I need to evaluate the agent using the classifiers
        super.getPopulation().parallelStream().forEach(agent -> {
//            System.out.println(LocalDateTime.now().toString()  + "  Evaluation individual--------------");
            //The fitness of each model is obtained by evaluating it with each of the classifiers in the competing population
            //For every classifier that wrongly judges the model as being the real agent, the model’s fitness increases by one.

            //for every example I need to run the classifier and check the result
            model.getPopulation().forEach(classifier -> {

                //this is one agent
                //I need to check for every output for every individual
                agent.getMyInputandOutput().parallelStream().forEach(trainReal -> {

                    ((FollowingTheGraph)transformation).setLastPoint(trainReal.getLastPoint());
                    List<InputsNetwork> inputFake = trainReal.getAllThePartTransformedFake();
                    if(inputFake == null) {
                        inputFake = transformation.transform(trainReal);
                    }

                    //run the classifier for the Fake trajectory
                    try {
                        this.runClassifier(model ,agent, classifier, inputFake, Boolean.TRUE);
                    } catch (Exception e) {
                        logger.log(Level.SEVERE, "Error " + e.getMessage());
                        e.printStackTrace();
                    }

                    //run the classifier for the Real trajectory
                    List<InputsNetwork> inputReal = trainReal.getAllThePartTransformedReal();
                    try {
                        this.runClassifier(model ,agent, classifier, inputReal, Boolean.FALSE);
                    } catch (Exception e) {
                        logger.log(Level.SEVERE, "Error " + e.getMessage());
                        e.printStackTrace();
                    }


                });
            });


//
//
//            opponent.getPopulation().forEach(classifier -> {
//                //I need to check for every output for every individual
//                individual.getMyInputandOutput().forEach(trainReal -> {
//                    ((FollowingTheGraph)transformation).setLastPoint(trainReal.getLastPoint());
//                    try {
//                        tgcfs.Classifiers.OutputNetwork result = (tgcfs.Classifiers.OutputNetwork) opponent.runIndividual(classifier, transformation.transform(trainReal.getOutputComputed(), trainReal.getPoints()));
//                        //if the classifier is saying true -> it is wrongly judging the agent
//                        if(result.getReal()){
//                            individual.increaseFitness();
//                        }else{
//                            //The fitness of each classifier is obtained by using it to evaluate each model in the competing population
//                            //For each correct judgement, the classifier’s fitness increases by one
//                            classifier.increaseFitness();
//                        }
//                    } catch (Exception e) {
//                        logger.log(Level.SEVERE, "Error " + e.getMessage());
//                        e.printStackTrace();
//                    }
//                });
//            });
        });
    }


    /**
     * Run the classifier
     * @param model model of the classifier
     * @param agent agent individual
     * @param classifier agent classifier
     * @param input input for the classifier
     * @param real Boolean value. If it is false I do not need to increment the agent fitness since I am checking the real trajectory
     */
    private void runClassifier(Algorithm model, Individual agent, Individual classifier, List<InputsNetwork> input, boolean real) throws Exception {
        tgcfs.Classifiers.OutputNetwork result = (tgcfs.Classifiers.OutputNetwork) model.runIndividual(classifier, input);
        //if the classifier is saying true -> it is wrongly judging the agent
        if(result.getReal()){
            if(real) agent.increaseFitness();
        }else{
            //The fitness of each classifier is obtained by using it to evaluate each model in the competing population
            //For each correct judgement, the classifier’s fitness increases by one
            classifier.increaseFitness();
        }
    }

    /**
     * Method to train the network with the input selected
     * @param combineInputList where to find the input to train
     */
    @Override
    public void trainNetwork(List<TrainReal> combineInputList) {
        //obtain list of inputs
        try {
            if(ReadConfig.Configurations.getTrain()) {
                throw new Exception("How should i train a LSTM without bad examples???");
//                combineInputList.forEach(trainReal -> {
//                    List<InputsNetwork> inputsNetworks = trainReal.getTrainingPoint();
//                    List<Point> points = trainReal.getPoints();
//                    //I have to train all the population with the same inputs
//                    super.getPopulation().parallelStream().forEach(individual -> {
//                        //train the model
//                        try {
//                            individual.fitModel(inputsNetworks, points);
//                        } catch (Exception e) {
//                            throw new Error("Error in training the model" + e.getMessage());
//                        }
//                    });
//                });
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error " + e.getMessage());
        }
    }


}

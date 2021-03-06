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
import tgcfs.InputOutput.Normalisation;
import tgcfs.InputOutput.Transformation;
import tgcfs.Loader.TrainReal;
import tgcfs.NN.EvolvableModel;
import tgcfs.NN.InputsNetwork;
import tgcfs.NN.OutputsNetwork;
import tgcfs.Networks.Convolutionary;
import tgcfs.Performances.SaveToFile;
import tgcfs.Utils.MultyScores;
import tgcfs.Utils.PointWithBearing;
import tgcfs.Utils.Scores;

import java.io.File;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private MultyScores scores;

    /**
     * Constructor zero parameter
     * Call the super constructor
     * @param logger logger
     * @throws Exception if the super constructor has problem in reading the config files
     */
    public Agents(Logger logger) throws Exception {
        super(logger);
        this.scores = new MultyScores();
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
        if(ReadConfig.Configurations.getHallOfFame()){
            this.maxFitnessAchievable = (ReadConfig.Configurations.getClassifierPopulationSize() + ReadConfig.Configurations.getClassifierOffspringSize() + ReadConfig.Configurations.getHallOfFameSample()) * ReadConfig.Configurations.getTrajectoriesTrained();
        }else{
            this.maxFitnessAchievable = (ReadConfig.Configurations.getClassifierPopulationSize() + ReadConfig.Configurations.getClassifierOffspringSize()) * ReadConfig.Configurations.getTrajectoriesTrained();
        }
        SaveToFile.Saver.saveMaxFitnessAchievable(this.maxFitnessAchievable, this.getClass().getName());
    }

    /**
     * Generate the population for the EA
     * set the max fitness achievable by an agent
     * using the information loaded from file
     * @param model the model of the population
     * @param populationLoaded the popolation loaded from file
     * @throws Exception exception
     */
    @Override
    public void generatePopulation(EvolvableModel model, List<INDArray> populationLoaded) throws Exception {
        super.generatePopulation(model,populationLoaded);
        this.maxFitnessAchievable = (ReadConfig.Configurations.getClassifierPopulationSize() + ReadConfig.Configurations.getClassifierOffspringSize()) * ReadConfig.Configurations.getTrajectoriesTrained();
        SaveToFile.Saver.saveMaxFitnessAchievable(this.maxFitnessAchievable, this.getClass().getName());
    }

    /**
     * @implNote Implementation from Abstract class Algorithm
     * @param input the input of the model
     * @throws Exception if there are problems in reading the info
     */
    @Override
    public void runIndividuals(List<TrainReal> input) throws Exception {
        //reset input
        for(Individual ind: super.getPopulationWithHallOfFame()){
            ind.resetInputOutput();
        }

        /**
         * Class for multithreading
         * Run one agent against all the classifiers
         */
        class ComputeUnit implements Runnable {
            private CountDownLatch latch;
            private Individual agent;

            /**
             * Constructor
             * @param agent agent
             */
            private ComputeUnit(Individual agent) {
                this.agent = agent;
            }

            /**
             * CountDownLatch is a java class in the java.util.concurrent package. It is a mechanism to safely handle
             * counting the number of completed tasks. You should call latch.countDown() whenever the run method competes.
             * @param latch {@link CountDownLatch}
             */
            private void setLatch(CountDownLatch latch) {
                this.latch = latch;
            }

            /**
             * Run the agent
             */
            @Override
            public void run() {
                try {
                    //retrieve model from the individual
                    EvolvableModel model = this.agent.getModel();
                    //set the weights
                    model.setWeights(this.agent.getObjectiveParameters());
                    //select which model I am using
                    if(model.getClass().equals(LSTMAgent.class)){
                        this.runLSTM(input, model, this.agent);
                    }else {
                        throw new Exception("Not yet Implemented");
                    }

                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Errors with the neural network " + e.getMessage());
                    e.printStackTrace();
                }
                latch.countDown();
            }

            /**
             * Run the LSTM agent
             * @param input the input of the model
             * @param model the model LSTM used
             * @param individual the individual under evaluation
             */
            private void runLSTM(List<TrainReal> input, EvolvableModel model, Individual individual) {
                int number;
                try{
                    number = ReadConfig.Configurations.getAgentTimeSteps();
                }catch (Exception e){
                    number = 1;
                }

                for (TrainReal inputsNetwork : input) {

                    TrainReal currentInputsNetwork = inputsNetwork.deepCopy();

                    //now for the number of time step that I want to check save the output
                    List<OutputsNetwork> outputsNetworks = new ArrayList<>();

                    List<InputsNetwork> in = currentInputsNetwork.getTrainingPoint();
                    in.remove(0);
                    int size = in.size();

                    int inputSize = InputNetwork.inputSize;

                    INDArray features = Nd4j.create(new int[]{1, inputSize, size}, 'f');
                    for (int j = 0; j < size; j++) {
                        INDArray vector = in.get(j).serialise();
                        features.put(new INDArrayIndex[]{NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.point(j)}, vector);
                    }
                    logger.log(Level.FINER, "Input LSTM ->" + features.toString());
                    INDArray lastOutput = model.computeOutput(features);

                    int timeSeriesLength = lastOutput.size(2);		//Size of time dimension
                    INDArray realLastOut = lastOutput.get(NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.point(timeSeriesLength-1));


                    List<Double> realOutput = new ArrayList<>();
                    INDArray flat = Nd4j.toFlattened(realLastOut);
                    for(int j=0; j<flat.columns(); j++){
                        realOutput.add(flat.getDouble(j));
                    }
                    logger.log(Level.FINER, "Output LSTM ->" + realOutput.toString());

                    this.addOutput(Nd4j.toFlattened(realLastOut), outputsNetworks);

                    logger.log(Level.FINER, inputsNetwork.getId() + " Output LSTM transformed ->" + outputsNetworks.toString());

                    //output has only two fields, input needs three
                    //I am using the last direction present into input I am adding that one to the last output

                    Double directionAPF = Normalisation.decodeDirectionData(((InputNetwork) currentInputsNetwork.getTrainingPoint().get(currentInputsNetwork.getTrainingPoint().size() - 1)).getDirectionAPF());
                    for (int i = 0; i < number - 1; i++) {
                        //transform output into input and add the direction
                        OutputNetwork outLocal = new OutputNetwork();
                        outLocal.deserialise((Nd4j.toFlattened(realLastOut)));
                        InputNetwork inputLocal = new InputNetwork(directionAPF, outLocal.getSpeed(), outLocal.getBearing(), currentInputsNetwork.getLastTime());
                        lastOutput = model.computeOutput(inputLocal.serialise());

                        logger.log(Level.FINER, "Output LSTM ->" + lastOutput.toString());
                        this.addOutput(Nd4j.toFlattened(lastOutput), outputsNetworks);
                    }
                    //assign the output to this individual
                    currentInputsNetwork.setOutputComputed(outputsNetworks);
                    individual.addMyInputandOutput(currentInputsNetwork.deepCopy());

                    ((LSTMAgent) model).clearPreviousState();
                }
            }

            /**
             * if I am considerind the time I need to de-serialise the output considering also the time, otherwise not
             * @param realLastOut output generator
             * @param outputsNetworks collection of outputs
             */
            private void addOutput(INDArray realLastOut, List<OutputsNetwork> outputsNetworks){
                OutputNetwork out = new OutputNetwork();
                out.deserialise(realLastOut);
                outputsNetworks.add(out);
            }

        }

        ExecutorService exec = Executors.newFixedThreadPool(16);
        CountDownLatch latch = new CountDownLatch(super.getPopulationWithHallOfFame().size());
        ComputeUnit[] runnables = new ComputeUnit[super.getPopulationWithHallOfFame().size()];

        for(int i = 0; i < super.getPopulationWithHallOfFame().size(); i ++){
            runnables[i] = new ComputeUnit(super.getPopulationWithHallOfFame().get(i));
        }
        for(ComputeUnit r : runnables) {
            r.setLatch(latch);
            exec.execute(r);
        }
        try {
            latch.await();
            exec.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



//        for(Individual agent: super.getPopulationWithHallOfFame()){
//            //retrieve model from the individual
//            EvolvableModel model = agent.getModel();
//            //set the weights
//            model.setWeights(agent.getObjectiveParameters());
//            //select which model I am using
//            if (model.getClass().equals(LSTMAgent.class)) {
//                this.runLSTM(input, model, agent);
//            } else {
//                throw new Exception("Not yet Implemented");
//            }
//        }


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

                    //TODO if using convolutionary I need to enable the following lines
                    //Point toAdd = transformation.singlePointConversion(outLocal);
//                    transformation.setLastPoint(new PointWithBearing(toAdd));
//                    growingTrajectory.add(toAdd);

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

            TrainReal currentInputsNetwork = inputsNetwork.deepCopy();

            //now for the number of time step that I want to check save the output
            List<OutputsNetwork> outputsNetworks = new ArrayList<>();

            List<InputsNetwork> in = currentInputsNetwork.getTrainingPoint();
            int size = in.size();

            INDArray features = Nd4j.create(new int[]{1, InputNetwork.inputSize, size}, 'f');
            for (int j = 0; j < size; j++) {
                INDArray vector = in.get(j).serialise();
                features.put(new INDArrayIndex[]{NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.point(j)}, vector);
            }
            lastOutput = model.computeOutput(features);

            int timeSeriesLength = lastOutput.size(2);		//Size of time dimension
            INDArray realLastOut = lastOutput.get(NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.point(timeSeriesLength-1));
            logger.log(Level.FINE, "Output LSTM ->" + realLastOut.toString());


            OutputNetwork out = new OutputNetwork();
            out.deserialise(realLastOut);
            outputsNetworks.add(out);
            logger.log(Level.INFO, "Output LSTM transformed ->" + outputsNetworks.toString());

            //output has only two fields, input needs three
            //I am using the last direction present into input I am adding that one to the last output

            Double directionAPF = ((InputNetwork) currentInputsNetwork.getTrainingPoint().get(currentInputsNetwork.getTrainingPoint().size() - 1)).getDirectionAPF();
            for (int i = 0; i < number - 1; i++) {
                //transform output into input and add the direction
                OutputNetwork outLocal = new OutputNetwork();
                outLocal.deserialise(lastOutput);
                InputNetwork inputLocal = new InputNetwork(directionAPF, outLocal.getSpeed(), outLocal.getBearing(), currentInputsNetwork.getLastTime());
                lastOutput = model.computeOutput(inputLocal.serialise());

                logger.log(Level.FINE, "Output LSTM ->" + lastOutput.toString());

                out = new OutputNetwork();
                out.deserialise(lastOutput);
                outputsNetworks.add(out);
            }
            //assign the output to this individual
            currentInputsNetwork.setOutputComputed(outputsNetworks);

            //create the output already computed
            individual.addMyInputandOutput(currentInputsNetwork);

            ((LSTMAgent)model).clearPreviousState();
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
     * @param competingPopulation competing population
     * @param transformation the class that will transform from one output to the new input
     */
    public void evaluateIndividuals(Algorithm competingPopulation, Transformation transformation){

        /**
         * Class for multithreading
         * Run one agent against all the classifiers
         * Compute the fitness function in this way:
         * If the classifier is classifying the fake point as correct the agent fitness increases by one
         * If the classifier is able to classify the fake point as a fake, it earns one point
         * If the classifier is able to classify the real point as a real, it earns a point
         */
        class BaseFitness implements Runnable {
            private CountDownLatch latch;
            private Individual agent;
            private List<Individual> adersarialPopulation;

            /**
             * Constructor
             * @param agent current agent
             * @param adersarialPopulation adversarial population
             */
            private BaseFitness(Individual agent, List<Individual> adersarialPopulation) {
                this.agent = agent;
                this.adersarialPopulation = adersarialPopulation;
            }

            /**
             * CountDownLatch is a java class in the java.util.concurrent package. It is a mechanism to safely handle
             * counting the number of completed tasks. You should call latch.countDown() whenever the run method competes.
             * @param latch {@link CountDownLatch}
             */
            private void setLatch(CountDownLatch latch) {
                this.latch = latch;
            }

            /**
             * Override method run
             * run the classifier over the agent
             */
            @Override
            public void run() {
                List<TrainReal> inputOutput = this.agent.getMyInputandOutput();
                for(Individual opponent: this.adersarialPopulation){
                    for(TrainReal example: inputOutput){
                        //run the classifier for the Fake trajectory
                        try {
                            tgcfs.Classifiers.OutputNetwork result = (tgcfs.Classifiers.OutputNetwork) competingPopulation.runIndividual(opponent, example.getAllThePartTransformedFake());
                            logger.log(Level.FINER, "Output network ->" + result.toString() + " realValue -> " + result.getRealValue() + " -->" + Boolean.FALSE);
                            double decision = result.getRealValue();
                            if( decision > 0.5 ) {
                                //it is saying it is true
                                this.agent.increaseFitness(1.0);
                            }else{
                                //it is false
                                opponent.increaseFitness(1.0);
                            }

                        } catch (Exception e) {
                            logger.log(Level.SEVERE, "Error Classifier Fake Input" + e.getMessage());
                            e.printStackTrace();
                        }

                        //run the classifier for the Real trajectory
                        try {
                            tgcfs.Classifiers.OutputNetwork result = (tgcfs.Classifiers.OutputNetwork) competingPopulation.runIndividual(opponent, example.getAllThePartTransformedReal());
                            logger.log(Level.FINER, "Output network ->" + result.toString() + " realValue -> " + result.getRealValue() + " -->" + Boolean.TRUE);
                            double decision = result.getRealValue();
                            if( decision > 0.5 ) {
                                //it is saying it is true
                                opponent.increaseFitness(1.0);
                            }

                        } catch (Exception e) {
                            logger.log(Level.SEVERE, "Error Classifier Real Input" + e.getMessage());
                            e.printStackTrace();
                        }
                    }

                }
                latch.countDown();
            }

        }

        /**
         * Class for multithreading
         * Run one agent against all the classifiers
         * Compute the fitness function in this way:
         * Now if the classifier (ENN) is returning the value x (>=0.5 -> true) as output, the fitness is computed in this way:
         * - agent fitness is x, classifier is (1-x)
         * In the other case, output x (<0.5 -> false), the fitness is:
         * - classifier is x, agent is (1-x)
         */
        class ComputeUnit implements Runnable {
            private CountDownLatch latch;
            private Individual agent;
            private List<Individual> adersarialPopulation;
            private MultyScores scores;
            private boolean score;

            /**
             * Constructor
             * @param agent current agent
             * @param adersarialPopulation adversarial population
             * @param score do I want the score
             * @param scores where to save the score
             */
            private ComputeUnit(Individual agent, List<Individual> adersarialPopulation, boolean score, MultyScores scores) {
                this.agent = agent;
                this.adersarialPopulation = adersarialPopulation;
                this.scores = scores;
                this.score = score;
            }

            /**
             * CountDownLatch is a java class in the java.util.concurrent package. It is a mechanism to safely handle
             * counting the number of completed tasks. You should call latch.countDown() whenever the run method competes.
             * @param latch {@link CountDownLatch}
             */
            private void setLatch(CountDownLatch latch) {
                this.latch = latch;
            }

            /**
             * Override method run
             * run the classifier over the agent
             */
            @Override
            public void run() {
                List<TrainReal> inputOutput = agent.getMyInputandOutput();
                for(Individual opponent: this.adersarialPopulation){
                    for(TrainReal example: inputOutput){
                        //run the classifier for the Fake trajectory
                        try {
                            this.runClassifier(competingPopulation ,agent, opponent, example, true);
                        } catch (Exception e) {
                            logger.log(Level.SEVERE, "Error Classifier Fake Input" + e.getMessage());
                            e.printStackTrace();
                        }

                        //run the classifier for the Real trajectory
                        try {
                            this.runClassifier(competingPopulation ,agent, opponent, example, false);
                        } catch (Exception e) {
                            logger.log(Level.SEVERE, "Error Classifier Real Input" + e.getMessage());
                            e.printStackTrace();
                        }
                    }

                }
                latch.countDown();
            }

            /**
             * Run the classifier
             * @param model model of the classifier
             * @param agent agent individual
             * @param classifier agent classifier
             * @param totalInput TrainReal
             * @param real Boolean value. If it is false I do not need to increment the agent fitness since I am checking the real trajectory
             */
            private synchronized void runClassifier(Algorithm model, Individual agent, Individual classifier, TrainReal totalInput, boolean real) throws Exception {
                List<InputsNetwork> input;
                if(real){
                    input = totalInput.getAllThePartTransformedFake();
                }else{
                    input = totalInput.getAllThePartTransformedReal();
                }
                 tgcfs.Classifiers.OutputNetwork result = (tgcfs.Classifiers.OutputNetwork) model.runIndividual(classifier, input);
                logger.log(Level.FINER, "Output network ->" + result.toString() + " realValue -> " + result.getRealValue() + " -->" + real);


                double decision = result.getRealValue();

                if( decision > 0.5 ) {
                    //it is saying it is true
                    //counting this only if the fake trajectory
                    if(real) {
                        agent.increaseFitness(decision);
                        classifier.increaseFitness(1 - decision);
                        if (this.score) {
                            Scores sc = new Scores(agent.getModel().getId(),totalInput.getId(), classifier.getModel().getId(), decision);
                            this.scores.addScore(sc);
                        }
                    }
                }else{
                    //it is false
                    classifier.increaseFitness(decision);
                    if(real) {
                        agent.increaseFitness(1 - decision);
                        if (this.score) {
                            Scores sc = new Scores(agent.getModel().getId(), totalInput.getId(), classifier.getModel().getId(), decision);
                            this.scores.addScore(sc);
                        }
                    }
                }

            }
        }

        /**
         * Class for multithreading
         * Run one agent against all the classifiers
         * Compute the fitness function in this way:
         *
         * R =  #real
         * T_jk = (sum_i( classifier_j(agent_i)))k   <- in this module
         * Y_ijk = (R * classifier_j(agent_i) / T_j)k
         * E_ijk = ({ i = real : 1 - Y_ij, i = fake: Y_ij })k
         *
         * FitnessAgent = sum_k sum_j( E_ijk )
         * FitnessClassifier = sum_k sum_i( 1 - E_ijk)
         *
         */
        class ComputeSelmarFitnessUnit implements Runnable{
            private CountDownLatch latch;
            private Individual classifier;
            private List<Individual> agentAdversarialPopulation;
            private Map<Integer, Map<UUID, Double>> Tik;
//            private Map<Integer, UUID> toForget;
//            private Map<Integer, Map<UUID, Double>> onlyRealClassifications;
//            private Map<Integer, Map<UUID, Double>> onlyFakeClassifications;
            private Map<Integer, Map<UUID, Double>> toForgetWithValues;


            /**
             * Constructor
             * @param classifier current classifier
             * @param adersarialPopulation adversarial population

             */
            private ComputeSelmarFitnessUnit(Individual classifier, List<Individual> adersarialPopulation) {
                this.classifier = classifier;
                this.agentAdversarialPopulation = adersarialPopulation;
                this.Tik = new HashMap<>();
//                this.toForget = new HashMap<>();
//                this.onlyRealClassifications = new HashMap<>();
//                this.onlyFakeClassifications = new HashMap<>();
                this.toForgetWithValues = new HashMap<>();
            }

            /**
             * CountDownLatch is a java class in the java.util.concurrent package. It is a mechanism to safely handle
             * counting the number of completed tasks. You should call latch.countDown() whenever the run method competes.
             * @param latch {@link CountDownLatch}
             */
            private void setLatch(CountDownLatch latch) {
                this.latch = latch;
            }

            /**
             * Getter for the classifier's model ID
             * @return int id
             */
            private int getClassifierID(){
                return this.classifier.getModel().getId();
            }


            /**
             * Getting to forget elements
             * @return Map<Integer, UUID>> containing id agent and id trajectory
             */
//            private Map<Integer, UUID> getToForget(){
//                return this.toForget;
//            }

            /**
             * Return only how I am classifying the real points
             * @return Map<Integer, Map<UUID, Double>> containing id agent and its classification
             */
//            private Map<Integer, Map<UUID, Double>> getOnlyRealClassifications(){
//                return this.onlyRealClassifications;
//            }
//            private Map<Integer, Map<UUID, Double>> getOnlyFakeClassifications(){
//                return this.onlyFakeClassifications;
//            }
            private Map<Integer, Map<UUID, Double>> getToForgetWithValues(){
                return this.toForgetWithValues;
            }


            /**
             * Getter for the results from the classification
             * @return Map<Integer, Map<UUID, Double>> containing id agent and its classification
             */
            private Map<Integer, Map<UUID, Double>> getResults(){
                return this.Tik;
            }

            /**
             * Override method run
             * run the classifier over the agent
             *
             * classifierResultAgentI = classifier_j(agent_i)
             * classifierResultAgentI = agent_i classification using classifier j
             */
            @Override
            public void run() {
                for(Individual agent: this.agentAdversarialPopulation){
                    List<TrainReal> inputOutput = agent.getMyInputandOutput();

//                    //agent id is always the same for all the trajectories
//                    int agentId = agent.getModel().getId();
//
//                    // trajectory result
//                    Map<UUID, Double> Tj = new HashMap<>();
//
//                    for(TrainReal example: inputOutput) {
//                        //run the classifier for the Fake trajectory
//                        try {
//                            tgcfs.Classifiers.OutputNetwork result = (tgcfs.Classifiers.OutputNetwork) competingPopulation.runIndividual(classifier, example.getAllThePartTransformedFake());
//                            if (ReadConfig.debug) logger.log(Level.INFO, "Fake Output network ->" + result.toString() + " realValue -> " + result.getRealValue());
//                            //save all the results
//                            Tj.put(example.getId(), result.getRealValue());
//                        } catch (Exception e) {
//                            logger.log(Level.SEVERE, "Error Classifier Fake Input" + e.getMessage());
//                            e.printStackTrace();
//                        }
//                    }
//                    //update the main result with the agent id
//                    this.Tik.put(agentId, Tj);
//
//                    //also this one is going to be always the same. Lets use only the first real one
//                    int realAgentId = inputOutput.get(0).getIdRealPoint().getId();
//                    // trajectory result
//                    Map<UUID, Double> T2j = new HashMap<>();
//
//                    for(TrainReal example: inputOutput){
//                        //run the classifier for the Real trajectory
//                        try {
//                            tgcfs.Classifiers.OutputNetwork resultReal = (tgcfs.Classifiers.OutputNetwork) competingPopulation.runIndividual(classifier, example.getAllThePartTransformedReal());
//                            if (ReadConfig.debug) logger.log(Level.INFO, "Real Output network ->" + resultReal.toString() + " realValue -> " + resultReal.getRealValue());
//                            //save all the results
//                            T2j.put(example.getId(), resultReal.getRealValue());
//
//                        } catch (Exception e) {
//                            logger.log(Level.SEVERE, "Error Classifier Real Input" + e.getMessage());
//                            e.printStackTrace();
//                        }
//                    }
//                    //update the main result with the agent id
//                    this.Tik.put(realAgentId, T2j);




                    //agent id is always the same for all the trajectories
                    int agentId = agent.getModel().getId();
                    //also this one is going to be always the same. Lets use only the first real one
                    int realAgentId = inputOutput.get(0).getIdRealPoint().getId();
                    // trajectory result
                    Map<UUID, Double> Tj = new HashMap<>();
                    // trajectory result
                    Map<UUID, Double> T2j = new HashMap<>();

                    // trajectory result
                    Map<UUID, Double> TjContraint = new HashMap<>();
                    // trajectory result
//                    Map<UUID, Double> T2jContraint = new HashMap<>();
                    for(TrainReal example: inputOutput) {
                        //run the classifier for the Fake trajectory
                        tgcfs.Classifiers.OutputNetwork result = null;
                        try {
                            result = (tgcfs.Classifiers.OutputNetwork) competingPopulation.runIndividual(this.classifier, example.getAllThePartTransformedFake());
                            logger.log(Level.FINER, "Fake Output network ->" + result.toString() + " realValue -> " + result.getRealValue());
                            //save all the results

                        } catch (Exception e) {
                            logger.log(Level.SEVERE, "Error Classifier Fake Input" + e.getMessage());
                            e.printStackTrace();
                        }

                        tgcfs.Classifiers.OutputNetwork resultReal = null;
                        //run the classifier for the Real trajectory
                        try {
                            resultReal = (tgcfs.Classifiers.OutputNetwork) competingPopulation.runIndividual(this.classifier, example.getAllThePartTransformedReal());
                            logger.log(Level.FINER, "Real Output network ->" + resultReal.toString() + " realValue -> " + resultReal.getRealValue());
                            //save all the results

                        } catch (Exception e) {
                            logger.log(Level.SEVERE, "Error Classifier Real Input" + e.getMessage());
                            e.printStackTrace();
                        }
                        Tj.put(example.getId(), result.getRealValue());
                        T2j.put(example.getId(), resultReal.getRealValue());

                        //fake point classified fake | real point classified real = *1
                        //fake point classified fake | real point classified fake = *0.5
                        //fake point classified real | real point classified real = *0.5
                        //fake point classified real | real point classified fake = *0.25
                        if(result.getRealValue() <= 0.5 && resultReal.getRealValue() > 0.5){
                            TjContraint.put(example.getId(), 1.0);
                        }else if(result.getRealValue() <= 0.5 && resultReal.getRealValue() <= 0.5){
                            TjContraint.put(example.getId(), 1.0);
                        }else if(result.getRealValue() > 0.5 && resultReal.getRealValue() > 0.5){
                            TjContraint.put(example.getId(), 1.0);
                        }else{
                            TjContraint.put(example.getId(), 1.0);
                        }

//                        if((result.getRealValue() <= 0.5 && resultReal.getRealValue() > 0.5) || (result.getRealValue() > 0.5 && resultReal.getRealValue() > 0.5)){
//                            //ok
//                        }else{
//                            //save the ids
//                            this.toForget.put(agentId, example.getId());
//                            this.toForget.put(realAgentId, example.getId());
//                        }

//                        this.onlyFakeClassifications.put(agent.getModel().getId(),Tj);
//                        this.onlyRealClassifications.put(example.getIdRealPoint().getId(), T2j);
                    }
                    //update the main result with the agent id
                    this.Tik.put(agentId, Tj);
                    //update the main result with the agent id
                    this.Tik.put(realAgentId, T2j);

                    this.toForgetWithValues.put(agentId , TjContraint);
                    this.toForgetWithValues.put(realAgentId , TjContraint);



                }
                latch.countDown();
            }

        }


        //Need this with both fitness functions
        List<Integer> realAgentsId = new ArrayList<>();
        //transform the outputs into the input for the classifiers
        for(Individual ind : super.getPopulationWithHallOfFame()){
            //transform trajectory in advance to prevent multiprocessing errors
            List<TrainReal> inputOutput = ind.getMyInputandOutput();
            for(TrainReal trainReal: inputOutput){
                ((FollowingTheGraph)transformation).setLastPoint(trainReal.getLastPoint());
                transformation.transform(trainReal);
                realAgentsId.add(trainReal.getIdRealPoint().getId());
            }
        }

        logger.log(Level.INFO, "Start real classification");


        //set the default fitness as the normal fitness
        int fitnessTypology = 0;
        try {
            fitnessTypology = ReadConfig.Configurations.getFitnessFunction();
        } catch (Exception ignored) { }


        switch (fitnessTypology){
            case 0:
                //  fitness function from paper
                //launch my way to compute the fitness
                ExecutorService execBase = Executors.newFixedThreadPool(16);
                CountDownLatch latchBase= new CountDownLatch(super.getPopulationWithHallOfFame().size());
                BaseFitness[] runnablesBase = new BaseFitness[super.getPopulationWithHallOfFame().size()];
                for(int i = 0; i < super.getPopulationWithHallOfFame().size(); i ++){
                    runnablesBase[i] = new BaseFitness(super.getPopulationWithHallOfFame().get(i), competingPopulation.getPopulationWithHallOfFame());
                }
                for(BaseFitness r : runnablesBase) {
                    r.setLatch(latchBase);
                    execBase.execute(r);
                }
                try {
                    latchBase.await();
                    execBase.shutdown();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                break;

            case 1:

                // original fitness function implemented
                boolean score = false;
                try {
                    score = ReadConfig.Configurations.getScore();
                } catch (Exception ignored) { }

                //launch my way to compute the fitness
                ExecutorService execOriginal = Executors.newFixedThreadPool(16);
                CountDownLatch latchOriginal = new CountDownLatch(super.getPopulationWithHallOfFame().size());
                ComputeUnit[] runnablesOriginal = new ComputeUnit[super.getPopulationWithHallOfFame().size()];


                for(int i = 0; i < super.getPopulationWithHallOfFame().size(); i ++){
                    runnablesOriginal[i] = new ComputeUnit(super.getPopulationWithHallOfFame().get(i), competingPopulation.getPopulationWithHallOfFame(), score, this.scores);
                }
                for(ComputeUnit r : runnablesOriginal) {
                    r.setLatch(latchOriginal);
                    execOriginal.execute(r);
                }
                try {
                    latchOriginal.await();
                    execOriginal.shutdown();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                break;
            case 2:
                // selmar fitness function
                // number real trajectories = total number of trajectories said as real = # trajectories trained * # individuals
                int R = super.getPopulationWithHallOfFame().size();

                //classifier id, agent id, trajectory id, result
                HashMap<Integer, Map<Integer, Map<UUID, Double>>> results = new HashMap<>();


                //realClassification
//                HashMap<Integer, Map<Integer, Map<UUID, Double>>> realClassification = new HashMap<>();
//                HashMap<Integer, Map<Integer, Map<UUID, Double>>> fakeClassification = new HashMap<>();
//
//                //id to not use in fitness
//                HashMap<Integer, Map<Integer, UUID>> toAvoid = new HashMap<>();
//
//                //multipliers
                HashMap<Integer, Map<Integer, Map<UUID, Double>>> multiplierData = new HashMap<>();

                //launch the threads for the computations
                ExecutorService exec = Executors.newFixedThreadPool(96);
                CountDownLatch latch = new CountDownLatch(competingPopulation.getPopulationWithHallOfFame().size());
                ComputeSelmarFitnessUnit[] runnables = new ComputeSelmarFitnessUnit[competingPopulation.getPopulationWithHallOfFame().size()];

                //create all the runnables
                for(int i = 0; i < competingPopulation.getPopulationWithHallOfFame().size(); i ++){
                    runnables[i] = new ComputeSelmarFitnessUnit(competingPopulation.getPopulationWithHallOfFame().get(i), super.getPopulationWithHallOfFame());
                }
                //execute them and wait them till they have finished
                for(ComputeSelmarFitnessUnit r : runnables) {
                    r.setLatch(latch);
                    exec.execute(r);
                }
                try {
                    latch.await();
                    exec.shutdown();

                    //collecting the results
                    for (ComputeSelmarFitnessUnit runnable : runnables) {
                        //all the i, j fixed -> classifier_j(agent_i))
                        results.put(runnable.getClassifierID(), runnable.getResults());
//                        toAvoid.put(runnable.getClassifierID(), runnable.getToForget());
//                        realClassification.put(runnable.getClassifierID(), runnable.getOnlyRealClassifications());
//                        fakeClassification.put(runnable.getClassifierID(), runnable.getOnlyFakeClassifications());
                        multiplierData.put(runnable.getClassifierID(), runnable.getToForgetWithValues());
                    }


//                    logger.log(Level.INFO, results.toString());
//                    logger.log(Level.INFO, "realAgentsIDs -> " + realAgentsId.toString());
//                    logger.log(Level.INFO, "----------");
//                    logger.log(Level.INFO, realClassification.toString());


                    //find list of trajectories id
                    Map<Integer, Map<UUID, Double>> a = results.get(results.keySet().toArray()[0]);
                    Map<UUID, Double> b = a.get(a.keySet().toArray()[0]);
                    List<UUID> trajectoriesID = new ArrayList<>(b.keySet());



                    //creation all the Tjk
                    //T_j_k = (sum_i( classifier_j(agent_i)))_k
                    //trajectory ID, classifier ID, sum Tjk
                    HashMap<UUID, HashMap<Integer, Double>> Tjk = new HashMap<>();

                    //classifier id, agent id, trajectory id, result
                    for(UUID traID: trajectoriesID){
                        HashMap<Integer, Double> Tj = new HashMap<>();
                        for(int classifierID: results.keySet()){
                            //classifierID is the classifier id -> i
                            Map<Integer, Map<UUID, Double>> classifierResults = results.get(classifierID);

                            double classifierTotal = 0.0;

                            for(Integer agentId: classifierResults.keySet()){
                                Map<UUID, Double> traMap = classifierResults.get(agentId);

                                classifierTotal += traMap.get(traID);

                            }
                            Tj.put(classifierID, classifierTotal);
                        }
                        Tjk.put(traID, Tj);
                    }

                    //Creation matrix Yij
                    //Y_jik = (R * classifier_j(agent_i) / T_j)k
                    //E_jik = ({ i = real : 1 - Y_ij, i = fake: Y_ij } ^ 2)k
                    double maxValue = 0;


                    HashMap<UUID, HashMap<Integer, HashMap<Integer, Double>>> Yijk = new HashMap<>();

//                    HashMap<UUID, HashMap<Integer, HashMap<Integer, Double>>> multiplier = new HashMap<>();

                    for(UUID traID: trajectoriesID){
                        HashMap<Integer, HashMap<Integer, Double>> Yij = new HashMap<>();
//                        HashMap<Integer, HashMap<Integer, Double>> Eij = new HashMap<>();

//                        HashMap<Integer, HashMap<Integer, Double>> subMultiplier = new HashMap<>();
                        for(int classifierID: results.keySet()){
                            //classifierID is the classifier id -> i
                            Map<Integer, Map<UUID, Double>> classifierResults = results.get(classifierID);


                            //Creation id agent,classifier
//                            HashMap<Integer, Double> subE = new HashMap<>();
                            //for debug, remove this hashmap, it is redundant
                            HashMap<Integer, Double> subY = new HashMap<>();


//                            HashMap<Integer, Double> subSubMultiplier = new HashMap<>();


                            for(int agentId: classifierResults.keySet()){
//                                Map<UUID, Double> traMap = classifierResults.get(agentId).get(traID);
                                //now I have classifier j and looping over agent i


                                double singleValue = classifierResults.get(agentId).get(traID);
                                double y = R * singleValue / Tjk.get(traID).get(classifierID);

                                if(Double.isNaN(y)) y = 0.0;
                                //save the max value for y
                                if(maxValue < y) maxValue = y;

                                subY.put(agentId, y);


//                                if(realAgentsId.stream().anyMatch(t -> t == agentId)){
//                                    //if point is a real point
////                                    subE.put(agentId, Math.pow(1 - y, 2));
//                                    if(y > 0.5) {
//                                        //if point real point and it is classified as a real
//                                        subE.put(agentId, Math.pow(1 - y, 2));
////                                        subSubMultiplier.put(agentId, 1.0);
//                                    }else {
//                                        //if point is a real point and it is classified as fake
//                                        subE.put(agentId, Math.pow(1 - y, 2));
////                                        subSubMultiplier.put(agentId, 1.0);
//                                    }
////                                    subE.put(agentId, Math.pow(1 - y, 2) * 100);
//                                }else{
//                                    ///if point is a generated point
//                                    subE.put(agentId, Math.pow(y, 2));
//                                    if(y > 0.5) {
//                                        //if point is a generated point and it is classified as real
//                                        subE.put(agentId, Math.pow(y, 2));
////                                        subSubMultiplier.put(agentId, 100.0);
//                                    }else {
//                                        //if point is a generated point and it is classified as fake
//                                        subE.put(agentId, Math.pow(y, 2));
////                                        subSubMultiplier.put(agentId, 1.0);
//                                    }
//
//                                }

                            }

//                            Eij.put(classifierID, subE);
                            //for debug, remove this hashmap, it is redundant
                            Yij.put(classifierID, subY);

//                            subMultiplier.put(classifierID, subSubMultiplier);
                        }
                        Yijk.put(traID, Yij);
//                        Eijk.put(traID, Eij);

//                        multiplier.put(traID, subMultiplier);
                    }

                    HashMap<UUID, HashMap<Integer, HashMap<Integer, Double>>> Eijk = new HashMap<>();
                    for(UUID traID: trajectoriesID){
                        HashMap<Integer, HashMap<Integer, Double>> Eij = new HashMap<>();
                        HashMap<Integer, HashMap<Integer, Double>> Yij = Yijk.get(traID);

                        for(int classifierID: Yij.keySet()){
                            HashMap<Integer, Double> subY = Yij.get(classifierID);
                            HashMap<Integer, Double> subE = new HashMap<>();

                            for(int agentId: subY.keySet()){

                                double y = subY.get(agentId);
                                double realY;

                                if(maxValue > 1){
                                    realY = Normalisation.convertToSomething(maxValue, 0.0,1.0,0.0, y);
                                }else{
                                    realY = y;
                                }

                                if(realAgentsId.stream().anyMatch(t -> t == agentId)){
                                    //if point is a real point
                                    subE.put(agentId, Math.pow(1 - realY, 2));
                                }else{
                                    ///if point is a generated point
                                    subE.put(agentId, Math.pow(realY, 2));
                                }
                            }
                            Eij.put(classifierID, subE);
                        }
                        Eijk.put(traID, Eij);
                    }

                    //FitnessAgent = sum_k(sum_j( E_jik ))
                    for(Individual agent : super.getPopulationWithHallOfFame()){
                        int id = agent.getModel().getId();
                        double fitness = 0;
                        //need to find all the values with that id and with all the trajectories
                        for(UUID uuid: Eijk.keySet()){
                            HashMap<Integer, HashMap<Integer, Double>> Eij = Eijk.get(uuid);

//                            HashMap<Integer, HashMap<Integer, Double>> subMultiplier = multiplier.get(uuid);
                            double traFitness = 0;
                            //need to find all the values with that id
                            for(int classifierID: Eij.keySet()){
                                //find values to avoid
//                                Map<Integer, UUID> toNotUse = toAvoid.get(classifierID);
//                                fitness += Eij.get(classifierID).get(id);
//                                if(!(toNotUse.containsKey(id) && toNotUse.get(id)==uuid)){

                                fitness += (Eij.get(classifierID).get(id) * multiplierData.get(classifierID).get(id).get(uuid));
                                traFitness += (Eij.get(classifierID).get(id) * multiplierData.get(classifierID).get(id).get(uuid));
                                //subMultiplier.get(classifierID).get(id));
//                                    fitness += (Eij.get(classifierID).get(id));
//                                }
                            }

                            double finalTraFitness = traFitness;
                            agent.getMyInputandOutput().stream().filter(ex -> ex.getId().equals(uuid)).findAny().ifPresent(el -> el.setFitnessGivenByTheClassifier(finalTraFitness));
                        }
                        agent.setFitness(fitness);
                        //setting the fitness for the examples of this agent
//                        for(TrainReal example: agent.getMyInputandOutput()){
//                            example.setFitnessGivenByTheClassifier(fitness);
//                        }
                    }


                    for(Individual classifier: competingPopulation.getPopulationWithHallOfFame()){
                        int id = classifier.getModel().getId();

                        //find values to avoid
//                        Map<Integer, UUID> toNotUse = toAvoid.get(id);

                        double fitness = 0;
                        for(UUID uuid: Eijk.keySet()){
                            HashMap<Integer, Double> allTheI = Eijk.get(uuid).get(id);
//                            HashMap<Integer, Double> subMultiplier = multiplier.get(uuid).get(id);


                            for(int agentId: allTheI.keySet()){
//                                fitness += Math.abs(1 - allTheI.get(agentId));
//                                if(!(toNotUse.containsKey(agentId) && toNotUse.get(agentId)==uuid)){
                              fitness += (Math.abs(1 - allTheI.get(agentId)) * multiplierData.get(id).get(agentId).get(uuid));
                                //subMultiplier.get(agentId));
//                                fitness += (Math.abs(1 - allTheI.get(agentId)));
//                                }
                            }
                        }
                        classifier.setFitness(fitness);
                    }




                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                break;

            default:
                throw new Error("Fitness type not implemented");
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
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error " + e.getMessage());
        }
    }


    /**
     * Save in JSON format the trajectory and the generated part of it
     * @param generationAgent number of generation for the agent population
     * @param generationClassifier number of generation for the classifier population

     * @throws Exception if something wrong happens in saving everything
     */
    public void saveTrajectoriesAndPointGenerated(int generationAgent, int generationClassifier) throws Exception {
        List<TrainReal> totalList = new ArrayList<>();

//        int maxValues = 2;

        for(Individual ind: super.getPopulation()){
//            int count = 0;
            for(TrainReal train: ind.getMyInputandOutput()){
//                if (count < maxValues)
                    totalList.add(train.deepCopy());
//                count++;
            }
        }

        SaveToFile.Saver.dumpTrajectoryAndGeneratedPart(totalList, generationAgent, generationClassifier);
    }


    /**
     * Save in JSON format the trajectory and the generated part of it
     * @param gen number of generation I am
     * @throws Exception if something wrong happens in saving everything
     */
    public void saveTrajectoriesAfterSelection(int gen) throws Exception {
        List<TrainReal> totalList = new ArrayList<>();
        for(Individual ind: super.getPopulation()){
            for(TrainReal train: ind.getMyInputandOutput()){
                totalList.add(train.deepCopy());
            }
        }
        SaveToFile.Saver.dumpTrajectoryAndGeneratedPart(totalList, gen);
    }


    /**
     * Save score of the battle
     * @param generationAgent number of generation for the agent population
     * @param generationClassifier number of generation for the classifier population
     * @throws Exception if something wrong happens in saving everything
     */
    public void saveScoresBattle(int generationAgent, int generationClassifier) throws Exception {
        SaveToFile.Saver.saveScoresBattle(this.scores.getScore(), generationAgent, generationClassifier);
    }

    /**
     * Reset scores
     */
    public void resetScore(){
        this.scores = new MultyScores();
    }

    /**
     * Generate the real last point
     * @param transformation {@link FollowingTheGraph} transformation reference to transform the output in real point //TODO generalise this
     */
    public void generateRealPoints(FollowingTheGraph transformation) throws Exception {
        for(Individual ind: super.getPopulation()) {
            for (TrainReal train : ind.getMyInputandOutput()) {
                if(train.getRealPointsOutputComputed() == null) {
                    List<PointWithBearing> generatedPoint = new ArrayList<>();
                    transformation.setLastPoint(train.getLastPoint());
                    for(OutputsNetwork outputsNetwork: train.getOutputComputed()){
                        PointWithBearing genP = new PointWithBearing(transformation.singlePointConversion(outputsNetwork, train.getLastTime()));
                        generatedPoint.add(genP);
                        transformation.setLastPoint(genP);
                    }
                    train.setRealPointsOutputComputed(generatedPoint);
                    train.computeStatistic();
                }
            }
        }
    }

}


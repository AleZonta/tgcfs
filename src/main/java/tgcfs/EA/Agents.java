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
import tgcfs.Performances.SaveToFile;
import tgcfs.Utils.MultyScores;
import tgcfs.Utils.PointWithBearing;
import tgcfs.Utils.Scores;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
                    if(ReadConfig.debug) logger.log(Level.INFO, "Input LSTM ->" + features.toString());
                    lastOutput = model.computeOutput(features);

                    int timeSeriesLength = lastOutput.size(2);		//Size of time dimension
                    INDArray realLastOut = lastOutput.get(NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.point(timeSeriesLength-1));
                    if(ReadConfig.debug) logger.log(Level.INFO, "Output LSTM ->" + realLastOut.toString());


                    OutputNetwork out = new OutputNetwork();
                    out.deserialise(realLastOut);
                    outputsNetworks.add(out);
                    if(ReadConfig.debug) logger.log(Level.INFO, "Output LSTM transformed ->" + outputsNetworks.toString());

                    //output has only two fields, input needs three
                    //I am using the last direction present into input I am adding that one to the last output

                    Double directionAPF = ((InputNetwork) currentInputsNetwork.getTrainingPoint().get(currentInputsNetwork.getTrainingPoint().size() - 1)).getDirectionAPF();
                    for (int i = 0; i < number - 1; i++) {
                        //transform output into input and add the direction
                        OutputNetwork outLocal = new OutputNetwork();
                        outLocal.deserialise(lastOutput);
                        InputNetwork inputLocal = new InputNetwork(directionAPF, outLocal.getSpeed(), outLocal.getBearing());
                        lastOutput = model.computeOutput(inputLocal.serialise());

                        if(ReadConfig.debug) logger.log(Level.INFO, "Output LSTM ->" + lastOutput.toString());

                        out = new OutputNetwork();
                        out.deserialise(lastOutput);
                        outputsNetworks.add(out);
                    }
                    //assign the output to this individual
                    currentInputsNetwork.setOutputComputed(outputsNetworks);

                    //create the output already computed
                    currentInputsNetwork.createRealOutputConverted();
                    individual.addMyInputandOutput(currentInputsNetwork.deepCopy());

                    ((LSTMAgent)model).clearPreviousState();
                }
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



        //every individual in parallel
//        super.getPopulationWithHallOfFame().parallelStream().forEach(individual -> {
//            try {
//                //retrieve model from the individual
//                EvolvableModel model = individual.getModel();
//                //set the weights
//                model.setWeights(individual.getObjectiveParameters());
//                //select which model I am using
//                if (model.getClass().equals(LSTMAgent.class)) {
//                    this.runLSTM(input, model, individual);
//                } else if (model.getClass().equals(Clax.class)) {
//                    this.runClax(input, model, individual);
//                } else if (model.getClass().equals(ConvAgent.class)) {
//                    this.runConvol(input, model, individual);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });

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
            if(ReadConfig.debug) logger.log(Level.INFO, "Output LSTM ->" + realLastOut.toString());


            OutputNetwork out = new OutputNetwork();
            out.deserialise(realLastOut);
            outputsNetworks.add(out);
            if(ReadConfig.debug) logger.log(Level.INFO, "Output LSTM transformed ->" + outputsNetworks.toString());

            //output has only two fields, input needs three
            //I am using the last direction present into input I am adding that one to the last output

            Double directionAPF = ((InputNetwork) currentInputsNetwork.getTrainingPoint().get(currentInputsNetwork.getTrainingPoint().size() - 1)).getDirectionAPF();
            for (int i = 0; i < number - 1; i++) {
                //transform output into input and add the direction
                OutputNetwork outLocal = new OutputNetwork();
                outLocal.deserialise(lastOutput);
                InputNetwork inputLocal = new InputNetwork(directionAPF, outLocal.getSpeed(), outLocal.getBearing());
                lastOutput = model.computeOutput(inputLocal.serialise());

                if(ReadConfig.debug) logger.log(Level.INFO, "Output LSTM ->" + lastOutput.toString());

                out = new OutputNetwork();
                out.deserialise(lastOutput);
                outputsNetworks.add(out);
            }
            //assign the output to this individual
            currentInputsNetwork.setOutputComputed(outputsNetworks);

            //create the output already computed
            currentInputsNetwork.createRealOutputConverted();
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
                if (ReadConfig.debug) logger.log(Level.INFO, "Output network ->" + result.toString() + " realValue -> " + result.getRealValue() + " -->" + real);


                double decision = result.getRealValue();

                if( decision>0.5 ) {
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



        boolean score;
        try {
            score = ReadConfig.Configurations.getScore();
        } catch (Exception e) {
            score = false;
        }

        //transform the outputs into the input for the classifiers
        for(Individual ind : super.getPopulationWithHallOfFame()){
            //transform trajectory in advance to prevent multiprocessing errors
            List<TrainReal> inputOutput = ind.getMyInputandOutput();
            inputOutput.forEach(trainReal -> {
                ((FollowingTheGraph)transformation).setLastPoint(trainReal.getLastPoint());
                transformation.transform(trainReal);
            });
        }


        logger.log(Level.SEVERE, "Start real classification");

//         launch my way to compute the fitness
//        ExecutorService exec = Executors.newFixedThreadPool(16);
//        CountDownLatch latch = new CountDownLatch(super.getPopulationWithHallOfFame().size());
//        ComputeUnit[] runnables = new ComputeUnit[super.getPopulationWithHallOfFame().size()];
//
//
//        for(int i = 0; i < super.getPopulationWithHallOfFame().size(); i ++){
//            runnables[i] = new ComputeUnit(super.getPopulationWithHallOfFame().get(i), competingPopulation.getPopulationWithHallOfFame(), score, this.scores);
//        }
//        for(ComputeUnit r : runnables) {
//            r.setLatch(latch);
//            exec.execute(r);
//        }
//        try {
//            latch.await();
//            exec.shutdown();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        // number real trajectories = total number of trajectories said as real = # trajectories trained * # individuals
        int numTraTra;
        try{
            numTraTra = ReadConfig.Configurations.getTrajectoriesTrained();
        } catch (Exception e){
            numTraTra = 1;
        }
        int R = numTraTra * super.getPopulationWithHallOfFame().size();

        /**
         * Class for multithreading
         * Run one agent against all the classifiers
         * Compute the fitness function in this way:
         *
         * R =  #real
         * T_j = sum_i( classifier_j(agent_i))   <- in this module
         * Y_ij = R * classifier_j(agent_i) / T_j
         * E_ij = { i = real : 1 - Y_ij, i = fake: Y_ij }
         *
         * FitnessAgent = sum_j( E_ij )
         * FitnessClassifier = sum_i( 1 - E_ij)
         *
         */
        class ComputeSelmarFitnessUnit implements Runnable{
            private CountDownLatch latch;
            private Individual classifier;
            private List<Individual> adersarialPopulation;
            private HashMap<Integer, Double> classifierResultAgentI;


            /**
             * Constructor
             * @param classifier current classifier
             * @param adersarialPopulation adversarial population

             */
            private ComputeSelmarFitnessUnit(Individual classifier, List<Individual> adersarialPopulation) {
                this.classifier = classifier;
                this.adersarialPopulation = adersarialPopulation;
                this.classifierResultAgentI = new HashMap<>();
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
             * Getter for the results from the classification
             * @return HashMap<Integer, Double> containing id agent and his classification
             */
            private HashMap<Integer, Double> getResults(){
                return this.classifierResultAgentI;
            }

            /**
             * Getter for the classifier's model ID
             * @return int id
             */
            private int getClassifierID(){
                return this.classifier.getModel().getId();
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
                for(Individual agent: this.adersarialPopulation){
                    List<TrainReal> inputOutput = agent.getMyInputandOutput();
                    for(TrainReal example: inputOutput){
                        //run the classifier for the Fake trajectory
                        int agentId = agent.getModel().getId();
                        try {
                            tgcfs.Classifiers.OutputNetwork result = (tgcfs.Classifiers.OutputNetwork) competingPopulation.runIndividual(classifier, example.getAllThePartTransformedFake());
                            if (ReadConfig.debug) logger.log(Level.INFO, "Fake Output network ->" + result.toString() + " realValue -> " + result.getRealValue());
                            //save all the results
                            this.classifierResultAgentI.put(agentId, result.getRealValue());
                        } catch (Exception e) {
                            logger.log(Level.SEVERE, "Error Classifier Fake Input" + e.getMessage());
                            e.printStackTrace();
                        }

                        int realAgentId = example.getIdRealPoint().getId();
                        //run the classifier for the Real trajectory
                        try {
                            tgcfs.Classifiers.OutputNetwork resultReal = (tgcfs.Classifiers.OutputNetwork) competingPopulation.runIndividual(classifier, example.getAllThePartTransformedReal());
                            if (ReadConfig.debug) logger.log(Level.INFO, "Real Output network ->" + resultReal.toString() + " realValue -> " + resultReal.getRealValue());
                            //save all the results
                            this.classifierResultAgentI.put(realAgentId, resultReal.getRealValue());
                        } catch (Exception e) {
                            logger.log(Level.SEVERE, "Error Classifier Real Input" + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }
                latch.countDown();
            }

        }

        HashMap<Integer, HashMap<Integer, Double>> results = new HashMap<>();

        ExecutorService exec = Executors.newFixedThreadPool(16);
        CountDownLatch latch = new CountDownLatch(competingPopulation.getPopulationWithHallOfFame().size());
        ComputeSelmarFitnessUnit[] runnables = new ComputeSelmarFitnessUnit[competingPopulation.getPopulationWithHallOfFame().size()];

        for(int i = 0; i < competingPopulation.getPopulationWithHallOfFame().size(); i ++){
            runnables[i] = new ComputeSelmarFitnessUnit(competingPopulation.getPopulationWithHallOfFame().get(i), super.getPopulationWithHallOfFame());
        }
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
            }

            //creation all the Tj
            //T_j = sum_i( classifier_j(agent_i))
            HashMap<Integer, Double> T = new HashMap<>();
            for(int key: results.keySet()){
                //key is the classifier id -> i
                HashMap<Integer, Double> classifierResultAgentI = results.get(key);
                double singleTj = classifierResultAgentI.values().stream().mapToDouble(i->i).sum();
                T.put(key, singleTj);
            }

            //Creation matrix Yij
            //Y_ij = R * classifier_j(agent_i) / T_j
            //E_ji = { i = real : 1 - Y_ij, i = fake: Y_ij } ^ 2
//        HashMap<String, Double> E = new HashMap<>();
//            HashMap<Integer, HashMap<Integer, Double>> Y = new HashMap<>();
            HashMap<Integer, HashMap<Integer, Double>> E = new HashMap<>();
            //Classifier Id
            for(int classifierID: results.keySet()){
                HashMap<Integer, Double> classifierResultAgentI = results.get(classifierID);

                //now I have all the Agent ID with their values
                //Creation id agent,classifier
                HashMap<Integer, Double> subE = new HashMap<>();
                //for debug, remove this hashmap, it is redundant //TODO
//                HashMap<Integer, Double> subY = new HashMap<>();
                for(int agentID: classifierResultAgentI.keySet()){

                    //now I have classifier j and looping over agent i

//                String ij = agentID.toString() + "/" + classifierID.toString();
                    double y = (R * classifierResultAgentI.get(agentID) / T.get(classifierID));
                    if(Double.isNaN(y)){
                        y = 0.0;
                    }
                    if(y > 1.0) y = 1.0;
                    if(y < 0.0) y = 0.0;
                    //for debug, remove this hashmap, it is redundant //TODO
//                    subY.put(agentID, y);
                    if(classifierResultAgentI.get(agentID) > 0.5){
                        //if TRUE=real
                        subE.put(agentID, Math.pow(1 - y, 2));
                    }else{
                        //if False=False
                        subE.put(agentID, Math.pow(y, 2));
                    }
                }
                E.put(classifierID, subE);
                //for debug, remove this hashmap, it is redundant //TODO
//                Y.put(classifierID, subY);
            }


            //FitnessAgent = sum_j( E_ij )

            //fitness agents
            for(Individual agent : super.getPopulationWithHallOfFame()){
                int id = agent.getModel().getId();
                double fitness = 0;
                //need to find all the values with that id
                for(int classifierID: E.keySet()){
                    fitness += E.get(classifierID).get(id);
                }
                agent.setFitness(fitness);
                //setting the fitness for the examples of this agent
                for(TrainReal example: agent.getMyInputandOutput()){
                    example.setFitnessGivenByTheClassifier(fitness);
                }

            }


            //FitnessClassifier = sum_i( 1 - E_ij)
            for(Individual classifier: competingPopulation.getPopulationWithHallOfFame()){
                int id = classifier.getModel().getId();
                HashMap<Integer, Double> allTheI = E.get(id);
                double fitness = 0;
                for(int agentId: allTheI.keySet()){
                    fitness += (1 - allTheI.get(agentId));
                }
                classifier.setFitness(fitness);
            }


        } catch (InterruptedException e) {
            e.printStackTrace();
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

        for(Individual ind: super.getPopulation()){
            for(TrainReal train: ind.getMyInputandOutput()){
                totalList.add(train.deepCopy());
            }
        }

        SaveToFile.Saver.dumpTrajectoryAndGeneratedPart(totalList, generationAgent, generationClassifier);
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
    public void generateRealPoints(FollowingTheGraph transformation){
        for(Individual ind: super.getPopulation()) {
            for (TrainReal train : ind.getMyInputandOutput()) {
                if(train.getRealPointsOutputComputed() == null) {
                    List<PointWithBearing> generatedPoint = new ArrayList<>();
                    transformation.setLastPoint(train.getLastPoint());
                    for(OutputsNetwork outputsNetwork: train.getOutputComputed()){
                        generatedPoint.add(new PointWithBearing(transformation.singlePointConversion(outputsNetwork)));
                    }
                    train.setRealPointsOutputComputed(generatedPoint);
                }
            }
        }
    }

}


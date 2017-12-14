package tgcfs.EA;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;
import tgcfs.Agents.InputNetwork;
import tgcfs.Agents.Models.LSTMAgent;
import tgcfs.Agents.OutputNetwork;
import tgcfs.Config.ReadConfig;
import tgcfs.InputOutput.FollowingTheGraph;
import tgcfs.InputOutput.Transformation;
import tgcfs.Loader.TrainReal;
import tgcfs.NN.InputsNetwork;
import tgcfs.NN.OutputsNetwork;
import tgcfs.Performances.SaveToFile;
import tgcfs.Utils.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
 * Class implementing the algorithm for the agents.
 */
public class Agents{
    private List<IndividualAgent> population; //representation of the population
    private int maxFitnessAchievable;
    private static Logger logger;
    private MultyScores scores;

    /**
     * Constructor zero parameter
     * Call the super constructor
     * @param log logger
     * @throws Exception if the super constructor has problem in reading the config files
     */
    public Agents(Logger log) throws Exception {
        logger = log;
        this.population = new ArrayList<>();
        this.maxFitnessAchievable = 0;
        this.scores = new MultyScores();
    }

    /**
     * Generate the population for the EA
     * set the max fitness achievable by an agent
     * @param model the model of the population
     * @throws Exception exception
     */
    public void generatePopulation(LSTMAgent model) throws Exception {
        this.generatePopulationAgent(model);
        this.maxFitnessAchievable = (ReadConfig.Configurations.getClassifierPopulationSize() + ReadConfig.Configurations.getClassifierOffspringSize()) * ReadConfig.Configurations.getTrajectoriesTrained();
    }

    private void generatePopulationAgent(LSTMAgent model)  throws Exception {
        int size = ReadConfig.Configurations.getAgentPopulationSize();
        IndividualStatus status = IndividualStatus.AGENT;
        logger.log(Level.INFO, "Generating Agents Population...");
        IntStream.range(0, size).forEach(i ->{
            try {
                IndividualAgent newBorn = new IndividualAgent(model.getArrayLength(), status);
                //assign the model to the classifier
                newBorn.setModel(model.deepCopy());
                this.population.add(newBorn);
            } catch (Exception e) {
                logger.log(Level.WARNING, "Error with generating population");
                e.printStackTrace();
            }

        });
    }


    /**
     * @implNote Implementation from Abstract class Algorithm
     * @param input the input of the model
     * @throws Exception if there are problems in reading the info
     */
    public void runIndividuals(List<TrainReal> input) throws Exception {
        //reset input
        this.population.forEach(IndividualAgent::resetInputOutput);

        //every individual in parallel
        this.population.parallelStream().forEach(individual -> {
            try {
                //retrieve model from the individual
                LSTMAgent model = individual.getModel();
                //set the weights
                model.setWeights(individual.getObjectiveParameters());
                //select which model I am using
                this.runLSTM(input, model, individual);

            } catch (Exception e) {
                logger.log(Level.SEVERE, "Errors with the neural network " + e.getMessage());
                e.printStackTrace();
            }

        });
    }

    /**
     * Run the LSTM agent
     * @param input the input of the model
     * @param model the model LSTM used
     * @param individual the individual under evaluation
     * @throws Exception if something bad happened
     */
    private void runLSTM(List<TrainReal> input, LSTMAgent model, IndividualAgent individual) throws Exception {
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
            List<INDArray> saveList = new ArrayList<>();
            for (int j = 0; j < size; j++) {
                INDArray vector = in.get(j).serialise();
                if(ReadConfig.debug) {
                    saveList.add(vector);
                }
                features.put(new INDArrayIndex[]{NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.point(j)}, vector);
            }
            lastOutput = model.computeOutput(features);
            int timeSeriesLength = lastOutput.size(2);		//Size of time dimension

            List<INDArray> saveList2 = new ArrayList<>();
            for (int i = 0; i< timeSeriesLength; i++){
                saveList2.add(lastOutput.get(NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.point(i)));
            }
            if(ReadConfig.debug) logger.log(Level.INFO, "Input LSTM ->"  + this.logINDArray(saveList) +  " // Output LSTM ->" + this.logINDArray(saveList2) + " // Model Weights" + this.printBetterRepresentation(model.getWeights()));


            INDArray realLastOut = lastOutput.get(NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.point(timeSeriesLength-1));
            if(ReadConfig.debug) logger.log(Level.INFO, "Real Last Output -> " + realLastOut);

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

                logger.log(Level.INFO, "Output LSTM ->" + lastOutput.toString());

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
    public OutputsNetwork runIndividual(LSTMAgent individual, List<InputsNetwork> input) throws Exception {
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
    public void evaluateIndividuals(Classifiers model, Transformation transformation){
        this.population.forEach(a -> {
            //transform trajectory in advance to prevent multiprocessing errors
            List<TrainReal> inputOutput = a.getMyInputandOutput();
            inputOutput.forEach(trainReal -> {
                ((FollowingTheGraph)transformation).setLastPoint(trainReal.getLastPoint());
                transformation.transform(trainReal);
            });
        });

        //I need to evaluate the agent using the classifiers
        this.population.parallelStream().forEach(agent -> {
//            System.out.println(LocalDateTime.now().toString()  + "  Evaluation individual--------------");
            //The fitness of each model is obtained by evaluating it with each of the classifiers in the competing population
            //For every classifier that wrongly judges the model as being the real agent, the model’s fitness increases by one.

//            //transform trajectory in advance to prevent multiprocessing errors
            List<TrainReal> inputOutput = agent.getMyInputandOutput();
//            inputOutput.forEach(trainReal -> {
//                ((FollowingTheGraph)transformation).setLastPoint(trainReal.getLastPoint());
//                transformation.transform(trainReal);
//            });



            //for every example I need to run the classifier and check the result
            model.getPopulation().parallelStream().forEach(classifier -> {

                //this is one agent
                //I need to check for every output for every individual
                inputOutput.parallelStream().forEach(trainReal -> {

                    List<InputsNetwork> inputFake = trainReal.getAllThePartTransformedFake();


                    //run the classifier for the Fake trajectory
                    try {
                        this.runClassifier(model ,agent, classifier, inputFake, Boolean.TRUE);
                    } catch (Exception e) {
                        logger.log(Level.SEVERE, "Error Classifier Fake Input" + e.getMessage());
                        e.printStackTrace();
                    }

                    //run the classifier for the Real trajectory
                    List<InputsNetwork> inputReal = trainReal.getAllThePartTransformedReal();

                    try {
                        this.runClassifier(model ,agent, classifier, inputReal, Boolean.FALSE);
                    } catch (Exception e) {
                        logger.log(Level.SEVERE, "Error Classifier Real Input" + e.getMessage());
                        e.printStackTrace();
                    }


                });
            });
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
    private synchronized void runClassifier(Classifiers model, IndividualAgent agent, IndividualClassifier classifier, List<InputsNetwork> input, boolean real) throws Exception {
        tgcfs.Classifiers.OutputNetwork result = (tgcfs.Classifiers.OutputNetwork) model.runIndividual(classifier, input);


        double decision = result.getRealValue01();

        if( decision>0.5 ) {
            //it is saying it is true
            //counting this only if the fake trajectory
            if(real) {
                agent.increaseFitness(decision);
                classifier.increaseFitness(1 - decision);
                Scores sc = new Scores(agent.getModel().getId(),0, classifier.getModel().getId(), 0d);
                this.scores.addScore(sc);
            }
        }else{
            //it is false
            classifier.increaseFitness(decision);
            if(real) {
                agent.increaseFitness(1 - decision);
                Scores sc = new Scores(agent.getModel().getId(), 0, classifier.getModel().getId(), 1d);
                this.scores.addScore(sc);
            }
        }




    }

    /**
     * Method to train the network with the input selected
     * @param combineInputList where to find the input to train
     */
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


    /**
     * Save in JSON format the trajectory and the generated part of it
     * @param generationAgent number of generation for the agent population
     * @param generationClassifier number of generation for the classifier population
     * @param transformation {@link FollowingTheGraph} transformation reference to transform the output in real point //TODO generalise this
     * @throws Exception if something wrong happens in saving everything
     */
    public void saveTrajectoriesAndPointGenerated(int generationAgent, int generationClassifier, FollowingTheGraph transformation) throws Exception {
        List<TrainReal> totalList = new ArrayList<>();
        this.population.forEach(individual -> individual.getMyInputandOutput().forEach(tra -> totalList.add(tra.deepCopy())));


        totalList.forEach(t -> {
            if(t.getRealPointsOutputComputed() == null) {
                List<PointWithBearing> generatedPoint = new ArrayList<>();
                transformation.setLastPoint(t.getLastPoint());
                t.getOutputComputed().forEach(outputsNetwork -> generatedPoint.add(new PointWithBearing(transformation.singlePointConversion(outputsNetwork))));
                t.setRealPointsOutputComputed(generatedPoint);
            }
        });

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
     * Select parents for the next generation.
     *
     * I will keep only the best one and then replace all the rest of the population with the sons.
     *
     *
     * @throws Exception if there are problems in reading the info
     */
    public void survivalSelections() throws Exception {
        //check which class is calling this method
        int size = ReadConfig.Configurations.getAgentPopulationSize();

        //sort the list
        this.population.sort(Comparator.comparing(IndividualAgent::getFitness));

        //log the fitness of all the population
        List<Double> fitn = new ArrayList<>();
        this.population.forEach(p -> fitn.add(p.getFitness()));

        logger.log(Level.INFO, "--Fitness population before selection--");
        logger.log(Level.INFO, fitn.toString());


        while(this.population.size() != size){
            this.population.remove(0);
        }

        List<IndividualAgent> newList = new ArrayList<>();
        this.population.forEach(p -> newList.add(p.deepCopy()));

        List<Double> fitnd = new ArrayList<>();
        newList.forEach(p -> fitnd.add(p.getFitness()));

        logger.log(Level.INFO, "--Fitness population after selection--");
        logger.log(Level.INFO, fitnd.toString());

        this.population = new ArrayList<>();
        this.population = newList;
        //now the population is again under the maximum size allowed and containing only the element with highest fitness.

        //check who is parents and who is son
        List<Integer> sonAndParent = new ArrayList<>();
        this.population.forEach(p -> {
            if(p.isSon()){
                // zero for offspring
                sonAndParent.add(0);
            }else{
                // one for parent
                sonAndParent.add(1);
            }
        });
        logger.log(Level.INFO, "--Parents[1] vs Sons[0]--");
        logger.log(Level.INFO, sonAndParent.toString());
    }

    public int getMaxFitnessAchievable() {
        return maxFitnessAchievable;
    }

    /**
     * Method to return the fitness of all the individuals
     * @return list of integer values
     */
    public List<Double> retAllFitness(){
        List<Double> list = new ArrayList<>();
        this.population.forEach(individual -> list.add(individual.getFitness()));
        return list;
    }

    /**
     * Method that returns the best genome in the population
     * @return list of doubles
     */
    public INDArray retBestGenome(){
        //sort the list
        this.population.sort(Comparator.comparing(IndividualAgent::getFitness));
        return this.population.get(0).getObjectiveParameters();
    }

    /**
     * Get the fittest individual of the population
     * @return fittest {@link IndividualAgent}
     */
    public IndividualAgent getFittestIndividual(){
        //sort the list
        this.population.sort(Comparator.comparing(IndividualAgent::getFitness));
        return this.population.get(this.population.size() - 1);
    }


    /**
     * With neural networks I could suffer from COMPETING CONVENTION problem if I am using the crossover
     * If I only use the mutation I am not suffering from it
     * Every parents is randomly selected and an offspring is generated exactly as the father.
     * Mutation is then applied to it
     *
     * @throws Exception if the parents have not the same length
     */
    public void generateOffspringOnlyWithMutation() throws Exception {
        //check which class is calling this method
        int size = ReadConfig.Configurations.getAgentOffspringSize();
        int tournamentSize = ReadConfig.Configurations.getTournamentSizeAgents();
        IndividualStatus status = IndividualStatus.AGENT;

        //set everyone as a parent now
        this.population.forEach(IndividualAgent::isParent);

        //create offspring_size offspring
        for(int i = 0; i < size; i ++) {

            //creating the tournament
            List<IndividualAgent> tournamentPop = new ArrayList<>();
            IntStream.range(0, tournamentSize).forEach(j -> {
                int idParent = RandomGenerator.getNextInt(0,this.population.size());
                logger.log(Level.FINE, "idParent for tournament selection: " + idParent);
                IndividualAgent ind = this.population.get(idParent);
                logger.log(Level.FINE,  idParent + ": " + ind.getObjectiveParameters().toString());
                tournamentPop.add(ind);
            });
            //find the winner of the tournament -> the one with the highest fitness
            tournamentPop.sort(Comparator.comparingDouble(IndividualAgent::getFitness));


            //log the fitness of all the tournament
            List<Double> fitn = new ArrayList<>();
            tournamentPop.forEach(p -> fitn.add(p.getFitness()));

            logger.log(Level.FINE, "--Fitness population on the tournament--");
            logger.log(Level.FINE, fitn.toString());


            //last one has the better fitness
            IndividualAgent parent = tournamentPop.get(tournamentPop.size() - 1);
            logger.log(Level.FINE, "Parent selected to mutate: \n" + parent.getObjectiveParameters());

            //son has the same genome of the father
            IndividualAgent son = new IndividualAgent(parent.getObjectiveParameters().dup(), status, true);

            //now the son is mutated 10 times (hardcoded value)
            //IntStream.range(0, 10).forEach(it -> son.mutate(son.getObjectiveParameters().columns()));
            son.mutate(son.getObjectiveParameters().columns());
            logger.log(Level.FINE, "Son: \n" + son.getObjectiveParameters());
            //set model to the son
            son.setModel(parent.getModel().deepCopy());

            //add the son to the population
            this.population.add(son);
        }

        //resetting the fitness of everyone
        this.resetFitness();
    }

    /**
     * Reset the fitness of all the individual
     */
    public void resetFitness(){
        this.population.forEach(IndividualAgent::resetFitness);
    }

    /**
     * Getter for the population
     * @return list of individuals
     */
    public List<IndividualAgent> getPopulation() {
        return this.population;
    }

    /**
     * Print an array of INDarray in a nice format
     * @param list list of {@link INDArray}
     * @return string
     */
    public String logINDArray(List<INDArray> list){
        List<String> print = new ArrayList<>();
        list.forEach(el -> {
            List<Double> d = this.printBetterRepresentation(el);
            print.add(d.toString());
        });
        return print.toString();
    }
    /**
     * Print a better representation of the weights
     * @param array {@link INDArray} array of weights
     * @return array transformed in list of double
     */
    public List<Double> printBetterRepresentation(INDArray array){
        List<Double> list = new ArrayList<>();
        for(int i=0; i< array.columns(); i++){
            list.add(array.getDouble(i));
        }
        return list;
    }
}


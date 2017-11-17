package tgcfs.EA;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;
import tgcfs.Agents.InputNetwork;
import tgcfs.Agents.Models.RealAgents;
import tgcfs.Classifiers.Models.ENNClassifier;
import tgcfs.Classifiers.OutputNetwork;
import tgcfs.Config.ReadConfig;
import tgcfs.InputOutput.Transformation;
import tgcfs.Loader.TrainReal;
import tgcfs.NN.EvolvableModel;
import tgcfs.NN.InputsNetwork;
import tgcfs.NN.OutputsNetwork;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
     * @param logger logger
     * @throws Exception if the super constructor has problem in reading the config files
     */
    public Classifiers(Logger logger) throws Exception {
        super(logger);
    }

    /**
     * Generate the population for the EA
     * set the max fitness achievable by an classifier
     * @param model the model of the population
     * @throws Exception exception
     */
    @Override
    public void generatePopulation(EvolvableModel model) throws Exception {
        super.generatePopulation(model);
        this.maxFitnessAchievable = ((ReadConfig.Configurations.getAgentPopulationSize() + ReadConfig.Configurations.getAgentOffspringSize()) * ReadConfig.Configurations.getTrajectoriesTrained()) * 2;
    }

    /**
     * Generate the population for the EA
     * set the max fitness achievable by an classifier
     * using the information loaded from file
     * @param model the model of the population
     * @param populationLoaded the popolation loaded from file
     * @throws Exception exception
     */
    @Override
    public void generatePopulation(EvolvableModel model, List<INDArray> populationLoaded) throws Exception {
        super.generatePopulation(model, populationLoaded);
        this.maxFitnessAchievable = ((ReadConfig.Configurations.getAgentPopulationSize() + ReadConfig.Configurations.getAgentOffspringSize()) * ReadConfig.Configurations.getTrajectoriesTrained()) * 2;
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
    @Override
    public synchronized OutputsNetwork runIndividual(Individual individual, List<InputsNetwork> input) throws Exception {
        //retrive model from the individual
        EvolvableModel model = individual.getModel();
        //set the weights
        model.setWeights(individual.getObjectiveParameters());
        //compute Output of the network


        INDArray lastOutput = null;
        OutputNetwork out = new OutputNetwork();
        if(model.getClass().equals(ENNClassifier.class)){
            //if the model is ENN
            for (InputsNetwork inputsNetwork : input) {
                lastOutput = model.computeOutput(inputsNetwork.serialise());
            }
            //I am interested only in the last output of this network
            out.deserialise(lastOutput);
            ((ENNClassifier)model).cleanParam();
        }else {
            //else
            //if it is a lstm
            int size = input.size();
            INDArray features = Nd4j.create(new int[]{1, tgcfs.Classifiers.InputNetwork.inputSize, size}, 'f');
            for (int j = 0; j < size; j++) {
                INDArray vector = input.get(j).serialise();
                features.put(new INDArrayIndex[]{NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.point(j)}, vector);
            }
            lastOutput = model.computeOutput(features);
            logger.log(Level.INFO, lastOutput.toString());
            int timeSeriesLength = lastOutput.size(2);		//Size of time dimension
            INDArray realLastOut = lastOutput.get(NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.point(timeSeriesLength-1));
            //I am interested only in the last output of this network
            out.deserialise(realLastOut);
        }



        return out;
    }


    /**
     * @implNote Implementation from Abstract class Algorithm
     * @param opponent competing population
     * @param transformation the class that will transform from one output to the new input
     */
    @Override
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
        super.getPopulation().parallelStream().forEach(individual -> {
            try {
                individual.fitModel(this.createDataSet(combineInputList));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        throw new Error("Method not usable for a Classifier");
    }

    /**
     * Evaluate the classifier on the real agent
     * Each classifier is evaluated on the real agent oer "agent_population" times
     * @param agents the real agent
     */
    public void evaluateRealAgent(RealAgents agents, Transformation transformation){
        throw new NoSuchMethodError("Not needed anymore");
//        super.getPopulation().parallelStream().forEach(individual -> {
//            try {
//                //evaluate classifier with real agents
//                agents.getRealAgents().forEach(agent -> {
//                    try {
//                        ((FollowingTheGraph)transformation).setLastPoint(agent.getLastPoint());
//                        OutputNetwork result = (tgcfs.Classifiers.OutputNetwork) this.runIndividual(individual, transformation.transform(agent.realOutput(),agent.getRealFirstPart()));
//                        //if the classifier is saying true -> it is correctly judging the agent
//                        if(result.getReal()){
//                            individual.increaseFitness();
//                        }
//                    } catch (Exception e) {
//                        logger.log(Level.SEVERE, "Errors with the neural network" + e.getMessage() + " " + e.toString());
//                    }
//                });
//            } catch (Exception e) {
//                logger.log(Level.SEVERE, "Error with the file" + e.getMessage());
//            }
//        });
    }


    /**
     * Create a Dataset instance of the data present in this object
     * The Dataset instance let the network train on the data present in this object
     *
     * @param input list of {@link TrainReal} objects
     * @return {@link DataSet} object
     */
    public DataSet createDataSet(List<TrainReal> input){
        //need to compute maximum length of the trajectories
        int maxLength = Integer.MIN_VALUE;
        for(TrainReal element: input){
            int size = element.getTotalList(true).size();
            if(size > maxLength){
                maxLength = size;
            }
        }

        int vectorSize = tgcfs.Classifiers.InputNetwork.inputSize;

        //Create data for training
        //Here: we have input.size() examples of varying lengths with vectorSize features
        INDArray features = Nd4j.create(new int[]{input.size(), vectorSize, maxLength}, 'f');
        INDArray labels = Nd4j.create(new int[]{input.size(), 2, maxLength}, 'f');    //Two labels: real or fake
        //Because we are dealing with trajectories of different lengths and only one output at the final time step: use padding arrays
        //Mask arrays contain 1 if data is present at that time step for that example, or 0 if data is just padding
        INDArray featuresMask = Nd4j.zeros(input.size(), maxLength);
        INDArray labelsMask = Nd4j.zeros(input.size(), maxLength);


        /**
         * Class that helps to administrate the element
         */
        class Example{
            private List<INDArray> array;
            private boolean label;

            /**
             * Constructor of one example with two parameters
             * @param array list of {@link INDArray} elements
             * @param label label of the list (Boolean)
             */
            public Example(List<INDArray> array, Boolean label){
                this.array = array;
                this.label = label;
            }

            /**
             * Getter for the array
             * @return list of {@link INDArray} elements
             */
            public List<INDArray> getArray() {
                return array;
            }

            /**
             * Getter for the label
             * @return label of the list (Boolean)
             */
            public Boolean getLabel() {
                return label;
            }
        }

        //generate the list of examples
        List<Example> generalList = new ArrayList<>();
        input.forEach(element -> {
            List<INDArray> totalListFalse = new ArrayList<>();
            element.getTrainingPoint().forEach(trainingPoint -> totalListFalse.add(((InputNetwork)trainingPoint).serialiaseAsInputClassifier()));
            element.getOutputComputed().forEach(trainingPoint -> totalListFalse.add(((tgcfs.Agents.OutputNetwork)trainingPoint).serialiaseAsInputClassifier()));
            generalList.add(new Example(totalListFalse, Boolean.FALSE));

            List<INDArray> totalListTrue = new ArrayList<>();
            element.getTrainingPoint().forEach(trainingPoint -> totalListTrue.add(((InputNetwork)trainingPoint).serialiaseAsInputClassifier()));
            element.getFollowingPartTransformed().forEach(trainingPoint -> totalListTrue.add(((InputNetwork)trainingPoint).serialiaseAsInputClassifier()));
            generalList.add(new Example(totalListTrue, Boolean.TRUE));
        });

        //shuffle list
        Collections.shuffle(generalList);


        //for every element into the input
        int[] temp = new int[2];
        for(int i = 0; i < generalList.size(); i++ ){
            Example element = generalList.get(i);
            temp[0] = i;

            for(int j = 0; j < element.getArray().size() && j < maxLength; j++){
                INDArray vector = element.getArray().get(j);
                features.put(new INDArrayIndex[]{NDArrayIndex.point(i), NDArrayIndex.all(), NDArrayIndex.point(j)}, vector);
                temp[1] = j;
                featuresMask.putScalar(temp, 1.0);  //Word is present (not padding) for this example + time step -> 1.0 in features mask
            }
            int idx = (element.getLabel() ? 0 : 1);
            int lastIdx = Math.min(element.getArray().size(),maxLength);
            labels.putScalar(new int[]{i,idx,lastIdx-1},1.0);   //Set label: [0,1] for false, [1,0] for true
            labelsMask.putScalar(new int[]{i,lastIdx-1},1.0);   //Specify that an output exists at the final time step for this example
        }


        return new DataSet(features,labels,featuresMask,labelsMask);
    }

    /**
     * Select parents for the next generation.
     *
     * I will keep only the best one and then replace all the rest of the population with the sons.
     *
     *
     * @throws Exception if there are problems in reading the info
     */
    @Override
    public void survivalSelections() throws Exception {
        if(ReadConfig.Configurations.getDifferentSelectionForClassifiers() == 0) {
            super.survivalSelections();
        }else{
            //log the fitness of all the population
            List<Double> fitn = new ArrayList<>();
            super.getPopulation().forEach(p -> fitn.add(p.getFitness()));

            logger.log(Level.INFO, "--Fitness population before selection--");
            logger.log(Level.INFO, fitn.toString());

            List<Individual> nextGeneration = new ArrayList<>();

            // lets find out who are all the sons
            List<Individual> sons = super.getPopulation().stream().filter(Individual::isSon).collect(Collectors.toList());
            List<Individual> parents = super.getPopulation().stream().filter(individual -> !individual.isSon()).collect(Collectors.toList());

            // keep the best parents -> order the parents and keep the one with highest fitness
            parents.sort(Comparator.comparing(Individual::getFitness));
            nextGeneration.add(parents.get(parents.size() - 1).deepCopy());

            int size = ReadConfig.Configurations.getClassifierPopulationSize();

            sons.forEach(s -> nextGeneration.add(s.deepCopy()));

            while(nextGeneration.size() > size){
                nextGeneration.remove(nextGeneration.size() - 1);
            }
            nextGeneration.sort(Comparator.comparing(Individual::getFitness));


            List<Double> fitnd = new ArrayList<>();
            nextGeneration.forEach(p -> fitnd.add(p.getFitness()));

            logger.log(Level.INFO, "--Fitness population after selection--");
            logger.log(Level.INFO, fitnd.toString());

            super.setPopulation(nextGeneration);

            //check who is parents and who is son
            List<Integer> sonAndParent = new ArrayList<>();
            super.getPopulation().forEach(p -> {
                if(p.isSon()){
                    // zero for offspring
                    sonAndParent.add(0);
                }else{
                    // one for parent
                    sonAndParent.add(1);
                }
            });
            logger.log(Level.INFO, "--Parents vs Sons--");
            logger.log(Level.INFO, sonAndParent.toString());
        }
    }


}

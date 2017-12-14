package tgcfs.EA;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;
import tgcfs.Classifiers.Models.ENNClassifier;
import tgcfs.Classifiers.OutputNetwork;
import tgcfs.Config.ReadConfig;
import tgcfs.Loader.TrainReal;
import tgcfs.NN.InputsNetwork;
import tgcfs.NN.OutputsNetwork;
import tgcfs.Utils.IndividualStatus;
import tgcfs.Utils.RandomGenerator;

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
 * Class implementing the algorithm for the classifiers.
 */
public class Classifiers{
    private List<IndividualClassifier> population; //representation of the population
    private int maxFitnessAchievable;
    private static Logger logger;

    /**
     * Constructor zero parameter
     * Call the super constructor
     * @param log logger
     * @throws Exception if the super constructor has problem in reading the config files
     */
    public Classifiers(Logger log) throws Exception {
        logger = log;
        this.population = new ArrayList<>();
        this.maxFitnessAchievable = 0;
    }

    /**
     * Generate the population for the EA
     * set the max fitness achievable by an classifier
     * @param model the model of the population
     * @throws Exception exception
     */
    public void generatePopulation(ENNClassifier model) throws Exception {
        this.generatePopulationAgent(model);
        this.maxFitnessAchievable = ((ReadConfig.Configurations.getAgentPopulationSize() + ReadConfig.Configurations.getAgentOffspringSize()) * ReadConfig.Configurations.getTrajectoriesTrained()) * 2;
    }


    private void generatePopulationAgent(ENNClassifier model)  throws Exception {
        int size = ReadConfig.Configurations.getAgentPopulationSize();
        IndividualStatus status = IndividualStatus.AGENT;
        logger.log(Level.INFO, "Generating Agents Population...");
        IntStream.range(0, size).forEach(i ->{
            try {
                IndividualClassifier newBorn = new IndividualClassifier(model.getArrayLength(), status);
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
        throw new Exception("Method not usable for a Classifier");
    }


    /**
     * @implNote Implementation from Abstract class Algorithm
     * @param individual individual with the parameter of the classifier
     * @param input input to assign to the classifier
     * @return the output of the classifier
     * @throws Exception if the nn has problem an exception is raised
     */
    public synchronized OutputsNetwork runIndividual(IndividualClassifier individual, List<InputsNetwork> input) throws Exception {
        //retrive model from the individual
        ENNClassifier model = individual.getModel();
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
     * Select parents for the next generation.
     *
     * I will keep only the best one and then replace all the rest of the population with the sons.
     *
     *
     * @throws Exception if there are problems in reading the info
     */
    public void survivalSelections() throws Exception {
        //check which class is calling this method
        int size = ReadConfig.Configurations.getClassifierPopulationSize();

        //sort the list
        this.population.sort(Comparator.comparing(IndividualClassifier::getFitness));

        //log the fitness of all the population
        List<Double> fitn = new ArrayList<>();
        this.population.forEach(p -> fitn.add(p.getFitness()));

        logger.log(Level.INFO, "--Fitness population before selection--");
        logger.log(Level.INFO, fitn.toString());


        while(this.population.size() != size){
            this.population.remove(0);
        }

        List<IndividualClassifier> newList = new ArrayList<>();
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


    /**
     * Getter for the population
     * @return list of individuals
     */
    public List<IndividualClassifier> getPopulation() {
        return this.population;
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
        this.population.sort(Comparator.comparing(IndividualClassifier::getFitness));
        return this.population.get(0).getObjectiveParameters();
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
        this.population.forEach(IndividualClassifier::isParent);

        //create offspring_size offspring
        for(int i = 0; i < size; i ++) {

            //creating the tournament
            List<IndividualClassifier> tournamentPop = new ArrayList<>();
            IntStream.range(0, tournamentSize).forEach(j -> {
                int idParent = RandomGenerator.getNextInt(0,this.population.size());
                logger.log(Level.FINE, "idParent for tournament selection: " + idParent);
                IndividualClassifier ind = this.population.get(idParent);
                logger.log(Level.FINE,  idParent + ": " + ind.getObjectiveParameters().toString());
                tournamentPop.add(ind);
            });
            //find the winner of the tournament -> the one with the highest fitness
            tournamentPop.sort(Comparator.comparingDouble(IndividualClassifier::getFitness));


            //log the fitness of all the tournament
            List<Double> fitn = new ArrayList<>();
            tournamentPop.forEach(p -> fitn.add(p.getFitness()));

            logger.log(Level.FINE, "--Fitness population on the tournament--");
            logger.log(Level.FINE, fitn.toString());


            //last one has the better fitness
            IndividualClassifier parent = tournamentPop.get(tournamentPop.size() - 1);
            logger.log(Level.FINE, "Parent selected to mutate: \n" + parent.getObjectiveParameters());

            //son has the same genome of the father
            IndividualClassifier son = new IndividualClassifier(parent.getObjectiveParameters().dup(), status, true);

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
        this.population.forEach(IndividualClassifier::resetFitness);
    }

    /**
     * Get the fittest individual of the population
     * @return fittest {@link IndividualClassifier}
     */
    public IndividualClassifier getFittestIndividual(){
        //sort the list
        this.population.sort(Comparator.comparing(IndividualClassifier::getFitness));
        return this.population.get(this.population.size() - 1);
    }


}

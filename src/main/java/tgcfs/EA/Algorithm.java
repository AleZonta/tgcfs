package tgcfs.EA;

import org.nd4j.linalg.api.ndarray.INDArray;
import tgcfs.Config.ReadConfig;
import tgcfs.EA.Mutation.NonUniformMutation;
import tgcfs.EA.Mutation.RandomResetting;
import tgcfs.EA.Mutation.UncorrelatedMutation;
import tgcfs.EA.Recombination.DiscreteRecombination;
import tgcfs.EA.Recombination.IntermediateRecombination;
import tgcfs.EA.Recombination.Recombination;
import tgcfs.InputOutput.Normalisation;
import tgcfs.InputOutput.Transformation;
import tgcfs.Loader.TrainReal;
import tgcfs.NN.EvolvableModel;
import tgcfs.NN.InputsNetwork;
import tgcfs.NN.OutputsNetwork;
import tgcfs.Utils.IndividualStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
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
 * The turing learning requires two sub-algorithms (one for the models and one for the classifier) which
 * are identical, and do not interact between them except for the fitness calculation step.
 * This class implement the common features of the algorithm
 */
public abstract class Algorithm {
    private List<Individual> population; //representation of the population
    protected int maxFitnessAchievable;
    protected static Logger logger;


    /**
     * Constructor zero parameter
     * Initialise the populaiton list
     * @param log log
     * @throws Exception exception if there is an error in readig the config file
     */
    public Algorithm(Logger log) throws Exception{
        this.population = new ArrayList<>();
        this.maxFitnessAchievable = 0;
        logger = log;
    }

    /**
     * Generate the population for the EA
     * @param model the model of the population
     * @throws Exception exception
     */
    public void generatePopulation(EvolvableModel model) throws Exception {
        //check which class is calling this method
        int size = 0;
        IndividualStatus status;
        if(this.getClass() == Agents.class){
            size = ReadConfig.Configurations.getAgentPopulationSize();
            status = IndividualStatus.AGENT;
            logger.log(Level.INFO, "Generating Agents Population...");
        }else{
            status = IndividualStatus.CLASSIFIER;
            size = ReadConfig.Configurations.getClassifierPopulationSize();
            logger.log(Level.INFO, "Generating Classifiers Population...");
        }
        IntStream.range(0, size).forEach(i ->{
            Individual newBorn;
            try {
                switch(ReadConfig.Configurations.getMutation()){
                    case 0:
                        newBorn = new UncorrelatedMutation(model.getArrayLength(), status);
                        break;
                    case 1:
                        newBorn = new RandomResetting(model.getArrayLength(), status);
                        break;
                    case 2:
                        newBorn = new NonUniformMutation(model.getArrayLength(), status);
                        break;
                    default:
                        throw new Exception("Mutation argument not correct");
                }
                //assign the model to the classifier
                newBorn.setModel(model.deepCopy());
                this.addIndividual(newBorn);
            } catch (Exception e) {
                logger.log(Level.WARNING, "Error with generating population");
                e.printStackTrace();
            }

        });
    }

    /**
     * Getter for the population
     * @return list of individuals
     */
    public List<Individual> getPopulation() {
        return this.population;
    }

    /**
     * Add one individual to the collection
     * @param individual individual to be added
     */
    public void addIndividual(Individual individual){
        this.population.add(individual);
    }

    /**
     * Generate the offspring following the idea used in
     * Li, W., Gauci, M., & Gross, R. (2013). A Coevolutionary Approach to Learn Animal Behavior Through Controlled
     * Interaction. In Gecco’13: Proceedings of the 2013 Genetic and Evolutionary Computation Conference (pp. 223–230).
     * http://doi.org/10.1145/2463372.2465801
     *
     * (µ + λ)evolution strategy
     * two individuals are chosen randomly, with replacement, from the parent population
     * Discrete and intermediary recombination are then used to generate the objective parameters and
     * themutation strengths of the recombined individual, respectively
     *
     * @throws Exception if the parents have not the same length or the value alpha is not okay
     */
    public void generateOffspring() throws Exception {
        //check which class is calling this method
        int size = 0;
        IndividualStatus status;
        if(this.getClass() == Agents.class){
            size = ReadConfig.Configurations.getAgentOffspringSize();
            status = IndividualStatus.AGENT;
        }else{
            size = ReadConfig.Configurations.getClassifierOffspringSize();
            status = IndividualStatus.CLASSIFIER;
        }
        //create offspring_size offspring
        for(int i = 0; i < size; i ++) {
            //two individuals are chosen randomly, with replacement, from the parent population
            int firstParentsIndex = ThreadLocalRandom.current().nextInt(this.population.size());
            int secondParentsIndex = ThreadLocalRandom.current().nextInt(this.population.size());
            Individual firstParents = this.population.get(firstParentsIndex);
            Individual secondParents = this.population.get(secondParentsIndex);

            //Discrete and intermediary recombination are then used to generate the objective parameters and
            //themutation strengths of the recombined individual, respectively
            Recombination obj = new DiscreteRecombination(firstParents.getObjectiveParameters().dup(), secondParents.getObjectiveParameters().dup());

            Individual son;
            switch(ReadConfig.Configurations.getMutation()){
                case 0:
                    Recombination mut = new IntermediateRecombination(((UncorrelatedMutation)firstParents).getMutationStrengths(), ((UncorrelatedMutation)secondParents).getMutationStrengths(), 0.5);
                    son = new UncorrelatedMutation(obj.recombination(), mut.recombination(), status);
                    break;
                case 1:
                    son = new RandomResetting(obj.recombination(), status);
                    break;
                case 2:
                    son = new NonUniformMutation(obj.recombination(), status);
                    break;
                default:
                    throw new Exception("Mutation argument not correct");
            }
            //set model to the son
            son.setModel(firstParents.getModel().deepCopy());

            //mutate the individual
            if(ReadConfig.Configurations.getMutation() == 0){
                throw new Exception("Not implemented");
            }

            //add the son to the population
            this.population.add(son);
        }

        //resetting the fitness of everyone
        this.resetFitness();
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
        int size = 0;
        IndividualStatus status;
        if(this.getClass() == Agents.class){
            size = ReadConfig.Configurations.getAgentOffspringSize();
            status = IndividualStatus.AGENT;
        }else{
            size = ReadConfig.Configurations.getClassifierOffspringSize();
            status = IndividualStatus.CLASSIFIER;
        }
        //create offspring_size offspring
        for(int i = 0; i < size; i ++) {

            //creating the tournament
            List<Individual> tournamentPop = new ArrayList<>();
            IntStream.range(0, ReadConfig.Configurations.getTournamentSize()).forEach(j -> {
                int idParent = ThreadLocalRandom.current().nextInt(this.population.size());
                tournamentPop.add(this.population.get(idParent));
            });
            //find the winner of the tournament -> the one with the highest fitness
            tournamentPop.sort(Comparator.comparingInt(Individual::getFitness));
            //last one has the better fitness
            Individual parent = tournamentPop.get(tournamentPop.size() - 1);
            //son has the same genome of the father
            Individual son = new RandomResetting(parent.getObjectiveParameters().dup(), status);
            //now the son is mutated 10 times (hardcoded value)
            //IntStream.range(0, 10).forEach(it -> son.mutate(son.getObjectiveParameters().columns()));
            son.mutate(son.getObjectiveParameters().columns());
            //set model to the son
            son.setModel(parent.getModel().deepCopy());

            //add the son to the population
            this.population.add(son);
        }

        //resetting the fitness of everyone
        this.resetFitness();
    }


    /**
     * Select parents for the next generation following the idea used in
     * Li, W., Gauci, M., & Gross, R. (2013). A Coevolutionary Approach to Learn Animal Behavior Through Controlled
     * Interaction. In Gecco’13: Proceedings of the 2013 Genetic and Evolutionary Computation Conference (pp. 223–230).
     * http://doi.org/10.1145/2463372.2465801
     *
     * the µ individuals with the highest fitness from the combined population (which contains µ + λ individuals),
     * are selected as the parents to form the population of the next generation.
     *
     * @throws Exception if there are problems in reading the info
     */
    public void survivalSelections() throws Exception {
        //check which class is calling this method
        int size = 0;
        if(this.getClass() == Agents.class){
            size = ReadConfig.Configurations.getAgentPopulationSize();
        }else{
            size = ReadConfig.Configurations.getClassifierPopulationSize();
        }

        //sort the list
        this.population.sort(Comparator.comparing(Individual::getFitness));

        //log the fitness of all the population
        List<Integer> fitn = new ArrayList<>();
        this.population.forEach(p -> fitn.add(p.getFitness()));

        logger.log(Level.INFO, "--Fitness population before selection--");
        logger.log(Level.INFO, fitn.toString());


        while(this.population.size() != size){
            this.population.remove(0);
        }

        List<Individual> newList = new ArrayList<>();
        this.population.forEach(p -> newList.add(p.deepCopy()));

        List<Integer> fitnd = new ArrayList<>();
        newList.forEach(p -> fitnd.add(p.getFitness()));

        logger.log(Level.INFO, "--Fitness population after selection--");
        logger.log(Level.INFO, fitnd.toString());

        this.population = new ArrayList<>();
        this.population = newList;
        //now the population is again under the maximum size allowed and containing only the element with highest fitness.
    }

    /**
     * Now we run the individual in order to collect the result.
     * Running an agent means set the weight to the neural network and obtain the results
     * @param input the input of the model
     * @throws Exception if there are problems in reading the info
     */
    public abstract void runIndividuals(List<TrainReal> input) throws Exception;

    /**
     * Run the classifier with the current input and obtain the result from the network
     * @param individual individual with the parameter of the classifier
     * @param input input to assign to the classifier
     * @return the output of the classifier
     * @throws Exception if the nn has problem an exception is raised
     */
    public abstract OutputsNetwork runIndividual(Individual individual, List<InputsNetwork> input) throws Exception;


    /**
     * Method to evaluate the individual using the competing population
     * @param opponent competing population
     * @param transformation the class that will transform from one output to the new input
     */
    public abstract void evaluateIndividuals(Algorithm opponent, Transformation transformation);


    /**
     * Method to return the fitness of all the individuals
     * @return list of integer values
     */
    public List<Integer> retAllFitness(){
        List<Integer> list = new ArrayList<>();
        this.population.forEach(individual -> list.add(individual.getFitness()));
        return list;
    }

    /**
     * Method that returns the best genome in the population
     * @return list of doubles
     */
    public INDArray retBestGenome(){
        //sort the list
        this.population.sort(Comparator.comparing(Individual::getFitness));
        return this.population.get(0).getObjectiveParameters();
    }

    /**
     * Method to train the network with the input selected
     * @param combineInputList where to find the input to train
     */
    public abstract void trainNetwork(List<TrainReal> combineInputList);


    /**
     * Reset the fitness of all the individual
     */
    public void resetFitness(){
        this.population.forEach(Individual::resetFitness);
    }


    /**
     * Get the fittest individual of the population
     * @return fittest {@link Individual}
     */
    public Individual getFittestIndividual(){
        //sort the list
        this.population.sort(Comparator.comparing(Individual::getFitness));
        return this.population.get(this.population.size() - 1);
    }


    /**
     * Getter for the max fitness achievable by an agent
     * @return int value of fitness
     */
    public int getMaxFitnessAchievable() {
        return maxFitnessAchievable;
    }


    /**
     * Implementation of the method explained in:
     * Cartlidge, J., & Bullock, S. (2004). Combating coevolutionary disengagement by reducing parasite virulence.
     * Evolutionary Computation, 12(2), 193–222. http://doi.org/10.1162/106365604773955148
     *
     *
     * The scores are normalised with respect to the maximum score achieved that generation such that the best current parasite always achieves a score of 1
     *
     * It needs a parameter (virulence) from outside
     * Maximum virulence (1.0) normal situation
     * Moderate virulence (0.75) win rate three quarters that of the highest scoring current parasite
     * Null virulence (0.5) half the win rate
     * < 0.5 encourage cooperation between populations
     *
     * reducing virulance can be thought of as maintaining a gradient for selection, forcing paeasites to evolve in difficulty at a similar speed to hosts
     */
    public void reduceVirulence(){
        double virulence = 0.75;
        try {
            if(this.getClass() == Agents.class) {
                virulence = ReadConfig.Configurations.getVirulenceAgents();
            }else{
                virulence = ReadConfig.Configurations.getVirulenceClassifiers();
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, e.getMessage() + " -> set virulence to 0.75 by default");
        }

        double maxFitness = Collections.max(this.population, Comparator.comparing(Individual::getFitness)).getFitness();

        double finalVirulence = virulence;
        this.population.forEach(individual -> {
            double normalisedFitness = Normalisation.convertToSomething(maxFitness, 0.0, 1.0,0.0, individual.getFitness());
            double virulencedFitness = this.functionFitness(normalisedFitness, finalVirulence);
            individual.setFitness((int) Normalisation.convertToSomething(1.0, 0.0, maxFitness,0.0, virulencedFitness));
        });

    }

    /**
     * Compute the new fitness following the reducing virulence method
     * @param fitness old fitness
     * @param virulence virulence parameter
     * @return new fitness
     */
    private double functionFitness(double fitness, double virulence){
        return (((2 * fitness) / virulence) - (Math.pow(fitness, 2) / Math.pow(virulence, 2)));
    }


    /**
     * Method that implements all the system to combat disengagement.
     *
     * I have two different way to compute measures that help us to understand if the population are disengaged
     * -> mean and standard deviation of the population's fitness
     * -> engagement metric
     *
     * How can I stop the disengagement in an automatic way? I can evolve two different parameters that can help the population to stabilise
     * -> I can change the step size following one of the previous metrics
     * -> I can change the virulence parameter in an automatic way
     */
    public void combactDisengagement(){

    }


}

package tgcfs.EA;

import org.nd4j.linalg.api.ndarray.INDArray;
import tgcfs.Config.ReadConfig;
import tgcfs.EA.Mutation.RandomResetting;
import tgcfs.EA.Mutation.UncorrelatedMutation;
import tgcfs.EA.Recombination.DiscreteRecombination;
import tgcfs.EA.Recombination.IntermediateRecombination;
import tgcfs.EA.Recombination.Recombination;
import tgcfs.InputOutput.Transformation;
import tgcfs.Loader.TrainReal;
import tgcfs.NN.EvolvableModel;
import tgcfs.NN.InputsNetwork;
import tgcfs.NN.OutputsNetwork;

import java.util.ArrayList;
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
    protected static final Logger logger = Logger.getLogger(Algorithm.class.getName()); //logger for this class


    /**
     * Constructor zero parameter
     * Initialise the populaiton list
     * @throws Exception exception if there is an error in readig the config file
     */
    public Algorithm() throws Exception{
        this.population = new ArrayList<>();
    }

    /**
     * Generate the population for the EA
     * @param model the model of the population
     * @throws Exception exception
     */
    public void generatePopulation(EvolvableModel model) throws Exception {
        //check which class is calling this method
        Integer size = 0;
        if(this.getClass() == Agents.class){
            size = ReadConfig.Configurations.getAgentPopulationSize();
            logger.log(Level.INFO, "Generating Agents Population...");
        }else{
            size = ReadConfig.Configurations.getClassifierPopulationSize();
            logger.log(Level.INFO, "Generating Classifiers Population...");
        }
        IntStream.range(0, size).forEach(i ->{
            Individual newBorn;
            try {
                if(ReadConfig.Configurations.getMutation() == 0){
                    newBorn = new UncorrelatedMutation(model.getArrayLength());
                }else{
                    newBorn = new RandomResetting(model.getArrayLength());
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
        Integer size = 0;
        if(this.getClass() == Agents.class){
            size = ReadConfig.Configurations.getAgentOffspringSize();
        }else{
            size = ReadConfig.Configurations.getClassifierOffspringSize();
        }
        //create offspring_size offspring
        for(int i = 0; i < size; i ++) {
            //two individuals are chosen randomly, with replacement, from the parent population
            Integer firstParentsIndex = ThreadLocalRandom.current().nextInt(this.population.size());
            Integer secondParentsIndex = ThreadLocalRandom.current().nextInt(this.population.size());
            Individual firstParents = this.population.get(firstParentsIndex);
            Individual secondParents = this.population.get(secondParentsIndex);

            //Discrete and intermediary recombination are then used to generate the objective parameters and
            //themutation strengths of the recombined individual, respectively
            Recombination obj = new DiscreteRecombination(firstParents.getObjectiveParameters().dup(), secondParents.getObjectiveParameters().dup());

            Individual son;
            if(ReadConfig.Configurations.getMutation() == 0){
                Recombination mut = new IntermediateRecombination(((UncorrelatedMutation)firstParents).getMutationStrengths(), ((UncorrelatedMutation)secondParents).getMutationStrengths(), 0.5);
                son = new UncorrelatedMutation(obj.recombination(), mut.recombination());
            }else{
                son = new RandomResetting(obj.recombination());
            }
            //set model to the son
            son.setModel(firstParents.getModel().deepCopy());

            //mutate the individual
            if(ReadConfig.Configurations.getMutation() == 0){
                son.mutate(this.population.size());
            }else{
                son.mutate(son.getObjectiveParameters().columns());
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
        Integer size = 0;
        if(this.getClass() == Agents.class){
            size = ReadConfig.Configurations.getAgentOffspringSize();
        }else{
            size = ReadConfig.Configurations.getClassifierOffspringSize();
        }
        //create offspring_size offspring
        for(int i = 0; i < size; i ++) {
            Integer idParent = ThreadLocalRandom.current().nextInt(this.population.size());
            Individual parent = this.population.get(idParent);
            //son has the same genome of the father
            Individual son = new RandomResetting(parent.getObjectiveParameters().dup());
            //now the son is mutated 10 times (hardcoded value)
            IntStream.range(0, 10).forEach(it -> son.mutate(son.getObjectiveParameters().columns()));
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
    public void selectParents() throws Exception {
        //check which class is calling this method
        Integer size = 0;
        if(this.getClass() == Agents.class){
            size = ReadConfig.Configurations.getAgentPopulationSize();
        }else{
            size = ReadConfig.Configurations.getClassifierPopulationSize();
        }
        //lets do binary selection

//        List<Individual> newList = new ArrayList<>();
//
//        IntStream.range(0, size).forEach(i -> {
//            Integer firstNumber = ThreadLocalRandom.current().nextInt(0,this.population.size());
//            Integer secondNumber = ThreadLocalRandom.current().nextInt(0,this.population.size());
//
//            Individual one = this.population.get(firstNumber);
//            Individual two = this.population.get(secondNumber);
//            if(one.getFitness() > two.getFitness()){
//                newList.add(one.deepCopy());
//            }else{
//                if(one.getFitness() < two.getFitness()){
//                    newList.add(two.deepCopy());
//                }else{
//                    //rando select one of the two
//                    Double rand = ThreadLocalRandom.current().nextDouble(0,1);
//                    if(rand >= 0.5){
//                        newList.add(one.deepCopy());
//                    }else {
//                        newList.add(two.deepCopy());
//                    }
//                }
//            }
//        });
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
}

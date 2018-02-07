package tgcfs.EA;

import org.nd4j.linalg.api.ndarray.INDArray;
import tgcfs.Config.ReadConfig;
import tgcfs.EA.Helpers.HallOfFame;
import tgcfs.EA.Mutation.NonUniformMutation;
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
import tgcfs.Utils.IndividualStatus;
import tgcfs.Utils.RandomGenerator;

import java.util.*;
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
 * The turing learning requires two sub-algorithms (one for the models and one for the classifier) which
 * are identical, and do not interact between them except for the fitness calculation step.
 * This class implement the common features of the algorithm
 */
public abstract class Algorithm {
    private List<Individual> population; //representation of the population
    protected int maxFitnessAchievable;
    protected static Logger logger;
    protected HallOfFame hallOfFame;

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
        this.hallOfFame = null;
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
        for(int i = 0; i < size; i ++){
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
        }
        //initialising the hall of fame
        if(ReadConfig.Configurations.getHallOfFame()) this.hallOfFame = new HallOfFame(model, logger, this, ReadConfig.Configurations.getSeed() * 5);
    }

    /**
     * Generate the population for the EA
     * using the information loaded from file
     * @param model the model of the population
     * @param populationLoaded the popolation loaded from file
     * @throws Exception exception
     */
    public void generatePopulation(EvolvableModel model, List<INDArray> populationLoaded) throws Exception {
        //check which class is calling this method

        IndividualStatus status;
        if(this.getClass() == Agents.class){
            status = IndividualStatus.AGENT;
            logger.log(Level.INFO, "Generating Loaded Agents Population...");
        }else{
            status = IndividualStatus.CLASSIFIER;
            logger.log(Level.INFO, "Generating Loaded Classifiers Population...");
        }

        int size = 0;
        if(Objects.equals(ReadConfig.Configurations.getUncorrelatedMutationStep(), "1")){
            size = 1;
        }
        final int[] finalSize = {size};
        for(INDArray ind: populationLoaded){
            Individual newBorn;
            try {
                switch(ReadConfig.Configurations.getMutation()){
                    case 0:
                        if(finalSize[0] == 0) finalSize[0] = ind.columns();
                        newBorn = new UncorrelatedMutation(ind, finalSize[0], status);
                        break;
                    case 1:
                        newBorn = new RandomResetting(ind, status);
                        break;
                    case 2:
                        newBorn = new NonUniformMutation(ind, status);
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
        }
        //initialising the hall of fame
        if(ReadConfig.Configurations.getHallOfFame()) this.hallOfFame = new HallOfFame(model, logger, this, ReadConfig.Configurations.getSeed() * 5);
    }

    /**
     * Getter for the population
     * @return list of {@link Individual}
     */
    public List<Individual> getPopulation() {
        return this.population;
    }

    /**
     * Getter for the population with the hall of fame sample included if requested
     * @return list of {@link Individual}
     */
    public List<Individual> getPopulationWithHallOfFame(){
        List<Individual> allTheIndividuals = new ArrayList<>(this.population);
        if(this.hallOfFame != null){
            allTheIndividuals.addAll(this.hallOfFame.getSample());
        }
        return allTheIndividuals;
    }

    /**
     * Add one individual to the collection
     * @param individual {@link Individual} to be added
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
            int firstParentsIndex = RandomGenerator.getNextInt(0,this.population.size());
            int secondParentsIndex = RandomGenerator.getNextInt(0,this.population.size());
            Individual firstParents = this.population.get(firstParentsIndex);
            Individual secondParents = this.population.get(secondParentsIndex);

            //Discrete and intermediary recombination are then used to generate the objective parameters and
            //themutation strengths of the recombined individual, respectively
            Recombination obj = new DiscreteRecombination(firstParents.getObjectiveParameters().dup(), secondParents.getObjectiveParameters().dup());

            Individual son;
            switch(ReadConfig.Configurations.getMutation()){
                case 0:
                    Recombination mut = new IntermediateRecombination(((UncorrelatedMutation)firstParents).getMutationStrengths(), ((UncorrelatedMutation)secondParents).getMutationStrengths(), 0.5);
                    son = new UncorrelatedMutation(obj.recombination(), mut.recombination(), status, true);
                    break;
                case 1:
                    son = new RandomResetting(obj.recombination(), status, true);
                    break;
                case 2:
                    son = new NonUniformMutation(obj.recombination(), status, true);
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
     * @param generation generation we are now
     * @param hallOfFame is it the turn for the hall of fame
     * @throws Exception if the parents have not the same length
     */
    public void generateOffspringOnlyWithMutation(int generation, boolean hallOfFame) throws Exception {
        //check which class is calling this method
        int size = 0;
        int tournamentSize = 0;
        IndividualStatus status;
        if(this.getClass() == Agents.class){
            size = ReadConfig.Configurations.getAgentOffspringSize();
            tournamentSize =  ReadConfig.Configurations.getTournamentSizeAgents();
            status = IndividualStatus.AGENT;
        }else{
            size = ReadConfig.Configurations.getClassifierOffspringSize();
            tournamentSize =  ReadConfig.Configurations.getTournamentSizeClassifiers();
            status = IndividualStatus.CLASSIFIER;
        }

        //set everyone as a parent now
        for(Individual ind: this.population){
            ind.isParent();
        }
        if(this.hallOfFame != null) {
            for(Individual ind: this.hallOfFame.getHallOfFame()){
                ind.isParent();
            }
        }

        //create offspring_size offspring
        for(int i = 0; i < size; i ++) {

            //creating the tournament
            List<Individual> tournamentPop = new ArrayList<>();
            for(int j = 0; j < tournamentSize; j++){
                boolean isHallOfFame = false;
                if(hallOfFame && this.hallOfFame != null) isHallOfFame = true;
                if(!isHallOfFame) {
                    //selection from the normal population
                    int idParent = RandomGenerator.getNextInt(0, this.population.size());
                    logger.log(Level.FINE, "idParent for tournament selection: " + idParent);
                    Individual ind = this.population.get(idParent);
                    logger.log(Level.FINE, idParent + ": " + ind.getObjectiveParameters().toString());
                    tournamentPop.add(ind);
                }else{
                    //selection from the hall of fame
                    Individual ind = this.hallOfFame.getRandomIndividualFromSample();
                    tournamentPop.add(ind);
                }
            }
            //find the winner of the tournament -> the one with the highest fitness
            tournamentPop.sort(Comparator.comparingDouble(Individual::getFitness));


            //log the fitness of all the tournament
            List<Double> fitn = new ArrayList<>();
            for(Individual ind: tournamentPop){
                fitn.add(ind.getFitness());
            }

            logger.log(Level.FINE, "--Fitness population on the tournament--");
            logger.log(Level.FINE, fitn.toString());


            //last one has the better fitness
            Individual parent = tournamentPop.get(tournamentPop.size() - 1);
            logger.log(Level.FINE, "Parent selected to mutate: \n" + parent.getObjectiveParameters());

            //son has the same genome of the father
            Individual son;
            int mutationType = ReadConfig.Configurations.getMutation();
            switch(mutationType){
                case 0:
                    son = new UncorrelatedMutation(parent.getObjectiveParameters().dup(), ((UncorrelatedMutation)parent).getMutationStrengths().dup(), status, true);
                    break;
                case 1:
                    son = new RandomResetting(parent.getObjectiveParameters().dup(), status, true);
                    break;
                case 2:
                    son = new NonUniformMutation(parent.getObjectiveParameters().dup(), status, true);
                    break;
                default:
                    throw new Exception("Mutation argument not correct");
            }
            //now the son is mutated 10 times (hardcoded value)
            //IntStream.range(0, 10).forEach(it -> son.mutate(son.getObjectiveParameters().columns()));
            if(mutationType == 2){
                son.mutate(generation);

            }else {
                son.mutate(son.getObjectiveParameters().columns());
            }
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
     * Select parents for the next generation
     * @throws Exception if there are problems in reading the info
     */
    public void survivalSelections() throws Exception {
        int value = 0;
        if (this.getClass() == Agents.class) {
            value = ReadConfig.Configurations.getDifferentSelectionForAgent();
        } else {
            value = ReadConfig.Configurations.getDifferentSelectionForClassifiers();
        }
        //0 means mu plus lambda, 1 means mu comma lambda, 2 means keep the best and throw away the others. combine them for agent and classifier.
        switch (value){
            case 0:
                this.muPlusLambda();
                break;
            case 1:
                this.muCommaLambda();
                break;
            case 2:
                this.keepBestN();
                break;
            default:
                throw new Exception("Survival selection selected not yet implemented.");
        }

        //save the best individual of the population for the hall of fame settings
        if(this.hallOfFame != null) this.hallOfFame.addIndividual(this.population.get(this.population.size() - 1));
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
        this.population.sort(Comparator.comparing(Individual::getFitness));
        return this.population.get(0).getObjectiveParameters();
    }

    /**
     * Methods that returns the step size of all the population, ordered by fitness
     * @return double/ list of double
     */
    public List<INDArray> retStepSizeBestGenome(){
        this.population.sort(Comparator.comparing(Individual::getFitness));
        List<INDArray> allTheStepSizes = new ArrayList<>();
        for(Individual ind: this.population){
            allTheStepSizes.add(((UncorrelatedMutation)ind).getMutationStrengths());
        }
        return allTheStepSizes;
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
        for(Individual ind: this.population){
            ind.resetFitness();
        }
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
     * Setter for the population
     * @param population new population
     */
    public void setPopulation(List<Individual> population){
        this.population = population;
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
    private void muPlusLambda() throws Exception {
        //check which class is calling this method
        int size = 0;
        if (this.getClass() == Agents.class) {
            size = ReadConfig.Configurations.getAgentPopulationSize();
        } else {
            size = ReadConfig.Configurations.getClassifierPopulationSize();
        }

        //sort the list
        this.population.sort(Comparator.comparing(Individual::getFitness));


        //log the fitness of all the population
        List<Double> fitn = new ArrayList<>();
        for(Individual ind : this.population){
            fitn.add(ind.getFitness());
        }

        logger.log(Level.INFO, "--Fitness population [" + this.getClass().getName() + "] before selection--\n" + fitn.toString());


        while (this.population.size() != size) {
            this.population.remove(0);
        }

        List<Individual> newList = new ArrayList<>();
        for(Individual ind : this.population){
            newList.add(ind.deepCopy());
        }

        List<Double> fitnd = new ArrayList<>();
        for(Individual ind : newList){
            fitnd.add(ind.getFitness());
        }

        logger.log(Level.INFO, "--Fitness population [" + this.getClass().getName() + "] after selection--\n" + fitnd.toString());

        this.population = new ArrayList<>();
        this.population = newList;
        //now the population is again under the maximum size allowed and containing only the element with highest fitness.

        //check who is parents and who is son
        List<Integer> sonAndParent = new ArrayList<>();
        for(Individual ind : this.population){
            if (ind.isSon()) {
                // zero for offspring
                sonAndParent.add(0);
            } else {
                // one for parent
                sonAndParent.add(1);
            }
        }
        logger.log(Level.INFO, "--[" + this.getClass().getName() + "] Parents[1] vs Sons[0]--\n" + sonAndParent.toString());
    }

    /**
     * the (µ, λ)-ES, in which the selection takes place among the λ offspring
     * only, whereas their parents are “forgotten” no matter how good or bad
     * their fitness was compared to that of the new generation. Obviously, this
     * strategy relies on a birth surplus, i.e., on λ>µ in a strict Darwinian
     * sense of natural selection.
     *
     * @throws Exception if there are problems in reading the info
     */
    private void muCommaLambda() throws Exception {
        //check which class is calling this method
        int size = 0;
        if (this.getClass() == Agents.class) {
            size = ReadConfig.Configurations.getAgentPopulationSize();
        } else {
            size = ReadConfig.Configurations.getClassifierPopulationSize();
        }

        //remove all the parents
        this.population.removeIf(individual -> !individual.isSon());

        //sort the list
        this.population.sort(Comparator.comparing(Individual::getFitness));

        //log the fitness of all the population
        List<Double> fitn = new ArrayList<>();
        for(Individual ind : this.population){
            fitn.add(ind.getFitness());
        }

        logger.log(Level.INFO, "--Fitness population [" + this.getClass().getName() + "] before selection--\n" + fitn.toString());


        while (this.population.size() != size) {
            this.population.remove(0);
        }

        List<Individual> newList = new ArrayList<>();
        for(Individual ind : this.population){
            newList.add(ind.deepCopy());
        }

        List<Double> fitnd = new ArrayList<>();
        for(Individual ind : newList){
            fitnd.add(ind.getFitness());
        }

        logger.log(Level.INFO, "--Fitness population [" + this.getClass().getName() + "] after selection--\n" + fitnd.toString());

        this.population = new ArrayList<>();
        this.population = newList;
        //now the population is again under the maximum size allowed and containing only the element with highest fitness.
    }


    /**
     * Keep the best N individual for the next generation
     * @throws Exception if there are problems in reading the info
     */
    private void keepBestN() throws Exception {
        //log the fitness of all the population
        List<Double> fitn = new ArrayList<>();
        for(Individual ind : this.population){
            fitn.add(ind.getFitness());
        }

        logger.log(Level.INFO, "--Fitness population [" + this.getClass().getName() + "] before selection--\n" + fitn.toString());

        List<Individual> nextGeneration = new ArrayList<>();

        // lets find out who are all the sons
        List<Individual> sons = this.population.stream().filter(Individual::isSon).collect(Collectors.toList());
        List<Individual> parents = this.population.stream().filter(individual -> !individual.isSon()).collect(Collectors.toList());

        // keep the best parents -> order the parents and keep the one with highest fitness
        parents.sort(Comparator.comparing(Individual::getFitness));

        int howManyIwillKeep = ReadConfig.Configurations.getKeepBestNElement();
        for (int i = 1; i < howManyIwillKeep + 1 ; i ++) {
            nextGeneration.add(parents.get(parents.size() - i).deepCopy());
        }

        int size = 0;
        if (this.getClass() == Agents.class) {
            size = ReadConfig.Configurations.getAgentPopulationSize();
        } else {
            size = ReadConfig.Configurations.getClassifierPopulationSize();
        }

        for(Individual ind: sons){
            nextGeneration.add(ind.deepCopy());
        }

        while(nextGeneration.size() > size){
            nextGeneration.remove(nextGeneration.size() - 1);
        }
        nextGeneration.sort(Comparator.comparing(Individual::getFitness));


        List<Double> fitnd = new ArrayList<>();
        for(Individual ind : nextGeneration){
            fitnd.add(ind.getFitness());
        }

        logger.log(Level.INFO, "--Fitness population [" + this.getClass().getName() + "] after selection--\n" + fitnd.toString());

        this.setPopulation(nextGeneration);

        //check who is parents and who is son
        List<Integer> sonAndParent = new ArrayList<>();
        for(Individual ind : this.population){
            if (ind.isSon()) {
                // zero for offspring
                sonAndParent.add(0);
            } else {
                // one for parent
                sonAndParent.add(1);
            }
        }
        logger.log(Level.INFO, "--[" + this.getClass().getName() + "] Parents[1] vs Sons[0]--\n" + sonAndParent.toString());

    }

    /**
     * If I am using the hall of fame system, create the new sample
     */
    public void requestSampleHoF(){
        if(this.hallOfFame != null) this.hallOfFame.createSample();
    }

}

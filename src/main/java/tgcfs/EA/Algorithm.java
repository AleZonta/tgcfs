package tgcfs.EA;

import tgcfs.Agents.OutputNetwork;
import tgcfs.Config.ReadConfig;
import tgcfs.EA.Recombination.DiscreteRecombination;
import tgcfs.EA.Recombination.IntermediateRecombination;
import tgcfs.EA.Recombination.Recombination;
import tgcfs.NN.EvolvableNN;
import tgcfs.NN.InputsNetwork;
import tgcfs.NN.OutputsNetwork;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
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
 * The turing learning requires two sub-algorithms (one for the models and one for the classifier) which
 * are identical, and do not interact between them except for the fitness calculation step.
 * This class implement the common features of the algorithm
 */
public abstract class Algorithm {
    private List<Individual> population; //representation of the population
    private ReadConfig configFile; //file containing configuration
    protected static final Logger logger = Logger.getLogger(Algorithm.class.getName()); //logger for this class


    /**
     * Constructor zero parameter
     * Initialise the populaiton list
     * @throws Exception exception if there is an error in readig the config file
     */
    public Algorithm() throws Exception{
        this.population = new ArrayList<>();
        //Loading config for this algorithm
        this.configFile = new ReadConfig();
        this.configFile.readFile();
    }

    /**
     * Generate the population for the EA
     * @throws Exception exception
     */
    public abstract void generatePopulation() throws Exception;

    /**
     * Getter for the config file
     * @return reference to config file
     */
    public ReadConfig getConfigFile() {
        return this.configFile;
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
            size = this.configFile.getAgentOffspringSize();
        }else{
            size = this.configFile.getClassifierOffspringSize();
        }
        //create offspring_size offspring
        for(int i = 0; i < size; i ++) {
            //two individuals are chosen randomly, with replacement, from the parent population
            Integer firstParentsIndex = new Random().nextInt(this.population.size());
            Integer secondParentsIndex = new Random().nextInt(this.population.size());
            Individual firstParents = this.population.get(firstParentsIndex);
            Individual secondParents = this.population.get(secondParentsIndex);

            //Discrete and intermediary recombination are then used to generate the objective parameters and
            //themutation strengths of the recombined individual, respectively
            Recombination obj = new DiscreteRecombination(firstParents.getObjectiveParameters(), secondParents.getObjectiveParameters());
            Recombination mut = new IntermediateRecombination(firstParents.getMutationStrengths(), secondParents.getMutationStrengths(), 0.5);
            Individual son = new Individual(obj.recombination(), mut.recombination());

            //mutate the individual
            son.mutate(this.population.size());
            //add the son to the population
            this.population.add(son);
        }
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
            size = this.configFile.getAgentPopulationSize();
        }else{
            size = this.configFile.getClassifierPopulationSize();
        }
        //sort the list
        this.population.sort(Comparator.comparing(Individual::getFitness));
        while(this.population.size() == size){
            this.population.remove(size + 1);
        }
        //now the population is again under the maximum size allowed and containing only the element with highest fitness.
    }

    /**
     * Now we run the individual in order to collect the result.
     * Running an agent means set the weight to the neural network and obtain the results
     * @param model is the model used for the agent
     * @param input the input of the model
     * @throws Exception if there are problems in reading the info
     */
    public void runIndividual(EvolvableNN model,  List<InputsNetwork> input) throws Exception {
        logger.log(Level.INFO, "Running Agents...");

        Integer  size = 0;
        if(this.getClass() == Agents.class){
            size = this.configFile.getAgentTimeSteps();
        }else{
            size = this.configFile.getClassifierTimeSteps();
        }
        final Integer realSize = size;

        this.population.forEach(individual -> {
            try {
                //set the weights
                model.setWeights(individual.getObjectiveParameters());

                //compute Output of the network
                List<Double> lastOutput = null;
                for (InputsNetwork inputsNetwork : input) {
                    lastOutput = model.computeOutput(inputsNetwork.serialise());
                }
                //now for the number of timestep that I want to check save the output
                List<OutputsNetwork> outputsNetworks = new ArrayList<>();

                OutputNetwork out = new OutputNetwork();
                out.deserialise(lastOutput);
                outputsNetworks.add(out);

                for(int i = 0; i < realSize; i++){ //TODO read number timestep from config file
                    lastOutput = model.computeOutput(lastOutput);

                    out = new OutputNetwork();
                    out.deserialise(lastOutput);
                    outputsNetworks.add(out);
                }
                //assign the output to this individual
                individual.setOutput(outputsNetworks);

            } catch (Exception e) {
                logger.log(Level.SEVERE, "Errors with the neural network" + e.getMessage());
            }

        });
    }


}

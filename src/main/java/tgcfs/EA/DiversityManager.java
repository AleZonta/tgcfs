package tgcfs.EA;

import tgcfs.Config.ReadConfig;
import tgcfs.EA.Mutation.NonUniformMutation;
import tgcfs.EA.Mutation.RandomResetting;
import tgcfs.EA.Mutation.UncorrelatedMutation;
import tgcfs.NN.EvolvableModel;
import tgcfs.Utils.IndividualStatus;
import tgcfs.Utils.RandomGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Alessandro Zonta on 10/01/2018.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 *
 * class that checks the diversity of the population
 */
public class DiversityManager {
    private static Logger logger;

    /**
     * Constructor zero parameter
     * @param log log
     */
    public DiversityManager(Logger log) {
        logger = log;
    }


    /**
     * Infusion technique
     *
     * New individuals are randomly inserted after a certain number of generations
     * Reinitialization has been proven successful in dynamic and changing environments, where the re-seeding of
     * good individuals from past cases has been proposed every 150 generations
     *
     * How many individuals are we going to replace?
     * Number of individual is tuned by a value read from config file
     * @param population population to modify
     */
    public void insertRandomIndividual(List<Individual> population) throws Exception {
        //make it from the config file
        int numberOfIndividualToReplace = 20;

        List<Individual> newIndividuals = new ArrayList<>();
        // model used
        EvolvableModel model = population.get(0).getModel();
        IndividualStatus status = population.get(0).getIndividualStatus();

        int mutationType = ReadConfig.Configurations.getMutation();
        //creation of the new members
        for(int i = 0; i < numberOfIndividualToReplace; i++){
            Individual newBorn;
            switch(mutationType){
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
                    newBorn = new NonUniformMutation(model.getArrayLength(), status);
            }
            //assign the model to the classifier
            newBorn.setModel(model.deepCopy());
            newIndividuals.add(newBorn);
        }

        //creating numberOfIndividualToReplace different position
        List<Integer> positions = new ArrayList<>();
        while (positions.size() < numberOfIndividualToReplace){
            //random position
            int pos = RandomGenerator.getNextInt(0, population.size());
            if (positions.stream().noneMatch(integer -> integer.equals(pos))){
                positions.add(pos);
            }
        }

        //now insert new individuals in the list
        positions.forEach(pos -> population.add(pos, newIndividuals.remove(0)));
    }


}

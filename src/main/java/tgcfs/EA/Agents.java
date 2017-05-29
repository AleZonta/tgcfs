package tgcfs.EA;

import java.util.logging.Level;

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

    /**
     * Constructor zero parameter
     * Call the super constructor
     * @throws Exception if the super constructor has problem in reading the config files
     */
    public Agents() throws Exception {
        super();
    }

    /**
     * @implNote Implementation from Abstract class Algorithm
     */
    @Override
    public void generatePopulation() throws Exception {
        logger.log(Level.INFO, "Generating Agents Population...");
        for(int i = 0; i < super.getConfigFile().getAgentPopulationSize(); i++ ){
            Individual newBorn = new Individual();
            super.addIndividual(newBorn);
        }
    }

}

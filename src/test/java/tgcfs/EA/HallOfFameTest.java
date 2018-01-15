package tgcfs.EA;

import org.junit.Test;
import tgcfs.Agents.Models.LSTMAgent;
import tgcfs.Config.ReadConfig;
import tgcfs.EA.Mutation.NonUniformMutation;
import tgcfs.Utils.IndividualStatus;
import tgcfs.Utils.RandomGenerator;

import static org.junit.Assert.assertNotNull;

/**
 * Created by Alessandro Zonta on 15/01/2018.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 */
public class HallOfFameTest {

    @Test
    public void addIndividual() throws Exception {
        new ReadConfig.Configurations();
        new RandomGenerator();
        HallOfFame hallOfFame = new HallOfFame(new LSTMAgent(1,1,1,1));
        for(int i = 0; i < 5; i++) {
            hallOfFame.addIndividual(new NonUniformMutation(5));
        }
    }

    @Test
    public void getSample() throws Exception {
        new ReadConfig.Configurations();
        new RandomGenerator();
        HallOfFame hallOfFame = new HallOfFame(new LSTMAgent(1,1,1,1));
        for(int i = 0; i < 5; i++) {
            Individual individual = new NonUniformMutation(5, IndividualStatus.AGENT);
            hallOfFame.addIndividual(individual);
        }
        hallOfFame.createSample();
        assertNotNull(hallOfFame.getSample());
        for(int i = 0; i < 7; i++) {
            Individual individual = new NonUniformMutation(5, IndividualStatus.AGENT);
            hallOfFame.addIndividual(individual);
        }
        hallOfFame.createSample();
        assertNotNull(hallOfFame.getSample());
        for(int i = 0; i < 14; i++) {
            Individual individual = new NonUniformMutation(5, IndividualStatus.AGENT);
            hallOfFame.addIndividual(individual);
        }
        hallOfFame.createSample();
        assertNotNull(hallOfFame.getSample());


    }

    @Test
    public void getHallOfFame() throws Exception {
        HallOfFame hallOfFame = new HallOfFame(new LSTMAgent(1,1,1,1));
        for(int i = 0; i < 5; i++) {
            hallOfFame.addIndividual(new NonUniformMutation(5));
        }
        assertNotNull(hallOfFame.getHallOfFame());

    }
}
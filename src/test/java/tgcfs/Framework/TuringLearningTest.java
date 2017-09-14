package tgcfs.Framework;

import org.junit.Test;
import tgcfs.EA.Agents;
import tgcfs.EA.Individual;

import java.util.List;

import static junit.framework.TestCase.assertTrue;

/**
 * Created by Alessandro Zonta on 14/09/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 */
public class TuringLearningTest {
    @Test
    public void load() throws Exception {
        TuringLearning app = new TuringLearning();
        app.load();
    }

    @Test
    public void run() throws Exception {
        TuringLearning app = new TuringLearning();
        app.load();
        app.run();
    }

    @Test
    public void generateMoreThanDiscriminate() throws Exception {
        TuringLearning app = new TuringLearning();
        app.load();

        Agents a = app.getAgents();
        List<Individual> popA = a.getPopulation();


        app.generateMoreThanDiscriminate(10);

        Agents b = app.getAgents();
        List<Individual> popB = b.getPopulation();

        for(int i=0; i < popA.size(); i++){
            for(int j=0; j < popB.size(); j++){
                int u = 0;
                for(int h=0; h<popA.get(i).getObjectiveParameters().columns(); h++){
                    if(popA.get(i).getObjectiveParameters().getScalar(h).equals(popB.get(j).getObjectiveParameters().getScalar(h))){
                        u++;
                    }
                }
                assertTrue(u<popA.get(i).getObjectiveParameters().columns());
            }
        }
    }

    @Test
    public void getAgents() throws Exception {
    }

}
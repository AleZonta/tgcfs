package tgcfs.EA;

import org.junit.Test;
import tgcfs.Classifiers.Classifier;
import tgcfs.Classifiers.InputNetwork;
import tgcfs.Classifiers.OutputNetwork;
import tgcfs.NN.EvolvableNN;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;

/**
 * Created by Alessandro Zonta on 30/05/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 */
public class ClassifiersTest {
    @Test
    public void generatePopulation() throws Exception {
        //random classifier
        EvolvableNN model = new Classifier(InputNetwork.inputSize,2, OutputNetwork.outputSize);
        Classifiers classifiers = new Classifiers();
        classifiers.generatePopulation(model);
        assertNotNull(classifiers.getPopulation());
    }

    @Test
    public void runIndividuals() throws Exception {
        Classifiers classifiers = new Classifiers();
        try {
            classifiers.runIndividuals(null);
        }catch (Exception e){
            assertEquals("Method not usable for a Classifier", e.getMessage());
        }
    }

    @Test
    public void runIndividual() throws Exception {
    }

    @Test
    public void evaluateIndividuals() throws Exception {
    }

    @Test
    public void evaluateRealAgent() throws Exception {
    }

}
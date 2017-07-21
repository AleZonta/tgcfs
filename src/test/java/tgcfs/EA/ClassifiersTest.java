package tgcfs.EA;

import org.junit.Test;
import tgcfs.Classifiers.Classifier;
import tgcfs.Classifiers.InputNetwork;
import tgcfs.Classifiers.OutputNetwork;
import tgcfs.Config.ReadConfig;
import tgcfs.NN.EvolvableNN;
import tgcfs.NN.InputsNetwork;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

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
    public void trainNetwork() throws Exception {
        Classifiers classifiers = new Classifiers();
        try {
            classifiers.trainNetwork(null);
        }catch (Error e){
            assertEquals("Method not usable for a Classifier", e.getMessage());
        }
    }

    @Test
    public void generatePopulation() throws Exception {
        new ReadConfig.Configurations();
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
        new ReadConfig.Configurations();
        EvolvableNN model = new Classifier(InputNetwork.inputSize,2, OutputNetwork.outputSize);
        Classifiers classifiers = new Classifiers();
        classifiers.generatePopulation(model);

        List<InputsNetwork> input = new ArrayList<>();
        input.add(new InputNetwork(15.0, 30.8));
        input.add(new InputNetwork(16.0, 31.8));
        input.add(new InputNetwork(17.0, 32.8));
        input.add(new InputNetwork(18.0, 33.8));
        input.add(new InputNetwork(19.0, 34.8));

        OutputNetwork res = (OutputNetwork) classifiers.runIndividual(classifiers.getPopulation().get(0),input);
        assertNotNull(res);


        IntStream.range(0,5000).forEach(i -> {
            List<InputsNetwork> inputHere = new ArrayList<>();
            inputHere.add(new InputNetwork(ThreadLocalRandom.current().nextDouble(-5,55), ThreadLocalRandom.current().nextDouble(-180,180)));
            inputHere.add(new InputNetwork(ThreadLocalRandom.current().nextDouble(-5,55), ThreadLocalRandom.current().nextDouble(-180,180)));
            inputHere.add(new InputNetwork(ThreadLocalRandom.current().nextDouble(-5,55), ThreadLocalRandom.current().nextDouble(-180,180)));
            inputHere.add(new InputNetwork(ThreadLocalRandom.current().nextDouble(-5,55), ThreadLocalRandom.current().nextDouble(-180,180)));
            inputHere.add(new InputNetwork(ThreadLocalRandom.current().nextDouble(-5,55), ThreadLocalRandom.current().nextDouble(-180,180)));
            try {
                OutputNetwork ress = (OutputNetwork) classifiers.runIndividual(classifiers.getPopulation().get(0),inputHere);
                System.out.println(ress.getReal().toString() );
            } catch (Exception e) {
                e.printStackTrace();
            }

        });



    }

    @Test
    public void evaluateIndividuals() throws Exception {
        Classifiers classifiers = new Classifiers();
        try {
            classifiers.evaluateIndividuals(null,null);
        }catch (Error e){
            assertEquals("Method not usable for a Classifier", e.getMessage());
        }
    }

    @Test
    public void evaluateRealAgent() throws Exception {
    }

}
package tgcfs.EA.Mutation;

import org.nd4j.linalg.api.ndarray.INDArray;
import tgcfs.Config.ReadConfig;
import tgcfs.EA.Individual;
import tgcfs.Loader.TrainReal;
import tgcfs.NN.EvolvableModel;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Alessandro Zonta on 13/10/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 */
public class NonUniformMutation extends Individual {

    /**
     * Two parameter constructor and set to 0 the fitness
     * @param objPar objectiveParameters list
     */
    public NonUniformMutation(INDArray objPar){
        super(objPar);
    }

    /**
     * One parameter constructor
     * It is loading the objective parameters list with random number
     * and the mutation strengths list with 1.0
     * @param size size of the objectiveParameter
     * @exception Exception if there are problems with the reading of the seed information
     */
    public NonUniformMutation(int size) throws Exception {
        super(size);
    }

    /**
     * four parameters constructor
     * @param objectiveParameters objectiveParameters list
     * @param atomicInteger fitness
     * @param evolvableModel model to assign to the individual
     * @param myInputandOutput input output last
     */
    public NonUniformMutation(INDArray objectiveParameters, AtomicInteger atomicInteger, EvolvableModel evolvableModel, List<TrainReal> myInputandOutput) {
        super(objectiveParameters, atomicInteger, evolvableModel, myInputandOutput);
    }

    /**
     * Implementation abstract method mutate from individual
     *
     * normal practice to apply this operator with probability one per gene
     *
     * It needs a mutation step size parameter
     *
     * @param n is the genome length
     */
    @Override
    public void mutate(int n) {
        double stepSize = 0d;
        Random rnd = new Random();
        try {
            stepSize = ReadConfig.Configurations.getStepSize();
        } catch (Exception ignored) { }
        for(int i = 0; i < super.getObjectiveParameters().columns(); i++){
            double newValue = super.getObjectiveParameters().getDouble(i) + stepSize * rnd.nextGaussian();
            //elastic bound
            if(newValue > 4d){
                double difference = newValue - 4d;
                newValue = 4d - difference;
            }
            if(newValue < -4){
                double difference = newValue - (-4d);
                newValue = -4 - difference;
            }
            super.getObjectiveParameters().putScalar(i, newValue);
        }
    }

    /**
     * Deep copy function
     * @return NonUniformMutation object
     */
    @Override
    public Individual deepCopy() {
        return new NonUniformMutation(this.getObjectiveParameters(), new AtomicInteger(this.getFitness()), this.getModel().deepCopy(), this.getMyInputandOutput());

    }
}

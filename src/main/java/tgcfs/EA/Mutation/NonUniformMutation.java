package tgcfs.EA.Mutation;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.primitives.AtomicDouble;
import tgcfs.EA.Individual;
import tgcfs.Loader.TrainReal;
import tgcfs.NN.EvolvableModel;
import tgcfs.Utils.IndividualStatus;

import java.util.List;
import java.util.Random;

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
     * @param ind kind of individual I am creating
     */
    public NonUniformMutation(INDArray objPar, IndividualStatus ind){
        super(objPar, ind);
    }

    /**
     * Three parameter constructor
     * @param objPar objectiveParameters list
     * @param ind kind of individual I am creating
     * @param isSon boolean variable if the individual is a son
     * @exception Exception if there are problems with the reading of the seed information
     */
    public NonUniformMutation(INDArray objPar, IndividualStatus ind, boolean isSon) throws Exception {
        super(objPar, ind, isSon);
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
     * Two parameter constructor
     * It is loading the objective parameters list with random number
     * and the mutation strengths list with 1.0
     * @param size size of the objectiveParameter
     * @param ind kind of individual I am creating
     * @exception Exception if there are problems with the reading of the seed information
     */
    public NonUniformMutation(int size, IndividualStatus ind) throws Exception {
        super(size, ind);
    }

    /**
     * four parameters constructor
     * @param objectiveParameters objectiveParameters list
     * @param atomicInteger fitness
     * @param evolvableModel model to assign to the individual
     * @param myInputandOutput input output last
     * @param ind kind of individual I am creating
     * @param isSon boolean variable if the individual is a son
     */
    public NonUniformMutation(INDArray objectiveParameters, AtomicDouble atomicInteger, EvolvableModel evolvableModel, List<TrainReal> myInputandOutput, IndividualStatus ind, boolean isSon) {
        super(objectiveParameters, atomicInteger, evolvableModel, myInputandOutput, ind, isSon);
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
            if(this.ind == IndividualStatus.AGENT) {
                stepSize = StepSize.getStepSizeAgents();
            }else {
                stepSize = StepSize.getStepSizeClassifiers();
            }
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
        return new NonUniformMutation(this.getObjectiveParameters(), new AtomicDouble(this.getFitness()), this.getModel().deepCopy(), this.getMyInputandOutput(), this.ind, this.isSon());

    }
}

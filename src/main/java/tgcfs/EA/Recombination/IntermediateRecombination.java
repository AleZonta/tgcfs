package tgcfs.EA.Recombination;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

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
 * Class implementing the intermediate recombination between two individuals
 */
public class IntermediateRecombination extends AbstractRecombination implements Recombination {
    private Double alpha; //parameter for the recombination

    /**
     * Constructor with two parameters
     * @param mother individual mother
     * @param father individual father
     * @param alpha parameter needed for the recombination. The parameter α can be:
     *              constant: uniform arithmetical crossover
     *              variable (e.g. depend on the age of the population)
     *              picked at random every time
     * @throws Exception raise an exception if the two individual has different length or the alpha value is not correct
     */
    public IntermediateRecombination(INDArray mother, INDArray father, Double alpha) throws Exception {
        super(mother,father);
        this.alpha = alpha;
        if(this.alpha < 0 || this.alpha > 1){
            throw new Exception("Alpha value is not correct");
        }
    }


    /**
     * @implNote Implementation from interface
     * Implements the intermediate recombination between two parents
     * zi =αxi +(1-α)yi whereα:0≤α ≤1
     * @return new individual variables
     */
    public INDArray recombination(){
        INDArray son = Nd4j.zeros(super.getSize());

        for (int i = 0; i < super.getSize(); i++){
            son.add(this.alpha * super.getMother().getDouble(i) + (1 - this.alpha) * super.getFather().getDouble(i));
        }

        return son;
    }
}

package tgcfs.EA.Recombination;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.util.Random;
import java.util.stream.DoubleStream;

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
 * Class implementing the discrete recombination between two individuals
 */
public class DiscreteRecombination extends AbstractRecombination implements Recombination {
    /**
     * Constructor with two parameters
     * @param mother individual mother
     * @param father individual father
     * @throws Exception raise an exception if the two individual has different length
     */
    public DiscreteRecombination(INDArray mother, INDArray father) throws Exception {
        super(mother,father);
    }

    /**
     * @implNote Implementation from interface
     * Implements the discrete recombination between two parents
     * For each position the parent who contributes its variable to the offspring is chosen randomly with equal probability.
     * @return new individual variables
     */
    public INDArray recombination(){
        INDArray son = Nd4j.zeros(super.getSize());

        final int[] index = {0};
        final int[] pos = {0};
        DoubleStream randomNumber = new Random().doubles(super.getSize(),0,1);
        randomNumber.forEach(number -> {
            if(number >= 0.5){
                son.putScalar(pos[0], super.getMother().getDouble(index[0]));
            }else{
                son.putScalar(pos[0], super.getFather().getDouble(index[0]));
            }
            pos[0]++;
            index[0]++;
        });
        return son;
    }

}

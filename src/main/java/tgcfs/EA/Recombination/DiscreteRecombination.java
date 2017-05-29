package tgcfs.EA.Recombination;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

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
    public DiscreteRecombination(List<Double> mother, List<Double> father) throws Exception {
        super(mother,father);
    }

    /**
     * @implNote Implementation from interface
     * Implements the discrete recombination between two parents
     * For each position the parent who contributes its variable to the offspring is chosen randomly with equal probability.
     * @return new individual variables
     */
    public List<Double> recombination(){
        List<Double> son = new ArrayList<>();

        final int[] index = {0};
        IntStream randomNumber = new Random().ints(super.getSize(),0,1);
        randomNumber.forEach(number -> {
            if(number >= 0.5){
                son.add(super.getMother().get(index[0]));
            }else{
                son.add(super.getFather().get(index[0]));
            }
            index[0]++;
        });
        return son;
    }

}

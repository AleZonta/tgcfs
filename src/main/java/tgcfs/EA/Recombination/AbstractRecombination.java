package tgcfs.EA.Recombination;

import org.nd4j.linalg.api.ndarray.INDArray;

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
 * abstract class recombination
 */
public class AbstractRecombination{
    private INDArray mother;
    private INDArray father;
    private Integer size;

    /**
     * Constructor with two parameters
     * @param mother individual mother
     * @param father individual father
     * @throws Exception raise an exception if the two individual has different length
     */
    public AbstractRecombination(INDArray mother, INDArray father) throws Exception {
        this.father = father;
        this.mother = mother;
        if(this.mother.columns() != this.father.columns()){
            throw new Exception("Mother and Father have different lengths");
        }
        this.size = this.mother.columns();
    }

    /**
     * Getter for the mother
     * @return mother elements
     */
    public INDArray getMother() {
        return this.mother;
    }

    /**
     * Getter for the father
     * @return father elements
     */
    public INDArray getFather() {
        return this.father;
    }

    /**
     * Getter for the size
     * @return
     */
    public Integer getSize() {
        return size;
    }
}

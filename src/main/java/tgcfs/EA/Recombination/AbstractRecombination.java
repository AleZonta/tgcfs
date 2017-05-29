package tgcfs.EA.Recombination;

import java.util.List;

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
    private List<Double> mother;
    private List<Double> father;
    private Integer size;

    /**
     * Constructor with two parameters
     * @param mother individual mother
     * @param father individual father
     * @throws Exception raise an exception if the two individual has different length
     */
    public AbstractRecombination(List<Double> mother, List<Double> father) throws Exception {
        this.father = father;
        this.mother = mother;
        if(this.mother.size() != this.father.size()){
            throw new Exception("Mother and Father have different lengths");
        }
        this.size = this.mother.size();
    }

    /**
     * Getter for the mother
     * @return mother elements
     */
    public List<Double> getMother() {
        return this.mother;
    }

    /**
     * Getter for the father
     * @return father elements
     */
    public List<Double> getFather() {
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

package tgcfs.Classifiers;

import tgcfs.NN.OutputsNetwork;

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
 */
public class OutputNetwork implements OutputsNetwork{
    private Boolean real;

    /**
     * Getter for real
     * @return Boolean value
     */
    public Boolean getReal() {
        return real;
    }

    /**
     * @implNote Implementation from Abstract class Algorithm
     * @param out list containing all the fields
     */
    @Override
    public void deserialise(List<Double> out) {
        if(out.get(0) >= 0.5){
            this.real = Boolean.TRUE;
        }else{
            this.real = Boolean.FALSE;
        }
    }


}

package tgcfs.Agents;

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
public class OutputNetwork implements OutputsNetwork {
    private Double speed;
    private Double bearing;

    /**
     * Getter for speed
     * @return Double value of speed
     */
    public Double getSpeed() {
        return speed;
    }

    /**
     * Getter for bearing
     * @return Double value of bearing
     */
    public Double getBearing() {
        return bearing;
    }

    /**
     * @implNote Implementation from Abstract class Algorithm
     * @param out list containing all the fields
     */
    @Override
    public void deserialise(List<Double> out) {
        this.speed = out.get(0);
        this.bearing = out.get(1);
    }
}

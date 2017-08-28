package tgcfs.Agents.Models;

import lgds.trajectories.Point;
import tgcfs.Loader.Feeder;
import tgcfs.NN.EvolvableModel;
import tgcfs.NN.InputsNetwork;
import tgcfs.Networks.Convolutionary;

import java.util.List;

/**
 * Created by Alessandro Zonta on 16/08/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 */
public class ConvAgent extends Convolutionary implements EvolvableModel {
    private Feeder feeder;
    private Integer size;


    /**
     * Constructor with zero parameter
     * It builds the network
     *
     * @param dimension dimension pictures
     */
    public ConvAgent(Integer dimension){
        super(dimension);
        this.size = dimension;
        this.feeder = null;
    }


    /**
     * @implNote Implementation from Interface
     * @return Integer value
     */
    @Override
    public Integer getArrayLength() {
        return super.getNumPar();
    }



    /**
     * @implNote Implementation from Interface
     * @return deep copy of the model
     */
    @Override
    public EvolvableModel deepCopy() {
        return new ConvAgent(this.size);
    }

    /**
     * @implNote Implementation from Interface
     * @param input input of the network
     */
    @Override
    public void fit(List<InputsNetwork> input, List<Point> points) {
        throw new NoSuchMethodError("Method not implemented");
    }

    /**
     * Getter for feeder
     * @return feeder object
     */
    public Feeder getFeeder() {
        if(this.feeder == null) throw new NullPointerException("Feeder needs to be instantiate before try to use it");
        return this.feeder;
    }

    /**
     * Setter for feeder
     * @param feeder feeder object
     */
    public void setFeeder(Feeder feeder) {
        this.feeder = feeder;
    }
}

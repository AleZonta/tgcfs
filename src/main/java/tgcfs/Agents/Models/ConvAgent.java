package tgcfs.Agents.Models;

import lgds.trajectories.Point;
import org.nd4j.linalg.dataset.DataSet;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import tgcfs.Loader.Feeder;
import tgcfs.NN.EvolvableModel;
import tgcfs.NN.InputsNetwork;
import tgcfs.Networks.Convolutionary;

import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
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
     * Constructor with one parameter
     * It builds the network
     *
     * @param dimension dimension pictures
     */
    public ConvAgent(int dimension){
        super(dimension);
        this.size = dimension;
        this.feeder = null;
    }


    /**
     * Constructor with two parameters
     * @param dimension dimension of the picture
     * @param feeder feeder system
     */
    public ConvAgent(int dimension, Feeder feeder){
        super(dimension);
        this.size = dimension;
        this.feeder = feeder;
    }

    /**
     * @implNote Implementation from Interface
     * @return Integer value
     */
    @Override
    public int getArrayLength() {
        return super.getNumPar();
    }



    /**
     * @implNote Implementation from Interface
     * @return deep copy of the model
     */
    @Override
    public EvolvableModel deepCopy() {
        return new ConvAgent(this.size, this.feeder);
    }

    /**
     * @implNote Implementation from Interface
     * @param input input of the network
     */
    @Override
    public void fit(List<InputsNetwork> input, List<Point> points) {
        throw new NoSuchMethodError("Method not implemented");
    }

    @Override
    public void fit(DataSet dataSet) {
        throw new NotImplementedException();
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


    /**
     * Erase the picture created if available
     * @param path path of the picture
     */
    public void erasePictureCreated(Path path){
        try {
            Files.delete(path);
        } catch (NoSuchFileException x) {
            System.err.format("%s: no such" + " file or directory%n", path);
        } catch (DirectoryNotEmptyException x) {
            System.err.format("%s not empty%n", path);
        } catch (IOException x) {
            // File permission problems are caught here.
            System.err.println(x);
        }
    }
}

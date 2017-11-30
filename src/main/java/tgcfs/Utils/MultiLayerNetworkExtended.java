package tgcfs.Utils;

import org.deeplearning4j.nn.api.layers.RecurrentLayer;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;

/**
 * Created by Alessandro Zonta on 30/11/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 */
public class MultiLayerNetworkExtended extends MultiLayerNetwork {

    public MultiLayerNetworkExtended(MultiLayerConfiguration conf) {
        super(conf);
    }

    public MultiLayerNetworkExtended(String conf, INDArray params) {
        super(conf, params);
    }

    public MultiLayerNetworkExtended(MultiLayerConfiguration conf, INDArray params) {
        super(conf, params);
    }

    public INDArray rnnTimeStep(INDArray input) {
        this.setInputMiniBatchSize(input.size(0));
        System.out.println(input);
        this.input = input;
        boolean inputIs2d = input.rank() == 2;
        System.out.println(inputIs2d);

        for(int i = 0; i < this.layers.length; ++i) {
            System.out.println(i);
            System.out.println(this.getLayerWiseConfigurations().getInputPreProcess(i));

            if (this.getLayerWiseConfigurations().getInputPreProcess(i) != null) {
                input = this.getLayerWiseConfigurations().getInputPreProcess(i).preProcess(input, this.getInputMiniBatchSize());
            }

            if (this.layers[i] instanceof RecurrentLayer) {
                input = ((RecurrentLayer)this.layers[i]).rnnTimeStep(input);
                System.out.println("Recurrent");
                System.out.println(input);
            } else if (this.layers[i] instanceof MultiLayerNetwork) {
                input = ((MultiLayerNetwork)this.layers[i]).rnnTimeStep(input);
                System.out.println("MultiLayerNetwork");
                System.out.println(input);
            } else {
                input = this.layers[i].activate(input, false);
                System.out.println("Normal");
                System.out.println(input);
            }
        }
        System.out.println(inputIs2d + " /" + input.rank() + " /" + this.layers[this.layers.length - 1].type());
        if (inputIs2d && input.rank() == 3 && this.layers[this.layers.length - 1].type() == Type.RECURRENT) {
            return input.tensorAlongDimension(0, new int[]{1, 0});
        } else {
            this.input = null;
            return input;
        }
    }
}

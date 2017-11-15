package tgcfs.InputOutput;

import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import tgcfs.Agents.OutputNetwork;
import tgcfs.NN.OutputsNetwork;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alessandro Zonta on 30/05/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 */
public class StraightforwardTest {
    @Test
    public void transform() throws Exception {
        List<OutputsNetwork> outputNetworks = new ArrayList<>();
        OutputNetwork out = new OutputNetwork();

        INDArray array = Nd4j.create(3);
        array.putScalar(0, 1.0);
        array.putScalar(1, 0.5);
        array.putScalar(2, 0.7);

        out.deserialise(array);
        outputNetworks.add(out);

        Transformation transformation = new Straightforward();
        //List<InputsNetwork> input = transformation.transform(outputNetworks);
        throw new Error("Finish to implement");
    }

}
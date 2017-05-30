package tgcfs.InputOutput;

import org.junit.Test;
import tgcfs.Agents.OutputNetwork;
import tgcfs.NN.InputsNetwork;
import tgcfs.NN.OutputsNetwork;

import java.util.ArrayList;
import java.util.Arrays;
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
        List<Double> list = new ArrayList<>(Arrays.asList(5.0,7.0));
        out.deserialise(list);
        outputNetworks.add(out);

        Transformation transformation = new Straightforward();
        List<InputsNetwork> input = transformation.transform(outputNetworks);
    }

}
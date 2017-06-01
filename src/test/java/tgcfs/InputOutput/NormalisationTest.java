package tgcfs.InputOutput;

import org.junit.Test;
import tgcfs.Agents.InputNetwork;

import static org.junit.Assert.assertEquals;


/**
 * Created by Alessandro Zonta on 01/06/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 */
public class NormalisationTest {
    @Test
    public void convertData() throws Exception {
        InputNetwork inputNetwork = new InputNetwork(5.0,10.0,30.0);
        Double data = 100.0;
        Double data1 = -100.0;
        Double data2 = 0.0;

        assertEquals(new Double(1.0), Normalisation.convertData(inputNetwork, data));
        assertEquals(new Double(-1.0), Normalisation.convertData(inputNetwork, data1));
        assertEquals(new Double(0.0), Normalisation.convertData(inputNetwork, data2));


        tgcfs.Classifiers.InputNetwork inputNetwork1 = new tgcfs.Classifiers.InputNetwork(5.0,20.0);
        assertEquals(new Double(1.0), Normalisation.convertData(inputNetwork1, data));
        assertEquals(new Double(-1.0), Normalisation.convertData(inputNetwork1, data1));
        assertEquals(new Double(0.0), Normalisation.convertData(inputNetwork1, data2));
    }

}
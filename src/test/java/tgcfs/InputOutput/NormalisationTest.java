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
    public void decodeDistance() throws Exception {
        Double top = 1.0;
        Double ret = Normalisation.decodeDistance(top);
        assertEquals(new Double(555.0), ret);
        Double low = -1.0;
        ret = Normalisation.decodeDistance(low);
        assertEquals(new Double(0.0), ret);

    }

    @Test
    public void fromHalfPItoTotalPI() throws Exception {
        Double data = 30.4;
        Double ret = Normalisation.fromHalfPItoTotalPI(data);
        assertEquals(data, ret);
        data = 180.0;
        ret = Normalisation.fromHalfPItoTotalPI(data);
        assertEquals(data, ret);
        data = -180.0;
        ret = Normalisation.fromHalfPItoTotalPI(data);
        assertEquals(new Double(180.0), ret);
        data = -90.0;
        ret = Normalisation.fromHalfPItoTotalPI(data);
        assertEquals(new Double(270), ret);
    }

    @Test
    public void fromTotalPItoHalfPI() throws Exception {
        Double data = 30.4;
        Double ret = Normalisation.fromTotalPItoHalfPI(data);
        assertEquals(data, ret);
        data = 180.0;
        ret = Normalisation.fromTotalPItoHalfPI(data);
        assertEquals(data, ret);
        data = 181.0;
        ret = Normalisation.fromTotalPItoHalfPI(data);
        assertEquals(new Double(-179.0), ret);
        data = 270.0;
        ret = Normalisation.fromTotalPItoHalfPI(data);
        assertEquals(new Double(-90), ret);
    }

    @Test
    public void decodeDirectionData() throws Exception {
        Double data = 30.4;
        Double ret = Normalisation.convertDirectionData(data);
        Double redata = Normalisation.decodeDirectionData(ret);
        assertEquals(data,redata,0.00001);
    }

    @Test
    public void decodeSpeed() throws Exception {
        Double data = 30.4;
        Double ret = Normalisation.convertSpeed(data);
        Double redata = Normalisation.decodeSpeed(ret);
        assertEquals(data,redata);
    }

    @Test
    public void convertSpeed() throws Exception {
        Double data = 55.5;
        Double data1 = 0.0;
        assertEquals(new Double(1.0), Normalisation.convertSpeed(data));
        assertEquals(new Double(-1.0), Normalisation.convertSpeed(data1));

        Double data2 = 1111111.0;
        assertEquals(new Double(1.0), Normalisation.convertSpeed(data2));

    }

    @Test
    public void convertDirectionData() throws Exception {
        Double data = 180.0;
        Double data1 = -180.0;
        Double data2 = 0.0;
        assertEquals(new Double(1.0), Normalisation.convertDirectionData(data));
        assertEquals(new Double(-1.0), Normalisation.convertDirectionData(data1));
        assertEquals(new Double(0.0), Normalisation.convertDirectionData(data2));

        Double data3 = 200.0;
        try{
            Normalisation.convertDirectionData(data3);
        }catch (Error e){
            assertEquals("Wrong range in input" , e.getMessage());
        }
        Double data4 = -200.0;
        try{
            Normalisation.convertDirectionData(data4);
        }catch (Error e){
            assertEquals("Wrong range in input" , e.getMessage());
        }
    }

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
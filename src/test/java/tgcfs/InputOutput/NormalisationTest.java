package tgcfs.InputOutput;

import org.junit.Test;
import tgcfs.Agents.InputNetwork;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


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
    public void fromDirectionToBearing() throws Exception {

//        double i = 0;
//        while (i < 180) {
//
//            double ret = Normalisation.fromDirectionToBearing(i);
//            System.out.println(ret);
//            i += 0.5;
//        }
//        i = 0;
//        while (i > -180) {
//
//            double ret = Normalisation.fromDirectionToBearing(i);
//            System.out.println(ret);
//            i -= 0.5;
//        }
//
        double a = -4.727666854858398;
        double b = -6.5977983474731445;
        System.out.println(Normalisation.decodeDirectionData(a));
        System.out.println(Normalisation.decodeDirectionData(b));


    }

    @Test
    public void convertToSomethingTest() throws Exception {
        double data = 30.4;
        double ret = Normalisation.convertSpeed(data);
        double redata = Normalisation.decodeSpeed(ret);

        double maxSpeed = 1.0;
        double minSpeed = -1.0;
        double b = 10.0;
        double a = 0.0;
        double val = Normalisation.convertToSomething(maxSpeed, minSpeed, b, a, ret);

        assertTrue(val == redata);

        double vall = 0d;
        double valllll = 0.1d;
        double vallllll = 0.2d;
        double valllllll = 0.3d;
        double vallllllll = 0.4d;
        double valllllllll = 0.5d;
        double vallllllllll = 0.6d;
        double valllllllllll = 0.7d;
        double vallllllllllll = 0.8d;
        double valllllllllllll = 0.9d;
        double valll = 1d;




        System.out.println(Normalisation.convertToSomething(1d, 0d, 0.001d,0.3d, vall));
        System.out.println(Normalisation.convertToSomething(1d, 0d, 0.001d,0.3d, valllll));
        System.out.println(Normalisation.convertToSomething(1d, 0d, 0.001d,0.3d, vallllll));
        System.out.println(Normalisation.convertToSomething(1d, 0d, 0.001d,0.3d, valllllll));
        System.out.println(Normalisation.convertToSomething(1d, 0d, 0.001d,0.3d, vallllllll));
        System.out.println(Normalisation.convertToSomething(1d, 0d, 0.001d,0.3d, valllllllll));
        System.out.println(Normalisation.convertToSomething(1d, 0d, 0.001d,0.3d, vallllllllll));
        System.out.println(Normalisation.convertToSomething(1d, 0d, 0.001d,0.3d, valllllllllll));
        System.out.println(Normalisation.convertToSomething(1d, 0d, 0.001d,0.3d, vallllllllllll));
        System.out.println(Normalisation.convertToSomething(1d, 0d, 0.001d,0.3d, valllllllllllll));
        System.out.println(Normalisation.convertToSomething(1d, 0d, 0.001d,0.3d, valll));

    }

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
//        Double data = 30.4;
//        Double ret = Normalisation.convertDirectionData(data);
//        Double redata = Normalisation.decodeDirectionData(ret);
//        assertEquals(data,redata,0.00001);

        System.out.println(Normalisation.decodeDirectionData(-1.0));
        System.out.println(Normalisation.decodeDirectionData(-2.0));
        System.out.println(Normalisation.decodeDirectionData(-3.0));
        System.out.println(Normalisation.decodeDirectionData(-4.0));
        System.out.println(Normalisation.decodeDirectionData(-5.0));
        System.out.println(Normalisation.decodeDirectionData(-6.0));
        System.out.println(Normalisation.decodeDirectionData(-7.0));
        System.out.println(Normalisation.decodeDirectionData(-8.0));
        System.out.println(Normalisation.decodeDirectionData(-9.0));


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
        assertTrue(1.0 == Normalisation.convertSpeed(data));
        assertTrue(-1.0 == Normalisation.convertSpeed(data1));

        Double data2 = 1111111.0;
        assertTrue(1.0 == Normalisation.convertSpeed(data2));

    }

    @Test
    public void convertDirectionData() throws Exception {
        Double data = 180.0;
        Double data1 = -180.0;
        Double data2 = 0.0;
        assertTrue(1.0 == Normalisation.convertDirectionData(data));
        assertTrue(-1.0 == Normalisation.convertDirectionData(data1));
        assertTrue(0.0 == Normalisation.convertDirectionData(data2));

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

        assertTrue(1.0 == Normalisation.convertData(inputNetwork, data));
        assertTrue(-1.0 == Normalisation.convertData(inputNetwork, data1));
        assertTrue(0.0 == Normalisation.convertData(inputNetwork, data2));


        tgcfs.Classifiers.InputNetwork inputNetwork1 = new tgcfs.Classifiers.InputNetwork(5.0,20.0);
        assertTrue(1.0 == Normalisation.convertData(inputNetwork1, data));
        assertTrue(-1.0 == Normalisation.convertData(inputNetwork1, data1));
        assertTrue(0.0 == Normalisation.convertData(inputNetwork1, data2));
    }

}
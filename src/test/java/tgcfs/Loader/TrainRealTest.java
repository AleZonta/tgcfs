package tgcfs.Loader;

import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import tgcfs.Agents.InputNetwork;
import tgcfs.Agents.OutputNetwork;
import tgcfs.Idsa.IdsaLoader;
import tgcfs.NN.InputsNetwork;
import tgcfs.NN.OutputsNetwork;
import tgcfs.Utils.PointWithBearing;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static junit.framework.TestCase.*;

/**
 * Created by Alessandro Zonta on 28/08/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 */
public class TrainRealTest {
    @Test
    public void getTrainingPoint() throws Exception {
        List<InputsNetwork> input = new ArrayList<>();
        input.add(new InputNetwork(15.0, 30.8, 15.0));
        input.add(new InputNetwork(16.0, 31.8, 15.0));
        input.add(new InputNetwork(17.0, 32.8, 15.0));
        input.add(new InputNetwork(18.0, 33.8, 15.0));
        input.add(new InputNetwork(19.0, 34.8, 15.0));
        List<PointWithBearing> p = new ArrayList<>();
        p.add(new PointWithBearing(3d,5d));
        p.add(new PointWithBearing(3d,5d));
        p.add(new PointWithBearing(3d,5d));
        p.add(new PointWithBearing(3d,5d));

        TrainReal t = new TrainReal(input, p);
        assertNotNull(t.getTrainingPoint());
        List<InputsNetwork> res = t.getTrainingPoint();
        Integer i = 0;
        for(InputsNetwork in : res){
            INDArray array = in.serialise();
            assertEquals(array.getScalar(0), input.get(i).serialise().getScalar(0));
            assertEquals(array.getScalar(1), input.get(i).serialise().getScalar(1));
            assertEquals(array.getScalar(2), input.get(i).serialise().getScalar(2));
            i++;
        }
    }

    @Test
    public void getFollowingPart() throws Exception {
        List<InputsNetwork> input = new ArrayList<>();
        input.add(new InputNetwork(15.0, 30.8, 15.0));
        input.add(new InputNetwork(16.0, 31.8, 15.0));
        input.add(new InputNetwork(17.0, 32.8, 15.0));
        input.add(new InputNetwork(18.0, 33.8, 15.0));
        input.add(new InputNetwork(19.0, 34.8, 15.0));
        List<PointWithBearing> p = new ArrayList<>();
        p.add(new PointWithBearing(3d,5d));
        p.add(new PointWithBearing(3d,5d));
        p.add(new PointWithBearing(3d,5d));
        p.add(new PointWithBearing(3d,5d));

        TrainReal t = new TrainReal(input, p);
        assertNotNull(t.getFollowingPart());

        List<PointWithBearing> res = t.getFollowingPart();
        for(int i = 0; i < res.size(); i++){
            assertEquals(res.get(i).getLatitude(), p.get(i).getLatitude());
            assertEquals(res.get(i).getAltitude(), p.get(i).getAltitude());
            assertEquals(res.get(i).getLongitude(), p.get(i).getLongitude());
            assertEquals(res.get(i).getDated(), p.get(i).getDated());
            assertEquals(res.get(i).getTime(), p.get(i).getTime());
        }
    }

    @Test
    public void getOutputComputed() throws Exception {
        List<InputsNetwork> input = new ArrayList<>();
        input.add(new InputNetwork(15.0, 30.8, 15.0));
        input.add(new InputNetwork(16.0, 31.8, 15.0));
        input.add(new InputNetwork(17.0, 32.8, 15.0));
        input.add(new InputNetwork(18.0, 33.8, 15.0));
        input.add(new InputNetwork(19.0, 34.8, 15.0));
        List<PointWithBearing> p = new ArrayList<>();
        p.add(new PointWithBearing(3d,5d));
        p.add(new PointWithBearing(3d,5d));
        p.add(new PointWithBearing(3d,5d));
        p.add(new PointWithBearing(3d,5d));

        TrainReal t = new TrainReal(input, p);
        assertNull(t.getOutputComputed());

        List<OutputsNetwork> out = new ArrayList<>();
        out.add(new OutputNetwork(12d,12d,12d));
        out.add(new OutputNetwork(13d,1d,182d));
        out.add(new OutputNetwork(14d,2d,192d));
        t.setOutputComputed(out);
        assertNotNull(t.getOutputComputed());

    }

    @Test
    public void setOutputComputed() throws Exception {
        List<InputsNetwork> input = new ArrayList<>();
        input.add(new InputNetwork(15.0, 30.8, 15.0));
        input.add(new InputNetwork(16.0, 31.8, 15.0));
        input.add(new InputNetwork(17.0, 32.8, 15.0));
        input.add(new InputNetwork(18.0, 33.8, 15.0));
        input.add(new InputNetwork(19.0, 34.8, 15.0));
        List<PointWithBearing> p = new ArrayList<>();
        p.add(new PointWithBearing(3d,5d));
        p.add(new PointWithBearing(3d,5d));
        p.add(new PointWithBearing(3d,5d));
        p.add(new PointWithBearing(3d,5d));

        TrainReal t = new TrainReal(input, p);
        assertNull(t.getOutputComputed());

        List<OutputsNetwork> out = new ArrayList<>();
        out.add(new OutputNetwork(12d,12d,12d));
        out.add(new OutputNetwork(13d,1d,182d));
        out.add(new OutputNetwork(14d,2d,192d));
        t.setOutputComputed(out);
    }

    @Test
    public void getLastPoint() throws Exception {
        List<InputsNetwork> input = new ArrayList<>();
        input.add(new InputNetwork(15.0, 30.8, 15.0));
        input.add(new InputNetwork(16.0, 31.8, 15.0));
        input.add(new InputNetwork(17.0, 32.8, 15.0));
        input.add(new InputNetwork(18.0, 33.8, 15.0));
        input.add(new InputNetwork(19.0, 34.8, 15.0));
        List<PointWithBearing> p = new ArrayList<>();
        p.add(new PointWithBearing(3d,5d));
        p.add(new PointWithBearing(3d,5d));
        p.add(new PointWithBearing(3d,5d));
        p.add(new PointWithBearing(3d,5d));

        TrainReal t = new TrainReal(input, p);
        try {
            t.getLastPoint();
        }catch (NullPointerException e){
            System.out.println("Error is true");
        }


        t.setPoints(p);

        assertNotNull(t.getLastPoint());
        assertEquals(3d,t.getLastPoint().getLatitude());
        assertEquals(5d,t.getLastPoint().getLongitude());
    }

    @Test
    public void getPoints() throws Exception {
        List<InputsNetwork> input = new ArrayList<>();
        input.add(new InputNetwork(15.0, 30.8, 15.0));
        input.add(new InputNetwork(16.0, 31.8, 15.0));
        input.add(new InputNetwork(17.0, 32.8, 15.0));
        input.add(new InputNetwork(18.0, 33.8, 15.0));
        input.add(new InputNetwork(19.0, 34.8, 15.0));
        List<PointWithBearing> p = new ArrayList<>();
        p.add(new PointWithBearing(3d,5d));
        p.add(new PointWithBearing(3d,5d));
        p.add(new PointWithBearing(3d,5d));
        p.add(new PointWithBearing(3d,5d));

        TrainReal t = new TrainReal(input, p);
        try {
            t.getLastPoint();
        }catch (NullPointerException e){
            System.out.println("Error is true");
        }


        t.setPoints(p);

        assertNotNull(t.getPoints());
        List<PointWithBearing> res = t.getPoints();
        for(int i = 0; i < res.size(); i++){
            assertEquals(res.get(i).getLatitude(), p.get(i).getLatitude());
            assertEquals(res.get(i).getLongitude(), p.get(i).getLongitude());
        }

    }

    @Test
    public void setPoints() throws Exception {
        List<InputsNetwork> input = new ArrayList<>();
        input.add(new InputNetwork(15.0, 30.8, 15.0));
        input.add(new InputNetwork(16.0, 31.8, 15.0));
        input.add(new InputNetwork(17.0, 32.8, 15.0));
        input.add(new InputNetwork(18.0, 33.8, 15.0));
        input.add(new InputNetwork(19.0, 34.8, 15.0));
        List<PointWithBearing> p = new ArrayList<>();
        p.add(new PointWithBearing(3d,5d));
        p.add(new PointWithBearing(3d,5d));
        p.add(new PointWithBearing(3d,5d));
        p.add(new PointWithBearing(3d,5d));

        TrainReal t = new TrainReal(input, p);
        try {
            t.getLastPoint();
        }catch (NullPointerException e){
            System.out.println("Error is true");
        }


        t.setPoints(p);

        assertNotNull(t.getPoints());
    }

    @Test
    public void getConditionalImage() throws Exception {
        List<InputsNetwork> input = new ArrayList<>();
        input.add(new InputNetwork(15.0, 30.8, 15.0));
        input.add(new InputNetwork(16.0, 31.8, 15.0));
        input.add(new InputNetwork(17.0, 32.8, 15.0));
        input.add(new InputNetwork(18.0, 33.8, 15.0));
        input.add(new InputNetwork(19.0, 34.8, 15.0));
        List<PointWithBearing> p = new ArrayList<>();
        p.add(new PointWithBearing(3d,5d));
        p.add(new PointWithBearing(3d,5d));
        p.add(new PointWithBearing(3d,5d));
        p.add(new PointWithBearing(3d,5d));

        TrainReal t = new TrainReal(input, p);
        String a = Paths.get(".").toAbsolutePath().normalize().toString() + "/cond.png";
        assertEquals(a, t.getConditionalImage());
    }

    @Test
    public void getIdsaLoader() throws Exception {
        List<InputsNetwork> input = new ArrayList<>();
        input.add(new InputNetwork(15.0, 30.8, 15.0));
        input.add(new InputNetwork(16.0, 31.8, 15.0));
        input.add(new InputNetwork(17.0, 32.8, 15.0));
        input.add(new InputNetwork(18.0, 33.8, 15.0));
        input.add(new InputNetwork(19.0, 34.8, 15.0));
        List<PointWithBearing> p = new ArrayList<>();
        p.add(new PointWithBearing(3d,5d));
        p.add(new PointWithBearing(3d,5d));
        p.add(new PointWithBearing(3d,5d));
        p.add(new PointWithBearing(3d,5d));

        TrainReal t = new TrainReal(input, p);
        assertNull(t.getIdsaLoader());

        Logger log =  Logger.getLogger(TrainRealTest.class.getName());

        IdsaLoader idsaLoader = new IdsaLoader(log);
        t.setIdsaLoader(idsaLoader);

        assertEquals(idsaLoader, t.getIdsaLoader());

    }

    @Test
    public void setIdsaLoader() throws Exception {
        List<InputsNetwork> input = new ArrayList<>();
        input.add(new InputNetwork(15.0, 30.8, 15.0));
        input.add(new InputNetwork(16.0, 31.8, 15.0));
        input.add(new InputNetwork(17.0, 32.8, 15.0));
        input.add(new InputNetwork(18.0, 33.8, 15.0));
        input.add(new InputNetwork(19.0, 34.8, 15.0));
        List<PointWithBearing> p = new ArrayList<>();
        p.add(new PointWithBearing(3d,5d));
        p.add(new PointWithBearing(3d,5d));
        p.add(new PointWithBearing(3d,5d));
        p.add(new PointWithBearing(3d,5d));

        TrainReal t = new TrainReal(input, p);
        assertNull(t.getIdsaLoader());

        Logger log =  Logger.getLogger(TrainRealTest.class.getName());

        IdsaLoader idsaLoader = new IdsaLoader(log);
        t.setIdsaLoader(idsaLoader);
    }

    @Test
    public void getNormalImage() throws Exception {
        List<InputsNetwork> input = new ArrayList<>();
        input.add(new InputNetwork(15.0, 30.8, 15.0));
        input.add(new InputNetwork(16.0, 31.8, 15.0));
        input.add(new InputNetwork(17.0, 32.8, 15.0));
        input.add(new InputNetwork(18.0, 33.8, 15.0));
        input.add(new InputNetwork(19.0, 34.8, 15.0));
        List<PointWithBearing> p = new ArrayList<>();
        p.add(new PointWithBearing(3d,5d));
        p.add(new PointWithBearing(3d,5d));
        p.add(new PointWithBearing(3d,5d));
        p.add(new PointWithBearing(3d,5d));

        TrainReal t = new TrainReal(input, p);
        String a =  Paths.get(".").toAbsolutePath().normalize().toString() + "/image.png";
        assertEquals(a, t.getNormalImage());
    }

}
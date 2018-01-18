package tgcfs.Routing;

import lgds.trajectories.Point;
import lgds.trajectories.Trajectory;
import org.junit.Assert;
import org.junit.Test;
import tgcfs.Config.ReadConfig;

import java.util.logging.Logger;
import java.util.stream.IntStream;

import static junit.framework.TestCase.assertNotNull;

/**
 * Created by Alessandro Zonta on 16/05/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 */
public class RoutesTest {

    @Test
    public void getTra() throws Exception {



//        class PointPrint extends Point {
//
//            public PointPrint(Double latitude, Double longitude, Double altitude, Double dated, String dates, String time) {
//                super(latitude, longitude, altitude, dated, dates, time);
//            }
//
//            public PointPrint(Double latitude, Double longitude) {
//                super(latitude, longitude);
//            }
//
//            @Override
//            public String toString() {
//                return "[" + super.getLatitude() + ", " + super.getLongitude() + "]";
//            }
//        }


        new ReadConfig.Configurations();

        Logger log =  Logger.getLogger(RoutesTest.class.getName());

        Routes routes = new Routes(log);
        routes.readTrajectories();
        assertNotNull(routes.getTra());


//        try (FileOutputStream zipFile = new FileOutputStream(new File("/Users/alessandrozonta/Desktop/tra.zip"));
//             ZipOutputStream zos = new ZipOutputStream(zipFile);
//             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(zos, "ISO-8859-1"))
//        ){
//            ZipEntry csvFile = new ZipEntry(  "tra.json");
//            zos.putNextEntry(csvFile);
//
//
//            JSONObject totalObj = new JSONObject();
//
//            Trajectories tra = routes.getTra();
//            List<Trajectory> allTra = tra.getTrajectories();
//
//            IntStream.range(0, allTra.size()).forEach(i -> {
//
//                Trajectory t = allTra.get(i);
//
//                JSONObject obj = new JSONObject();
//                JSONArray trajectory = new JSONArray();
//                //put the trajectory
//
//                List<PointPrint> ppoints = new ArrayList<>();
//                Point po = routes.getNextPosition(t);
//                while (po != null){
//                    PointPrint p = new PointPrint(po.getLatitude(), po.getLongitude());
//                    ppoints.add(p);
//                    po = routes.getNextPosition(t);
//                }
//
//                trajectory.addAll(ppoints);
//                obj.put("trajectory", trajectory);
//
//                String name = "trajectory-" + i;
//                totalObj.put(name, obj);
//            });
//
//            totalObj.put("size", allTra.size());
//            try {
////                writer.write("git-sha-1=" + PropertiesFileReader.getGitSha1());
//                writer.write(totalObj.toJSONString());
//                writer.newLine();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }


    }

    @Test
    public void getNextTrajectory() throws Exception {
        new ReadConfig.Configurations();

        Logger log =  Logger.getLogger(RoutesTest.class.getName());

        Routes routes = new Routes(log);
        routes.readTrajectories();
        Trajectory tra = routes.getNextTrajectory();
        Trajectory tra1 = routes.getNextTrajectory();
        assertNotNull(tra);
        assertNotNull(tra1);
        Assert.assertNotEquals(tra, tra1);


        IntStream.range(0,1000).forEach(i -> {
            Trajectory t = routes.getNextTrajectory();
            assertNotNull(t);
        });
    }

    @Test
    public void getNextPosition() throws Exception {

        new ReadConfig.Configurations();

        Logger log =  Logger.getLogger(RoutesTest.class.getName());

        Routes routes = new Routes(log);
        routes.readTrajectories();
        Trajectory tra = routes.getNextTrajectory();
        Point p1 = routes.getNextPosition(tra);
        Point p2 = routes.getNextPosition(tra);
        assertNotNull(p1);
        assertNotNull(p2);
        Assert.assertNotEquals(p1, p2);

        System.out.println(p1.getLongitude() + " " + p1.getLatitude());
        System.out.println(p2.getLongitude() + " " + p2.getLatitude());
        Point p3 = routes.getNextPosition(tra);
        while(p3 != null) {
            assertNotNull(p3);
            Assert.assertNotEquals(p3, p1);
            System.out.println(p3.getLongitude() + " " + p3.getLatitude());
            p3 = routes.getNextPosition(tra);
        }
    }

    @Test
    public void readTrajectories() throws Exception {
        new ReadConfig.Configurations();

        Logger log =  Logger.getLogger(RoutesTest.class.getName());

        Routes routes = new Routes(log);
        routes.readTrajectories();
    }

}
package tgcfs;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import tgcfs.Framework.TuringLearning;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
//        for(int i = 0; i < 20; i++){
            try {
                TuringLearning app = new TuringLearning();
                app.load();
                app.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
//        }

    }
}

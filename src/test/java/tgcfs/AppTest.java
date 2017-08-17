package tgcfs;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import tgcfs.EA.Agents;
import tgcfs.EA.Individual;

import java.util.List;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    public void testGenerateMoreThanDiscriminate() throws Exception {
        App app = new App();
        app.load();

        Agents a = app.getagents();
        List<Individual> popA = a.getPopulation();


        app.generateMoreThanDiscriminate(10);

        Agents b = app.getagents();
        List<Individual> popB = b.getPopulation();

        for(int i=0; i < popA.size(); i++){
            for(int j=0; j < popB.size(); j++){
                int u = 0;
                for(int h=0; h<popA.get(i).getObjectiveParameters().columns(); h++){
                    if(popA.get(i).getObjectiveParameters().getScalar(h).equals(popB.get(j).getObjectiveParameters().getScalar(h))){
                        u++;
                    }
                }
                assertTrue(u<popA.get(i).getObjectiveParameters().columns());
            }
        }


    }

    public void testRun() throws Exception {
        App app = new App();
        app.load();
        app.run();
    }


    public void testLoad() throws Exception {
        App app = new App();
        app.load();
    }

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
        assertTrue( true );
    }
}

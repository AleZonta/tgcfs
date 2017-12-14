package tgcfs.Framework;

import org.junit.Test;

/**
 * Created by Alessandro Zonta on 14/09/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 */
public class TuringLearningTest {
    @Test
    public void load() throws Exception {
        TuringLearning app = new TuringLearning();
        app.load();
    }

    @Test
    public void run() throws Exception {
        TuringLearning app = new TuringLearning();
        app.load();
        app.run();
    }


    @Test
    public void getAgents() throws Exception {
    }

}
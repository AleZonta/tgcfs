package tgcfs;

import tgcfs.Framework.TuringLearning;

/**
 * Created by Alessandro Zonta on 16/05/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 *
 * Loader class For everything.
 * This class will launch the experiment.
 * It loads all the agents, all the classifiers and lunch the experiment
 *
 */
public class App {


    public static void main( String[] args )
    {
        TuringLearning app = null;
        try {
            app = new TuringLearning();
            app.load();
            app.run();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

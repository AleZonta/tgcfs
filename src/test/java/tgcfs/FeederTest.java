package tgcfs;

import org.junit.Test;

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
public class FeederTest {
    @Test
    public void loadSystem() throws Exception {
        Feeder system = new Feeder();
        system.loadSystem();
    }

}
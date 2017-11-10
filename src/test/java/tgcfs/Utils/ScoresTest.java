package tgcfs.Utils;

import org.junit.Test;

/**
 * Created by Alessandro Zonta on 10/11/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 */
public class ScoresTest {
    @Test
    public void test() throws Exception {
        Scores s = new Scores(5,0,8,0);
        System.out.println(s.toString());
    }

}
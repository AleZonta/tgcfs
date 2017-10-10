package tgcfs.Config;

import org.junit.Test;
import tgcfs.Framework.TuringLearning;

/**
 * Created by Alessandro Zonta on 06/10/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 */
public class PropertiesFileReaderTest {
    @Test
    public void getGitSha1() throws Exception {
        TuringLearning app = new TuringLearning();
        System.out.println(PropertiesFileReader.getGitSha1());
    }

}
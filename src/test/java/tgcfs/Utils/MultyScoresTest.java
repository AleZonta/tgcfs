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
public class MultyScoresTest {
    @Test
    public void addScore() throws Exception {
        MultyScores scores = new MultyScores();
        scores.addScore(new Scores(5,0,8,0));
        scores.addScore(new Scores(1,0,8,0));
        scores.addScore(new Scores(2,0,8,0));
        scores.addScore(new Scores(5,0,8,0));
        scores.addScore(new Scores(8,0,8,0));
        scores.addScore(new Scores(8,0,8,0));
        scores.addScore(new Scores(8,0,8,0));
        scores.addScore(new Scores(8,0,8,0));

        scores.getScore().forEach(s -> System.out.println(s.toString()));


    }

}
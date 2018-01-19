package tgcfs.Utils;

import org.junit.Test;

import java.util.UUID;

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
        scores.addScore(new Scores(5, UUID.randomUUID(),8,0));
        scores.addScore(new Scores(1,UUID.randomUUID(),8,0));
        scores.addScore(new Scores(2,UUID.randomUUID(),8,0));
        scores.addScore(new Scores(5,UUID.randomUUID(),8,0));
        scores.addScore(new Scores(8,UUID.randomUUID(),8,0));
        scores.addScore(new Scores(8,UUID.randomUUID(),8,0));
        scores.addScore(new Scores(8,UUID.randomUUID(),8,0));
        scores.addScore(new Scores(8,UUID.randomUUID(),8,0));

        scores.getScore().forEach(s -> System.out.println(s.toString()));


    }

}
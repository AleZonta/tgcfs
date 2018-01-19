package tgcfs.Utils;

import java.util.ArrayList;
import java.util.List;

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
public class MultyScores {
    private List<Scores> score;

    /**
     * Constructor one parameter
     */
    public MultyScores(){
        this.score = new ArrayList<>();
    }

    /**
     * Add value to list
     * if the list has already id as the one added, increase the trajectory
     * @param scores score to add
     */
    public synchronized void addScore(Scores scores){
        this.score.add(scores);
    }

    /**
     * getter for the list
     * @return List of {@link Scores}
     */
    public List<Scores> getScore() {
        return score;
    }
}

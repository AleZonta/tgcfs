package tgcfs.Utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
        List<Scores> found = this.score.stream().filter(s -> s.getIdAgent() == scores.getIdAgent() & s.getIdClassifier() == scores.getIdClassifier()).collect(Collectors.toList());
        if(found.isEmpty()){
            this.score.add(scores);
        }else{
            found.sort(Comparator.comparingInt(Scores::getTrajectory));
            Scores f = found.get(found.size() - 1);
            this.score.add(new Scores(f.getIdAgent(), f.getTrajectory() + 1, f.getIdClassifier(), f.getScore()));
        }

    }

    /**
     * getter for the list
     * @return List of {@link Scores}
     */
    public List<Scores> getScore() {
        return score;
    }
}

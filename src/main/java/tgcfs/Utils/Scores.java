package tgcfs.Utils;

/**
 * Created by Alessandro Zonta on 10/11/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 *
 *
 * keep the scores -> agent against classifiers
 */
public class Scores {
    private int idAgent;
    private int idClassifier;
    private int score;
    private int trajectory;

    /**
     * Constructor all the parameters
     * @param idAgent id agent
     * @param trajectory id agent
     * @param idClassifier id classifiers
     * @param score score of the battle -> 0 wins the agent, 1 wins the classifier
     */
    public Scores(int idAgent, int trajectory, int idClassifier, int score) {
        this.idAgent = idAgent;
        this.trajectory = trajectory;
        this.idClassifier = idClassifier;
        this.score = score;
    }

    /**
     * Get score
     * @return int value -> 0 wins the agent, 1 wins the classifier
     */
    public int getScore() {
        return score;
    }

    /**
     * Get classifier id
     * @return int id
     */
    public int getIdClassifier() {
        return idClassifier;
    }

    /**
     * Get agent id
     * @return int id
     */
    public int getIdAgent() {
        return idAgent;
    }

    /**
     * Get trajectory number
     * @return int trajectory number
     */
    public int getTrajectory() {
        return trajectory;
    }

    /**
     * To string override method
     * @return string
     */
    @Override
    public String toString() {
        return "[" + idAgent + ", " + idClassifier + ", " + trajectory + ", " + score + "]";
    }
}

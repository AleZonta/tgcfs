package tgcfs.Performances;

import java.util.UUID;

/**
 * Created by Alessandro Zonta on 10/03/2018.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 */
public class Statistics {
    private UUID idTra;
    private double mse;

    public Statistics(UUID idTra, double mse){
        this.idTra = idTra;
        this.mse = mse;
    }

    @Override
    public String toString() {
        return "{ Trajectory: " + this.idTra.toString() + " mse: " + this.mse + " }";
    }
}

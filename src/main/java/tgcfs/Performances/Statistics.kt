package tgcfs.Performances

import java.util.*

/**
 * Created by Alessandro Zonta on 21/02/2018.
 * PhD Situational Analytics
 *
 *
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 *
 *
 * a.zonta@vu.nl
 */
class Statistics(private val idTra: UUID, private val mse: Double) {

    /**
     * To string override
     * @return print the two values
     */
    override fun toString(): String {
        return "{ Trajectory: ${this.idTra} mse: " + this.mse + "}"
    }
}

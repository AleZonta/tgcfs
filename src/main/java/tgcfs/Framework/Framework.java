package tgcfs.Framework;

/**
 * Created by Alessandro Zonta on 14/09/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 *
 * interface for the framework implemented
 */
public interface Framework {

    /**
     * Method that loads the system
     * @throws Exception something is wrong with the loading
     */
    void load() throws Exception;

    /**
     * Main loop for the algorithm
     *
     * @throws Exception if some errors occurs
     */
    void run() throws Exception;


}

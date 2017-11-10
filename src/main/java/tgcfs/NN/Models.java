package tgcfs.NN;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Alessandro Zonta on 30/05/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 *
 * abstract class representing a model
 * It gives an unique ID for every model in the system
 */
public abstract class Models {
    static final AtomicInteger NEXT_ID = new AtomicInteger(0); //A different ID per Istance
    private Integer id; //id of the agent



    /**
     * Constructor that assign an id to the agent
     */
    public Models(){
        this.id = NEXT_ID.getAndIncrement();
    }

    /**
     * Getter for the ID
     * @return Integer value used as ID
     */
    public int getId() {
        return this.id;
    }

}

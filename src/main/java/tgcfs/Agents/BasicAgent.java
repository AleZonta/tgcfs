package tgcfs.Agents;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Alessandro Zonta on 17/05/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 *
 * abstract class representing the agent
 */
public abstract class BasicAgent {
    static final AtomicInteger NEXT_ID = new AtomicInteger(0); //A different ID per Istance
    private Integer id; //id of the agent


    /**
     * Constructor that assign an id to the agent
     */
    public BasicAgent(){
        this.id = NEXT_ID.getAndIncrement();
    }

    /**
     * Getter for the ID
     * @return Integer value used as ID
     */
    public Integer getId() {
        return this.id;
    }


}

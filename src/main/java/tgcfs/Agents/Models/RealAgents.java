package tgcfs.Agents.Models;

import tgcfs.Agents.Agent;
import tgcfs.Loader.TrainReal;
import tgcfs.Utils.PointWithBearing;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alessandro Zonta on 30/06/2017.
 * PhD Situational Analytics
 * <p>
 * Computational Intelligence Group
 * Computer Science Department
 * Faculty of Sciences - VU University Amsterdam
 * <p>
 * a.zonta@vu.nl
 *
 * All the real agents are implemented in this class
 */
public class RealAgents {
    private List<Agent> realAgents;

    /**
     * Constructor that initialises the real agents
     */
    public RealAgents(){
        this.realAgents = new ArrayList<>();
    }

    /**
     * Add new real agent to the total list
     * @param points real output for the agent
     */
    public void newAgent(List<PointWithBearing> points){
        Agent agent = new Agent();
        agent.setRealOutput(points);
        this.realAgents.add(agent);
    }

    /**
     * Add new real agent to the total list
     * @param points real output for the agent
     * @param realPoint real first part of the agent
     */
    public void newAgent(List<PointWithBearing> points, List<PointWithBearing> realPoint){
        Agent agent = new Agent();
        agent.setRealOutput(points);
        agent.setRealFirstPart(realPoint);
        this.realAgents.add(agent);
    }

    /**
     * Create the real agents directly from the multiple combined list
     * @param combineInputList combined list with input and output
     */
    public void createAgent(List<TrainReal> combineInputList){
        //empty every time I create them
        this.realAgents = new ArrayList<>();
        combineInputList.forEach(comb -> this.newAgent(comb.getFollowingPart(), comb.getPoints()));
    }

    /**
     * Getter for the list of real agents
     * @return list of real agents
     */
    public List<Agent> getRealAgents() {
        return this.realAgents;
    }
}

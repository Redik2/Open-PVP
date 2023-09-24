package main;

import java.util.ArrayList;
import main.JoinRequest;

public class TeamInfo {
    public Integer units_cap;
    public Integer item_cap;
    public Integer score;
    public ArrayList<JoinRequest> join_requests;
    public String leader;

    public TeamInfo()
    {
        this.units_cap = 0;
        this.item_cap = 0;
        this.score = 0;
        this.join_requests = new ArrayList<JoinRequest>();
        this.leader = null;
    }
}

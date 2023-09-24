package main;

import mindustry.game.Team;

public class PlayerInfo
{
    public Team team;
    public Integer respawn_coldown;
    public boolean leader;

    public PlayerInfo()
    {
        this.team = Team.all[0];
        this.respawn_coldown = 0;
        this.leader = false;
    }
}

package main;

import mindustry.game.Team;

import java.util.HashMap;
import java.util.Map;

public class Cache {
    public static int time;
    public static Map<String, PlayerInfo> players_info;
    public static Map<Team, TeamInfo> teams_info;
    static
    {
        Map<String, PlayerInfo> players_info = new HashMap<String, PlayerInfo>();
        Map<Team, TeamInfo> teams_info = new HashMap<Team, TeamInfo>();
        time = 0;
    }
}

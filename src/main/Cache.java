package main;

import mindustry.game.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cache {
    public static int time;
    public static Map<String, PlayerInfo> players_info;
    public static Map<Team, TeamInfo> teams_info;

    public static Integer voiting_time;
    public static Boolean restart_voiting;
    public static List<String> voted;

    public static void reset()
    {
        players_info = new HashMap<String, PlayerInfo>();
        teams_info = new HashMap<Team, TeamInfo>();
        voiting_time = 0;
        restart_voiting = false;
        voted = new ArrayList<String>();
        time = 0;
    }
}

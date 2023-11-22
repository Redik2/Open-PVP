package main.Managers;

import arc.Events;
import arc.util.Log;
import main.Cache;
import main.OpenEvents;
import main.PlayerInfo;
import main.TeamInfo;
import mindustry.game.Team;

import java.util.ArrayList;
import java.util.HashMap;

public class CacheManager {
    public static void init()
    {
        Events.on(OpenEvents.InitGame.class, event -> {
            Log.info("Cache manager");
            Cache.reset();

            for (Team team : Team.all) {
                Cache.teams_info.put(team, new TeamInfo());
            }
        });
    }
}

package main.Managers;

import arc.Events;
import arc.util.Align;
import main.Cache;
import main.Constants.Values;
import main.Localisation;
import main.OpenEvents;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.net.Administration;

public class InfoManager {
    public static void init()
    {
        Events.on(OpenEvents.InfoUpdate.class, event ->
        {
            int time_left = Values.maxTime - Cache.time;
            int minute = (int) (time_left / 60 / 60 % 60);
            int hour = (int) (time_left / 60 / 60 / 60 % 60);
            Groups.player.forEach(p -> {
                Team team = p.team();
                String text = String.format(Localisation.local(p, "MaxUnitsAndItems"), Cache.teams_info.get(team).units_cap + 8);
                float sub = 1000;
                int ks = 0;

                while (1 < Cache.teams_info.get(team).item_cap / sub) {
                    sub = sub * 1000;
                    ks += 1;
                }

                if (Cache.teams_info.get(team).item_cap > 1000)
                    text += Math.round(Cache.teams_info.get(team).item_cap) / Math.pow(1000, ks);
                else text += Math.round(Cache.teams_info.get(team).item_cap);
                for (int i = 0; i < ks; i++) {
                    text += "K";
                }
                text += "\n" + Localisation.local(p, "TimeLeft") + (hour > 0 ? hour + "h " : "") + (minute > 0 ? minute + "min" : "");

                Call.infoPopup(p.con, text, 20f, Align.topLeft, 85, 5, 0, 0);
            });
        });
    }
}

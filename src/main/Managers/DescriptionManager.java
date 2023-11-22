package main.Managers;

import arc.Events;
import main.Cache;
import main.Constants.Values;
import main.OpenEvents;
import mindustry.net.Administration;

public class DescriptionManager {
    public void init()
    {
        Events.on(OpenEvents.DescriptionUpdate.class, event ->
        {
            int time_left = Values.maxTime - Cache.time;
            int minute = (int) (time_left / 60 / 60 % 60);
            int hour = (int) (time_left / 60 / 60 / 60 % 60);
            Administration.Config.desc.set("[#78b193]Open world PVP with mixtech\n[#78b193]Restart in [cyan]" + (hour > 0 ? hour + "h " : " ") + (minute > 0 ? minute + "m" : ""));
        });
    }
}

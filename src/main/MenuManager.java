package main;
import arc.Application;
import arc.Core;
import arc.util.Log;
import mindustry.gen.Call;
import mindustry.gen.Player;
import mindustry.ui.Menus;

import java.awt.*;

public class MenuManager {
    public static int guideMenu;

    public void setupMenus()
    {
        guideMenu = Menus.registerMenu((player, option) ->
        {
            Log.info(option);
            if (option == 1)
            {
                Core.app.openURI("https://discord.gg/QdsUAazufw");
                Log.info("dc");
            }
            else if (option == 2)
            {
                Menus.openURI("https://www.donationalerts.com/r/openpvp");
                Log.info("donation");
            }
            return;
        });
    }

    public static void callGuideMenu(Player player)
    {
        Call.menu(player.con(), guideMenu, "GUIDE", Localisation.local(player, "Guide"),
                new String[][]{{"OK"}});//, {Localisation.local(player, "Discord")}, {Localisation.local(player, "Support")}});
    }
}

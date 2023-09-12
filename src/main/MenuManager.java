package main;
import mindustry.gen.Call;
import mindustry.gen.Player;
import mindustry.ui.Menus;

public class MenuManager {
    public static int guideMenu;

    public static void setupMenus()
    {
        guideMenu = Menus.registerMenu((player, option) ->
        {
            return;
        });
    }

    public static void callGuideMenu(Player player)
    {
        Call.menu(player.con(), guideMenu, "GUIDE", Localisation.local(player, "Guide"),
                new String[][]{{"OK"}});
    }
}

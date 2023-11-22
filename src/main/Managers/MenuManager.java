package main.Managers;
import arc.util.Log;
import main.*;
import mindustry.entities.EntityGroup;
import mindustry.game.Team;
import mindustry.game.Teams;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.ui.Menus;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

public class MenuManager {
    public static int guideMenu;
    public static int joinMenu;
    public static int teamMenu;
    public static int rulesMenu;
    public static Map<String, Player[]> localPlayerList = new HashMap<String, Player[]>();

    public void setupMenus()
    {
        guideMenu = Menus.registerMenu((player, option) ->
        {
            Log.info(option);
            if (option <= 0) return;
            if (option == 1)
            {
                callRulesMenu(player);
            }
            else if (option == 2)
            {
                Call.openURI(player.con(), "https://discord.com/invite/QdsUAazufw");
            }
        });


        rulesMenu = Menus.registerMenu((player, option) ->
        {
            Log.info(option);
            callGuideMenu(player);
        });

        joinMenu = Menus.registerMenu((player, option) ->
        {
            Log.info(option);
            if (option <= 0) return;
            Player player2 = localPlayerList.get(player.uuid())[option - 1];
            if (player2.team().id == 0)
            {
                player.sendMessage(Localisation.local(player, "SpectatorTip"));
                return;
            }
            if (!player2.team().active())
            {
                player.sendMessage(Localisation.local(player, "NotActive"));
                return;
            }
            if (player2.team().id == player.team().id)
            {
                player.sendMessage(String.format(Localisation.local(player, "SameTeam"), player2.team().id));
                return;
            }
            TeamInfo info = Cache.teams_info.get(player2.team());
            info.join_requests.add(new JoinRequest(player.uuid(), Cache.time));
            player.sendMessage(String.format(Localisation.local(player, "RequestSent"), player2.team().id));
        });


        teamMenu = Menus.registerMenu((player, option) ->
        {
            Log.info(option);
            if (option <= 0) return;
            Player choosen_player = localPlayerList.get(player.uuid())[(int)Math.floor((option - 1) / 3d)];

            if ((option - 1) % 3 == 1)
            {
                if (Cache.players_info.get(choosen_player.uuid()).leader)
                {
                    player.sendMessage(Localisation.local(player, "AlreadyLeader"));
                    return;
                }
                Cache.players_info.get(choosen_player.uuid()).leader = true;
                Cache.players_info.get(player.uuid()).leader = false;
                Cache.teams_info.get(player.team()).leader = choosen_player.uuid();
                choosen_player.sendMessage(Localisation.local(choosen_player, "YouLeader"));
                player.sendMessage(Localisation.local(player, "YouNotLeader"));
            }
            else if ((option - 1) % 3 == 2)
            {
                if (choosen_player == player)
                {
                    player.sendMessage(Localisation.local(player, "KickYourself"));
                    return;
                }
                Cache.players_info.get(choosen_player.uuid()).team = Team.all[0];
                choosen_player.team(Team.all[0]);
                choosen_player.sendMessage(Localisation.local(choosen_player, "YouWasKicked"));
                player.sendMessage(Localisation.local(player, "YouKicked"));
            }
        });
    }

    public static void callGuideMenu(Player player)
    {
        Call.menu(player.con(), guideMenu, "GUIDE", Localisation.local(player, "Guide"),
                new String[][]{{"OK", Localisation.local(player, "RulesB"), Localisation.local(player, "Discord")}});
    }
    public static void callRulesMenu(Player player)
    {
        Call.menu(player.con(), rulesMenu, "RULES", Localisation.local(player, "Rules"),
                new String[][]{{Localisation.local(player, "Back")}});
    }

    public static void callJoinMenu(Player player)
    {
        Map<String, PlayerInfo> infos = Cache.players_info;
        if (player.team().id != 0)
        {
            player.sendMessage(Localisation.local(player, "OnlySpectator"));
            return;
        }
        for (Team team : Team.all)
        {
            TeamInfo info = Cache.teams_info.get(team);
            if (info.join_requests.size() != 0)
            {
                for (JoinRequest request : info.join_requests)
                {
                    if (request.player == player.uuid())
                    {
                        player.sendMessage(Localisation.local(player, "AlreadyHaveRequest"));
                        return;
                    }
                }
            }
        }
        String[][] buttons = new String[Groups.player.size() + 1][1];
        final int[] i = {0};
        buttons[i[0]] = new String[]{Localisation.local(player, "Cancel")};
        Player[] localPlayerList_ = new Player[Groups.player.size()];
        Groups.player.forEach(p ->
        {
            localPlayerList_[i[0]] = p;
            i[0] += 1;
            String hex = String.format("[#%02x%02x%02x]", Math.round(p.team().color.r * 255), Math.round(p.team().color.g * 255), Math.round(p.team().color.b * 255));
            buttons[i[0]] = new String[]{String.format("%s%s %s", hex, (infos.get(p.uuid()).leader ? "\uE809 " : "●"), p.coloredName())};
        });
        localPlayerList.put(player.uuid(), localPlayerList_);
        Call.menu(player.con(), joinMenu, "PLAYERS", " ", buttons);
    }



    public static void callTeamMenu(Player player)
    {
        Map<String, PlayerInfo> infos = Cache.players_info;
        if (player.team() == Team.all[0])
        {
            player.sendMessage(Localisation.local(player, "SpectatorError"));
            return;
        }
        if (!infos.get(player.uuid()).leader)
        {
            player.sendMessage(Localisation.local(player, "LeaderError"));
            return;
        }
        Team team = player.team();
        String[][] buttons = new String[team.data().players.size + 1][3];
        final int[] i = {0};
        buttons[i[0]] = new String[]{Localisation.local(player, "Cancel")};
        Player[] localPlayerList_ = new Player[team.data().players.size];
        team.data().players.forEach(p ->
        {
            localPlayerList_[i[0]] = p;
            i[0] += 1;
            if (p != player)
                buttons[i[0]] = new String[]{p.coloredName(), "[yellow]\uE809", "[red]✖"};
            else
                buttons[i[0]] = new String[]{p.coloredName(), "[gray]\uE809", "[gray]✖"};
        });
        localPlayerList.put(player.uuid(), localPlayerList_);
        Call.menu(player.con(), teamMenu, "TEAM", "[red]✖ []- kick | [yellow]\uE809 []- make leader", buttons);
    }
}

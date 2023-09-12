package main;

import mindustry.world.Tile;
import mindustry.game.*;
import mindustry.mod.Plugin;

import java.util.*;
import arc.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.gen.*;

import java.lang.Math;

import static mindustry.Vars.*;
import static mindustry.Vars.player;

import main.Constants.BlocksTypes;
import main.Constants.Rules;

import mindustry.net.Administration.Config;

public class Main extends Plugin {
    public Map<String, PlayerInfo> players_info = new HashMap<String, PlayerInfo>();
    public Map<Team, TeamInfo> teams_info = new HashMap<Team, TeamInfo>();
    public int last_team = 0;
    public Integer time = 0;
    public Integer maxTime = 648000;
    public Integer voiting_time = 0;
    public Boolean restart_voiting = false;
    public List<String> voted = new ArrayList<String>();

    @Override
    public void init(){
        Config.serverName.set("\uF714 [#6efacf]O[#76d5b1]p[#78b193]e[#768e77]n [#6f6c5c]P[#654b42]V[#582a2a]P");
        Config.motd.set("\uF714 [#6efacf]Discord server:[#0000ff] discord.gg/QdsUAazufw");
        
        logic.reset();
        Vars.state.rules = Rules.rules.copy();



        Events.on(EventType.WorldLoadEvent.class, event -> {
            players_info = new HashMap<String, PlayerInfo>();
            teams_info = new HashMap<Team, TeamInfo>();
            time = 0;
            last_team = 5;
            voiting_time = 0;
            restart_voiting = false;
            voted = new ArrayList<String>();

            for (Team team : Team.all) {
                teams_info.put(team, new TeamInfo());
            }

            Log.info("world load");
        });

        Events.on(EventType.PlayerJoin.class, event -> {
            Player pl = event.player;
            if (!players_info.containsKey(pl.uuid()))
            {
                players_info.put(pl.uuid(), new PlayerInfo());
            }
            pl.team(players_info.get(pl.uuid()).team);

            if (pl.team() == Team.all[0])
            {
                MenuManager.callGuideMenu(pl);
            }
        });

        Events.on(EventType.PlayerLeave.class, event -> {
            Player pl = event.player;
            if (pl.team().data().buildings.size < 5)
            {
                kill_team(pl.team());
                players_info.put(pl.uuid(), new PlayerInfo());
            }
        });
        
        Events.on(EventType.TapEvent.class, event -> {
            Player player = event.player;
            Tile tile = event.tile;

            if (player.team() == Team.all[0]) {
                if (players_info.getOrDefault(player.uuid(), new PlayerInfo()).respawn_coldown > time)
                {
                    player.sendMessage(String.format(Localisation.local(player, "respawnSpeedErr"), Math.round((players_info.getOrDefault(player.uuid(), new PlayerInfo()).respawn_coldown - time) / 60)));
                    return;
                }
                if (BlocksTypes.nonvalid_floors.contains(tile.floor()))
                {
                    player.sendMessage(Localisation.local(player, "InvalidFloor"));
                    return;
                }
                if (!VaultLogic.valid_test(tile, Team.all[0]))
                {
                    player.sendMessage(Localisation.local(player, "closeEnemyErr"));
                    return;
                }

                Team new_team = takeNewTeam();
                tile.setNet(Blocks.coreNucleus, new_team, 0);
                player.team(new_team);
                players_info.get(player.uuid()).team = new_team;
            }
        });

        Events.run(EventType.Trigger.update, () -> {
            time += 1;
            Groups.player.forEach(p -> {
                if (!players_info.containsKey(p.uuid()))
                {
                    players_info.put(p.uuid(), new PlayerInfo());
                }
            });
            
            if (restart_voiting) {
                voiting_time += 1;
                if (voiting_time > 3600) {
                    restart_voiting = false;
                    voiting_time = 0;
                    voted = new ArrayList<String>();
                    Groups.player.forEach(p -> {
                        p.sendMessage(Localisation.local(player, "votingTimeEnd"));
                    });
                } else if (voted.size() >= Math.round(Groups.player.size() * 0.5)) {
                    Groups.player.forEach(p -> {
                        p.sendMessage(Localisation.local(player, "votingRestart"));
                        restart();
                    });
                    restart_voiting = false;
                    voiting_time = 0;
                    voted = new ArrayList<String>();
                    restart();
                }
            }

            if (Vars.world.width() != 0) 
            {
                if (time % 10 == 0) 
                {
                    Groups.player.forEach(p -> {
                        if (!p.team().active() && p.team() != Team.all[0]) {
                            p.team(Team.all[0]);
                            players_info.get(p.uuid()).respawn_coldown = time + 60 * 30;
                            players_info.get(p.uuid()).team = Team.all[0];
                        }
                        if (p.team() == Team.all[0])
                        {
                            p.unit().kill();
                        }
                    });

                    for (Team team : Team.all) 
                    {
                        TeamInfo info = teams_info.get(team);
                        if (info.join_requests.size() != 0)
                        {
                            for (JoinRequest request : info.join_requests)
                            {
                                if (time - request.time > 3600)
                                {
                                    request.status = 2;
                                }
                                Groups.player.forEach(player -> {
                                    if (player.uuid() == request.player)
                                    {
                                        if (request.status == 1)
                                        {
                                            player.sendMessage(String.format(Localisation.local(player, "AcceptPlayer"), team.id));
                                            player.team(team);
                                            players_info.get(player.uuid()).team = team;
                                        }
                                        else if (request.status == 2)
                                        {
                                            player.sendMessage(String.format(Localisation.local(player, "DenyPlayer"), team.id));
                                        }
                                    }
                                    else if (player.team() == team)
                                    {
                                        if (request.status == 1)
                                        {
                                            player.sendMessage(Localisation.local(player, "AcceptPlayerToTeam"));
                                        }
                                        else if (request.status == 2)
                                        {
                                            player.sendMessage(Localisation.local(player, "DenyPlayerToTeam"));
                                        }
                                    }
                                });
                            }

                            for (int i = 0; i < info.join_requests.size(); i++)
                            {
                                if (info.join_requests.get(i).status != 0)
                                {
                                    info.join_requests.remove(i);
                                }
                            }
                        }
                    }
                }

                if (time % 60 == 0) 
                {
                    int time_left = maxTime - time;
                    int minute = (int)(time_left / 60 / 60 % 60);
                    int hour = (int)(time_left / 60 / 60 / 60 % 60);
                    Config.desc.set("[#78b193]Open world PVP with mixtech\n[#78b193]Restart in [cyan]" + (hour > 0 ? hour + "h " : " ") + (minute > 0 ? minute + "m" : ""));
                    
                    for (Team team : Team.all) 
                    {
                        TeamInfo info = teams_info.get(team);
                        info.units_cap = 0;
                        info.item_cap = 0;
                        info.score = 0;
                    }
                    
                    List<Building> cores_was = new ArrayList<Building>();
                    Vars.world.tiles.forEach(tile -> {
                        if (tile.build != null) {
                            Team bteam = tile.build.team();
                            TeamInfo info = teams_info.get(bteam);
                            if (BlocksTypes.cores.contains(tile.block()) && !cores_was.contains(tile.build))
                            {
                                if (bteam == Team.all[0])
                                {
                                    tile.build.kill();
                                }
                                else
                                {
                                    info.units_cap += tile.block().unitCapModifier;
                                    info.item_cap += tile.block().itemCapacity;
                                    cores_was.add(tile.build);
                                }
                            }
                            info.score += Math.round(tile.block().health / 100);
                        }
                    });

                    for (Team team : Team.all) 
                    {
                        if (teams_info.get(team).item_cap == 0 && team != Team.all[0] && team.id <= last_team)
                            kill_team(team);
                    }
                    
                    Groups.player.forEach(p -> {
                        Team team = p.team();
                        String text = String.format(Localisation.local(p, "MaxUnitsAndItems"), teams_info.get(team).units_cap);
                        float sub = 1000;
                        int ks = 0;

                        while (1 < teams_info.get(team).item_cap / sub) 
                        {
                            sub = sub * 1000;
                            ks += 1;
                        }

                        if (teams_info.get(team).item_cap > 1000) text += Math.round(teams_info.get(team).item_cap) / Math.pow(1000, ks);
                        else text += Math.round(teams_info.get(team).item_cap);
                        for (int i = 0; i < ks; i++) 
                        {
                            text += "K";
                        }
                        text += "\n" + Localisation.local(p, "TimeLeft") + (hour > 0 ? hour + "h " : "") + (minute > 0 ? minute + "min" : "");

                        Call.infoPopup(p.con, text, 1f, Align.topLeft, 85, 5, 0, 0);
                    });

                    if (time_left <= 0)
                    {
                        restart();
                    }
                }
            }
        });
        
    }

    @Override
    public void registerClientCommands(CommandHandler handler)
    {
        handler.<Player>register("destroy", " ", "Взрывает любую вашу постройку под вами(Не возвращает ресурсы)", (args, player) -> {
            Tile tile = player.tileOn();
            if (tile.build != null && tile.build.team() == player.team()) {
                tile.build.kill();
                player.sendMessage(Localisation.local(player, "Destroyed"));
            } else {
                player.sendMessage(Localisation.local(player, "CantDestroy"));
            }
        });

        handler.<Player>register("team", " ", "Выводит ID вашей команды", (args, player) -> {
            player.sendMessage("[gray]<[cyan]SERVER[gray]> [#78b193] ID: " + player.team().id);
        });

        handler.<Player>register("join", "<id>", "Позволяет вступить в команду другого игрока", (args, player) -> {
            if (!isInteger(args[0]))
            {
                player.sendMessage(Localisation.local(player, "NotID"));
                return;
            }
            Integer id = Integer.valueOf(args[0]);
            if (id == 0)
            {
                player.sendMessage(Localisation.local(player, "SpectatorTip"));
                return;
            }
            if (!Team.all[id].active())
            {
                player.sendMessage(Localisation.local(player, "NotActive"));
                return;
            }
            if (id == player.team().id)
            {
                player.sendMessage(String.format(Localisation.local(player, "SameTeam"), id));
                return;
            }
            if (player.team().id != 0)
            {
                player.sendMessage(Localisation.local(player, "OnlySpectator"));
                return;
            }
            for (Team team : Team.all) 
            {
                TeamInfo info = teams_info.get(team);
                if (info.join_requests.size() != 0)
                {
                    for (JoinRequest request : info.join_requests)
                    {
                        if (request.player == player.uuid())
                        {
                            player.sendMessage(Localisation.local(player, "AlreadyHaveActive"));
                            return;
                        }
                    }
                }
            }
            TeamInfo info = teams_info.get(Team.all[id]);
            info.join_requests.add(new JoinRequest(player.uuid(), time));
            player.sendMessage(String.format(Localisation.local(player, "RequestSent"), id));

            Groups.player.forEach(p -> {
                if (p.team().id == id)
                {
                    p.sendMessage(String.format(Localisation.local(player, "RequestMessageToTeam"), player.name));
                }
            });
        });

        handler.<Player>register("accept", " ", "Принимает последний сделанный запрос на вступление к вам в команду", (args, player) -> {
            TeamInfo info = teams_info.get(player.team());
            if (info.join_requests.size() == 0)
            {
                player.sendMessage(Localisation.local(player, "HaventAnyRequests"));
                return;
            }
            for (int i = 1; i <= info.join_requests.size(); i++)
            {
                if (info.join_requests.get(info.join_requests.size() - i).status == 0)
                {
                    info.join_requests.get(info.join_requests.size() - i).status = 1;
                    return;
                }
            }
        });

        handler.<Player>register("deny", " ", "Отклоняет последний сделанный запрос на вступление к вам в команду", (args, player) -> {
            TeamInfo info = teams_info.get(player.team());
            if (info.join_requests.size() == 0)
            {
                player.sendMessage(Localisation.local(player, "HaventAnyRequests"));
                return;
            }
            for (int i = 1; i <= info.join_requests.size(); i++)
            {
                if (info.join_requests.get(info.join_requests.size() - i).status == 0)
                {
                    info.join_requests.get(info.join_requests.size() - i).status = 2;
                    return;
                }
            }

        });

        handler.<Player>register("spectate", " ", "Делает вашу базу заброшенной, а вас наблюдателем", (args, player) -> {
            if (player.team() != Team.all[0]) {
                if (player.team().data().players.size <= 1) kill_team(player.team());
                players_info.get(player.uuid()).team = Team.all[0];
                player.team(Team.all[0]);
            } else {
                player.sendMessage(Localisation.local(player, "AlreadySpectate"));
            }
        });

        handler.<Player>register("restart", " ", "Vote to restart map", (args, player) -> {
            if (restart_voiting) {
                if (!voted.contains(player.uuid())) {
                    voted.add(player.uuid());
                    Groups.player.forEach(p -> {
                        p.sendMessage(String.format(Localisation.local(player, "VotingScore"), voted.size(), Math.round(Groups.player.size() * 0.5)));
                    });
                } 
                else player.sendMessage(Localisation.local(player, "Voted"));
            } else {
                voted.add(player.uuid());
                Groups.player.forEach(p -> {
                    p.sendMessage(Localisation.local(player, "VotingStart"));
                });
                restart_voiting = true;
                Groups.player.forEach(p -> {
                    p.sendMessage("[gray]<[cyan]SERVER[gray]> [#78b193]" + voted.size() + "/" + Math.round(Groups.player.size() * 0.5) + " проголосовали за рестарт");
                });
            }
        });

        handler.<Player>register("cteam", "<id>", "Меняет комманду(для админов)", (args, player) -> {
            if (player.admin)
            {
                player.team(Team.all[Integer.valueOf(args[0])]);
                player.sendMessage("[gray]<[cyan]SERVER[gray]>[#78b193] Ваша команда изменена!");
            }
            else
            {
                player.sendMessage("[gray]<[cyan]SERVER[gray]>[#ff] Вы не админ!");
            }
        });
    }

    public Team takeNewTeam () {
        last_team += 1;
        return Team.all[last_team];
    }

    public static boolean isInteger(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

    public void restart() {
        Events.fire(new EventType.GameOverEvent(Team.derelict));
    }

    public void kill_team(Team team) {
        team.data().destroyToDerelict();
    }
}

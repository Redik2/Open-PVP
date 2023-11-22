package main;

import main.Managers.CacheManager;
import main.Managers.DescriptionManager;
import main.Managers.InfoManager;
import main.Managers.MenuManager;
import mindustry.io.MapIO;
import mindustry.world.Tile;
import mindustry.game.*;
import mindustry.mod.Plugin;

import java.util.*;
import arc.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.gen.*;
import arc.func.Cons;

import java.lang.Math;

import static mindustry.Vars.*;

import main.Constants.BlocksTypes;
import mindustry.type.ItemStack;
import mindustry.net.Administration.Config;

import main.Constants.Values;

public class Main extends Plugin {
    @Override
    public void init() {
        Config.serverName.set("\uF714 [#6efacf]O[#76d5b1]p[#78b193]e[#768e77]n [#6f6c5c]P[#654b42]V[#582a2a]P");
        Config.motd.set("\uF714 [#6efacf]Discord server:[#0000ff] discord.gg/QdsUAazufw");

        MenuManager menuManager = new MenuManager();
        CacheManager.init();
        VaultLogic.init();
        InfoManager.init();
        main.Constants.Rules.init();
        menuManager.setupMenus();

        Events.on(EventType.WorldLoadEndEvent.class, event -> {
            Cache.time = 0;
            Log.info("World load end");
            Events.fire(new OpenEvents.InitGame());
        });

        Events.on(EventType.WorldLoadBeginEvent.class, event -> {
            Groups.player.forEach(player -> {
                player.team(Team.derelict);
            });
        });

        Events.on(OpenEvents.InitGame.class, event -> {
            Log.info("Game init");
        });

        Events.on(OpenEvents.GameOver.class, event -> {
            Log.info("Restart");
        });

        Events.on(EventType.GameOverEvent.class, event -> {Events.fire(new OpenEvents.GameOver());});

        Events.on(EventType.PlayerJoin.class, event -> {
            Player pl = event.player;
            Log.info(pl.locale);
            if (!Cache.players_info.containsKey(pl.uuid())) {
                Cache.players_info.put(pl.uuid(), new PlayerInfo());
            }
            pl.team(Cache.players_info.get(pl.uuid()).team);

            if (pl.team() == Team.all[0]) {
                MenuManager.callGuideMenu(pl);
            }
        });

        Events.on(EventType.PlayerLeave.class, event -> {
            Player pl = event.player;
            if (pl.team().data().buildings.size < 5) {
                kill_team(pl.team());
                Cache.players_info.put(pl.uuid(), new PlayerInfo());
            }
        });



        Events.run(EventType.Trigger.update, () -> {
            Cache.time += 1;
            Groups.player.forEach(p -> {
                if (!Cache.players_info.containsKey(p.uuid())) {
                    Cache.players_info.put(p.uuid(), new PlayerInfo());
                }
            });

            if (world.width() != 0) {
                if (Cache.time % 10 == 0) {
                    Events.fire(new OpenEvents.InfoUpdate());
                    Groups.player.forEach(p -> {
                        if (!p.team().active() && p.team() != Team.all[0]) {
                            p.team(Team.all[0]);
                            Cache.players_info.get(p.uuid()).respawn_coldown = Cache.time + 60 * 30;
                            Cache.players_info.get(p.uuid()).team = Team.all[0];
                        }
                        if (p.team() == Team.all[0]) {
                            p.unit().kill();
                            Cache.players_info.get(p.uuid()).leader = false;
                        }
                    });
                }

                if (Cache.restart_voiting) {
                    Cache.voiting_time += 1;
                    if (Cache.voiting_time > 3600) {
                        Cache.restart_voiting = false;
                        Cache.voiting_time = 0;
                        Cache.voted = new ArrayList<String>();
                        Groups.player.forEach(player -> {
                            player.sendMessage(Localisation.local(player, "votingTimeEnd"));
                        });
                    } else if (Cache.voted.size() >= vote_needs()) {
                        Groups.player.forEach(player -> {
                            player.sendMessage(Localisation.local(player, "votingRestart"));
                            restart();
                        });
                        Cache.restart_voiting = false;
                        Cache.voiting_time = 0;
                        Cache.voted = new ArrayList<String>();
                        restart();
                    }
                }

                if (Cache.time % 60 == 0) {
                    Events.fire(new OpenEvents.DescriptionUpdate());

                    for (Team team : Team.all) {
                        TeamInfo info = Cache.teams_info.get(team);
                        info.units_cap = 0;
                        info.item_cap = 0;
                        info.score = 0;
                        for (PlayerInfo pinfo : Cache.players_info.values()) {
                            if (pinfo.team == team && !team.active() && team != Team.all[0]) {
                                pinfo.team = Team.all[0];
                                pinfo.leader = false;
                                pinfo.respawn_coldown = Cache.time + 60 * 30;
                            }
                        }
                    }

                    List<Building> cores_was = new ArrayList<Building>();
                    Vars.world.tiles.forEach(tile -> {
                        if (tile.build != null) {
                            Team bteam = tile.build.team();
                            TeamInfo info = Cache.teams_info.get(bteam);
                            if (BlocksTypes.cores.contains(tile.block()) && !cores_was.contains(tile.build)) {
                                if (bteam == Team.all[0]) {
                                    tile.build.kill();
                                } else {
                                    info.units_cap += tile.block().unitCapModifier;
                                    info.item_cap += tile.block().itemCapacity;
                                    cores_was.add(tile.build);
                                }
                            }
                            info.score += Math.round(tile.block().health / 100);
                        }
                    });

                    for (Team team : Team.all) {
                        if (Cache.teams_info.get(team).item_cap == 0 && team != Team.all[0])
                            kill_team(team);
                    }

                    for (Team team : Team.all) {
                        TeamInfo info = Cache.teams_info.get(team);
                        if (info.join_requests.size() != 0) {
                            for (JoinRequest request : info.join_requests) {
                                if (Cache.time - request.time > 3600) {
                                    request.status = 2;
                                }
                                Groups.player.forEach(player -> {
                                    if (request.unread && player.team() == team && Cache.players_info.get(player.uuid()).leader) {
                                        player.sendMessage(String.format(Localisation.local(player, "RequestMessageToTeam"), Groups.player.find(pl -> {
                                            return pl.uuid() == request.player;
                                        }).name));
                                        request.unread = false;
                                    }
                                    if (player.uuid() == request.player) {
                                        if (request.status == 1) {
                                            player.sendMessage(String.format(Localisation.local(player, "AcceptPlayer"), team.id));
                                            player.team(team);
                                            Cache.players_info.get(player.uuid()).team = team;
                                        } else if (request.status == 2) {
                                            player.sendMessage(String.format(Localisation.local(player, "DenyPlayer"), team.id));
                                        }
                                    } else if (player.team() == team) {
                                        if (request.status == 1) {
                                            player.sendMessage(Localisation.local(player, "AcceptPlayerToTeam"));
                                        } else if (request.status == 2) {
                                            player.sendMessage(Localisation.local(player, "DenyPlayerToTeam"));
                                        }
                                    }
                                });
                            }

                            for (int i = 0; i < info.join_requests.size(); i++) {
                                if (info.join_requests.get(i).status != 0) {
                                    info.join_requests.remove(i);
                                }
                            }
                        }
                    }

                    if (Cache.time > Values.maxTime) {
                        restart();
                    }
                }
            }
        });

    }

    @Override
    public void registerServerCommands(CommandHandler handler)
    {
//        handler.register("host", "", "Start OpenPvP mode", args -> {
//            Log.info("Custom host.");
//            if (state.isPlaying()) {
//                Log.err("Already hosting. Type 'stop' to stop hosting first.");
//                return;
//            }
//            Cache.reset();
//            logic.reset();
//            logic.pause();
//
//            Log.info("- - - - | OPEN PVP | - - - -");
//
//            setup_map();
//
//            netServer.openServer();
//        });
//
//        handler.register("gameover", "", "Run gameover event", args -> {
//            Log.info("- - - - | OPEN PVP | - - - -");
//            Events.fire(new OpenEvents.GameOver());
//        });
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
            //player.sendMessage("[gray]<[cyan]SERVER[gray]> [#78b193] ID: " + player.team().id);
            MenuManager.callTeamMenu(player);
        });

        handler.<Player>register("join", " ", "Позволяет вступить в команду другого игрока", (args, player) -> {
            MenuManager.callJoinMenu(player);
        });

        handler.<Player>register("accept", " ", "Принимает последний сделанный запрос на вступление к вам в команду", (args, player) -> {
            TeamInfo info = Cache.teams_info.get(player.team());
            if (!Cache.players_info.get(player.uuid()).leader)
            {
                player.sendMessage(Localisation.local(player, "LeaderError"));
                return;
            }
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
            TeamInfo info = Cache.teams_info.get(player.team());
            if (!Cache.players_info.get(player.uuid()).leader)
            {
                player.sendMessage(Localisation.local(player, "LeaderError"));
                return;
            }
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
                if (player.team().data().players.size > 1 && Cache.players_info.get(player.uuid()).leader)
                {
                    player.sendMessage(Localisation.local(player, "CantBeLeader"));
                    return;
                }
                if (player.team().data().players.size <= 1) kill_team(player.team());
                Cache.players_info.get(player.uuid()).team = Team.all[0];
                Cache.players_info.get(player.uuid()).leader = false;
                player.team(Team.all[0]);
            } else {
                player.sendMessage(Localisation.local(player, "AlreadySpectate"));
            }
        });

        handler.<Player>register("restart", " ", "Vote to restart map", (args, player) -> {
            if (Cache.restart_voiting) {
                if (!Cache.voted.contains(player.uuid())) {
                    Cache.voted.add(player.uuid());
                    Groups.player.forEach(p -> {
                        p.sendMessage(String.format(Localisation.local(p, "VotingScore"), Cache.voted.size(), vote_needs()));
                    });
                } 
                else player.sendMessage(Localisation.local(player, "Voted"));
            } else {
                Cache.voted.add(player.uuid());
                Groups.player.forEach(p -> {
                    p.sendMessage(Localisation.local(p, "VotingStart"));
                });
                Cache.restart_voiting = true;
                Groups.player.forEach(p -> {
                    p.sendMessage(String.format(Localisation.local(p, "VotingScore"), Cache.voted.size(), vote_needs()));
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
        Events.fire(new OpenEvents.GameOver());
    }

    public void kill_team(Team team) {
        team.data().destroyToDerelict();
    }
    public long vote_needs() {return Groups.player.size() > 1 ? Math.round(Groups.player.size() * 0.5) + 1 : 1;}
    public void setup_map()
    {
        mindustry.maps.Map map = maps.getNextMap(Gamemode.survival, state.map);
        Log.info("Randomized next map to be @.", map.plainName());

        Groups.player.forEach(player -> {
            player.con.kick("Restart", 0);
        });


        try {
            Log.info(map.name());
            Log.info("Loading map...");
            Events.fire(new OpenEvents.InitGame());
            MapIO.loadMap(map, world.new FilterContext(map));

            Log.info("Map loaded.");
            logic.play();

        } catch (Exception exception) {
            Log.err("@: @", map.plainName(), exception.getLocalizedMessage());
        }
    }

}

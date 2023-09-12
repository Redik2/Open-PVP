package main;

import mindustry.world.Build;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.game.*;
import mindustry.world.blocks.environment.Floor;
import mindustry.mod.Plugin;

import java.util.*;

import javax.swing.text.html.BlockView;

import mindustry.type.*;
import arc.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.gen.*;
import mindustry.mod.*;
import mindustry.net.Administration;
import mindustry.world.blocks.storage.*;
import mindustry.core.GameState;
import mindustry.core.Logic;
import java.lang.Math;
import java.lang.reflect.Array;
import java.time.Year;

import static mindustry.Vars.*;

import main.Main.JoinRequest;
import main.Main.PlayerInfo;
import main.Main.TeamInfo;
import main.Constants.*;

import mindustry.net.Administration.Config;

public class Main extends Plugin {

    public class TeamInfo
    {
        public Integer units_cap;
        public Integer item_cap;
        public Integer score;
        public ArrayList<JoinRequest> join_requests;

        public TeamInfo()
        {
            this.units_cap = 0;
            this.item_cap = 0;
            this.score = 0;
            this.join_requests = new ArrayList<JoinRequest>();
        }
    }

    public class PlayerInfo
    {
        public Team team;
        public Integer respawn_coldown;

        public PlayerInfo()
        {
            this.team = Team.all[0];
            this.respawn_coldown = 0;
        }
    }

    public class JoinRequest
    {
        public String player;
        public Integer time;
        public Integer status;

        public JoinRequest(String uuid, Integer time)
        {
            this.player = uuid;
            this.time = time;
            this.status = 0;
        }
    }

    public List<Block> nonvalid_floors = new ArrayList<Block>();
    public List<Block> valid_blocks = new ArrayList<Block>();
    public List<Block> cores = new ArrayList<Block>();
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
        nonvalid_floors.add(Blocks.space);
        nonvalid_floors.add(Blocks.slag);
        nonvalid_floors.add(Blocks.tar);
        nonvalid_floors.add(Blocks.deepwater);
        nonvalid_floors.add(Blocks.cryofluid);
        nonvalid_floors.add(Blocks.deepTaintedWater);
        nonvalid_floors.add(Blocks.arkyciteFloor);

        valid_blocks.add(Blocks.shaleBoulder);
        valid_blocks.add(Blocks.sandBoulder);
        valid_blocks.add(Blocks.daciteBoulder);
        valid_blocks.add(Blocks.boulder);
        valid_blocks.add(Blocks.snowBoulder);
        valid_blocks.add(Blocks.basaltBoulder);
        valid_blocks.add(Blocks.carbonBoulder);
        valid_blocks.add(Blocks.ferricBoulder);
        valid_blocks.add(Blocks.beryllicBoulder);
        valid_blocks.add(Blocks.yellowStoneBoulder);
        valid_blocks.add(Blocks.arkyicBoulder);
        valid_blocks.add(Blocks.crystalCluster);
        valid_blocks.add(Blocks.crystallineBoulder);
        valid_blocks.add(Blocks.redIceBoulder);
        valid_blocks.add(Blocks.rhyoliteBoulder);
        valid_blocks.add(Blocks.redStoneBoulder);
        valid_blocks.add(Blocks.sporeCluster);
        valid_blocks.add(Blocks.air);

        cores.add(Blocks.coreShard);
        cores.add(Blocks.coreNucleus);
        cores.add(Blocks.coreBastion);
        cores.add(Blocks.coreCitadel);
        cores.add(Blocks.coreFoundation);
        cores.add(Blocks.coreAcropolis);
        
        logic.reset();
        state.rules = Dest_Rules.rules.copy();

        Vars.netServer.admins.addActionFilter(action -> 
        {
            if (action.type != Administration.ActionType.placeBlock) return true;

            if (!valid_test(action.tile, action.player.team()) && action.block == Blocks.vault)
            {
                action.player.sendMessage("[gray]<[cyan]SERVER[gray]> [#f]Слишком близко к вражескому ядру");
                action.tile.setNet(Blocks.air);
                return false;
            }
            return true;
        });

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

        Events.on(EventType.BlockBuildEndEvent.class, event -> {
            if (!event.breaking && event.tile.block() == Blocks.vault)
            {
                Core.app.post(() -> {
                    event.tile.setNet(Blocks.coreShard, event.tile.build.team(), 0);
                });

                Groups.player.forEach(player -> {
                    if (player.team() == event.tile.build.team()) 
                    {
                        player.sendMessage("[gray]<[cyan]SERVER[gray]> [#78b193]Ядро установлено! [gray]Если вы не видите ядро, введите /sync");
                    }
                });
            }
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
                pl.sendMessage("[cyan]При нажатии по карте, на выбранном месте появитсья ваше ядро");
            }
        });
        
        Events.on(EventType.TapEvent.class, event -> {
            Player player = event.player;
            Tile tile = event.tile;

            if (player.team() == Team.all[0]) {
                if (players_info.getOrDefault(player.uuid(), new PlayerInfo()).respawn_coldown > time)
                {
                    player.sendMessage("[gray]<[cyan]SERVER[gray]> [#ff9000]\uE80A [#ff2000]Ограничение скорости возрождения! Осталось: " + Math.round((players_info.getOrDefault(player.uuid(), new PlayerInfo()).respawn_coldown - time) / 60) + "сек");
                    return;
                }
                if (!valid_test(tile, Team.all[0]))
                {
                    player.sendMessage("[gray]<[cyan]SERVER[gray]> [#ff9000]\uE80A [#ff2000]Слишком близко к вражескому ядру!");
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
                        p.sendMessage("[gray]<[cyan]SERVER[gray]> [#78b193]Время голосования вышло!");
                    });
                } else if (voted.size() >= Math.round(Groups.player.size() * 0.5)) {
                    Groups.player.forEach(p -> {
                        p.sendMessage("[gray]<[cyan]SERVER[gray]> [#78b193]Сервер перезагружается!\nГолосование окончено");
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
                                            player.sendMessage("[gray]<[cyan]SERVER[gray]> [#78b193]Вы были приняты в команду #" + team.id);
                                            player.team(team);
                                            players_info.get(player.uuid()).team = team;
                                        }
                                        else if (request.status == 2)
                                        {
                                            player.sendMessage("[gray]<[cyan]SERVER[gray]> [#78b193]Вы не были приняты в команду #" + team.id);
                                        }
                                    }
                                    else if (player.team() == team)
                                    {
                                        if (request.status == 1)
                                        {
                                            player.sendMessage("[gray]<[cyan]SERVER[gray]> [#78b193]Запрос принят!");
                                        }
                                        else if (request.status == 2)
                                        {
                                            player.sendMessage("[gray]<[cyan]SERVER[gray]> [#78b193]Запрос отклонен!");
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
                    Config.desc.set("[#78b193]PVP в открытом мире\n[#78b193]До вайпа: [cyan]" + (hour > 0 ? hour + "h " : " ") + (minute > 0 ? minute + "m" : ""));
                    
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
                            if (cores.contains(tile.block()) && !cores_was.contains(tile.build)) 
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
                        String text = "[#78b193]Максимум юнитов: [cyan]" + (teams_info.get(team).units_cap) + "[white]\n[#78b193]Максимум предметов: [cyan]";
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
                        text += "\n[#78b193]До вайпа: [cyan]" + (hour > 0 ? hour + "ч " : "") + (minute > 0 ? minute + "мин" : "");

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
                player.sendMessage("[gray]<[cyan]SERVER[gray]> [#f]Блок уничтожен");
            } else {
                player.sendMessage("[gray]<[cyan]SERVER[gray]> [#f]Вы не можете взорвать этот блок!");
            }
        });

        handler.<Player>register("team", " ", "Выводит ID вашей команды", (args, player) -> {
            player.sendMessage("[gray]<[cyan]SERVER[gray]> [#78b193] ID: " + player.team().id);
        });

        handler.<Player>register("join", "<id>", "Позволяет вступить в команду другого игрока", (args, player) -> {
            if (!isInteger(args[0]))
            {
                player.sendMessage("[gray]<[cyan]SERVER[gray]> [#f]Это не ID");
                return;
            }
            Integer id = Integer.valueOf(args[0]);
            if (id == 0)
            {
                player.sendMessage("[gray]<[cyan]SERVER[gray]> [#f]Для вступления в спектаторы, напишите /spectate");
                return;
            }
            if (!Team.all[id].active())
            {
                player.sendMessage("[gray]<[cyan]SERVER[gray]> [#f]Можно присоедениться только к активной команде");
                return;
            }
            if (id == player.team().id)
            {
                player.sendMessage("[gray]<[cyan]SERVER[gray]> [#f]Вы уже в команде #" + id);
                return;
            }
            if (player.team().id != 0)
            {
                player.sendMessage("[gray]<[cyan]SERVER[gray]> [#f]Можно отправить запрос только если вы спектатор");
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
                            player.sendMessage("[gray]<[cyan]SERVER[gray]> [#f]У вас уже есть активный запрос");
                            return;
                        }
                    }
                }
            }
            TeamInfo info = teams_info.get(Team.all[id]);
            info.join_requests.add(new JoinRequest(player.uuid(), time));
            player.sendMessage("[gray]<[cyan]SERVER[gray]> [#78b193]Вы отправили запрос на вступление в команду #" + id);

            Groups.player.forEach(p -> {
                if (p.team().id == id)
                {
                    p.sendMessage("[gray]<[cyan]SERVER[gray]> [white]" + player.name + " [#78b193]запрашивает присоеденение к вашей команде. Любой ее участник может написать [green]/accept[#78b193] или [red]/deny");
                }
            });
        });

        handler.<Player>register("accept", " ", "Принимает последний сделанный запрос на вступление к вам в команду", (args, player) -> {
            TeamInfo info = teams_info.get(player.team());
            if (info.join_requests.size() == 0)
            {
                player.sendMessage("[gray]<[cyan]SERVER[gray]> [#f]У вас нет активных запросов на вступление");
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
                player.sendMessage("[gray]<[cyan]SERVER[gray]> [#f]У вас нет активных запросов на вступление");
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
                player.sendMessage("[gray]<[cyan]SERVER[gray]> [#78b193]Вы уже спектатор!");
            }
        });

        handler.<Player>register("time", " ", "Показывает время до вайпа", (args, player) -> {
            String text = "[#78b193]" + (int)Math.round((maxTime - time) / 360) / 10.0 + " минут [cyan]до вайпа";
            player.sendMessage(text);
        });

        handler.<Player>register("restart", " ", "Vote to restart map", (args, player) -> {
            if (restart_voiting) {
                if (!voted.contains(player.uuid())) {
                    voted.add(player.uuid());
                    Groups.player.forEach(p -> {
                        p.sendMessage("[gray]<[cyan]SERVER[gray]> [#78b193]" + voted.size() + "/" + Math.round(Groups.player.size() * 0.5) + " проголосовали за рестарт");
                    });
                } 
                else player.sendMessage("[gray]<[cyan]SERVER[gray]> [#78b193]Вы уже голосовали");
            } else {
                voted.add(player.uuid());
                Groups.player.forEach(p -> {
                    p.sendMessage("[gray]<[cyan]SERVER[gray]> [#78b193]Начинается голосование за рестарт!\nНапишите [cyan]/restart[#78b193] чтобы проголосовать [cyan]за[#78b193] рестарт\nГолосование окончиться через 1мин");
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

    public Boolean valid_test(Tile tile, Team team) {
        Boolean is_valid = true;
        for (int x_add = -100; x_add <= 100; x_add+=3)
        {
            for (int y_add = -100; y_add <= 100; y_add+=3)
            {
                if (Math.sqrt((x_add * x_add) + (y_add * y_add)) < 100)
                {
                    Tile test_tile = Vars.world.tile(tile.x + x_add, tile.y + y_add);
                    if ((test_tile != null) && (cores.contains(test_tile.block())) && (test_tile.build.team() != team))
                    {
                        is_valid = false;
                        break;
                    }
                }
            }
            if (!is_valid) break;
        }
        return is_valid;
    }

    public void restart() {
        Events.fire(new EventType.GameOverEvent(Team.derelict));
    }

    public void kill_team(Team team) {
        team.data().destroyToDerelict();
    }
}

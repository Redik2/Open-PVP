package main;

import arc.Core;
import arc.Events;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.net.Administration;
import mindustry.world.Build;
import mindustry.world.Tile;
import main.Constants.BlocksTypes;

public class VaultLogic {

    private static Team takeNewTeam () {
        for (Team team : Team.all)
        {
            if (!team.active() && team.id > 5)
            {
                return team;
            }
        }
        return Team.all[0];
    }
    public static void init() {

        Vars.netServer.admins.addActionFilter(action ->
        {
            if (action.type != Administration.ActionType.placeBlock) return true;

            if (!valid_test(action.tile, action.player.team()) && action.block == Blocks.vault) {
                action.player.sendMessage(Localisation.local(action.player, "closeEnemyErr"));
                action.tile.setNet(Blocks.air);
                return false;
            }
            return true;
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
                        player.sendMessage(Localisation.local(player, "CoreSet"));
                    }
                });
            }
        });

        Events.on(EventType.TapEvent.class, event -> {
            Player player = event.player;
            Tile tile = event.tile;

            if (player.team() == Team.all[0]) {
                if (Cache.players_info.getOrDefault(player.uuid(), new PlayerInfo()).respawn_coldown > Cache.time) {
                    player.sendMessage(String.format(Localisation.local(player, "respawnSpeedErr"), Math.round((Cache.players_info.getOrDefault(player.uuid(), new PlayerInfo()).respawn_coldown - Cache.time) / 60)));
                    return;
                }
                if (!VaultLogic.valid_test(tile, Team.all[0])) {
                    player.sendMessage(Localisation.local(player, "closeEnemyErr"));
                    return;
                }
                if (BlocksTypes.nonvalid_floors.contains(tile.floor()) || !Build.validPlace(Blocks.malign, Team.all[0], tile.x, tile.y, 0)) {
                    player.sendMessage(Localisation.local(player, "InvalidFloor"));
                    return;
                }

                Team new_team = takeNewTeam();
                tile.setNet(Blocks.coreNucleus, new_team, 0);
                player.team(new_team);
                Cache.players_info.get(player.uuid()).team = new_team;
                Cache.players_info.get(player.uuid()).leader = true;
                Cache.teams_info.get(new_team).leader = player.uuid();
            }
        });
    }
    public static Boolean valid_test(Tile tile, Team team) {
        boolean is_valid = true;
        int r = 128;
        for (int x_add = -r; x_add <= r; x_add+=3)
        {
            for (int y_add = -r; y_add <= r; y_add+=3)
            {
                if (Math.sqrt((x_add * x_add) + (y_add * y_add)) < r)
                {
                    Tile test_tile = Vars.world.tile(tile.x + x_add, tile.y + y_add);
                    if ((test_tile != null) && (BlocksTypes.cores.contains(test_tile.block())) && (test_tile.build.team() != team))
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

}

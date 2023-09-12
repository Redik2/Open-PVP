package main;

import arc.Core;
import arc.Events;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.Groups;
import mindustry.net.Administration;
import mindustry.world.Tile;
import main.Constants.BlocksTypes;

public class VaultLogic {
    public VaultLogic() {
        Vars.netServer.admins.addActionFilter(action ->
        {
            if (action.type != Administration.ActionType.placeBlock) return true;

            if (!valid_test(action.tile, action.player.team()) && action.block == Blocks.vault) {
                action.player.sendMessage("[gray]<[cyan]SERVER[gray]> [#f]Слишком близко к вражескому ядру");
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
                        player.sendMessage("[gray]<[cyan]SERVER[gray]> [#78b193]Ядро установлено! [gray]Если вы не видите ядро, введите /sync");
                    }
                });
            }
        });
    }
    public static Boolean valid_test(Tile tile, Team team) {
        boolean is_valid = true;
        for (int x_add = -100; x_add <= 100; x_add+=3)
        {
            for (int y_add = -100; y_add <= 100; y_add+=3)
            {
                if (Math.sqrt((x_add * x_add) + (y_add * y_add)) < 100)
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

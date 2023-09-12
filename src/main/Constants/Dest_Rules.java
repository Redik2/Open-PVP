package main.Constants;

import arc.struct.ObjectSet;
import arc.struct.Seq;
import mindustry.content.Blocks;
import mindustry.game.SpawnGroup;
import mindustry.game.Team;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.defense.turrets.LaserTurret;
import mindustry.world.blocks.units.Reconstructor;

import static mindustry.content.UnitTypes.*;

public class Dest_Rules {
    public static final mindustry.game.Rules rules = new mindustry.game.Rules();

    static {
        rules.enemyCoreBuildRadius = 50.5f;
        rules.canGameOver = false;
        rules.defaultTeam = Team.all[0];

        rules.modeName = "OPVP";

        rules.unitCap = 8;

        Blocks.coreShard.unitCapModifier = 0;
        Blocks.coreShard.itemCapacity = 0;
        Blocks.coreFoundation.unitCapModifier = 2;
        Blocks.coreNucleus.unitCapModifier = 6;
    }
}

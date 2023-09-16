package main.Constants;

import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.game.Team;

public class Rules {
    public static void init(mindustry.game.Rules rules)
    {
        rules.enemyCoreBuildRadius = 50.5f;
        rules.hideBannedBlocks = true;

        rules.buildCostMultiplier = 1f;
        rules.buildSpeedMultiplier = 1f;

        rules.canGameOver = false;
        rules.defaultTeam = Team.all[0];
        rules.modeName = "OPVP";

        Vars.state.rules.unitCap = 10;
        Blocks.coreShard.unitCapModifier = 0;
        Blocks.coreShard.itemCapacity = 0;
        Blocks.coreFoundation.unitCapModifier = 2;
        Blocks.coreNucleus.unitCapModifier = 6;
    }
}

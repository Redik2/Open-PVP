package main.Constants;

import mindustry.content.Blocks;
import mindustry.game.Team;

public class Rules {
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

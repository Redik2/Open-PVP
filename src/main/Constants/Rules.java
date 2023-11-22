package main.Constants;

import arc.Events;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Items;
import mindustry.game.Team;
import mindustry.type.ItemStack;

import main.OpenEvents;

public class Rules {
    public static void init()
    {
        Events.on(OpenEvents.InitGame.class, event -> {
            Blocks.coreShard.unitCapModifier = 0;
            Blocks.coreShard.itemCapacity = 0;
            Blocks.coreFoundation.itemCapacity = 5000;
            Blocks.coreFoundation.unitCapModifier = 1;
            Blocks.coreNucleus.itemCapacity = 25000;
            Blocks.coreNucleus.unitCapModifier = 8;
        });
    }
}

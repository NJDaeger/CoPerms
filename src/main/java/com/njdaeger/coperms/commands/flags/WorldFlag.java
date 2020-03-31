package com.njdaeger.coperms.commands.flags;


import com.njdaeger.coperms.CoPerms;
import com.njdaeger.coperms.data.CoWorld;
import com.njdaeger.pdk.command.CommandContext;
import com.njdaeger.pdk.command.TabContext;
import com.njdaeger.pdk.command.exception.PDKCommandException;
import com.njdaeger.pdk.command.flag.Flag;

public final class WorldFlag extends Flag<CoWorld>  {
    
    public WorldFlag() {
        super(CoWorld.class, "Finds a given world", "-w <world>", "w");
    }

    @Override
    public void complete(TabContext context) {
        context.completion(CoPerms.getInstance().getWorlds().keySet().toArray(new String[0]));
    }

    @Override
    public CoWorld parse(CommandContext context, String argument) throws PDKCommandException {
        if (argument == null) throw new PDKCommandException();
        CoWorld world = CoPerms.getInstance().getWorld(argument);
        if (world == null) throw new PDKCommandException();
        else return world;
    }
}

package com.njdaeger.coperms.commands.flags;

import com.njdaeger.bci.base.BCIException;
import com.njdaeger.bci.types.ParsedType;
import com.njdaeger.coperms.CoPerms;
import com.njdaeger.coperms.data.CoWorld;

public final class WorldParser extends ParsedType<CoWorld> {
    
    @Override
    public CoWorld parse(String input) throws BCIException {
        if (input == null) throw new BCIException();
        CoWorld world = CoPerms.getInstance().getWorld(input);
        if (world == null) throw new BCIException();
        else return world;
    }
    
    @Override
    public Class<CoWorld> getType() {
        return CoWorld.class;
    }
}

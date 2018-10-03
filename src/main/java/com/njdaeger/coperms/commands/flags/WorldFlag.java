package com.njdaeger.coperms.commands.flags;

import com.njdaeger.bci.flags.AbstractFlag;

public final class WorldFlag extends AbstractFlag<WorldParser> {
    
    public WorldFlag() {
        super('w', ':');
    }
    
    @Override
    protected WorldParser getFlagType() {
        return new WorldParser();
    }
}

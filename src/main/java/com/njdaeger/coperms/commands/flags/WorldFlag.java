package com.njdaeger.coperms.commands.flags;

import com.njdaeger.bci.base.executors.TabExecutor;
import com.njdaeger.bci.defaults.TabContext;
import com.njdaeger.bci.flags.AbstractFlag;
import com.njdaeger.coperms.CoPerms;

public final class WorldFlag extends AbstractFlag<WorldParser> implements TabExecutor<TabContext> {
    
    public WorldFlag() {
        super("w", ':');
    }
    
    @Override
    public WorldParser getFlagType() {
        return new WorldParser();
    }

    @Override
    public void complete(TabContext context) {
        context.completion(CoPerms.getInstance().getWorlds().keySet().stream().map("w:"::concat).toArray(String[]::new));
    }
}

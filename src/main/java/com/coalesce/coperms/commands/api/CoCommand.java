package com.coalesce.coperms.commands.api;

import com.coalesce.coperms.CoPerms;
import com.coalesce.core.command.base.CommandContext;
import com.coalesce.core.command.base.ProcessedCommand;
import com.coalesce.core.command.base.TabContext;

public class CoCommand extends ProcessedCommand<CommandContext, TabContext, CoBuilder> {
    
    public CoCommand(CoPerms plugin, String name) {
        super(plugin, name);
    }
    
    public static CoBuilder builder(String name) {
        return new CoBuilder(CoPerms.getPlugin(CoPerms.class), name);
    }
    
}

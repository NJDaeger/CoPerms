package com.coalesce.coperms.commands.api;

import com.coalesce.coperms.CoPerms;
import com.coalesce.core.command.base.CommandContext;
import com.coalesce.core.command.base.ProcessedCommand;
import com.coalesce.core.command.base.TabContext;

public class CoCommand extends ProcessedCommand<CommandContext, TabContext, CoBuilder> {
    
    private static CoPerms plugin;
    
    public CoCommand(CoPerms plugin, String name) {
        super(plugin, name);
        CoCommand.plugin = plugin;
    }
    
    public static CoBuilder builder(String name) {
        return new CoBuilder(plugin, name);
    }
    
}

package com.coalesce.coperms.commands.api;

import com.coalesce.coperms.CoPerms;
import com.coalesce.core.command.base.CommandBuilder;
import com.coalesce.core.command.base.CommandContext;
import com.coalesce.core.command.base.TabContext;

public class CoBuilder extends CommandBuilder<CommandContext, TabContext, CoBuilder, CoCommand> {
    
    /**
     * Creates a new CommandBuilder
     *
     * @param plugin The plugin the command is registered to
     * @param name   The name of the command
     */
    public CoBuilder(CoPerms plugin, String name) {
        super(plugin, name, new CoCommand(plugin, name));
    }
    
    @Override
    public CoCommand build() {
        return command;
    }
}

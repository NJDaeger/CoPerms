package com.coalesce.coperms.commands.api;

import com.coalesce.coperms.CoPerms;
import com.coalesce.core.command.base.CommandContext;
import com.coalesce.core.command.base.CommandRegister;
import com.coalesce.core.command.base.ProcessedCommand;
import com.coalesce.core.command.base.TabContext;
import com.coalesce.core.wrappers.CoSender;
import org.bukkit.command.CommandSender;

import java.util.List;

public class CoRegister extends CommandRegister<CommandContext, TabContext, CoBuilder, CoCommand> {
    
    private final CoPerms plugin;
    
    public CoRegister(CoCommand command, CoPerms plugin) {
        super(command, plugin);
        this.plugin = plugin;
    }
    
    @Override
    public boolean execute(CommandSender sender, String alias, String[] args) {
        return command.run(new CommandContext<>(new CoSender(plugin, sender), alias, args, plugin));
    }
    
    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        return command.complete(new TabContext<>(new CommandContext<>(new CoSender(plugin, sender), alias, args, plugin), command, args));
    }
}

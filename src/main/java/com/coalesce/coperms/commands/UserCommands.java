package com.coalesce.coperms.commands;

import com.coalesce.command.CoCommand;
import com.coalesce.command.CommandBuilder;
import com.coalesce.command.CommandContext;
import com.coalesce.command.tabcomplete.TabContext;
import com.coalesce.coperms.CoPerms;
import com.coalesce.plugin.CoModule;
import org.bukkit.Bukkit;

import java.util.HashSet;
import java.util.Set;

public final class UserCommands extends CoModule {
	
	private final CoPerms plugin;
	
	public UserCommands(CoPerms plugin) {
		super(plugin, "User Commands");
		this.plugin = plugin;
	}
	
	@Override
	protected void onEnable() throws Exception {
		CoCommand promote = new CommandBuilder(plugin, "promote")
				.executor(this::promote)
				.completer(this::promoteTab)
				.aliases("copro", "promo")
				.description("Promotes someone to the next rank.")
				.usage("/promote <user> [world]")
				.minArgs(1)
				.maxArgs(2)
				.permission("coperms.promote")
				.build();
		
		plugin.addCommand(promote);
	}
	
	@Override
	protected void onDisable() throws Exception {
	
	}
	
	private void promote(CommandContext context) {
	
	}
	
	private void promoteTab(TabContext context) {
	
	}
	
}

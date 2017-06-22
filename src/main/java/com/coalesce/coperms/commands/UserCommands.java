package com.coalesce.coperms.commands;

import com.coalesce.command.CoCommand;
import com.coalesce.command.CommandBuilder;
import com.coalesce.command.CommandContext;
import com.coalesce.command.tabcomplete.TabContext;
import com.coalesce.coperms.CoPerms;
import com.coalesce.coperms.DataHolder;
import com.coalesce.plugin.CoModule;
import org.bukkit.Bukkit;

import java.util.HashSet;
import java.util.Set;

public final class UserCommands extends CoModule {
	
	private final CoPerms plugin;
	private final DataHolder holder;
	
	public UserCommands(CoPerms plugin, DataHolder holder) {
		super(plugin, "User Commands");
		this.plugin = plugin;
		this.holder = holder;
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
		
		CoCommand setrank = new CommandBuilder(plugin, "setrank")
				.executor(this::setrank)
				.completer(this::setrankTab)
				.aliases("setuser", "setr")
				.description("Adds a user to a specified rank")
				.usage("/setrank <user> <rank> [world]") //Make a list in the UserDataFile for each user that is similar to the group data file that specifies ranks per world
				.minArgs(2)
				.maxArgs(3)
				.permission("coperms.setrank")
				.build();
		
		plugin.addCommand(promote, setrank);
	}
	
	@Override
	protected void onDisable() throws Exception {
	
	}
	
	private void promote(CommandContext context) {
	
	}
	
	private void promoteTab(TabContext context) {
	
	}
	
	private void setrank(CommandContext context) {
	
	}
	
	private void setrankTab(TabContext context) {
	
	}
	
}

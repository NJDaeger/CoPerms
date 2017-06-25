package com.coalesce.coperms.commands;

import com.coalesce.command.CoCommand;
import com.coalesce.command.CommandBuilder;
import com.coalesce.command.CommandContext;
import com.coalesce.command.tabcomplete.TabContext;
import com.coalesce.coperms.CoPerms;
import com.coalesce.coperms.DataHolder;
import com.coalesce.coperms.data.CoUser;
import com.coalesce.coperms.data.CoWorld;
import com.coalesce.coperms.data.Group;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.bukkit.ChatColor.*;

public final class UserCommands {
	
	private final CoPerms plugin;
	private final DataHolder holder;
	
	public UserCommands(CoPerms plugin, DataHolder holder) {
		this.plugin = plugin;
		this.holder = holder;
		
		CoCommand promote = new CommandBuilder(plugin, "promote")
				.executor(this::promote)
				.completer(this::promoteTab)
				.aliases("promo")
				.description("Promotes someone to the next rank.")
				.usage("/promote <user> [world]")
				.minArgs(1)
				.maxArgs(2)
				.permission("coperms.promote")
				.build();
		
		CoCommand setRank = new CommandBuilder(plugin, "setrank")
				.executor(this::setRank)
				.completer(this::setRankTab)
				.aliases("setr", "setgroup")
				.description("Adds a user to a specified rank")
				.usage("/setrank <user> <rank> [world]") //Make a list in the UserDataFile for each user that is similar to the group data file that specifies ranks per world
				.minArgs(2)
				.maxArgs(3)
				.permission("coperms.setrank")
				.build();
		
		CoCommand demote = new CommandBuilder(plugin, "demote")
				.executor(this::demote)
				.completer(this::demoteTab)
				.aliases("demo")
				.description("Demotes someone to the previous rank.")
				.usage("/demote <user> [world]")
				.minArgs(1)
				.maxArgs(2)
				.permission("coperms.demote")
				.build();
		
		plugin.addCommand(promote, setRank, demote);
	}
	
	//
	//
	//
	//
	
	private void promote(CommandContext context) {
		CoWorld world = holder.getWorld(context.argAt(1)) == null ? holder.getDefaultWorld() : holder.getWorld(context.argAt(1));
		CoUser user = holder.getUser((context.getArgs().size() == 1 ? holder.getDefaultWorld().getName() : context.argAt(1)), context.argAt(0));
		if (world == null) {
			context.pluginMessage(RED + "The world specified does not exist.");
			return;
		}
		if (user == null) {
			context.pluginMessage(RED + "The user specified does not exist in the world specified");
			return;
		}
		Group group = world.getGroup(user.getGroup().getRankID() + 1);
		if (group == null) {
			context.pluginMessage(RED + "The group specified does not exist in the world specified");
			return;
		}
		user.setGroup(world, group.getName());
		context.pluginMessage(
				DARK_AQUA + user.getName() +
				GRAY + " was promoted to " +
				DARK_AQUA + group.getName() +
				GRAY + " in world " +
				DARK_AQUA + world.getName());
	}
	
	private void promoteTab(TabContext context) {
		List<String> names = new ArrayList<>();
		Set<String> worlds = holder.getWorlds().keySet();
		Bukkit.getOnlinePlayers().forEach(u -> names.add(u.getName()));
		context.completionAt(0, names.toArray(new String[names.size()]));
		context.completionAt(1, worlds.toArray(new String[worlds.size()]));
	}
	
	//
	//
	//
	//
	
	private void demote(CommandContext context) {
		CoWorld world = holder.getWorld(context.argAt(1)) == null ? holder.getDefaultWorld() : holder.getWorld(context.argAt(1));
		CoUser user = holder.getUser((context.getArgs().size() == 1 ? holder.getDefaultWorld().getName() : context.argAt(1)), context.argAt(0));
		if (world == null) {
			context.pluginMessage(RED + "The world specified does not exist.");
			return;
		}
		if (user == null) {
			context.pluginMessage(RED + "The user specified does not exist in the world specified");
			return;
		}
		Group group = world.getGroup(user.getGroup().getRankID() - 1);
		if (group == null) {
			context.pluginMessage(RED + "The group specified does not exist in the world specified");
			return;
		}
		user.setGroup(world, group.getName());
		context.pluginMessage(
				DARK_AQUA + user.getName() +
				GRAY + " was demoted to " +
				DARK_AQUA + group.getName() +
				GRAY + " in world " +
				DARK_AQUA + world.getName());
	}
	
	private void demoteTab(TabContext context) {
	}
	
	//
	//
	//
	//
	
	private void setRank(CommandContext context) {
		CoWorld world = holder.getWorld(context.argAt(2)) == null ? holder.getDefaultWorld() : holder.getWorld(context.argAt(2));
		CoUser user = holder.getUser((context.getArgs().size() < 3 ? holder.getDefaultWorld().getName() : context.argAt(2)), context.argAt(0));
		if (world == null) {
			context.pluginMessage(RED + "The world specified does not exist.");
			return;
		}
		Group group = world.getGroup(context.argAt(1));
		if (user == null) {
			context.pluginMessage(RED + "The user specified does not exist in the world specified");
			return;
		}
		if (group == null) {
			context.pluginMessage(RED + "The group specified does not exist in the world specified");
			return;
		}
		user.setGroup(world, group.getName());
		context.pluginMessage(
				DARK_AQUA + user.getName() +
				GRAY + " was added to group " +
				DARK_AQUA + group.getName() +
				GRAY + " in " +
				DARK_AQUA + world.getName());
	}
	
	private void setRankTab(TabContext context) {
		List<String> names = new ArrayList<>();
		Set<String> groups = holder.getGroups().keySet();
		Set<String> worlds = holder.getWorlds().keySet();
		Bukkit.getOnlinePlayers().forEach(u -> names.add(u.getName()));
		context.completionAt(0, names.toArray(new String[names.size()]));
		context.completionAt(1, groups.toArray(new String[groups.size()]));
		context.completionAt(2, worlds.toArray(new String[worlds.size()]));
	}
	
}

package com.coalesce.coperms.commands;

import com.coalesce.command.CoCommand;
import com.coalesce.command.CommandBuilder;
import com.coalesce.command.CommandContext;
import com.coalesce.command.tabcomplete.TabContext;
import com.coalesce.coperms.CoPerms;
import com.coalesce.coperms.DataHolder;
import com.coalesce.coperms.data.CoUser;
import com.coalesce.coperms.data.CoWorld;
import org.bukkit.Bukkit;

import static org.bukkit.ChatColor.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
		
		plugin.addCommand(promote, setRank);
	}
	
	private void promote(CommandContext context) {
	
	}
	
	private void promoteTab(TabContext context) {
	
	}
	
	private void demote(CommandContext context) {
		if (holder.getUser(context.argAt(0)) == null) {
		
		}
	}
	
	private void demoteTab(TabContext context) {
	}
	
	private void setRank(CommandContext context) {
		CoUser user;
		if (holder.getUser(context.argAt(0)) == null) {
			if (context.getArgs().size() == 3) {
				CoWorld world = holder.getWorld(context.argAt(2));
				if (world == null) {
					context.pluginMessage(RED + "The world specified does not exist.");
					return;
				}
				user = holder.getUser(world.getWorld(), context.argAt(0));
				if (user == null) {
					context.pluginMessage(RED + "The user specified does not exist in the world specified");
					return;
				}
				if (world.getGroup(context.argAt(1)) == null) {
					context.pluginMessage(RED + "Group does not exist in the world specified.");
					return;
				}
				user.setGroup(world, context.argAt(1));
				context.pluginMessage(
						DARK_AQUA + user.getName() +
						GRAY + " was added to group " +
						DARK_AQUA + context.argAt(1) +
						GRAY + " in world " +
						DARK_AQUA + context.argAt(2));
				return;
			}
			user = holder.getDefaultWorld().getUser(context.argAt(0));
			if (user == null) {
				context.pluginMessage(RED + "The user specified does not exist in the world specified");
				return;
			}
			if (holder.getDefaultWorld().getGroup(context.argAt(1)) == null) {
				context.pluginMessage(RED + "Group does not exist in the world specified.");
				return;
			}
			user.setGroup(holder.getDefaultWorld(), context.argAt(1));
			context.pluginMessage(
					DARK_AQUA + user.getName() +
					GRAY + " was added to group " +
					DARK_AQUA + context.argAt(1) +
					GRAY + " in world " +
					DARK_AQUA + holder.getDefaultWorld().getWorld().getName());
			return;
		}
		user = holder.getUser(context.argAt(0));
		if (context.getArgs().size() == 3) {
			if (holder.getWorld(context.argAt(2)) == null) {
				context.pluginMessage(RED + "The world specified does not exist.");
				return;
			}
			if (holder.getWorld(context.argAt(2)).getGroup(context.argAt(1)) == null) {
				context.pluginMessage(RED + "Group does not exist in the world specified.");
				return;
			}
			context.pluginMessage(
					DARK_AQUA + user.getName() +
					GRAY + " was added to group " +
					DARK_AQUA + context.argAt(1) +
					GRAY + " in world " +
					DARK_AQUA + context.argAt(2));
			user.setGroup(holder.getWorld(context.argAt(2)), context.argAt(1));
			return;
		}
		if (user.getWorld().getGroup(context.argAt(1)) == null) {
			context.pluginMessage(RED + "Group does not exist in the users current world.");
			return;
		}
		user.setGroup(user.getWorld(), context.argAt(1));
		context.pluginMessage(
				DARK_AQUA + user.getName() +
				RESET + GRAY + " was added to group " +
				DARK_AQUA + context.argAt(1) +
				RESET + GRAY + " in " +
				DARK_AQUA + user.getWorld().getWorld().getName());
	}
	
	private void setRankTab(TabContext context) {
		List<String> names = new ArrayList<>();
		Set<String> groups = holder.getGroups().keySet();
		Set<String> worlds = holder.getWorlds().keySet();
		Bukkit.getOnlinePlayers().forEach(p -> names.add(p.getName()));
		context.completionAt(0, names.toArray(new String[names.size()]));
		context.completionAt(1, groups.toArray(new String[groups.size()]));
		context.completionAt(2, worlds.toArray(new String[worlds.size()]));
	}
	
}

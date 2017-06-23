package com.coalesce.coperms.commands;

import com.coalesce.command.CoCommand;
import com.coalesce.command.CommandBuilder;
import com.coalesce.command.CommandContext;
import com.coalesce.command.tabcomplete.TabContext;
import com.coalesce.coperms.CoPerms;
import com.coalesce.coperms.DataHolder;
import com.coalesce.coperms.data.CoUser;
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
				.aliases("copro", "promo")
				.description("Promotes someone to the next rank.")
				.usage("/promote <user> [world]")
				.minArgs(1)
				.maxArgs(2)
				.permission("coperms.promote")
				.build();
		
		CoCommand setrank = new CommandBuilder(plugin, "setrank")
				.executor(this::setRank)
				.completer(this::setRankTab)
				.aliases("setr", "setgroup")
				.description("Adds a user to a specified rank")
				.usage("/setrank <user> <rank> [world]") //Make a list in the UserDataFile for each user that is similar to the group data file that specifies ranks per world
				.minArgs(2)
				.maxArgs(3)
				.permission("coperms.setrank")
				.build();
		
		plugin.addCommand(promote, setrank);
	}
	
	private void promote(CommandContext context) {
	
	}
	
	private void promoteTab(TabContext context) {
	
	}
	
	private void setRank(CommandContext context) {
		
		//Checks if the user exists
		if (holder.getUser(context.argAt(0)) == null) {
			context.pluginMessage(RED + "The player specified does not exist.");
			return;
		}
		
		//We know the user exists, lets get the user
		CoUser user = holder.getUser(context.argAt(0));
		
		//World was supplied in the command.
		if (context.getArgs().size() == 3) {
			
			if (holder.getWorld(context.argAt(2)) == null) {
				context.pluginMessage(RED + "The world specified does not exist.");
				return;
			}
			
			//Checking for the group in the specified world
			if (holder.getWorld(context.argAt(2)).getGroup(context.argAt(1)) == null) {
				context.pluginMessage(RED + "Group does not exist in the world specified.");
				return;
			}
			
			//Group exists, lets add the player to it.
			context.pluginMessage("" +
					BLUE + user.getName() +
					RESET + GRAY + " was added to group " +
					BLUE + context.argAt(1) +
					RESET + GRAY + " in world " +
					BLUE + context.argAt(2));
			user.setGroup(holder.getWorld(context.argAt(2)), context.argAt(1));
			return;
		}
		
		//World not supplied in the command
		if (user.getWorld().getGroup(context.argAt(1)) == null) { //Checks if the group was a valid group
			context.pluginMessage(RED + "Group does not exist in the users world.");
			return;
		}
		user.setGroup(user.getWorld(), context.argAt(1));
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

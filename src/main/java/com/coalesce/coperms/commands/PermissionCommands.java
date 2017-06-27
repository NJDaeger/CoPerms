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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.bukkit.ChatColor.*;

public final class PermissionCommands {
	
	private final DataHolder holder;
	
	public PermissionCommands(CoPerms plugin, DataHolder holder) {
		this.holder = holder;
		
		CoCommand adduperm = new CommandBuilder(plugin, "adduperm")
				.executor(this::addUserPermission)
				.aliases("adduserperm")
				.description("Adds a permission to a user")
				.usage("/adduperm <user> w:[world] <permission> [permission]...")
				.minArgs(2)
				.permission("coperms.permissions.user.add")
				.build();
		
		CoCommand remuperm = new CommandBuilder(plugin, "remuperm")
				.executor(this::removeUserPermission)
				.aliases("remuserperm")
				.description("Removes a permission from a user")
				.usage("/remuperm <user> w:[world] <permission> [permission]...")
				.minArgs(2)
				.permission("coperms.permission.user.remove")
				.build();
		
		CoCommand addgperm = new CommandBuilder(plugin, "addgperm")
				.executor(this::addGroupPermission)
				.aliases("addgroupperm")
				.description("Adds a permission to a group")
				.usage("/addgperm <group> w:[world] <permission> [permission]...")
				.minArgs(2)
				.permission("coperms.permission.group.add")
				.build();
		
		CoCommand remgperm = new CommandBuilder(plugin, "remgperm")
				.executor(this::removeGroupPermission)
				.aliases("remgroupperm")
				.description("Removes a permission from a group")
				.usage("/remgperm <group> w:[world] <permission> [permission]...")
				.minArgs(2)
				.permission("coperms.permission.group.remove")
				.build();
		
		CoCommand getuperms = new CommandBuilder(plugin, "getuperms")
				.executor(this::getUserPermissions)
				.completer(this::getUPermsTab)
				.aliases("userperms")
				.description("Shows a list of user permissions")
				.usage("/getuperms <user>")
				.minArgs(1)
				.maxArgs(1)
				.permission("coperms.permission.user.see")
				.build();
		
		CoCommand getgperms = new CommandBuilder(plugin, "getgperms")
				.executor(this::getGroupPermissions)
				.completer(this::getGPermsTab)
				.aliases("groupperms")
				.description("Shows a list of group permissions")
				.usage("/getgperms <group>")
				.minArgs(1)
				.maxArgs(1)
				.permission("coperms.permission.group.see")
				.build();
		
		plugin.addCommand(getuperms, getgperms, adduperm, addgperm, remuperm, remgperm);
		
	}
	
	//
	//
	//
	//
	
	private void addUserPermission(CommandContext context) {
		CoWorld world = context.argAt(1).startsWith("w:") ? holder.getWorld(context.argAt(1).substring(2)) : holder.getDefaultWorld();
		if (world == null) {
		
		}
		CoUser user = holder.getUser(context.argAt(0)) == null ? holder.getUser(world.getName(), context.argAt(0)) : holder.getUser(context.argAt(0));
		if (user == null) {
		
		}
		Set<String> unable = new HashSet<>();
		for (int i = 0; context.getArgs().size() > i; i++) {
			if (i < (context.argAt(1).startsWith("w:") ? 2 : 1)) continue;
			if (!user.addPermission(context.argAt(i))) unable.add(context.argAt(i));
		}
		context.pluginMessage(GRAY + "The following permissions were added to user " + DARK_AQUA + user.getName() + GRAY + ": " + WHITE + formatPerms(context.getArgs().subList((context.argAt(1).startsWith("w:") ? 2 : 1), context.getArgs().size())));
		if (unable.size() > 0) context.pluginMessage(GRAY + "Some permissions could not be added to user " + DARK_AQUA + user.getName() + GRAY + ": " + WHITE + formatPerms(unable));
	}
	
	//
	//
	//
	//
	
	private void removeUserPermission(CommandContext context) {
		CoWorld world = context.argAt(1).startsWith("w:") ? holder.getWorld(context.argAt(1).substring(2)) : holder.getDefaultWorld();
		if (world == null) {
		
		}
		CoUser user = holder.getUser(context.argAt(0)) == null ? holder.getUser(world.getName(), context.argAt(0)) : holder.getUser(context.argAt(0));
		if (user == null) {
		
		}
		Set<String> unable = new HashSet<>();
		for (int i = 0; context.getArgs().size() > i; i++) {
			if (i < 2) continue;
			if (!user.removePermission(context.argAt(i))) unable.add(context.argAt(i));
		}
		context.pluginMessage(GRAY + "The following permissions were removed from user " + DARK_AQUA + user.getName() + GRAY + ": " + WHITE + formatPerms(context.getArgs().subList(2, context.getArgs().size())));
		context.pluginMessage(GRAY + "Some permissions could not be removed from user " + DARK_AQUA + user.getName() + GRAY + ": " + WHITE + formatPerms(unable));
	}
	
	//
	//
	//
	//
	
	private void addGroupPermission(CommandContext context) {
	
	}
	
	//
	//
	//
	//
	
	private void removeGroupPermission(CommandContext context) {
	
	}
	
	//
	//
	//
	//
	
	private void getUserPermissions(CommandContext context) {
		CoUser user = holder.getUser(context.argAt(0)) == null ? holder.getUser(holder.getDefaultWorld().getName(), context.argAt(0)) : holder.getUser(context.argAt(0));
		if (user == null) {
			context.pluginMessage(RED + "The user specified does not exist in the world specified");
			return;
		}
		context.pluginMessage(GRAY + "All permissions for user " + DARK_AQUA + user.getName() + GRAY + ": " + WHITE + formatPerms(user.getPermissions()));
	}
	
	private void getUPermsTab(TabContext context) {
		context.playerCompletion(0);
	}
	
	//
	//
	//
	//
	
	private void getGroupPermissions(CommandContext context) {
		Group group = holder.getGroup(context.argAt(0).toLowerCase());
		if (group == null) {
			context.pluginMessage(RED + "The group specified does not exist");
			return;
		}
		context.pluginMessage(GRAY + "All permissions for group " + DARK_AQUA + group.getName() + GRAY + ": " + WHITE + formatPerms(group.getPermissions()));
	}
	
	private void getGPermsTab(TabContext context) {
		context.completionAt(0, holder.getGroups().keySet().toArray(new String[holder.getGroups().size()]));
	}
	
	private String formatPerms(Collection<String> permissions) {
		StringBuilder builder = new StringBuilder();
		String[] message = permissions.toArray(new String[permissions.size()]);
		for (int i = 0; i < message.length; i++) {
			if (message[i].startsWith("-") && message[i].endsWith(".*")) {
				builder.append("" + GRAY + ITALIC + UNDERLINE + message[i]).append(RESET + ", ");
				continue;
			}
			if (message[i].startsWith("-")) {
				builder.append("" + GRAY +ITALIC + message[i]).append(RESET + ", ");
				continue;
			}
			if (message[i].endsWith(".*")) {
				builder.append("" + GRAY + UNDERLINE + message[i]).append(RESET + ", ");
				continue;
			}
			builder.append(message[i]).append(RESET + ", ");
		}
		return builder.toString().trim();
	}
	
}

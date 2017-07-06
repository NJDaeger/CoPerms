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
				.completer(this::userPermissionTab)
				.aliases("adduserperm")
				.description("Adds a permission to a user")
				.usage("/adduperm <user> w:[world] <permission> [permission]...")
				.minArgs(2)
				.permission("coperms.permissions.user.add")
				.build();
		
		CoCommand remuperm = new CommandBuilder(plugin, "remuperm")
				.executor(this::removeUserPermission)
				.completer(this::userPermissionTab)
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
		
		CoCommand test = new CommandBuilder(plugin, "test")
				.executor(this::testCommand)
				.build();
		
		plugin.addCommand(getuperms, getgperms, test, adduperm, /*addgperm,*/ remuperm /* remgperm*/);
		
	}
	
	//
	//
	//
	//
	
	private void addUserPermission(CommandContext context) {
		CoWorld world = context.argAt(1).startsWith("w:") ? holder.getWorld(context.argAt(1).substring(2)) : holder.getDefaultWorld();
		if (world == null) {
			context.pluginMessage(RED + "The world specified does not exist.");
			return;
		}
		CoUser user = holder.getUser(context.argAt(0)) == null ? holder.getUser(world.getName(), context.argAt(0)) : holder.getUser(context.argAt(0));
		if (user == null) {
			context.pluginMessage(RED + "The user specified does not exist in the world specified");
			return;
		}
		Set<String> unable = new HashSet<>();
		Set<String> added = new HashSet<>();
		for (int i = 0; context.getArgs().size() > i; i++) {
			if (i < (context.argAt(1).startsWith("w:") ? 2 : 1)) continue;
			if (!user.addPermission(context.argAt(i))) {
				unable.add(context.argAt(i));
			}
			else {
				added.add(context.argAt(i));
			}
		}
		if (!added.isEmpty()) context.pluginMessage(GRAY + "The following permission(s) was added to user " + DARK_AQUA + user.getName() + GRAY + ": " + WHITE + formatPerms(added));
		if (!unable.isEmpty()) context.pluginMessage(GRAY + "Some permissions could not be added to user " + DARK_AQUA + user.getName() + GRAY + ": " + WHITE + formatPerms(unable));
	}
	
	//
	//
	//
	//
	
	private void removeUserPermission(CommandContext context) {
		CoWorld world = context.argAt(1).startsWith("w:") ? holder.getWorld(context.argAt(1).substring(2)) : holder.getDefaultWorld();
		if (world == null) {
			context.pluginMessage(RED + "The world specified does not exist.");
			return;
		}
		CoUser user = holder.getUser(context.argAt(0)) == null ? holder.getUser(world.getName(), context.argAt(0)) : holder.getUser(context.argAt(0));
		if (user == null) {
			context.pluginMessage(RED + "The user specified does not exist in the world specified");
			return;
		}
		Set<String> unable = new HashSet<>();
		Set<String> removed = new HashSet<>();
		for (int i = 0; context.getArgs().size() > i; i++) {
			if (i < (context.argAt(1).startsWith("w:") ? 2 : 1)) continue;
			if (!user.removePermission(context.argAt(i))) {
				unable.add(context.argAt(i));
			}
			else {
				removed.add(context.argAt(i));
			}
		}
		if (!removed.isEmpty()) context.pluginMessage(GRAY + "The following permission(s) was removed from user " + DARK_AQUA + user.getName() + GRAY + ": " + WHITE + formatPerms(removed));
		if (!unable.isEmpty()) context.pluginMessage(GRAY + "Some permissions could not be removed from user " + DARK_AQUA + user.getName() + GRAY + ": " + WHITE + formatPerms(unable));
	}
	
	private void userPermissionTab(TabContext context) {
		Set<String> worlds = new HashSet<>();
		holder.getWorlds().keySet().forEach(w -> worlds.add("w:"+ w));
		context.playerCompletion(0);
		context.completionAt(1, worlds.toArray(new String[worlds.size()]));
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
		if (permissions == null || permissions.isEmpty()) return null;
		String[] message = permissions.toArray(new String[permissions.size()]);
		String comma = ""  +RESET + WHITE + ", ";
		for (String node : message) {
			if (node == null) continue;
			if (node.startsWith("-") && node.endsWith(".*")) {
				builder.append(GRAY).append(ITALIC).append(UNDERLINE).append(node).append(comma);
				continue;
			}
			if (node.startsWith("-")) {
				builder.append(GRAY).append(ITALIC).append(node).append(comma);
				continue;
			}
			if (node.endsWith(".*")) {
				builder.append(GRAY).append(UNDERLINE).append(node).append(comma);
				continue;
			}
			builder.append(node).append(comma);
		}
		String s = builder.toString().trim();
		return s.substring(0, (s.endsWith(",") ? s.lastIndexOf(",") : s.length()));
	}
	
	private void testCommand(CommandContext context) {
		CoUser user = holder.getUser(context.argAt(0));
		System.out.println(user.getName());
		user.getUserPermissions().forEach(System.out::println);
	}
	
}

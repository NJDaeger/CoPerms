package com.coalesce.coperms.commands;

import com.coalesce.command.CoCommand;
import com.coalesce.command.CommandBuilder;
import com.coalesce.command.CommandContext;
import com.coalesce.coperms.CoPerms;
import com.coalesce.coperms.DataHolder;
import com.coalesce.coperms.data.CoUser;
import com.coalesce.coperms.data.Group;

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
				.aliases("userperms")
				.description("Shows a list of user permissions")
				.usage("/getuperms <user>")
				.minArgs(1)
				.maxArgs(1)
				.permission("coperms.permission.user.see")
				.build();
		
		CoCommand getgperms = new CommandBuilder(plugin, "getgperms")
				.executor(this::getGroupPermissions)
				.aliases("groupperms")
				.description("Shows a list of group permissions")
				.usage("/getgperms <group>")
				.minArgs(1)
				.maxArgs(1)
				.permission("coperms.permission.group.see")
				.build();
		
	}
	
	private void addUserPermission(CommandContext context) {
	
	}
	
	private void removeUserPermission(CommandContext context) {
	
	}
	
	private void addGroupPermission(CommandContext context) {
	
	}
	
	private void removeGroupPermission(CommandContext context) {
	
	}
	
	private void getUserPermissions(CommandContext context) {
		CoUser user = holder.getUser(context.argAt(0)) == null ? holder.getUser(holder.getDefaultWorld().getName(), context.argAt(0)) : holder.getUser(context.argAt(0));
		if (user == null) {
			context.pluginMessage(RED + "The user specified does not exist in the world specified");
			return;
		}
		StringBuilder builder = new StringBuilder();
		String[] message = user.getPermissions().toArray(new String[user.getPermissions().size()]);
		for (int i = 0; i < message.length; i++) {
			builder.append(message[i]).append(" ");
		}
		String msg = builder.toString().trim();
		context.pluginMessage(GRAY + "All permissions for user " + DARK_AQUA + user.getName() + GRAY + ": " + WHITE + msg);
	}
	
	private void getGroupPermissions(CommandContext context) {
		Group group;
	}
	
}

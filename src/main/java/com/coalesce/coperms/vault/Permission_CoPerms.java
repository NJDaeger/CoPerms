package com.coalesce.coperms.vault;

import com.coalesce.coperms.CoPerms;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.Set;

public final class Permission_CoPerms extends Permission {
	
	private CoPerms coperms;
	
	public Permission_CoPerms(Plugin plugin) {
		this.plugin = plugin;
		Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(this), plugin);
	}
	
	public class PermissionServerListener implements Listener {
		
		Permission_CoPerms permission = null;
		
		public PermissionServerListener(Permission_CoPerms permission) {
			this.permission = permission;
		}
		
		@EventHandler( priority = EventPriority.MONITOR)
		public void onEnable(PluginEnableEvent e) {
			if (permission.coperms == null) {
				Plugin p = e.getPlugin();
				if (p.getDescription().getName().equalsIgnoreCase("CoPerms")) {
					permission.coperms = (CoPerms) p;
					System.out.println("[Vault][Permission] CoPerms hooked.");
				}
			}
		}
		
		public void onDisable(PluginDisableEvent e) {
			if (permission.coperms != null) {
				if (e.getPlugin().getDescription().getName().equalsIgnoreCase("CoPerms")) {
					permission.coperms = null;
					System.out.println("[Vault][Permission] CoPerms un-hooked.");
				}
			}
		}
		
	}
	
	@Override
	public String getName() {
		return "CoPerms";
	}
	
	@Override
	public boolean isEnabled() {
		return coperms != null && coperms.isEnabled();
	}
	
	@Override
	public boolean hasSuperPermsCompat() {
		return true;
	}
	
	@Override
	public boolean playerHas(String world, String player, String permission) {
		return coperms.getDataHolder().getWorld(world).getUser(player).hasPermission(permission);
	}
	
	@Override
	public boolean playerAdd(String world, String player, String permission) {
		return coperms.getDataHolder().getWorld(world).getUser(player).addPermission(permission);
	}
	
	@Override
	public boolean playerRemove(String world, String player, String permission) {
		return coperms.getDataHolder().getWorld(world).getUser(player).removePermission(permission);
	}
	
	@Override
	public boolean groupHas(String world, String group, String permission) {
		return coperms.getDataHolder().getWorld(world).getGroup(group).hasPermission(permission);
	}
	
	@Override
	public boolean groupAdd(String world, String group, String permission) {
		return coperms.getDataHolder().getWorld(world).getGroup(group).addPermission(permission);
	}
	
	@Override
	public boolean groupRemove(String world, String group, String permission) {
		return coperms.getDataHolder().getWorld(world).getGroup(group).removePermission(permission);
	}
	
	@Override
	public boolean playerInGroup(String world, String player, String group) {
		return coperms.getDataHolder().getWorld(world).getUser(player).getGroup().getName().equalsIgnoreCase(group);
	}
	
	@Override
	public boolean playerAddGroup(String world, String player, String group) {
		return coperms.getDataHolder().getWorld(world).getUser(player).setGroup(coperms.getDataHolder().getWorld(world), group);
	}
	
	@Override
	public boolean playerRemoveGroup(String world, String player, String group) {
		return coperms.getDataHolder().getWorld(world).getUser(player).setGroup(coperms.getDataHolder().getWorld(world), group);
	}
	
	@Override
	public String[] getPlayerGroups(String world, String player) {
		Set<String> groups = new HashSet<>();
		coperms.getDataHolder().getWorld(world).getUser(player).getGroups().forEach(g -> groups.add(g.getName()));
		return groups.toArray(new String[groups.size()]);
	}
	
	@Override
	public String getPrimaryGroup(String world, String player) {
		return coperms.getDataHolder().getWorld(world).getUser(player).getGroup().getName();
	}
	
	@Override
	public String[] getGroups() {
		Set<String> groups = new HashSet<>();
		coperms.getDataHolder().getGroups().forEach((n, g) -> groups.add(n));
		return groups.toArray(new String[groups.size()]);
	}
	
	@Override
	public boolean hasGroupSupport() {
		return true;
	}
}

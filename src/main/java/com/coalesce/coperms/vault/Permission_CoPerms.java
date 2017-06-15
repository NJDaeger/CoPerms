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
					log.info(String.format("[%s][Permission] %s hooked.", "Vault", permission.getName()));
				}
			}
		}
		
		public void onDisable(PluginDisableEvent e) {
			if (permission.coperms != null) {
				if (e.getPlugin().getDescription().getName().equalsIgnoreCase("CoPerms")) {
					permission.coperms = null;
					log.info(String.format("[%s][Permission] %s un-hooked.", "Vault", permission.getName()));
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
		return false;
	}
	
	@Override
	public boolean playerHas(String s, String s1, String s2) {
		return false;
	}
	
	@Override
	public boolean playerAdd(String s, String s1, String s2) {
		return false;
	}
	
	@Override
	public boolean playerRemove(String s, String s1, String s2) {
		return false;
	}
	
	@Override
	public boolean groupHas(String s, String s1, String s2) {
		return false;
	}
	
	@Override
	public boolean groupAdd(String s, String s1, String s2) {
		return false;
	}
	
	@Override
	public boolean groupRemove(String s, String s1, String s2) {
		return false;
	}
	
	@Override
	public boolean playerInGroup(String s, String s1, String s2) {
		return false;
	}
	
	@Override
	public boolean playerAddGroup(String s, String s1, String s2) {
		return false;
	}
	
	@Override
	public boolean playerRemoveGroup(String s, String s1, String s2) {
		return false;
	}
	
	@Override
	public String[] getPlayerGroups(String s, String s1) {
		return new String[0];
	}
	
	@Override
	public String getPrimaryGroup(String s, String s1) {
		return null;
	}
	
	@Override
	public String[] getGroups() {
		return new String[0];
	}
	
	@Override
	public boolean hasGroupSupport() {
		return true;
	}
}

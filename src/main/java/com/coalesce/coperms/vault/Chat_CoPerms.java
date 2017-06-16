package com.coalesce.coperms.vault;

import com.coalesce.coperms.CoPerms;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

public final class Chat_CoPerms extends Chat {
	
	private Plugin plugin = null;
	private CoPerms coperms;
	
	public Chat_CoPerms(Plugin plugin, Permission perms) {
		super(perms);
		this.plugin = plugin;
		Bukkit.getServer().getPluginManager().registerEvents(new PermissionServiceListener(this), plugin);
	}
	
	public class PermissionServiceListener implements Listener {
		
		Chat_CoPerms chat = null;
		
		public PermissionServiceListener(Chat_CoPerms chat) {
			this.chat = chat;
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onEnable(PluginEnableEvent e) {
			if (chat.coperms == null) {
				Plugin p = e.getPlugin();
				if (p.getDescription().getName().equalsIgnoreCase("CoPerms")) {
					chat.coperms = (CoPerms) p;
					System.out.println("[Vault][Chat] CoPerms hooked.");
				}
			}
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onDisable(PluginDisableEvent e) {
			if (chat.coperms != null) {
				if (e.getPlugin().getDescription().getName().equalsIgnoreCase("CoPerms")) {
					System.out.println("[Vault][Chat] CoPerms un-hooked.");
					chat.coperms = null;
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
		if (coperms == null) {
			return false;
		}
		else return coperms.isEnabled();
	}
	
	@Override
	public String getPlayerPrefix(String world, String player) {
		return null;
	}
	
	@Override
	public void setPlayerPrefix(String world, String player, String prefix) {
	
	}
	
	@Override
	public String getPlayerSuffix(String world, String player) {
		return null;
	}
	
	@Override
	public void setPlayerSuffix(String world, String player, String suffix) {
	
	}
	
	@Override
	public String getGroupPrefix(String world, String group) {
		return null;
	}
	
	@Override
	public void setGroupPrefix(String world, String group, String prefix) {
	
	}
	
	@Override
	public String getGroupSuffix(String world, String group) {
		return null;
	}
	
	@Override
	public void setGroupSuffix(String world, String group, String suffix) {
	
	}
	
	@Override
	public int getPlayerInfoInteger(String world, String player, String node, int defVal) {
		return 0;
	}
	
	@Override
	public void setPlayerInfoInteger(String world, String player, String node, int val) {
	
	}
	
	@Override
	public int getGroupInfoInteger(String world, String group, String node, int defVal) {
		return 0;
	}
	
	@Override
	public void setGroupInfoInteger(String world, String group, String node, int value) {
	
	}
	
	@Override
	public double getPlayerInfoDouble(String world, String player, String node, double defVal) {
		return 0;
	}
	
	@Override
	public void setPlayerInfoDouble(String world, String player, String node, double value) {
	
	}
	
	@Override
	public double getGroupInfoDouble(String world, String group, String node, double defVal) {
		return 0;
	}
	
	@Override
	public void setGroupInfoDouble(String world, String group, String node, double value) {
	
	}
	
	@Override
	public boolean getPlayerInfoBoolean(String world, String player, String node, boolean defVal) {
		return false;
	}
	
	@Override
	public void setPlayerInfoBoolean(String world, String player, String node, boolean value) {
	
	}
	
	@Override
	public boolean getGroupInfoBoolean(String world, String group, String node, boolean defVal) {
		return false;
	}
	
	@Override
	public void setGroupInfoBoolean(String world, String group, String node, boolean value) {
	
	}
	
	@Override
	public String getPlayerInfoString(String world, String player, String node, String defVal) {
		return null;
	}
	
	@Override
	public void setPlayerInfoString(String world, String player, String node, String value) {
	
	}
	
	@Override
	public String getGroupInfoString(String world, String group, String node, String defVal) {
		return null;
	}
	
	@Override
	public void setGroupInfoString(String world, String group, String node, String value) {
	
	}
}

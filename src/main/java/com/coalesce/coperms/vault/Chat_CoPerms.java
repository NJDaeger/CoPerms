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
					plugin.getLogger().info(String.format("[%s][Chat] %s hooked.", "Vault", chat.getName()));
				}
			}
		}
		
		@EventHandler(priority = EventPriority.MONITOR)
		public void onDisable(PluginDisableEvent e) {
			if (chat.coperms != null) {
				if (e.getPlugin().getDescription().getName().equalsIgnoreCase("CoPerms")) {
					chat.coperms = null;
					plugin.getLogger().info(String.format("[%s][Chat] %s un-hooked.", "Vault", chat.getName()));
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
	public void setPlayerPrefix(String s, String s1, String s2) {
	
	}
	
	@Override
	public String getPlayerSuffix(String s, String s1) {
		return null;
	}
	
	@Override
	public void setPlayerSuffix(String s, String s1, String s2) {
	
	}
	
	@Override
	public String getGroupPrefix(String s, String s1) {
		return null;
	}
	
	@Override
	public void setGroupPrefix(String s, String s1, String s2) {
	
	}
	
	@Override
	public String getGroupSuffix(String s, String s1) {
		return null;
	}
	
	@Override
	public void setGroupSuffix(String s, String s1, String s2) {
	
	}
	
	@Override
	public int getPlayerInfoInteger(String s, String s1, String s2, int i) {
		return 0;
	}
	
	@Override
	public void setPlayerInfoInteger(String s, String s1, String s2, int i) {
	
	}
	
	@Override
	public int getGroupInfoInteger(String s, String s1, String s2, int i) {
		return 0;
	}
	
	@Override
	public void setGroupInfoInteger(String s, String s1, String s2, int i) {
	
	}
	
	@Override
	public double getPlayerInfoDouble(String s, String s1, String s2, double v) {
		return 0;
	}
	
	@Override
	public void setPlayerInfoDouble(String s, String s1, String s2, double v) {
	
	}
	
	@Override
	public double getGroupInfoDouble(String s, String s1, String s2, double v) {
		return 0;
	}
	
	@Override
	public void setGroupInfoDouble(String s, String s1, String s2, double v) {
	
	}
	
	@Override
	public boolean getPlayerInfoBoolean(String s, String s1, String s2, boolean b) {
		return false;
	}
	
	@Override
	public void setPlayerInfoBoolean(String s, String s1, String s2, boolean b) {
	
	}
	
	@Override
	public boolean getGroupInfoBoolean(String s, String s1, String s2, boolean b) {
		return false;
	}
	
	@Override
	public void setGroupInfoBoolean(String s, String s1, String s2, boolean b) {
	
	}
	
	@Override
	public String getPlayerInfoString(String s, String s1, String s2, String s3) {
		return null;
	}
	
	@Override
	public void setPlayerInfoString(String s, String s1, String s2, String s3) {
	
	}
	
	@Override
	public String getGroupInfoString(String s, String s1, String s2, String s3) {
		return null;
	}
	
	@Override
	public void setGroupInfoString(String s, String s1, String s2, String s3) {
	
	}
}

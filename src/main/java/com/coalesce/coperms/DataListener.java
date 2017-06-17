package com.coalesce.coperms;

import com.coalesce.plugin.CoModule;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class DataListener extends CoModule implements Listener {

	private final CoPerms plugin;
	private final DataHolder holder;
	
	public DataListener(DataHolder holder, CoPerms plugin) {
		super(plugin, "Data Listener");
		this.holder = holder;
		this.plugin = plugin;
		
		plugin.registerListener(this);
	}
	
	@Override
	protected void onEnable() throws Exception {
		plugin.registerListener(this);
	}
	
	@Override
	protected void onDisable() throws Exception {
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
	
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
	
	}
	
	@EventHandler
	public void onTeleport(PlayerTeleportEvent e) {
	
	}
	
	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent e) {
	
	}
}

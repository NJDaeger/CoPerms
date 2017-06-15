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
		holder.loadUser(e.getPlayer().getUniqueId());
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		holder.unloadUser(e.getPlayer().getUniqueId());
	}
	
	@EventHandler
	public void onTeleport(PlayerTeleportEvent e) {
		if (!e.getFrom().getWorld().equals(e.getTo()) && e.getFrom() != null) {
			holder.getWorld(e.getFrom().getWorld()).unloadUser(e.getPlayer().getUniqueId());
			holder.getWorld(e.getTo().getWorld()).loadUser(e.getPlayer().getUniqueId());
		}
	}
	
	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent e) {
		holder.getWorld(e.getFrom()).unloadUser(e.getPlayer().getUniqueId());
		holder.getWorld(e.getPlayer().getWorld()).loadUser(e.getPlayer().getUniqueId());
	}
}

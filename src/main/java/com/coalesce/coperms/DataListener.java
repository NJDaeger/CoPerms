package com.coalesce.coperms;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public final class DataListener implements Listener {

    private final CoPerms plugin;
    private final DataHolder holder;

    public DataListener(DataHolder holder, CoPerms plugin) {
        this.holder = holder;
        this.plugin = plugin;
    
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        holder.loadUser(e.getPlayer().getWorld(), e.getPlayer().getUniqueId());
        new Inject(e.getPlayer());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        holder.unloadUser(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        if (e.getFrom().getWorld() != e.getTo().getWorld()) {
            holder.getWorld(e.getFrom().getWorld()).unloadUser(holder.getUser(e.getPlayer().getUniqueId()));
            holder.getWorld(e.getTo().getWorld()).loadUser(holder.getUser(e.getPlayer().getUniqueId()));
        }
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent e) {
        holder.getWorld(e.getFrom()).unloadUser(holder.getUser(e.getPlayer().getUniqueId()));
        holder.getWorld(e.getPlayer().getWorld()).loadUser(holder.getUser(e.getPlayer().getUniqueId()));
    }
}

package com.njdaeger.coperms;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

@SuppressWarnings("unused")
public final class DataListener implements Listener {
    
    private final DataHolder holder;

    DataListener(DataHolder holder, CoPerms plugin) {
        this.holder = holder;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        holder.loadUser(e.getPlayer().getWorld(), e.getPlayer().getUniqueId());
        Injector.inject(e.getPlayer());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        holder.unloadUser(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        if (e.getFrom().getWorld() != e.getTo().getWorld()) {
            holder.getWorld(e.getFrom().getWorld()).removeUser(holder.getUser(e.getPlayer().getUniqueId()));
            holder.getWorld(e.getTo().getWorld()).addUser(holder.getUser(e.getPlayer().getUniqueId()));
        }
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent e) {
        holder.getWorld(e.getFrom()).removeUser(holder.getUser(e.getPlayer().getUniqueId()));
        holder.getWorld(e.getPlayer().getWorld()).addUser(holder.getUser(e.getPlayer().getUniqueId()));
    }
}

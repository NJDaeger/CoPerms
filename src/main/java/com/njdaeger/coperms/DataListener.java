package com.njdaeger.coperms;

import com.njdaeger.coperms.data.CoWorld;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class DataListener implements Listener {

    private final CoPerms plugin;

    DataListener(CoPerms plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        plugin.getWorld(e.getPlayer().getWorld()).addPlayer(e.getPlayer());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        plugin.getUser(e.getPlayer().getWorld(), e.getPlayer().getUniqueId()).save();
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent e) {
        if (e.getFrom() != e.getPlayer().getWorld()) {
            CoWorld from = plugin.getWorld(e.getFrom());
            CoWorld to = plugin.getWorld(e.getPlayer().getWorld());
            if (!to.hasUser(e.getPlayer().getUniqueId())) {
                to.addPlayer(e.getPlayer());
            }
            from.getUser(e.getPlayer().getUniqueId()).save();
        }
    }
}

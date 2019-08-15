package com.njdaeger.coperms.vault;

import com.njdaeger.coperms.CoPerms;
import com.njdaeger.coperms.groups.AbstractGroup;
import com.njdaeger.coperms.groups.Group;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

@SuppressWarnings("all")
public final class Permission_CoPerms extends Permission {

    private CoPerms coperms;

    public Permission_CoPerms(Plugin plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(this), plugin);
    }

    public class PermissionServerListener implements Listener {

        Permission_CoPerms permission;

        public PermissionServerListener(Permission_CoPerms permission) {
            this.permission = permission;
        }

        @EventHandler( priority = EventPriority.MONITOR )
        public void onEnable(PluginEnableEvent e) {
            if (permission.coperms == null) {
                Plugin p = e.getPlugin();
                if (p.getDescription().getName().equalsIgnoreCase("CoPerms")) {
                    permission.coperms = (CoPerms)p;
                    log.info("[Permission] CoPerms hooked.");
                }
            }
        }

        @EventHandler( priority = EventPriority.MONITOR )
        public void onDisable(PluginDisableEvent e) {
            if (permission.coperms != null) {
                if (e.getPlugin().getDescription().getName().equalsIgnoreCase("CoPerms")) {
                    permission.coperms = null;
                    log.info("[Permission] CoPerms unhooked.");
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
        return coperms.getUser(world, player).hasPermission(permission);
    }

    @Override
    public boolean playerAdd(String world, String player, String permission) {
        return coperms.getUser(world, player).addPermission(permission);
    }

    @Override
    public boolean playerRemove(String world, String player, String permission) {
        return coperms.getUser(world, player).removePermission(permission);
    }

    @Override
    public boolean groupHas(String world, String group, String permission) {
        return coperms.getGroup(world, group).hasPermission(permission);
    }

    @Override
    public boolean groupAdd(String world, String group, String permission) {
        return coperms.getGroup(world, group).addPermission(permission);
    }

    @Override
    public boolean groupRemove(String world, String group, String permission) {
        return coperms.getGroup(world, group).removePermission(permission);
    }

    @Override
    public boolean playerInGroup(String world, String player, String group) {
        return coperms.getWorld(world).getUser(player).getGroup().getName().equalsIgnoreCase(group);
    }

    @Override
    public boolean playerAddGroup(String world, String player, String group) {
        return coperms.getUser(world, player).setGroup(coperms.getWorld(world), group);
    }

    @Override
    public boolean playerRemoveGroup(String world, String player, String group) {
        return coperms.getUser(world, player).setGroup(coperms.getWorld(world), group);
    }

    @Override
    public String[] getPlayerGroups(String world, String player) {
        return coperms.getUser(world, player).getGroup().getInheritedGroups().stream().map(AbstractGroup::getName).toArray(String[]::new);
    }

    @Override
    public String getPrimaryGroup(String world, String player) {
        return coperms.getUser(world, player).getGroup().getName();
    }

    @Override
    public String[] getGroups() {
        return coperms.getWorlds().values().stream().flatMap(world -> world.getGroups().values().stream()).map(Group::getName).toArray(String[]::new);
    }

    @Override
    public boolean hasGroupSupport() {
        return true;
    }
}

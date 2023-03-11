package com.njdaeger.coperms.vault;

import com.njdaeger.coperms.CoPerms;
import com.njdaeger.coperms.data.CoUser;
import com.njdaeger.coperms.data.CoWorld;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Permission_CoPerms extends Permission {

    private static final String NAME = "CoPerms";
    private CoPerms coperms;

    public Permission_CoPerms(Plugin plugin) {
        Bukkit.getServer().getPluginManager().registerEvents(new PermissionServerListener(this), plugin);
    }

    public class PermissionServerListener implements Listener {

        Permission_CoPerms permission;

        public PermissionServerListener(Permission_CoPerms permission) {
            this.permission = permission;
        }

        @EventHandler( priority = EventPriority.MONITOR )
        public void onEnable(PluginEnableEvent e) {
            if (permission.coperms == null && e.getPlugin() instanceof CoPerms) {
                permission.coperms = (CoPerms)e.getPlugin();
            }
        }

        @EventHandler( priority = EventPriority.MONITOR )
        public void onDisable(PluginDisableEvent e) {
            if (permission.coperms != null && e.getPlugin() instanceof CoPerms) {
                permission.coperms = null;
            }
        }

    }

    @Override
    public String getName() {
        return NAME;
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
        if (world == null) return Bukkit.getWorlds().stream().filter(w -> coperms.getUser(w.getName(), player) != null).anyMatch(w -> coperms.getUser(w, player).hasPermission(permission));
        else if (coperms.getWorld(world) == null) return false;
        else if (coperms.getUser(world, player) == null) return false;
        else return coperms.getUser(world, player).hasPermission(permission);
    }

    @Override
    public boolean playerAdd(String world, String player, String permission) {
        if (world == null) {
            AtomicBoolean bool = new AtomicBoolean(false);
            Bukkit.getWorlds().stream().filter(w -> coperms.getUser(w, player) != null).forEach(w -> {
                boolean result = coperms.getUser(w, player).grantPermission(permission);
                if (result) bool.set(true);
            });
            return bool.get();
        }
        else if (coperms.getWorld(world) == null) return false;
        else if (coperms.getUser(world, player) == null) return false;
        else return coperms.getUser(world, player).grantPermission(permission);
    }

    @Override
    public boolean playerRemove(String world, String player, String permission) {
        if (world == null) {
            AtomicBoolean bool = new AtomicBoolean(false);
            Bukkit.getWorlds().stream().filter(w -> coperms.getUser(w, player) != null).forEach(w -> {
                boolean result = coperms.getUser(w, player).revokePermission(permission);
                if (result) bool.set(true);
            });
            return bool.get();
        }
        else if (coperms.getWorld(world) == null) return false;
        else if (coperms.getUser(world, player) == null) return false;
        else return coperms.getUser(world, player).revokePermission(permission);
    }

    @Override
    public boolean groupHas(String world, String group, String permission) {
        if (world == null) return Bukkit.getWorlds().stream().filter(w -> coperms.getGroup(w.getName(), group) != null).anyMatch(w -> coperms.getGroup(w, group).hasPermission(permission));
        else if (coperms.getWorld(world) == null) return false;
        else if (coperms.getGroup(world, group) == null) return false;
        else return coperms.getGroup(world, group).hasPermission(permission);
    }

    @Override
    public boolean groupAdd(String world, String group, String permission) {
        if (world == null) {
            AtomicBoolean bool = new AtomicBoolean(false);
            Bukkit.getWorlds().stream().filter(w -> coperms.getGroup(w, group) != null).forEach(w -> {
                boolean result = coperms.getGroup(w, group).grantPermission(permission);
                if (result) bool.set(true);
            });
            return bool.get();
        }
        else if (coperms.getWorld(world) == null) return false;
        else if (coperms.getGroup(world, group) == null) return false;
        else return coperms.getGroup(world, group).grantPermission(permission);
    }

    @Override
    public boolean groupRemove(String world, String group, String permission) {
        if (world == null) {
            AtomicBoolean bool = new AtomicBoolean(false);
            Bukkit.getWorlds().stream().filter(w -> coperms.getGroup(w, group) != null).forEach(w -> {
                boolean result = coperms.getGroup(w, group).revokePermission(permission);
                if (result) bool.set(true);
            });
            return bool.get();
        }
        else if (coperms.getWorld(world) == null) return false;
        else if (coperms.getGroup(world, group) == null) return false;
        else return coperms.getGroup(world, group).revokePermission(permission);
    }

    @Override
    public boolean playerInGroup(String world, String player, String group) {
        if (world == null)  return Bukkit.getWorlds().stream().filter(w -> coperms.getUser(w, player) != null).anyMatch(w -> coperms.getUser(w, player).getGroup().getName().equalsIgnoreCase(group));
        else if (coperms.getWorld(world) == null) return false;
        else if (coperms.getUser(world, player) == null) return false;
        else return coperms.getUser(world, player).getGroup().getName().equalsIgnoreCase(group);
    }

    @Override
    public boolean playerAddGroup(String world, String player, String group) {
        if (world == null) {
            AtomicBoolean bool = new AtomicBoolean(false);
            Bukkit.getWorlds().stream().filter(w -> coperms.getUser(w, player) != null && coperms.getGroup(w, group) != null).forEach(w -> {
                CoWorld wld = coperms.getWorld(w);
                if (wld.getUser(player).setGroup(wld, group)) bool.set(true);
            });
            return bool.get();
        }
        else if (coperms.getWorld(world) == null) return false;
        else if (coperms.getWorld(world).getGroup(group) == null) return false;
        else if (coperms.getUser(world, player) == null) return false;
        else return coperms.getUser(world, player).setGroup(coperms.getWorld(world), group);
    }

    @Override
    public boolean playerRemoveGroup(String world, String player, String group) {
        if (world == null) {//todo: this implementation is not correct, however, I dont use this, so I am just copy pasting from the add.
            //in addition to above, I dont have the concept of being in multiple groups, so it isnt possible to remove a group without supplying a new group to replace it with.
            //no one is groupless.
            AtomicBoolean bool = new AtomicBoolean(false);
            Bukkit.getWorlds().stream().filter(w -> coperms.getUser(w, player) != null && coperms.getGroup(w, group) != null).forEach(w -> {
                CoWorld wld = coperms.getWorld(w);
                if (wld.getUser(player).setGroup(wld, group)) bool.set(true);
            });
            return bool.get();
        }
        else if (coperms.getWorld(world) == null) return false;
        else if (coperms.getWorld(world).getGroup(group) == null) return false;
        else if (coperms.getUser(world, player) == null) return false;
        else return coperms.getUser(world, player).setGroup(coperms.getWorld(world), group);
    }

    @Override
    public String[] getPlayerGroups(String world, String player) {
        if (world == null) {
            List<String> groups = new ArrayList<>();
            Bukkit.getWorlds().stream().filter(w -> coperms.getUser(w, player) != null).forEach(w -> groups.addAll(coperms.getUser(w, player).getGroup().getInheritedGroups().stream().map(AbstractGroup::getName).toList()));
            return groups.toArray(String[]::new);
        }
        else if (coperms.getWorld(world) == null) return new String[0];
        else if (coperms.getUser(world, player) == null) return new String[0];
        else return coperms.getUser(world, player).getGroup().getInheritedGroups().stream().map(AbstractGroup::getName).toArray(String[]::new);
    }

    @Override
    public String getPrimaryGroup(String world, String player) {
        if (world == null) world = Bukkit.getWorlds().get(0).getName();
        if (coperms.getUser(world, player) == null) return null;
        else return coperms.getUser(world, player).getGroup().getName();
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

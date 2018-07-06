package com.coalesce.coperms.commands;

import com.coalesce.coperms.CoPerms;
import com.coalesce.coperms.DataHolder;
import com.coalesce.coperms.data.CoUser;
import com.coalesce.coperms.data.CoWorld;
import com.coalesce.coperms.data.Group;
import com.coalesce.core.command.defaults.DefaultCContext;
import com.coalesce.core.command.defaults.DefaultProcessedCommand;
import com.coalesce.core.command.defaults.DefaultTContext;
import com.coalesce.core.i18n.DummyLang;
import javafx.util.Pair;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static com.coalesce.core.command.defaults.DefaultProcessedCommand.builder;
import static org.bukkit.ChatColor.*;

public final class PermissionCommands {

    private final DataHolder holder;

    public PermissionCommands(CoPerms plugin, DataHolder holder) {
        this.holder = holder;
        
        DefaultProcessedCommand<DummyLang> adduperm = builder(plugin,"adduperm")
                .executor(this::addUserPermission)
                .completer(this::userPermissionTab)
                .aliases("adduserperm")
                .description("Adds a permission to a user")
                .usage("/adduperm <user> w:[world] <permission> [permission]...")
                .minArgs(2)
                .permission("coperms.permissions.user.add")
                .build();
    
        DefaultProcessedCommand remuperm = builder(plugin,"remuperm")
                .executor(this::removeUserPermission)
                .completer(this::userPermissionTab)
                .aliases("remuserperm")
                .description("Removes a permission from a user")
                .usage("/remuperm <user> w:[world] <permission> [permission]...")
                .minArgs(2)
                .permission("coperms.permission.user.remove")
                .build();
    
        DefaultProcessedCommand addgperm = builder(plugin,"addgperm")
                .executor(this::addGroupPermission)
                .completer(this::groupPermissionsTab)
                .aliases("addgroupperm")
                .description("Adds a permission to a group")
                .usage("/addgperm <group> w:[world] <permission> [permission]...")
                .minArgs(2)
                .permission("coperms.permission.group.add")
                .build();
    
        DefaultProcessedCommand remgperm = builder(plugin,"remgperm")
                .executor(this::removeGroupPermission)
                .completer(this::groupPermissionsTab)
                .aliases("remgroupperm")
                .description("Removes a permission from a group")
                .usage("/remgperm <group> w:[world] <permission> [permission]...")
                .minArgs(2)
                .permission("coperms.permission.group.remove")
                .build();
    
        DefaultProcessedCommand getuperms = builder(plugin,"getuperms")
                .executor(this::getUserPermissions)
                .completer(this::getUPermsTab)
                .aliases("userperms")
                .description("Shows a list of user permissions")
                .usage("/getuperms <user>")
                .minArgs(1)
                .maxArgs(1)
                .permission("coperms.permission.user.see")
                .build();
    
        DefaultProcessedCommand getgperms = builder(plugin,"getgperms")
                .executor(this::getGroupPermissions)
                .completer(this::getGPermsTab)
                .aliases("groupperms")
                .description("Shows a list of group permissions")
                .usage("/getgperms <group>")
                .minArgs(1)
                .maxArgs(1)
                .permission("coperms.permission.group.see")
                .build();
        
        plugin.getCommandStore().registerCommands(adduperm, remuperm, addgperm, remgperm, getuperms, getgperms);
    }

    private Pair<Set<String>, Set<String>> resolveSets(DefaultCContext<DummyLang> context, Group group, CoUser user) {
        Set<String> setA = new HashSet<>();
        Set<String> setB = new HashSet<>();
        for (int i = 0; context.getArgs().size() > i; i++) {
            if (i < (context.argAt(1).startsWith("w:") ? 2 : 1)) {
                continue;
            }
            if (user == null) {
                if (group.addPermission(context.argAt(i))) {
                    setA.add(context.argAt(i));
                    continue;
                }
            } else {
                if (user.addPermission(context.argAt(i))) {
                    setA.add(context.argAt(i));
                    continue;
                }
            }
            setB.add(context.argAt(i));
        }
        return new Pair<>(setA, setB);
    }
    
    //
    //
    //
    //
    private void addUserPermission(DefaultCContext<DummyLang> context) {
        CoWorld world = context.argAt(1).startsWith("w:") ? holder.getWorld(context.argAt(1).substring(2)) : holder.getDefaultWorld();
        if (world == null) {
            context.pluginMessage(RED + "The world specified does not exist.");
            return;
        }
        CoUser user = holder.getUser(context.argAt(0)) == null ? holder.getUser(world.getName(), context.argAt(0)) : holder.getUser(context.argAt(0));
        if (user == null) {
            context.pluginMessage(RED + "The user specified does not exist in the world specified");
            return;
        }
        Pair<Set<String>, Set<String>> pair = resolveSets(context, null, user);
        if (!pair.getKey().isEmpty()) {
            context.pluginMessage(GRAY + "The following permission(s) was added to user " + DARK_AQUA + user.getName() + GRAY + ": " + WHITE + formatPerms(pair.getKey()));
        }
        if (!pair.getValue().isEmpty()) {
            context.pluginMessage(GRAY + "Some permissions could not be added to user " + DARK_AQUA + user.getName() + GRAY + ": " + WHITE + formatPerms(pair.getValue()));
        }
    }

    //
    //
    //
    //
    private void removeUserPermission(DefaultCContext<DummyLang> context) {
        CoWorld world = context.argAt(1).startsWith("w:") ? holder.getWorld(context.argAt(1).substring(2)) : holder.getDefaultWorld();
        if (world == null) {
            context.pluginMessage(RED + "The world specified does not exist.");
            return;
        }
        CoUser user = holder.getUser(context.argAt(0)) == null ? holder.getUser(world.getName(), context.argAt(0)) : holder.getUser(context.argAt(0));
        if (user == null) {
            context.pluginMessage(RED + "The user specified does not exist in the world specified");
            return;
        }
        Pair<Set<String>, Set<String>> pair = resolveSets(context, null, user);
        if (!pair.getKey().isEmpty()) {
            context.pluginMessage(GRAY + "The following permission(s) was removed from user " + DARK_AQUA + user.getName() + GRAY + ": " + WHITE + formatPerms(pair.getKey()));
        }
        if (!pair.getValue().isEmpty()) {
            context.pluginMessage(GRAY + "Some permissions could not be removed from user " + DARK_AQUA + user.getName() + GRAY + ": " + WHITE + formatPerms(pair.getValue()));
        }
    }

    private void userPermissionTab(DefaultTContext context) {
        Set<String> worlds = new HashSet<>();
        holder.getWorlds().keySet().forEach(w -> worlds.add("w:" + w));
        context.playerCompletion(0);
        context.completionAt(1, worlds.toArray(new String[0]));
    }

    //
    //
    //
    //
    private void addGroupPermission(DefaultCContext<DummyLang> context) {
        CoWorld world = context.argAt(1).startsWith("w:") ? holder.getWorld(context.argAt(1).substring(2)) : holder.getDefaultWorld();
        if (world == null) {
            context.pluginMessage(RED + "The world specified does not exist.");
            return;
        }
        Group group = world.getGroup(context.argAt(0));
        if (group == null) {
            context.pluginMessage(RED + "The group specified does not exist in the world specified");
            return;
        }
        Pair<Set<String>, Set<String>> pair = resolveSets(context, group, null);
        if (!pair.getKey().isEmpty()) {
            context.pluginMessage(GRAY + "The following permission(s) was added to group " + DARK_AQUA + group.getName() + GRAY + ": " + WHITE + formatPerms(pair.getKey()));
        }
        if (!pair.getValue().isEmpty()) {
            context.pluginMessage(GRAY + "Some permissions could not be added to group " + DARK_AQUA + group.getName() + GRAY + ": " + WHITE + formatPerms(pair.getValue()));
        }
    }

    //
    //
    //
    //
    private void removeGroupPermission(DefaultCContext<DummyLang> context) {
        CoWorld world = context.argAt(1).startsWith("w:") ? holder.getWorld(context.argAt(1).substring(2)) : holder.getDefaultWorld();
        if (world == null) {
            context.pluginMessage(RED + "The world specified does not exist.");
            return;
        }
        Group group = world.getGroup(context.argAt(0));
        if (group == null) {
            context.pluginMessage(RED + "The group specified does not exist in the world specified");
            return;
        }
        Pair<Set<String>, Set<String>> pair = resolveSets(context, group, null);
        if (!pair.getKey().isEmpty()) {
            context.pluginMessage(GRAY + "The following permission(s) was removed from group " + DARK_AQUA + group.getName() + GRAY + ": " + WHITE + formatPerms(pair.getKey()));
        }
        if (!pair.getValue().isEmpty()) {
            context.pluginMessage(GRAY + "Some permissions could not be removed from group " + DARK_AQUA + group.getName() + GRAY + ": " + WHITE + formatPerms(pair.getValue()));
        }
    }

    private void groupPermissionsTab(DefaultTContext context) {
        Set<String> groups = holder.getGroups().keySet();
        Set<String> worlds = new HashSet<>();
        holder.getWorlds().keySet().forEach(w -> worlds.add("w:" + w));
        context.completionAt(0, groups.toArray(new String[0]));
        context.completionAt(1, worlds.toArray(new String[0]));
    }

    //
    //
    //
    //

    private void getUserPermissions(DefaultCContext<DummyLang> context) {
        CoUser user = holder.getUser(context.argAt(0)) == null ? holder.getUser(holder.getDefaultWorld().getName(), context.argAt(0)) : holder.getUser(context.argAt(0));
        if (user == null) {
            context.pluginMessage(RED + "The user specified does not exist in the world specified");
            return;
        }
        context.pluginMessage(GRAY + "All permissions for user " + DARK_AQUA + user.getName() + GRAY + ": " + WHITE + formatPerms(user.getPermissions()));
    }

    private void getUPermsTab(DefaultTContext context) {
        context.playerCompletion(0);
    }

    //
    //
    //
    //
    private void getGroupPermissions(DefaultCContext<DummyLang> context) {
        Group group = holder.getGroup(context.argAt(0).toLowerCase());
        if (group == null) {
            context.pluginMessage(RED + "The group specified does not exist");
            return;
        }
        context.pluginMessage(GRAY + "All permissions for group " + DARK_AQUA + group.getName() + GRAY + ": " + WHITE + formatPerms(group.getPermissions()));
    }

    private void getGPermsTab(DefaultTContext context) {
        context.completionAt(0, holder.getGroups().keySet().toArray(new String[0]));
    }

    private String formatPerms(Collection<String> permissions) {
        StringBuilder builder = new StringBuilder();
        if (permissions == null || permissions.isEmpty()) {
            return null;
        }
        String[] message = permissions.toArray(new String[0]);
        String comma = "" + RESET + WHITE + ", ";
        for (String node : message) {
            if (node == null) {
                continue;
            }
            if (node.startsWith("-") && node.endsWith(".*")) {
                builder.append(GRAY).append(ITALIC).append(UNDERLINE).append(node).append(comma);
                continue;
            }
            if (node.startsWith("-")) {
                builder.append(GRAY).append(ITALIC).append(node).append(comma);
                continue;
            }
            if (node.endsWith(".*")) {
                builder.append(GRAY).append(UNDERLINE).append(node).append(comma);
                continue;
            }
            builder.append(node).append(comma);
        }
        String s = builder.toString().trim();
        return s.substring(0, (s.endsWith(",") ? s.lastIndexOf(",") : s.length()));
    }

}

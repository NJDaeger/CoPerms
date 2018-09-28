package com.njdaeger.coperms.commands;

import com.njdaeger.coperms.CoPerms;
import com.njdaeger.coperms.DataHolder;
import com.njdaeger.coperms.Pair;
import com.njdaeger.coperms.data.CoUser;
import com.njdaeger.coperms.data.CoWorld;
import com.njdaeger.coperms.groups.Group;
import com.njdaeger.bci.base.BCICommand;
import com.njdaeger.bci.defaults.BCIBuilder;
import com.njdaeger.bci.defaults.CommandContext;
import com.njdaeger.bci.defaults.TabContext;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.bukkit.ChatColor.*;

public final class PermissionCommands {

    private final DataHolder holder;

    public PermissionCommands(CoPerms plugin, DataHolder holder) {
        this.holder = holder;
        
        BCICommand adduperm = BCIBuilder.create("adduperm")
                .executor(this::addUserPermission)
                .completer(this::userPermissionTab)
                .aliases("adduserperm")
                .description("Adds a permission to a user")
                .usage("/adduperm <user> w:[world] <permission> [permission]...")
                .minArgs(2)
                .permissions("coperms.permissions.user.add")
                .build();
    
        BCICommand remuperm = BCIBuilder.create("remuperm")
                .executor(this::removeUserPermission)
                .completer(this::userPermissionTab)
                .aliases("remuserperm")
                .description("Removes a permission from a user")
                .usage("/remuperm <user> w:[world] <permission> [permission]...")
                .minArgs(2)
                .permissions("coperms.permission.user.remove")
                .build();
    
        BCICommand addgperm = BCIBuilder.create("addgperm")
                .executor(this::addGroupPermission)
                .completer(this::groupPermissionsTab)
                .aliases("addgroupperm")
                .description("Adds a permission to a group")
                .usage("/addgperm <group> w:[world] <permission> [permission]...")
                .minArgs(2)
                .permissions("coperms.permission.group.add")
                .build();
    
        BCICommand remgperm = BCIBuilder.create("remgperm")
                .executor(this::removeGroupPermission)
                .completer(this::groupPermissionsTab)
                .aliases("remgroupperm")
                .description("Removes a permission from a group")
                .usage("/remgperm <group> w:[world] <permission> [permission]...")
                .minArgs(2)
                .permissions("coperms.permission.group.remove")
                .build();
    
        BCICommand getuperms = BCIBuilder.create("getuperms")
                .executor(this::getUserPermissions)
                .completer(this::getUPermsTab)
                .aliases("userperms")
                .description("Shows a list of user permissions")
                .usage("/getuperms <user>")
                .minArgs(1)
                .maxArgs(1)
                .permissions("coperms.permission.user.see")
                .build();
    
        BCICommand getgperms = BCIBuilder.create("getgperms")
                .executor(this::getGroupPermissions)
                .completer(this::getGPermsTab)
                .aliases("groupperms")
                .description("Shows a list of group permissions")
                .usage("/getgperms <group>")
                .minArgs(1)
                .maxArgs(1)
                .permissions("coperms.permission.group.see")
                .build();
        
        plugin.getCommandStore().registerCommands(adduperm, remuperm, addgperm, remgperm, getuperms, getgperms);
    }

    private Pair<Set<String>, Set<String>> resolveSets(CommandContext context, Group group, CoUser user) {
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
    private void addUserPermission(CommandContext context) {
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
        if (!pair.getFirst().isEmpty()) {
            context.pluginMessage(GRAY + "The following permission(s) was added to user " + AQUA + user.getName() + GRAY + ": " + WHITE + formatPerms(pair.getFirst()));
        }
        if (!pair.getSecond().isEmpty()) {
            context.pluginMessage(GRAY + "Some permissions could not be added to user " + AQUA + user.getName() + GRAY + ": " + WHITE + formatPerms(pair.getSecond()));
        }
    }

    //
    //
    //
    //
    private void removeUserPermission(CommandContext context) {
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
        if (!pair.getFirst().isEmpty()) {
            context.pluginMessage(GRAY + "The following permission(s) was removed from user " + AQUA + user.getName() + GRAY + ": " + WHITE + formatPerms(pair.getFirst()));
        }
        if (!pair.getSecond().isEmpty()) {
            context.pluginMessage(GRAY + "Some permissions could not be removed from user " + AQUA + user.getName() + GRAY + ": " + WHITE + formatPerms(pair.getSecond()));
        }
    }

    private void userPermissionTab(TabContext context) {
        Set<String> worlds = new HashSet<>();
        holder.getWorlds().keySet().forEach(w -> worlds.add("w:" + w));
        context.playerCompletionAt(0);
        context.completionAt(1, worlds.toArray(new String[0]));
    }

    //
    //
    //
    //
    private void addGroupPermission(CommandContext context) {
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
        if (!pair.getFirst().isEmpty()) {
            context.pluginMessage(GRAY + "The following permission(s) was added to group " + AQUA + group.getName() + GRAY + ": " + WHITE + formatPerms(pair.getFirst()));
        }
        if (!pair.getSecond().isEmpty()) {
            context.pluginMessage(GRAY + "Some permissions could not be added to group " + AQUA + group.getName() + GRAY + ": " + WHITE + formatPerms(pair.getSecond()));
        }
    }

    //
    //
    //
    //
    private void removeGroupPermission(CommandContext context) {
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
        if (!pair.getFirst().isEmpty()) {
            context.pluginMessage(GRAY + "The following permission(s) was removed from group " + AQUA + group.getName() + GRAY + ": " + WHITE + formatPerms(pair.getFirst()));
        }
        if (!pair.getSecond().isEmpty()) {
            context.pluginMessage(GRAY + "Some permissions could not be removed from group " + AQUA + group.getName() + GRAY + ": " + WHITE + formatPerms(pair.getSecond()));
        }
    }

    private void groupPermissionsTab(TabContext context) {
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

    private void getUserPermissions(CommandContext context) {
        CoUser user = holder.getUser(context.argAt(0)) == null ? holder.getUser(holder.getDefaultWorld().getName(), context.argAt(0)) : holder.getUser(context.argAt(0));
        if (user == null) {
            context.pluginMessage(RED + "The user specified does not exist in the world specified");
            return;
        }
        context.pluginMessage(GRAY + "All permissions for user " + AQUA + user.getName() + GRAY + ": " + WHITE + formatPerms(user.getPermissions()));
    }

    private void getUPermsTab(TabContext context) {
        context.playerCompletionAt(0);
    }

    //
    //
    //
    //
    private void getGroupPermissions(CommandContext context) {
        Group group = holder.getGroup(context.argAt(0).toLowerCase());
        if (group == null) {
            context.pluginMessage(RED + "The group specified does not exist");
            return;
        }
        context.pluginMessage(GRAY + "All permissions for group " + AQUA + group.getName() + GRAY + ": " + WHITE + formatPerms(group.getPermissions()));
    }

    private void getGPermsTab(TabContext context) {
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

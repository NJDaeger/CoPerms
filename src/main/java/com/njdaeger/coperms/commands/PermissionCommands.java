package com.njdaeger.coperms.commands;

import com.njdaeger.bci.base.BCICommand;
import com.njdaeger.bci.base.BCIException;
import com.njdaeger.bci.defaults.BCIBuilder;
import com.njdaeger.bci.defaults.CommandContext;
import com.njdaeger.bci.defaults.TabContext;
import com.njdaeger.coperms.CoPerms;
import com.njdaeger.coperms.Pair;
import com.njdaeger.coperms.commands.flags.WorldFlag;
import com.njdaeger.coperms.data.CoUser;
import com.njdaeger.coperms.data.CoWorld;
import com.njdaeger.coperms.exceptions.GroupNotExistException;
import com.njdaeger.coperms.exceptions.UserNotExistException;
import com.njdaeger.coperms.exceptions.WorldNotExistException;
import com.njdaeger.coperms.groups.Group;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.bukkit.ChatColor.*;

public final class PermissionCommands {

    private final CoPerms plugin;

    public PermissionCommands(CoPerms plugin) {
        this.plugin = plugin;

        BCICommand adduperm = BCIBuilder.create("adduperm")
                .executor(this::addUserPermission)
                .completer(this::userPermissionTab)
                .aliases("adduserperm")
                .description("Adds a permission to a user")
                .usage("/adduperm <user> w:[world] <permission> [permission]...")
                .flag(new WorldFlag())
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
                .completer(this::groupPermissionTab)
                .aliases("addgroupperm")
                .description("Adds a permission to a group")
                .usage("/addgperm <group> w:[world] <permission> [permission]...")
                .minArgs(2)
                .permissions("coperms.permission.group.add")
                .build();
    
        BCICommand remgperm = BCIBuilder.create("remgperm")
                .executor(this::removeGroupPermission)
                .completer(this::groupPermissionTab)
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
                .usage("/getuperms <user> [world]")
                .minArgs(1)
                .maxArgs(2)
                .permissions("coperms.permission.user.see")
                .build();
    
        BCICommand getgperms = BCIBuilder.create("getgperms")
                .executor(this::getGroupPermissions)
                .completer(this::getGPermsTab)
                .aliases("groupperms")
                .description("Shows a list of group permissions")
                .usage("/getgperms <group> [world]")
                .minArgs(1)
                .maxArgs(2)
                .permissions("coperms.permission.group.see")
                .build();
        
        plugin.getCommandStore().registerCommands(adduperm, remuperm, addgperm, remgperm, getuperms, getgperms);
    }
    
    //
    //
    //
    //
    //TODO redo
    private void addUserPermission(CommandContext context) throws BCIException {
        
        CoWorld world = context.hasFlag("w") ? context.getFlag("w").getAs(CoWorld.class) : resolveWorld(context);
        if (world == null) throw new WorldNotExistException();

        CoUser user = world.getUser(context.argAt(0));
        if (user == null) throw new UserNotExistException();
    
        Pair<Set<String>, Set<String>> pair = resolveSets(context, null, user);
        if (!pair.getFirst().isEmpty()) {
            context.pluginMessage(GRAY + "The following permission(s) was added to user " + AQUA + user.getName() + GRAY + ": " + WHITE + formatPerms(pair.getFirst()));
        }
        if (!pair.getSecond().isEmpty()) {
            context.pluginMessage(GRAY + "Some permissions could not be added to user " + AQUA + user.getName() + GRAY + ": " + WHITE + formatPerms(pair.getSecond()));
        }
    }

    //TODO redo
    private void removeUserPermission(CommandContext context) throws BCIException {
        
        CoWorld world = context.hasFlag("w") ? context.getFlag("w").getAs(CoWorld.class) : resolveWorld(context);
        if (world == null) throw new WorldNotExistException();
        
        CoUser user = world.getUser(context.argAt(0));
        if (user == null) throw new UserNotExistException();
        
        Pair<Set<String>, Set<String>> pair = resolveSets(context, null, user);
        if (!pair.getFirst().isEmpty()) {
            context.pluginMessage(GRAY + "The following permission(s) was removed from user " + AQUA + user.getName() + GRAY + ": " + WHITE + formatPerms(pair.getFirst()));
        }
        if (!pair.getSecond().isEmpty()) {
            context.pluginMessage(GRAY + "Some permissions could not be removed from user " + AQUA + user.getName() + GRAY + ": " + WHITE + formatPerms(pair.getSecond()));
        }
    }
    
    private void userPermissionTab(TabContext context) {
        context.playerCompletionAt(0);
        context.completionIf(c -> context.getCurrent().startsWith("w:"), plugin.getWorlds().keySet().stream().map("w:"::concat).toArray(String[]::new));
    }

    //
    //
    //
    //
    
    private void addGroupPermission(CommandContext context) throws BCIException {
        
        CoWorld world = context.hasFlag("w") ? context.getFlag("w").getAs(CoWorld.class) : resolveWorld(context);
        if (world == null) throw new WorldNotExistException();
        
        Group group = world.getGroup(context.argAt(0));
        if (group == null) throw new GroupNotExistException();
        
        Pair<Set<String>, Set<String>> pair = resolveSets(context, group, null);
        if (!pair.getFirst().isEmpty()) {
            context.pluginMessage(GRAY + "The following permission(s) was added to group " + AQUA + group.getName() + GRAY + ": " + WHITE + formatPerms(pair.getFirst()));
        }
        if (!pair.getSecond().isEmpty()) {
            context.pluginMessage(GRAY + "Some permissions could not be added to group " + AQUA + group.getName() + GRAY + ": " + WHITE + formatPerms(pair.getSecond()));
        }
    }
    
    private void removeGroupPermission(CommandContext context) throws BCIException {
        
        CoWorld world = context.hasFlag("w") ? context.getFlag("w").getAs(CoWorld.class) : resolveWorld(context);
        if (world == null) throw new WorldNotExistException();
    
        Group group = world.getGroup(context.argAt(0));
        if (group == null) throw new GroupNotExistException();
        
        Pair<Set<String>, Set<String>> pair = resolveSets(context, group, null);
        if (!pair.getFirst().isEmpty()) {
            context.pluginMessage(GRAY + "The following permission(s) was removed from group " + AQUA + group.getName() + GRAY + ": " + WHITE + formatPerms(pair.getFirst()));
        }
        if (!pair.getSecond().isEmpty()) {
            context.pluginMessage(GRAY + "Some permissions could not be removed from group " + AQUA + group.getName() + GRAY + ": " + WHITE + formatPerms(pair.getSecond()));
        }
    }
    
    private void groupPermissionTab(TabContext context) {
        //context.completionAt(0, holder.getGroupNames().toArray(new String[0]));
        //context.completionIf(c -> context.getCurrent().startsWith("w:"), holder.getWorlds().keySet().stream().map("w:"::concat).toArray(String[]::new));
    }
    
    //
    //
    //
    //

    private void getUserPermissions(CommandContext context) throws BCIException {
    
        CoWorld world = context.isLength(2) ? plugin.getWorld(context.argAt(1)) : resolveWorld(context);
        if (world == null) throw new WorldNotExistException();
        
        CoUser user = world.getUser(context.argAt(0));
        if (user == null) throw new UserNotExistException();
        
        context.pluginMessage(GRAY + "All permissions for user " + AQUA + user.getName() + GRAY + ": " + WHITE + formatPerms(user.getPermissions()));
    }

    private void getUPermsTab(TabContext context) {
        context.playerCompletionAt(0);
    }

    //
    //
    //
    //
    private void getGroupPermissions(CommandContext context) throws BCIException {
        
        CoWorld world = context.isLength(2) ? plugin.getWorld(context.argAt(1)) : resolveWorld(context);
        if (world == null) throw new WorldNotExistException();
        
        Group group = world.getGroup(context.argAt(0).toLowerCase());
        if (group == null) throw new GroupNotExistException();
        
        context.pluginMessage(GRAY + "All permissions for group " + AQUA + group.getName() + GRAY + ": " + WHITE + formatPerms(group.getPermissions()));
    }

    private void getGPermsTab(TabContext context) {
        //context.completionAt(0, holder.getGroupNames().toArray(new String[0]));
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
    
    
    private Pair<Set<String>, Set<String>> resolveSets(CommandContext context, Group group, CoUser user) {
        Set<String> setA = new HashSet<>();
        Set<String> setB = new HashSet<>();
        for (int i = 1; context.getArgs().size() > i; i++) {
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
    
    private CoWorld resolveWorld(CommandContext context) {
        if (!context.isLocatable()) return plugin.getDefaultWorld();
        else return plugin.getWorld(context.getLocation().getWorld());
    }

}

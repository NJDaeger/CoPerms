package com.njdaeger.coperms.commands;

import com.njdaeger.bci.base.BCICommand;
import com.njdaeger.bci.base.BCIException;
import com.njdaeger.bci.defaults.BCIBuilder;
import com.njdaeger.bci.defaults.CommandContext;
import com.njdaeger.btu.Text;
import com.njdaeger.coperms.CoPerms;
import com.njdaeger.coperms.Pair;
import com.njdaeger.coperms.commands.flags.PageFlag;
import com.njdaeger.coperms.commands.flags.WorldFlag;
import com.njdaeger.coperms.data.CoUser;
import com.njdaeger.coperms.data.CoWorld;
import com.njdaeger.coperms.exceptions.GroupNotExistException;
import com.njdaeger.coperms.exceptions.PageOutOfBoundsException;
import com.njdaeger.coperms.exceptions.UserNotExistException;
import com.njdaeger.coperms.exceptions.WorldNotExistException;
import com.njdaeger.coperms.groups.Group;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.bukkit.ChatColor.*;

public final class PermissionCommands {

    private final CoPerms plugin;

    public PermissionCommands(CoPerms plugin) {
        this.plugin = plugin;

        BCICommand addPerm = BCIBuilder.create("addperm")
                .executor((c) -> {
                    if (c.subCommandAt(0, "user", true, this::addUserPermission)) return;
                    if (c.subCommandAt(0, "group", true, this::addGroupPermission)) return;
                    throw new BCIException("Please specify 'group' or 'user' to add a permission to.");
                })
                .completer((c) -> {
                    c.completionAt(0, "user", "group");
                    c.subCompletionAt(1, true, "user", CommandUtil::playerCompletion);
                    c.subCompletionAt(1, true, "group", CommandUtil::allGroupCompletion);
                })
                .minArgs(3)
                .flag(new WorldFlag())
                .description("Adds a permission or permissions to a user or group")
                .usage("/addperm user|group <user|group> w:[world] <permission> [permissions...]")
                .build();

        BCICommand removePerm = BCIBuilder.create("removeperm")
                .executor((c) -> {
                    if (c.subCommandAt(0, "user", true, this::removeUserPermission)) return;
                    if (c.subCommandAt(0, "group", true, this::removeGroupPermission)) return;
                    throw new BCIException("Please specify 'group' or 'user' to remove a permission from.");
                })
                .completer((c) -> {
                    c.completionAt(0, "user", "group");
                    c.subCompletionAt(1, true, "user", CommandUtil::playerCompletion);
                    c.subCompletionAt(1, true, "group", CommandUtil::allGroupCompletion);
                    if (c.isGreater(1) && c.first().equalsIgnoreCase("user")) c.completion(CommandUtil.resolveWorld(c).getUser(c.first()).getPermissions().toArray(new String[0]));
                    if (c.isGreater(1) && c.first().equalsIgnoreCase("group")) c.completion(CommandUtil.resolveWorld(c).getGroup(c.first()).getPermissions().toArray(new String[0]));
                })
                .minArgs(3)
                .flag(new WorldFlag())
                .description("Removes a permission or permissions from a user or group")
                .usage("/removeperm user|group <user|group> w:[world] <permission> [permissions...]")
                .build();


        BCICommand adduperm = BCIBuilder.create("adduperm")
                .executor(this::addUserPermission)
                .completer((c) -> c.completionAt(0, CommandUtil::playerCompletion))
                .flag(new WorldFlag())
                .aliases("adduserperm")
                .description("Adds a permission to a user")
                .usage("/adduperm <user> w:[world] <permission> [permissions...]")
                .flag(new WorldFlag())
                .minArgs(2)
                .permissions("coperms.permissions.user.add")
                .build();
    
        BCICommand remuperm = BCIBuilder.create("remuperm")
                .executor(this::removeUserPermission)
                .completer((c) -> {
                    c.completionAt(0, CommandUtil::playerCompletion);
                    if (c.isGreater(1)) c.completion(CommandUtil.resolveWorld(c).getUser(c.first()).getPermissions().toArray(new String[0]));
                })
                .flag(new WorldFlag())
                .aliases("remuserperm")
                .description("Removes a permission from a user")
                .usage("/remuperm <user> w:[world] <permission> [permission]...")
                .minArgs(2)
                .permissions("coperms.permission.user.remove")
                .build();
    
        BCICommand addgperm = BCIBuilder.create("addgperm")
                .executor(this::addGroupPermission)
                .completer((c) -> c.completionAt(0, CommandUtil::allGroupCompletion))
                .flag(new WorldFlag())
                .aliases("addgroupperm")
                .description("Adds a permission to a group")
                .usage("/addgperm <group> w:[world] <permission> [permission]...")
                .minArgs(2)
                .permissions("coperms.permission.group.add")
                .build();
    
        BCICommand remgperm = BCIBuilder.create("remgperm")
                .executor(this::removeGroupPermission)
                .completer((c) -> {
                    c.completionAt(0, CommandUtil::allGroupCompletion);
                    if (c.isGreater(1)) c.completion(CommandUtil.resolveWorld(c).getGroup(c.first()).getPermissions().toArray(new String[0]));
                })
                .flag(new WorldFlag())
                .aliases("remgroupperm")
                .description("Removes a permission from a group")
                .usage("/remgperm <group> w:[world] <permission> [permission]...")
                .minArgs(2)
                .permissions("coperms.permission.group.remove")
                .build();
    
        BCICommand getuperms = BCIBuilder.create("getuperms")
                .executor(this::getUserPermissions)
                .completer((c) -> {
                    c.completionAt(0, CommandUtil::playerCompletion);
                    c.completionAt(1, (ctx) -> new ArrayList<>(plugin.getWorlds().keySet()));
                })
                .aliases("userperms", "listuperms")
                .description("Shows a list of user permissions")
                .usage("/getuperms <user> w:[world] p:[page]")
                .flag(new WorldFlag())
                .flag(new PageFlag())
                .minArgs(1)
                .maxArgs(2)
                .permissions("coperms.permission.user.see")
                .build();
    
        BCICommand getgperms = BCIBuilder.create("getgperms")
                .executor(this::getGroupPermissions)
                .completer((c) -> {
                    c.completionAt(0, CommandUtil::groupCompletion);
                    c.completionAt(1, (ctx) -> new ArrayList<>(plugin.getWorlds().keySet()));
                })
                .aliases("groupperms", "listgperms")
                .description("Shows a list of group permissions")
                .usage("/getgperms <group> w:[world] p:[page]")
                .flag(new WorldFlag())
                .flag(new PageFlag())
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
    private void addUserPermission(CommandContext context) throws BCIException {
        
        CoWorld world = context.hasFlag("w") ? context.getFlag("w") : CommandUtil.resolveWorld(context);
        if (world == null) throw new WorldNotExistException();

        CoUser user = world.getUser(context.argAt(0));
        if (user == null) throw new UserNotExistException();

        Pair<Set<String>, Set<String>> pair = resolveSets(context, null, user, true);
        if (!pair.getFirst().isEmpty()) {
            context.pluginMessage(GRAY + "Added the following permission(s) to user " + AQUA + user.getName() + GRAY + ": " + WHITE + formatInlinePerms(pair.getFirst()));
        }
        if (!pair.getSecond().isEmpty()) {
            context.pluginMessage(GRAY + "Some permissions could not be added to user " + AQUA + user.getName() + GRAY + ": " + WHITE + formatInlinePerms(pair.getSecond()));
        }
    }

    private void removeUserPermission(CommandContext context) throws BCIException {
        
        CoWorld world = context.hasFlag("w") ? context.getFlag("w") : CommandUtil.resolveWorld(context);
        if (world == null) throw new WorldNotExistException();
        
        CoUser user = world.getUser(context.argAt(0));
        if (user == null) throw new UserNotExistException();
        
        Pair<Set<String>, Set<String>> pair = resolveSets(context, null, user, false);
        if (!pair.getFirst().isEmpty()) {
            context.pluginMessage(GRAY + "Removed the following permission(s) from user " + AQUA + user.getName() + GRAY + ": " + WHITE + formatInlinePerms(pair.getFirst()));
        }
        if (!pair.getSecond().isEmpty()) {
            context.pluginMessage(GRAY + "Some permissions could not be removed from user " + AQUA + user.getName() + GRAY + ": " + WHITE + formatInlinePerms(pair.getSecond()));
        }
    }

    //
    //
    //
    //
    
    private void addGroupPermission(CommandContext context) throws BCIException {
        
        CoWorld world = context.hasFlag("w") ? context.getFlag("w") : CommandUtil.resolveWorld(context);
        if (world == null) throw new WorldNotExistException();
        
        Group group = world.getGroup(context.argAt(0));
        if (group == null) throw new GroupNotExistException();
        
        Pair<Set<String>, Set<String>> pair = resolveSets(context, group, null, true);
        if (!pair.getFirst().isEmpty()) {
            context.pluginMessage(GRAY + "The following permission(s) was added to group " + AQUA + group.getName() + GRAY + ": " + WHITE + formatInlinePerms(pair.getFirst()));
        }
        if (!pair.getSecond().isEmpty()) {
            context.pluginMessage(GRAY + "Some permissions could not be added to group " + AQUA + group.getName() + GRAY + ": " + WHITE + formatInlinePerms(pair.getSecond()));
        }
        world.getUsers().forEach((uuid, user) -> user.resolvePermissions());
    }
    
    private void removeGroupPermission(CommandContext context) throws BCIException {
        
        CoWorld world = context.hasFlag("w") ? context.getFlag("w") : CommandUtil.resolveWorld(context);
        if (world == null) throw new WorldNotExistException();
    
        Group group = world.getGroup(context.argAt(0));
        if (group == null) throw new GroupNotExistException();

        Pair<Set<String>, Set<String>> pair = resolveSets(context, group, null, false);
        if (!pair.getFirst().isEmpty()) {
            context.pluginMessage(GRAY + "The following permission(s) was removed from group " + AQUA + group.getName() + GRAY + ": " + WHITE + formatInlinePerms(pair.getFirst()));
        }
        if (!pair.getSecond().isEmpty()) {
            context.pluginMessage(GRAY + "Some permissions could not be removed from group " + AQUA + group.getName() + GRAY + ": " + WHITE + formatInlinePerms(pair.getSecond()));
        }
        world.getUsers().forEach((uuid, user) -> user.resolvePermissions());
    }
    
    //
    //
    //
    //

    private void getUserPermissions(CommandContext context) throws BCIException {

        CoWorld world = context.hasFlag("w") ? context.getFlag("w") : CommandUtil.resolveWorld(context);
        if (world == null) throw new WorldNotExistException();

        int page = context.hasFlag("p") ? context.getFlag("p") : 0;

        CoUser user = world.getUser(context.argAt(0));
        if (user == null) throw new UserNotExistException();

        if (page < 0 || page*10 >= user.getPermissions().size()) throw new PageOutOfBoundsException();

        context.pluginMessage(GRAY + "All permissions for user " + AQUA + user.getName() + GRAY + ":");
        sendPaginatedList(context, world, null, user, user.getPermissions(), page);

    }

    //
    //
    //
    //
    private void getGroupPermissions(CommandContext context) throws BCIException {

        CoWorld world = context.hasFlag("w") ? context.getFlag("w") : CommandUtil.resolveWorld(context);
        if (world == null) throw new WorldNotExistException();

        int page = context.hasFlag("p") ? context.getFlag("p") : 0;

        Group group = world.getGroup(context.argAt(0).toLowerCase());
        if (group == null) throw new GroupNotExistException();

        if (page < 0 || page*10 >= group.getPermissions().size()) throw new PageOutOfBoundsException();

        context.pluginMessage(GRAY + "All permissions for group " + AQUA + group.getName() + GRAY + ":");
        sendPaginatedList(context, world, group, null, group.getPermissions(), page);
    }

    private void sendPaginatedList(CommandContext context, CoWorld world, Group group, CoUser user, Collection<String> permissions, int page) {
        // [X] (negate for group) [O] remove from group, fallback to whether the permission is given to the group via inheritance [+] add for group

        for (String perm : permissions.stream().skip(page*10).limit(10).toArray(String[]::new)) {
            context.send("> " + formatPermission(perm));
        }
        if (context.isPlayer()) {
            Text.TextSection txt = Text.of(page == 0 ? RED + "|X|--" : GREEN + "<<--")
                    .setBold(true)
                    .clickEvent(c -> {
                        c.action(Text.ClickAction.RUN_COMMAND);
                        c.click(user == null ? "/getgperms " + group.getName() + " w:" + world.getName().replaceAll(" ", "_") + " p:" + (page - 1)
                                : "/getuperms " + user.getName() + " w:" + world.getName().replaceAll(" ", "_") + " p:" + (page - 1));
                    })
                    .append(" =================== ")
                    .setColor(GRAY)
                    .append((page + 1)*10 > permissions.size() ? RED + "--|X|" : GREEN + "-->>").setBold(true)
                    .clickEvent(c -> {
                        c.action(Text.ClickAction.RUN_COMMAND);
                        c.click(user == null ? "/getgperms " + group.getName() + " w:" + world.getName().replaceAll(" ", "_") + " p:" + (page + 1)
                                : "/getuperms " + user.getName() + " w:" + world.getName().replaceAll(" ", "_") + " p:" + (page + 1));
                    });
            Text.sendTo(txt, context.asPlayer());
        }
        else context.send("Use the page flag to change pages. Eg. /getgperms Admin p:3");
    }

    private String formatPermission(String permission) {
        return GRAY.toString() + (permission.startsWith("-") ? ITALIC : "") + (permission.endsWith(".*") ? UNDERLINE : "") + permission;
    }

    private String formatInlinePerms(Collection<String> permissions) {
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
            builder.append(formatPermission(node)).append(comma);
        }
        String s = builder.toString().trim();
        return s.substring(0, (s.endsWith(",") ? s.lastIndexOf(",") : s.length()));
    }
    
    
    private Pair<Set<String>, Set<String>> resolveSets(CommandContext context, Group group, CoUser user, boolean add) {
        Set<String> setA = new HashSet<>();
        Set<String> setB = new HashSet<>();
        for (int i = 1; context.getArgs().size() > i; i++) {
            if (user == null) {
                if (add) {
                    if (group.grantPermission(context.argAt(i))) {
                        setA.add(context.argAt(i));
                        continue;
                    }
                } else {
                    if (group.revokePermission(context.argAt(i))) {
                        setA.add(context.argAt(i));
                        continue;
                    }
                }
            } else {
                if (add) {
                    if (user.grantPermission(context.argAt(i))) {
                        setA.add(context.argAt(i));
                        continue;
                    }
                } else {
                    if (user.revokePermission(context.argAt(i))) {
                        setA.add(context.argAt(i));
                        continue;
                    }
                }
            }
            setB.add(context.argAt(i));
        }
        return new Pair<>(setA, setB);
    }

}

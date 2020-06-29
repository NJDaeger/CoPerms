package com.njdaeger.coperms.commands;

import com.njdaeger.coperms.CoPerms;
import com.njdaeger.coperms.commands.flags.PageFlag;
import com.njdaeger.coperms.commands.flags.WorldFlag;
import com.njdaeger.coperms.data.CoUser;
import com.njdaeger.coperms.data.CoWorld;
import com.njdaeger.coperms.exceptions.GroupNotExistException;
import com.njdaeger.coperms.exceptions.UserNotExistException;
import com.njdaeger.coperms.exceptions.WorldNotExistException;
import com.njdaeger.coperms.groups.AbstractGroup;
import com.njdaeger.coperms.tree.PermissionTree;
import com.njdaeger.pdk.command.CommandBuilder;
import com.njdaeger.pdk.command.CommandContext;
import com.njdaeger.pdk.command.PDKCommand;
import com.njdaeger.pdk.command.exception.PDKCommandException;
import com.njdaeger.pdk.command.flag.OptionalFlag;
import com.njdaeger.pdk.utils.Text;
import org.bukkit.ChatColor;

import java.util.Set;

import static org.bukkit.ChatColor.*;

@SuppressWarnings("Duplicates")
public final class PermissionCommands {

    private final CoPerms plugin;

    public PermissionCommands(CoPerms plugin) {
        this.plugin = plugin;

        PDKCommand grantUserPerm = CommandBuilder.of("grantuperm", "guperm")
                .executor(this::userPermissionChange)
                .completer(c -> c.completionAt(0, CommandUtil::playerCompletion))
                .description("Grants a permission to a specific user")
                .usage("/grantuperm <user> <permissions...> [-w <world>]")
                .flag(new WorldFlag())
                .flag(new OptionalFlag("Silent the command output", "-s", "s"))
                .min(2)
                .permissions("coperms.permission.user.grant")
                .build();
        grantUserPerm.register(plugin);

        PDKCommand grantGroupPerm = CommandBuilder.of("grantgperm", "ggperm")
                .executor(this::groupPermissionChange)
                .completer(c -> c.completionAt(0, CommandUtil::allGroupCompletion))
                .description("Grants a permission to a specific group")
                .usage("/grantgperm <group> <permissions...> [-w <world>]")
                .flag(new WorldFlag())
                .flag(new OptionalFlag("Silent the command output", "-s", "s"))
                .min(2)
                .permissions("coperms.permission.group.grant")
                .build();
        grantGroupPerm.register(plugin);

        PDKCommand revokeUserPerm = CommandBuilder.of("revokeuperm", "revuperm")
                .executor(this::userPermissionChange)
                .completer(c -> c.completionAt(0, CommandUtil::playerCompletion))
                .description("Revokes a permission to a specific user")
                .usage("/revokeuperm <user> <permissions...> [-w <world>]")
                .flag(new OptionalFlag("Silent the command output", "-s", "s"))
                .flag(new WorldFlag())
                .min(2)
                .permissions("coperms.permission.user.revoke")
                .build();
        revokeUserPerm.register(plugin);

        PDKCommand revokeGroupPerm = CommandBuilder.of("revokegperm", "revgperm")
                .executor(this::groupPermissionChange)
                .completer(c -> c.completionAt(0, CommandUtil::allGroupCompletion))
                .description("Revokes a permission to a specific group")
                .usage("/revokegperm <group> <permissions...> [-w <world>]")
                .flag(new WorldFlag())
                .flag(new OptionalFlag("Silent the command output", "-s", "s"))
                .min(2)
                .permissions("coperms.permission.group.revoke")
                .build();
        revokeGroupPerm.register(plugin);

        PDKCommand removeUserPerm = CommandBuilder.of("removeuperm", "remuperm")
                .executor(this::userPermissionChange)
                .completer(c -> c.completionAt(0, CommandUtil::playerCompletion))
                .description("Removes a permission to a specific user")
                .usage("/removeuperm <user> <permissions...> [-w <world>]")
                .flag(new WorldFlag())
                .flag(new OptionalFlag("Silent the command output", "-s", "s"))
                .min(2)
                .permissions("coperms.permission.user.remove")
                .build();
        removeUserPerm.register(plugin);

        PDKCommand removeGroupPerm = CommandBuilder.of("removegperm", "remgperm")
                .executor(this::groupPermissionChange)
                .completer(c -> c.completionAt(0, CommandUtil::allGroupCompletion))
                .description("Removes a permission to a specific group")
                .usage("/removegperm <group> <permissions...> [-w <world>]")
                .flag(new WorldFlag())
                .flag(new OptionalFlag("Silent the command output", "-s", "s"))
                .min(2)
                .permissions("coperms.permission.group.remove")
                .build();
        removeGroupPerm.register(plugin);

        PDKCommand listUserPerms = CommandBuilder.of("listuperms", "edituperms", "euperms", "getuperms")
                .executor(this::listUserPermissions)
                .completer(c -> c.completionAt(0, CommandUtil::playerCompletion))
                .description("View and modify the permissions of a user")
                .usage("/listuperms <user> [-p <page>] [-w <world>]")
                .flag(new WorldFlag())
                .flag(new PageFlag())
                .min(1)
                .max(1)
                .permissions("coperms.permission.user.list")
                .build();
        listUserPerms.register(plugin);

        PDKCommand listGroupPerms = CommandBuilder.of("listgperms", "editgperms", "egperms", "getgperms")
                .executor(this::listGroupPermissions)
                .completer(c -> c.completionAt(0, CommandUtil::allGroupCompletion))
                .description("View and modify the permissions of a group")
                .usage("/listgperms <group> [-p <page>] [-w <world>]")
                .flag(new WorldFlag())
                .flag(new PageFlag())
                .min(1)
                .max(1)
                .permissions("coperms.permission.group.list")
                .build();
        listGroupPerms.register(plugin);

    }

    private void listGroupPermissions(CommandContext context) throws PDKCommandException {

        CoWorld world = context.hasFlag("w") ? context.getFlag("w") : CommandUtil.resolveWorld(context);
        if (world == null) throw new WorldNotExistException();

        AbstractGroup group = world.getGroup(context.argAt(0));
        if (group == null) group = plugin.getSuperGroup(context.argAt(0));
        if (group == null) throw new GroupNotExistException();


        int page = context.hasFlag("p") ? context.getFlag("p") : 1;
        sendEditPermissionPage(context, group.getPermissionTree(), world, page, true);

    }

    private void listUserPermissions(CommandContext context) throws PDKCommandException {

        CoWorld world = context.hasFlag("w") ? context.getFlag("w") : CommandUtil.resolveWorld(context);
        if (world == null) throw new WorldNotExistException();

        CoUser user = world.getUser(context.argAt(0));
        if (user == null) throw new UserNotExistException();


        int page = context.hasFlag("p") ? context.getFlag("p") : 1;
        sendEditPermissionPage(context, user.getPermissionTree(), world, page, false);

    }

    private void userPermissionChange(CommandContext context) throws PDKCommandException {

        boolean isSilent = context.hasFlag("s");

        CoWorld world = context.hasFlag("w") ? context.getFlag("w") : CommandUtil.resolveWorld(context);
        if (world == null) throw new WorldNotExistException();

        CoUser user = world.getUser(context.argAt(0));
        if (user == null) throw new UserNotExistException();

        int length = context.getLength()-1;
        Set<String> unused;
        String action;

        if (context.getAlias().startsWith("g")) {
            action = "Granted ";
            unused = user.grantPermissions(fixPermissions(context.joinArgs(1).split(" ")));
        } else if (context.getAlias().startsWith("rev")) {
            action = "Revoked ";
            unused = user.revokePermissions(fixPermissions(context.joinArgs(1).split(" ")));
        } else {
            action = "Removed ";
            unused = user.removePermissions(fixPermissions(context.joinArgs(1).split(" ")));
        }

        if (!isSilent) {
            context.pluginMessage(GRAY + action + DARK_AQUA + (length - unused.size()) + "/" + length + GRAY + " permissions. " + (!unused.isEmpty() ? "The following couldn't be " + action.toLowerCase().trim() + ":" : ""));
            if (!unused.isEmpty()) context.send(createPermissionListString(unused));
        }
    }

    private void groupPermissionChange(CommandContext context) throws PDKCommandException {

        boolean isSilent = context.hasFlag("s");

        CoWorld world = context.hasFlag("w") ? context.getFlag("w") : CommandUtil.resolveWorld(context);
        if (world == null) throw new WorldNotExistException();

        AbstractGroup group = world.getGroup(context.argAt(0));
        if (group == null) group = plugin.getSuperGroup(context.argAt(0));
        if (group == null) throw new GroupNotExistException();

        int length = context.getLength()-1;
        Set<String> unused;
        String action;

        if (context.getAlias().startsWith("g")) {
            action = "Granted ";
            unused = group.grantPermissions(fixPermissions(context.joinArgs(1).split(" ")));
        } else if (context.getAlias().startsWith("rev")) {
            action = "Revoked ";
            unused = group.revokePermissions(fixPermissions(context.joinArgs(1).split(" ")));
        } else {
            action = "Removed ";
            unused = group.removePermissions(fixPermissions(context.joinArgs(1).split(" ")));
        }

        if (!isSilent) {
            context.pluginMessage(GRAY + action + DARK_AQUA + (length - unused.size()) + "/" + length + GRAY + " permissions. " + (!unused.isEmpty() ? "The following couldn't be " + action.toLowerCase().trim() + ":" : ""));
            if (!unused.isEmpty()) context.send(createPermissionListString(unused));
        }
        world.getUsers().forEach((uuid, user) -> user.updateCommands());
    }

    private String[] fixPermissions(String[] perms) {
        for (int i = 0; i < perms.length; i++) {
            if (perms[i].startsWith("-")) perms[i] = perms[i].substring(1);
        }
        return perms;
    }

    private void sendEditPermissionPage(CommandContext context, PermissionTree permissionTree, CoWorld world, int page, boolean group) throws PDKCommandException {
        Set<String> permissions = permissionTree.getPermissionNodes();
        int pages = (int) Math.ceil(permissions.size()/10.);

        if (pages < page || page <= 0) context.error(RED + "There are no more pages to display.");

        Text.TextSection text = Text.of("CoPerms ").setColor(AQUA).append("Permission Editor ------ Page: ").setColor(GRAY).append(page + "/" + pages).setColor(AQUA).append("\n");
        for (String permission : permissions.stream().skip((page-1)*10).limit(10).toArray(String[]::new)) {
            if (permission.startsWith("-")) permission = permission.substring(1);
            switch (permissionTree.getGrantedState(permission)) {
                case -1:
                    text.append("[-]").setColor(RED).setBold(true).hoverEvent(Text.HoverAction.SHOW_TEXT, Text.of("Permission is revoked").setColor(GRAY))
                            .append("[o]").setColor(GRAY).setBold(true)
                            .hoverEvent(Text.HoverAction.SHOW_TEXT, Text.of("Remove permission").setColor(GRAY))
                            .clickEvent(Text.ClickAction.RUN_COMMAND, "/remove" + (group ? "g" : "u") + "perm " + context.argAt(0) + " " + permission)
                            .append("[+]").setColor(GRAY).setBold(true)
                            .hoverEvent(Text.HoverAction.SHOW_TEXT, Text.of("Grant permission").setColor(GRAY))
                            .clickEvent(Text.ClickAction.RUN_COMMAND, "/grant" + (group ? "g" : "u") + "perm " + context.argAt(0) + " " + permission)
                            .append(" >  ").setColor(DARK_AQUA).setBold(true).append(formatPermission(permission)).append("\n");
                    break;
                case 1:
                    text.append("[-]").setColor(GRAY).setBold(true)
                            .hoverEvent(Text.HoverAction.SHOW_TEXT, Text.of("Revoke permission").setColor(GRAY))
                            .clickEvent(Text.ClickAction.RUN_COMMAND, "/revoke" + (group ? "g" : "u") + "perm " + context.argAt(0) + " " + permission)
                            .append("[o]").setColor(GRAY).setBold(true)
                            .hoverEvent(Text.HoverAction.SHOW_TEXT, Text.of("Remove permission").setColor(GRAY))
                            .clickEvent(Text.ClickAction.RUN_COMMAND, "/remove" + (group ? "g" : "u") + "perm " + context.argAt(0) + " " + permission)
                            .append("[+]").setColor(GREEN).setBold(true)
                            .hoverEvent(Text.HoverAction.SHOW_TEXT, Text.of("Permission is granted").setColor(GRAY))
                            .append(" >  ").setColor(DARK_AQUA).setBold(true).append(formatPermission(permission)).append("\n");
                    break;
                case 0:
                    text.append("[-]").setColor(GRAY).setBold(true)
                            .hoverEvent(Text.HoverAction.SHOW_TEXT, Text.of("Revoke permission").setColor(GRAY))
                            .clickEvent(Text.ClickAction.RUN_COMMAND, "/revoke" + (group ? "g" : "u") + "perm " + context.argAt(0) + " " + permission)
                            .append("[o]").setColor(DARK_GRAY).setBold(true)
                            .hoverEvent(Text.HoverAction.SHOW_TEXT, Text.of("Permission is removed (inherited)").setColor(GRAY))
                            .append("[+]").setColor(GRAY).setBold(true)
                            .hoverEvent(Text.HoverAction.SHOW_TEXT, Text.of("Grant permission").setColor(GRAY))
                            .clickEvent(Text.ClickAction.RUN_COMMAND, "/grant" + (group ? "g" : "u") + "perm " + context.argAt(0) + " " + permission)
                            .append(" >  ").setColor(DARK_AQUA).setBold(true).append(formatPermission(permission)).append("\n");
                    break;
                default:
                    text.append("[-][o][+]").setColor(GRAY).setBold(true).hoverEvent(Text.HoverAction.SHOW_TEXT, Text.of("Unknown state").setColor(GRAY))
                        .append(" >  ").setColor(DARK_AQUA).setBold(true).append(formatPermission(permission)).append("\n");
            }
        }
        text.append(page <= 1 ? RED + "|X|--" : GREEN + "<<--")
                .setBold(true)
                .clickEvent(Text.ClickAction.RUN_COMMAND, "/list" + (group ? "g" : "u") + "perms " + context.argAt(0) + " -p " + (page-1) + " -w " + world.getName().replaceAll(" ", "_"))
                .append(" =================== ")
                .setColor(GRAY)
                .append(page == pages ? RED + "--|X|" : GREEN + "-->>").setBold(true)
                .clickEvent(Text.ClickAction.RUN_COMMAND, "/list" + (group ? "g" : "u") + "perms " + context.argAt(0) + " -p " + (page+1) + " -w " + world.getName().replaceAll(" ", "_"));

        if (context.isPlayer()) {
            text.sendTo(context.asPlayer());
        } else context.send(text.getFormatted());
    }

    private String formatPermission(String permission) {
        return GRAY.toString() + (permission.startsWith("-") ? ITALIC : "") + (permission.endsWith(".*") ? UNDERLINE : "") + permission;
    }

    private String createPermissionListString(Set<String> permissions) {
        StringBuilder builder = new StringBuilder();
        permissions.forEach(perm -> builder.append(AQUA).append(BOLD).append("> ").append(RESET).append(formatPermission(perm)));
        return builder.toString();
    }
}

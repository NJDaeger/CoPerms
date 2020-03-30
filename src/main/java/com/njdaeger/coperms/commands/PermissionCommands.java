package com.njdaeger.coperms.commands;

import com.njdaeger.bci.base.BCICommand;
import com.njdaeger.bci.base.BCIException;
import com.njdaeger.bci.defaults.BCIBuilder;
import com.njdaeger.bci.defaults.CommandContext;
import com.njdaeger.btu.Text;
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

import java.util.Set;

import static org.bukkit.ChatColor.*;

public final class PermissionCommands {

    private final CoPerms plugin;

    public PermissionCommands(CoPerms plugin) {
        this.plugin = plugin;

        BCICommand grantUserPerm = BCIBuilder.create("grantuperm")
                .executor(this::userPermissionChange)
                .aliases("guperm")
                .description("Grants a permission to a specific user")
                .usage("/grantuperm <user> <permissions...> [-w <world>]")
                .flag(new WorldFlag())
                .minArgs(2)
                .permissions("coperms.permission.user.grant")
                .build();

        BCICommand grantGroupPerm = BCIBuilder.create("grantgperm")
                .executor(this::groupPermissionChange)
                .aliases("ggperm")
                .description("Grants a permission to a specific group")
                .usage("/grantgperm <group> <permissions...> [-w <world>]")
                .flag(new WorldFlag())
                .minArgs(2)
                .permissions("coperms.permission.group.grant")
                .build();

        BCICommand revokeUserPerm = BCIBuilder.create("revokeuperm")
                .executor(this::userPermissionChange)
                .aliases("revuperm")
                .description("Revokes a permission to a specific user")
                .usage("/revokeuperm <user> <permissions...> [-w <world>]")
                .flag(new WorldFlag())
                .minArgs(2)
                .permissions("coperms.permission.user.revoke")
                .build();

        BCICommand revokeGroupPerm = BCIBuilder.create("revokegperm")
                .executor(this::groupPermissionChange)
                .aliases("revgperm")
                .description("Revokes a permission to a specific group")
                .usage("/revokegperm <group> <permissions...> [-w <world>]")
                .flag(new WorldFlag())
                .minArgs(2)
                .permissions("coperms.permission.group.revoke")
                .build();

        BCICommand removeUserPerm = BCIBuilder.create("removeuperm")
                .executor(this::userPermissionChange)
                .aliases("remuperm")
                .description("Removes a permission to a specific user")
                .usage("/removeuperm <user> <permissions...> [-w <world>]")
                .flag(new WorldFlag())
                .minArgs(2)
                .permissions("coperms.permission.user.remove")
                .build();

        BCICommand removeGroupPerm = BCIBuilder.create("removegperm")
                .executor(this::groupPermissionChange)
                .aliases("remgperm")
                .description("Removes a permission to a specific group")
                .usage("/removegperm <group> <permissions...> [-w <world>]")
                .flag(new WorldFlag())
                .minArgs(2)
                .permissions("coperms.permission.group.remove")
                .build();

        BCICommand listUserPerms = BCIBuilder.create("listuperms")
                .executor(this::listUserPermissions)
                .aliases("edituperms", "euperms", "getuperms")
                .description("View and modify the permissions of a user")
                .usage("/listuperms <user> [-p <page>] [-w <world>]")
                .flag(new WorldFlag())
                .flag(new PageFlag())
                .minArgs(1)
                .maxArgs(1)
                .permissions("coperms.permission.user.list")
                .build();

        BCICommand listGroupPerms = BCIBuilder.create("listgperms")
                .executor(this::listGroupPermissions)
                .aliases("editgperms", "egperms", "getgperms")
                .description("View and modify the permissions of a user")
                .usage("/listgperms <group> [-p <page>] [-w <world>]")
                .flag(new WorldFlag())
                .flag(new PageFlag())
                .minArgs(1)
                .maxArgs(1)
                .permissions("coperms.permission.group.list")
                .build();

        plugin.getCommandStore().registerCommands(grantGroupPerm, grantUserPerm, revokeGroupPerm, revokeUserPerm, removeGroupPerm, removeUserPerm, listGroupPerms, listUserPerms);
    }

    private void listGroupPermissions(CommandContext context) throws BCIException {

        CoWorld world = context.hasFlag("w") ? context.getFlag("w") : CommandUtil.resolveWorld(context);
        if (world == null) throw new WorldNotExistException();

        AbstractGroup group = world.getGroup(context.argAt(0));
        if (group == null) group = plugin.getSuperGroup(context.argAt(0));
        if (group == null) throw new GroupNotExistException();


        int page = context.hasFlag("p") ? context.getFlag("p") : 0;
        sendEditPermissionPage(context, group.getPermissionTree(), world, page, true);

    }

    private void listUserPermissions(CommandContext context) throws BCIException {

        CoWorld world = context.hasFlag("w") ? context.getFlag("w") : CommandUtil.resolveWorld(context);
        if (world == null) throw new WorldNotExistException();

        CoUser user = world.getUser(context.argAt(0));
        if (user == null) throw new UserNotExistException();


        int page = context.hasFlag("p") ? context.getFlag("p") : 0;
        sendEditPermissionPage(context, user.getPermissionTree(), world, page, false);

    }

    private void userPermissionChange(CommandContext context) throws BCIException {

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
            unused = user.grantPermissions(context.joinArgs(1).split(" "));
        } else if (context.getAlias().startsWith("rev")) {
            action = "Revoked ";
            unused = user.revokePermissions(context.joinArgs(1).split(" "));
        } else {
            action = "Removed ";
            unused = user.removePermissions(context.joinArgs(1).split(" "));
        }

        if (!isSilent) {
            context.pluginMessage(GRAY + action + AQUA + (length - unused.size()) + "/" + length + GRAY + " permissions. The following couldn't be " + action.toLowerCase().trim() + ":");
            context.send(createPermissionListString(unused));
        }
    }

    private void groupPermissionChange(CommandContext context) throws BCIException {

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
            unused = group.grantPermissions(context.joinArgs(1).split(" "));
        } else if (context.getAlias().startsWith("rev")) {
            action = "Revoked ";
            unused = group.revokePermissions(context.joinArgs(1).split(" "));
        } else {
            action = "Removed ";
            unused = group.removePermissions(context.joinArgs(1).split(" "));
        }

        if (!isSilent) {
            context.pluginMessage(GRAY + action + AQUA + (length - unused.size()) + "/" + length + GRAY + " permissions. The following couldn't be " + action.toLowerCase().trim() + ":");
            context.send(createPermissionListString(unused));
        }
        world.getUsers().forEach((uuid, user) -> user.resolvePermissions());
    }

    private void sendEditPermissionPage(CommandContext context, PermissionTree permissionTree, CoWorld world, int page, boolean group) {
        Set<String> permissions = permissionTree.getPermissionNodes();
        int pages = (int) Math.ceil(permissions.size()/10.);

        Text.TextSection text = Text.of("CoPerms ").setColor(AQUA).append("Permission Editor ------ Page: ").setColor(GRAY).append(page + "/" + pages).setColor(AQUA).append("\n");
        for (String permission : permissions.stream().skip(page*10).limit(10).toArray(String[]::new)) {
            switch (permissionTree.getGrantedState(permission)) {
                case -1:
                    text.append("[-]").setColor(RED).setBold(true).hoverEvent(e -> {
                        e.action(Text.HoverAction.SHOW_TEXT);
                        e.hover(h -> h.setText("Permission is revoked").setColor(GRAY));
                    }).append("[o]").setColor(GRAY).setBold(true).hoverEvent(e -> {
                        e.action(Text.HoverAction.SHOW_TEXT);
                        e.hover(h -> h.setText("Remove permission").setColor(GRAY));
                    }).clickEvent(e -> {
                        e.action(Text.ClickAction.RUN_COMMAND);
                        e.click("/remove" + (group ? "g" : "u") + "perm " + context.argAt(0) + " " + permission);
                    }).append("[+]").setColor(GRAY).setBold(true).hoverEvent(e -> {
                        e.action(Text.HoverAction.SHOW_TEXT);
                        e.hover(h -> h.setText("Grant permission").setColor(GRAY));
                    }).clickEvent(e -> {
                        e.action(Text.ClickAction.RUN_COMMAND);
                        e.click("/grant" + (group ? "g" : "u") + "perm " + context.argAt(0) + " " + permission);
                    }).append(" >  ").setColor(AQUA).setBold(true).append(formatPermission(permission)).append("\n");
                    break;
                case 1:
                    text.append("[-]").setColor(GRAY).setBold(true).hoverEvent(e -> {
                        e.action(Text.HoverAction.SHOW_TEXT);
                        e.hover(h -> h.setText("Revoke permission").setColor(GRAY));
                    }).clickEvent(e -> {
                        e.action(Text.ClickAction.RUN_COMMAND);
                        e.click("/revoke" + (group ? "g" : "u") + "perm " + context.argAt(0) + " " + permission);
                    }).append("[o]").setColor(GRAY).setBold(true).hoverEvent(e -> {
                        e.action(Text.HoverAction.SHOW_TEXT);
                        e.hover(h -> h.setText("Remove permission").setColor(GRAY));
                    }).clickEvent(e -> {
                        e.action(Text.ClickAction.RUN_COMMAND);
                        e.click("/remove" + (group ? "g" : "u") + "perm " + context.argAt(0) + " " + permission);
                    }).append("[+]").setColor(GREEN).setBold(true).hoverEvent(e -> {
                        e.action(Text.HoverAction.SHOW_TEXT);
                        e.hover(h -> h.setText("Permission is granted").setColor(GRAY));
                    }).append(" >  ").setColor(AQUA).setBold(true).append(formatPermission(permission)).append("\n");
                    break;
                case 0:
                    text.append("[-]").setColor(GRAY).setBold(true).hoverEvent(e -> {
                        e.action(Text.HoverAction.SHOW_TEXT);
                        e.hover(h -> h.setText("Revoke permission").setColor(GRAY));
                    }).clickEvent(e -> {
                        e.action(Text.ClickAction.RUN_COMMAND);
                        e.click("/revoke" + (group ? "g" : "u") + "perm " + context.argAt(0) + " " + permission);
                    }).append("[0]").setColor(DARK_GRAY).setBold(true).hoverEvent(e -> {
                        e.action(Text.HoverAction.SHOW_TEXT);
                        e.hover(h -> h.setText("Permission is removed (inherited)").setColor(GRAY));
                    }).append("[+]").setColor(GRAY).setBold(true).hoverEvent(e -> {
                        e.action(Text.HoverAction.SHOW_TEXT);
                        e.hover(h -> h.setText("Grant permission").setColor(GRAY));
                    }).clickEvent(e -> {
                        e.action(Text.ClickAction.RUN_COMMAND);
                        e.click("/grant" + (group ? "g" : "u") + "perm " + context.argAt(0) + permission);
                    }).append(" >  ").setColor(AQUA).setBold(true).append(formatPermission(permission)).append("\n");
                    break;
                default:
                    text.append("[-][o][+]").setColor(GRAY).setBold(true).hoverEvent(e -> {
                        e.action(Text.HoverAction.SHOW_TEXT);
                        e.hover(h -> h.setText("Unknown state."));
                    }).append(" >  ").setColor(AQUA).setBold(true).append(formatPermission(permission)).append("\n");
            }
        }
        text.append(page == 0 ? RED + "|X|--" : GREEN + "<<--")
                .setBold(true)
                .clickEvent(c -> {
                    c.action(Text.ClickAction.RUN_COMMAND);
                    c.click("/list" + (group ? "g" : "u") + "perms " + context.argAt(0) + " -p " + (page-1) + " -w " + world.getName().replaceAll(" ", "_"));
                })
                .append(" =================== ")
                .setColor(GRAY)
                .append((page + 1)*10 > permissions.size() ? RED + "--|X|" : GREEN + "-->>").setBold(true)
                .clickEvent(c -> {
                    c.action(Text.ClickAction.RUN_COMMAND);
                    c.click("/list" + (group ? "g" : "u") + "perms " + context.argAt(0) + " -p " + (page+1) + " -w " + world.getName().replaceAll(" ", "_"));
                });

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

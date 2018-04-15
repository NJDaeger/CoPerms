package com.coalesce.coperms.commands;

import com.coalesce.coperms.CoPerms;
import com.coalesce.coperms.DataHolder;
import com.coalesce.coperms.commands.api.CoCommand;
import com.coalesce.coperms.data.CoUser;
import com.coalesce.coperms.data.CoWorld;
import com.coalesce.coperms.data.Group;
import com.coalesce.core.command.base.CommandContext;
import com.coalesce.core.command.base.TabContext;

import java.util.Set;

import static org.bukkit.ChatColor.*;

public final class UserCommands {

    private final DataHolder holder;

    public UserCommands(CoPerms plugin, DataHolder holder) {
        this.holder = holder;

        CoCommand promote = CoCommand.builder("promote")
                .executor(this::promote)
                .completer(this::promoteTab)
                .aliases("promo")
                .description("Promotes someone to the next rank.")
                .usage("/promote <user> [world]")
                .minArgs(1)
                .maxArgs(2)
                .permission("coperms.ranks.promote")
                .build();
    
        CoCommand setRank = CoCommand.builder( "setrank")
                .executor(this::setRank)
                .completer(this::setRankTab)
                .aliases("setr", "setgroup")
                .description("Adds a user to a specified rank")
                .usage("/setrank <user> <rank> [world]") //Make a list in the UserDataFile for each user that is similar to the group data file that specifies ranks per world
                .minArgs(2)
                .maxArgs(3)
                .permission("coperms.ranks.setrank")
                .build();

        CoCommand demote = CoCommand.builder("demote")
                .executor(this::demote)
                .completer(this::demoteTab)
                .aliases("demo")
                .description("Demotes someone to the previous rank.")
                .usage("/demote <user> [world]")
                .minArgs(1)
                .maxArgs(2)
                .permission("coperms.ranks.demote")
                .build();

        CoCommand setPrefix = CoCommand.builder("setprefix")
                .executor(this::setPrefix)
                .completer(this::setPrefixTab)
                .aliases("prefix")
                .description("Adds or removes a prefix from a user")
                .usage("/prefix <user> [prefix]")
                .minArgs(1)
                .permission("coperms.variables.user.prefix")
                .build();

        CoCommand setSuffix = CoCommand.builder("setsuffix")
                .executor(this::setSuffix)
                .completer(this::setSuffixTab)
                .description("Adds or removes a suffix from a user")
                .usage("/suffix <user> [prefix]")
                .minArgs(1)
                .permission("coperms.variables.user.suffix")
                .build();
    
        CoCommand setGPrefix = CoCommand.builder("setgprefix")
                .executor(this::setGroupPrefix)
                .completer(this::groupVarChange)
                .description("Sets the prefix of a group.")
                .usage("/setgprefix <group> <world> [prefix]")
                .aliases("setgpre")
                .minArgs(2)
                .permission("coperms.variables.group.prefix").build();
    
        CoCommand setGSuffix = CoCommand.builder("setgsuffix")
                .executor(this::setGroupSuffix)
                .completer(this::groupVarChange)
                .description("Sets the suffix of a group.")
                .usage("/setgsuffix <group> <world> [suffix]")
                .aliases("setgsuf")
                .minArgs(2)
                .permission("coperms.variables.group.suffix")
                .build();
        
        plugin.registerCommand(promote, setRank, demote, setPrefix, setSuffix, setGPrefix, setGSuffix);
    }

    //
    //
    //
    //

    private void promote(CommandContext context) {
        CoWorld world = holder.getWorld(context.argAt(1)) == null ? holder.getDefaultWorld() : holder.getWorld(context.argAt(1));
        CoUser user = holder.getUser((context.getArgs().size() == 1 ? holder.getDefaultWorld().getName() : context.argAt(1)), context.argAt(0));
        if (world == null) {
            context.pluginMessage(RED + "The world specified does not exist.");
            return;
        }
        if (user == null) {
            context.pluginMessage(RED + "The user specified does not exist in the world specified");
            return;
        }
        Group group = world.getGroup(user.getGroup().getRankID() + 1);
        if (group == null) {
            context.pluginMessage(RED + "The group specified does not exist in the world specified");
            return;
        }
        user.setGroup(world, group.getName());
        context.pluginMessage(DARK_AQUA + user.getName() + GRAY + " was promoted to " + DARK_AQUA + group.getName() + GRAY + " in world " + DARK_AQUA + world.getName());
        user.pluginMessage(GRAY + "You were promoted to " + DARK_AQUA + group.getName() + GRAY + " in world " + DARK_AQUA + world.getName());
    }

    private void promoteTab(TabContext context) {
        Set<String> worlds = holder.getWorlds().keySet();
        context.playerCompletion(0);
        context.completionAt(1, worlds.toArray(new String[0]));
    }

    //
    //
    //
    //

    private void demote(CommandContext context) {
        CoWorld world = holder.getWorld(context.argAt(1)) == null ? holder.getDefaultWorld() : holder.getWorld(context.argAt(1));
        CoUser user = holder.getUser((context.getArgs().size() == 1 ? holder.getDefaultWorld().getName() : context.argAt(1)), context.argAt(0));
        if (world == null) {
            context.pluginMessage(RED + "The world specified does not exist.");
            return;
        }
        if (user == null) {
            context.pluginMessage(RED + "The user specified does not exist in the world specified");
            return;
        }
        Group group = world.getGroup(user.getGroup().getRankID() - 1);
        if (group == null) {
            context.pluginMessage(RED + "The group specified does not exist in the world specified");
            return;
        }
        user.setGroup(world, group.getName());
        context.pluginMessage(DARK_AQUA + user.getName() + GRAY + " was demoted to " + DARK_AQUA + group.getName() + GRAY + " in world " + DARK_AQUA + world.getName());
        user.pluginMessage(GRAY + "You were demoted to " + DARK_AQUA + group.getName() + GRAY + " in world " + DARK_AQUA + world.getName());
    }

    private void demoteTab(TabContext context) {
        Set<String> worlds = holder.getWorlds().keySet();
        context.playerCompletion(0);
        context.completionAt(1, worlds.toArray(new String[0]));
    }

    //
    //
    //
    //

    private void setRank(CommandContext context) {
        CoWorld world = holder.getWorld(context.argAt(2)) == null ? holder.getDefaultWorld() : holder.getWorld(context.argAt(2));
        CoUser user = holder.getUser((context.getArgs().size() < 3 ? holder.getDefaultWorld().getName() : context.argAt(2)), context.argAt(0));
        if (world == null) {
            context.pluginMessage(RED + "The world specified does not exist.");
            return;
        }
        Group group = world.getGroup(context.argAt(1));
        if (user == null) {
            context.pluginMessage(RED + "The user specified does not exist in the world specified");
            return;
        }
        if (group == null) {
            context.pluginMessage(RED + "The group specified does not exist in the world specified");
            return;
        }
        user.setGroup(world, group.getName());
        context.pluginMessage(DARK_AQUA + user.getName() + GRAY + " was added to group " + DARK_AQUA + group.getName() + GRAY + " in world " + DARK_AQUA + world.getName());
        user.pluginMessage(GRAY + "You were added to group " + DARK_AQUA + group.getName() + GRAY + " in world " + DARK_AQUA + world.getName());
    }

    private void setRankTab(TabContext context) {
        Set<String> groups = holder.getGroups().keySet();
        Set<String> worlds = holder.getWorlds().keySet();
        context.playerCompletion(0);
        context.completionAt(1, groups.toArray(new String[0]));
        context.completionAt(2, worlds.toArray(new String[0]));
    }

    //
    //
    //
    //
    @SuppressWarnings( "ConstantConditions" )
    private void setPrefix(CommandContext context) {
        CoUser user = holder.getUser((holder.getUser(context.argAt(0)) == null ? holder.getDefaultWorld().getName() : holder.getUser(context.argAt(0)).getWorld().getName()), context.argAt(0));
        if (user == null) {
            context.pluginMessage(RED + "The user specified does not exist in the world specified");
            return;
        }
        if (!context.getSender().hasPermission("coperms.variables.prefix.other") && !user.getName().equalsIgnoreCase(context.argAt(0))) {
            context.pluginMessage(RED + "You do not have permission for this command! Required Permission: " + GRAY + "coperms.variables.prefix.other");
            return;
        }
        if (context.getArgs().size() < 2) {
            user.setPrefix(null);
            context.pluginMessage(GRAY + "Prefix for " + DARK_AQUA + user.getName() + GRAY + " has been disabled.");
            user.pluginMessage(GRAY + "Your prefix has been disabled.");
            return;
        }
        user.setPrefix(context.joinArgs(1) + " ");
        context.pluginMessage(GRAY + "Prefix for " + DARK_AQUA + user.getName() + GRAY + " has been changed to " + DARK_AQUA + translateAlternateColorCodes('&', user.getPrefix()));
        user.pluginMessage(GRAY + "Your prefix has been changed to " + DARK_AQUA + translateAlternateColorCodes('&', user.getPrefix()));
    }

    private void setPrefixTab(TabContext context) {
        context.playerCompletion(0);
    }

    //
    //
    //
    //
    @SuppressWarnings( "ConstantConditions" )
    private void setSuffix(CommandContext context) {
        CoUser user = holder.getUser((holder.getUser(context.argAt(0)) == null ? holder.getDefaultWorld().getName() : holder.getUser(context.argAt(0)).getWorld().getName()), context.argAt(0));
        if (user == null) {
            context.pluginMessage(RED + "The user specified does not exist in the world specified");
            return;
        }
        if (!context.getSender().hasPermission("coperms.variables.suffix.other") && !user.getName().equalsIgnoreCase(context.argAt(0))) {
            context.pluginMessage(RED + "You do not have permission for this command! Required Permission: " + GRAY + "coperms.variables.suffix.other");
            return;
        }
        if (context.getArgs().size() < 2) {
            user.setSuffix(null);
            context.pluginMessage(GRAY + "Suffix for " + DARK_AQUA + user.getName() + GRAY + " has been disabled.");
            user.pluginMessage(GRAY + "Your suffix has been disabled.");
            return;
        }
        user.setSuffix(" " + context.joinArgs(1));
        context.pluginMessage(GRAY + "Suffix for " + DARK_AQUA + user.getName() + GRAY + " has been changed to " + DARK_AQUA + translateAlternateColorCodes('&', user.getSuffix()));
        user.pluginMessage(GRAY + "Your suffix has been changed to " + DARK_AQUA + translateAlternateColorCodes('&', user.getSuffix()));
    }

    private void setSuffixTab(TabContext context) {
        context.playerCompletion(0);
    }

    //
    //
    //
    //

    private void setGroupPrefix(CommandContext context) {
        CoWorld world = holder.getWorld(context.argAt(1)) == null ? holder.getDefaultWorld() : holder.getWorld(context.argAt(1));
        Group group = world.getGroup(context.argAt(0));
        if (group == null) {
            context.pluginMessage(RED + "The group specified does not exist in the world specified");
            return;
        }
        if (context.getArgs().size() < 3) {
            group.setPrefix(null);
            context.pluginMessage(GRAY + "Prefix for " + DARK_AQUA + group.getName() + GRAY + " has been disabled.");
            return;
        }
        group.setPrefix(context.joinArgs(2) + " ");
        context.pluginMessage(GRAY + "Prefix for " + DARK_AQUA + group.getName() + GRAY + " has been changed to " + DARK_AQUA + group.getPrefix());
    }

    private void groupVarChange(TabContext context) {
        Set<String> worlds = holder.getWorlds().keySet();
        Set<String> groups = holder.getGroups().keySet();
        context.completionAt(0, groups.toArray(new String[0]));
        context.completionAt(1, worlds.toArray(new String[0]));
    }

    //
    //
    //
    //

    private void setGroupSuffix(CommandContext context) {
        CoWorld world = holder.getWorld(context.argAt(1)) == null ? holder.getDefaultWorld() : holder.getWorld(context.argAt(1));
        Group group = world.getGroup(context.argAt(0));
        if (group == null) {
            context.pluginMessage(RED + "The group specified does not exist in the world specified");
            return;
        }
        if (context.getArgs().size() < 3) {
            group.setSuffix(null);
            context.pluginMessage(GRAY + "Suffix for " + DARK_AQUA + group.getName() + GRAY + " has been disabled.");
            return;
        }
        group.setSuffix(" " + context.joinArgs(2));
        context.pluginMessage(GRAY + "Suffix for " + DARK_AQUA + group.getName() + GRAY + " has been changed to" + DARK_AQUA + group.getSuffix());
    }
}

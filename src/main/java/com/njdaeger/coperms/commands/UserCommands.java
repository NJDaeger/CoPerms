package com.njdaeger.coperms.commands;

import com.njdaeger.coperms.CoPerms;
import com.njdaeger.coperms.DataHolder;
import com.njdaeger.coperms.data.CoUser;
import com.njdaeger.coperms.data.CoWorld;
import com.njdaeger.coperms.groups.Group;
import com.njdaeger.bci.base.BCICommand;
import com.njdaeger.bci.defaults.BCIBuilder;
import com.njdaeger.bci.defaults.CommandContext;
import com.njdaeger.bci.defaults.TabContext;

import java.util.Set;

import static org.bukkit.ChatColor.*;

public final class UserCommands {

    private final DataHolder holder;

    public UserCommands(CoPerms plugin, DataHolder holder) {
        this.holder = holder;
    
        BCICommand promote = BCIBuilder.create("promote")
                .executor(this::promote)
                .completer(this::promoteTab)
                .aliases("promo")
                .description("Promotes someone to the next rank.")
                .usage("/promote <user> [world]")
                .minArgs(1)
                .maxArgs(2)
                .permissions("coperms.ranks.promote")
                .build();
    
        BCICommand setRank = BCIBuilder.create("setrank")
                .executor(this::setRank)
                .completer(this::setRankTab)
                .aliases("setr", "setgroup")
                .description("Adds a user to a specified rank")
                .usage("/setrank <user> <rank> [world]") //Make a list in the UserDataFile for each user that is similar to the group data file that specifies ranks per world
                .minArgs(2)
                .maxArgs(3)
                .permissions("coperms.ranks.setrank")
                .build();
    
        BCICommand demote = BCIBuilder.create("demote")
                .executor(this::demote)
                .completer(this::demoteTab)
                .aliases("demo")
                .description("Demotes someone to the previous rank.")
                .usage("/demote <user> [world]")
                .minArgs(1)
                .maxArgs(2)
                .permissions("coperms.ranks.demote")
                .build();
    
        BCICommand setPrefix = BCIBuilder.create("setprefix")
                .executor(this::setPrefix)
                .completer(this::setPrefixTab)
                .aliases("prefix")
                .description("Adds or removes a prefix from a user")
                .usage("/prefix <user> [prefix]")
                .minArgs(1)
                .permissions("coperms.variables.user.prefix")
                .build();
    
        BCICommand setSuffix = BCIBuilder.create("setsuffix")
                .executor(this::setSuffix)
                .completer(this::setSuffixTab)
                .description("Adds or removes a suffix from a user")
                .usage("/suffix <user> [prefix]")
                .minArgs(1)
                .permissions("coperms.variables.user.suffix")
                .build();
    
        BCICommand setGPrefix = BCIBuilder.create("setgprefix")
                .executor(this::setGroupPrefix)
                .completer(this::groupVarChange)
                .description("Sets the prefix of a group.")
                .usage("/setgprefix <group> <world> [prefix]")
                .aliases("setgpre")
                .minArgs(2)
                .permissions("coperms.variables.group.prefix").build();
    
        BCICommand setGSuffix = BCIBuilder.create("setgsuffix")
                .executor(this::setGroupSuffix)
                .completer(this::groupVarChange)
                .description("Sets the suffix of a group.")
                .usage("/setgsuffix <group> <world> [suffix]")
                .aliases("setgsuf")
                .minArgs(2)
                .permissions("coperms.variables.group.suffix")
                .build();
        
        plugin.getCommandStore().registerCommands(promote, setRank, demote, setPrefix, setSuffix, setGPrefix, setGSuffix);
    }

    //
    //
    //
    //

    private void promote(CommandContext context) {
        CoWorld world = holder.getWorld(context.argAt(1)) == null ? holder.getDefaultWorld() : holder.getWorld(context.argAt(1));
        if (world == null) {
            context.pluginMessage(RED + "The world specified does not exist.");
            return;
        }
        CoUser user = world.getUser(context.argAt(0));
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
        context.pluginMessage(AQUA + user.getName() + GRAY + " was promoted to " + AQUA + group.getName() + GRAY + " in world " + AQUA + world.getName());
        user.pluginMessage(GRAY + "You were promoted to " + AQUA + group.getName() + GRAY + " in world " + AQUA + world.getName());
    }

    private void promoteTab(TabContext context) {
        Set<String> worlds = holder.getWorlds().keySet();
        context.playerCompletionAt(0);
        context.completionAt(1, worlds.toArray(new String[0]));
    }

    //
    //
    //
    //

    private void demote(CommandContext context) {
        CoWorld world = holder.getWorld(context.argAt(1)) == null ? holder.getDefaultWorld() : holder.getWorld(context.argAt(1));
        if (world == null) {
            context.pluginMessage(RED + "The world specified does not exist.");
            return;
        }
        CoUser user = world.getUser(context.argAt(0));
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
        context.pluginMessage(AQUA + user.getName() + GRAY + " was demoted to " + AQUA + group.getName() + GRAY + " in world " + AQUA + world.getName());
        user.pluginMessage(GRAY + "You were demoted to " + AQUA + group.getName() + GRAY + " in world " + AQUA + world.getName());
    }

    private void demoteTab(TabContext context) {
        Set<String> worlds = holder.getWorlds().keySet();
        context.playerCompletionAt(0);
        context.completionAt(1, worlds.toArray(new String[0]));
    }

    //
    //
    //
    //

    private void setRank(CommandContext context) {
        CoWorld world = holder.getWorld(context.argAt(2)) == null ? holder.getDefaultWorld() : holder.getWorld(context.argAt(2));
        if (world == null) {
            context.pluginMessage(RED + "The world specified does not exist.");
            return;
        }
        CoUser user = world.getUser(context.argAt(0));
        if (user == null) {
            context.pluginMessage(RED + "The user specified does not exist in the world specified");
            return;
        }
        Group group = world.getGroup(context.argAt(1));
        if (group == null) {
            context.pluginMessage(RED + "The group specified does not exist in the world specified");
            return;
        }
        user.setGroup(world, group.getName());
        context.pluginMessage(AQUA + user.getName() + GRAY + " was added to group " + AQUA + group.getName() + GRAY + " in world " + AQUA + world.getName());
        user.pluginMessage(GRAY + "You were added to group " + AQUA + group.getName() + GRAY + " in world " + AQUA + world.getName());
    }

    private void setRankTab(TabContext context) {
        Set<String> groups = holder.getGroups().keySet();
        Set<String> worlds = holder.getWorlds().keySet();
        context.playerCompletionAt(0);
        context.completionAt(1, groups.toArray(new String[0]));
        context.completionAt(2, worlds.toArray(new String[0]));
    }

    //
    //
    //
    //
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
            context.pluginMessage(GRAY + "Prefix for " + AQUA + user.getName() + GRAY + " has been disabled.");
            user.pluginMessage(GRAY + "Your prefix has been disabled.");
            return;
        }
        user.setPrefix(context.joinArgs(1) + " ");
        context.pluginMessage(GRAY + "Prefix for " + AQUA + user.getName() + GRAY + " has been changed to " + AQUA + translate(user.getPrefix()));
        user.pluginMessage(GRAY + "Your prefix has been changed to " + AQUA + translate(user.getPrefix()));
    }

    private void setPrefixTab(TabContext context) {
        context.playerCompletionAt(0);
    }

    //
    //
    //
    //
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
            context.pluginMessage(GRAY + "Suffix for " + AQUA + user.getName() + GRAY + " has been disabled.");
            user.pluginMessage(GRAY + "Your suffix has been disabled.");
            return;
        }
        user.setSuffix(" " + context.joinArgs(1));
        context.pluginMessage(GRAY + "Suffix for " + AQUA + user.getName() + GRAY + " has been changed to " + AQUA + translate(user.getSuffix()));
        user.pluginMessage(GRAY + "Your suffix has been changed to " + AQUA + translate(user.getSuffix()));
    }

    private void setSuffixTab(TabContext context) {
        context.playerCompletionAt(0);
    }

    //
    //
    //
    //

    private void setGroupPrefix(CommandContext context) {
        CoWorld world = holder.getWorld(context.argAt(1)) == null ? holder.getDefaultWorld() : holder.getWorld(context.argAt(1));
        if (world == null) {
            context.pluginMessage(RED + "The world specified does not exist.");
            return;
        }
        Group group = world.getGroup(context.argAt(0));
        if (group == null) {
            context.pluginMessage(RED + "The group specified does not exist in the world specified");
            return;
        }
        if (context.getArgs().size() < 3) {
            group.setPrefix(null);
            context.pluginMessage(GRAY + "Prefix for " + AQUA + group.getName() + GRAY + " has been disabled.");
            return;
        }
        group.setPrefix(context.joinArgs(2) + " ");
        context.pluginMessage(GRAY + "Prefix for " + AQUA + group.getName() + GRAY + " has been changed to " + AQUA + group.getPrefix());
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
        if (world == null) {
            context.pluginMessage(RED + "The world specified does not exist.");
            return;
        }
        Group group = world.getGroup(context.argAt(0));
        if (group == null) {
            context.pluginMessage(RED + "The group specified does not exist in the world specified");
            return;
        }
        if (context.getArgs().size() < 3) {
            group.setSuffix(null);
            context.pluginMessage(GRAY + "Suffix for " + AQUA + group.getName() + GRAY + " has been disabled.");
            return;
        }
        group.setSuffix(" " + context.joinArgs(2));
        context.pluginMessage(GRAY + "Suffix for " + AQUA + group.getName() + GRAY + " has been changed to" + AQUA + group.getSuffix());
    }
    
    private String translate(String message) {
        return translateAlternateColorCodes('&', message);
    }
    
}

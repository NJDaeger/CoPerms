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

import java.util.Set;

import static com.coalesce.core.command.defaults.DefaultProcessedCommand.builder;
import static com.coalesce.core.Color.*;

public final class UserCommands {

    private final DataHolder holder;

    public UserCommands(CoPerms plugin, DataHolder holder) {
        this.holder = holder;

        DefaultProcessedCommand promote = builder(plugin, "promote")
                .executor(this::promote)
                .completer(this::promoteTab)
                .aliases("promo")
                .description("Promotes someone to the next rank.")
                .usage("/promote <user> [world]")
                .minArgs(1)
                .maxArgs(2)
                .permission("coperms.ranks.promote")
                .build();
    
        DefaultProcessedCommand setRank = builder(plugin, "setrank")
                .executor(this::setRank)
                .completer(this::setRankTab)
                .aliases("setr", "setgroup")
                .description("Adds a user to a specified rank")
                .usage("/setrank <user> <rank> [world]") //Make a list in the UserDataFile for each user that is similar to the group data file that specifies ranks per world
                .minArgs(2)
                .maxArgs(3)
                .permission("coperms.ranks.setrank")
                .build();

        DefaultProcessedCommand demote = builder(plugin, "demote")
                .executor(this::demote)
                .completer(this::demoteTab)
                .aliases("demo")
                .description("Demotes someone to the previous rank.")
                .usage("/demote <user> [world]")
                .minArgs(1)
                .maxArgs(2)
                .permission("coperms.ranks.demote")
                .build();

        DefaultProcessedCommand setPrefix = builder(plugin, "setprefix")
                .executor(this::setPrefix)
                .completer(this::setPrefixTab)
                .aliases("prefix")
                .description("Adds or removes a prefix from a user")
                .usage("/prefix <user> [prefix]")
                .minArgs(1)
                .permission("coperms.variables.user.prefix")
                .build();

        DefaultProcessedCommand setSuffix = builder(plugin, "setsuffix")
                .executor(this::setSuffix)
                .completer(this::setSuffixTab)
                .description("Adds or removes a suffix from a user")
                .usage("/suffix <user> [prefix]")
                .minArgs(1)
                .permission("coperms.variables.user.suffix")
                .build();
    
        DefaultProcessedCommand setGPrefix = builder(plugin, "setgprefix")
                .executor(this::setGroupPrefix)
                .completer(this::groupVarChange)
                .description("Sets the prefix of a group.")
                .usage("/setgprefix <group> <world> [prefix]")
                .aliases("setgpre")
                .minArgs(2)
                .permission("coperms.variables.group.prefix").build();
    
        DefaultProcessedCommand setGSuffix = builder(plugin, "setgsuffix")
                .executor(this::setGroupSuffix)
                .completer(this::groupVarChange)
                .description("Sets the suffix of a group.")
                .usage("/setgsuffix <group> <world> [suffix]")
                .aliases("setgsuf")
                .minArgs(2)
                .permission("coperms.variables.group.suffix")
                .build();
        
        plugin.getCommandStore().registerCommands(promote, setRank, demote, setPrefix, setSuffix, setGPrefix, setGSuffix);
    }

    //
    //
    //
    //

    private void promote(DefaultCContext<DummyLang> context) {
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
        context.pluginMessage(AQUA + user.getName() + SILVER + " was promoted to " + AQUA + group.getName() + SILVER + " in world " + AQUA + world.getName());
        user.pluginMessage(SILVER + "You were promoted to " + AQUA + group.getName() + SILVER + " in world " + AQUA + world.getName());
    }

    private void promoteTab(DefaultTContext<DummyLang> context) {
        Set<String> worlds = holder.getWorlds().keySet();
        context.playerCompletion(0);
        context.completionAt(1, worlds.toArray(new String[0]));
    }

    //
    //
    //
    //

    private void demote(DefaultCContext<DummyLang> context) {
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
        context.pluginMessage(AQUA + user.getName() + SILVER + " was demoted to " + AQUA + group.getName() + SILVER + " in world " + AQUA + world.getName());
        user.pluginMessage(SILVER + "You were demoted to " + AQUA + group.getName() + SILVER + " in world " + AQUA + world.getName());
    }

    private void demoteTab(DefaultTContext<DummyLang> context) {
        Set<String> worlds = holder.getWorlds().keySet();
        context.playerCompletion(0);
        context.completionAt(1, worlds.toArray(new String[0]));
    }

    //
    //
    //
    //

    private void setRank(DefaultCContext<DummyLang> context) {
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
        context.pluginMessage(AQUA + user.getName() + SILVER + " was added to group " + AQUA + group.getName() + SILVER + " in world " + AQUA + world.getName());
        user.pluginMessage(SILVER + "You were added to group " + AQUA + group.getName() + SILVER + " in world " + AQUA + world.getName());
    }

    private void setRankTab(DefaultTContext<DummyLang> context) {
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
    private void setPrefix(DefaultCContext<DummyLang> context) {
        CoUser user = holder.getUser((holder.getUser(context.argAt(0)) == null ? holder.getDefaultWorld().getName() : holder.getUser(context.argAt(0)).getWorld().getName()), context.argAt(0));
        if (user == null) {
            context.pluginMessage(RED + "The user specified does not exist in the world specified");
            return;
        }
        if (!context.getSender().hasPermission("coperms.variables.prefix.other") && !user.getName().equalsIgnoreCase(context.argAt(0))) {
            context.pluginMessage(RED + "You do not have permission for this command! Required Permission: " + SILVER + "coperms.variables.prefix.other");
            return;
        }
        if (context.getArgs().size() < 2) {
            user.setPrefix(null);
            context.pluginMessage(SILVER + "Prefix for " + AQUA + user.getName() + SILVER + " has been disabled.");
            user.pluginMessage(SILVER + "Your prefix has been disabled.");
            return;
        }
        user.setPrefix(context.joinArgs(1) + " ");
        context.pluginMessage(SILVER + "Prefix for " + AQUA + user.getName() + SILVER + " has been changed to " + AQUA + translate(user.getPrefix()));
        user.pluginMessage(SILVER + "Your prefix has been changed to " + AQUA + translate(user.getPrefix()));
    }

    private void setPrefixTab(DefaultTContext<DummyLang> context) {
        context.playerCompletion(0);
    }

    //
    //
    //
    //
    private void setSuffix(DefaultCContext<DummyLang> context) {
        CoUser user = holder.getUser((holder.getUser(context.argAt(0)) == null ? holder.getDefaultWorld().getName() : holder.getUser(context.argAt(0)).getWorld().getName()), context.argAt(0));
        if (user == null) {
            context.pluginMessage(RED + "The user specified does not exist in the world specified");
            return;
        }
        if (!context.getSender().hasPermission("coperms.variables.suffix.other") && !user.getName().equalsIgnoreCase(context.argAt(0))) {
            context.pluginMessage(RED + "You do not have permission for this command! Required Permission: " + SILVER + "coperms.variables.suffix.other");
            return;
        }
        if (context.getArgs().size() < 2) {
            user.setSuffix(null);
            context.pluginMessage(SILVER + "Suffix for " + AQUA + user.getName() + SILVER + " has been disabled.");
            user.pluginMessage(SILVER + "Your suffix has been disabled.");
            return;
        }
        user.setSuffix(" " + context.joinArgs(1));
        context.pluginMessage(SILVER + "Suffix for " + AQUA + user.getName() + SILVER + " has been changed to " + AQUA + translate(user.getSuffix()));
        user.pluginMessage(SILVER + "Your suffix has been changed to " + AQUA + translate(user.getSuffix()));
    }

    private void setSuffixTab(DefaultTContext<DummyLang> context) {
        context.playerCompletion(0);
    }

    //
    //
    //
    //

    private void setGroupPrefix(DefaultCContext<DummyLang> context) {
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
            context.pluginMessage(SILVER + "Prefix for " + AQUA + group.getName() + SILVER + " has been disabled.");
            return;
        }
        group.setPrefix(context.joinArgs(2) + " ");
        context.pluginMessage(SILVER + "Prefix for " + AQUA + group.getName() + SILVER + " has been changed to " + AQUA + group.getPrefix());
    }

    private void groupVarChange(DefaultTContext<DummyLang> context) {
        Set<String> worlds = holder.getWorlds().keySet();
        Set<String> groups = holder.getGroups().keySet();
        context.completionAt(0, groups.toArray(new String[0]));
        context.completionAt(1, worlds.toArray(new String[0]));
    }

    //
    //
    //
    //

    private void setGroupSuffix(DefaultCContext<DummyLang> context) {
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
            context.pluginMessage(SILVER + "Suffix for " + AQUA + group.getName() + SILVER + " has been disabled.");
            return;
        }
        group.setSuffix(" " + context.joinArgs(2));
        context.pluginMessage(SILVER + "Suffix for " + AQUA + group.getName() + SILVER + " has been changed to" + AQUA + group.getSuffix());
    }
}

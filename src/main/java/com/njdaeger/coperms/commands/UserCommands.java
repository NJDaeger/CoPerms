package com.njdaeger.coperms.commands;

import com.njdaeger.bci.base.BCICommand;
import com.njdaeger.bci.base.BCIException;
import com.njdaeger.bci.defaults.BCIBuilder;
import com.njdaeger.bci.defaults.CommandContext;
import com.njdaeger.coperms.CoPerms;
import com.njdaeger.coperms.commands.flags.WorldFlag;
import com.njdaeger.coperms.data.CoUser;
import com.njdaeger.coperms.data.CoWorld;
import com.njdaeger.coperms.exceptions.GroupNotExistException;
import com.njdaeger.coperms.exceptions.UserNotExistException;
import com.njdaeger.coperms.exceptions.WorldNotExistException;
import com.njdaeger.coperms.groups.Group;

import static org.bukkit.ChatColor.*;

public final class UserCommands {

    public UserCommands(CoPerms plugin) {

        BCICommand promote = BCIBuilder.create("promote")
                .executor(this::promote)
                .completer((c) -> c.completionAt(0, CommandUtil::playerCompletion))
                .flag(new WorldFlag())
                .aliases("promo")
                .description("Promotes someone to the next rank.")
                .usage("/promote <user> w:[world]")
                .minArgs(1)
                .maxArgs(2)
                .permissions("coperms.ranks.promote")
                .build();

        BCICommand setRank = BCIBuilder.create("setrank")
                .executor(this::setRank)
                .completer((c) -> {
                    c.completionAt(0, CommandUtil::playerCompletion);
                    c.completionAt(1, CommandUtil::groupCompletion);
                })
                .flag(new WorldFlag())
                .aliases("setr", "setgroup")
                .description("Adds a user to a specified rank")
                .usage("/setrank <user> <rank> w:[world]")
                .minArgs(2)
                .maxArgs(3)
                .permissions("coperms.ranks.setrank")
                .build();
    
        BCICommand demote = BCIBuilder.create("demote")
                .executor(this::demote)
                .completer((c) -> c.completionAt(0, CommandUtil::playerCompletion))
                .flag(new WorldFlag())
                .aliases("demo")
                .description("Demotes someone to the previous rank.")
                .usage("/demote <user> w:[world]")
                .minArgs(1)
                .maxArgs(2)
                .permissions("coperms.ranks.demote")
                .build();
    
        BCICommand setPrefix = BCIBuilder.create("setprefix")
                .executor(this::setPrefix)
                .completer((c) -> c.completionAt(0, CommandUtil::playerCompletion))
                .aliases("prefix")
                .description("Adds or removes a prefix from a user")
                .usage("/prefix <user> w:[world] [prefix]")
                .flag(new WorldFlag())
                .minArgs(1)
                .permissions("coperms.variables.user.prefix")
                .build();
    
        BCICommand setSuffix = BCIBuilder.create("setsuffix")
                .executor(this::setSuffix)
                .completer((c) -> c.completionAt(0, CommandUtil::playerCompletion))
                .aliases("suffix")
                .description("Adds or removes a suffix from a user")
                .usage("/suffix <user> w:[world] [suffix]")
                .flag(new WorldFlag())
                .minArgs(1)
                .permissions("coperms.variables.user.suffix")
                .build();
    
        BCICommand setGPrefix = BCIBuilder.create("setgprefix")
                .executor(this::setGroupPrefix)
                .completer((c) -> c.completionAt(0, CommandUtil::groupCompletion))
                .flag(new WorldFlag())
                .aliases("gprefix", "setgpre")
                .description("Sets the prefix of a group.")
                .usage("/setgprefix <group> w:[world] [prefix]")
                .minArgs(2)
                .permissions("coperms.variables.group.prefix").build();
    
        BCICommand setGSuffix = BCIBuilder.create("setgsuffix")
                .executor(this::setGroupSuffix)
                .completer((c) -> c.completionAt(0, CommandUtil::groupCompletion))
                .flag(new WorldFlag())
                .description("Sets the suffix of a group.")
                .usage("/setgsuffix <group> w:[world] [suffix]")
                .aliases("gsuffix", "setgsuf")
                .minArgs(2)
                .permissions("coperms.variables.group.suffix")
                .build();
        
        plugin.getCommandStore().registerCommands(promote, setRank, demote, setPrefix, setSuffix, setGPrefix, setGSuffix);
    }

    //
    //
    //
    //

    //promote command
    private void promote(CommandContext context) throws BCIException {

        CoWorld world = context.hasFlag("w") ? context.getFlag("w") : CommandUtil.resolveWorld(context);
        if (world == null) throw new WorldNotExistException();
        
        CoUser user = world.getUser(context.argAt(0));
        if (user == null) throw new UserNotExistException();
        
        Group group = world.getGroup(user.getGroup().getRankID() + 1);
        if (group == null) throw new GroupNotExistException();
        
        user.setGroup(world, group.getName());
        context.pluginMessage(AQUA + user.getName() + GRAY + " was promoted to " + AQUA + group.getName() + GRAY + " in world " + AQUA + world.getName());
        user.pluginMessage(GRAY + "You were promoted to " + AQUA + group.getName() + GRAY + " in world " + AQUA + world.getName());
    }

    //Demote command
    private void demote(CommandContext context) throws BCIException {

        CoWorld world = context.hasFlag("w") ? context.getFlag("w") : CommandUtil.resolveWorld(context);
        if (world == null) throw new WorldNotExistException();
        
        CoUser user = world.getUser(context.argAt(0));
        if (user == null) throw new UserNotExistException();
        
        Group group = world.getGroup(user.getGroup().getRankID() - 1);
        if (group == null) throw new GroupNotExistException();
        
        user.setGroup(world, group.getName());
        context.pluginMessage(AQUA + user.getName() + GRAY + " was demoted to " + AQUA + group.getName() + GRAY + " in world " + AQUA + world.getName());
        user.pluginMessage(GRAY + "You were demoted to " + AQUA + group.getName() + GRAY + " in world " + AQUA + world.getName());
    }

    //
    //
    //
    //

    private void setRank(CommandContext context) throws BCIException {
        
        CoWorld world = context.hasFlag("w") ? context.getFlag("w") : CommandUtil.resolveWorld(context);
        if (world == null) throw new WorldNotExistException();
        
        CoUser user = world.getUser(context.argAt(0));
        if (user == null) throw new UserNotExistException();
        
        Group group = world.getGroup(context.argAt(1));
        if (group == null) throw new GroupNotExistException();
        
        user.setGroup(world, group.getName());
        context.pluginMessage(AQUA + user.getName() + GRAY + " was added to group " + AQUA + group.getName() + GRAY + " in world " + AQUA + world.getName());
        user.pluginMessage(GRAY + "You were added to group " + AQUA + group.getName() + GRAY + " in world " + AQUA + world.getName());
    }

    //
    //
    //
    //

    //User prefix
    private void setPrefix(CommandContext context) throws BCIException {
        
        CoWorld world = context.hasFlag("w") ? context.getFlag("w") : CommandUtil.resolveWorld(context);
        if (world == null) throw new WorldNotExistException();
        
        CoUser user = world.getUser(context.argAt(0));
        if (user == null) throw new UserNotExistException();
        
        if (!context.hasPermission("coperms.variables.prefix.other") && !user.getName().equalsIgnoreCase(context.argAt(0))) context.noPermission();
        
        if (context.getLength() < 2) {
            user.setPrefix(null);
            context.pluginMessage(GRAY + "Prefix for " + AQUA + user.getName() + GRAY + " has been disabled.");
            user.pluginMessage(GRAY + "Your prefix has been disabled.");
            return;
        }
        user.setPrefix(context.joinArgs(1) + " ");
        context.pluginMessage(GRAY + "Prefix for " + AQUA + user.getName() + GRAY + " has been changed to " + AQUA + translate(user.getPrefix()));
        user.pluginMessage(GRAY + "Your prefix has been changed to " + AQUA + translate(user.getPrefix()));
    }

    //User suffix
    private void setSuffix(CommandContext context) throws BCIException {
    
        CoWorld world = context.hasFlag("w") ? context.getFlag("w") : CommandUtil.resolveWorld(context);
        if (world == null) throw new WorldNotExistException();
    
        CoUser user = world.getUser(context.argAt(0));
        if (user == null) throw new UserNotExistException();
        
        if (!context.hasPermission("coperms.variables.suffix.other") && !user.getName().equalsIgnoreCase(context.argAt(0))) context.noPermission();

        if (context.getLength() < 2) {
            user.setSuffix(null);
            context.pluginMessage(GRAY + "Suffix for " + AQUA + user.getName() + GRAY + " has been disabled.");
            user.pluginMessage(GRAY + "Your suffix has been disabled.");
            return;
        }
        user.setSuffix(" " + context.joinArgs(1));
        context.pluginMessage(GRAY + "Suffix for " + AQUA + user.getName() + GRAY + " has been changed to " + AQUA + translate(user.getSuffix()));
        user.pluginMessage(GRAY + "Your suffix has been changed to " + AQUA + translate(user.getSuffix()));
    }

    //
    //
    //
    //

    //Group prefix
    private void setGroupPrefix(CommandContext context) throws BCIException {
    
        CoWorld world = context.hasFlag("w") ? context.getFlag("w") : CommandUtil.resolveWorld(context);
        if (world == null) throw new WorldNotExistException();
        
        Group group = world.getGroup(context.argAt(0));
        if (group == null) throw new GroupNotExistException();
        
        if (context.getLength() < 3) {
            group.setPrefix(null);
            context.pluginMessage(GRAY + "Prefix for " + AQUA + group.getName() + GRAY + " has been disabled.");
            return;
        }
        group.setPrefix(context.joinArgs(2) + " ");
        context.pluginMessage(GRAY + "Prefix for " + AQUA + group.getName() + GRAY + " has been changed to " + AQUA + group.getPrefix());
    }

    //Group suffix
    private void setGroupSuffix(CommandContext context) throws BCIException {
    
        CoWorld world = context.hasFlag("w") ? context.getFlag("w") : CommandUtil.resolveWorld(context);
        if (world == null) throw new WorldNotExistException();
        
        Group group = world.getGroup(context.argAt(0));
        if (group == null) throw new GroupNotExistException();
        
        if (context.getLength() < 3) {
            group.setSuffix(null);
            context.pluginMessage(GRAY + "Suffix for " + AQUA + group.getName() + GRAY + " has been disabled.");
            return;
        }
        group.setSuffix(" " + context.joinArgs(2));
        context.pluginMessage(GRAY + "Suffix for " + AQUA + group.getName() + GRAY + " has been changed to" + AQUA + group.getSuffix());
    }

    //
    //
    //
    //

    private String translate(String message) {
        return translateAlternateColorCodes('&', message);
    }

}

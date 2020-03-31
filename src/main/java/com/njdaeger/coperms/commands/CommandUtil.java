package com.njdaeger.coperms.commands;

import com.njdaeger.coperms.CoPerms;
import com.njdaeger.coperms.data.CoUser;
import com.njdaeger.coperms.data.CoWorld;
import com.njdaeger.pdk.command.CommandContext;
import com.njdaeger.pdk.command.TabContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class CommandUtil {

    public static List<String> playerCompletion(TabContext context) {
        if (context.isPlayer()) return (context.hasFlag("w") ? (CoWorld)context.getFlag("w") : CoPerms.getInstance().getWorld(context.asPlayer().getWorld())).getUsers().values().stream().map(CoUser::getName).collect(Collectors.toList());
        else {
            if (context.hasFlag("w")) return ((CoWorld)context.getFlag("w")).getUsers().values().stream().map(CoUser::getName).collect(Collectors.toList());
            else return CoPerms.getInstance().getWorlds().values().stream().flatMap(world -> world.getUsers().values().stream().map(CoUser::getName)).collect(Collectors.toList());
        }
    }

    public static CoWorld resolveWorld(CommandContext context) {
        if (!context.isLocatable()) return CoPerms.getInstance().getDefaultWorld();
        else return CoPerms.getInstance().getWorld(Objects.requireNonNull(context.getLocation().getWorld()));
    }

    public static List<String> groupCompletion(TabContext context) {
        if (context.isPlayer()) return new ArrayList<>((context.hasFlag("w") ? (CoWorld)context.getFlag("w") : CoPerms.getInstance().getWorld(context.asPlayer().getWorld())).getGroups().keySet());
        else {
            if (context.hasFlag("w")) return new ArrayList<>(((CoWorld)context.getFlag("w")).getGroups().keySet());
            else return CoPerms.getInstance().getWorlds().values().stream().flatMap(world -> world.getGroups().keySet().stream()).collect(Collectors.toList());
        }
    }

    public static List<String> allGroupCompletion(TabContext context) {
        List<String> groups = groupCompletion(context);
        groups.addAll(CoPerms.getInstance().getSuperGroups().keySet());
        return groups;
    }



}

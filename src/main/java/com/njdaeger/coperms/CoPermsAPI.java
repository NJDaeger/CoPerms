package com.njdaeger.coperms;

import com.njdaeger.coperms.configuration.CoPermsConfig;
import com.njdaeger.coperms.configuration.SuperDataFile;
import com.njdaeger.coperms.data.CoUser;
import com.njdaeger.coperms.data.CoWorld;
import com.njdaeger.coperms.groups.Group;
import com.njdaeger.coperms.groups.SuperGroup;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

public interface CoPermsAPI extends Plugin {

    CoWorld getDefaultWorld();

    CoUser getUser(@NotNull World world,@NotNull UUID uuid);

    CoUser getUser(@NotNull World world, @NotNull String name);

    CoUser getUser(@NotNull String world, @NotNull UUID uuid);

    CoUser getUser(@NotNull String world, @NotNull String name);

    CoUser getOnlineUser(@NotNull UUID uuid);

    CoUser getOnlineUser(@NotNull String name);

    CoWorld getWorld(@NotNull World world);

    CoWorld getWorld(@NotNull String world);

    Map<String, CoWorld> getWorlds();

    Map<UUID, CoUser> getUsers(@NotNull World world);

    Map<UUID, CoUser> getUsers(@NotNull String world);

    Group getGroup(@NotNull World world, String name);

    Group getGroup(@NotNull World world, int id);

    Group getGroup(@NotNull String world, String name);

    Group getGroup(@NotNull String world, int id);

    Map<String, Group> getGroups(@NotNull World world);

    Map<String, Group> getGroups(@NotNull String world);

    SuperGroup getSuperGroup(String name);

    Map<String, SuperGroup> getSuperGroups();

    /**
     * Gets CoPerms' configuration.
     *
     * @return CoPerm's configuration.
     */
    CoPermsConfig getPermsConfig();

    /**
     * Returns the super data file.
     *
     * @return The super data file.
     */
    SuperDataFile getSuperDataFile();

}

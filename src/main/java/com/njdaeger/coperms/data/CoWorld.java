package com.njdaeger.coperms.data;

import com.njdaeger.coperms.CoPerms;
import com.njdaeger.coperms.configuration.GroupDataFile;
import com.njdaeger.coperms.configuration.UserDataFile;
import com.njdaeger.coperms.groups.Group;
import org.apache.commons.lang.Validate;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings({"unused", "WeakerAccess"})
public final class CoWorld {

    private final World world;
    private final UserDataFile userData;
    private final GroupDataFile groupData;
    private final Map<UUID, CoUser> users;

    public CoWorld(World world, UserDataFile userData, GroupDataFile groupData) {
        this.world = world;
        this.users = new HashMap<>();
        this.userData = userData;
        this.groupData = groupData;

        userData.getUsers().forEach(uuid -> users.putIfAbsent(uuid, new CoUser((CoPerms) userData.getPlugin(), this, uuid)));
    }

    /**
     * Gets the world represented by this CoWorld
     *
     * @return The world
     */
    public World getWorld() {
        return world;
    }

    /**
     * Gets the name of the world
     *
     * @return The name of the world
     */
    public String getName() {
        return getWorld().getName();
    }

    /**
     * Gets the user data file this world uses
     *
     * @return The worlds user data file.
     */
    public UserDataFile getUserDataFile() {
        return userData;
    }

    /**
     * Gets the group data file this world uses
     *
     * @return The worlds group data file
     */
    public GroupDataFile getGroupDataFile() {
        return groupData;
    }

    public CoUser getUser(@NotNull UUID uuid) {
        return users.get(uuid);
    }

    public CoUser getUser(@NotNull String name) {
        return users.get(userData.getUserId(name));
    }

    public boolean hasUser(@NotNull UUID uuid) {
        return users.containsKey(uuid);
    }

    public boolean hasUser(@NotNull String name) {
        return users.containsKey(userData.getUserId(name));
    }

    public Map<UUID, CoUser> getUsers() {
        return users;
    }

    /**
     * Gets a group from this world
     *
     * @param name The name of the group to find
     * @return The group
     */
    public Group getGroup(@NotNull String name) {
        Validate.notNull(name, "Name cannot be null");
        return groupData.getGroup(name);
    }

    /**
     * Gets a rank via ID
     *
     * @param id The id of the rank
     * @return The rank if it exists
     */
    public Group getGroup(int id) {
        Validate.isTrue(id >= 0, "Group ID must be greater than or equal to 0. Given: ", id);
        return groupData.getGroup(id);
    }
    
    /**
     * Check if this world has a group
     * @param name The name of the group to look for
     * @return True if this world has the group, false otherwise.
     */
    public boolean hasGroup(@NotNull String name) {
        Validate.notNull(name, "Name cannot be null");
        return getGroup(name) != null;
    }

    public boolean hasGroup(int id) {
        return getGroup(id) != null;
    }

    /**
     * Gets a list of all the groups from this world.
     * @return All the groups in this world.
     */
    public Map<String, Group> getGroups() {
        return groupData.getGroups();
    }

    /**
     * Gets the default group of this world
     *
     * @return The worlds default group
     */
    public Group getDefaultGroup() {
        return groupData.getDefaultGroup();
    }
    
    /**
     * Loads a user into this world
     * @param player The user to load
     */
    public void addPlayer(@NotNull Player player) {
        Validate.notNull(player, "Player cannot be null");
        userData.loadPlayer(player);
        users.putIfAbsent(player.getUniqueId(), new CoUser((CoPerms) userData.getPlugin(), this, player.getUniqueId()));
    }
}

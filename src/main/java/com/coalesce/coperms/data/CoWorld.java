package com.coalesce.coperms.data;

import com.coalesce.coperms.CoPerms;
import com.coalesce.coperms.DataLoader;
import com.coalesce.coperms.configuration.GroupDataFile;
import com.coalesce.coperms.configuration.UserDataFile;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.*;

public final class CoWorld {

    private final World world;
    private final CoPerms plugin;
    private final UserDataFile userData;
    private final GroupDataFile groupData;
    private final Map<UUID, CoUser> users;
    private final Map<String, Group> groups;
    private final Map<Integer, String> rankID;

    public CoWorld(CoPerms plugin, World world, UserDataFile userData, GroupDataFile groupData, DataLoader loader) {
        this.world = world;
        this.plugin = plugin;
        this.userData = userData;
        this.groupData = groupData;
        this.users = new HashMap<>();
        this.groups = new HashMap<>();
        this.rankID = new HashMap<>();


        groupData.getSection("groups").getKeys(false).forEach(key -> groups.put(key.toLowerCase(), new Group(plugin, this, key, loader)));
        groups.values().forEach(Group::loadInheritanceTree);
        groups.values().forEach(g -> rankID.put(g.getRankID(), g.getName()));
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

    /**
     * Gets a user from this world
     *
     * @param uuid The user to find
     * @return The user if in this world, null otherwise.
     */
    public CoUser getUser(UUID uuid) {
        if (plugin.getDataHolder().getUser(uuid) != null) {
            return users.get(uuid);
        }
        if (hasUser(uuid)) {
            CoUser user = new CoUser(plugin, uuid, false);
            user.load(this);
            return user;
        }
        return null;
    }

    /**
     * Gets a user via their name
     *
     * @param name The name of the user
     * @return The user online or not
     */
    public CoUser getUser(String name) {
        if (plugin.getDataHolder().getUser(name) != null) {
            return users.get(Bukkit.getPlayer(name).getUniqueId());
        } else if (hasUser(name)) {
            CoUser user = new CoUser(plugin, resolveID(name), false);
            user.load(this);
            return user;
        }
        return null;
    }

    /**
     * Checks if the user data file of this world has a user in it.
     *
     * @param uuid The user to look for
     * @return True if the user has been in this world, false otherwise.
     */
    public boolean hasUser(UUID uuid) {
        return getUser(uuid) != null || userData.getSection("users").contains(uuid.toString());
    }

    /**
     * Checks if the user data file of this world has a user in it.
     *
     * @param name The user to look for
     * @return True if the user has been in this world, false otherwise.
     */
    public boolean hasUser(String name) {
        for (UUID id : getAllUsers()) {
            if (userData.getSection("users").getEntry(id.toString() + ".username").getString().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    private UUID resolveID(String name) {
        for (UUID id : getAllUsers()) {
            if (userData.getSection("users").getEntry(id.toString() + ".username").getString().equalsIgnoreCase(name)) {
                return id;
            }
        }
        return null;
    }

    /**
     * Gets a map of all the users in this world
     *
     * @return The worlds user map
     */
    public Map<UUID, CoUser> getUsers() {
        return users;
    }

    /**
     * Gets all the users that exist in the userdata file
     *
     * @return All the users. Online or offline
     */
    public Set<UUID> getAllUsers() {
        Set<UUID> users = new HashSet<>();
        userData.getSection("users").getKeys(false).forEach(k -> users.add(UUID.fromString(k)));
        return users;
    }

    /**
     * Gets a group from this world
     *
     * @param name The name of the group to find
     * @return The group
     */
    public Group getGroup(String name) {
        return groups.get(name.toLowerCase());
    }

    /**
     * Gets a rank via ID
     *
     * @param id The id of the rank
     * @return The rank if it exists
     */
    public Group getGroup(int id) {
        if (rankID.get(id) == null) {
            return null;
        }
        return getGroup(rankID.get(id));
    }

    /**
     * Gets a map of all the groups in this world
     *
     * @return The group map
     */
    public Map<String, Group> getGroups() {
        return groups;
    }

    /**
     * Gets the default group of this world
     *
     * @return The worlds default group
     */
    public Group getDefaultGroup() {
        for (Group group : groups.values()) {
            if (group.isDefault()) {
                return group;
            }
        }
        return null;
    }

    /**
     * Loads a user to this world.
     *
     * @param user The user to load.
     *             <p>
     *             <p>FOR INTERNAL USE ONLY</p>
     */
    public void loadUser(CoUser user) {
        this.userData.loadUser(user.getUserID());
        this.users.put(user.getUserID(), user);
        user.load(this);
    }

    /**
     * Unloads a user from this world.
     *
     * @param user The user to unload
     *             <p>
     *             <p>FOR INTERNAL USE ONLY</p>
     */
    public void unloadUser(CoUser user) {
        user.unload();
        if (!users.containsKey(user.getUserID())) {
            return;
        }
        this.users.remove(user.getUserID());
    }
}

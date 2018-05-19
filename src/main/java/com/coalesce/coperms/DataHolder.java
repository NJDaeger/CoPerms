package com.coalesce.coperms;

import com.coalesce.coperms.data.CoUser;
import com.coalesce.coperms.data.CoWorld;
import com.coalesce.coperms.data.Group;
import com.coalesce.coperms.data.SuperGroup;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings({"unused", "UnusedReturnValue", "WeakerAccess"})
public class DataHolder {

    private final CoPerms plugin;
    private final Map<UUID, CoUser> users;
    private final Map<String, Group> groups;
    private final Map<String, CoWorld> worlds;
    private final Map<String, SuperGroup> supers;

    public DataHolder(DataLoader dataloader, CoPerms plugin) {
        this.plugin = plugin;
        this.users = new HashMap<>();
        this.supers = dataloader.getSuperGroups();
        this.worlds = dataloader.getWorlds();
        this.groups = new HashMap<>();

        worlds.forEach((name, world) -> world.getGroups().forEach(groups::putIfAbsent));
    }

    /**
     * Gets the default world of the server
     *
     * @return The servers default world
     */
    public CoWorld getDefaultWorld() {
        return getWorld(Bukkit.getWorlds().get(0));
    }

    /**
     * Gets a user from a world user file.
     *
     * @param world The world to get the user from.
     * @param user  The user to get.
     * @return The user.
     * <p>
     * <p>Note: The user can be offline
     */
    public CoUser getUser(World world, UUID user) {
        return getWorld(world).getUser(user);
    }

    /**
     * Gets a user from a world user file.
     *
     * @param world The world to get the user from.
     * @param user  The user to get.
     * @return The user.
     * <p>
     * <p>Note: The user can be offline
     */
    public CoUser getUser(World world, String user) {
        return getWorld(world).getUser(user);
    }

    /**
     * Gets a user from a world user file
     *
     * @param world The world to get the user from
     * @param user  The user to get.
     * @return The user.
     * <p>
     * <p>Note: the user can be offline
     */
    public CoUser getUser(String world, UUID user) {
        return getWorld(world).getUser(user);
    }

    /**
     * Gets a user from a world user file.
     *
     * @param world The world to get the user from.
     * @param user  The user to get.
     * @return The user.
     * <p>
     * <p>Note: The user can be offline
     */
    public CoUser getUser(String world, String user) {
        return getWorld(world).getUser(user);
    }

    /**
     * Gets an online user
     *
     * @param uuid The user to get
     * @return The user
     */
    public CoUser getUser(UUID uuid) {
        if (!users.containsKey(uuid)) {
            return null;
        }
        return users.get(uuid);
    }

    /**
     * Gets an online user by name
     *
     * @param name The name of the user
     * @return the user
     */
    public CoUser getUser(String name) {
        if (Bukkit.getPlayer(name) == null) return null;
        return users.get(Bukkit.getPlayer(name).getUniqueId());
    }

    /**
     * Gets a world via the Bukkit world object
     *
     * @param world The world to get
     * @return The corresponding CoWorld
     */
    public CoWorld getWorld(World world) {
        return worlds.get(world.getName());
    }

    /**
     * Gets a world via its name
     *
     * @param name The name of the world to get
     * @return The corresponding CoWorld
     */
    public CoWorld getWorld(String name) {
        return worlds.get(name);
    }

    /**
     * A map of all the world names and the corresponding CoWorlds
     *
     * @return The current CoWorlds loaded.
     */
    public Map<String, CoWorld> getWorlds() {
        return worlds;
    }

    /**
     * A map of all the user UUID's and the corresponding CoUsers
     *
     * @return The current CoUsers loaded.
     */
    public Map<UUID, CoUser> getUsers() {
        return users;
    }

    /**
     * Gets a group from a world.
     *
     * @param world The world to get the group from
     * @param name  The name of the group
     * @return The group if exists
     */
    public Group getGroup(World world, String name) {
        return getWorld(world).getGroup(name);
    }

    /**
     * Gets a group via name
     *
     * @param name The name of the group
     * @return The group if exists
     */
    public Group getGroup(String name) {
        if (!groups.containsKey(name)) {
            return null;
        }
        return groups.get(name);
    }

    /**
     * Gets a map of all the groups
     *
     * @return All the groups loaded.
     */
    public Map<String, Group> getGroups() {
        return groups;
    }

    /**
     * Gets a SuperGroup if exists
     *
     * @param name The name of the SuperGroup
     * @return The SuperGroup
     */
    public SuperGroup getSuperGroup(String name) {
        if (!supers.containsKey(name)) {
            return null;
        }
        return supers.get(name.toLowerCase());
    }

    /**
     * Gets a list of all the SuperGroups
     *
     * @return The server SuperGroups
     */
    public Map<String, SuperGroup> getSuperGroups() {
        return supers;
    }

    /**
     * Loads a user into a world
     *
     * @param world  The world to load the user into
     * @param userID The user to load
     * @return The loaded user
     */

    //This should only ever load a user that is online.
    public CoUser loadUser(World world, UUID userID) {
        CoUser user = getUser(userID);
        if (user != null) {
            getWorld(world).unloadUser(user);
            user.load(getWorld(world));
            return getUser(userID);
        }
        user = new CoUser(plugin, userID, true);
        this.users.put(userID, user);
        getWorld(world).loadUser(user);
        return user;
    }

    /**
     * Unloads a user from the server
     *
     * @param userID The user to unload
     */

    //This should only ever unload a user that is online
    public void unloadUser(UUID userID) {
        CoUser user = getUser(userID);
        if (user == null) return;
        user.getWorld().unloadUser(user);
        this.users.remove(userID);
    }

}

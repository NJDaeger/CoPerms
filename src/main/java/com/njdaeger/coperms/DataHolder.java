package com.njdaeger.coperms;

import com.njdaeger.coperms.data.CoUser;
import com.njdaeger.coperms.data.CoWorld;
import com.njdaeger.coperms.groups.Group;
import com.njdaeger.coperms.groups.SuperGroup;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@SuppressWarnings({"unused", "UnusedReturnValue", "WeakerAccess"})
public class DataHolder {
    
    private final CoPerms plugin;
    private final Map<UUID, CoUser> users;
    private final Map<String, CoWorld> worlds;
    private final Map<String, SuperGroup> supers;

    DataHolder(DataLoader dataloader, CoPerms plugin) {
        this.plugin = plugin;
        this.users = new HashMap<>();
        this.supers = dataloader.getSuperGroups();
        this.worlds = dataloader.getWorlds();
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
     * Get an online user from a world
     *
     * @param world The world to look for the user in
     * @param uuid  The user to search for
     * @return The user if found, or null otherwise.
     */
    public CoUser getUser(@NotNull World world, @NotNull UUID uuid) {
        Validate.notNull(world, "World cannot be null");
        Validate.notNull(uuid, "UUID cannot be null");
        return getUser(world, uuid, true);
    }
    
    /**
     * Get an online or offline user from a world
     * @param world The world to look for the user in
     * @param uuid The user to search for
     * @param onlineOnly Whether to search online users only or not
     * @return The user if found, or null otherwise.
     */
    public CoUser getUser(@NotNull World world, @NotNull UUID uuid, boolean onlineOnly) {
        Validate.notNull(world, "World cannot be null");
        Validate.notNull(uuid, "UUID cannot be null");
        return getWorld(world).getUserDataFile().getUser(uuid, onlineOnly);
    }
    
    /**
     * Get an online user from a world
     *
     * @param world The world to look for the user in
     * @param name  The user to search for
     * @return The user if found, or null otherwise
     */
    public CoUser getUser(@NotNull World world, @NotNull String name) {
        Validate.notNull(world, "World cannot be null");
        Validate.notNull(name, "Name cannot be null");
        return getUser(world, name, true);
    }
    
    /**
     * Get an online or offline user from a world
     *
     * @param world The world to look for the user in
     * @param name The name of the user to search for
     * @param onlineOnly Whether to search online users only or not
     * @return The user if found, or null otherwise.
     */
    public CoUser getUser(@NotNull World world, @NotNull String name, boolean onlineOnly) {
        Validate.notNull(world, "World cannot be null");
        Validate.notNull(name, "Name cannot be null");
        return getWorld(world).getUserDataFile().getUser(name, onlineOnly);
    }
    
    /**
     * Get an online user from a world
     *
     * @param world The name of the world to look for the user in
     * @param uuid  The user to search for
     * @return The user if found, null otherwise
     */
    public CoUser getUser(@NotNull String world, @NotNull UUID uuid) {
        Validate.isTrue(Bukkit.getWorld(world) != null, "World cannot be null");
        Validate.notNull(uuid, "UUID cannot be null");
        return getUser(world, uuid, true);
    }
    
    /**
     * Get an online or offline user from a world
     * @param world The world to look for the user in
     * @param uuid The user to search for
     * @param onlineOnly Whether to search online users only or not
     * @return The user if found, null otherwise
     */
    public CoUser getUser(@NotNull String world, @NotNull UUID uuid, boolean onlineOnly) {
        Validate.isTrue(Bukkit.getWorld(world) != null, "World cannot be null");
        Validate.notNull(uuid, "UUID cannot be null");
        return getUser(Bukkit.getWorld(world), uuid, onlineOnly);
    }

    /**
     * Gets an online user from a world
     *
     * @param world The world to look for the user in
     * @param name  The user to search for
     * @return The user.
     * <p>
     * <p>Note: The user can be offline
     */
    public CoUser getUser(@NotNull String world, @NotNull String name) {
        Validate.isTrue(Bukkit.getWorld(world) != null, "World cannot be null");
        Validate.notNull(name, "Name cannot be null");
        return getWorld(world).getUser(name);
    }

    /**
     * Gets an online user
     *
     * @param uuid The user to get
     * @return The user
     */
    public CoUser getUser(@NotNull UUID uuid) {
        Validate.notNull(uuid, "UUID cannot be null");
        return users.get(uuid);
    }

    /**
     * Gets an online user by name
     *
     * @param name The name of the user
     * @return the user
     */
    public CoUser getUser(@NotNull String name) {
        Validate.notNull(name, "Name cannot be null");
        if (Bukkit.getPlayer(name) == null) return null;
        else return users.get(Bukkit.getPlayer(name).getUniqueId());
    }

    /**
     * Gets a world via the Bukkit world object
     *
     * @param world The world to get
     * @return The corresponding CoWorld
     */
    public CoWorld getWorld(@NotNull World world) {
        Validate.notNull(world, "World cannot be null");
        return worlds.get(world.getName());
    }

    /**
     * Gets a world via its name
     *
     * @param name The name of the world to get
     * @return The corresponding CoWorld
     */
    public CoWorld getWorld(@NotNull String name) {
        Validate.notNull(name, "World cannot be null");
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
    public Group getGroup(@NotNull World world, @NotNull String name) {
        Validate.notNull(world, "World cannot be null");
        Validate.notNull(name, "Group name cannot be null");
        return getWorld(world).getGroup(name);
    }
    
    /**
     * Get a set of all the groups on the server
     * @return A set of all the groups on the server
     */
    public Set<Group> getGroups() {
        Set<Group> groups = new HashSet<>();
        for (CoWorld world : worlds.values()) {
            groups.addAll(world.getGroups());
        }
        return groups;
    }
    
    /**
     * A set of all the group names on the server
     * @return All the names of the groups on the server
     */
    public Set<String> getGroupNames() {
        return getGroups().stream().map(Group::getName).collect(Collectors.toSet());
    }
    
    /**
     * Get a SuperGroup if exists
     *
     * @param name The name of the SuperGroup
     * @return The SuperGroup
     */
    public SuperGroup getSuperGroup(@NotNull String name) {
        Validate.notNull(name, "Group name cannot be null");
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
     */

    //This should only ever load a user that is online.
    void loadUser(World world, UUID userID) {
        CoUser user = getUser(userID);
        if (user != null) {
            getWorld(world).removeUser(user);
            user.load(getWorld(world));
            return;
        }
        user = new CoUser(plugin, userID, true);
        this.users.put(userID, user);
        getWorld(world).addUser(user);
    }

    /**
     * Unloads a user from the server
     *
     * @param userID The user to unload
     */

    //This should only ever unload a user that is online
    void unloadUser(UUID userID) {
        CoUser user = getUser(userID);
        if (user == null) return;
        user.getWorld().removeUser(user);
        this.users.remove(userID);
    }

}

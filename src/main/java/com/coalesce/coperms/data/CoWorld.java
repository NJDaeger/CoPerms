package com.coalesce.coperms.data;

import com.coalesce.coperms.CoPerms;
import com.coalesce.coperms.configuration.GroupDataFile;
import com.coalesce.coperms.configuration.UserDataFile;
import com.coalesce.core.config.base.ISection;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@SuppressWarnings({"unused", "WeakerAccess"})
public final class CoWorld {

    private final World world;
    private final CoPerms plugin;
    private final Set<UUID> allUsers;
    private UserDataFile userData;
    private GroupDataFile groupData;
    private final Map<UUID, CoUser> users;

    public CoWorld(CoPerms plugin, World world) {
        this.world = world;
        this.plugin = plugin;
        this.users = new HashMap<>();
        this.allUsers = new HashSet<>();
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
     * @return The user whether they
     */
    public CoUser getUser(UUID uuid) {
        if (users.containsKey(uuid)) {
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
        }
        if (hasUser(name)) {
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
        return getUser(uuid) != null || allUsers.contains(uuid);
    }

    /**
     * Checks if the user data file of this world has a user in it.
     *
     * @param name The user to look for
     * @return True if the user has been in this world, false otherwise.
     */
    public boolean hasUser(String name) {
        return resolveID(name) != null;
    }

    private UUID resolveID(String name) {
        ISection idSection = userData.getSection("users");
        
        for (UUID uuid : allUsers) {
            if (idSection.getSection(uuid.toString()).getString("username").equalsIgnoreCase(name)) return uuid;
        }
        
        return null;
    }

    /**
     * Gets a map of all the users in this world
     *
     * @return The worlds user map
     */
    public Map<UUID, CoUser> getOnlineUsers() {
        return users;
    }

    /**
     * Gets all the users that exist in the userdata file
     *
     * @return All the users. Online or offline
     */
    public Set<UUID> getAllUsers() {
        return allUsers;
    }

    /**
     * Gets a group from this world
     *
     * @param name The name of the group to find
     * @return The group
     */
    public Group getGroup(String name) {
        return groupData.getGroup(name);
    }

    /**
     * Gets a rank via ID
     *
     * @param id The id of the rank
     * @return The rank if it exists
     */
    public Group getGroup(int id) {
        return groupData.getGroup(id);
    }
    
    /**
     * Check if this world has a group
     * @param name The name of the group to look for
     * @return True if this world has the group, false otherwise.
     */
    public boolean hasGroup(String name) {
        return getGroup(name) != null;
    }
    
    /**
     * Gets a map of all the groups in this world
     *
     * @return The group map
     */
    public Map<String, Group> getGroups() {
        return groupData.getGroupMap();
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
    
    public void load(UserDataFile userData, GroupDataFile groupData) {
        this.userData = userData;
        this.groupData = groupData;
        groupData.addWorld(this);
        userData.getSection("users").getKeys(false).forEach(id -> allUsers.add(UUID.fromString(id)));
    }
}

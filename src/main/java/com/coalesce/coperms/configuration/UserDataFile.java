package com.coalesce.coperms.configuration;

import com.coalesce.coperms.CoPerms;
import com.coalesce.coperms.data.CoUser;
import com.coalesce.coperms.data.CoWorld;
import com.coalesce.core.config.YmlConfig;
import com.coalesce.core.config.base.ISection;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@SuppressWarnings({"unused", "WeakerAccess"})
public final class UserDataFile extends YmlConfig {

    private final List<CoWorld> worlds;
    private final Set<UUID> users;
    private final CoPerms plugin;
    private final World world;

    public UserDataFile(CoPerms plugin, World world) {
        super("worlds" + File.separator + world.getName() + File.separator + "users", plugin);
        
        this.worlds = new ArrayList<>();
        this.users = getSection("users").getKeys(false).stream().map(UUID::fromString).collect(Collectors.toSet());
        this.plugin = plugin;
        this.world = world;
    }

    /**
     * Loads a user to this world via uuid
     *
     * @param uuid The user to load
     */
    public void loadUser(UUID uuid) {
        users.add(uuid);
        String path = "users." + uuid.toString();
        setEntry(path + ".username", Bukkit.getPlayer(uuid).getName());
        addEntry(path + ".group", plugin.getDataHolder().getWorld(world).getDefaultGroup().getName());
        addEntry(path + ".info.prefix", null);
        addEntry(path + ".info.suffix", null);
        addEntry(path + ".permissions", Collections.emptyList());
    }
    
    /**
     * Gets a user from this file in the specified world. Can be offline.
     * @param world The world to get the user from
     * @param uuid The id of the user
     * @return The user (online or offline) if the world has had the user and this file has the user. Null otherwise
     */
    public CoUser getUser(CoWorld world, UUID uuid) {
        CoUser user = plugin.getDataHolder().getUser(uuid);
        if (user != null && world.hasUser(uuid)) {
            return user;
        }
        if (hasUser(uuid)) {
            if (world.hasUser(uuid)) {
                user = new CoUser(plugin, uuid, false);
                user.load(world);
                return user;
            }
        }
        return null;
    }
    
    /**
     * Gets a user from this file in the specified world. Can be offline.
     * @param world The world to get the user from
     * @param name The name of the user
     * @return The user (online or offline) if the world has had the user and this file has the user. Null otherwise
     */
    public CoUser getUser(CoWorld world, String name) {
        CoUser user = plugin.getDataHolder().getUser(name);
        if (user != null && world.hasUser(name)) {
            return user;
        }
        if (hasUser(name)) {
            if (world.hasUser(name)) {
                user = new CoUser(plugin, resolveId(name), false);
                user.load(world);
                return user;
            }
        }
        return null;
    }
    
    /**
     * Gets a user from this configuration file
     * @param uuid The id of the user to get
     * @return The user if they are online, in one of the worlds that uses this file. Otherwise null.
     */
    public CoUser getUser(UUID uuid) {
        CoUser user = plugin.getDataHolder().getUser(uuid);
        if (user != null && worlds.stream().anyMatch(w -> user.getWorld().equals(w))) return user;
        else return null;
    }
    
    /**
     * Gets a user from this configuration file
     * @param name The name of the user to get
     * @return The user if they are online, in one of the worlds that uses this file. Otherwise null.
     */
    public CoUser getUser(String name) {
        CoUser user = plugin.getDataHolder().getUser(name);
        if (user != null && worlds.stream().anyMatch(w -> user.getWorld().equals(w))) return user;
        return null;
    }
    
    /**
     * Whether this datafile has had a user before
     * @param uuid The user to look for
     * @return True if the userID is in this datafile.
     */
    public boolean hasUser(UUID uuid) {
        return users.contains(uuid);
    }
    
    /**
     * Whether this datafile has had a user before
     * @param name The user to look for
     * @return True if the username is in this datafile.
     */
    public boolean hasUser(String name) {
        return hasUser(resolveId(name));
    }
    
    /**
     * Gets all the users that are in this data file. Online or not.
     * @return The users that are in this data file
     */
    public Set<UUID> getUsers() {
        return users;
    }
    
    /**
     * Gets a uuid from a username
     * @param name The name of the user to lookup
     * @return The UUID if the user exists in this file.
     */
    public UUID resolveId(String name) {
        ISection idSection = getSection("users");
    
        for (UUID uuid : users) {
            if (idSection.getSection(uuid.toString()).getString("username").equalsIgnoreCase(name)) return uuid;
        }
        return null;
    }
    
    public List<CoWorld> getWorlds() {
        return worlds;
    }
    
    public void addWorld(CoWorld world) {
        worlds.add(world);
    }
    
}

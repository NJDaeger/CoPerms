package com.njdaeger.coperms.configuration;

import com.njdaeger.bcm.Configuration;
import com.njdaeger.bcm.base.ConfigType;
import com.njdaeger.bcm.base.ISection;
import com.njdaeger.coperms.CoPerms;
import com.njdaeger.coperms.data.CoUser;
import com.njdaeger.coperms.data.CoWorld;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@SuppressWarnings({"unused", "WeakerAccess"})
public final class UserDataFile extends Configuration {
    
    private final Set<UUID> users; //Contains all the users online or offline which are held in this user data file.
    private final CoPerms plugin;
    private final World world; //The original world this user file is derived from.

    public UserDataFile(CoPerms plugin, World world) {
        super(plugin, ConfigType.YML, "worlds" + File.separator + world.getName() + File.separator + "users");
        
        this.users = hasSection("users") ? getSection("users").getKeys(false).stream().map(UUID::fromString).collect(Collectors.toSet()) : new HashSet<>();
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
     * Get a user from this file in the specified world. User can be online or offline.
     *
     * @param world The world to get the user from
     * @param uuid The id of the user
     * @return The user if they have been in the specified world and the world's user data file is this specific file. Null otherwise.
     */
    public CoUser getUser(CoWorld world, UUID uuid) {
        Validate.notNull(world, "World cannot be null");
        Validate.notNull(uuid, "UUID cannot be null");
        if (!hasUser(uuid) || !world.getUserDataFile().equals(this)) return null;
        else if (world.hasUser(uuid, true)) return world.getUser(uuid);
        else {
            CoUser user = new CoUser(plugin, uuid, false);
            user.load(world);
            return user;
        }
        
    }
    
    /**
     * Get a user from this file in the specified world. User can be online or offline.
     *
     * @param world The world to get the user from
     * @param name The name of the user
     * @return The user if they have been in the specified world and the world's user data file is this specific file. Null otherwise.
     */
    public CoUser getUser(CoWorld world, String name) {
        Validate.notNull(world, "World cannot be null");
        Validate.notNull(name, "Name cannot be null");
        if (!hasUser(name) || !world.getUserDataFile().equals(this)) return null;
        else if (world.hasUser(name, true)) return world.getUser(name);
        else {
            CoUser user = new CoUser(plugin, resolveId(name), false);
            user.load(world);
            return user;
        }
    }
    
    /**
     * Attempts to get an online user via uuid from this file.
     *
     * @param uuid The id of the user to get
     * @return The user if they are online, in one of the worlds that uses this file. Otherwise null.
     */
    public CoUser getUser(UUID uuid) {
        return getUser(uuid, true);
    }
    
    /**
     * Attempts to get a user whether they are online or offline.
     *
     * @param uuid The user uuid
     * @param onlineOnly Whether to search only online players or both online and offline.
     * @return The user if found in this file, or null otherwise.
     */
    public CoUser getUser(UUID uuid, boolean onlineOnly) {
        CoUser user = plugin.getDataHolder().getUser(uuid);
        if (user != null) return user.getWorld().getUserDataFile().equals(this) ? user : null;
        else if (!onlineOnly && hasUser(uuid)) return new CoUser(plugin, uuid, false);
        else return null;
    }
    
    /**
     * Attempts to get an online user from this UserData file via their name.
     * @param name The name of the user to get
     * @return The user if they are online, in one of the worlds that uses this file. Otherwise null.
     */
    public CoUser getUser(String name) {
        return getUser(name, true);
    }
    
    /**
     * Attempts to get an online or offline user via name from this file.
     * @param name The name of the user
     * @param onlineOnly Whether to search online users only
     * @return The user if found in this file, or null otherwise
     */
    public CoUser getUser(String name, boolean onlineOnly) {
        CoUser user = plugin.getDataHolder().getUser(name);
        if (user != null) return user.getWorld().getUserDataFile().equals(this) ? user : null;
        else if (!onlineOnly && hasUser(name)) return new CoUser(plugin, resolveId(name), false);
        else return null;
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
     * Whether this user file has any users.
     * @return True if the user file has users specified, false otherwise.
     */
    public boolean hasUsers() {
        return users.isEmpty();
    }
    
    /**
     * Gets a uuid from a username
     * @param name The name of the user to lookup
     * @return The UUID if the user exists in this file.
     */
    public UUID resolveId(String name) {
        ISection idSection = getSection("users");
        
        for (String uuid : idSection.getKeys(false)) {
            if (idSection.getSection(uuid).getString("username").equalsIgnoreCase(name)) return UUID.fromString(uuid);
        }
        return null;
    }
    
}

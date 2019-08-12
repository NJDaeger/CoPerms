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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@SuppressWarnings({"unused"})
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
     * Loads a user to this file
     *
     * @param uuid The user to load
     */
    public void loadUser(@NotNull UUID uuid) {
        Validate.notNull(uuid, "UUID cannot be null");
        users.add(uuid);
        String path = "users." + uuid.toString();
        setEntry(path + ".username", Bukkit.getPlayer(uuid).getName());
        addEntry(path + ".group", plugin.getDataHolder().getWorld(world).getDefaultGroup().getName());
        addEntry(path + ".info.prefix", null);
        addEntry(path + ".info.suffix", null);
        addEntry(path + ".permissions", Collections.emptyList());
    }

    public CoUser getUser(CoWorld world, UUID uuid) {
        return world.getUser(uuid);
    }

    public CoUser getUser(CoWorld world, String name) {
        return world.getUser(name);
    }

    /**
     * Whether this datafile has had a user before
     * @param uuid The user to look for
     * @return True if the userID is in this datafile.
     */
    public boolean hasUser(@Nullable UUID uuid) {
        if (uuid == null) return false;
        return users.contains(uuid);
    }
    
    /**
     * Whether this datafile has had a user before
     * @param name The user to look for
     * @return True if the username is in this datafile.
     */
    public boolean hasUser(@NotNull String name) {
        Validate.notNull(name, "Name cannot be null");
        return hasUser(getUserId(name));
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
    public UUID getUserId(@NotNull String name) {
        Validate.notNull(name, "Name cannot be null");
        ISection idSection = getSection("users");
        
        for (String uuid : idSection.getKeys(false)) {
            if (idSection.getSection(uuid).getString("username").equalsIgnoreCase(name)) return UUID.fromString(uuid);
        }
        return null;
    }
    
}

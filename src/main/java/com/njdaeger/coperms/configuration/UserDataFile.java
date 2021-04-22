package com.njdaeger.coperms.configuration;

import com.njdaeger.coperms.CoPerms;
import com.njdaeger.coperms.data.CoUser;
import com.njdaeger.pdk.config.ConfigType;
import com.njdaeger.pdk.config.Configuration;
import com.njdaeger.pdk.config.ISection;
import org.apache.commons.lang.Validate;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings({"unused"})
public final class UserDataFile extends Configuration {

    private final Map<UUID, CoUser> userMap;
    private final CoPerms plugin;
    private final World world; //The original world this user file is derived from.

    public UserDataFile(CoPerms plugin, World world) {
        super(plugin, ConfigType.YML, "worlds" + File.separator + world.getName() + File.separator + "users");

        this.userMap = new HashMap<>();
        (hasSection("users") ? getSection("users").getKeys(false).stream().map(UUID::fromString).collect(Collectors.toSet()) : new HashSet<>()).forEach(uuid -> userMap.putIfAbsent((UUID) uuid, new CoUser(plugin, this, (UUID) uuid)));

        this.plugin = plugin;
        this.world = world;
    }

    /**
     * Loads a user to this file
     *
     * @param player The player to load
     */
    public void loadPlayer(@NotNull Player player) {
        Validate.notNull(player, "Player cannot be null");
        String path = "users." + player.getUniqueId().toString();
        setEntry(path + ".username", player.getName());
        addEntry(path + ".group", plugin.getWorld(world).getDefaultGroup().getName());
        addEntry(path + ".info.prefix", null);
        addEntry(path + ".info.suffix", null);
        addEntry(path + ".permissions", Collections.emptyList());
        userMap.put(player.getUniqueId(), new CoUser(plugin, this, player.getUniqueId()));
    }

    public CoUser getUser(UUID uuid) {
        return userMap.get(uuid);
    }

    public CoUser getUser(String name) {
        return userMap.values().stream().filter(user -> user.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    /**
     * Whether this datafile has had a user before
     * @param uuid The user to look for
     * @return True if the userID is in this datafile.
     */
    public boolean hasUser(@Nullable UUID uuid) {
        if (uuid == null) return false;
        return userMap.containsKey(uuid);
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
    public Set<UUID> getUserIds() {
        return userMap.keySet();
    }

    /**
     * Gets all the users that are in this data file. Online or not.
     * @return The users that are in this data file
     */
    public Set<CoUser> getUsers() {
        return new HashSet<>(userMap.values());
    }

    public Map<UUID, CoUser> getUserMap() {
        return userMap;
    }

    /**
     * Whether this user file has any users.
     * @return True if the user file has users specified, false otherwise.
     */
    public boolean hasUsers() {
        return userMap.isEmpty();
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

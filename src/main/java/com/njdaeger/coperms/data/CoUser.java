package com.njdaeger.coperms.data;

import com.njdaeger.coperms.CoPerms;
import com.njdaeger.coperms.Injector;
import com.njdaeger.coperms.configuration.UserDataFile;
import com.njdaeger.coperms.groups.Group;
import com.njdaeger.coperms.tree.PermissionTree;
import com.njdaeger.pdk.config.ISection;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@SuppressWarnings({"unused", "WeakerAccess"})
public final class CoUser {

    //Determine if the user has changed since the last file save
    private boolean hasChanged;

    private String name;
    private Group group;
    private CoWorld world;
    private String prefix;
    private String suffix;
    private final UUID uuid;
    private ISection userInfo;
    private final ISection userSection;
    private final CoPerms plugin;
    private final PermissionTree userPermissionTree;

    public CoUser(CoPerms plugin, UserDataFile userData, UUID userID, boolean hasChanged) {
        this(plugin, userData, userID);
        this.hasChanged = true;
    }

    public CoUser(CoPerms plugin, UserDataFile userData, UUID userID) {
        this.userPermissionTree = new PermissionTree();
        this.plugin = plugin;
        this.uuid = userID;

        this.userSection = userData.getSection("users." + uuid.toString());
        this.userInfo = userSection.getSection("info");

        //Checking for any custom permissions this user has in their user section in the datafile
        List<String> customPerms = userSection.getStringList("permissions");
        if (customPerms != null) this.userPermissionTree.importPermissions(customPerms);

        //Gets the name of the user. Only update if the user is online.
        this.name = userSection.getString("username");
        if (isOnline()) {
            if (this.name == null || !this.name.equals(Bukkit.getPlayer(uuid).getName())) {
                this.name = Bukkit.getPlayer(uuid).getName();
                this.hasChanged = true;
            }
        }

        //Get the prefix and suffix. The info may be null.
        this.prefix = (String) getInfo("prefix");
        this.suffix = (String) getInfo("suffix");
    }

    public PermissionTree getPermissionTree() {
        return userPermissionTree;
    }

    /**
     * Checks if this CoUser is an online user.
     *
     * @return True if online, false otherwise.
     */
    public boolean isOnline() {
        return Bukkit.getPlayer(uuid) != null;
    }

    /**
     * Gets the current group of the user
     *
     * @return The users current group
     */
    public Group getGroup() {
        return group;
    }

    /**
     * Gets the name of the user
     *
     * @return The name of the user.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the user prefix
     *
     * @return The user prefix
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Sets the user prefix
     *
     * @param prefix The user prefix
     */
    public void setPrefix(@Nullable String prefix) {
        this.prefix = prefix;
        this.hasChanged = true;
    }

    /**
     * Gets the user suffix
     *
     * @return The user suffix
     */
    public String getSuffix() {
        return suffix;
    }

    /**
     * Sets the user suffix
     *
     * @param suffix The user suffix
     */
    public void setSuffix(@Nullable String suffix) {
        this.suffix = suffix;
        this.hasChanged = true;
    }

    /**
     * Adds info to the user section
     *
     * @param node The node to add
     * @param value The value to set the node
     */
    public void addInfo(@NotNull String node, @Nullable Object value) {
        Validate.notNull(node, "Node cannot be null");
        if (userInfo == null) {
            userSection.setEntry("info." + node, value);
            userInfo = userSection.getSection("info");
        } else userInfo.setEntry(node, value);
        this.hasChanged = true;
    }

    /**
     * Gets a node from the user section
     *
     * @param node The node path to get
     * @return The value of the node, null if it doesn't exist.
     */
    public Object getInfo(@NotNull String node) {
        Validate.notNull(node, "Node cannot be null");
        if (userInfo == null || userInfo.getValue(node) == null) return null;
        else return userInfo.getValue(node);
    }

    /**
     * Gets the section containing additional user info
     *
     * @return The user info section. May be null if the user doesn't have a user info section.
     */
    public ISection getUserInfo() {
        return userInfo;
    }

    /**
     * Sets the group of a user
     *
     * @param world The world to set the group of the user in
     * @param name The name of the group
     * @return Whether the user was added or not.
     */
    public boolean setGroup(@NotNull CoWorld world, @NotNull String name) {
        Validate.notNull(world, "World cannot be null");
        Validate.notNull(name, "Name cannot be null");
        if (world.getGroup(name) == null) return false;

        this.group = world.getGroup(name);

        resolvePermissions();
        this.hasChanged = true;
        return true;
    }

    /**
     * Gets the user UUID
     *
     * @return The user UUID
     */
    public UUID getUserID() {
        return uuid;
    }

    /**
     * Gets the section of the user in its current world.
     *
     * @return The user section
     */
    public ISection getUserSection() {
        return userSection;
    }

    /**
     * Gets all the permissions specified in the user's current world UserDataFile
     *
     * @return All the user permissions in this world
     */
    public Set<String> getUserPermissions() {
        return userPermissionTree.getPermissionNodes();
    }

    /**
     * Gets the current world of the user.
     *
     * @return The users current world
     */
    public CoWorld getWorld() {
        return world;
    }

    public void loadIntoWorld(CoWorld world) {
        this.world = world;
        //Trying to get the group the user is in on this world. If the group doesnt exist, it gets the default of the world
        this.group = world.getGroup(userSection.getString("group"));
        if (group == null) setGroup(world, world.getDefaultGroup().getName());
    }

    /**
     * Checks if the user has a permission or not.
     *
     * @param permission The permission to check
     * @return True if the user has the permission, false otherwise.
     */
    public boolean hasPermission(@NotNull String permission) {
        Validate.notNull(permission, "Permission cannot be null");
        byte state = userPermissionTree.getGrantedState(permission);
        if (state == 1) return true;
        else if (state == 0) return group.hasPermission(permission);
        return false;
    }

    /**
     * Grants this user permission to a specific permission.
     *
     * @param permission The permission to grant access to
     * @return True if the permission was granted, or false if the permission was already granted.
     */
    public boolean grantPermission(@NotNull String permission) {
        Validate.notNull(permission, "Permission cannot be null");
        boolean ret = userPermissionTree.grantPermission(permission);
        if (ret) {
            updateCommands();
            this.hasChanged = true;
        }
        return ret;
    }

    public Set<String> grantPermissions(@NotNull String... permissions) {
        Validate.notNull(permissions, "Permission cannot be null");
        Set<String> unable = new HashSet<>();
        for (String permission : permissions) {
            if (!userPermissionTree.grantPermission(permission)) unable.add(permission);
        }
        if (unable.isEmpty()) {
            updateCommands();
            this.hasChanged = true;
        }
        return unable;
    }

    /**
     * Revokes this permission from the user (negates)
     *
     * @param permission The permission to revoke from the user
     * @return True if the permission was successfully revoked, false if it is already revoked.
     */
    public boolean revokePermission(@NotNull String permission) {
        Validate.notNull(permission, "Permission cannot be null");
        boolean ret = userPermissionTree.revokePermission(permission);
        if (ret) {
            updateCommands();
            this.hasChanged = true;
        }
        return ret;
    }

    public Set<String> revokePermissions(@NotNull String... permissions) {
        Validate.notNull(permissions, "Permission cannot be null");
        Set<String> unable = new HashSet<>();
        for (String permission : permissions) {
            if (!userPermissionTree.revokePermission(permission)) unable.add(permission);
        }
        if (unable.isEmpty()) {
            updateCommands();
            this.hasChanged = true;
        }
        return unable;
    }

    /**
     * Removes this permission from the user (inherits)
     *
     * @param permission The permission to remove from the user
     * @return True if the permission was successfully removed, false if it is already removed.
     */
    public boolean removePermission(@NotNull String permission) {
        Validate.notNull(permission, "Permission cannot be null");
        boolean ret = userPermissionTree.removePermission(permission);
        if (ret) {
            updateCommands();
            this.hasChanged = true;
        }
        return ret;
    }

    public Set<String> removePermissions(@NotNull String... permissions) {
        Validate.notNull(permissions, "Permission cannot be null");
        Set<String> unable = new HashSet<>();
        for (String permission : permissions) {
            if (!userPermissionTree.removePermission(permission)) unable.add(permission);
        }
        if (unable.isEmpty()) {
            updateCommands();
            this.hasChanged = true;
        }
        return unable;
    }

    /**
     * Sends a formatted plugin message to the represented player
     *
     * @param message The message to send
     */
    public void pluginMessage(@Nullable String message) {
        if (isOnline())
            Bukkit.getPlayer(uuid).sendMessage(ChatColor.GRAY + "[" + ChatColor.DARK_AQUA + plugin.getName() + ChatColor.GRAY + "] " + ChatColor.RESET + message);
    }


    public boolean hasChanged() {
        return hasChanged;
    }

    public void updateCommands() {
        if (isOnline()) Bukkit.getScheduler().runTask(CoPerms.getInstance(), () -> {
            Player player = Bukkit.getPlayer(uuid);
            player.updateCommands();
        });
    }

    /**
     * Resolves the user permissions by Injecting the permissible and updating user commands.
     */
    public void resolvePermissions() {
        if (isOnline()) Injector.inject(Bukkit.getPlayer(uuid));
    }

    public void save() {
        addInfo("suffix", suffix);
        addInfo("prefix", prefix);
        this.userSection.setEntry("group", group.getName());
        this.userSection.setEntry("permissions", userPermissionTree.getPermissionNodes().toArray(new String[0]));
        this.hasChanged = false;
    }

}

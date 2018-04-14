package com.coalesce.coperms.data;

import com.coalesce.coperms.CoPerms;
import com.coalesce.core.config.base.ISection;
import org.bukkit.Bukkit;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@SuppressWarnings({"unused", "WeakerAccess"})
public final class CoUser {

    private String name;
    private Group group;
    private String prefix;
    private String suffix;
    private CoWorld world;
    private final UUID uuid;
    private ISection userInfo;
    private final CoPerms plugin;
    private ISection userSection;
    private final boolean isOnline;
    private final Set<Group> groups;
    private final Set<String> wildcards;
    private final Set<String> negations;
    private final Set<String> permissions;
    private final Set<String> userPermissions;

    public CoUser(CoPerms plugin, UUID userID, boolean isOnline) {
        this.userPermissions = new HashSet<>();
        this.permissions = new HashSet<>();
        this.wildcards = new HashSet<>();
        this.negations = new HashSet<>();
        this.groups = new HashSet<>();
        this.isOnline = isOnline;
        this.plugin = plugin;
        this.uuid = userID;
    }

    /**
     * Checks if this CoUser is an online user.
     *
     * @return True if online, false otherwise.
     */
    public boolean isOnline() {
        return isOnline;
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
    public void setPrefix(String prefix) {
        this.prefix = prefix;
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
    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    /**
     * Adds info to the user section
     *
     * @param node  The node to add
     * @param value The value to set the node
     */
    public void addInfo(String node, Object value) {
        if (userInfo == null) {
            userSection.setEntry(".info." + node, value);
            userInfo = userSection.getSection("info");
        }
        else userInfo.setEntry(node, value);
    }

    /**
     * Gets a node from the user section
     *
     * @param node The node path to get
     * @return The value of the node, null if it doesn't exist.
     */
    public Object getInfo(String node) {
        if (userInfo == null || userInfo.getValue(node) == null) return null;
        else return userInfo.getValue(node);
    }
    
    /**
     * Gets the section containing additional user info
     * @return The user info section. May be null if the user doesn't have a user info section.
     */
    public ISection getUserInfo() {
        return userInfo;
    }

    /**
     * Gets all the groups the user is in from every world
     *
     * @return All the user groups
     */
    public Set<Group> getGroups() {
        return groups;
    }

    /**
     * Sets the group of a user
     *
     * @param world The world to set the group of the user in
     * @param name  The name of the group
     * @return Whether the user was added or not.
     */
    public boolean setGroup(CoWorld world, String name) {
        if (world.getGroup(name) == null) return false;
        
        this.group.removeUser(uuid);
        this.groups.remove(group);
        this.group = world.getGroup(name);
        this.groups.add(group);
        this.group.addUser(uuid);
        
        resolvePermissions();
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
        return userPermissions;
    }

    /**
     * All the permissions the user has, including the ones given from the group its currently in
     *
     * @return All the user permissions
     */
    public Set<String> getPermissions() {
        return permissions;
    }

    /**
     * Gets the current world of the user.
     *
     * @return The users current world
     */
    public CoWorld getWorld() {
        return world;
    }

    /**
     * Checks if the user has a permission or not.
     *
     * @param node The node to check
     * @return True if the user has the permission, false otherwise.
     */
    public boolean hasPermission(String node) {
        return permissions.contains(node);
    }

    /**
     * Adds a permission to the users permissions.
     *
     * @param node The permission to add
     * @return If the permission was added or not.
     */
    public boolean addPermission(String node) {
        boolean ret = getUserPermissions().add(node);
        resolvePermissions();
        return ret;
    }
    
    /**
     * Sends a formatted plugin message to the represented player
     * @param message The message to send
     */
    public void pluginMessage(String message) {
        if (isOnline()) Bukkit.getPlayer(uuid).sendMessage(plugin.getCoFormatter().format(message));
    }

    /**
     * Removes a permission from the users permissions.
     *
     * @param node The permission to add
     * @return If the permission was removed or not.
     */
    public boolean removePermission(String node) {
        boolean ret = userPermissions.remove(node);
        resolvePermissions();
        return ret;
    }

    /**
     * Loads a user into a world
     *
     * @param world The world to load the user into
     */
    public void load(CoWorld world) {
        this.world = world;
        
        //Set the user section in this CoUser object
        this.userSection = world.getUserDataFile().getSection("users." + uuid.toString());
        
        //Get the user info if possible
        this.userInfo = userSection.getSection("info");
        
        //Checking for any custom permissions this user has in their user section in the datafile
        List<String> customPerms = userSection.getStringList("permissions");
        if (customPerms != null) this.userPermissions.addAll(customPerms);
        
        //Trying to get the group the user is in on this world. If the group doesnt exist, it gets the default of the world
        this.group = world.getGroup(userSection.getString("group"));
        if (group == null) setGroup(world, world.getDefaultGroup().getName());
        
        //Gets the name of the user. If the username doesnt exist for some reason and the user is online, we get their name
        this.name = userSection.getString("username");
        if (name == null && isOnline) this.name = Bukkit.getPlayer(uuid).getName();
        
        //Get the prefix and suffix. The info may be null.
        this.prefix = (String)getInfo("prefix");
        this.suffix = (String)getInfo("suffix");
    
        //Add in all the groups this user is currently in into the groups set.
        for (CoWorld w : plugin.getDataHolder().getWorlds().values()) {
            if (w.hasUser(uuid)) groups.add(w.getUser(uuid).getGroup());
            
        }
        //Finish up permission parsing
        resolvePermissions();
    }

    /**
     * Unloads a user from any world.
     */
    public void unload() {
        addInfo("suffix", suffix);
        addInfo("prefix", prefix);
        this.userSection.setEntry("group", name);
        this.userSection.setEntry("permissions", userPermissions.toArray());
        
        this.userPermissions.clear();
        this.group.removeUser(uuid);
        this.permissions.clear();
        this.wildcards.clear();
        this.negations.clear();
        this.userSection = null;
        this.suffix = null;
        this.prefix = null;
        this.world = null;
        this.group = null;
    }

    /**
     * Gets all the wildcard permissions
     *
     * @return The user wildcard permissions
     */
    public Set<String> getWildcardNodes() {
        return wildcards;
    }

    /**
     * Gets all the negated nodes. (including negated wildcards)
     *
     * @return The user negated nodes.
     */
    public Set<String> getNegationNodes() {
        return negations;
    }

    /**
     * Resolves the user permissions
     */
    public void resolvePermissions() {
        permissions.clear();
        this.permissions.addAll(group.getPermissions());
        this.permissions.addAll(getUserPermissions());
        permissions.forEach(node -> {
            if (node.endsWith(".*")) {
                wildcards.add(node);
            }
            if (node.startsWith("-")) {
                negations.add(node);
            }
        });
    }
}

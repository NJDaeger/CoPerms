package com.njdaeger.coperms.groups;

import com.njdaeger.bcm.base.ISection;
import com.njdaeger.coperms.DataLoader;
import com.njdaeger.coperms.configuration.GroupDataFile;
import com.njdaeger.coperms.configuration.UserDataFile;
import com.njdaeger.coperms.data.CoUser;
import com.njdaeger.coperms.exceptions.InheritanceParseException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@SuppressWarnings({"unused", "WeakerAccess", "UnusedReturnValue"})
public final class Group extends AbstractGroup {
    
    private final GroupDataFile groupDataFile;
    private final UserDataFile userDataFile;
    private List<AbstractGroup> inherited;
    private Set<String> groupPermissions;
    private final ISection infoSection;
    private boolean inheritanceLoaded;
    private Set<String> permissions;
    private final DataLoader loader;
    private final boolean isDefault;
    private final ISection section;
    private final Set<UUID> users;
    private final String name;
    private boolean canBuild;
    private final int rankID;
    private String prefix;
    private String suffix;


    public Group(GroupDataFile groupDataFile, UserDataFile userDataFile, String name, DataLoader loader) {
        this.section = groupDataFile.getSection("groups." + name);
        this.rankID = section.getInt("info.rankid");
        this.infoSection = section.getSection("info");
        this.inherited = new ArrayList<>();
        this.groupDataFile = groupDataFile;
        this.permissions = new HashSet<>();
        this.userDataFile = userDataFile;
        this.isDefault = rankID == 0;
        this.users = new HashSet<>();
        this.loader = loader;
        this.name = name;
    
        this.groupPermissions = new HashSet<>(section.getStringList("permissions"));
        this.canBuild = section.getBoolean("info.canBuild");
        this.prefix = section.getString("info.prefix");
        this.suffix = section.getString("info.suffix");
    
        if (userDataFile.hasUsers()) users.addAll(userDataFile.getUsers());
        
    }

    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public Set<String> getPermissions() {
        return permissions;
    }
    
    /**
     * Gets the group prefix
     *
     * @return The group prefix
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Sets the group prefix
     *
     * @param prefix The new group prefix
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * Gets the group suffix
     *
     * @return The group suffix
     */
    public String getSuffix() {
        return suffix;
    }

    /**
     * Sets the group suffix
     *
     * @param suffix The new suffix
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
        infoSection.setEntry(node, value);
    }

    /**
     * Gets a node from the user section
     *
     * @param node The node path to get
     * @return The value of the node, null if it doesn't exist.
     */
    public Object getInfo(String node) {
        if (!infoSection.contains(node, true)) return null;
        else return infoSection.getValue(node);
    }

    /**
     * Checks if the group has permissions to build
     *
     * @return True if the group can build
     */
    public boolean canBuild() {
        return canBuild;
    }

    /**
     * Gets the rank ID number. This is used to determine the rank tree
     *
     * @return The id of this rank
     */
    public int getRankID() {
        return rankID;
    }

    /**
     * Checks if this group is a default group or not
     *
     * @return If this group is default or not.
     */
    public boolean isDefault() {
        return isDefault;
    }

    /**
     * Gets the permissions that are specified for this group
     *
     * @return THe group private permissions.
     */
    public Set<String> getGroupPermissions() {
        return groupPermissions;
    }

    /**
     * Adds a user to this group
     *
     * @param user The user to add
     */
    public boolean addUser(UUID user) {
        return users.add(user);
    }

    /**
     * Removes a user from this group
     *
     * @param user The user to remove
     */
    public boolean removeUser(UUID user) {
        return users.remove(user);
    }

    /**
     * Checks if this group has a user in it
     *
     * @param user The user to look for
     * @return True if the user is currently in it, false otherwise
     */
    public boolean hasUser(UUID user) {
        return users.contains(user);
    }

    /**
     * Gets a user from this group
     *
     * @param uuid The user to get
     * @return The user if online.
     */
    public CoUser getUser(UUID uuid) {
        if (hasUser(uuid)) {
            return userDataFile.getUser(uuid);
        }
        return null;
    }

    /**
     * Checks whether this group has a permission or not
     *
     * @param permission The permission to look for
     * @return True if the group has the permission, false otherwise
     */
    public boolean hasPermission(String permission) {
        return permissions.contains(permission);
    }

    /**
     * Adds a permission to the group
     *
     * @param permission The permission to add
     * @return True if it was successfully added.
     */
    public boolean addPermission(String permission) {
        boolean ret;
        ret = groupPermissions.add(permission);
        loadInheritance();
        reloadUsers();
        return ret;
    }

    /**
     * Removes a permission from the group
     *
     * @param permission The permission to remove
     * @return True if the permission was successfully removed.
     */
    public boolean removePermission(String permission) {
        boolean ret = groupPermissions.remove(permission);
        loadInheritance();
        reloadUsers();
        return ret;
    }
    
    /**
     * Adds an inherited group to a group
     *
     * @param group The group to add to the inheritance tree
     * @return True if successfully added
     */
    public boolean addInheritance(AbstractGroup group) {
        boolean ret = inherited.add(group);
        if (ret) {
            loadInheritance();
            reloadUsers();
        }
        return ret;
    }
    
    /**
     * Removes an inherited group from a group
     *
     * @param group The group to remove from the inheritance tree
     * @return True if successfully removed
     */
    public boolean removeInheritance(AbstractGroup group) {
        boolean ret = inherited.remove(group);
        if (ret) {
            loadInheritance();
            reloadUsers();
        }
        return ret;
    }
    
    /**
     * Get a list of all the groups which this group inherits its permissions from.
     * @return A list of groups this groups permissions are inherited from.
     */
    public List<AbstractGroup> getInheritedGroups() {
        return inherited;
    }
    
    public void loadInheritance() {
        
        this.inheritanceLoaded = true;
        //We create a queue list of all the known groups which are inherited.
        List<String> queueList = section.getStringList("inherits");
        
        //We go through the list of known groups
        for (String key : section.getStringList("inherits")) {
            //If the group is a super group, we know they cannot inherit from something, so we automatically skip them
            //We check if the queue list contains the group or not, if it does we skip it. Otherwise its added to the queue
            if (!key.startsWith("s:")) groupDataFile.getGroup(key).section.getStringList("inherits").stream().filter(k -> !queueList.contains(k)).forEach(queueList::add);
            
        }
        
        //We go through the queue now loading all the permissions from the groups.
        queueList.forEach(key -> {
            //Its a super group specified if the key starts with 's:'
            if (key.startsWith("s:")) {
                //Always split at the colon and get the right side because that is where the group name is
                SuperGroup group = loader.getPlugin().getDataHolder().getSuperGroup(key.split(":")[1]);
                if (group == null) throw new InheritanceParseException(key, name);
                else inherited.add(group);
            }
            else {
                Group group = groupDataFile.getGroup(key);
                if (group == null) throw new InheritanceParseException(key, name);
                if (!group.inheritanceLoaded) group.loadInheritance();
                else inherited.add(group);
            }
        });
        permissions.clear();
        permissions.addAll(groupPermissions);
        //Adding all the permissions from the inherited groups
        inherited.forEach(g -> permissions.addAll(g.getPermissions()));
    }
    
    private void reloadUsers() {
        users.forEach(u -> {
            CoUser user = getUser(u);
            if (user != null) user.resolvePermissions();
        });
    }
    
    public void unload() {
        section.setEntry("permissions", groupPermissions.toArray(new String[0]));
        section.setEntry("inherits", inherited.stream().map(AbstractGroup::getName).toArray(String[]::new));
        addInfo("canBuild", canBuild);
        addInfo("rankid", rankID);
        addInfo("prefix", prefix);
        addInfo("suffix", suffix);
    }
}

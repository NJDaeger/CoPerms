package com.njdaeger.coperms.groups;

import com.njdaeger.bcm.base.ISection;
import com.njdaeger.coperms.CoPerms;
import com.njdaeger.coperms.configuration.GroupDataFile;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class Group extends AbstractGroup {
    
    private final GroupDataFile groupDataFile;
    
    //All of the inherited groups. This includes the indirect ones which are not listed in the inherited section.
    private List<AbstractGroup> inherited;
    
    //All the directly inherited groups. Anything which is listed in the groups inherits section is in this.
    private List<AbstractGroup> direct;
    
    private Set<String> groupPermissions;
    private final ISection infoSection;
    private boolean inheritanceLoaded;
    private Set<String> permissions;
    private final boolean isDefault;
    private final ISection section;
    private final CoPerms plugin;
    private final String name;
    private boolean canBuild;
    private final int rankID;
    private String prefix;
    private String suffix;

    public Group(CoPerms plugin, GroupDataFile groupDataFile, String name) {
        this.section = groupDataFile.getSection("groups." + name);
        this.rankID = section.getInt("info.rankid");
        this.infoSection = section.getSection("info");
        this.inherited = new ArrayList<>();
        this.direct = new ArrayList<>();
        this.groupDataFile = groupDataFile;
        this.permissions = new HashSet<>();
        this.isDefault = rankID == 0;
        this.plugin = plugin;
        this.name = name;
    
        this.groupPermissions = new HashSet<>(section.getStringList("permissions"));
        this.canBuild = section.getBoolean("info.canBuild");
        this.prefix = section.getString("info.prefix");
        this.suffix = section.getString("info.suffix");
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
    public void setPrefix(@Nullable String prefix) {
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
    public void setSuffix(@Nullable String suffix) {
        this.suffix = suffix;
    }

    /**
     * Adds info to the user section
     *
     * @param node  The node to add
     * @param value The value to set the node
     */
    public void addInfo(@NotNull String node, @Nullable Object value) {
        Validate.notNull(node, "Node cannot be null");
        infoSection.setEntry(node, value);
    }

    /**
     * Gets a node from the user section
     *
     * @param node The node path to get
     * @return The value of the node, null if it doesn't exist.
     */
    public Object getInfo(@NotNull String node) {
        Validate.notNull(node, "Node cannot be null");
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
     * Checks whether this group has a permission or not
     *
     * @param permission The permission to look for
     * @return True if the group has the permission, false otherwise
     */
    public boolean hasPermission(@NotNull String permission) {
        Validate.notNull(permission, "Permission cannot be null");
        return permissions.contains(permission);
    }

    /**
     * Adds a permission to the group
     *
     * @param permission The permission to add
     * @return True if it was successfully added.
     */
    public boolean addPermission(@NotNull String permission) {
        Validate.notNull(permission, "Permission cannot be null");
        boolean ret = groupPermissions.add(permission);
        if (ret) groupDataFile.reloadGroups();
        return ret;
    }

    /**
     * Removes a permission from the group
     *
     * @param permission The permission to remove
     * @return True if the permission was successfully removed.
     */
    public boolean removePermission(@NotNull String permission) {
        Validate.notNull(permission, "Permission cannot be null");
        boolean ret = groupPermissions.remove(permission);
        if (ret) groupDataFile.reloadGroups();
        return ret;
    }
    
    /**
     * Adds an inherited group to a group
     *
     * @param group The group to add to the inheritance tree
     * @return True if successfully added
     */
    public boolean addInheritance(@NotNull AbstractGroup group) {
        Validate.notNull(group, "Group cannot be null");
        direct.add(group);
        groupDataFile.reloadGroups();
        return true;
    }
    
    /**
     * Removes an inherited group from a group
     *
     * @param group The group to remove from the inheritance tree
     * @return True if successfully removed
     */
    public boolean removeInheritance(@NotNull AbstractGroup group) {
        Validate.notNull(group, "Group cannot be null");
        boolean ret = direct.remove(group);
        if (ret) groupDataFile.reloadGroups();
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
    
        permissions.clear();
        inherited.clear();
        direct.clear();
        
        for (String key : section.getStringList("inherits")) {
            if (key.startsWith("s:")) {
                SuperGroup group = plugin.getSuperGroup(key.substring(2));
                if (group == null) plugin.getLogger().warning("Cannot inherit supergroup " + key + " for group " + getName() + ". Is it spelled correctly? Does it exist?");
                else direct.add(group);
                
            }
            else {
                Group group = groupDataFile.getGroup(key);
                if (group == null) plugin.getLogger().warning("Cannot inherit group " + key + " for group " + getName() + ". Is it spelled correctly? Does it exist?");
                else direct.add(group);
            }
        }
        
        for (AbstractGroup abstractGroup : direct) {
            if (abstractGroup instanceof SuperGroup) inherited.add(abstractGroup);
            else {
                Group group = (Group)abstractGroup;
                if (!group.inheritanceLoaded) group.loadInheritance();
                inherited.add(group);
                inherited.addAll(group.getInheritedGroups());
            }
        }
        
        permissions.addAll(groupPermissions);
        inherited.forEach(g -> permissions.addAll(g.getPermissions()));
        
    }
    
    //This is called only when the permissions of a group are changed.
    public void preInheritanceLoad() {
        inherited.stream().filter(g -> g instanceof Group).forEach(g -> ((Group)g).inheritanceLoaded = false);
    }

    public void save() {
        section.setEntry("permissions", groupPermissions.toArray(new String[0]));
        section.setEntry("inherits", direct.stream().map(group -> {
            if (group instanceof SuperGroup) return "s:".concat(group.getName());
            else return group.getName();
        }).toArray(String[]::new));
        addInfo("canBuild", canBuild);
        addInfo("rankid", rankID);
        addInfo("prefix", prefix);
        addInfo("suffix", suffix);
    }
}

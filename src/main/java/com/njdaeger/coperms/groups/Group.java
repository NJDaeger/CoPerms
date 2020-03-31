package com.njdaeger.coperms.groups;

import com.njdaeger.bcm.base.ISection;
import com.njdaeger.coperms.CoPerms;
import com.njdaeger.coperms.configuration.GroupDataFile;
import com.njdaeger.coperms.tree.PermissionTree;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class Group extends AbstractGroup {

    private final GroupDataFile groupDataFile;

    //All the directly inherited groups. Anything which is listed in the groups inherits section is in this.
    private List<AbstractGroup> directlyInherits;
    private List<AbstractGroup> inheritors;

    private PermissionTree groupPermissionTree;
    //private PermissionTree fullPermissionTree;
    private final ISection infoSection;
    private boolean inheritanceLoaded;
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
        this.directlyInherits = new ArrayList<>();
        this.inheritors = new ArrayList<>();
        this.groupDataFile = groupDataFile;
        this.isDefault = rankID == 0;
        this.plugin = plugin;
        this.name = name;

        this.groupPermissionTree = new PermissionTree(section.getStringList("permissions"));
        //this.fullPermissionTree = new PermissionTree();
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
        return groupPermissionTree.getPermissionNodes();
    }

    @Override
    public PermissionTree getPermissionTree() {
        return groupPermissionTree;
    }

    /*@Override
    public Set<String> getGroupPermissions() {
        return groupPermissionTree.getPermissionNodes();
    }

    @Override
    public PermissionTree getGroupPermissionTree() {
        return groupPermissionTree;
    }*/

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
     * @param node The node to add
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

    @Override
    public boolean hasPermission(@NotNull String permission) {
        Validate.notNull(permission, "Permission cannot be null");
        byte state = groupPermissionTree.getGrantedState(permission);
        if (state == 1) return true;
        else if (state == 0) return directlyInherits.parallelStream().anyMatch(group -> group.hasPermission(permission));
        return false;
        //return fullPermissionTree.hasPermission(permission);
    }

    @Override
    public List<AbstractGroup> getInheritors() {
        return inheritors;
    }

    @Override
    public boolean addInheritor(AbstractGroup group) {
        return inheritors.add(group);
    }

    @Override
    public boolean removeInheritor(AbstractGroup group) {
        return inheritors.remove(group);
    }

    @Override
    public boolean grantPermission(@NotNull String permission) {
        Validate.notNull(permission, "Permission cannot be null");
        boolean ret = groupPermissionTree.grantPermission(permission);
        /*fullPermissionTree.grantPermission(permission);
        inheritors.forEach(g -> {
            //If the permission is explicitly defined in the group, and that definition prohibits them from using said
            //we do want to give them permission
            //permission, then we do not want to grant that group permission to this newly coming permission. Otherwise,
            if (g instanceof Group && !g.getGroupPermissionTree().isPermissionDefined(permission) || g.getGroupPermissionTree().hasPermission(permission)) {
                g.getPermissionTree().grantPermission(permission);
                g.getInheritors().forEach(gr -> gr.getPermissionTree().grantPermission(permission));

            }
        });*/
        return ret;
    }

    @Override
    public Set<String> grantPermissions(@NotNull String... permissions) {
        Validate.notNull(permissions, "Permission cannot be null");
        Set<String> unable = new HashSet<>();
        for (String permission : permissions) {
            if (!groupPermissionTree.grantPermission(permission)) unable.add(permission);
            //else fullPermissionTree.grantPermission(permission);
        }
        /*if (permissions.length == unable.size()) return unable;
        inheritors.forEach(g -> {
            for (String permission : permissions) {
                if (g instanceof Group && !g.getGroupPermissionTree().isPermissionDefined(permission) || g.getGroupPermissionTree().hasPermission(permission)) {
                    g.getPermissionTree().grantPermission(permission);
                    g.getInheritors().forEach(gr -> gr.getPermissionTree().grantPermission(permission));
                }
            }
        });*/
        return unable;
    }

    @Override
    public boolean revokePermission(@NotNull String permission) {
        Validate.notNull(permission, "Permission cannot be null");
        boolean ret = groupPermissionTree.revokePermission(permission);
        //fullPermissionTree.revokePermission(permission);
        /*inheritors.forEach(g -> {
            //The idea here is that since group permissions override all inherited permissions, if the group has explicit
            //permission to do something, then they should not have the permission taken away from them. Otherwise, the
            //permission will be taken from the fullPermissionTree, which is just a mashup of all the inherited permissions

            if (g instanceof Group && !g.getGroupPermissionTree().hasPermission(permission)) {
                g.getPermissionTree().revokePermission(permission);
                g.getInheritors().forEach(gr -> gr.getPermissionTree().revokePermission(permission));
            }
        });*/
        return ret;
    }

    @Override
    public Set<String> revokePermissions(@NotNull String... permissions) {
        Validate.notNull(permissions, "Permission cannot be null");
        Set<String> unable = new HashSet<>();
        for (String permission : permissions) {
            if (!groupPermissionTree.revokePermission(permission)) unable.add(permission);
            //else fullPermissionTree.revokePermission(permission);
        }
        /*if (permissions.length == unable.size()) return unable;
        inheritors.forEach(g -> {
            for (String permission : permissions) {
                if (g instanceof Group && !g.getGroupPermissionTree().hasPermission(permission)) {
                    g.getPermissionTree().revokePermission(permission);
                    g.getInheritors().forEach(gr -> gr.getPermissionTree().revokePermission(permission));
                }
            }
        });*/
        return unable;
    }

    @Override
    public boolean removePermission(@NotNull String permission) {
        Validate.notNull(permission, "Permission cannot be null");
        boolean ret = groupPermissionTree.removePermission(permission);
        /*loadInheritance();
        inheritors.forEach(g -> {
            //Since we need to notify all the groups which inherit this group, instead of granting/revoking/removing the
            //permission from the groups, we need to reload their inheritance to figure out if they had gotten the
            //permission from somewhere else.

            //Supergroups cant inherit anything
            if (g instanceof Group) ((Group) g).loadInheritance();
        });*/
        return ret;
    }

    @Override
    public Set<String> removePermissions(@NotNull String... permissions) {
        Set<String> unable = new HashSet<>();
        for (String permission : permissions) {
            if (!groupPermissionTree.removePermission(permission)) unable.add(permission);
        }
        /*loadInheritance();
        inheritors.forEach(g -> {
            if (g instanceof Group) ((Group) g).loadInheritance();
        });*/
        return unable;
    }

    /**
     * Adds an inherited group to a group
     *
     * @param group The group to add to the inheritance tree
     * @return True if successfully added
     */
    public boolean addInheritance(@NotNull AbstractGroup group) {
        Validate.notNull(group, "Group cannot be null");
        group.addInheritor(this);
        boolean ret = directlyInherits.add(group);
        if (ret) groupDataFile.reloadGroups();
        return ret;
    }

    /**
     * Removes an inherited group from a group
     *
     * @param group The group to remove from the inheritance tree
     * @return True if successfully removed
     */
    public boolean removeInheritance(@NotNull AbstractGroup group) {
        Validate.notNull(group, "Group cannot be null");
        group.removeInheritor(this);
        boolean ret = directlyInherits.remove(group);
        if (ret) groupDataFile.reloadGroups();
        return ret;
    }

    /**
     * Get a list of all the groups which this group inherits its permissions from.
     *
     * @return A list of groups this groups permissions are inherited from.
     */
    public List<AbstractGroup> getInheritedGroups() {
        List<AbstractGroup> inherited = new ArrayList<>();
        for (AbstractGroup abstractGroup : directlyInherits) {

            if (abstractGroup instanceof SuperGroup) inherited.add(abstractGroup);
            else {
                Group group = (Group) abstractGroup;
                if (!group.inheritanceLoaded) group.loadInheritance();
                inherited.add(group);
                inherited.addAll(group.getInheritedGroups());
            }
        }
        return inherited;
    }

    public void loadInheritance() {

        this.inheritanceLoaded = true;

        //fullPermissionTree.clear();
        //inherited.clear();
        directlyInherits.clear();

        for (String key : section.getStringList("inherits")) {
            if (key.startsWith("s:")) {
                SuperGroup group = plugin.getSuperGroup(key.substring(2));
                if (group == null)
                    plugin.getLogger().warning("Cannot inherit supergroup " + key + " for group " + getName() + ". Is it spelled correctly? Does it exist?");
                else {
                    group.addInheritor(this);
                    directlyInherits.add(group);
                }

            } else {
                Group group = groupDataFile.getGroup(key);
                if (group == null)
                    plugin.getLogger().warning("Cannot inherit group " + key + " for group " + getName() + ". Is it spelled correctly? Does it exist?");
                else {
                    group.addInheritor(this);
                    directlyInherits.add(group);
                }
            }
        }

        directlyInherits.forEach(g -> {
            if (g instanceof Group && !((Group) g).inheritanceLoaded) ((Group) g).loadInheritance();
            //fullPermissionTree.importPermissions(g.getPermissionTree());
        });
        //fullPermissionTree.importPermissions(groupPermissionTree);
    }

    //This is called only when the permissions of a group are changed.
    public void preInheritanceLoad() {
        getInheritedGroups().stream().filter(g -> g instanceof Group).forEach(g -> ((Group) g).inheritanceLoaded = false);
    }

    public void save() {
        section.setEntry("permissions", groupPermissionTree.getPermissionNodes().toArray(new String[0]));
        section.setEntry("inherits", directlyInherits.stream().map(group -> {
            if (group instanceof SuperGroup) return "s:".concat(group.getName());
            else return group.getName();
        }).toArray(String[]::new));
        addInfo("canBuild", canBuild);
        addInfo("rankid", rankID);
        addInfo("prefix", prefix);
        addInfo("suffix", suffix);
    }
}

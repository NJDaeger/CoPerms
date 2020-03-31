package com.njdaeger.coperms.groups;

import com.njdaeger.bcm.base.ISection;
import com.njdaeger.coperms.tree.PermissionTree;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class SuperGroup extends AbstractGroup {

    private final String name;
    private final ISection section;
    private final PermissionTree permissionTree;
    private final List<AbstractGroup> inheritors;

    public SuperGroup(String name, ISection section) {
        this.permissionTree = new PermissionTree(section.getStringList("permissions"));
        this.inheritors = new ArrayList<>();
        this.section = section;
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Set<String> getPermissions() {
        return permissionTree.getPermissionNodes();
    }

    @Override
    public PermissionTree getPermissionTree() {
        return permissionTree;
    }

    @Override
    public boolean grantPermission(@NotNull String permission) {
        Validate.notNull(permission, "Permission cannot be null");
        return permissionTree.grantPermission(permission);
    }

    @Override
    public Set<String> grantPermissions(@NotNull String... permissions) {
        Validate.notNull(permissions, "Permission cannot be null");
        Set<String> unable = new HashSet<>();
        for (String permission : permissions) {
            if (!permissionTree.grantPermission(permission)) unable.add(permission);
        }
        return unable;
    }

    @Override
    public boolean revokePermission(@NotNull String permission) {
        Validate.notNull(permission, "Permission cannot be null");
        return permissionTree.revokePermission(permission);
    }

    @Override
    public Set<String> revokePermissions(@NotNull String... permissions) {
        Validate.notNull(permissions, "Permission cannot be null");
        Set<String> unable = new HashSet<>();
        for (String permission : permissions) {
            if (!permissionTree.revokePermission(permission)) unable.add(permission);
        }
        return unable;
    }

    @Override
    public boolean removePermission(@NotNull String permission) {
        Validate.notNull(permission, "Permission cannot be null");
        return permissionTree.removePermission(permission);
    }

    @Override
    public Set<String> removePermissions(@NotNull String... permissions) {
        Validate.notNull(permissions, "Permission cannot be null");
        Set<String> unable = new HashSet<>();
        for (String permission : permissions) {
            if (!permissionTree.removePermission(permission)) unable.add(permission);
        }
        return unable;
    }

    @Override
    public boolean hasPermission(@NotNull String permission) {
        return permissionTree.hasPermission(permission);
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

    public void save() {
        section.setEntry("permissions", permissionTree.getPermissionNodes().toArray(new String[0]));
    }

}

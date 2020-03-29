package com.njdaeger.coperms.groups;

import com.njdaeger.bcm.base.ISection;
import com.njdaeger.coperms.tree.PermissionTree;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class SuperGroup extends AbstractGroup {

    private final String name;
    private final PermissionTree permissionTree;
    private final List<AbstractGroup> inheritors;

    public SuperGroup(String name, ISection section) {
        this.permissionTree = new PermissionTree(section.getStringList("permissions"));
        this.inheritors = new ArrayList<>();
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
    public Set<String> getGroupPermissions() {
        return getPermissions();
    }

    @Override
    public PermissionTree getGroupPermissionTree() {
        return getPermissionTree();
    }

    @Override
    public boolean grantPermission(@NotNull String permission) {
        return permissionTree.grantPermission(permission);
    }

    @Override
    public boolean revokePermission(@NotNull String permission) {
        return permissionTree.revokePermission(permission);
    }

    @Override
    public boolean removePermission(@NotNull String permission) {
        return false;
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

}

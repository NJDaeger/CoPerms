package com.njdaeger.coperms.tree;

public final class PermissionNode {

    private boolean granted;
    private final String permission;
    private final PermissionTree owner;

    public PermissionNode(PermissionTree owner, String permission, boolean granted) {
        this.permission = permission;
        this.granted = granted;
        this.owner = owner;
    }

    public boolean isGranted() {
        return granted;
    }

    public void setGranted(boolean granted) {
        this.granted = granted;
    }

    public String getPermission() {
        return permission;
    }

    public PermissionTree getOwner() {
        return owner;
    }

}

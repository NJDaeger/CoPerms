package com.njdaeger.coperms.groups;

import com.njdaeger.coperms.tree.PermissionTree;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public abstract class AbstractGroup {

    /**
     * Get the name of this group
     *
     * @return The name of this group.
     */
    public abstract String getName();

    /**
     * Get a set of all permissions this group has including its inherited ones.
     *
     * @return The set of permissions this group has.
     */
    public abstract Set<String> getPermissions();

    /**
     * Get the permission tree which belongs to this group
     *
     * @return The permission tree which belongs to this group.
     */
    public abstract PermissionTree getPermissionTree();

    /**
     * Get a set of the permissions which specifically belong to this group not including any inherited permissions.
     *
     * @return This specific groups permissions
     */
    public abstract Set<String> getGroupPermissions();

    /**
     * Get the permission tree which holds all this specific groups permissions.
     *
     * @return The group permission tree
     */
    public abstract PermissionTree getGroupPermissionTree();

    /**
     * Grant a permission to this group.
     *
     * @param permission The permission to grant
     * @return True if the permission was successfully granted, false if the permission was already granted before
     */
    public abstract boolean grantPermission(@NotNull String permission);

    /**
     * Grants an array of permissions and notifies its inheritors
     *
     * @param permissions The permissions to grant
     * @return The set of permissions which were unable to be granted.
     */
    public abstract Set<String> grantPermissions(@NotNull String... permissions);

    /**
     * Revoke a permission from this group (negates the permission)
     *
     * @param permission The permission to revoke
     * @return True if the permission was successfully revoked, false if the permission was already revoked before
     */
    public abstract boolean revokePermission(@NotNull String permission);

    /**
     * Revokes an array of permissions and notifies its inheritors
     *
     * @param permissions The permissions to revoke
     * @return The set of permissions which were unable to be revoked.
     */
    public abstract Set<String> revokePermissions(@NotNull String... permissions);

    /**
     * Remove a permission from this group (removes it from the tree, causing it to fall back onto its inheritance for
     * permission)
     *
     * @param permission The permission to remove
     * @return True if the permission was successfully removed, false if the permission wasnt in the tree already
     */
    public abstract boolean removePermission(@NotNull String permission);

    /**
     * Removes an array of permissions before notifying the inheritors
     *
     * @param permissions The permissions to remove
     * @return The set of permissions which were unable to be removed.
     */
    public abstract Set<String> removePermissions(@NotNull String... permissions);

    /**
     * Check if a group has a permission or not
     *
     * @param permission The permission to check for
     * @return True if the group has permission, false otherwise.
     */
    public abstract boolean hasPermission(@NotNull String permission);

    /**
     * Gets the list of abstract groups which inherit this group directly or indirectly
     *
     * @return The list of groups inheriting this group
     */
    public abstract List<AbstractGroup> getInheritors();

    /**
     * Adds an inheritor to this group
     *
     * @param group The group which is now inheriting this group
     * @return true if the group was successfully inherited, false if not
     */
    public abstract boolean addInheritor(AbstractGroup group);

    /**
     * Removes an inheritor from this group
     *
     * @param group The group which is no longer inheriting this group
     * @return True if the group was successfully un-inherited, false if not
     */
    public abstract boolean removeInheritor(AbstractGroup group);

}

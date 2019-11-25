package com.njdaeger.coperms.tree;

import java.util.HashSet;
import java.util.Set;

public class PermissionTree {

    //private final Set<PermissionNode> treeNodes; //All nodes which belong to this tree which are carried over to other trees
    //private final Set<PermissionNode> privateChangedNodes; //All local changed nodes only
    //private final Set<PermissionNode> publicChangedNodes; //All changed nodes which affect trees which inherit this
    //private final Set<PermissionTree> inheritedTrees; //All directly inherited trees

    //private final Set<PermissionNode> computedNodes; //The end result of the modified nodes.

    //public PermissionTree() {
    //    this.nodes = new HashSet<>();
    //    this.inherits = new HashSet<>();
    //}



    /*

    A:
    some.permission1
    some.permission2

    B inherits A
    some.permission3
    some.permission4

    C inherits A
    some.permission5
    some.permission6


    D inherits B
    some.permission7
    some.permission8


    silent changes:
        some.permission3 removed from B, but doesnt affect groups which inherit it

    semi-loud changes:
        some.permission1 removed from B, removed from all groups which inherit it, but does not affect A or groups which inherit it

    loud changes:
        some.permission1 removed from A, removed from all groups which inherit it

    Permission List:
    [Granted/Denied] [Edit] some.permission1

    Edit Permission
    [Granted/Denied] [Grant/Deny] some.permission1
    [Granted->Denied | Denied->Granted] [Source|Sub|Single] some.permission1
        - Source = Remove it from the group where the permission was defined in
        - Sub = Remove it from this group and the groups which inherit this group, but dont remove it from the group the permission is defined in
        - Single = Remove it from this group, but other groups which inherit this will not feel the change
     */

}

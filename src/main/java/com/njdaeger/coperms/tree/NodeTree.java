package com.njdaeger.coperms.tree;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class NodeTree {

    private final Map<String, Node> root;

    public NodeTree(Set<String> permissions) {
        this.root = new HashMap<>();
    }

    private void addPermission0(String permission) {
        boolean wildcard = permission.charAt(permission.length() - 1) == '*';
        boolean negation = permission.charAt(0) == '-';

        permission = permission.substring(negation ? 1 : 0, wildcard ? permission.length() - 2 : permission.length());

        String[] split = permission.split("\\.");

        Node parent = null;
        for (int i = 0; i < split.length; i++) {

            boolean isLast = i == split.length - 1;

            if (i == 0) {
                if (root.containsKey(split[i])) {
                    parent = root.get(split[i]);
                } else {
                    parent = new Node(split[i]);
                    root.put(split[i], parent);
                }
                if (isLast) {
                    parent.setWildcard(wildcard);
                    parent.setNegated(negation);
                    break;
                }
                continue;
            }

            if (parent == null) throw new RuntimeException("Parent is null");

            if (parent.hasChild(split[i])) {
                parent = parent.getChild(split[i]);
            } else {
                Node next = new Node(split[i]);
                parent.addChild(next);
                parent = next;
            }

            if (isLast) { //This is the last possible node, we should probably add the wildcard and negation flags.
                parent.setWildcard(wildcard);
                parent.setNegated(negation);
            }
        }
    }

    public Map<String, Node> getRootTree() {
        return root;
    }

    public boolean isExplicit(String permission) {
        String[] split = permission.split("\\.");

        Node current = null;
        for (int i = 0; i < split.length; i++) {
            if (i == 0) {
                if (root.containsKey(split[i])) current = root.get(split[i]);
                else return false;
                continue;
            }
            if (current.hasChild(split[i])) current = current.getChild(split[i]);
            else return false;
        }
        return true;
    }

    /**
     * This will add a permission to the user if they do not have it already. If the user has the wildcard which covers
     * this permission, this will return false. If the user has a negation which covers this permission the negation
     * will be removed, and this will return true. If it is a wildcard negation, the permission will be explicitly added
     * to the tree and this will return true.
     *
     * @param permission the permission the user does not have.
     * @return True if the permission was added. False otherwise.
     */
    public boolean addPermission(String permission) {
        boolean negation = permission.charAt(0) == '-';

        //If this is a negation being added, and this already doesnt have permission for the specified permission, its
        //useless to add the new permission as a negation to the current tree.
        if (negation && !hasPermission(permission)) return false;
        //If this is a negation being added, and this player does have the specified permission, we will need to
        //determine if the permission they have is through an explicit declaration or through a wildcard. If it is
        //explicit, we will need to just remove the permission, otherwise, we will need to add a negation to the tree
        //to make sure the given wildcard does not override the permission
        else if (negation && hasPermission(permission)) {
            if (isExplicit(permission)) return removePermission(permission);
            else addPermission0(permission);
        }
        else if (hasPermission(permission)) return false;
        else addPermission0(permission);
        return true;
    }

    /**
     * This will remove a permission from the user if they have the permission. If the permission being removed is
     * covered by a wildcard, a negation will be added to remove it and this will return true. If the permission is
     * explicitly given, the specified node will be removed from the tree and this will return true. If the permission
     * given is already negated, this will return false.
     *
     * @param permission The permission the user does have.
     * @return True if the permission was removed. False otherwise.
     */
    public boolean removePermission(String permission) {
    //    boolean wildcard = permission.charAt(permission.length() - 1) == '*';
    //    boolean negation = permission.charAt(0) == '-';
        return true;
    }

    /**
     * Attempts to get the given permission node.
     * @param permission The permission node to get.
     * @return The permission node if found, false otherwise.
     */
    public Node getPermission(String permission) {
        return null;
    }

    public boolean hasPermission(String permission) {
        String[] split = permission.split("\\.");
        Node current = null;

        for (int i = 0; i <  split.length; i++) {

            if (current == null) {
                if (i > 0) return false;
                else {
                    current = root.get(split[i]);
                    continue;
                }
            }

            //If it has this current node as a child, the child probably has some kind of exemption from the parent.
            if (current.hasChild(split[i])) {
                if (i == split.length - 1) return !current.getChild(split[i]).isNegated(); //We dont need to check if its a wildcard since we're directly checking the actual permission rather than a parent node. If it were a parent, it wouldnt come to this logic.
                else current = current.getChild(split[i]);
                //If we go to this, we know that we need to check if the parent has a wildcard or a negation
            } else return !current.isNegated() && current.isWildcard();
        }
        return false;
    }

}

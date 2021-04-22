package com.njdaeger.coperms.tree;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PermissionTree {

    public static void main(String[] args) {
        Set<String> permissions = new HashSet<>();
        permissions.add("essentials.build.*");
        permissions.add("-essentials.build.bedrock");
        permissions.add("essentials.set");
        permissions.add("-essentials.set.*");
        permissions.add("essentials.set.granite");
        permissions.add("essentials");
        PermissionTree perms = new PermissionTree(permissions);
        System.out.println(perms.hasPermission("essentials.build"));            //false
        System.out.println(perms.hasPermission("essentials.build.gravel"));     //true
        System.out.println(perms.hasPermission("essentials.build.bedrock"));    //false
        System.out.println(perms.hasPermission("essentials.set"));              //true
        System.out.println(perms.hasPermission("essentials.set.gravel"));       //false
        System.out.println(perms.hasPermission("essentials.set.granite"));      //true
        System.out.println(perms.hasPermission("essentials.build.granite.all"));//true
        System.out.println(perms.hasPermission("essentials.essentials"));       //false
        System.out.println(perms.grantPermission("essentials.build.bedrock"));  //true
        System.out.println(perms.grantPermission("essentials.build.bedrock"));  //false
        System.out.println("-----");
        System.out.println(perms.grantPermission("test1"));                     //true
        System.out.println(perms.grantPermission("test1"));                     //false
        System.out.println(perms.hasPermission("essentials.build.bedrock"));    //true
        System.out.println(perms.hasPermission("essentials"));                  //true
        System.out.println(perms.revokePermission("essentials"));               //true
        System.out.println(perms.hasPermission("essentials"));                  //false
        System.out.println(perms.revokePermission("essentials.build.*"));       //true
        System.out.println(perms.revokePermission("essentials.build.*"));       //false
        System.out.println(perms.hasPermission("essentials.build.gravel"));     //false
        System.out.println(perms.hasPermission("essentials.build.bedrock"));    //true
        System.out.println(perms.hasPermission("essentials.build.granite.all"));//false
        System.out.println(perms.removePermission("essentials"));               //True
        System.out.println(perms.removePermission("essentials"));               //false
        System.out.println(perms.getPermissionNodes());

        System.out.println(perms.grantPermission("test.test.test"));
        System.out.println(perms.grantPermission("test.test.test"));


        perms.printTree();
        //System.out.println(perms.getPermissionNodes());
    }

    private Map<String, Node> nodes;

    public PermissionTree() {
        this.nodes = new HashMap<>();
    }

    /**
     * Creates a new permission tree with
     *
     * @param permissions The permissions to automatically put in the tree when created.
     */
    public PermissionTree(Collection<String> permissions) {
        this.nodes = new HashMap<>();
        importPermissions(permissions);
    }

    /**
     * Prints the tree into the console
     */
    public void printTree() {
        nodes.forEach((name, node) -> {
            System.out.printf("%s [%d]\n", name, node.granted);
            node.printNode(1);
        });
    }

    /**
     * Gets a set of all the permission nodes formatted in standard non-tree format.
     *
     * @return A set of all the permission nodes
     */
    public Set<String> getPermissionNodes() {
        Set<String> perms = new HashSet<>();

        nodes.forEach((root, node) -> {
            if ((!node.hasChildren() || node.isGranted()) && !node.isInherited()) {
                perms.add((node.isNegated() ? "-" : "") + root);
            }
            getPermissionNodes0(node).forEach(n -> {
                boolean negate = n.endsWith("-");
                perms.add((negate ? "-" : "") + root + "." + (negate ? n.substring(0, n.length() - 1) : n));
            });
        });
        return perms;
    }

    /**
     * Gets a list of all the child nodes of the specified node
     *
     * @param node The node to get the children of
     * @return A set of partial permission nodes of all the children nodes of the specified node
     */
    private Set<String> getPermissionNodes0(Node node) {
        Set<String> perms = new HashSet<>();

        node.getChildren().forEach((root, child) -> {
            if ((!child.hasChildren() || child.isGranted()) && !child.isInherited()) {
                perms.add(root + (child.isNegated() ? "-" : ""));
            }
            getPermissionNodes0(child).forEach(n -> perms.add(root + "." + n));
        });
        return perms;
    }

    /**
     * Clears this permission tree
     */
    public void clear() {
        nodes.clear();
    }

    /**
     * Import a collection of string permissions and override any of the current permissions
     *
     * @param permissions The permissions to import
     */
    public void importPermissions(Collection<String> permissions) {
        for (String node : permissions) {

            //Check if the given node is negated or not and save that value
            boolean negated = node.startsWith("-");
            //If it is negated, remove the negated character
            if (negated) node = node.substring(1);

            //Split the permission node into its parts
            String[] parts = node.split("\\.");

            //Check if the current root map contains any mapping for the first part of the node
            //If it doesnt, create a mapping for it. If the node does not start with the negation character, and if
            //the parts array is only one in length, then the user will be granted permission to that root node (Note, it is not a wildcard)
            if (!nodes.containsKey(parts[0])) nodes.put(parts[0], new Node((byte) ((parts.length == 1) ? (negated ? -1 : 1) : 0))); //We wont enter inherited permissions into the tree

            //We automatically set the last node to the root node by default
            Node lastNode = nodes.get(parts[0]);

            for (int i = 1; i < parts.length; i++) {

                //if the last node has a child of one of its
                if (lastNode.hasChild(parts[i])) {
                    if (parts.length - 1 == i) lastNode.getChild(parts[i]).granted = (byte)(negated ? -1 : 1);
                    else lastNode = lastNode.getChild(parts[i]);

                } else lastNode = lastNode.addChild(parts[i], (byte) ((parts.length - 1 == i) ? (negated ? -1 : 1) : 0));
            }

            if (parts.length == 1) lastNode.granted = (byte) (negated ? -1 : 1);
        }
    }

    /**
     * Import another permission tree and override any of the current permissions
     *
     * @param permissionTree The permission tree to import
     */
    public void importPermissions(PermissionTree permissionTree) {
        importPermissions(permissionTree.getPermissionNodes());
    }

    /**
     * Checks if this permission tree and its inherited trees has a permission granted.
     *
     * @param permission The permission to search for
     * @return True if the permission is granted, false otherwise
     */
    public boolean hasPermission(String permission) {
        String[] parts = permission.split("\\.");
        Node lastNode = nodes.get(parts[0]);
        if (lastNode == null) return nodes.containsKey("*") && nodes.get("*").isGranted();

        for (int i = 1; i < parts.length; i++) {
            if (lastNode.hasChild(parts[i])) {
                if (parts.length - 1 == i) return lastNode.getChild(parts[i]).isGranted();
                else lastNode = lastNode.getChild(parts[i]);
            } else return lastNode.hasChild("*") && lastNode.getChild("*").isGranted();
        }
        return lastNode.isGranted();
    }

    /**
     * Gets the granted state of a specific permission node.
     * @param permission The permission node to get the granted state of
     * @return 0 if the permission isn't defined, -1 if the permission is negated, or 1 if the tree has permission.
     */
    public byte getGrantedState(String permission) {
        String[] parts = permission.split("\\.");
        Node lastNode = nodes.get(parts[0]);
        if (lastNode == null) return nodes.containsKey("*") ? nodes.get("*").granted : 0;
        for (int i = 1; i < parts.length; i++) {
            if (lastNode.hasChild(parts[i])) {
                if (parts.length - 1 == i) return lastNode.getChild(parts[i]).granted;
                else lastNode = lastNode.getChild(parts[i]);
            } else return lastNode.hasChild("*") ? lastNode.getChild("*").granted : 0;
        }
        return lastNode.granted;
    }

    /**
     * Checks if the given permission is explicitly defined in here (regardless of whether it's granted or not)
     *
     * @param permission The permission to look for
     * @return True if the permission is explicitly defined in here, false otherwise.
     */
    public boolean isPermissionDefined(String permission) {
        String[] parts = permission.split("\\.");
        Node lastNode = nodes.get(parts[0]);
        if (lastNode == null) return false;

        for (int i = 1; i < parts.length; i++) {
            if (!lastNode.hasChild(parts[i])) return lastNode.hasChild("*");
        }
        return true;
    }

    /**
     * Grant a permission to this permission tree. If the permission given does not exist in the tree, it will be added
     * in and granted. If the permission does exist, and it is not granted, it will be set to granted.
     *
     * @param permission The permission to add
     * @return True if the permission was added, false otherwise.
     */
    public boolean grantPermission(String permission) {

        //Split the permission node into its parts
        String[] parts = permission.split("\\.");

        boolean newSingleNode = false;

        //Check if the current root map contains any mapping for the first part of the node
        //If it doesnt, create a mapping for it. If the parts array is only one in length,
        //then the user will be granted permission to that root node
        if (!nodes.containsKey(parts[0])) {
            if (parts.length == 1) newSingleNode = true;
            nodes.put(parts[0], new Node((byte) (parts.length == 1 ? 1 : 0)));
        }

        //If the parts array is only one long and if the node is new, we just return true by default.
        if (newSingleNode) return true;

        //We automatically set the last node to the root node by default
        Node lastNode = nodes.get(parts[0]);

        if (parts.length == 1 && lastNode.isGranted()) return false;

        for (int i = 1; i < parts.length; i++) {

            //if the last node has a child of one of its
            if (lastNode.hasChild(parts[i])) {
                if (parts.length - 1 == i) {
                    if (lastNode.getChild(parts[i]).isGranted()) return false;
                    else {
                        lastNode.getChild(parts[i]).granted = 1;
                        return true;
                    }
                } else lastNode = lastNode.getChild(parts[i]);
            } else lastNode = lastNode.addChild(parts[i], (byte) (parts.length - 1 == i ? 1 : 0));
        }
        if (!lastNode.isGranted()) {
            lastNode.granted = 1;
            return true;
        }
        return true;
    }

    /**
     * Revoke a permission from this permission tree. If this permission exists in the tree, it will be ungranted- if
     * the permission does not exist in the tree, it will be added and ungranted
     *
     * @param permission The permission to revoke
     * @return True if the permission was successfully revoked, false otherwise (if it was revoked previously)
     */
    public boolean revokePermission(String permission) {
        //Split the permission node into its parts
        String[] parts = permission.split("\\.");

        boolean revokeSingleNewNode = false;

        //Check if the current root map contains any mapping for the first part of the node
        //If it doesnt, create a mapping for it. If the parts array is only one in length,
        //then the user will be granted permission to that root node
        if (!nodes.containsKey(parts[0])) {
            if (parts.length == 1) revokeSingleNewNode = true;
            nodes.put(parts[0], new Node((byte) (parts.length == 1 ? -1 : 0)));
        }

        //If the parts array is only one long and if the node is new, we just return true by default.
        if (revokeSingleNewNode) return true;

        //We automatically set the last node to the root node by default
        Node lastNode = nodes.get(parts[0]);

        if (parts.length == 1 && lastNode.isNegated()) return false;


        for (int i = 1; i < parts.length; i++) {

            if (lastNode.hasChild(parts[i])) {
                if (parts.length - 1 == i) {
                    if (lastNode.getChild(parts[i]).isNegated()) return false;
                    else {
                        lastNode.getChild(parts[i]).granted = -1;
                        return true;
                    }
                } else lastNode = lastNode.getChild(parts[i]);
            } else lastNode = lastNode.addChild(parts[i], (byte) (parts.length - 1 == i ? -1 : 0));
        }
        if (!lastNode.isNegated()) {
            lastNode.granted = -1;
            return true;
        }
        return true;
    }

    /**
     * Removes a permission from this tree via setting its granted state to "inherit". If the state is set it inherit,
     * it acts like the tree doesnt have permission inherently AND the node doesnt "technically" exist in terms of the
     * tree, even though it is still contained in the tree. Its common use is when you want to export permissions to
     * another tree, where if the other tree has permission for something you want to keep, if that same permission is
     * set to inherit in this tree, it will not override the other tree.
     *
     * @param permission The permission to revoke
     * @return True if the permission was successfully revoked, false otherwise (if it was revoked previously)
     */
    public boolean removePermission(String permission) {
        //Split the permission node into its parts
        String[] parts = permission.split("\\.");

        boolean newRemovedPerm = false;

        //Check if the current root map contains any mapping for the first part of the node
        //If it doesnt, create a mapping for it. If the parts array is only one in length,
        //then the user will be granted permission to that root node
        if (!nodes.containsKey(parts[0])) {
            if (parts.length == 1) newRemovedPerm = true;
            nodes.put(parts[0], new Node((byte) 0));
        }

        //If the parts array is only one long and if the node is new, we just return true by default.
        if (newRemovedPerm) return true;

        //We automatically set the last node to the root node by default
        Node lastNode = nodes.get(parts[0]);

        if (parts.length == 1 && lastNode.isInherited()) return false;

        for (int i = 1; i < parts.length; i++) {

            if (lastNode.hasChild(parts[i])) {
                if (parts.length - 1 == i) {
                    if (lastNode.getChild(parts[i]).isInherited()) return false;
                    else {
                        lastNode.getChild(parts[i]).granted = 0;
                        return true;
                    }
                } else lastNode = lastNode.getChild(parts[i]);
            } else lastNode = lastNode.addChild(parts[i], (byte) 0);
        }
        if (!lastNode.isInherited()) {
            lastNode.granted = 0;
            return true;
        }
        return true;
    }

    /**
     * Represents a node in the permission tree
     */
    public class Node {

        private Map<String, Node> children;
        private byte granted;

        /**
         * Creates a new permission node
         *
         * @param granted Whether this node was granted or not
         */
        private Node(byte granted) {
            this.granted = granted;
            this.children = new HashMap<>();
        }

        /**
         * Whether the granted state is set to 1.
         *
         * @return True if the node is granted.
         */
        public boolean isGranted() {
            return granted == 1;
        }

        /**
         * Whether the granted state is set to -1.
         *
         * @return True if the node is negated.
         */
        public boolean isNegated() {
            return granted == -1;
        }

        /**
         * Whether the granted state is set to 0.
         *
         * @return True if the node is inherited.
         */
        public boolean isInherited() {
            return granted == 0;
        }

        /**
         * Add a child node to this node
         *
         * @param node The name of the child node
         * @param granted Whether the child node was granted
         * @return The new child node
         */
        private Node addChild(String node, byte granted) {
            Node child = new Node(granted);
            children.put(node, child);
            return child;
        }

        /**
         * Gets a child node via its name
         *
         * @param node The name of the child node to find
         * @return the child node if found, or null otherwise
         */
        private Node getChild(String node) {
            return children.get(node);
        }

        /**
         * Gets a map of all the children of this node
         *
         * @return A map of child nodes
         */
        private Map<String, Node> getChildren() {
            return children;
        }

        /**
         * Check whether this node has any children or not.
         *
         * @return True if it has children, false otherwise.
         */
        private boolean hasChildren() {
            return !children.isEmpty();
        }

        /**
         * Check whether a specific child exists in this node
         *
         * @param node The child node to look for
         * @return True if the child node exists, false otherwise
         */
        private boolean hasChild(String node) {
            return children.containsKey(node);
        }

        /**
         * Prints this node and its children
         */
        private void printNode(int depth) {
            children.forEach((name, node) -> {
                int i = 0;
                while (i++ < depth) System.out.print("\t");
                System.out.printf("%s [%d]\n", name, node.granted);
                node.printNode(depth + 1);
            });
        }

    }

}

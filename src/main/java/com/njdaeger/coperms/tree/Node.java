package com.njdaeger.coperms.tree;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a node in the permission tree
 */
public final class Node {

    private final List<Node> children;
    private final String node;
    private boolean wildcard;
    private boolean negated;
    private Node parent;

    public Node(String node) {
        this.children = new ArrayList<>();
        this.node = node;
        this.parent = null;
    }

    public boolean isNegated() {
        return negated;
    }

    public boolean isWildcard() {
        return wildcard;
    }

    public void setNegated(boolean negated) {
        this.negated = negated;
    }

    public void setWildcard(boolean wildcard) {
        this.wildcard = wildcard;
    }

    public String getNode() {
        return node;
    }

    public Node getParent() {
        return parent;
    }

    public List<Node> getChildren() {
        return children;
    }

    public void addChild(Node child) {
        child.parent = this;
        children.add(child);
    }

    @NotNull
    public Node getChild(String child) {
        for (Node node : children) {
            if (node.getNode().equalsIgnoreCase(child)) return node;
        }
        throw new RuntimeException("\'" + child + "\' not found as a child of \'" + node + "\'");
    }

    public boolean hasChild(String child) {
        for (Node node : children) {
            if (node.getNode().equalsIgnoreCase(child)) return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return parent == null ? node : parent.toString() + "->" + node;
    }
}

package com.njdaeger.coperms.groups;

import com.njdaeger.bcm.base.ISection;
import com.njdaeger.coperms.tree.Node;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("WeakerAccess")
public final class SuperGroup extends AbstractGroup {

    private final String name;
    private final Set<String> permissions;
    //private final Map<String, Node> permissions;

    public SuperGroup(String name, ISection section) {
        this.permissions = new HashSet<>(section.getStringList("permissions"));
        //this.permissions = new HashSet<>(section.getStringList("permissions"));
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Set<String> getPermissions() {
        return permissions;
    }

}

package com.coalesce.coperms.data;

import com.coalesce.core.config.base.ISection;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unchecked")
public final class SuperGroup {

    private final String name;
    private final Set<String> permissions;

    public SuperGroup(String name, ISection section) {
        this.permissions = (Set<String>)section.getStringList("permissions");
        this.name = name;
    }

    /**
     * Gets the name of the SuperGroup
     *
     * @return The super group name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the list of permissions in this super group
     *
     * @return The super group permissions
     */
    public Set<String> getPermissions() {
        return permissions;
    }

}

package com.njdaeger.coperms.groups;

import java.util.Set;

public abstract class AbstractGroup {
    
    /**
     * Get the name of this group
     * @return The name of this group.
     */
    public abstract String getName();
    
    /**
     * Get a set of permissions this group has.
     *
     * @return The set of permissions this group has.
     */
    public abstract Set<String> getPermissions();
    
}

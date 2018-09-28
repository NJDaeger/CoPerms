package com.njdaeger.coperms.configuration;

import com.njdaeger.coperms.CoPerms;
import com.njdaeger.coperms.groups.SuperGroup;
import com.njdaeger.bcm.Configuration;
import com.njdaeger.bcm.base.ConfigType;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class SuperDataFile extends Configuration {

    private final Set<SuperGroup> superGroups;

    public SuperDataFile(CoPerms plugin) {
        super(plugin, ConfigType.YML, "supergroups");
        this.superGroups = new HashSet<>();

        if (!hasSection("super")) {
            addEntry("super.default.permissions", Collections.singletonList("*"));
        }
        getSection("super").getKeys(false).forEach(key -> superGroups.add(new SuperGroup(key, getSection("super." + key))));
    }
    
    /**
     * Get a set of all the current supergroups.
     * @return A set of all the supergroups
     */
    public Set<SuperGroup> getSuperGroups() {
        return superGroups;
    }


}

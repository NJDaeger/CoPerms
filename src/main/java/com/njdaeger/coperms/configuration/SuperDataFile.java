package com.njdaeger.coperms.configuration;

import com.njdaeger.bcm.Configuration;
import com.njdaeger.bcm.base.ConfigType;
import com.njdaeger.coperms.CoPerms;
import com.njdaeger.coperms.groups.SuperGroup;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class SuperDataFile extends Configuration {

    private final Map<String, SuperGroup> superGroups;

    public SuperDataFile(CoPerms plugin) {
        super(plugin, ConfigType.YML, "supergroups");
        this.superGroups = new HashMap<>();

        if (!hasSection("super")) {
            addEntry("super.default.permissions", Collections.singletonList("*"));
        }
        getSection("super").getKeys(false).forEach(key -> superGroups.put(key, new SuperGroup(key, getSection("super." + key))));
    }
    
    /**
     * Get a set of all the current supergroups.
     * @return A set of all the supergroups
     */
    public Map<String, SuperGroup> getSuperGroups() {
        return superGroups;
    }


}

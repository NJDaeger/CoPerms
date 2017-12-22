package com.coalesce.coperms.configuration;

import com.coalesce.coperms.CoPerms;
import com.coalesce.coperms.data.Group;
import com.coalesce.core.config.YmlConfig;
import org.bukkit.World;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class GroupDataFile extends YmlConfig {

    private final List<Group> groups;

    public GroupDataFile(CoPerms plugin, World world) {
        super("worlds" + File.separator + world.getName() + File.separator + "groups", plugin);

        this.groups = new ArrayList<>();

        if (!contains("groups", false)) {
            addEntry("groups.default.permissions", Arrays.asList("ttb.generate", "ttb.undo", "ttb.redo"));
            addEntry("groups.default.inherits", Collections.emptyList());
            addEntry("groups.default.info.canBuild", true);
            addEntry("groups.default.info.prefix", "");
            addEntry("groups.default.info.suffix", "");
            addEntry("groups.default.info.rankid", 0);
        }
    }

    /**
     * Gets a list of groups held in this file.
     *
     * @return The groups list.
     */
    public List<Group> getGroups() {
        return groups;
    }
}

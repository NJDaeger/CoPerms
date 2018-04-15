package com.coalesce.coperms.configuration;

import com.coalesce.coperms.CoPerms;
import com.coalesce.coperms.DataLoader;
import com.coalesce.coperms.data.CoWorld;
import com.coalesce.coperms.data.Group;
import com.coalesce.core.config.YmlConfig;
import org.bukkit.World;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"unused", "WeakerAccess"})
public final class GroupDataFile extends YmlConfig {

    
    private final Group defaultGroup;
    private final List<CoWorld> worlds;
    private final Map<String, Group> groups;
    private final Map<Integer, String> groupIds;
    
    /**
     * Generates a groupdata file
     * @param plugin The plugin instance
     * @param loader The data loader instance
     * @param world The world the datafile was originally created with.
     */
    public GroupDataFile(CoPerms plugin, DataLoader loader, UserDataFile userDataFile, World world) {
        super("worlds" + File.separator + world.getName() + File.separator + "groups", plugin);
        
        this.groups = new HashMap<>();
        this.groupIds = new HashMap<>();
        this.worlds = new ArrayList<>();
        
        if (!contains("groups", false)) {
            addEntry("groups.default.permissions", Arrays.asList("ttb.generate", "ttb.undo", "ttb.redo"));
            addEntry("groups.default.inherits", Collections.emptyList());
            addEntry("groups.default.info.canBuild", true);
            addEntry("groups.default.info.prefix", "");
            addEntry("groups.default.info.suffix", "");
            addEntry("groups.default.info.rankid", 0);
        }
        
        getSection("groups").getKeys(false).forEach(k -> groups.put(k.toLowerCase(), new Group(this, userDataFile, k, loader)));
        groups.values().forEach(Group::load);
        groups.values().forEach(Group::loadInheritanceTree);
        groups.values().forEach(g -> groupIds.put(g.getRankID(), g.getName()));
        
        int[] arr = groupIds.keySet().stream().mapToInt(Integer::intValue).toArray();
        Arrays.sort(arr);
        
        this.defaultGroup = getGroup(arr[0]);
        
    }

    public Map<String, Group> getGroupMap() {
        return groups;
    }
    
    /**
     * Gets a list of groups held in this file.
     *
     * @return The groups list.
     */
    public Collection<Group> getGroups() {
        return groups.values();
    }
    
    public Group getGroup(String name) {
        return groups.get(name.toLowerCase());
    }
    
    public Group getGroup(int id) {
        if (groupIds.get(id) == null) return null;
        return getGroup(groupIds.get(id));
    }
    
    public Group getDefaultGroup() {
        return defaultGroup;
    }
    
    public List<CoWorld> getWorlds() {
        return worlds;
    }
    
    public void addWorld(CoWorld world) {
        worlds.add(world);
    }
    
}

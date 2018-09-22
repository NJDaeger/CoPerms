package com.njdaeger.coperms;

import com.njdaeger.coperms.commands.PermissionCommands;
import com.njdaeger.coperms.commands.UserCommands;
import com.njdaeger.coperms.configuration.GroupDataFile;
import com.njdaeger.coperms.configuration.UserDataFile;
import com.njdaeger.coperms.data.CoWorld;
import com.njdaeger.coperms.data.SuperGroup;
import com.njdaeger.bcm.base.ISection;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings({"unused", "WeakerAccess"})
public final class DataLoader {
    
    private final Map<String, GroupDataFile> groupDataFiles;
    private final Map<String, UserDataFile> userDataFiles;
    private final Map<String, SuperGroup> supers;
    private final Map<String, CoWorld> worlds;
    private final Set<World> loaded;
    private final Set<World> queue;
    private final ISection mirrors;
    private DataHolder dataHolder;
    private final CoPerms plugin;
    private final String def;

    /**
     * Create a new module
     *
     * @param plugin The plugin that's creating this module
     */
    public DataLoader(CoPerms plugin) {
        this.mirrors = plugin.getPermsConfig().getSection("mirrors");
        this.def = Bukkit.getWorlds().get(0).getName();
        this.groupDataFiles = new HashMap<>();
        this.userDataFiles = new HashMap<>();
        this.supers = new HashMap<>();
        this.worlds = new HashMap<>();
        this.loaded = new HashSet<>();
        this.queue = new HashSet<>();
        this.plugin = plugin;
    }

    //All other worlds needs to be any worlds that weren't specified in the configuration.
    
    public void enable() {
        plugin.getSuperDataFile().getSuperGroups().forEach(g -> supers.put(g.getName().toLowerCase(), g));
        Bukkit.getWorlds().forEach(this::loadData);
        queue.forEach(this::loadOtherWorlds);
        loadWorlds();

        this.dataHolder = new DataHolder(this, plugin);
        new DataListener(dataHolder, plugin);
        new UserCommands(plugin, dataHolder);
        new PermissionCommands(plugin, dataHolder);

        if (!Bukkit.getOnlinePlayers().isEmpty()) {
            Bukkit.getOnlinePlayers().forEach(p -> {
                dataHolder.loadUser(p.getWorld(), p.getUniqueId());
                new Inject(p);
            });
        }
    }
    
    public void disable() {
        if (!Bukkit.getOnlinePlayers().isEmpty()) {
            Bukkit.getOnlinePlayers().forEach(p -> dataHolder.unloadUser(p.getUniqueId()));
        }
    }

    /**
     * Gets the data holder for the plugin.
     *
     * @return The data folder.
     */
    DataHolder getDataHolder() {
        return dataHolder;
    }

    /**
     * Gets the loaded worlds
     *
     * @return The loaded worlds
     */
    Map<String, CoWorld> getWorlds() {
        return worlds;
    }

    /**
     * Gets a SuperGroup if exists
     *
     * @param name The name of the SuperGroup
     * @return The SuperGroup
     */
    public SuperGroup getSuperGroup(String name) {
        if (!supers.containsKey(name)) {
            return null;
        }
        return supers.get(name.toLowerCase());
    }

    /**
     * Gets a list of all the SuperGroups
     *
     * @return The server SuperGroups
     */
    public Map<String, SuperGroup> getSuperGroups() {
        return supers;
    }
    
    private void loadWorlds() {
        loaded.forEach(world -> {
            CoWorld cw = new CoWorld(world);
            cw.load(userDataFiles.get(world.getName()), groupDataFiles.get(world.getName()));
            worlds.put(world.getName(), cw);
        });
        
    }
    
    /**
     * Loads the data from a world that is contained in the mirrors configuration section.
     *
     * @param world The world to load the data from.
     */
    private void loadData(World world) {

        //Mirrors must contain the default world
        if (mirrors.contains(world.getName())) {
            
            List<String> mirror = mirrors.getStringList(world.getName());
            //if the world in mirrors has both the groups and users string, then load the datafiles.
            if (mirror.contains("groups") && mirror.contains("users")) {
                userDataFiles.put(world.getName(), new UserDataFile(plugin, world));
                groupDataFiles.put(world.getName(), new GroupDataFile(plugin, this, userDataFiles.get(world.getName()), world));
                loaded.add(world);
            }

            //we need to resolve what else is contained in this datafile
            else {
                if (mirrors.getStringList(world.getName()) == null) {
                    if (world.equals(Bukkit.getWorlds().get(0))) {
                        throw new RuntimeException("Default world must have both the groups file and users file specified.");
                    }
                    userDataFiles.put(world.getName(), userDataFiles.get(def));
                    groupDataFiles.put(world.getName(), groupDataFiles.get(def));
                    loaded.add(world);
                    return;
                }
                loadKeys(mirrors.getStringList(world.getName()), world);
            }
        } else {
            queue.add(world);
        }
    }

    /**
     * Loads the "all-other-worlds" key in the mirrors config section.
     *
     * @param world A world from the queue list.
     */
    private void loadOtherWorlds(World world) {
        //If the mirrors section of the config contains "all-other-worlds" then we will load the keys and generate files for each world.
        if (mirrors.contains("all-other-worlds")) {
            loadKeys(mirrors.getStringList("all-other-worlds"), world);
            return;
        }

        //All other worlds will get their data from the default world.
        userDataFiles.put(world.getName(), userDataFiles.get(def));
        groupDataFiles.put(world.getName(), groupDataFiles.get(def));
        loaded.add(world);
    }

    private void loadKeys(List<String> keys, World world) {
        //Lets go through each key in this list.
        keys.forEach(key -> {

            //If even one key matches a loaded world name, then the loop stops and both datafiles are used from the found world
            if (loaded.contains(Bukkit.getWorld(key))) {
                userDataFiles.put(world.getName(), userDataFiles.get(key));
                groupDataFiles.put(world.getName(), groupDataFiles.get(key));
                return;
            }

            //If a key equals "users" then it creates a new userfile for this world
            if (key.equalsIgnoreCase("users")) {
                userDataFiles.put(world.getName(), new UserDataFile(plugin, world));
            }
    
            //If a key equals "groups" then it creates a new groupfile for this world
            if (key.equalsIgnoreCase("groups")) {
                groupDataFiles.put(world.getName(), new GroupDataFile(plugin, this, userDataFiles.get(world.getName()), world));
            }

            //If the key contains a colon, then we know the key means its getting data from elsewhere.
            if (key.contains(":")) {
                String[] s = key.split(":");
                if (Bukkit.getWorld(s[0]) == null) {
                    throw new RuntimeException("World " + s[0] + " does not exist.");
                }

                //Gets the user file from the specified world
                if (s[1].equalsIgnoreCase("users")) {
                    userDataFiles.put(world.getName(), userDataFiles.get(s[0]));
                }

                //Gets the group file from the specified world
                if (s[1].equalsIgnoreCase("groups")) {
                    groupDataFiles.put(world.getName(), groupDataFiles.get(s[0]));
                }

            }
        });

        //We need to load something, so we load the default worlds data.
        userDataFiles.putIfAbsent(world.getName(), userDataFiles.get(def));
        groupDataFiles.putIfAbsent(world.getName(), groupDataFiles.get(def));
        loaded.add(world);
    }

}

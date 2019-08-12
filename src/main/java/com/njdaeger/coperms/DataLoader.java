package com.njdaeger.coperms;

import com.njdaeger.bcm.base.ISection;
import com.njdaeger.coperms.commands.PermissionCommands;
import com.njdaeger.coperms.commands.UserCommands;
import com.njdaeger.coperms.configuration.GroupDataFile;
import com.njdaeger.coperms.configuration.UserDataFile;
import com.njdaeger.coperms.data.CoWorld;
import com.njdaeger.coperms.groups.SuperGroup;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class DataLoader {
    
    private final Map<String, GroupDataFile> groupDataFiles;
    private final Map<String, UserDataFile> userDataFiles;
    private final Map<String, SuperGroup> supers;
    private final Map<String, CoWorld> worlds;
    private final ISection mirrors;
    private DataHolder dataHolder;
    private final CoPerms plugin;
    private final String def;

    /*

    Read config to see what group data and user data each world gets


     */

    /**
     * Create a new module
     *
     * @param plugin The plugin that's creating this module
     */
    DataLoader(CoPerms plugin) {
        this.mirrors = plugin.getPermsConfig().getSection("mirrors");
        this.def = Bukkit.getWorlds().get(0).getName();
        this.groupDataFiles = new HashMap<>();
        this.userDataFiles = new HashMap<>();
        this.supers = new HashMap<>();
        this.worlds = new HashMap<>();
        this.plugin = plugin;
    }

    //All other worlds needs to be any worlds that weren't specified in the configuration.
    
    void enable() {
        this.dataHolder = new DataHolder(this, plugin);
        plugin.getSuperDataFile().getSuperGroups().forEach(g -> supers.put(g.getName().toLowerCase(), g));
        loadWorlds();
        
        new DataListener(dataHolder, plugin);
        new UserCommands(plugin, dataHolder);
        new PermissionCommands(plugin, dataHolder);

        if (!Bukkit.getOnlinePlayers().isEmpty()) {
            Bukkit.getOnlinePlayers().forEach(p -> {
                dataHolder.loadUser(p.getWorld(), p.getUniqueId());
                Injector.inject(p);
            });
        }
    }
    
    void disable() {
        if (!Bukkit.getOnlinePlayers().isEmpty()) {
            Bukkit.getOnlinePlayers().forEach(p -> dataHolder.unloadUser(p.getUniqueId()));
        }
    }
    
    /**
     * Get the CoPerms Plugin
     * @return The CoPerms plugin
     */
    public CoPerms getPlugin() {
        return plugin;
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
     * Gets a list of all the SuperGroups
     *
     * @return The server SuperGroups
     */
    Map<String, SuperGroup> getSuperGroups() {
        return supers;
    }

    private void loadWorlds() {

        List<World> queue = new ArrayList<>();

        Bukkit.getWorlds().forEach(world -> {
            if (mirrors.contains(world.getName(), true)) {

                List<String> mirror = mirrors.getStringList(world.getName());
                //if the world in mirrors has both the groups and users string, then load the datafiles.
                if (mirror.contains("groups") && mirror.contains("users")) {
                    UserDataFile uf = new UserDataFile(plugin, world);
                    userDataFiles.put(world.getName(), uf);
                    groupDataFiles.put(world.getName(), new GroupDataFile(plugin, this, uf, world));
                    worlds.put(world.getName(), new CoWorld(world, userDataFiles.get(world.getName()), groupDataFiles.get(world.getName())));
                }

                //we need to resolve what else is contained in this datafile
                else {
                    if (mirrors.getStringList(world.getName()) == null) {
                        if (world.equals(Bukkit.getWorlds().get(0))) {
                            throw new RuntimeException("Default world must have both the groups file and users file specified.");
                        }
                        userDataFiles.put(world.getName(), userDataFiles.get(def));
                        groupDataFiles.put(world.getName(), groupDataFiles.get(def));
                        worlds.put(world.getName(), new CoWorld(world, userDataFiles.get(world.getName()), groupDataFiles.get(world.getName())));
                    } else parseKeys(mirrors.getStringList(world.getName()), world);
                }
            } else queue.add(world);
        });

        //The queued worlds rely on other worlds to be loaded to get their data.
        queue.forEach(world -> {
            //If the mirrors section of the config contains "all-other-worlds" then we will load the keys and generate files for each world.
            if (mirrors.contains("all-other-worlds", true)) {
                parseKeys(mirrors.getStringList("all-other-worlds"), world);
            } else {
                //All other worlds will get their data from the default world.
                userDataFiles.put(world.getName(), userDataFiles.get(def));
                groupDataFiles.put(world.getName(), groupDataFiles.get(def));
                worlds.put(world.getName(), new CoWorld(world, userDataFiles.get(world.getName()), groupDataFiles.get(world.getName())));
            }
        });

    }

    /**
     * This will parse the keys within the mirrors section of the configuration to determine what group data and user data each world has.
     * @param keys The keys contained within the world key
     * @param world The world which keys are being loaded.
     */
    private void parseKeys(List<String> keys, World world) {
        //Lets go through each key in this list.
        keys.forEach(key -> {

            //If even one key matches a loaded world name, then the loop stops and both datafiles are used from the found world
            if (worlds.containsKey(key)) {
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
                if (s[0] == null || s[0].isEmpty() || Bukkit.getWorld(s[0]) == null) {
                    throw new RuntimeException("World " + s[0] + " does not exist.");
                }

                //Gets the user file from the specified world
                if (s[1].equalsIgnoreCase("users")) {
                    userDataFiles.put(world.getName(), userDataFiles.get(s[0]));
                }

                //Gets the group file from the specified world
                else if (s[1].equalsIgnoreCase("groups")) {
                    groupDataFiles.put(world.getName(), groupDataFiles.get(s[0]));
                }
                else Bukkit.getLogger().warning(s[0] + ":" + s[1] + " cannot be inherited to world " + world.getName());

            }
        });
        //We need to load something, so we load the default worlds data.
        userDataFiles.putIfAbsent(world.getName(), userDataFiles.get(def));
        groupDataFiles.putIfAbsent(world.getName(), groupDataFiles.get(def));
        worlds.put(world.getName(), new CoWorld(world, userDataFiles.get(world.getName()), groupDataFiles.get(world.getName())));
    }

}

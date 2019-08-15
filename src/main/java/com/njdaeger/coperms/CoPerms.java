package com.njdaeger.coperms;

import com.njdaeger.bci.defaults.CommandStore;
import com.njdaeger.bcm.base.ISection;
import com.njdaeger.coperms.commands.PermissionCommands;
import com.njdaeger.coperms.commands.UserCommands;
import com.njdaeger.coperms.configuration.CoPermsConfig;
import com.njdaeger.coperms.configuration.GroupDataFile;
import com.njdaeger.coperms.configuration.SuperDataFile;
import com.njdaeger.coperms.configuration.UserDataFile;
import com.njdaeger.coperms.data.CoUser;
import com.njdaeger.coperms.data.CoWorld;
import com.njdaeger.coperms.groups.Group;
import com.njdaeger.coperms.groups.SuperGroup;
import com.njdaeger.coperms.vault.Chat_CoPerms;
import com.njdaeger.coperms.vault.Permission_CoPerms;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class CoPerms extends JavaPlugin implements CoPermsAPI{

    private static CoPerms INSTANCE;

    private Map<String, GroupDataFile> groupDataFiles;
    private Map<String, UserDataFile> userDataFiles;
    private Map<String, SuperGroup> superGroups;
    private Map<String, CoWorld> worlds;
    private CommandStore commandStore;
    private CoPermsConfig config;
    private SuperDataFile supers;
    private String defaultWorld;
    private ISection mirrors;

    @Override
    public void onEnable() {
        INSTANCE = this;

        //Creating configurations, command store, and metrics
        this.config = new CoPermsConfig(this);
        this.supers = new SuperDataFile(this);
        this.commandStore = new CommandStore(this);
        new Metrics(this);

        //Setting up maps
        this.groupDataFiles = new HashMap<>();
        this.userDataFiles = new HashMap<>();
        this.worlds = new HashMap<>();

        //Setup vault
        if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
            Permission_CoPerms perms = new Permission_CoPerms(this);
            getServer().getServicesManager().register(Permission.class, perms, this, ServicePriority.High);
            getServer().getServicesManager().register(Chat.class, new Chat_CoPerms(this, perms), this, ServicePriority.High);
        } else getLogger().warning("Vault is not hooked into CoPerms... Loading anyway...");

        //Setting some defaults before we start the loading of worlds and groups.
        this.mirrors = config.getSection("mirrors");
        this.defaultWorld = Bukkit.getWorlds().get(0).getName();
        this.superGroups = supers.getSuperGroups();
        loadWorlds();

        //Listener and command loading
        new DataListener(this);
        new UserCommands(this);
        new PermissionCommands(this);
    }

    @Override
    public void onDisable() {
        final int[] totalSaved = {0};
        getWorlds().values().forEach(world -> {
            world.getGroups().values().forEach(Group::save);
            totalSaved[0] += world.getUsers().values().stream().filter(CoUser::hasChanged).count();
            world.getUsers().values().stream().filter(CoUser::hasChanged).forEach(CoUser::save);
        });
        getLogger().info("Saved " + totalSaved[0] + " users to their respective world user base.");
    }

    @Override
    public CoWorld getDefaultWorld() {
        return getWorld(Bukkit.getWorlds().get(0));
    }

    @Override
    public CoUser getUser(@NotNull World world, @NotNull UUID uuid) {
        Validate.notNull(world, "World cannot be null");
        Validate.notNull(uuid, "UUID cannot be null");
        return getWorld(world).getUser(uuid);
    }

    @Override
    public CoUser getUser(@NotNull World world, @NotNull String name) {
        Validate.notNull(world, "World cannot be null");
        Validate.notNull(name, "Name cannot be null");
        return getWorld(world).getUser(name);
    }

    @Override
    public CoUser getUser(@NotNull String world, @NotNull UUID uuid) {
        Validate.notNull(world, "World cannot be null");
        Validate.isTrue(Bukkit.getWorld(world) != null, "World must exist");
        Validate.notNull(uuid, "UUID cannot be null");
        return getWorld(world).getUser(uuid);
    }

    @Override
    public CoUser getUser(@NotNull String world, @NotNull String name) {
        Validate.notNull(world, "World cannot be null");
        Validate.isTrue(Bukkit.getWorld(world) != null, "World must exist");
        Validate.notNull(name, "Name cannot be null");
        return getWorld(world).getUser(name);
    }

    @Override
    public CoUser getOnlineUser(@NotNull UUID uuid) {
        return getWorld(Bukkit.getPlayer(uuid).getWorld()).getUser(uuid);
    }

    @Override
    public CoUser getOnlineUser(@NotNull String name) {
        return getWorld(Bukkit.getPlayer(name).getWorld()).getUser(name);
    }

    @Override
    public CoWorld getWorld(@NotNull World world) {
        return worlds.get(world.getName());
    }

    @Override
    public CoWorld getWorld(@NotNull String world) {
        return worlds.get(world);
    }

    @Override
    public Map<String, CoWorld> getWorlds() {
        return worlds;
    }

    @Override
    public Map<UUID, CoUser> getUsers(@NotNull World world) {
        return getWorld(world).getUsers();
    }

    @Override
    public Map<UUID, CoUser> getUsers(@NotNull String world) {
        return getWorld(world).getUsers();
    }

    @Override
    public Group getGroup(@NotNull World world, @NotNull String name) {
        return getWorld(world).getGroup(name);
    }

    @Override
    public Group getGroup(@NotNull World world, int id) {
        return getWorld(world).getGroup(id);
    }

    @Override
    public Group getGroup(@NotNull String world, @NotNull String name) {
        return getWorld(world).getGroup(name);
    }

    @Override
    public Group getGroup(@NotNull String world, int id) {
        return getWorld(world).getGroup(id);
    }

    @Override
    public Map<String, Group> getGroups(@NotNull World world) {
        return getWorld(world).getGroups();
    }

    @Override
    public Map<String, Group> getGroups(@NotNull String world) {
        return getWorld(world).getGroups();
    }

    @Override
    public SuperGroup getSuperGroup(@NotNull String name) {
        return superGroups.get(name);
    }

    @Override
    public Map<String, SuperGroup> getSuperGroups() {
        return superGroups;
    }

    @Override
    public CoPermsConfig getPermsConfig() {
        return config;
    }

    @Override
    public SuperDataFile getSuperDataFile() {
        return supers;
    }
    
    @Override
    public CommandStore getCommandStore() {
        return commandStore;
    }

    public static CoPerms getInstance() {
        return INSTANCE;
    }

    private void loadWorlds() {

        List<World> queue = new ArrayList<>();

        Bukkit.getWorlds().forEach(world -> {
            if (mirrors.contains(world.getName(), true)) {

                List<String> mirror = mirrors.getStringList(world.getName());
                //if the world in mirrors has both the groups and users string, then load the datafiles.
                if (mirror.contains("groups") && mirror.contains("users")) {
                    UserDataFile uf = new UserDataFile(this, world);
                    userDataFiles.put(world.getName(), uf);
                    groupDataFiles.put(world.getName(), new GroupDataFile(this, world));
                    worlds.put(world.getName(), new CoWorld(world, userDataFiles.get(world.getName()), groupDataFiles.get(world.getName())));
                }

                //we need to resolve what else is contained in this datafile
                else {
                    if (mirrors.getStringList(world.getName()) == null) {
                        if (world.equals(Bukkit.getWorlds().get(0))) {
                            throw new RuntimeException("Default world must have both the groups file and users file specified.");
                        }
                        userDataFiles.put(world.getName(), userDataFiles.get(defaultWorld));
                        groupDataFiles.put(world.getName(), groupDataFiles.get(defaultWorld));
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
                userDataFiles.put(world.getName(), userDataFiles.get(defaultWorld));
                groupDataFiles.put(world.getName(), groupDataFiles.get(defaultWorld));
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
                userDataFiles.put(world.getName(), new UserDataFile(this, world));
            }

            //If a key equals "groups" then it creates a new groupfile for this world
            if (key.equalsIgnoreCase("groups")) {
                groupDataFiles.put(world.getName(), new GroupDataFile(this, world));
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
        userDataFiles.putIfAbsent(world.getName(), userDataFiles.get(defaultWorld));
        groupDataFiles.putIfAbsent(world.getName(), groupDataFiles.get(defaultWorld));
        worlds.put(world.getName(), new CoWorld(world, userDataFiles.get(world.getName()), groupDataFiles.get(world.getName())));
    }

}

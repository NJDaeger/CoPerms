package com.coalesce.coperms.configuration;

import com.coalesce.coperms.CoPerms;
import com.coalesce.core.config.YmlConfig;
import org.bukkit.Bukkit;

import java.util.Arrays;

public final class CoPermsConfig extends YmlConfig {

    public CoPermsConfig(CoPerms plugin) {
        super("config", plugin);

        addEntry("allow-manual-promotion", false);
        addEntry("operator-overrides", true);
        addEntry("log-commands", true);
        addEntry("mirrors." + Bukkit.getWorlds().get(0).getName(), Arrays.asList("users", "groups"));
        addEntry("mirrors.all-other-worlds", Arrays.asList("users", "groups"));
    }

    /**
     * Checks if manual promotions are allowed
     *
     * @return True if allowed, false otherwise.
     */
    public boolean allowManualPromotion() {
        return getBoolean("allow-manual-promotion");
    }

    /**
     * Checks if ops can set ranks to people higher than them
     *
     * @return True if allowed, false otherwise.
     */
    public boolean allowOperatorOverrides() {
        return getBoolean("operator-overrides");
    }

    /**
     * Whether to log all CoPerms commands
     *
     * @return True if logging, false otherwise.
     */
    public boolean logCommands() {
        return getBoolean("log-commands");
    }
}

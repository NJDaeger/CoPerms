package com.njdaeger.coperms.configuration;

import com.njdaeger.coperms.CoPerms;
import com.njdaeger.pdk.config.ConfigType;
import com.njdaeger.pdk.config.Configuration;
import org.bukkit.Bukkit;

import java.util.Arrays;

public final class CoPermsConfig extends Configuration {

    public CoPermsConfig(CoPerms plugin) {
        super(plugin, ConfigType.YML, "config");

        addEntry("allow-self-promotion", true);
        addEntry("operator-overrides", true);
        addEntry("mirrors." + Bukkit.getWorlds().get(0).getName(), Arrays.asList("users", "groups"));
        addEntry("mirrors.all-other-worlds", Arrays.asList("users", "groups"));

        save();
    }

    /**
     * Checks if manual promotions are allowed
     *
     * @return True if allowed, false otherwise.
     */
    public boolean allowSelfPromotion() {
        return getBoolean("allow-self-promotion");
    }

    /**
     * Checks if ops can set ranks to people higher than them
     *
     * @return True if allowed, false otherwise.
     */
    public boolean allowOperatorOverrides() {
        return getBoolean("operator-overrides");
    }
}

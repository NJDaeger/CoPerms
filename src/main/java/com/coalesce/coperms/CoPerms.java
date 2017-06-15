package com.coalesce.coperms;

import com.coalesce.coperms.configuration.CoPermsConfig;
import com.coalesce.coperms.vault.Chat_CoPerms;
import com.coalesce.coperms.vault.Permission_CoPerms;
import com.coalesce.plugin.CoPlugin;
import org.bukkit.Bukkit;

public final class CoPerms extends CoPlugin {
	
	private DataLoader dataLoader;
	private CoPermsConfig config;
	
	@Override
	public void onPluginEnable() {
		this.updateCheck("Project-Coalesce", "CoPerms", true);
		
		this.config = new CoPermsConfig(this);
		
		addModules(this.dataLoader = new DataLoader(this));
		
		//Vault setup
		if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
			new Chat_CoPerms(this, new Permission_CoPerms(this));
		}
		else getCoLogger().warn("Some plugins may not work properly without Vault installed.");
	}
	
	@Override
	public void onPluginDisable() {
	
	}
	
	/**
	 * Gets CoPerms' configuration.
	 * @return CoPerm's configuration.
	 */
	public CoPermsConfig getPermsConfig() {
		return config;
	}
	
	/**
	 * Gets the plugin data holder.
	 * @return The data holder.
	 */
	public DataHolder getDataHolder() {
		return dataLoader.getDataHolder();
	}
	
}

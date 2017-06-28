package com.coalesce.coperms;

import com.coalesce.coperms.configuration.CoPermsConfig;
import com.coalesce.coperms.configuration.SuperDataFile;
import com.coalesce.coperms.vault.Chat_CoPerms;
import com.coalesce.coperms.vault.Permission_CoPerms;
import com.coalesce.plugin.CoPlugin;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;

public final class CoPerms extends CoPlugin {
	
	private DataLoader dataLoader;
	private CoPermsConfig config;
	private SuperDataFile supers;
	
	@Override
	public void onPluginEnable() {
		this.updateCheck("Project-Coalesce", "CoPerms", true);
		this.config = new CoPermsConfig(this);
		this.supers = new SuperDataFile(this);
		new Metrics(this);
		
		addModules(this.dataLoader = new DataLoader(this));
		
		if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
			Permission_CoPerms perms = new Permission_CoPerms(this);
			getServer().getServicesManager().register(Permission.class, perms, this, ServicePriority.High);
			getServer().getServicesManager().register(Chat.class, new Chat_CoPerms(this, perms), this, ServicePriority.High);
		}
		else getCoLogger().warn("Vault is not hooked into CoPerms... Loading anyway...");
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
	
	/**
	 * Returns the super data file.
	 * @return The super data file.
	 */
	public SuperDataFile getSuperDataFile() {
		return supers;
	}
	
}

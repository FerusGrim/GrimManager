package io.github.ferusgrim.GrimBanned;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Copyright (C) 2014 Nicholas Badger
 * @author FerusGrim
 */

public class GrimBanned extends JavaPlugin {
	public static final Logger toLog = Logger.getLogger("Minecraft");
	public final String pName = "[GrimBanned]";
	
	@Override
	public void onEnable() {
		ConfigManager.Start(this);
		if(ConfigManager.gbEnabled){
			ConfigManager.setupDatabase();
			ConfigManager.setupTable();
			ConfigManager.setupLog();
			PluginManager pm = getServer().getPluginManager();
			pm.registerEvents(new PlayerManager(), this);
			getCommand("ban").setExecutor(new Executor(this));
			getCommand("banip").setExecutor(new Executor(this));
			getCommand("unban").setExecutor(new Executor(this));
			getCommand("unbanip").setExecutor(new Executor(this));
		}else{
			toLog.log(Level.SEVERE, "Configuration is set to disable GrimBanned.");
			toLog.log(Level.SEVERE, "If this is your first time running this plugin, go ahead and edit your configuration and reload your server.");
			this.setEnabled(false);
		}
	}
}

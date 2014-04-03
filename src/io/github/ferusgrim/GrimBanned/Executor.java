package io.github.ferusgrim.GrimBanned;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Copyright (C) 2014 Nicholas Badger
 * @author FerusGrim
 */

public class Executor implements CommandExecutor {
	private GrimBanned plugin;

	public Executor(GrimBanned plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		String command = cmd.getName().toLowerCase();
		if(!sender.hasPermission("grimbanned." + command)){
			sender.sendMessage("[GB] Sorry, but you have insufficient permissions to run this command!");
			return false;
		}
		if(args.length < 1 && !command.equals("kickall")){
			return false;
		}
		switch(command){
			case "ban":
				return BanManager.Ban(plugin, sender, args);
			case "banip":
				return BanManager.BanIp(plugin, sender, args);
			case "unban":
				return BanManager.Unban(sender, args);
			case "unbanip":
				return BanManager.UnbanIp(sender, args);
			case "kick":
				return KickManager.Kick(plugin, sender, args);
			case "kickall":
				return KickManager.KickAll(plugin, sender, args);
			default:
				throw new IllegalArgumentException("[GB] Command is unknown!: " + label);
		}
	}
}

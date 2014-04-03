package io.github.ferusgrim.GrimBanned;

import java.util.logging.Level;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Copyright (C) 2014 Nicholas Badger
 * @author FerusGrim
 */

@SuppressWarnings("unused")
public class Executor implements CommandExecutor {
	private GrimBanned plugin;

	public Executor(GrimBanned plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		String command = cmd.getName().toLowerCase();
		boolean hasPerm = false;
		if(!(sender instanceof Player))hasPerm = true;
		if(!hasPerm){
			Player player = null;
			player = (Player) sender;
			if(player.hasPermission("grimbanned." + command))hasPerm = true;
			if(!hasPerm){
				sender.sendMessage("[GB] Insufficient Permissions!"); 
				return true;
			}
		}
		if(args.length < 1 && !command.equals("kickall")){
			return false;
		}
		if(command.equals("ban"))return BanManager.Ban(plugin, sender, args);
		if(command.equals("banip"))return BanManager.BanIp(plugin, sender, args);
		if(command.equals("unban"))return BanManager.Unban(sender, args);
		if(command.equals("unbanip"))return BanManager.UnbanIp(sender, args);
		if(command.equals("kick"))return KickManager.Kick(plugin, sender, args);
		if(command.equals("kickall"))return KickManager.KickAll(plugin, sender, args);
		return false;
	}

}

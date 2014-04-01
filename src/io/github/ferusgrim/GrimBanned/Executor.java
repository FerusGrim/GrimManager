package io.github.ferusgrim.GrimBanned;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
		boolean hasPerm = false;
		if(!(sender instanceof Player)) hasPerm = true;
		if(!hasPerm){
			Player player = null;
			player = (Player) sender;
			if(player.hasPermission("grimbanned." + args[0])) hasPerm = true;
			if(!hasPerm) sender.sendMessage("[GB] Insufficient Permissions!"); return true;
		}
		if(args.length < 1){
			return false;
		}
		if(args[0].equals("ban")) return BanManager.Ban(plugin, sender, args);
		if(args[0].equals("banip")) return BanManager.BanIp(plugin, sender, args);
		if(args[0].equals("unban")) return BanManager.Unban(sender, args);
		if(args[0].equals("unbanip")) return BanManager.UnbanIp(sender, args);
		return false;
	}

}

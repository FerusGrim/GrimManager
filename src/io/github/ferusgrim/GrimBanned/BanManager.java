package io.github.ferusgrim.GrimBanned;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Copyright (C) 2014 Nicholas Badger
 * @author FerusGrim
 */

public class BanManager {
	private static int i;

	public static boolean Ban(GrimBanned plugin, CommandSender sender, String[] args) {
		i = 0;
		String senderstr = sender.getName();
		String sender_ip = "";
		if(!(sender instanceof Player))sender_ip = "127.0.0.1";
		if(sender_ip.isEmpty()) sender_ip = ((Player) sender).getAddress().getAddress().getHostAddress();
		while(i < args.length){
			String playerstr = args[i].toLowerCase();
			Player player = plugin.getServer().getPlayerExact(args[i]);
			if(!(args[i].length() > 16) && args[i].matches("[a-zA-Z0-9_]+") && !ConfigManager.isPlayerNameBanned(args[i])){
				if(player != null){
					String playerip = player.getAddress().toString();
					ConfigManager.banPlayerOnline(playerstr, playerip, senderstr, sender_ip);
					ConfigManager.updateLog(playerstr, "banned using /ban", senderstr, sender_ip);
					player.kickPlayer("[GB] Banned from server!");
				}else{
					ConfigManager.banPlayerOffline(playerstr, senderstr, sender_ip);
					ConfigManager.updateLog(playerstr, "banned using /ban", senderstr, sender_ip);
				}
			}else{
				sender.sendMessage("[GB] Username '" + args[i] + "' is already banned, or is invalid.");
				return true;
			}
			sender.sendMessage("[GB] Player '" + args[i] + "' has been banned!");
			if(ConfigManager.NotifyServer) Bukkit.getServer().broadcast("[GB] " + sender.getName() + " banned '" + args[i] + "'!", "grimbanned.notify-ban");
			i++;
		}
		return true;
	}
	
	public static boolean BanIp(GrimBanned plugin, CommandSender sender, String[] args){
		i = 0;
		String senderstr = sender.getName();
		String sender_ip = "";
		if(!(sender instanceof Player))sender_ip = "127.0.0.1";
		if(sender_ip.isEmpty()) sender_ip = ((Player) sender).getAddress().getAddress().getHostAddress();
		while(i < args.length){
			String player_ip = args[i];
			Player player = null;
			for(Player p : Bukkit.getOnlinePlayers()){
				if(p.getAddress().getAddress().getHostAddress().equals(player_ip)){
					player = p;
				}
			}
			if(!(player_ip.length() > 15) && !(player_ip.length() < 7) && !ConfigManager.isPlayerIpBanned(player_ip)){
				if(player != null){
					String playerstr = player.getName().toLowerCase();
					ConfigManager.banPlayerOnline(playerstr, player_ip, senderstr, sender_ip);
					ConfigManager.updateLog(player_ip, "banned using /banip", senderstr, sender_ip);
					player.kickPlayer("[GB] Banned from server!");
				}else{
					ConfigManager.banPlayerIp(player_ip, senderstr, sender_ip);
					ConfigManager.updateLog(player_ip, "banned using /banip", senderstr, sender_ip);
				}
			}else{
				sender.sendMessage("[GB] IP Address '" + args[i] + "' is already banned, or is invalid.");
				return true;
			}
			sender.sendMessage("[GB] IP '" + args[i] + "' has been banned!");
			if(ConfigManager.NotifyServer) Bukkit.getServer().broadcast("[GB] " + sender.getName() + " banned the IP Address '" + args[i] + "'!", "grimbanned.notify-ban");
			i++;
		}
		return true;
	}

	public static boolean Unban(CommandSender sender, String[] args) {
		i = 0;
		String player = args[i].toLowerCase();
		String senderstr = sender.getName();
		String sender_ip = "";
		if(!(sender instanceof Player))sender_ip = "127.0.0.1";
		if(sender_ip.isEmpty()) sender_ip = ((Player) sender).getAddress().getAddress().getHostAddress();
		while(i < args.length){
			if(!(args[i].length() > 16) && args[i].matches("[a-zA-Z0-9_]+") && ConfigManager.isPlayerNameBanned(args[i])){
				ConfigManager.unbanPlayer("player", player);
				ConfigManager.updateLog(player, "unbanned using /unban", senderstr, sender_ip);
			}else{
				sender.sendMessage("[GB] Username '" + args[i] + "' isn't banned, or is invalid.");
				return true;
			}
			sender.sendMessage("[GB] Player '" + args[i] + "' has been unbanned!");
			i++;
		}
		return true;
	}
	
	public static boolean UnbanIp(CommandSender sender, String[] args) {
		i = 0;
		String player_ip = args[i];
		String senderstr = sender.getName();
		String sender_ip = "";
		if(!(sender instanceof Player))sender_ip = "127.0.0.1";
		if(sender_ip.isEmpty()) sender_ip = ((Player) sender).getAddress().getAddress().getHostAddress();
		while(i < args.length){
			if(!(player_ip.length() > 15) && !(player_ip.length() < 7) && ConfigManager.isPlayerIpBanned(player_ip)){
				ConfigManager.unbanPlayer("player_ip", player_ip);
				ConfigManager.updateLog(player_ip, "unbanned using /unbanip", senderstr, sender_ip);
			}else{
				sender.sendMessage("[GB] IP Address '" + args[i] + "' isn't banned, or is invalid.");
				return true;
			}
			sender.sendMessage("[GB] IP '" + args[i] + "' has been unbanned!");
			i++;
		}
		return true;
	}

}

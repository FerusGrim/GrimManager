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
		if(args.length < 2){
			sender.sendMessage("[GB] You forgot to enter a username to ban!");
			return true;
		}
		i = 0;
		String senderstr = sender.getName();
		String sender_ip = ((Player) sender).getAddress().getAddress().getHostAddress().toString().replaceAll("/", "").replaceAll("\\.", "-");
		while(i < args.length){
			String playerstr = args[i].toLowerCase();
			Player player = plugin.getServer().getPlayer(args[i]);
			if(!(args[i].length() > 16) && args[i].matches("[a-z0-9_]") && !(ConfigManager.isPlayerNameBanned(args[i]))){
				if(player != null){
					String playerip = player.getAddress().toString().replaceAll("/", "").replaceAll("\\.", "-");
					ConfigManager.banPlayerOnline(playerstr, playerip, senderstr, sender_ip);
					ConfigManager.updateLog(playerstr, "banned using /ban", senderstr, sender_ip);
					player.kickPlayer("[GB] Banned from server!");
				}else{
					ConfigManager.banPlayerOffline(playerstr, senderstr, sender_ip);
					ConfigManager.updateLog(playerstr, "banned using /ban", senderstr, sender_ip);
				}
			}else{
				sender.sendMessage("[GB] Username '" + args[i] + "' is already banned, or is invalid.");
			}
			if(ConfigManager.NotifyServer) Bukkit.getServer().broadcast("[GB] " + sender.getName() + " banned '" + args[i] + "'!", "grimbanned.notify-ban");
			i++;
		}
		return true;
	}
	
	public static boolean BanIp(GrimBanned plugin, CommandSender sender, String[] args){
		if(args.length < 2){
			sender.sendMessage("[GB] You for to enter an IP Address to ban!");
			return true;
		}
		i = 0;
		String senderstr = sender.getName().toString().toLowerCase();
		String sender_ip = ((Player) sender).getAddress().getAddress().getHostAddress().toString().replaceAll("/", "").replaceAll("\\.", "-");
		while(i < args.length){
			String player_ip = args[i].replace("\\.", "-");
			Player player = null;
			for(Player p : Bukkit.getOnlinePlayers()){
				p.getAddress().getAddress().getHostAddress().toString().replaceAll("/", "").replaceAll("\\.", "-").equals(player_ip);
				player = p;
			}
			if(!(player_ip.length() > 15) && !(player_ip.length() < 7) && player_ip.matches("[0-9-]") && ConfigManager.isPlayerIpBanned(player_ip)){
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
			}
			if(ConfigManager.NotifyServer) Bukkit.getServer().broadcast("[GB] " + sender.getName() + " banned the IP Address '" + args[i] + "'!", "grimbanned.notify-ban");
			i++;
		}
		return true;
	}

	public static boolean Unban(CommandSender sender, String[] args) {
		if(args.length < 2){
			sender.sendMessage("[GB] You forgot to enter a username to unban!");
			return true;
		}
		i = 0;
		String player = args[i].toLowerCase();
		String senderstr = sender.getName().toLowerCase();
		while(i < args.length){
			String sender_ip = ((Player) sender).getAddress().getAddress().getHostAddress().toString().replaceAll("/", "").replaceAll("\\.", "-");
			if(!(args[i].length() > 16) && args[i].matches("[a-z0-9_]") && ConfigManager.isPlayerNameBanned(args[i])){
				ConfigManager.unbanPlayer("player", player);
				ConfigManager.updateLog(player, "unbanned using /unban", senderstr, sender_ip);
			}else{
				sender.sendMessage("[GB] Username '" + args[i] + "' isn't banned, or is invalid.");
			}
			i++;
		}
		return true;
	}
	
	public static boolean UnbanIp(CommandSender sender, String[] args) {
		if(args.length < 2){
			sender.sendMessage("[GB] You forgot to enter an IP Address to unban!");
			return true;
		}
		i = 0;
		String player_ip = args[i].replace("\\.", "-");
		String senderstr = sender.getName().toLowerCase();
		while(i < args.length){
			String sender_ip = ((Player) sender).getAddress().getAddress().getHostAddress().toString().replaceAll("/", "").replaceAll("\\.", "-");
			if(!(player_ip.length() > 15) && !(player_ip.length() < 7) && player_ip.matches("[0-9-]") && ConfigManager.isPlayerIpBanned(player_ip)){
				ConfigManager.unbanPlayer("player_ip", player_ip);
				ConfigManager.updateLog(player_ip, "unbanned using /unbanip", senderstr, sender_ip);
			}else{
				sender.sendMessage("[GB] IP Address '" + args[i] + "' isn't banned, or is invalid.");
			}
			i++;
		}
		return true;
	}

}

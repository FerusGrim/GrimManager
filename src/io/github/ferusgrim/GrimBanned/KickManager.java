package io.github.ferusgrim.GrimBanned;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KickManager {
	private static int TaskID = -1;

	public static boolean Kick(GrimBanned plugin, CommandSender sender, String[] args) {
		int i = 0;
		String senderstr = sender.getName();
		String sender_ip = "";
		if(!(sender instanceof Player)){
			sender_ip = "127.0.0.1";
		}
		if(sender_ip.isEmpty()){
			sender_ip = ((Player) sender).getAddress().getAddress().getHostAddress().toString();
		}
		while(i < args.length){
			String playerstr = args[i].toLowerCase();
			Player player = plugin.getServer().getPlayerExact(args[i]);
			if(!(args[i].length() > 16) && args[i].matches("[a-zA-Z0-9]+")){
				if(player == null){
					sender.sendMessage(playerstr + " cannot be kicked, because they're not online!");
				}else{
					player.kickPlayer("[GB] Kicked from server!");
					sender.sendMessage("[GB] " + playerstr + " was kicked from the server!");
					if(ConfigManager.NotifyServerKicked){
						Bukkit.getServer().broadcast("[GB] " + senderstr + " kicked '" + args[i] + "'!", "grimbanned.notify-kick");
					}
					ConfigManager.updateLog(playerstr, "kicked using /kick", senderstr, sender_ip);
				}
			}else{
				sender.sendMessage("[GB] Username '" + args[i] + "' isn't a valid username!");
			}
			i++;
		}
		return true;
	}

	public static boolean KickAll(GrimBanned plugin, final CommandSender sender, String[] args) {
		int c = 0;
		int t = 0;
		int m = 0;
		if(args.length == 1){
			if(args[0].equals("stop")){
				if(TaskID > 0){
					Bukkit.getScheduler().cancelTask(TaskID);
					sender.sendMessage("[GB] Canceled scheduled \"/kickall\"");
					Bukkit.getServer().broadcastMessage("[GB] Previously scheduled \"/kickall\" has been canceled!");
					TaskID = -1;
					return true;
				}else{
					sender.sendMessage("[GB] There isn't a \"/kickall\" scheduled to stop!");
					return true;
				}
			}
			if(TaskID > 0){
				sender.sendMessage("[GB] There is already a \"/kickall\" scheduled!");
				return true;
			}
			if(args[0].matches("[0-9]+")){
				t = Integer.parseInt(args[0]);
				if(t > 300){
					sender.sendMessage("[GB] Maximum value is 5 minutes (300).");
					return true;
				}
			}else{
				sender.sendMessage("[GB] That's not a valid integer!");
				return true;
			}
		}else if(args.length > 1){
			sender.sendMessage("[GB] Command accepted. Discounting other arguments!");
		}
		final String senderstr = sender.getName();
		final String sender_ip = !(sender instanceof Player)? "127.0.0.1" : ((Player) sender).getAddress().getAddress().getHostAddress().toString();
		for(Player player : Bukkit.getOnlinePlayers()){
			if(player != sender){
				c++;
			}
		}
		if(c == 0){
			sender.sendMessage("[GB] No one online to kick!");
			return true;
		}
		if(c == 0){
			sender.sendMessage("[GB] No one online to kick!");
			return true;
		}
		m = t / 60;
		boolean usem = false;
		if(String.valueOf(m).length() == 1){
			usem = true;
		}
		if(usem){
			Bukkit.getServer().broadcastMessage("[GB] Kicking all players in " + m + " minute(s)!");
		}else{
			Bukkit.getServer().broadcastMessage("[GB] Kicking all players in " + t + " second(s)!");
		}
		if(TaskID < 0){
			TaskID = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
				@Override
				public void run() {
					for(Player player : Bukkit.getOnlinePlayers()){
						if(player != sender){
							player.kickPlayer("[GB] Kicked from server!");
							String playerstr = player.getName().toString().toLowerCase();
							ConfigManager.updateLog(playerstr, "kicked using /kickall", senderstr, sender_ip);
						}
					}
				TaskID = -1;
				}
			}, t * 20);
		}
		return true;
	}
}

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
			sender_ip = ((Player) sender).getAddress().getAddress().getHostAddress();
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
		int m = 0;
        int t = 0;
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
            t = doMath(args[0]);
		}
		final String senderstr = sender.getName();
		final String sender_ip = !(sender instanceof Player)? "127.0.0.1" : ((Player) sender).getAddress().getAddress().getHostAddress();
		for(Player player : Bukkit.getOnlinePlayers()){
			if(player != sender){
				c++;
			}
		}
		if(c == 0){
			sender.sendMessage("[GB] No one online to kick!");
			return true;
		}
		Bukkit.getServer().broadcastMessage("[GB] Kicking all players in " + (useM(t)? m + "minutes!" : t + "seconds!"));
		if(TaskID < 0){
			TaskID = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
				@Override
				public void run() {
					for(Player player : Bukkit.getOnlinePlayers()){
						if(player != sender){
							player.kickPlayer("[GB] Kicked from server!");
							String playerstr = player.getName().toLowerCase();
							ConfigManager.updateLog(playerstr, "kicked using /kickall", senderstr, sender_ip);
						}
					}
				TaskID = -1;
				}
			}, t * 20);
		}
		return true;
	}

    public static int doMath(String args){
        int t;
        if(args.matches("[0-9]+")){
            t = Integer.parseInt(args);
            if(t > 300){
                t = 300;
            }
        }else{
            t = 0;
        }
        return t;
    }

    private static boolean useM(int t){
        int m;
        m = t / 60;
        return (String.valueOf(m).length() == 1);
    }
}

package io.github.ferusgrim.GrimBanned;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

/**
 * Copyright (C) 2014 Nicholas Badger
 * @author FerusGrim
 */

public class PlayerManager implements Listener {
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerLogin(PlayerLoginEvent event){
		if(ConfigManager.gbEnabled){
			if(event.getResult() == Result.ALLOWED){
				Player player = event.getPlayer();
				String playerstr = player.getName().toLowerCase();
				String playerip = player.getAddress().getAddress().getHostAddress().toString().replaceAll("/", "").replaceAll("\\.", "-");
				String DMSG = ConfigManager.DisconnectMSG.replace("{player}", player.getName());
				String NMSG = ConfigManager.NotifyMSG.replace("{player}", player.getName());
				if(ConfigManager.isPlayerNameBanned(playerstr)){
					ConfigManager.updatePlayer("player_ip", playerip, "player", playerstr);
					event.setKickMessage(DMSG);
					event.setResult(PlayerLoginEvent.Result.KICK_BANNED);
					if(ConfigManager.Notify) Bukkit.getServer().broadcast(NMSG, "grimbanned.notify-fail");
				}
				if(ConfigManager.isPlayerIpBanned(playerip)){
					ConfigManager.updatePlayer("player", playerstr, "player_ip", playerip);
					event.setKickMessage(DMSG);
					event.setResult(PlayerLoginEvent.Result.KICK_BANNED);
					if(ConfigManager.Notify) Bukkit.getServer().broadcast(NMSG, "grimbanned.notify-fail");
				}
			}
		}
	}
}

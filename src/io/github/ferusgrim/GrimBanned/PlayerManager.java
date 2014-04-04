package io.github.ferusgrim.GrimBanned;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.server.ServerListPingEvent;

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
				String playerip = event.getAddress().getHostAddress();
				String DMSG = ConfigManager.DisconnectMSG.replace("{player}", player.getName());
				String NMSG = ConfigManager.NotifyMSG.replace("{player}", player.getName());
				if(ConfigManager.isPlayerNameBanned(playerstr)){
					if(!ConfigManager.isPlayerIpBanned(playerip)){
						ConfigManager.updatePlayer("player", playerstr, playerip);
						ConfigManager.updateLog(playerip, "IP logged automatically", playerstr, playerip);
					}
					event.setKickMessage(DMSG);
					event.setResult(PlayerLoginEvent.Result.KICK_BANNED);
					if(ConfigManager.Notify) Bukkit.getServer().broadcast(NMSG, "grimbanned.notify-fail");
				}
				if(ConfigManager.isPlayerIpBanned(playerip)){
					if(!ConfigManager.isPlayerNameBanned(playerstr)){
						ConfigManager.updatePlayer("player_ip", playerip, playerstr);
						ConfigManager.updateLog(playerstr, "Name logged automatically", playerstr, playerip);
					}
					ConfigManager.updatePlayer("player_ip", playerip, playerstr);
					event.setKickMessage(DMSG);
					event.setResult(PlayerLoginEvent.Result.KICK_BANNED);
					if(ConfigManager.Notify) Bukkit.getServer().broadcast(NMSG, "grimbanned.notify-fail");
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onServerListPing(final ServerListPingEvent event){
		if(ConfigManager.motdEnabled){
			String playerIP = event.getAddress().getHostAddress();
			if(ConfigManager.isPlayerIpBanned(playerIP)){
				event.setMotd("You are BANNED!: (" + playerIP + ")");
			}
		}
	}
}

package io.github.ferusgrim.GrimBanned;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Copyright (C) 2014 Nicholas Badger
 * @author FerusGrim
 */

public class ConfigManager {
	public static boolean gbEnabled;
	public static boolean motdEnabled;
	public static boolean Notify;
	public static boolean NotifyServer;
	public static String DisconnectMSG;
	public static String NotifyMSG;
	private static int sqlPort;
	private static String sqlHost;
	private static String sqlDatabase;
	private static String sqlUsername;
	private static String sqlPassword;

	public static void Start(GrimBanned plugin) {
		File mDir = plugin.getDataFolder();
		if(!mDir.exists()) mDir.mkdir();
		plugin.saveDefaultConfig();
		ConfigureVariables(plugin);
	}

	private static void ConfigureVariables(GrimBanned plugin) {
		gbEnabled = plugin.getConfig().getBoolean("GrimBanned");
		motdEnabled = plugin.getConfig().getBoolean("MotD");
		DisconnectMSG = "[GB] " + plugin.getConfig().getString("Messages.Disconnected");
		NotifyMSG = "[GB] " + plugin.getConfig().getString("Messages.Notification");
		Notify = plugin.getConfig().getBoolean("Notifications.Failues");
		NotifyServer = plugin.getConfig().getBoolean("Notifications.Banned");
		if(gbEnabled) ConfigureSQL(plugin);
	}

	private static void ConfigureSQL(GrimBanned plugin) {
		sqlHost = plugin.getConfig().getString("MySQL.Host");
		sqlPort = plugin.getConfig().getInt("MySQL.Port");
		sqlDatabase = plugin.getConfig().getString("MySQL.Database");
		sqlUsername = plugin.getConfig().getString("MySQL.Username");
		sqlPassword = plugin.getConfig().getString("MySQL.Password");
	}
	
	public static Connection sqlConnectionForFirstUse() {
		try{
			return DriverManager.getConnection("jdbc:mysql://" + sqlHost + ":" + sqlPort + "/?autoReconnect=true&user=" + sqlUsername + "&password=" + sqlPassword);
		}catch(SQLException e){
			e.printStackTrace();
		}
		return null;
	}
	
	public static Connection sqlConnection(){
		try{
			return DriverManager.getConnection("jdbc:mysql://" + sqlHost + ":" + sqlPort + "/" + sqlDatabase + "?autoReconnect=true&user=" + sqlUsername + "&password=" + sqlPassword);
		}catch(SQLException e){
			e.printStackTrace();
		}
		return null;
	}
	
	public static void cleanUp(Connection conn, PreparedStatement ps){
		try{
			if(ps != null) ps.close();
			if(conn != null) conn.close();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static void setupDatabase(){
		Connection conn = null;
		PreparedStatement ps = null;
		try{
			conn = sqlConnectionForFirstUse();
			ps = conn.prepareStatement("CREATE DATABASE IF NOT EXISTS `" + sqlDatabase + "` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci");
			ps.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
		cleanUp(conn, ps);
	}
	
	public static void setupTable(){
		Connection conn = null;
		PreparedStatement ps = null;
		try{
			conn = sqlConnection();
			ps = conn.prepareStatement("CREATE TABLE IF NOT EXISTS `playerbans` ("
					+ "`player` varchar(16) DEFAULT NULL, "
					+ "`player_ip` varchar(16) DEFAULT NULL, "
					+ "`banned_by` varchar(16) NOT NULL, "
					+ "`banner_ip` varchar(16) NOT NULL, "
					+ "`banned_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP, "
					+ "UNIQUE KEY `player` (`player`)"
					+ ") ENGINE=InnoDB DEFAULT CHARSET=latin1;");
			ps.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
		cleanUp(conn, ps);
	}
	
	public static void setupLog(){
		Connection conn = null;
		PreparedStatement ps = null;
		try{
			conn = sqlConnection();
			ps = conn.prepareStatement("CREATE TABLE IF NOT EXISTS `playerbans_log` ("
					+ "`victim` varchar(16) NOT NULL, "
					+ "`action` varchar(64) NOT NULL, "
					+ "`executor` varchar(16) NOT NULL, "
					+ "`executor_ip` varchar(16) NOT NULL, "
					+ "`action_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,"
					+ "`lognum` MEDIUMINT NOT NULL AUTO_INCREMENT,"
					+ "PRIMARY KEY (`lognum`)"
					+ ") ENGINE=InnoDB DEFAULT CHARSET=latin1;");
			ps.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
		cleanUp(conn, ps);
	}
	
	public static boolean isPlayerNameBanned(String player){
		Connection conn = null;
		PreparedStatement ps = null;
		try{
			conn = sqlConnection();
			ps = conn.prepareStatement("SELECT COUNT(*) FROM `" + sqlDatabase + "`.`playerbans` WHERE `player` = ?;");
			ps.setString(1, player);
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				if(rs.getInt(1) == 1){
					return true;
				}
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		cleanUp(conn, ps);
		return false;
	}
	
	public static boolean isPlayerIpBanned(String playerip){
		Connection conn = null;
		PreparedStatement ps = null;
		try{
			conn = sqlConnection();
			ps = conn.prepareStatement("SELECT COUNT(*) FROM `" + sqlDatabase + "`.`playerbans` WHERE `player_ip` = ?;");
			ps.setString(1, playerip);
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				if(rs.getInt(1) == 1){
					return true;
				}
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		cleanUp(conn, ps);
		return false;
	}
	
	public static void banPlayerOnline(String victim, String victim_ip, String executor, String executor_ip){
		Connection conn = null;
		PreparedStatement ps = null;
		try{
			conn = sqlConnection();
			ps = conn.prepareStatement("INSERT INTO `" + sqlDatabase + "`.`playerbans` ("
					+ "`player`, `player_ip`, `banned_by`, `banner_ip`, `banned_date`) VALUES ("
					+ "?, ?, ?, ?, CURRENT_TIMESTAMP);");
			ps.setString(1, victim);
			ps.setString(2, victim_ip);
			ps.setString(3, executor);
			ps.setString(4, executor_ip);
			ps.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
		cleanUp(conn, ps);
	}
	
	public static void updateLog(String victim, String action, String executor, String executor_ip){
		Connection conn = null;
		PreparedStatement ps = null;
		try{
			conn = sqlConnection();
			ps = conn.prepareStatement("INSERT INTO `" + sqlDatabase + "`.`playerbans_log` ("
					+ "`victim`, `action`, `executor`, `executor_ip`, `action_date`) VALUES ("
					+ "?, ?, ?, ?, CURRENT_TIMESTAMP);");
			ps.setString(1, victim);
			ps.setString(2, action);
			ps.setString(3, executor);
			ps.setString(4, executor_ip);
			ps.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
		cleanUp(conn, ps);
	}
	
	public static void banPlayerOffline(String victim, String executor, String executor_ip){
		Connection conn = null;
		PreparedStatement ps = null;
		try{
			conn = sqlConnection();
			ps = conn.prepareStatement("INSERT INTO `" + sqlDatabase + "`.`playerbans` ("
					+ "`player`, `banned_by`, `banner_ip`, `banned_date`) VALUES ("
					+ "?, ?, ?, CURRENT_TIMESTAMP);");
			ps.setString(1, victim);
			ps.setString(2, executor);
			ps.setString(3, executor_ip);
			ps.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
		cleanUp(conn, ps);
	}
	
	public static void banPlayerIp(String victim_ip, String executor, String executor_ip){
		Connection conn = null;
		PreparedStatement ps = null;
		try{
			conn = sqlConnection();
			ps = conn.prepareStatement("INSERT INTO `" + sqlDatabase + "`.`playerbans` ("
					+ "`player_ip`, `banned_by`, `banner_ip`, `banned_date`) VALUES ("
					+ "?, ?, ?, CURRENT_TIMESTAMP);");
			ps.setString(1, victim_ip);
			ps.setString(2, executor);
			ps.setString(3, executor_ip);
			ps.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
		cleanUp(conn, ps);
	}
	
	public static void updatePlayer(String known_type, String known_value, String new_value){
		Connection conn = null;
		PreparedStatement ps = null;
		if(known_type.equals("player")){
			try{
				conn = sqlConnection();
				ps = conn.prepareStatement("UPDATE `" + sqlDatabase + "`.`playerbans` SET `player_ip` = ? WHERE `playerbans`.`player` = ?");
				ps.setString(1, new_value);
				ps.setString(2, known_value);
				ps.executeUpdate();
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
		if(known_type.equals("player_ip")){
			try{
				conn = sqlConnection();
				ps = conn.prepareStatement("UPDATE `" + sqlDatabase + "`.`playerbans` SET `player` = ? WHERE `playerbans`.`player_ip` = ?");
				ps.setString(1, new_value);
				ps.setString(2, known_value);
				ps.executeUpdate();
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
		cleanUp(conn, ps);
	}
	
	public static void unbanPlayer(String ban_type, String victim){
		Connection conn = null;
		PreparedStatement ps = null;
		if(ban_type.equals("player")){
			try{
				conn = sqlConnection();
				ps = conn.prepareStatement("DELETE FROM `" + sqlDatabase + "`.`playerbans` WHERE `playerbans`.`player` = ?;");
				ps.setString(1, victim);
				ps.executeUpdate();
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
		if(ban_type.equals("player_ip")){
			try{
				conn = sqlConnection();
				ps = conn.prepareStatement("DELETE FROM `" + sqlDatabase + "`.`playerbans` WHERE `playerbans`.`player_ip` = ?;");
				ps.setString(1, victim);
				ps.executeUpdate();
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
		cleanUp(conn, ps);
	}
}

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
	public static boolean Notify;
	public static boolean NotifyServer;
	public static String DisconnectMSG;
	public static String NotifyMSG;
	private static String QUERY_CONNECTOR;
	private static String QUERY_DATABASE;
	private static String QUERY_TABLE;
	private static String QUERY_TABLE_LOG;
	private static String QUERY_BAN_ONLINE;
	private static String QUERY_BAN_OFFLINE;
	private static String QUERY_BANIP;
	private static String QUERY_UNBAN;
	private static String QUERY_CHECKPLAYER;
	private static String QUERY_UPDATEPLAYER;
	private static String QUERY_UPDATELOG;

	public static void Start(GrimBanned plugin) {
		File mDir = plugin.getDataFolder();
		if(!mDir.exists()) mDir.mkdir();
		plugin.saveDefaultConfig();
		ConfigureVariables(plugin);
	}

	private static void ConfigureVariables(GrimBanned plugin) {
		gbEnabled = plugin.getConfig().getBoolean("GrimBanned");
		DisconnectMSG = "[GB]" + plugin.getConfig().getString("Messages.Disconnected");
		NotifyMSG = "[GB]" + plugin.getConfig().getString("Messages.Notification");
		Notify = plugin.getConfig().getBoolean("Notifications.Failues");
		NotifyServer = plugin.getConfig().getBoolean("Notifications.Banned");
		if(gbEnabled) ConfigureSQL(plugin);
	}

	private static void ConfigureSQL(GrimBanned plugin) {
		boolean usePassword;
		int sqlPort;
		String sqlHost;
		String sqlDatabase;
		String sqlUsername;
		String sqlPassword;
		sqlHost = plugin.getConfig().getString("MySQL.Host");
		sqlPort = plugin.getConfig().getInt("MySQL.Port");
		sqlDatabase = plugin.getConfig().getString("MySQL.Database");
		sqlUsername = plugin.getConfig().getString("MySQL.Username");
		sqlPassword = plugin.getConfig().getString("MySQL.Password");
		usePassword = sqlPassword.isEmpty()? false : true;
		QUERY_CONNECTOR = "jdbc:mysql://" + sqlHost + ":" + sqlPort + "/" + sqlDatabase + "?user=" + sqlUsername;
		if(usePassword) QUERY_CONNECTOR = QUERY_CONNECTOR + "&password=" + sqlPassword;
		QUERY_DATABASE = "CREATE DATABASE IF NOT EXISTS `" + sqlDatabase + "` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci";
		QUERY_TABLE = "CREATE TABLE IF NOT EXISTS `playerbans` ("
				+ "`player` varchar(16) NOT NULL, "
				+ "`player_ip` varchar(16) NOT NULL, "
				+ "`banned_by` varchar(16) NOT NULL, "
				+ "`banner_ip` varchar(16) NOT NULL, "
				+ "`banned_date` timestamp(4) NOT NULL DEFAULT CURRENT_TIMESTAMP(4) ON UPDATE_CURRENT_TIMESTAMP(4), "
				+ "UNIQUE KEY `player` (`player`)"
				+ ") ENGINE=InnoDB DEFAULT CHARSET=latin1;";
		QUERY_TABLE_LOG = "CREATE TABLE IF NOT EXISTS `playerbans_log` ("
				+ "`victim` varchar(16) NOT NULL, "
				+ "`action` varchar(64) NOT NULL, "
				+ "`executor` varchar(16) NOT NULL, "
				+ "`exector_ip` varchar(16) NOT NULL, "
				+ "`action_date` timestamp(4) NOT NULL DEFAULT CURRENT_TIMESTAMP(4) ON UPDATE_CURRENT_TIMESTAMP(4), "
				+ ") ENGINE=InnoDB DEFAULT CHARSET=latin1;";
		QUERY_CHECKPLAYER = "SELECT COUNT(*) FROM `" + sqlDatabase + "`.`playerbans` WHERE `?` = '?';";
		QUERY_UPDATEPLAYER = "UPDATE `" + sqlDatabase + "`.`playerbans` SET `?` = '?' WHERE `playerbans`.`?` = '?'";
		QUERY_BAN_ONLINE = "INSERT INTO `" + sqlDatabase + "`.`playerbans` ("
				+ "`player`, `player_ip`, `banned_by`, `banner_ip`, `banned_date`) VALUES ("
				+ "'?', '?', '?', '?', TIMESTAMP('CURRENT_TIMESTAMP(4)'));";
		QUERY_BAN_OFFLINE = "INSERT INTO `" + sqlDatabase + "`.`playerbans` ("
				+ "`player`, `banned_by`, `banner_ip`, `banned_date`) VALUES ("
				+ "'?', '?', '?', TIMESTAMP('CURRENT_TIMESTAMP(4)'));";
		QUERY_BANIP = "INSERT INTO `" + sqlDatabase + "`.`playerbans` ("
				+ "`player_ip`, `banned_by`, `banner_ip`, `banned_date`) VALUES ("
				+ "'?', '?', '?', TIMESTAMP('CURRENT_TIMESTAMP(4)'));";
		QUERY_UNBAN = "DELETE FROM `" + sqlDatabase + "`.`playerbans` WHERE `playerbans`.`?` = '?';";
		QUERY_UPDATELOG = "INSERT INTO `" + sqlDatabase + "`.`playerbans_log` ("
				+ "`victim`, `action`, `executor`, `executor_ip`, `action_date`) VALUES ("
				+ "'?', '?', '?', '?', TIMESTAMP('CURRENT_TIMESTAMP(4)'));";
	}
	
	public static Connection sqlConnection() {
		try{
			return DriverManager.getConnection(QUERY_CONNECTOR);
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
			conn = sqlConnection();
			ps = conn.prepareStatement(QUERY_DATABASE);
			ps.executeQuery();
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
			ps = conn.prepareStatement(QUERY_TABLE);
			ps.executeQuery();
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
			ps = conn.prepareStatement(QUERY_TABLE_LOG);
			ps.executeQuery();
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
			ps = conn.prepareStatement(QUERY_CHECKPLAYER);
			ps.setString(1, "player");
			ps.setString(2, player);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) cleanUp(conn, ps); return true;
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
			ps = conn.prepareStatement(QUERY_CHECKPLAYER);
			ps.setString(1, "player_ip");
			ps.setString(2, playerip);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) cleanUp(conn, ps); return true;
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
			ps = conn.prepareStatement(QUERY_BAN_ONLINE);
			ps.setString(1, victim);
			ps.setString(2, victim_ip);
			ps.setString(3, executor);
			ps.setString(4, executor_ip);
			ps.executeQuery();
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
			ps = conn.prepareStatement(QUERY_UPDATELOG);
			ps.setString(1, victim);
			ps.setString(2, action);
			ps.setString(3, executor);
			ps.setString(4, executor_ip);
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
			ps = conn.prepareStatement(QUERY_BAN_OFFLINE);
			ps.setString(1, victim);
			ps.setString(2, executor);
			ps.setString(3, executor_ip);
			ps.executeQuery();
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
			ps = conn.prepareStatement(QUERY_BANIP);
			ps.setString(1, victim_ip);
			ps.setString(2, executor);
			ps.setString(3, executor_ip);
			ps.executeQuery();
		}catch(SQLException e){
			e.printStackTrace();
		}
		cleanUp(conn, ps);
	}
	
	public static void updatePlayer(String new_type, String new_value, String known_type, String known_value){
		Connection conn = null;
		PreparedStatement ps = null;
		try{
			conn = sqlConnection();
			ps = conn.prepareStatement(QUERY_UPDATEPLAYER);
			ps.setString(1, new_type);
			ps.setString(2, new_value);
			ps.setString(3, known_type);
			ps.setString(4, known_value);
			ps.executeQuery();
		}catch(SQLException e){
			e.printStackTrace();
		}
		cleanUp(conn, ps);
	}
	
	public static void unbanPlayer(String ban_type, String victim){
		Connection conn = null;
		PreparedStatement ps = null;
		try{
			conn = sqlConnection();
			ps = conn.prepareStatement(QUERY_UNBAN);
			ps.setString(1, ban_type);
			ps.setString(2, victim);
			ps.executeQuery();
		}catch(SQLException e){
			e.printStackTrace();
		}
		cleanUp(conn, ps);
	}
}

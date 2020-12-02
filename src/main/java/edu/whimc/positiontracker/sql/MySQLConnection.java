/**
 * 
 */
package edu.whimc.positiontracker.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import edu.whimc.positiontracker.Tracker;

/**
 * 
 * @author huwenxuan
 */
public class MySQLConnection {
	
	public static final String DRIVER_CLASS = "com.mysql.jdbc.Driver";
	public static final String URL_TEMPLATE = "jdbc:mysql://%s:%s/%s";
	public static final String CREATE_TABLE =
			"CREATE TABLE IF NOT EXISTS `whimc_player_positions` (" +
			"  `rowid`    BIGINT AUTO_INCREMENT NOT NULL," +
			"  `x`        INT                   NOT NULL," +
			"  `y`        INT                   NOT NULL," +
			"  `z`        INT                   NOT NULL," +
			"  `world`    VARCHAR(64)           NOT NULL," +
			"  `biome`    VARCHAR(64)           NOT NULL," +
			"  `username` VARCHAR(16)           NOT NULL," +
			"  `uuid`     VARCHAR(36)           NOT NULL," +
			"  `time` 	  BIGINT                NOT NULL," +
			"  PRIMARY KEY (`rowid`));";
	
	private Connection connection;
	private String host, database, username, password, url;
	private int port;
	
	public MySQLConnection(Tracker plugin) {
		this.host = plugin.getConfig().getString("mysql.host", "localhost");
		this.port = plugin.getConfig().getInt("mysql.port", 3306);
		this.database = plugin.getConfig().getString("mysql.database", "minecraft");
		this.username = plugin.getConfig().getString("mysql.username", "user");
		this.password = plugin.getConfig().getString("mysql.password", "pass");
		
		this.url = String.format(URL_TEMPLATE, host, port, database);
	}
	
	public boolean initialize() {
		if (getConnection() == null) {
			return false;
		}
		
		try {
			PreparedStatement statement = this.connection.prepareStatement(CREATE_TABLE);
			statement.execute();
		} catch (SQLException e) {
			return false;
		}
		
		return true;
	}
	
	public Connection getConnection() {
		try {
			if (this.connection != null && !this.connection.isClosed()) {
				return this.connection;
			}
			
			Class.forName(DRIVER_CLASS);
			this.connection = DriverManager.getConnection(this.url, this.username, this.password);
		} catch (SQLException | ClassNotFoundException e) {
			return null;
		}
		
		return this.connection;
	}
	
	public void closeConnection() {
		if (this.connection != null) {
			try {
				this.connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

}

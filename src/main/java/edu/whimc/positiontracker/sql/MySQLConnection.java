package edu.whimc.positiontracker.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import edu.whimc.positiontracker.Tracker;

/**
 * Handles the connection to the SQL database.
 */
public class MySQLConnection {
	/** The template for the URL. */
	public static final String URL_TEMPLATE = "jdbc:mysql://%s:%s/%s";
	/** The SQL command to create a table. */
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

	/** The connection to the SQL database. */
	private Connection connection;
	/** The SQL database credentials. */
	private String host, database, username, password, url;
	/** The SQL database port. */
	private int port;

	/**
	 * Constructs a MySQLConnection.
	 *
	 * @param plugin The instance of the plugin.
	 */
	public MySQLConnection(Tracker plugin) {
		// fetch credentials and database info from config
		this.host = plugin.getConfig().getString("mysql.host", "localhost");
		this.port = plugin.getConfig().getInt("mysql.port", 3306);
		this.database = plugin.getConfig().getString("mysql.database", "minecraft");
		this.username = plugin.getConfig().getString("mysql.username", "user");
		this.password = plugin.getConfig().getString("mysql.password", "pass");

		// create the URL with the fetched information
		this.url = String.format(URL_TEMPLATE, host, port, database);
	}

	/**
	 *	Initializes the MySQLConnection.
	 *
	 * @return true if the connection succeeds.
	 */
	public boolean initialize() {
		// ensure there is a connection
		if (getConnection() == null) {
			return false;
		}
		
		try {
			// create a table
			PreparedStatement statement = this.connection.prepareStatement(CREATE_TABLE);
			statement.execute();
		} catch (SQLException e) {
			return false;
		}
		
		return true;
	}

	/**
	 * Fetches the connection to the SQL database.
	 *
	 * @return the SQL database Connection.
	 */
	public Connection getConnection() {
		try {
			// ensure connection exists and is open
			if (this.connection != null && !this.connection.isClosed()) {
				return this.connection;
			}

			// try connecting if a connection doesn't currently exist
			this.connection = DriverManager.getConnection(this.url, this.username, this.password);
		} catch (SQLException ignored) {
			return null;
		}
		
		return this.connection;
	}

	/**
	 * Closes the connection to the SQL database.
	 */
	public void closeConnection() {
		// ensure connection exists
		if (this.connection != null) {
			try {
				this.connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

}

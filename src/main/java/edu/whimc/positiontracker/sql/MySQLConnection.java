package edu.whimc.positiontracker.sql;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import com.mysql.cj.jdbc.MysqlDataSource;
import com.mysql.cj.x.protobuf.MysqlxPrepare;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import edu.whimc.positiontracker.Tracker;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Handles the connection to the SQL database.
 */
public class MySQLConnection {

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

	private MysqlDataSource dataSource;

	/**
	 * Constructs a MySQLConnection.
	 *
	 * @param plugin The instance of the plugin.
	 */
	public MySQLConnection(Tracker plugin) {
		this.dataSource = new MysqlConnectionPoolDataSource();

		ConfigurationSection config = plugin.getConfig();
		this.dataSource.setServerName(config.getString("mysql.host", "localhost"));
		this.dataSource.setPortNumber(config.getInt("mysql.port", 3306));
		this.dataSource.setDatabaseName(config.getString("mysql.database", "minecraft"));
		this.dataSource.setUser(config.getString("mysql.username", "user"));
		this.dataSource.setPassword(config.getString("mysql.password", "pass"));
	}

	/**
	 * Attempt to connect to the database and create/update the schema.
	 *
	 * @return Whether the database was successfully initialized.
	 */
	public boolean initialize() {
		try (Connection connection = getConnection()) {
			if (connection == null) {
				return false;
			}

			try (PreparedStatement statement = connection.prepareStatement(CREATE_TABLE)) {
				statement.execute();
			}
		} catch (SQLException unused) {
			return false;
		}

		return true;
	}

	/**
	 * A connection to the configured database or null if unsuccessful.
	 */
	public Connection getConnection() throws SQLException {
		Connection connection = this.dataSource.getConnection();
		if (!connection.isValid(1)) {
			return null;
		}

		return connection;
	}

}

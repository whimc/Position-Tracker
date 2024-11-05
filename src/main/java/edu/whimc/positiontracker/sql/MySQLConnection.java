package edu.whimc.positiontracker.sql;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import com.mysql.cj.jdbc.MysqlDataSource;
import edu.whimc.positiontracker.PositionTracker;
import edu.whimc.positiontracker.sql.migration.SchemaManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.bukkit.configuration.ConfigurationSection;

/**
 * Handles the connection to the SQL database.
 */
public class MySQLConnection {

	private final MysqlDataSource dataSource;

	private final PositionTracker plugin;

	/**
	 * Constructs a MySQLConnection.
	 *
	 * @param plugin The instance of the plugin.
	 */
	public MySQLConnection(PositionTracker plugin) {
		this.dataSource = new MysqlConnectionPoolDataSource();
		this.plugin = plugin;

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

			SchemaManager manager = new SchemaManager(this.plugin, this);
			return manager.initialize();
		} catch (SQLException unused) {
			return false;
		}
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

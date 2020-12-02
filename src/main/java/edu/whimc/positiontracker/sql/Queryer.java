package edu.whimc.positiontracker.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import edu.whimc.positiontracker.Tracker;

/**
 * Handles storing position data 
 * @author Jack Henhapl
 *
 */

public class Queryer {
	
	public class PositionEntry {
		
		private final int x, y, z;
		private final String world, biome, username;
		private final UUID uuid;
		private final Timestamp time;
		
		public PositionEntry(Player player) {
			Location loc = player.getLocation();
			this.x = loc.getBlockX();
			this.y = loc.getBlockY();
			this.z = loc.getBlockZ();
			this.world = loc.getWorld().getName();
			this.biome = loc.getBlock().getBiome().name();
			this.username = player.getName();
			this.uuid = player.getUniqueId();
			this.time = new Timestamp(System.currentTimeMillis());
		}
		
		public void addInsertionToBatch(PreparedStatement statement) throws SQLException {
			statement.setInt(1, x);
			statement.setInt(2, y);
			statement.setInt(3, z);
			statement.setString(4, world);
			statement.setString(5, biome);
			statement.setString(6, username);
			statement.setString(7, uuid.toString());
			statement.setLong(8, time.getTime() / 1000);
			statement.addBatch();
		}
		
	}
	
	private static final String INSERT_FORMAT =
			"INSERT INTO `whimc_player_positions` " +
					"(x, y, z, world, biome, username, uuid, time) " +
					"VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
	
	private Tracker plugin;
	private MySQLConnection sqlConnection;
	
	public Queryer(Tracker plugin, Consumer<Boolean> callback) {
		this.plugin = plugin;
		this.sqlConnection = new MySQLConnection(plugin);
		
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			final boolean success = sqlConnection.initialize();
			Bukkit.getScheduler().runTask(plugin, () -> {
				callback.accept(success);
			});
		});
	}
	
	/**
	 * Stores position data of all online players.
	 */
	public void storePositionData() {
		final List<PositionEntry> entries = Bukkit.getOnlinePlayers().stream()
				.map(v -> new PositionEntry(v))
				.collect(Collectors.toList());
		
		Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
			Connection connection = this.sqlConnection.getConnection();
			try {
				connection.setAutoCommit(false);
				PreparedStatement statement = connection.prepareStatement(INSERT_FORMAT);
				for (PositionEntry entry : entries) {
					entry.addInsertionToBatch(statement);
				}
				statement.executeBatch();
				connection.commit();
				statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		});
	}
}

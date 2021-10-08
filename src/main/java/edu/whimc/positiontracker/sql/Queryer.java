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
 * Handles adding position data to the SQL Database.
 */
public class Queryer {

    /**
     * An entry containing data on the current player's position and time it was created.
     */
    public class PositionEntry {
        /** The x, y, and z coordinates representing the position. */
        private final int x, y, z;
        /** The current world, biome, and player's username. */
        private final String world, biome, username;
        /** The unique ID for this entry. */
        private final UUID uuid;
        /** The time the entry was created. */
        private final Timestamp time;

        /**
         * Constructs a PositionEntry.
         *
         * @param player the current player.
         */
        public PositionEntry(Player player) {
            Location loc = player.getLocation();
            this.x = loc.getBlockX();
            this.y = loc.getBlockY();
            this.z = loc.getBlockZ();
            this.world = loc.getWorld().getName();
            String biome;
            try {
                biome = loc.getBlock().getBiome().name();
            } catch(Exception e) {
                biome = "unknown";
            }
            this.biome = biome;
            this.username = player.getName();
            this.uuid = player.getUniqueId();
            this.time = new Timestamp(System.currentTimeMillis());
        }

        /**
         * Adds this entry's data to the PreparedStatement's batch
         *
         * @param statement the SQL PreparedStatement.
         * @throws SQLException when the statement cannot be added.
         */
        public void addInsertionToBatch(PreparedStatement statement) throws SQLException {
            statement.setInt(1, this.x);
            statement.setInt(2, this.y);
            statement.setInt(3, this.z);
            statement.setString(4, this.world);
            statement.setString(5, this.biome);
            statement.setString(6, this.username);
            statement.setString(7, this.uuid.toString());
            statement.setLong(8, this.time.getTime() / 1000);
            statement.addBatch();
        }

    }

    /** SQL query to insert position data. */
    private static final String INSERT_FORMAT =
            "INSERT INTO `whimc_player_positions` " +
                    "(x, y, z, world, biome, username, uuid, time) " +
                    "VALUES(?, ?, ?, ?, ?, ?, ?, ?)";

    /** The instance of the plugin. */
    private Tracker plugin;
    /** The connection to the SQL database. */
    private MySQLConnection sqlConnection;

    /**
     * Constructs a Queryer.
     *
     * @param plugin the instance of the plugin.
     * @param callback the event callback.
     */
    public Queryer(Tracker plugin, Consumer<Boolean> callback) {
        this.plugin = plugin;
        this.sqlConnection = new MySQLConnection(plugin);

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            final boolean success = this.sqlConnection.initialize();
            Bukkit.getScheduler().runTask(plugin, () -> {
                callback.accept(success);
            });
        });
    }

    /**
     * Stores the position data.
     */
    public void storePositionData() {
        // get position data for all online players
        final List<PositionEntry> entries = Bukkit.getOnlinePlayers().stream()
                .map(PositionEntry::new)
                .collect(Collectors.toList());

        // add data to database asynchronously
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try (Connection connection = this.sqlConnection.getConnection()) {
                connection.setAutoCommit(false);
                try (PreparedStatement statement = connection.prepareStatement(INSERT_FORMAT)) {
                    for (PositionEntry entry : entries) {
                        entry.addInsertionToBatch(statement);
                    }
                    statement.executeBatch();
                    connection.commit();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}

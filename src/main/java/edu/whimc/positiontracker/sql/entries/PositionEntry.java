package edu.whimc.positiontracker.sql.entries;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * An entry containing data on the current player's position and time it was created.
 */
public class PositionEntry extends DataEntry {

    public static final String INSERT_QUERY =
            "INSERT INTO `whimc_player_positions` (`x`, `y`, `z`, `yaw`, `pitch`, `world`, `biome`, `username`, `gamemode`, `uuid`, `time`) " +
                    "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    /**
     * The x, y, and z coordinates representing the position. Yaw and Pitch for player facing.
     */
    private final int x, y, z;
    private final float yaw, pitch;
    /**
     * The current world, biome, and player's username.
     */
    private final String world, biome, username, gamemode;
    /**
     * The unique ID for this entry.
     */
    private final UUID uuid;
    /**
     * The time the entry was created.
     */
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
        this.yaw = loc.getYaw();
        this.pitch = loc.getPitch();
        this.world = loc.getWorld().getName();
        String biome;
        try {
            biome = loc.getBlock().getBiome().name();
        } catch (Exception e) {
            biome = "unknown";
        }
        this.biome = biome;
        this.username = player.getName();
        this.gamemode = player.getGameMode().name();
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
        statement.setFloat(4, this.yaw);
        statement.setFloat(5, this.pitch);
        statement.setString(6, this.world);
        statement.setString(7, this.biome);
        statement.setString(8, this.username);
        statement.setString(9, this.gamemode);
        statement.setString(10, this.uuid.toString());
        statement.setLong(11, this.time.getTime() / 1000);
        statement.addBatch();
    }

    @Override
    public void addToStatement(PreparedStatement statement) throws SQLException {
        addInsertionToBatch(statement);
    }
}

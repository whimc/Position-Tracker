package edu.whimc.positiontracker.sql.entries;

import edu.whimc.positiontracker.regionevents.events.RegionEvent;
import edu.whimc.positiontracker.regionevents.events.RegionLeaveEvent;
import edu.whimc.positiontracker.regionevents.objects.RegionTrigger;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;
import org.bukkit.Location;

public class RegionEntry extends DataEntry {

    public static final String INSERT_QUERY =
            "INSERT INTO `whimc_player_region_events` (`region`, `trigger`, `isEnter`, `x`, `y`, `z`, `world`, `username`, `uuid`, `time`) " +
                    "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    /**
     * The region's name.
     */
    private final String regionName;
    /**
     * The type of action that caused the event.
     */
    private final RegionTrigger trigger;
    /**
     * Whether this is an enter (or leave) event.
     */
    private final boolean isEnter;
    /**
     * The x, y, and z coordinates representing the position.
     */
    private final int x, y, z;
    /**
     * The current world and player's username.
     */
    private final String world, username;
    /**
     * The unique ID for this entry.
     */
    private final UUID uuid;
    /**
     * The time the entry was created.
     */
    private final Timestamp time;

    public RegionEntry(RegionEvent event) {
        Location loc = event.getLocation();
        this.regionName = event.getRegion().getId();
        this.trigger = event.getTrigger();
        this.isEnter = event instanceof RegionLeaveEvent;
        this.x = loc.getBlockX();
        this.y = loc.getBlockY();
        this.z = loc.getBlockZ();
        this.world = loc.getWorld().getName();
        this.username = event.getPlayer().getName();
        this.uuid = event.getPlayer().getUniqueId();
        this.time = new Timestamp(System.currentTimeMillis());
    }

    @Override
    public void addToStatement(PreparedStatement statement) throws SQLException {
        statement.setString(1, this.regionName);
        statement.setString(2, this.trigger.toString());
        statement.setBoolean(3, this.isEnter);
        statement.setInt(4, this.x);
        statement.setInt(5, this.y);
        statement.setInt(6, this.z);
        statement.setString(7, this.world);
        statement.setString(8, this.username);
        statement.setString(9, this.uuid.toString());
        statement.setLong(10, this.time.getTime() / 1000);
        statement.addBatch();
    }
}

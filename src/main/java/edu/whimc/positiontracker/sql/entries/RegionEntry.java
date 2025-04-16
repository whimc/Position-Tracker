package edu.whimc.positiontracker.sql.entries;

import edu.whimc.positiontracker.regionevents.events.RegionEvent;
import edu.whimc.positiontracker.regionevents.events.RegionLeaveEvent;
import edu.whimc.positiontracker.regionevents.objects.RegionTrigger;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class RegionEntry extends DataEntry {

    public static final String INSERT_QUERY =
            "INSERT INTO `whimc_player_region_events` (`region`, `region_members`, `trigger`, `isEnter`, `x`, `y`, `z`, `yaw`, `pitch`, `world`, `username`, `uuid`, `time`) " +
                    "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    /**
     * The region's name.
     */
    private final String regionName;
    /**
     * The members of the region at the time of the event.
     */
    private final String regionMembers;
    /**
     * The type of action that caused the event.
     */
    private final RegionTrigger trigger;
    /**
     * Whether this is an enter (or leave) event.
     */
    private final boolean isEnter;
    /**
     * The x, y, and z coordinates representing the position as well as the angle they're looking.
     */
    private final int x, y, z;
    private final float yaw, pitch;
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
        this.regionMembers = String.join(",",
                event.getRegion().getMembers().getUniqueIds().stream()
                        .map(uuid -> {
                            String name = Bukkit.getOfflinePlayer(uuid).getName(); //convert UUID to username
                            return (name != null) ? name : uuid.toString(); // fallback if name unknown
                        })
                        .toList()
        );
        this.trigger = event.getTrigger();
        this.isEnter = event instanceof RegionLeaveEvent;
        this.x = loc.getBlockX();
        this.y = loc.getBlockY();
        this.z = loc.getBlockZ();
        this.yaw = loc.getYaw();
        this.pitch = loc.getPitch();
        /* default to unknown if null pointer exception: */
        this.world = (loc.getWorld() != null) ? loc.getWorld().getName() : "unknown";
        this.username = event.getPlayer().getName();
        this.uuid = event.getPlayer().getUniqueId();
        this.time = new Timestamp(System.currentTimeMillis());
    }

    @Override
    public void addToStatement(PreparedStatement statement) throws SQLException {
        statement.setString(1, this.regionName);
        statement.setString(2, this.regionMembers);
        statement.setString(3, this.trigger.toString());
        statement.setBoolean(4, this.isEnter);
        statement.setInt(5, this.x);
        statement.setInt(6, this.y);
        statement.setInt(7, this.z);
        statement.setFloat(8, this.yaw);
        statement.setFloat(9, this.pitch);
        statement.setString(10, this.world);
        statement.setString(11, this.username);
        statement.setString(12, this.uuid.toString());
        statement.setLong(13, this.time.getTime() / 1000);
        statement.addBatch();
    }
}

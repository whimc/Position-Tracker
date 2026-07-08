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

    private final String regionName;
    private final String regionMembers;
    private RegionTrigger trigger;
    private final boolean isEnter;
    private final int x, y, z;
    private final float yaw, pitch;
    private final String world, username;
    private final UUID uuid;
    private final Timestamp time;

    public RegionEntry(RegionEvent event) {
        Location loc = event.getLocation();
        this.regionName = event.getRegion().getId();
        this.regionMembers = String.join(",",
                event.getRegion().getMembers().getUniqueIds().stream()
                        .map(uuid -> {
                            String name = Bukkit.getOfflinePlayer(uuid).getName();
                            return (name != null) ? name : uuid.toString();
                        })
                        .toList()
        );
        this.trigger = event.getTrigger();
        this.isEnter = !(event instanceof RegionLeaveEvent);
        this.x = loc.getBlockX();
        this.y = loc.getBlockY();
        this.z = loc.getBlockZ();
        this.yaw = loc.getYaw();
        this.pitch = loc.getPitch();
        this.world = (loc.getWorld() != null) ? loc.getWorld().getName() : "unknown";
        this.username = event.getPlayer().getName();
        this.uuid = event.getPlayer().getUniqueId();
        this.time = new Timestamp(System.currentTimeMillis());
    }

    public RegionEntry(RegionEvent event, RegionTrigger overrideTrigger) {
        this(event);
        this.trigger = overrideTrigger;
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
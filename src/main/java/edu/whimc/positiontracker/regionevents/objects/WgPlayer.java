package edu.whimc.positiontracker.regionevents.objects;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import edu.whimc.positiontracker.PositionTracker;
import edu.whimc.positiontracker.regionevents.RegionUtils;
import edu.whimc.positiontracker.regionevents.events.RegionEnterEvent;
import edu.whimc.positiontracker.regionevents.events.RegionLeaveEvent;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Modified version of <a href="https://github.com/NetzkroneHD/WGRegionEvents/tree/master">WGRegionEvents</a>
 */
public class WgPlayer {

    private final PositionTracker plugin;
    private final Player player;
    private final Set<ProtectedRegion> currentRegions = new HashSet<>();

    public WgPlayer(PositionTracker plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    public Set<ProtectedRegion> getCurrentRegions() {
        return this.currentRegions;
    }

    public void updateRegionsFoo(RegionTrigger trigger, Location from, Location to) {
        final ApplicableRegionSet toRegions = RegionUtils.getRegions(to);
        final ApplicableRegionSet fromRegions = RegionUtils.getRegions(from);

        // Handle leaving regions
        for (ProtectedRegion region : fromRegions) {
            if (!toRegions.getRegions().contains(region)) {
                handleLeaveRegion(trigger, region, from, to);
            }
        }

        // Handle entering regions
        for (ProtectedRegion region : toRegions) {
            if (!this.currentRegions.contains(region)) {
                handleEnterRegion(trigger, region, from, to);
            }
        }
    }

    public void handleEnterRegion(RegionTrigger trigger, ProtectedRegion region, Location from, Location to) {
        this.currentRegions.add(region);
        this.plugin.getServer().getPluginManager()
                .callEvent(new RegionEnterEvent(trigger, region, this.player, from, to));
    }

    public void handleLeaveRegion(RegionTrigger trigger, ProtectedRegion region, Location from, Location to) {
        this.plugin.getServer().getPluginManager()
                .callEvent(new RegionLeaveEvent(trigger, region, this.player, from, to));
        this.currentRegions.remove(region);
    }

}

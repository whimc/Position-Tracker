package edu.whimc.positiontracker.regionevents.listeners;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import edu.whimc.positiontracker.PositionTracker;
import edu.whimc.positiontracker.regionevents.events.RegionEnterEvent;
import edu.whimc.positiontracker.sql.entries.RegionEntry;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Set;
import java.util.UUID;

public class RegionVisitListener implements Listener {

    private final PositionTracker plugin;

    public RegionVisitListener(PositionTracker plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onRegionEnter(RegionEnterEvent event) {
        ProtectedRegion region = event.getRegion();

        // Only react to regions with BASE_ prefix
        if (!region.getId().startsWith("BASE_")) return;

        UUID playerId = event.getPlayer().getUniqueId();
        Set<UUID> memberIds = region.getMembers().getUniqueIds();

        // Skip if player is a member
        if (memberIds.contains(playerId)) return;

        // Skip if there are no other members
        if (memberIds.isEmpty() || (memberIds.size() == 1 && memberIds.contains(playerId))) return;

        // Create and queue the RegionEntry to be saved to the database
        RegionEntry entry = new RegionEntry(event);
        plugin.getDataStore().addEntry(entry);

        //debug logging
        plugin.getLogger().info("VISIT trigger fired for " + event.getPlayer().getName() + " in " + region.getId());
    }
}

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

        // Defensive: only proceed if region's world matches the player's world
        if (!event.getFrom().getWorld().equals(event.getTo().getWorld())) {
            return; // unlikely, but safe
        }
        if (!event.getTo().getWorld().getName().equals(event.getPlayer().getWorld().getName())) {
            return; // redundant but safe
        }

        // Only react to regions with BASE_ prefix
        if (!region.getId().startsWith("base_")) return;

        // Exclude region named "perimeter" exactly (case-insensitive or not? Here: case-insensitive example)
        if (region.getId().equalsIgnoreCase("perimeter")) return;

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
        plugin.debugLog("VISIT fired: " + region.getId() + " in world " + event.getTo().getWorld().getName());
    }
}

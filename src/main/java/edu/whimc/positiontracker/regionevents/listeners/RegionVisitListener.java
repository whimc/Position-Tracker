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

        // Defensive: world consistency check
        if (!event.getFrom().getWorld().equals(event.getTo().getWorld())) return;
        if (!event.getTo().getWorld().getName().equals(event.getPlayer().getWorld().getName())) return;

        // Only react to regions with BASE_ prefix (case-insensitive)
        if (!region.getId().toLowerCase().startsWith("base_")) return;

        // Exclude region named exactly perimeter
        if (region.getId().equalsIgnoreCase("perimeter")) return;
        if (region.getMembers() == null) return;

        UUID playerId = event.getPlayer().getUniqueId();
        Set<UUID> memberIds = region.getMembers().getUniqueIds();

        if (memberIds.contains(playerId)) return;
        if (memberIds.isEmpty() || (memberIds.size() == 1 && memberIds.contains(playerId))) return;

        plugin.getDataStore().addEntry(new RegionEntry(event));
        plugin.debugLog("VISIT fired: " + region.getId() + " in world " + event.getTo().getWorld().getName());
    }
}

package edu.whimc.positiontracker.listeners;

import edu.whimc.positiontracker.PositionTracker;
import edu.whimc.positiontracker.regionevents.events.RegionEvent;
import edu.whimc.positiontracker.sql.entries.RegionEntry;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class RegionEnterLeaveListener implements Listener {

    private final PositionTracker plugin;

    public RegionEnterLeaveListener(PositionTracker plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onRegionChange(RegionEvent event) {
        this.plugin.getDataStore().addData(new RegionEntry(event));
    }
}

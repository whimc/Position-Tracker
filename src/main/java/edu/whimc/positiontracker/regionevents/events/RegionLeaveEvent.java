package edu.whimc.positiontracker.regionevents.events;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import edu.whimc.positiontracker.regionevents.objects.RegionTrigger;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class RegionLeaveEvent extends RegionEvent {

    public RegionLeaveEvent(RegionTrigger trigger, ProtectedRegion region, Player player, Location from, Location to) {
        super(trigger, region, player, from, to);
    }

    @Override
    public Location getLocation() {
        return getFrom();
    }
}

package edu.whimc.positiontracker.regionevents.events;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import edu.whimc.positiontracker.regionevents.objects.RegionTrigger;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public abstract class RegionEvent extends PlayerEvent {

    private static final HandlerList handlerList = new HandlerList();

    private final RegionTrigger trigger;
    private final ProtectedRegion region;
    private final Location from, to;

    public RegionEvent(RegionTrigger trigger, ProtectedRegion region, Player player, Location from, Location to) {
        super(player);
        this.trigger = trigger;
        this.region = region;
        this.from = from;
        this.to = to;
    }

    public static HandlerList getHandlerList() {
        return RegionEvent.handlerList;
    }

    @Override

    public HandlerList getHandlers() {
        return RegionEvent.handlerList;
    }

    public RegionTrigger getTrigger() {
        return this.trigger;
    }

    public ProtectedRegion getRegion() {
        return this.region;
    }

    public Location getFrom() {
        return this.from;
    }

    public Location getTo() {
        return this.to;
    }

    public abstract Location getLocation();
}

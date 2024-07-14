package edu.whimc.positiontracker.regionevents.objects;

import edu.whimc.positiontracker.PositionTracker;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class WgPlayerCache {

    private PositionTracker plugin;
    private Map<UUID, WgPlayer> cache = new HashMap<>();

    public WgPlayerCache(PositionTracker plugin) {
        this.plugin = plugin;
    }

    public @Nullable WgPlayer getPlayer(Player player) {
        return this.cache.get(player.getUniqueId());
    }

    public void remove(Player player) {
        this.cache.remove(player.getUniqueId());
    }

    public void add(Player player) {
        this.cache.put(player.getUniqueId(), new WgPlayer(this.plugin, player));
    }

    public void updateRegions(RegionTrigger trigger, Player player, Location from, Location to) {
        WgPlayer wgPlayer = this.cache.get(player.getUniqueId());
        if (wgPlayer == null) {
            return;
        }

        wgPlayer.updateRegionsFoo(trigger, from, to);
    }
}

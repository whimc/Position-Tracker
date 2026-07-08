package edu.whimc.positiontracker.regionevents.listeners;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import edu.whimc.positiontracker.PositionTracker;
import edu.whimc.positiontracker.regionevents.events.RegionLeaveEvent;
import edu.whimc.positiontracker.regionevents.objects.RegionTrigger;
import edu.whimc.positiontracker.regionevents.objects.WgPlayer;
import edu.whimc.positiontracker.regionevents.objects.WgPlayerCache;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class RegionListeners implements Listener {

    private final PositionTracker plugin;
    private final WgPlayerCache cache;

    public RegionListeners(PositionTracker plugin, WgPlayerCache cache) {
        this.plugin = plugin;
        this.cache = cache;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onLogin(PlayerLoginEvent event) {
        if (event.getResult() == PlayerLoginEvent.Result.ALLOWED) {
            // Clear from cache just in case
            this.cache.remove(event.getPlayer());
            this.cache.add(event.getPlayer());
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        Location loc = event.getPlayer().getLocation();
        this.cache.updateRegions(RegionTrigger.JOIN, event.getPlayer(), loc, loc);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent event) {
        handlePlayerLeave(RegionTrigger.QUIT, event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onKick(PlayerKickEvent event) {
        handlePlayerLeave(RegionTrigger.KICK, event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onMove(PlayerMoveEvent event) {
        this.cache.updateRegions(RegionTrigger.MOVE, event.getPlayer(), event.getFrom(), event.getTo());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onTeleport(PlayerTeleportEvent event) {
        this.cache.updateRegions(RegionTrigger.TELEPORT, event.getPlayer(), event.getFrom(), event.getTo());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRespawn(PlayerRespawnEvent event) {
        this.cache.updateRegions(RegionTrigger.RESPAWN, event.getPlayer(), event.getPlayer().getLocation(),
                event.getRespawnLocation());
    }

    private void handlePlayerLeave(RegionTrigger trigger, Player player) {
        WgPlayer wgPlayer = this.cache.getPlayer(player);
        if (wgPlayer == null) {
            return;
        }

        for (ProtectedRegion region : wgPlayer.getCurrentRegions()) {
            this.plugin.getServer().getPluginManager().callEvent(
                    new RegionLeaveEvent(trigger, region, player, player.getLocation(), player.getLocation()));
        }

        this.cache.remove(player);
    }

}

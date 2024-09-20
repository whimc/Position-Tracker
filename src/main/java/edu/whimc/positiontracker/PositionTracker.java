package edu.whimc.positiontracker;

import edu.whimc.positiontracker.listeners.RegionEnterLeaveListener;
import edu.whimc.positiontracker.regionevents.listeners.RegionListeners;
import edu.whimc.positiontracker.regionevents.objects.WgPlayerCache;
import edu.whimc.positiontracker.sql.DataStore;
import edu.whimc.positiontracker.sql.entries.PositionEntry;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The main plugin class.
 */
public class PositionTracker extends JavaPlugin {
    /**
     * The debug mode status.
     */
    private boolean debug = false;
    /**
     * The DataStore for the SQL database.
     */
    private DataStore dataStore;
    /**
     * Cache for per-player WorldGuard regions.
     */
    private WgPlayerCache wgPlayerCache;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onEnable() {
        saveDefaultConfig();
        getConfig().options().copyDefaults(false);
        this.debug = getConfig().getBoolean("debug", false);

        getCommand("positiontracker").setExecutor(new TrackerCommand(this));

        // Load WorldGuard-specific things if we have WorldGuard
        if (Bukkit.getPluginManager().isPluginEnabled("WorldGuard")) {
            this.wgPlayerCache = new WgPlayerCache(this);
            Bukkit.getPluginManager().registerEvents(new RegionListeners(this, this.wgPlayerCache), this);
            Bukkit.getPluginManager().registerEvents(new RegionEnterLeaveListener(this), this);
        }

        this.dataStore = new DataStore(this, success -> {
            if (success) {
                this.dataStore.run();
                this.getLogger().info("---- PositionTracker has started! ----");
            } else {
                this.getLogger().severe("Could not create MySQL connection! Disabling plugin...");
                this.getPluginLoader().disablePlugin(this);
            }
        });

        // Poll player data every few seconds
        int pollInterval = getConfig().getInt("position_poll_interval_seconds", 2) * 20;
        Bukkit.getScheduler().runTaskTimer(this,
                () -> Bukkit.getOnlinePlayers().stream().map(PositionEntry::new).forEach(this.dataStore::addData), 0,
                pollInterval);

    }

    public DataStore getDataStore() {
        return this.dataStore;
    }

    /**
     * Prints the passed String into the server logs.
     *
     * @param str the String to print.
     */
    public void debugLog(String str) {
        if (this.debug) {
            this.getLogger().info(str);
        }
    }

    /**
     * @return whether debug mode is enabled.
     */
    public boolean isDebug() {
        return this.debug;
    }

    /**
     * @param bool the debug desired status (true = on, false = off).
     */
    public void setDebug(boolean bool) {
        this.debug = bool;
    }
}

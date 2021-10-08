package edu.whimc.positiontracker;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import edu.whimc.positiontracker.sql.Queryer;

/**
 * The main plugin class.
 */
public class Tracker extends JavaPlugin {
	/** The current Task's ID. */
	private int taskID = -1;
	/** The debug mode status. */
	private boolean debug = false;
	/** The Queryer for the SQL database */
	private Queryer queryer;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onEnable() {
		saveDefaultConfig();
		getConfig().options().copyDefaults(false);
		this.debug = getConfig().getBoolean("debug", false);

		getCommand("positiontracker").setExecutor(new TrackerCommand(this));
		Bukkit.getPluginManager().registerEvents(new JoinLeaveListener(this), this);
		queryer = new Queryer(this, success -> {
			if (success) {
				if (startRunner()) {
					this.getLogger().info("---- Tracker has started! ----");
				} else {
					this.getLogger().info("---- Tracker did not start! ----");
				}
			} else {
				this.getLogger().severe("Could not create MySQL connection! Disabling plugin...");
				this.getPluginLoader().disablePlugin(this);
			}
		});
		
	}

	/**
	 * Starts player location data collection.
	 *
	 * @return whether or not the data collection has been successfully started.
	 */
	public boolean startRunner() {
		// check if already running
		if (taskID != -1) {
			this.getLogger().warning("The runner has already been started!");
			return false;
		}

		taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
			if (Bukkit.getOnlinePlayers().size() != 0) {
				debugLog("Logging for " + Bukkit.getOnlinePlayers().size() + " player(s)...");
				this.queryer.storePositionData();
			}
		}, 20 * 3, 20 * 3);

		return true;
	}

	/**
	 * Stops player data collection.
	 *
	 * @return whether or not the data collection has been successfully stopped.
	 */
	public boolean stopRunner() {
		// check if already stopped
		if (!isRunning()) {
			return false;
		}

		Bukkit.getScheduler().cancelTask(taskID);
		taskID = -1;
		return true;
	}

	/**
	 * @return if the data collection is running.
	 */
	public boolean isRunning() {
		return Bukkit.getScheduler().isQueued(taskID);
	}

	/**
	 * @return the current task ID.
	 */
	public int getTaskID() {
		return taskID;
	}

	/**
	 * Prints the passed String into the server logs.
	 *
	 * @param str the String to print.
	 */
	public void debugLog(String str) {
		// check if in debug mode
		if (!this.debug) return;
		this.getLogger().info(str);
	}

	/**
	 * @return whether or not debug mode is enabled.
	 */
	public boolean getDebug() {
		return this.debug;
	}

	/**
	 * @param bool the debug desired status (true = on, false = off).
	 */
	public void setDebug(boolean bool) {
		this.debug = bool;
	}
}

package edu.whimc.positiontracker;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import edu.whimc.positiontracker.sql.Queryer;

/**
 * Calls http://69.175.72.201:8120/up/world/world/ every 3 seconds.
 * @author Jack Henhapl
 */
public class Tracker extends JavaPlugin {

	private int taskID = -1;
	private boolean debug = false;
	private Queryer queryer;
	
	/**
	 * Opens connection and starts calling it every 3 seconds.
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
	 * Starts the three second runner
	 * @return Whether or not the runner was set
	 */
	public boolean startRunner() {
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
	 * Cancels the runner
	 * @return
	 */
	public boolean stopRunner() {
		if (!isRunning()) {
			return false;
		}

		Bukkit.getScheduler().cancelTask(taskID);
		taskID = -1;
		return true;
	}

	/**
	 * Is the task currently running.
	 * @return true/false whether or not the task is running
	 */
	public boolean isRunning() {
		return Bukkit.getScheduler().isQueued(taskID);
	}

	public int getTaskID() {
		return taskID;
	}

	public void debugLog(String str) {
		if (!this.debug) return;
		this.getLogger().info(str);
	}

	public boolean getDebug() {
		return this.debug;
	}

	public void setDebug(boolean bool) {
		this.debug = bool;
	}
}

package edu.whimc.positiontracker;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import edu.whimc.positiontracker.sql.Queryer;

public class Tracker extends JavaPlugin {

	private int taskID = -1;
	private boolean debug = false;
	private Queryer queryer;
	
	//initialization
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

	// start plugin, run storePositionData() every 3 seconds
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

	// stop plugin
	public boolean stopRunner() {
		if (!isRunning()) {
			return false;
		}

		Bukkit.getScheduler().cancelTask(taskID);
		taskID = -1;
		return true;
	}
	
	// all functions used for in-game commands
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

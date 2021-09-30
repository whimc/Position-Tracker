package edu.whimc.positiontracker;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Handles toggling of data collection based on number of current online players.
 */
public class JoinLeaveListener implements Listener {
	/** The instance of the plugin. */
	private Tracker plugin;

	/**
	 * Constructs a JoinLeaveListener.
	 *
	 * @param plugin
	 */
	public JoinLeaveListener(Tracker plugin) {
		this.plugin = plugin;
	}

	/**
	 * Starts data collection if it's not running already.
	 *
	 * @param event the event called when a player joins the server.
	 */
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		if (plugin.isRunning()) return;
		plugin.startRunner();
	}

	/**
	 * Stops data collection when there are no players online.
	 *
	 * @param event the event called when a player joins the server.
	 */
	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		if (Bukkit.getOnlinePlayers().size() != 0) return;
		plugin.stopRunner();
	}
}

package edu.whimc.positiontracker;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinLeaveListener implements Listener {

	private Tracker plugin;
	
	public JoinLeaveListener(Tracker plugin) {
		this.plugin = plugin;
	}
	
	// starts plugin on first player joining
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		if (plugin.isRunning()) return;
		plugin.startRunner();
	}
	
	// stops plugin on last player leaving
	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		if (Bukkit.getOnlinePlayers().size() != 0) return;
		plugin.stopRunner();
	}
}

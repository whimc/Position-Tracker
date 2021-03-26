package edu.whimc.positiontracker;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TrackerCommand implements CommandExecutor{
	
	private Tracker plugin;
	
	public TrackerCommand(Tracker plugin) {
		this.plugin = plugin;
	}
	
	// function to handle all commands
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.isOp()) {
			sender.sendMessage(ChatColor.RED + "You must be OP to use this command!");
			return true;
		}
		
		if (args.length == 0) {
			sendCommands(sender);
			return true;
		}
		
		String arg = args[0];
		
		if (arg.equalsIgnoreCase("debug")) {
			String message = ChatColor.YELLOW + "Console debug messages are now ";
			if (plugin.getDebug()) {
				message += ChatColor.RED + "off";
			} else {
				message += ChatColor.GREEN + "on";
			}
			
			plugin.setDebug(!plugin.getDebug());
			sender.sendMessage(message);
			
			return true;
		}
		
		boolean running = plugin.isRunning();
		
		if (arg.equalsIgnoreCase("status")) {
			String message = ChatColor.YELLOW + "PositionTracker is currently ";
			
			if (running) {
				message += ChatColor.GREEN + "running";
			} else {
				message += ChatColor.RED + "stopped";
			}
			message += ChatColor.YELLOW + ". " + ChatColor.GRAY + "" + ChatColor.ITALIC + 
					" (debug messages are currently " + (plugin.getDebug() ? "on" : "off") + ")";
			
			sender.sendMessage(message);
			return true;
		}
		
		if (arg.equalsIgnoreCase("start")) {
			if (running) {
				sender.sendMessage(ChatColor.RED + "The tracker is already running!");
				return true;
			}
			
			boolean success = plugin.startRunner();
			if (success) {
				sender.sendMessage(ChatColor.GREEN + "Tracker started!");
			} else {
				sender.sendMessage(ChatColor.RED + "There was an error! Check console.");
			}
			
			return true;
		}
		
		if (arg.equalsIgnoreCase("stop")) {
			if (!running) {
				sender.sendMessage(ChatColor.RED + "The tracker is already stopped!");
				return true;
			}
			
			boolean success = plugin.stopRunner();
			if (success) {
				sender.sendMessage(ChatColor.GREEN + "Tracker stopped!");
			} else {
				sender.sendMessage(ChatColor.RED + "There was an error! Check console.");
			}
			
			return true;
		}
		
		if (arg.equalsIgnoreCase("help")) {
			sendCommands(sender);
		}
		
		sendMessage(ChatColor.RED + "Not a valid argument - /positiontracker for a list of valid commands.")
		return true;
	}
	
	// prints list of commands
	private void sendCommands(CommandSender sender) {
		sender.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "Position Tracker");
		sender.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + 
				"   /positiontracker - brings up this menu");
		sender.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + 
				"   /positiontracker status - get status of tracker");
		sender.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + 
				"   /positiontracker debug - toggle debug messages on/off in console");
		sender.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + 
				"   /positiontracker start - starts tracker");
		sender.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + 
				"   /positiontracker stop - stops tracker");
		
	}
}

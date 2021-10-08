package edu.whimc.positiontracker;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Main handler for the /positiontracker root command.
 */
public class TrackerCommand implements CommandExecutor{
	/** The instance of the plugin. */
	private Tracker plugin;

	/**
	 * Constructs a TrackerCommand.
	 *
	 * @param plugin the instance of the plugin.
	 */
	public TrackerCommand(Tracker plugin) {
		this.plugin = plugin;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		// ensure sender is an operator
		if (!sender.isOp()) {
			sender.sendMessage(ChatColor.RED + "You must be OP to use this command!");
			return true;
		}

		// send valid command list if no arguments provided
		if (args.length == 0) {
			sendCommands(sender);
			return true;
		}
		
		String arg = args[0];

		// toggling debug mode
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

		// checking plugin status
		if (arg.equalsIgnoreCase("status")) {
			String message = ChatColor.YELLOW + "PositionTracker is currently ";
			
			if (running) {
				message += ChatColor.GREEN + "Running";
			} else {
				message += ChatColor.RED + "Stopped";
			}
			message += ChatColor.YELLOW + ". " + ChatColor.GRAY + "" + ChatColor.ITALIC + 
					" (debugger " + (plugin.getDebug() ? "on" : "off") + ")";
			
			sender.sendMessage(message);
			return true;
		}

		// starting the tracker
		if (arg.equalsIgnoreCase("start")) {
			// notify sender if already running
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

		// stopping the tracker
		if (arg.equalsIgnoreCase("stop")) {
			// notify sender if already stopped
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

		sendCommands(sender);
		return true;
	}

	/**
	 * Sends the list of commands and their usage to the sender.
	 *
	 * @param sender the command's sender.
	 */
	private void sendCommands(CommandSender sender) {
		sender.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "Position Tracker");
		sender.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + 
				"   /positiontracker status - get status of tracker");
		sender.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + 
				"   /positiontracker debug - toggle debug messages in console");
		sender.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + 
				"   /positiontracker start - starts tracker");
		sender.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + 
				"   /positiontracker stop - stops tracker");
	}
}

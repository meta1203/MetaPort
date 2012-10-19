package me.meta1203.plugins.metaport;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.ChatColor;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;

public class Metaport extends JavaPlugin implements Listener {
    public void onDisable() {
        // TODO: Place any custom disable code here.
    }

    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }
    
    public static WorldEditPlugin getWorldGuard(JavaPlugin plugin)
    {
        Plugin wPlugin = plugin.getServer().getPluginManager().getPlugin("WorldGuard");
        
        if ((wPlugin == null) || (!(wPlugin instanceof WorldEditPlugin)))
        {
            return null;
        }
        
        return (WorldEditPlugin) wPlugin;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage("Welcome, " + event.getPlayer().getDisplayName() + "!");
    }

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 0) {
			printUsage(sender);
			return true;
		}
		if (args[0].equalsIgnoreCase("addserver")) {
			
		}
		
		// Fail
		printUsage(sender);
		return true;
	}

	@Override
	public List<Class<?>> getDatabaseClasses() {
		List<Class<?>> classes = new LinkedList<Class<?>>();
		
		return classes;
	}

	private void printUsage(CommandSender sender) {
		sender.sendMessage(ChatColor.RED + "Usage:\n");
		sender.sendMessage(ChatColor.BLUE + "/mport addserver <servername> <ip> <port>");
		sender.sendMessage(ChatColor.BLUE + "/mport delserver <servername>");
		sender.sendMessage(ChatColor.BLUE + "/mport create <name>");
		sender.sendMessage(ChatColor.BLUE + "/mport delete <name>");
		sender.sendMessage(ChatColor.BLUE + "/mport setdest <name> <servername> <x> <y> <z>");
		sender.sendMessage(ChatColor.BLUE + "/mport stp <servername> [x] [y] [z]");
	}
}


package me.meta1203.plugins.metaport;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import com.avaje.ebean.Query;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;

public class Metaport extends JavaPlugin implements Listener {
	private Connection c = null;
	private WorldEditPlugin wep = null;
	private String sName = "";
	private String mysql_ip = "";
	private String mysql_pass = "";
	private int mysql_port = 0;
	private String database = "";
	private String mysql_user = "";
	private List<PortalStorage> portals = new ArrayList<PortalStorage>();
	
    public void onDisable() {
        // TODO: Place any custom disable code here.
    }

    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        this.getConfig().options().copyDefaults(true);
        this.sName = this.getConfig().getString("server-name");
        this.mysql_ip = this.getConfig().getString("mysql-ip");
        this.mysql_pass = this.getConfig().getString("mysql-pass");
        this.mysql_user = this.getConfig().getString("mysql-user");
        this.mysql_port = this.getConfig().getInt("mysql-port");
        this.database = this.getConfig().getString("database-name");
        this.wep = getWorldEdit(this);
        
        c = new Connection(sName, database, mysql_ip, mysql_port, mysql_user, mysql_pass);
    }
    
    public static WorldEditPlugin getWorldEdit(JavaPlugin plugin)
    {
        Plugin wPlugin = plugin.getServer().getPluginManager().getPlugin("WorldEdit");
        
        if ((wPlugin == null) || (!(wPlugin instanceof WorldEditPlugin)))
        {
            return null;
        }
        
        return (WorldEditPlugin) wPlugin;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerLoginEvent event) {
        event.setKickMessage(null);
        try {
			Location l = c.getPlayerPos(c.getCon().createStatement(), event.getPlayer().getName(), this.getServer());
			event.getPlayer().teleport(l);
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
    
    @EventHandler
    public void onPlayerLogout(PlayerQuitEvent event) {
    	Coord l = new Coord();
        l.x = event.getPlayer().getLocation().getX();
        l.y = event.getPlayer().getLocation().getY();
        l.z = event.getPlayer().getLocation().getZ();
        try {
			c.setPlayerPos(c.getCon().createStatement(), event.getPlayer().getName(), l, this.sName);
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
    	for (PortalStorage p : portals) {
    		Selection s = p.getParea();
    		if (s.contains(e.getTo())) {
    			try {
					c.setPlayerPos(c.getCon().createStatement(), e.getPlayer().getName(), p.getTo(), p.getServer());
					e.getPlayer().sendPluginMessage(this, "RubberBand", p.getServer().getBytes());
					e.setCancelled(true);
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
    		}
    	}
    }

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 0) {
			printUsage(sender);
			return true;
		}
		if (args[0].equalsIgnoreCase("create") && args.length == 2 && sender instanceof Player) {
			if (wep.getSelection((Player) sender) == null) {
				sender.sendMessage("Please make a WorldEdit Selection, then rerun the command");
				return true;
			}
			Coord out = new Coord();
			out.setLocation(wep.getSelection((Player) sender).getMinimumPoint());
			savePortal(args[1], wep.getSelection((Player) sender), out, this.sName);
			sender.sendMessage("Create new portal " + args[1] + ".");
			return true;
		}
		
		if (args[0].equalsIgnoreCase("setdest") && args.length == 7) {
			Coord c = new Coord();
			try {
				c.x = Double.parseDouble(args[4]);
				c.y = Double.parseDouble(args[5]);
				c.z = Double.parseDouble(args[6]);
			}
			catch (NumberFormatException nfe) {
				sender.sendMessage(ChatColor.RED + "Locations must be numbers!");
				return true;
			}
			c.world = args[3];
			String s = args[2];
			String name = args[1];
			if (loadPortal(name) == null) {
				sender.sendMessage("Portal must be created first!");
			}
			Selection select = loadPortal(name).getParea();
			savePortal(name, select, c, s);
			sender.sendMessage("Set new portal exit location!");
			return true;
		}
		
		if (args[0].equalsIgnoreCase("delete") && args.length == 2) {
			deletePortal(args[1]);
			sender.sendMessage("Deleted portal " + args[1] + ".");
			return true;
		}
		
		if (args[0].equalsIgnoreCase("stp") && sender instanceof Player) {
			if (args.length == 6) {
				Coord c = new Coord();
				try {
					c.x = Double.parseDouble(args[3]);
					c.y = Double.parseDouble(args[4]);
					c.z = Double.parseDouble(args[5]);
				}
				catch (NumberFormatException nfe) {
					sender.sendMessage(ChatColor.RED + "Locations must be numbers!");
					return true;
				}
				c.world = args[3];
				try {
					this.c.setPlayerPos(this.c.getCon().createStatement(), ((Player)sender).getName(), c, args[2]);
					((Player)sender).getPlayer().sendPluginMessage(this, "RubberBand", args[2].getBytes());
				} catch (SQLException e) {
					e.printStackTrace();
				}
				return true;
			}
			else if (args.length == 2) {
				((Player)sender).getPlayer().sendPluginMessage(this, "RubberBand", args[1].getBytes());
			}
		}
		// Fail
		printUsage(sender);
		return true;
	}

	@Override
	public List<Class<?>> getDatabaseClasses() {
		List<Class<?>> classes = new LinkedList<Class<?>>();
		classes.add(PortalStorage.class);
		return classes;
	}

	private void printUsage(CommandSender sender) {
		sender.sendMessage(ChatColor.RED + "Usage:\n");
		// sender.sendMessage(ChatColor.BLUE + "/mport addserver <servername> <ip> <port>");
		// sender.sendMessage(ChatColor.BLUE + "/mport delserver <servername>");
		sender.sendMessage(ChatColor.BLUE + "/mport create <name>");
		sender.sendMessage(ChatColor.BLUE + "/mport delete <name>");
		sender.sendMessage(ChatColor.BLUE + "/mport setdest <name> <servername> <world> <x> <y> <z>");
		sender.sendMessage(ChatColor.BLUE + "/mport stp <servername> ([world] [x] [y] [z])");
	}
	
	public void savePortal(String name, Selection area, Coord out, String server) {
		PortalStorage work = loadPortal(name);
		if (work == null) {
			work = getDatabase().createEntityBean(PortalStorage.class);
		}
		work.setName(name);
		work.setParea(area);
		work.setTo(out);
		work.setServer(server);
		getDatabase().save(work);
		portals.add(work);
	}
	
	public void deletePortal(String name) {
		Query<PortalStorage> query = getDatabase().find(PortalStorage.class);
		query.where().eq("name", name);
		List<PortalStorage> list = query.findList();
		if (list == null || list.size() == 0) {
			return;
		}
		getDatabase().delete(list.get(0));
	}
	
	public PortalStorage loadPortal(String name) {
		Query<PortalStorage> query = getDatabase().find(PortalStorage.class);
		query.where().eq("name", name);
		List<PortalStorage> list = query.findList();
		if (list == null || list.size() == 0) {
			return null;
		}
		return list.get(0);
	}
	
	public void loadAllPortals() {
		Query<PortalStorage> query = getDatabase().find(PortalStorage.class);
		portals = query.findList();
	}
}


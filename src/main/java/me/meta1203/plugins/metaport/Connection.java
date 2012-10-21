package me.meta1203.plugins.metaport;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;

public class Connection {
	private String url;
    private String user;
    private String password;
    private String serverName;
    
    public Connection(String sname, String database, String ip, int port, String user, String pass) {
    	this.url = "jdbc:mysql://" + ip + ":" + port + "/" + database;
    	this.user = user;
    	this.password = pass;
    	this.serverName = sname;
    }
    
    public java.sql.Connection getCon() {
    	try {
			return DriverManager.getConnection(url, user, password);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
    }
    
    public void startup() {
    	try {
			Statement s = this.getCon().createStatement();
			s.executeUpdate("CREATE TABLE IF NOT EXISTS mport_" + this.serverName + " (" +
					"name VARCHAR(20)," +
					"world VARCHAR(20)," +
					"x DOUBLE," +
					"y DOUBLE," +
					"z DOUBLE)");
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
    
    public Location getPlayerPos(Statement s, String pName, Server getWorld) {
    	try {
			ResultSet rs = s.executeQuery("SELECT * FROM mport_" + this.serverName + " WHERE name = " + pName);
			String world = rs.getString("world");
			double x = rs.getDouble("x");
			double y = rs.getDouble("y");
			double z = rs.getDouble("z");
			World w = getWorld.getWorld(world);
			if (w == null) {
				w = getWorld.getWorlds().get(0);
				return w.getSpawnLocation();
			}
			return new Location(w,x,y,z);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
    }
    
    public void setPlayerPos(Statement s, String pName, Coord v, String serverName) {
    	try {
			s.executeUpdate("INSERT INTO mport_" + serverName + " (name, world, x, y, z,) VALUES (" + "," + v.world + pName + "," + v.x + "," + v.y + "," + v.z + ")" +
					" ON DUPLICATE KEY UPDATE (world,x,y,z) VALUES (" + v.x + "," + v.y + "," + v.z + ")");
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
}

package me.meta1203.plugins.metaport;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;

public class Coord {
	public double x;
	public double y;
	public double z;
	public String world;
	
	public Location getLocation(Server s) {
		World w = s.getWorld(world);
		if (w == null) {
			return s.getWorlds().get(0).getSpawnLocation();
		}
		return new Location(w, x, y, z);
	}
	
	public void setLocation(Location l) {
		x = l.getX();
		y = l.getY();
		z = l.getZ();
		world = l.getWorld().getName();
	}
}

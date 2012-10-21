package me.meta1203.plugins.metaport;

import javax.persistence.Column;
import javax.persistence.Id;

import com.sk89q.worldedit.bukkit.selections.Selection;

public class PortalStorage {
	@Id private Long id;
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long data) {
		this.id = data;
	}
	
	@Column private String name;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Column private Selection parea;
	
	public Selection getParea() {
		return parea;
	}
	
	public void setParea(Selection area) {
		this.parea = area;
	}
	
	@Column private Coord to;
	
	public Coord getTo() {
		return to;
	}
	
	public void setTo(Coord v) {
		this.to = v;
	}
	
	@Column private String server;
	
	public String getServer() {
		return name;
	}
	
	public void setServer(String name) {
		this.name = name;
	}
}

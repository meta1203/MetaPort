package me.meta1203.plugins.metaport;

import javax.persistence.*;

@Entity
@Table(name ="table_name")
public class ServerStorage {
	@Id private Long id;
	
	@Column private String name;
	
	@Column private String ip;
	
	@Column private Integer port;
	
	public Long getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getIp() {
		return ip;
	}
	
	public Integer getPort() {
		return port;
	}
	
	public void setId(Long data) {
		this.id = data;
	}
	
	public void setName(String data) {
		this.name = data;
	}
	
	public void setIp(String data) {
		this.ip = data;
	}
	
	public void setPort(Integer data) {
		this.port = data;
	}
}

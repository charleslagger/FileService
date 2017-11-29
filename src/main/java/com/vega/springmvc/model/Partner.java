package com.vega.springmvc.model;

import java.io.Serializable;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * @author khoenv
 *
 */

@Document(collection = "Partners")
public class Partner implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3925384365267765732L;
	private String name;
	private String ip;
	private String authenKey;
	private short status;

	@Field("Name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Field("IP")
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	@Field("AuthKey")
	public String getAuthenKey() {
		return authenKey;
	}

	public void setAuthenKey(String authenKey) {
		this.authenKey = authenKey;
	}

	@Field("Status")
	public short getStatus() {
		return status;
	}

	public void setStatus(short status) {
		this.status = status;
	}
}

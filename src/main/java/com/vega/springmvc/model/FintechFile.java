package com.vega.springmvc.model;

import java.io.Serializable;

import javax.persistence.Id;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * @author khoenv
 *
 */
@Document(collection = "FintechFiles")
public class FintechFile implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7997758656741332017L;
	private ObjectId id;
	private String fileName;
	private String path;
	private String dateCreated;
	private String content;
	private Long orderId;
	private String partnerId;
	
	@Id
	@Field("_id")
	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	@Field("FileName")
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Field("Path")
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Field("DateCreated")
	public String getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(String dateCreated) {
		this.dateCreated = dateCreated;
	}

	@Field("Content")
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Field("OrderId")
	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	@Field("PartnerId")
	public String getPartnerId() {
		return partnerId;
	}

	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}
}

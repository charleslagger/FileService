package com.vega.springmvc.model;

public class LoanDocument {
	private Long orderId;
	private String documentTypeName;
	private String originalFileId;
	
	public Long getOrderId() {
		return orderId;
	}
	
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public String getDocumentTypeName() {
		return documentTypeName;
	}

	public void setDocumentTypeName(String documentTypeName) {
		this.documentTypeName = documentTypeName;
	}

	public String getOriginalFileId() {
		return originalFileId;
	}

	public void setOriginalFileId(String originalFileId) {
		this.originalFileId = originalFileId;
	}

	
}

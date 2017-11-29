package com.vega.springmvc.model;

public class UrlParam {
	private int amountFile;
	// List param in url
	private String fName;
	private String partnerId;
	private Long orderId;
	private String ackUrl;
	private String mKey;
	private Long opt;// field to determine request from core is loan request(0) or contract(2) or
						// disbursement(1)

	public String getfName() {
		return fName;
	}

	public void setfName(String fName) {
		this.fName = fName;
	}

	public String getPartnerId() {
		return partnerId;
	}

	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public String getAckUrl() {
		return ackUrl;
	}

	public void setAckUrl(String ackUrl) {
		this.ackUrl = ackUrl;
	}

	public String getmKey() {
		return mKey;
	}

	public void setmKey(String mKey) {
		this.mKey = mKey;
	}

	public Long getOpt() {
		return opt;
	}

	public void setOpt(Long opt) {
		this.opt = opt;
	}

	public int getAmountFile() {
		return amountFile;
	}

	public void setAmountFile(int amountFile) {
		this.amountFile = amountFile;
	}

}

/**
 * Created CoreResponse.java at 10:45:56 AM by hungvq
 * TODO
 */
package com.vega.springmvc.service.transform;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import com.vega.springmvc.service.page.DataPage;
/**
 * @author hungvq
 *
 */
@SuppressWarnings("serial")
public class CoreResponse implements Serializable{
	private boolean isSuccess;
	private Object result;
	private String[] messageParams;
	private String messageCode;
	private Integer responseCode;
		
	public CoreResponse() {
	}

	public CoreResponse(Collection<?> result) {
		this.result = result;
		if (result != null) {
			this.isSuccess = true;
			this.responseCode = 0;
		}
	}

	public CoreResponse(Map<?, ?> result) {
		this.result = result;
		if (result != null) {
			this.isSuccess = true;
			this.responseCode = 0;
		}
	}

	public CoreResponse(DataPage<?> result) {
		this.result = result;
		if (result != null) {
			this.isSuccess = true;
			this.responseCode = 0;
		}
	}

	public CoreResponse(Serializable object) {
		if (object instanceof String) {
			this.isSuccess = false;
			this.messageCode = object.toString();
		} else {
			if (object != null) {
				this.result = object;
				this.isSuccess = true;
				this.responseCode = 0;
			} else {
				this.isSuccess = false;
			}
		}
	}

	public CoreResponse(Integer responseCode, boolean isSuccess, String messageCode, String... strings) {
		this.responseCode = responseCode;
		this.isSuccess = isSuccess;
		this.messageCode = messageCode;
		this.messageParams = strings;
	}

	/**
	 * 
	 * @param success
	 * @param messageCode
	 * @param strings
	 */
	public CoreResponse(boolean success, String messageCode, String... strings) {
		this.isSuccess = success;
		this.messageCode = messageCode;
		this.messageParams = strings;
	}

	public CoreResponse(String message) {
		this.isSuccess = false;
		this.messageCode = message;
	}
	public CoreResponse(String message,boolean success){
		this.messageCode=message;
		this.isSuccess=success;
	}

	public CoreResponse(Integer responseCode, boolean isSuccess, String messageCode) {
		this.responseCode = responseCode;
		this.isSuccess = isSuccess;
		this.messageCode = messageCode;
	}

	public CoreResponse(boolean isSuccess, Object result) {
		super();
		this.isSuccess = isSuccess;
		this.result = result;
	}
 
	public CoreResponse(boolean isSuccess, String messageCode) {
		super();
		this.isSuccess = isSuccess;
		this.messageCode = messageCode;
	}

	/**
	 * @return the isSuccess
	 */
	public boolean isSuccess() {
		return isSuccess;
	}

	/**
	 * @param isSuccess the isSuccess to set
	 */
	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	/**
	 * @return the result
	 */
	public Object getResult() {
		return result;
	}

	/**
	 * @param result the result to set
	 */
	public void setResult(Object result) {
		this.result = result;
	}

	/**
	 * @return the messageParams
	 */
	public String[] getMessageParams() {
		return messageParams;
	}

	/**
	 * @param messageParams the messageParams to set
	 */
	public void setMessageParams(String[] messageParams) {
		this.messageParams = messageParams;
	}

	/**
	 * @return the messageCode
	 */
	public String getMessageCode() {
		return messageCode;
	}

	/**
	 * @param messageCode the messageCode to set
	 */
	public void setMessageCode(String messageCode) {
		this.messageCode = messageCode;
	}

	/**
	 * @return the responseCode
	 */
	public Integer getResponseCode() {
		return responseCode;
	}

	/**
	 * @param responseCode the responseCode to set
	 */
	public void setResponseCode(Integer responseCode) {
		this.responseCode = responseCode;
	}

	@Override
	public String toString() {
		return "InvokeResponse [isSuccess=" + isSuccess + ", result=" + result + ", messageParams="
				+ Arrays.toString(messageParams) + ", messageCode=" + messageCode + ", responseCode=" + responseCode
				+ "]";
	}

}

/**
 * Created DataPage.java at 2:21:35 PM by hungvq
 * TODO
 */
package com.vega.springmvc.service.page;

import java.util.List;

@SuppressWarnings("serial")
public class DataPage<T> extends Pageable{
	private List<T> data;

	public List<T> getData() {
		return data;
	}

	public void setData(List<T> data) {
		this.data = data;
	}
	
}

/**
 * Created Pageable.java at 2:20:36 PM by hungvq
 * TODO
 */
package com.vega.springmvc.service.page;

import java.io.Serializable;
import java.util.List;

import org.hibernate.criterion.Order;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@SuppressWarnings("serial")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Pageable implements Serializable {
	private int pageIndex;
	private int pageSize = 10;
	private int totalItems;
	private List<Order> orders;
	private boolean withPage = true;
	private Double sumAmount;

	/**
	 *  Map <Field, RestrictionType>
	 */

	public Pageable() {
	}

	public int getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public List<Order> getOrders() {
		return this.orders;
	}

	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}

	public boolean isWithPage() {
		return withPage;
	}

	public void setWithPage(boolean withPage) {
		this.withPage = withPage;
	}

	public int getTotalItems() {
		return totalItems;
	}

	public void setTotalItems(int totalItems) {
		this.totalItems = totalItems;
	}

	public int gTotalPage() {
		return (int) Math.ceil(((double) totalItems / (double) pageSize));
	}

	public boolean hasNext() {
		return pageIndex < gTotalPage() - 1;
	}

	public boolean hasPrevious() {
		return 0 < pageIndex;
	}

	public Double getSumAmount() {
		return sumAmount;
	}

	public void setSumAmount(Double sumAmount) {
		this.sumAmount = sumAmount;
	}

}

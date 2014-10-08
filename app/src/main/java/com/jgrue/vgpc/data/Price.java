package com.jgrue.vgpc.data;

import java.util.Date;

public class Price {
	private Date priceDate;
	private float price;
	
	public void setPriceDate(Date priceDate) {
		this.priceDate = priceDate;
	}
	public Date getPriceDate() {
		return priceDate;
	}
	public void setPrice(float price) {
		this.price = price;
	}
	public float getPrice() {
		return price;
	}
}

package com.jgrue.vgpc.data;

import org.jsoup.nodes.Document;

public class FullGame extends Game {
	private float completePrice;
	private String volume;
	private String completeVolume;
	private String newVolume;
	private String lastObservation;
	private String imageUrl;
	private Document document;
	
	public float getCompletePrice() {
		return completePrice;
	}
	public void setCompletePrice(float completePrice) {
		this.completePrice = completePrice;
	}
	public void setUsedVolume(String volume) {
		this.volume = volume;
	}
	public String getUsedVolume() {
		return volume;
	}
	public String getCompleteVolume() {
		return completeVolume;
	}
	public void setCompleteVolume(String completeVolume) {
		this.completeVolume = completeVolume;
	}
	public String getNewVolume() {
		return newVolume;
	}
	public void setNewVolume(String newVolume) {
		this.newVolume = newVolume;
	}
	public void setLastObservation(String lastObservation) {
		this.lastObservation = lastObservation;
	}
	public String getLastObservation() {
		return lastObservation;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
		if (this.imageUrl.startsWith("/")) {
			this.imageUrl = "http://videogames.pricecharting.com" + this.imageUrl;
		}
	}
	public Document getDocument() {
		return document;
	}
	public void setDocument(Document document) {
		this.document = document;
	}
}

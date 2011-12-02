package com.jgrue.vgpc.data;

public class FullGame extends Game {
	private float newPrice;
	private String volume;
	private String lastObservation;
	
	public void setNewPrice(float newPrice) {
		this.newPrice = newPrice;
	}
	public float getNewPrice() {
		return newPrice;
	}
	public void setVolume(String volume) {
		this.volume = volume;
	}
	public String getVolume() {
		return volume;
	}
	public void setLastObservation(String lastObservation) {
		this.lastObservation = lastObservation;
	}
	public String getLastObservation() {
		return lastObservation;
	}
}

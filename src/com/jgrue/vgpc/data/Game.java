package com.jgrue.vgpc.data;

public class Game {
	private String gameName;
	private String gameAlias;
	private String consoleName;
	private String consoleAlias;
	private String genre;
	private float usedPrice;
	
	public void setGameName(String gameName) {
		this.gameName = gameName;
	}
	public String getGameName() {
		return gameName;
	}
	public void setGameAlias(String gameAlias) {
		this.gameAlias = gameAlias;
	}
	public String getGameAlias() {
		return gameAlias;
	}
	public void setConsoleName(String consoleName) {
		this.consoleName = consoleName;
	}
	public String getConsoleName() {
		return consoleName;
	}
	public void setConsoleAlias(String consoleAlias) {
		this.consoleAlias = consoleAlias;
	}
	public String getConsoleAlias() {
		return consoleAlias;
	}
	public void setGenre(String genre) {
		this.genre = genre;
	}
	public String getGenre() {
		return genre;
	}
	public void setUsedPrice(float usedPrice) {
		this.usedPrice = usedPrice;
	}
	public float getUsedPrice() {
		return usedPrice;
	}
}

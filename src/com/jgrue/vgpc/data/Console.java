package com.jgrue.vgpc.data;

public class Console {
	private int id;
	private int ordinal;
	private String name;
	private String alias;
	
	public Console(String name, String alias, int ordinal) {
		this.name = name;
		this.alias = alias;
		this.ordinal = ordinal;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getOrdinal() {
		return ordinal;
	}
	public void setOrdinal(int ordinal) {
		this.ordinal = ordinal;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
}

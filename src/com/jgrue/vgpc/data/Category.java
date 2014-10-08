package com.jgrue.vgpc.data;

import java.util.ArrayList;
import java.util.List;

public class Category {
	private int id;
	private int ordinal;
	private String name;
	private List<Console> consoles;
	
	public Category(String name, int ordinal)
	{
		this.name = name;
		this.ordinal = ordinal;
		this.consoles = new ArrayList<Console>();
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
	public List<Console> getConsoles() {
		return consoles;
	}
	public void setConsoles(List<Console> consoles) {
		this.consoles = consoles;
	}
}

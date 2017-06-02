package model;

import java.io.Serializable;

public class Tag implements Serializable {
	private static final long serialVersionUID = 89472823;
	private String type;
	private String value;
	
	public Tag(String type, String value) {
		this.type = type;
		this.value = value;
	}
	
	public String getType() { return type; }
	
	public String getValue() { return value; }
}

package comp1206.sushi.common;

import java.io.Serializable;

public class Restaurant implements Serializable{

	private static final long serialVersionUID = -1138699077322398775L;
	private String name;
	private Postcode location;

	public Restaurant(String name, Postcode location) {
		this.name = name;
		this.location = location;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Postcode getLocation() {
		return location;
	}

	public void setLocation(Postcode location) {
		this.location = location;
	}
	
}

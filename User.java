package comp1206.sushi.common;

import java.io.Serializable;

public class User extends Model implements Serializable{
	
	private String name;
	private String password;
	private String address;
	private Postcode postcode;

	public User(String username, String password, String address, Postcode postcode) {
		this.name = username;
		this.password = password;
		this.address = address;
		this.postcode = postcode;
	}

	public String getPassword() {
		return password;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Number getDistance() {
		return postcode.getDistance();
	}

	public Postcode getPostcode() {
		return this.postcode;
	}
	
	public void setPostcode(Postcode postcode) {
		this.postcode = postcode;
	}
	
	public String getAddress() {
		return address;
	}

}

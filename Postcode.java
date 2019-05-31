package comp1206.sushi.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Postcode extends Model implements Serializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3654857277777407774L;
	private String name;
	private Map<String,Double> latLong;
	private Number distance = 0;

	public Postcode(String code){
		this.name = code;
		try {
			calculateLatLong(this.name);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Postcode(String code, Restaurant restaurant){
		this.name = code;
		calculateLatLong(this.name);
		distance(restaurant);
	}

	@Override
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Number getDistance() {

		return this.distance;
	}

	public Map<String,Double> getLatLong() {
		try {
			calculateLatLong(this.name);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return this.latLong;
	}

	protected void calculateDistance(Restaurant restaurant) {
		Postcode destination = restaurant.getLocation();
	}

	// streams the website that has an api given by the university of southampton
	// gets the latitude and longitude from the content of the page 
	// adds them into the map
	private ArrayList<String> calculateLatLong(String code) {
		ArrayList<String> array = new ArrayList<>();
		try {
			
			array.clear();
			URL url;
			url = new URL("https://www.southampton.ac.uk/~ob1a12/postcode/postcode.php?postcode="+ code.toUpperCase().replaceAll("\\s+","") +"/");
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			String inputLine, lat = null , longt = null;
			while ((inputLine = in.readLine()) != null) {
				lat = inputLine.substring(inputLine.indexOf("lat")+6, inputLine.lastIndexOf(",")-1);
				longt = inputLine.substring(inputLine.indexOf("long")+7, inputLine.lastIndexOf("}")-2);
			}
			in.close(); 
			this.latLong = new HashMap<String,Double>();
			latLong.put("lat", Double.parseDouble(lat));
			latLong.put("lon", Double.parseDouble(longt));
			array.add(lat);
			array.add(longt); 
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return array;
	}

	// calculates the distance between the restaurant and the given postcode
	// uses ‘haversine’ formula from https://www.movable-type.co.uk/scripts/latlong.html
	private void distance(Restaurant restaurant) {
		try {
		final double RADIUS = 6371e3;
		double latA = Double.valueOf(calculateLatLong(this.name).get(0));
		double latB = Double.valueOf(calculateLatLong(restaurant.getLocation().toString()).get(0));
		double latDistance = Math.toRadians(latB - latA);
		double longDistance = Math.toRadians(Double.valueOf(calculateLatLong(this.name).get(1))-Double.valueOf(calculateLatLong(restaurant.getLocation().toString()).get(1)));

		double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
				+ Math.cos(Math.toRadians(latA)) * Math.cos(Math.toRadians(latB))
				* Math.sin(longDistance / 2) * Math.sin(longDistance / 2);

		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		BigDecimal bd = new BigDecimal(RADIUS * c).setScale(2, RoundingMode.HALF_UP);
		this.distance =  bd.doubleValue();
		}catch(Exception e) {}

	}
}

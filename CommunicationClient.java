package comp1206.sushi.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import comp1206.sushi.common.Dish;
import comp1206.sushi.common.Order;
import comp1206.sushi.common.Postcode;
import comp1206.sushi.common.Restaurant;
import comp1206.sushi.common.User;

public class CommunicationClient {
	
	Socket socket ;
	private ArrayList<Postcode> postcodes = new ArrayList<Postcode>();
	private CopyOnWriteArrayList<Dish> dishes = new CopyOnWriteArrayList<Dish>();
	private CopyOnWriteArrayList<Order> orders = new CopyOnWriteArrayList<Order>();
	private Restaurant restaurant;
	private ArrayList<User> users = new ArrayList<User>();
	
	public void receive(String string) {
		try {
			socket = new Socket("localhost", 9000);

			ObjectOutputStream osw = new ObjectOutputStream(socket.getOutputStream());
			osw.writeObject(string);
			
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			ArrayList<Object> ob = (ArrayList<Object>) ois.readObject();
		
			if(string.equals("all")) {
				users = ((ArrayList<User>) ob.get(0));
				dishes = ((CopyOnWriteArrayList<Dish>) ob.get(1));
				orders = ((CopyOnWriteArrayList<Order>) ob.get(2));
				postcodes = ((ArrayList<Postcode>) ob.get(3));
				restaurant = ((Restaurant) ob.get(4));
			}else if(string.equals("dishes")) {
				dishes = ((CopyOnWriteArrayList<Dish>) ob.get(0));
			}else if(string.equals("orders")) {
				orders = ((CopyOnWriteArrayList<Order>) ob.get(0));
			}
			
			ois.close();
			osw.close();
			socket.close();
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}


	public ArrayList<Postcode> getPostcodes() {
		return postcodes;
	}


	public CopyOnWriteArrayList<Dish> getDishes() {
		return dishes;
	}


	public CopyOnWriteArrayList<Order> getOrders() {
		return orders;
	}


	public Restaurant getRestaurant() {
		return restaurant;
	}


	public ArrayList<User> getUsers() {
		return users;
	}
	
}

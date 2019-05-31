package comp1206.sushi.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import comp1206.sushi.common.Dish;
import comp1206.sushi.common.Order;
import comp1206.sushi.common.Postcode;
import comp1206.sushi.common.Restaurant;
import comp1206.sushi.common.UpdateEvent;
import comp1206.sushi.common.UpdateListener;
import comp1206.sushi.common.User;
import comp1206.sushi.server.CommunicationClient;

public class Client implements ClientInterface {

	private static final Logger logger = LogManager.getLogger("Client");

	Map<User, Map<Dish, Number>> basket = new HashMap<>();
	Map<Dish, Number> temp = new HashMap<>();

	private ArrayList<UpdateListener> listeners = new ArrayList<UpdateListener>();
	String upload;
	CommunicationClient communicationClient;
	private Restaurant restaurant;
	private Postcode restaurantPostcode;
	private CopyOnWriteArrayList<Dish> dishes = new CopyOnWriteArrayList<Dish>();
	private CopyOnWriteArrayList<Order> orders = new CopyOnWriteArrayList<Order>();
	private ArrayList<User> users = new ArrayList<User>();
	private ArrayList<Postcode> postcodes = new ArrayList<Postcode>();

	public Client() {
		logger.info("Starting up client...");
		this.communicationClient = new CommunicationClient();
		communicationClient.receive("all");
		postcodes = communicationClient.getPostcodes();
		users = communicationClient.getUsers();
		orders = communicationClient.getOrders();
		restaurant = communicationClient.getRestaurant();
		dishes = communicationClient.getDishes();

	}

	public void setRestaurant(Restaurant restaurant) {
		this.restaurant = restaurant;
	}

	public void setDishes(CopyOnWriteArrayList<Dish> dishes) {
		this.dishes = dishes;
	}

	public void setOrders(CopyOnWriteArrayList<Order> orders) {
		this.orders = orders;
	}

	public void setUsers(ArrayList<User> users) {
		this.users = users;
	}

	public void setPostcodes(ArrayList<Postcode> postcodes) {
		this.postcodes = postcodes;
	}

	public List<Order> getOrders() {
		communicationClient.receive("orders");
		orders = communicationClient.getOrders();
		return orders;
	}

	@Override
	public Restaurant getRestaurant() {
		return restaurant;
	}

	@Override
	public String getRestaurantName() {
		return restaurant.getName();
	}

	@Override
	public Postcode getRestaurantPostcode() {
		return restaurantPostcode;
	}

	@Override
	public User register(String username, String password, String address, Postcode postcode) {
		for (User cUser : getUsers()) {
			if (cUser.getName().toLowerCase().toString().equals(username.toLowerCase()))
				return null;
		}
		User rUser = new User(username, password, address, postcode);
		users.add(rUser);

		upload = "USER:" + username + ":" + password + ":" + address + ":" + postcode;

		communicationClient.receive(upload);

		return rUser;
	}

	@Override
	public User login(String username, String password) {
		boolean flagUsername = false;
		boolean flagPassword = false;
		for (User cUser : getUsers()) {
			if (cUser.getName().toString().toLowerCase().equals(username.toLowerCase()))
				flagUsername = true;
			if (cUser.getPassword().toString().toLowerCase().equals(password.toLowerCase()))
				flagPassword = true;
			if (flagUsername == true && flagPassword == true)
				return cUser;
		}

		return null;
	}

	@Override
	public List<Postcode> getPostcodes() {
		return postcodes;
	}

	@Override
	public List<Dish> getDishes() {
		communicationClient.receive("dishes");

		dishes = communicationClient.getDishes();


		return dishes;
	}

	@Override
	public String getDishDescription(Dish dish) {
		return dish.getDescription();
	}

	@Override
	public Number getDishPrice(Dish dish) {
		return dish.getPrice();
	}

	@Override
	public Map<Dish, Number> getBasket(User user) {
		Map<Dish, Number> currentBasket = new HashMap<Dish, Number>();
		for (Entry<User, Map<Dish, Number>> basketItem : basket.entrySet()) {
			for (Entry<Dish, Number> basketItem2 : basketItem.getValue().entrySet()) {
				currentBasket.put(basketItem2.getKey(), basketItem2.getValue());
			}
		}

		return currentBasket;
	}

	@Override
	public Number getBasketCost(User user) {
		double cost = 0;
		for (Entry<User, Map<Dish, Number>> basketItem : basket.entrySet())
			for (Entry<Dish, Number> basketItem2 : basketItem.getValue().entrySet())
				for (Dish dish : getDishes())
					if (dish.equals(basketItem2.getKey()))
						cost += dish.getPrice().doubleValue() * basketItem2.getValue().doubleValue();
		return cost;
	}

	@Override
	public void addDishToBasket(User user, Dish dish, Number quantity) {
		temp.put(dish, quantity);
		basket.put(user, temp);
	}

	@Override
	public void updateDishInBasket(User user, Dish dish, Number quantity) {
		temp.put(dish, quantity);
		basket.put(user, temp);
	}

	@Override
	public Order checkoutBasket(User user) {
		Order nOrder = new Order(user, getBasket(user));
		orders.add(nOrder);
		nOrder.setStatus("Waiting");

		String basket = "";
		for (Entry<Dish, Number> cbasket : getBasket(user).entrySet())
			basket += (int) cbasket.getValue().doubleValue() + " " + "* " + cbasket.getKey().getName().toString() + ",";

		upload = "ORDER:" + user.getName() + ":" + basket;

		communicationClient.receive(upload);
		clearBasket(user);

		return nOrder;
	}

	public ArrayList<User> getUsers() {
		return users;
	}

	@Override
	public void clearBasket(User user) {
		temp.clear();
	}

	@Override
	public List<Order> getOrders(User user) {
		List<Order> allOrders = new ArrayList<Order>();

		communicationClient.receive("orders");

		for (Order cOrder : getOrders()) {
			if (cOrder.getUser().toString().toLowerCase().equals(user.getName().toLowerCase().toString()))
				allOrders.add(cOrder);
		}

		return allOrders;
	}

	@Override
	public boolean isOrderComplete(Order order) {
		if (getOrderStatus(order).equals("Complete"))
			return true;
		else
			return false;
	}

	@Override
	public String getOrderStatus(Order order) {
		for (Order cOrder : getOrders())
			if (order.getName().equals(cOrder.getName()))
				return order.getStatus();
		return null;
	}

	@Override
	public Number getOrderCost(Order order) {
		double cost = 0;
		for (Entry<Dish, Number> orderItem : order.getMap().entrySet())
			cost += orderItem.getKey().getPrice().doubleValue() * orderItem.getValue().doubleValue();
		return cost;
	}

	@Override
	public void cancelOrder(Order order) {

		for(Order cOrder : this.orders) {
			if( order.getStatus().equals("Waiting") && cOrder.getName().toString().equals(order.getName().toString())) {
				communicationClient.receive("remove," + order.getName());
				this.notifyUpdate();
			}else {
				JOptionPane.showMessageDialog(new JFrame(), "Cannot Be deleted", "Dialog",JOptionPane.ERROR_MESSAGE);
			}
		}
		
		

	}

	@Override
	public void addUpdateListener(UpdateListener listener) {
		this.listeners.add(listener);
	}

	@Override
	public void notifyUpdate() {
		this.listeners.forEach(listener -> listener.updated(new UpdateEvent()));
	}

}

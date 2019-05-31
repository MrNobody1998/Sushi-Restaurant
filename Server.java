package comp1206.sushi.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import comp1206.sushi.common.Dish;
import comp1206.sushi.common.Drone;
import comp1206.sushi.common.Ingredient;
import comp1206.sushi.common.Order;
import comp1206.sushi.common.Postcode;
import comp1206.sushi.common.Restaurant;
import comp1206.sushi.common.Staff;
import comp1206.sushi.common.Supplier;
import comp1206.sushi.common.UpdateEvent;
import comp1206.sushi.common.UpdateListener;
import comp1206.sushi.common.User;

/**
 * @author Andreas
 *
 */
public class Server implements ServerInterface {

	private static final Logger logger = LogManager.getLogger("Server");

	private Restaurant restaurant;
	private CopyOnWriteArrayList<Dish> dishes = new CopyOnWriteArrayList<Dish>();
	private CopyOnWriteArrayList<Drone> drones = new CopyOnWriteArrayList<Drone>();
	private CopyOnWriteArrayList<Ingredient> ingredients = new CopyOnWriteArrayList<Ingredient>();
	private CopyOnWriteArrayList<Order> orders = new CopyOnWriteArrayList<Order>();
	private ArrayList<Staff> staff = new ArrayList<Staff>();
	private ArrayList<Supplier> suppliers = new ArrayList<Supplier>();
	private ArrayList<User> users = new ArrayList<User>();
	private ArrayList<Postcode> postcodes = new ArrayList<Postcode>();
	private ArrayList<UpdateListener> listeners = new ArrayList<UpdateListener>();
	StockControl stockcontrol = new StockControl();
	CommunicationServer communicationServer;
	DataPersistence dataPersistence;

	private boolean restockingIngredients = true;
	private boolean restockingDishes = true;

	public Server() {
		logger.info("Starting up server...");
		Configuration configuration = new Configuration();
		populateServer(configuration.getContent());
		this.communicationServer = new CommunicationServer(this);
		communicationServer.start();
		this.dataPersistence = new DataPersistence(this);
	}

	// this method loads the selected configuration file
	@Override
	public void loadConfiguration(String filename) {
		serverInitialise();
		System.out.println("Loaded configuration: " + filename);
		Configuration configuration = new Configuration(filename);
		populateServer(configuration.getContent());
	}

	// this method initializes the server
	private void serverInitialise() {
		dishes.clear();
		drones.clear();
		ingredients.clear();
		orders.clear();
		staff.clear();
		suppliers.clear();
		users.clear();
		postcodes.clear();
	}

	// this method populates the server with the content of the configuration file
	private void populateServer(ArrayList<String> content) {
		Postcode restaurantPostcode = null;

		for (String line : content) {
			if (line.startsWith("POSTCODE:")) {
				addPostcode(line.substring(line.indexOf(":") + 1));
			} 
			else if(line.startsWith("RESTAURANT:")){
				String[] cLine = line.split(":");
				restaurantPostcode  = new Postcode(cLine[2]);
				restaurant = new Restaurant(cLine[1], restaurantPostcode);
			}
			else if (line.startsWith("SUPPLIER:")) {
				for (Postcode cPostcode : getPostcodes()) {
					if (cPostcode.getName().toString().equals(line.substring(line.lastIndexOf(":") + 1))) {
						addSupplier(line.substring(line.indexOf(":") + 1, line.lastIndexOf(":")), cPostcode);
					}
				}
			} else if (line.startsWith("INGREDIENT:")) {
				for (Supplier cSupplier : getSuppliers()) {
					String[] cSupplierIngredient = line.split(":");
					if (cSupplier.getName().toString().equals(cSupplierIngredient[3])) {
						addIngredient(cSupplierIngredient[1], cSupplierIngredient[2], cSupplier,
								(Number) Integer.parseInt(cSupplierIngredient[4]),
								(Number) Integer.parseInt(cSupplierIngredient[5]),
								Integer.parseInt(cSupplierIngredient[6]));
					}
				}
			} else if (line.startsWith("DISH:")) {
				String[] cDish = line.split(":");
				addDish(cDish[1], cDish[2], Integer.parseInt(cDish[3]), Integer.parseInt(cDish[4]),
						Integer.parseInt(cDish[5]));
				String allIngredients = (line.substring(line.lastIndexOf(":") + 1));
				String[] ingredients = allIngredients.split(",");
				for (String cIngredient : ingredients) {
					for (Dish cuDish : getDishes()) {
						for (Ingredient cuIngredient : getIngredients()) {
							if (cuDish.getName().toString().equals(cDish[1]) && (cIngredient.substring(cIngredient.indexOf("*") + 2).equals(cuIngredient.getName()))) {
								addIngredientToDish(cuDish, cuIngredient,
										Integer.parseInt(cIngredient.substring(0, cIngredient.indexOf("*") - 1)));
							}
						}
					}
				}
			} else if (line.startsWith("USER:")) {
				String[] cUser = line.split(":");
				for (Postcode cPostcode : getPostcodes())
					if (cPostcode.getName().toString().equals(cUser[4])) {
						users.add(new User(cUser[1], cUser[2], cUser[3], cPostcode));
					}
			} else if (line.startsWith("ORDER:")) {
				String[] cLine = line.split(":");
				for (User cUser : getUsers()) {
					if (cUser.getName().toString().toLowerCase().equals(cLine[1].toLowerCase())) {
						Map<Dish, Number> basket = new HashMap<Dish, Number>();
						String allDishes = (line.substring(line.lastIndexOf(":") + 1));
						String[] dish = allDishes.split(",");
						for (Dish cDish : getDishes()) {
							for (String cuDish : dish) {
								if (cDish.getName().toString().toLowerCase()
										.equals(cuDish.substring(cuDish.indexOf("*") + 2).toLowerCase())) {
									basket.put(cDish, Integer.parseInt(cuDish.substring(0, cuDish.indexOf("*") - 1)));
								}
							}
						}
						Order order = new Order(cUser, basket);
						addOrder(order);
					}
				}
			}else if (line.startsWith("DRONE:")) {
				addDrone(Integer.parseInt(line.substring(line.indexOf(":") + 1)));	
			} else if (line.startsWith("STAFF:")) {
				addStaff(line.substring(line.indexOf(":") + 1));
			} else if (line.startsWith("STOCK:")) {
				String[] cLine = line.split(":");
				for (Dish cDish : getDishes()) {
					if (cDish.getName().toString().equals(cLine[1].toString())) {
						setStock(cDish, Integer.parseInt(cLine[2]));
					}
				}
				for (Ingredient cIngredient : getIngredients()) {
					if (cIngredient.getName().toString().equals(cLine[1].toString()))
						setStock(cIngredient, Integer.parseInt(cLine[2]));
				}
			}
		}		

		this.notifyUpdate();
	}

	@Override
	public synchronized List<Dish> getDishes() {
		return this.dishes;
	}

	@Override
	public Dish addDish(String name, String description, Number price, Number restockThreshold, Number restockAmount) {
		Dish newDish = new Dish(name, description, price, restockThreshold, restockAmount);
		this.dishes.add(newDish);
		setStock(newDish, 0);
		this.notifyUpdate();
		return newDish;
	}

	@Override
	public void removeDish(Dish dish) {
		this.dishes.remove(dish);
		this.notifyUpdate();
	}

	@Override
	public synchronized Map<Dish, Number> getDishStockLevels() {
		return stockcontrol.getStockDishMap();
	}

	@Override
	public synchronized Map<Ingredient, Number> getIngredientStockLevels() {
		return stockcontrol.getStockIngredientMap();
	}

	@Override
	public void setRestockingIngredientsEnabled(boolean enabled) {
		setRestockingIngredients(enabled);
	}

	@Override
	public void setRestockingDishesEnabled(boolean enabled) {
		setRestockingDishes(enabled);
	}

	@Override
	public void setStock(Dish dish, Number stock) {
		stockcontrol.getStockDishMap().put(dish, stock);
		this.notifyUpdate();
	}

	@Override
	public void setStock(Ingredient ingredient, Number stock) {
		stockcontrol.getStockIngredientMap().put(ingredient, stock);
		this.notifyUpdate();
	}

	@Override
	public List<Ingredient> getIngredients() {
		return this.ingredients;
	}

	@Override
	public Ingredient addIngredient(String name, String unit, Supplier supplier, Number restockThreshold,
			Number restockAmount, Number weight) {
		Ingredient mockIngredient = new Ingredient(name, unit, supplier, restockThreshold, restockAmount, weight);
		this.ingredients.add(mockIngredient);
		setStock(mockIngredient, 0);
		this.notifyUpdate();
		return mockIngredient;
	}

	@Override
	public void removeIngredient(Ingredient ingredient) {
		int index = this.ingredients.indexOf(ingredient);
		this.ingredients.remove(index);
		this.notifyUpdate();
	}

	@Override
	public List<Supplier> getSuppliers() {
		return this.suppliers;
	}

	@Override
	public Supplier addSupplier(String name, Postcode postcode) {
		Supplier mock = new Supplier(name, postcode);
		this.suppliers.add(mock);
		return mock;
	}

	@Override
	public void removeSupplier(Supplier supplier) {
		int index = this.suppliers.indexOf(supplier);
		this.suppliers.remove(index);
		this.notifyUpdate();
	}

	@Override
	public synchronized List<Drone> getDrones() {
		return this.drones;
	}

	@Override
	public Drone addDrone(Number speed) {
		Drone mock = new Drone(speed, this);
		this.drones.add(mock);
		this.notifyUpdate();
		return mock;
	}

	@Override
	public void removeDrone(Drone drone) {
		int index = this.drones.indexOf(drone);
		this.drones.remove(index);
		this.notifyUpdate();
	}

	@Override
	public List<Staff> getStaff() {
		return this.staff;
	}

	public void addOrder(Order order) {
		this.orders.add(order); 
		assigningStaff();
		this.notifyUpdate();
	}

	public void assigningStaff() {
	out:	for (Staff cStaff : getStaff()) {
			if (cStaff.getStatus().equals("Idle")) {
				Thread staff = new Thread(cStaff);
				staff.start();
				break out;
			}
		}
	}

	@Override
	public Staff addStaff(String name) {
		Staff mock = new Staff(name, this);
		this.staff.add(mock);
		this.notifyUpdate();
		return mock;
	}

	@Override
	public void removeStaff(Staff staff) {
		this.staff.remove(staff);
		this.notifyUpdate();
	}

	@Override
	public synchronized List<Order> getOrders() {
		return this.orders;
	}

	@Override
	public void removeOrder(Order order) {
		int index = this.orders.indexOf(order);
		this.orders.remove(index);
		this.notifyUpdate();
	}

	@Override
	public Number getOrderCost(Order order) {
		double cost = 0;
		for (Entry<Dish, Number> orderItem : order.getMap().entrySet())
			cost += orderItem.getKey().getPrice().doubleValue() * orderItem.getValue().doubleValue();
		if (cost == 0)
			removeOrder(order);
		return cost;
	}

	@Override
	public Number getSupplierDistance(Supplier supplier) {
		return supplier.getDistance();
	}

	@Override
	public Number getDroneSpeed(Drone drone) {
		return drone.getSpeed();
	}

	@Override
	public Number getOrderDistance(Order order) {
		return order.getDistance();
	}

	@Override
	public void addIngredientToDish(Dish dish, Ingredient ingredient, Number quantity) {
		if (quantity == Integer.valueOf(0)) {
			removeIngredientFromDish(dish, ingredient);
		} else {
			dish.getRecipe().put(ingredient, quantity);
		}
	}

	@Override
	public void removeIngredientFromDish(Dish dish, Ingredient ingredient) {
		dish.getRecipe().remove(ingredient);
		this.notifyUpdate();
	}

	@Override
	public Map<Ingredient, Number> getRecipe(Dish dish) {
		return dish.getRecipe();
	}

	@Override
	public List<Postcode> getPostcodes() {
		return this.postcodes;
	}

	@Override
	public Postcode addPostcode(String code) {
		Postcode mock = null;
		try {
			mock = new Postcode(code,this.restaurant);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.postcodes.add(mock);
		this.notifyUpdate();
		return mock;
	}

	@Override
	public void removePostcode(Postcode postcode) throws UnableToDeleteException {
		this.postcodes.remove(postcode);
		this.notifyUpdate();
	}

	@Override
	public List<User> getUsers() {
		return users;
	}

	@Override
	public void removeUser(User user) {
		this.users.remove(user);
		this.notifyUpdate();
	}

	@Override
	public void setRecipe(Dish dish, Map<Ingredient, Number> recipe) {
		for (Entry<Ingredient, Number> recipeItem : recipe.entrySet()) {
			addIngredientToDish(dish, recipeItem.getKey(), recipeItem.getValue());
		}
		this.notifyUpdate();
	}

	@Override
	public boolean isOrderComplete(Order order) {
		return true;
	}

	@Override
	public String getOrderStatus(Order order) {
		return order.getStatus();
	}

	@Override
	public String getDroneStatus(Drone drone) {
		Random rand = new Random();
		if (rand.nextBoolean()) {
			return "Idle";
		} else {
			return "Flying";
		}
	}

	@Override
	public String getStaffStatus(Staff staff) {
		return staff.getStatus();
	}

	@Override
	public void setRestockLevels(Dish dish, Number restockThreshold, Number restockAmount) {
		dish.setRestockThreshold(restockThreshold);
		dish.setRestockAmount(restockAmount);
		this.notifyUpdate();
	}

	@Override
	public void setRestockLevels(Ingredient ingredient, Number restockThreshold, Number restockAmount) {
		ingredient.setRestockThreshold(restockThreshold);
		ingredient.setRestockAmount(restockAmount);
		this.notifyUpdate();
	}

	@Override
	public Number getRestockThreshold(Dish dish) {
		return dish.getRestockThreshold();
	}

	@Override
	public Number getRestockAmount(Dish dish) {
		return dish.getRestockAmount();
	}

	@Override
	public Number getRestockThreshold(Ingredient ingredient) {
		return ingredient.getRestockThreshold();
	}

	@Override
	public Number getRestockAmount(Ingredient ingredient) {
		return ingredient.getRestockAmount();
	}

	@Override
	public void addUpdateListener(UpdateListener listener) {
		this.listeners.add(listener);
	}

	@Override
	public void notifyUpdate() {
		this.listeners.forEach(listener -> listener.updated(new UpdateEvent()));
	}

	@Override
	public Postcode getDroneSource(Drone drone) {
		return drone.getSource();
	}

	@Override
	public Postcode getDroneDestination(Drone drone) {
		return drone.getDestination();
	}

	@Override
	public Number getDroneProgress(Drone drone) {
		return drone.getProgress();
	}

	@Override
	public String getRestaurantName() {
		return restaurant.getName();
	}

	@Override
	public Postcode getRestaurantPostcode() {
		return restaurant.getLocation();
	}

	@Override
	public Restaurant getRestaurant() {
		return restaurant;
	}

	// this method is responsible for starting a drone thread
	// when a drone is idle
	public void assigningDrones() {
		for (Drone drone : getDrones()) {
			if (drone.getStatus().equals("Idle")) {
				Thread thread = new Thread(drone);
				thread.start();
			}
		}
	}

	public synchronized boolean isRestockingIngredients() {
		return restockingIngredients;
	}

	public synchronized void setRestockingIngredients(boolean restockingIngedients) {
		this.restockingIngredients = restockingIngedients;
	}

	public synchronized boolean isRestockingDishes() {
		return restockingDishes;
	}

	public synchronized void setRestockingDishes(boolean restockingDishes) {
		this.restockingDishes = restockingDishes;
	}

}

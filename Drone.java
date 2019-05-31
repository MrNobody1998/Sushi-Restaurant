package comp1206.sushi.common;

import java.io.Serializable;
import java.util.Map.Entry;

import comp1206.sushi.server.Server;

public class Drone extends Model implements Serializable, Runnable {

	private static final long serialVersionUID = 6202467790091719131L;
	private Number speed;
	private Number progress;

	private Number capacity;
	private Number battery;

	private String status;

	private Postcode source;
	private Postcode destination;

	private Server server;

	public Drone(Number speed,Server server) {
		this.setSpeed(speed);
		this.setCapacity(1);
		this.setBattery(100);
		this.status = "Idle";
		this.server = server;
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Thread task = new Thread(this);
		task.start();
	}

	public Number getSpeed() {
		return speed;
	}

	public Number getProgress() {
		return progress;
	}

	public void setProgress(Number progress) {
		this.progress = progress;
	}

	public void setSpeed(Number speed) {
		this.speed = speed;
	}

	@Override
	public String getName() {
		return "Drone (" + getSpeed() + " speed)";
	}

	public Postcode getSource() {
		return source;
	}

	public void setSource(Postcode source) {
		this.source = source;
	}

	public Postcode getDestination() {
		return destination;
	}

	public void setDestination(Postcode destination) {
		this.destination = destination;
	}

	public Number getCapacity() {
		return capacity;
	}

	public void setCapacity(Number capacity) {
		this.capacity = capacity;
	}

	public Number getBattery() {
		return battery;
	}

	public void setBattery(Number battery) {
		this.battery = battery;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		notifyUpdate("status", this.status, status);
		this.status = status;
	}

	@Override
	public void run() {	
		if(server.getDishStockLevels().isEmpty()) return;
		if(server.getIngredientStockLevels().isEmpty()) return;
		if(!(server.isRestockingIngredients())) return;
		if(server.getSuppliers().isEmpty()) return;
		if(server.getPostcodes().isEmpty()) return;
		if(server.getDrones().isEmpty()) return;
		if(server.getIngredients().isEmpty()) return;

		if(this.getStatus().equals("Idle")) {
			restock();
			deliverOrder();
		}
	}

	private void restock() {
		for(Ingredient ingredient : server.getIngredients()) {
			if((ingredient.isRestocking() == false) && (server.getIngredientStockLevels().get(ingredient).intValue() < ingredient.getRestockThreshold().intValue())) {
				ingredient.setRestocking(true);	
				server.notifyUpdate();

				int distance = ingredient.getSupplier().getDistance().intValue();
				int speed = this.getSpeed().intValue();
				int restockTime = distance/speed;

				setStatus("Going to " + ingredient.getSupplier().getName());
				setSource(server.getRestaurantPostcode());
				setDestination(ingredient.getSupplier().getPostcode());
				server.notifyUpdate();

				for(int i = 0 ; i <= restockTime ; i++){
					if((i >= (restockTime/10)) && (i <= (restockTime*99/100))){
						setProgress(((i*100)/restockTime));
						server.notifyUpdate();
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					else{
						setProgress((100));
						server.notifyUpdate();
					} 
				}

				setProgress(null);
				setStatus("Coming back");
				setSource(ingredient.getSupplier().getPostcode());
				setDestination(server.getRestaurantPostcode());
				server.notifyUpdate();

				for(int i=0;i<=restockTime;i++){
					if((i>=(restockTime/10)) && (i<=(restockTime*99/100))){
						setProgress(((i*100)/restockTime));
						server.notifyUpdate();
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					else{
						setProgress((100));
						server.notifyUpdate();
					} 
				}

				server.assigningStaff();
				setStatus("Idle");
				setProgress(null);
				setSource(null);
				setDestination(null);
				server.getIngredientStockLevels().put(ingredient, (server.getIngredientStockLevels().get(ingredient).intValue() + ingredient.getRestockAmount().intValue()));
				ingredient.setRestocking(false);
				server.notifyUpdate();
			}
		}
	}

	private void deliverOrder() {	
		out:
			for(Order order : server.getOrders()) {
				if(order.getStatus().equals("Waiting")) {
					for(Entry<Dish, Number> recipeItem : order.getMap().entrySet()) {
						if(server.getDishStockLevels().get(recipeItem.getKey()).intValue() < recipeItem.getValue().intValue()) {
							continue out;
						}
					}

					order.setStatus("Delivering");

					setSource(server.getRestaurantPostcode());
					setDestination(order.getUser().getPostcode());

					for(Entry<Dish, Number> recipeItem : order.getMap().entrySet()) {
						server.getDishStockLevels().put(recipeItem.getKey(), server.getDishStockLevels().get(recipeItem.getKey()).intValue() - recipeItem.getValue().intValue());
					}
					server.notifyUpdate();

					int deliveryTime =  (order.getDistance().intValue() / this.getSpeed().intValue());		

					setStatus("Delivering order");
					server.notifyUpdate();

					for(int i=0;i<=deliveryTime;i++){
						if((i>=(deliveryTime/10)) && (i<=(deliveryTime*99/100))){
							setProgress(((i*100)/deliveryTime));
							server.notifyUpdate();
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						else{
							setProgress((100));
							server.notifyUpdate();
						} 
					}

					order.setStatus("Completed");
					server.notifyUpdate();

					setStatus("Returning to restaurant");
					setSource(order.getUser().getPostcode());
					setDestination(server.getRestaurantPostcode());
					server.notifyUpdate();

					for(int i=0;i<=deliveryTime;i++){
						if((i>=(deliveryTime/10)) && (i<=(deliveryTime*99/100))){
							setProgress(((i*100)/deliveryTime));
							server.notifyUpdate();
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						else{
							setProgress((100));
							server.notifyUpdate();
						} 
					}				

					setProgress(null);
					setStatus("Idle");
					setDestination(null);
					setSource(null);
					server.notifyUpdate();
				}	
			}
	}
}

package comp1206.sushi.common;

import java.io.Serializable;
import java.util.Map.Entry;
import java.util.Random;

import comp1206.sushi.server.Server;

public class Staff extends Model implements Serializable, Runnable {

	private static final long serialVersionUID = 1L;
	private String name;
	private String status;
	private Number fatigue;
	private Server server;

	public Staff(String name, Server server) {
		this.setName(name);
		this.setFatigue(0);
		this.server = server;
		this.status = "Idle";
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Number getFatigue() {
		return fatigue;
	}

	public void setFatigue(Number fatigue) {
		this.fatigue = fatigue;
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
		check();
	}

	// this method checks if the server is empty
	// prevents nullpointerexception
	private void check() {
		if (server.getDishes().isEmpty())
			return;
		if (server.getDishStockLevels().isEmpty())
			return;
		if (server.getIngredientStockLevels().isEmpty())
			return;
		if (!(server.isRestockingDishes()))	return;
		
		makeDish();
	}

	private void makeDish() {
		out:for (Dish dish : server.getDishes()) {
			if (!(dish.isRestocking())) {
				if (server.getDishStockLevels().get(dish).intValue() < dish.getRestockThreshold().intValue()) {	
					for (int times = 1; times < dish.getRestockAmount().intValue(); times++) {

						// check if we have sufficient ingredients for making the dish
						for (Entry<Ingredient, Number> recipe : dish.getRecipe().entrySet()) {
							while (server.getIngredientStockLevels().get(recipe.getKey()).intValue() < (recipe.getValue().intValue())) {
								if ((server.getIngredientStockLevels().get(recipe.getKey()).intValue() - recipe.getValue().intValue()) < 0) {
									server.assigningDrones();
									continue out;
								}
							}
						}

						Random rand = new Random();
						int result = rand.nextInt(60000 - 20000) + 20000;
						this.setStatus("Preparing " + dish.getName());

						dish.setRestocking(true);

						try {
							Thread.sleep(result);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

						server.getDishStockLevels().put(dish,
								(int) (server.getDishStockLevels().get(dish).intValue() + 1)); // increases the stock of
																								// the dish by 1

						// decreasing the ingredient levels
						// calling the drones to check if the ingredient levels are now below the
						// threashold
						for (Entry<Ingredient, Number> recipe : dish.getRecipe().entrySet()) {
							Integer currentStock = (int) server.getIngredientStockLevels().get(recipe.getKey())
									.intValue();
							Integer dishDemands = (int) (recipe.getValue().doubleValue());
							Number newStock = currentStock - dishDemands;
							server.getIngredientStockLevels().put(recipe.getKey(), newStock);
							server.assigningDrones();
						}

						this.setStatus("Idle");
						dish.setRestocking(false);
					}
				}
			}
		}

	}
}

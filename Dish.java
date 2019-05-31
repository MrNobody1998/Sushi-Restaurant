package comp1206.sushi.common;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Dish extends Model implements Serializable {

	private String name;
	private String description;
	private Number price;
	private Map <Ingredient,Number> recipe;
	private Number restockThreshold;
	private Number restockAmount;
	private boolean restocking;

	public Dish(String name, String description, Number price, Number restockThreshold, Number restockAmount) {
		this.name = name;
		this.description = description;
		this.price = price;
		this.restockThreshold = restockThreshold;
		this.restockAmount = restockAmount;
		this.recipe = new HashMap<Ingredient,Number>();
		this.restocking = false;
	}

	public synchronized boolean isRestocking() {
		return restocking;
	}

	public synchronized void setRestocking(boolean restocking) {
		this.restocking = restocking;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Number getPrice() {
		return price;
	}

	public void setPrice(Number price) {
		this.price = price;
	}

	public Map <Ingredient,Number> getRecipe() {
		return recipe;
	}

	public void setRecipe(Map <Ingredient,Number> recipe) {
		this.recipe = recipe;
	}

	public void setRestockThreshold(Number restockThreshold) {
		this.restockThreshold = restockThreshold;
	}
	
	public void setRestockAmount(Number restockAmount) {
		this.restockAmount = restockAmount;
	}

	public Number getRestockThreshold() {
		return this.restockThreshold;
	}

	public Number getRestockAmount() {
		return this.restockAmount;
	}

}

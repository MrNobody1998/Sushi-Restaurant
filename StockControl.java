package comp1206.sushi.server;

import java.util.HashMap;
import java.util.Map;

import comp1206.sushi.common.Dish;
import comp1206.sushi.common.Ingredient;

/*
 * class that holds the stock levels of ingredients and dishes
 */

public class StockControl {
	Map<Ingredient, Number> stockIngredientMap = new HashMap<>();
	Map<Dish, Number> stockDishMap = new HashMap<>();

	public synchronized Map<Dish, Number> getStockDishMap() {
		return stockDishMap;
	}

	public synchronized Map<Ingredient, Number> getStockIngredientMap() {
		return stockIngredientMap;
	}


}

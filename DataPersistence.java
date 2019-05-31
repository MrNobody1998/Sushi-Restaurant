package comp1206.sushi.server;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map.Entry;

import comp1206.sushi.common.Dish;
import comp1206.sushi.common.Drone;
import comp1206.sushi.common.Ingredient;
import comp1206.sushi.common.Order;
import comp1206.sushi.common.Postcode;
import comp1206.sushi.common.Staff;
import comp1206.sushi.common.Supplier;
import comp1206.sushi.common.User;

public class DataPersistence implements Runnable {

	private Server server;

	public DataPersistence(Server server) {
		this.server = server;
		Thread thread = new Thread(this);
		thread.start();
	}

	@Override
	public void run() {		

		while(true) {
			try {
				Thread.sleep(10000);

				FileWriter writer = new FileWriter("data.txt", false);

				writer.write("POSTCODE:" + server.getRestaurantPostcode().toString());

				writer.write("\n");	
				writer.write("\n");	

				writer.write("RESTAURANT:" + server.getRestaurantName().toString()+":" + server.getRestaurantPostcode().toString());

				writer.write("\n");	

				for(Postcode postcodes : server.getPostcodes()) {
					if(postcodes.getName().equals(server.getRestaurantPostcode().toString())) {}
					else
						writer.write("POSTCODE:" + postcodes.getName().toString());
					writer.write("\n");	
				}

				writer.write("\n");	

				for(Supplier suppliers : server.getSuppliers()) {
					writer.write("SUPPLIER:" + suppliers.getName().toString() + ":" + suppliers.getPostcode().toString());
					writer.write("\n");	
				}

				writer.write("\n");	

				for(Ingredient ingredients : server.getIngredients()) {
					writer.write("INGREDIENT:" + ingredients.getName().toString() + ":" + ingredients.getUnit().toString()  + ":" 
							+ ingredients.getSupplier().toString()  + ":" + ingredients.getRestockThreshold().toString() + ":" + ingredients.getRestockAmount().toString() +
							":" + ingredients.getWeight().toString());
					writer.write("\n");	
				}

				writer.write("\n");				

				for (Dish dishes : server.getDishes()) {
					writer.write("DISH:"+dishes.getName()+":"+dishes.getDescription()+":"+dishes.getPrice()+":"+
							dishes.getRestockThreshold()+":"+dishes.getRestockAmount()+":");

					for(Entry<Ingredient,Number> recipe : dishes.getRecipe().entrySet()) {
						writer.write(recipe.getValue().intValue() + " * " + recipe.getKey().getName() + ",");
					}

					writer.write("\n");	
				}

				writer.write("\n");	

				for(User users: server.getUsers()) {
					writer.write("USER:" + users.getName() + ":" + users.getPassword() + ":" + users.getAddress() + ":" + users.getPostcode().getName());
					writer.write("\n");	
				}

				writer.write("\n");	

				for(Order orders : server.getOrders()) {
					writer.write("ORDER:" + orders.getUser() + ":" );

					for(Entry<Dish,Number> basket : orders.getMap().entrySet())
						writer.write(basket.getValue() + " * "  + basket.getKey().getName() + ",");

					writer.write("\n");	
				}

				writer.write("\n");	

				for(Entry<Dish,Number> dishes : server.getDishStockLevels().entrySet()) {
					if(dishes.getValue().intValue() != 0) {
						writer.write("STOCK:"  + dishes.getKey().getName() + ":" + dishes.getValue().intValue());
						writer.write("\n");	
					}
				}

				writer.write("\n");	

				for(Entry<Ingredient,Number> ingredients : server.getIngredientStockLevels().entrySet()) {
					if(ingredients.getValue().intValue() != 0) {
						writer.write("STOCK:"+ ingredients.getKey().getName() + ":" + ingredients.getValue());
						writer.write("\n");	
					}
				}

				writer.write("\n");	

				for(Staff staff : server.getStaff()) {
					writer.write("STAFF:" + staff.getName());
					writer.write("\n");	
				}

				writer.write("\n");	

				for(Drone drones : server.getDrones()) {
					writer.write("DRONE:" + drones.getSpeed().toString());
					writer.write("\n");	
				}

				writer.write("\n");	

				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}

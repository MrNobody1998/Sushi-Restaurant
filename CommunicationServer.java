package comp1206.sushi.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import comp1206.sushi.common.Dish;
import comp1206.sushi.common.Order;
import comp1206.sushi.common.Postcode;
import comp1206.sushi.common.User;

/**
 * @author Andreas
 *	this class is responsible for the communication between the client and the server
 */
public class CommunicationServer extends Thread {

	Server server;
	ServerSocket serverSocket;
	Socket socket;
	ObjectOutputStream out;
	ObjectInputStream a;

	public CommunicationServer(Server server){
		this.server = server;
	}

	@Override
	public void run() {
		try {
			serverSocket = new ServerSocket(9000);
			while (true) {		
				socket = serverSocket.accept();			
				sender(receiver());
				a.close();
				out.close();
				socket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sender(String receive) {
		try {	
			out = new ObjectOutputStream(socket.getOutputStream());
			ArrayList<Object> serialised = new ArrayList<>();
			if (receive.equals("all")) {
				serialised.add(server.getUsers());
				serialised.add(server.getDishes());
				serialised.add(server.getOrders());
				serialised.add(server.getPostcodes());
				serialised.add(server.getRestaurant());
			}else if(receive.equals("dishes")) {
				serialised.add(server.getDishes());
			}else if(receive.equals("orders")) {
				serialised.add(server.getOrders());
			}		
			out.writeObject(serialised);
		}catch(Exception e) {}


	}

	public String receiver() {
		try {

			a = new ObjectInputStream(socket.getInputStream());
			String answer = (String) a.readObject();

			if (answer.startsWith("USER:")) {
				String[] temp = answer.split(":");
				outside: for (Postcode cPostcode : server.getPostcodes()) {
					if (cPostcode.getName().toString().equals(temp[4])) {
						User user = new User(temp[1], temp[2], temp[3], cPostcode);
						server.getUsers().add(user);
						server.notifyUpdate();
						break outside;
					}
				}
			} else if (answer.startsWith("ORDER:")) {
				String[] cLine = answer.split(":");
				for (User cUser : server.getUsers()) {
					Map<Dish, Number> basket = new HashMap<Dish, Number>();
					if (cUser.getName().toString().toLowerCase().equals(cLine[1].toLowerCase())) {
						String allDishes = (answer.substring(answer.lastIndexOf(":") + 1));
						String[] dish = allDishes.split(",");
						for (Dish cDish : server.getDishes()) {
							for (String cuDish : dish) {
								if (cDish.getName().toString().toLowerCase()
										.equals(cuDish.substring(cuDish.indexOf("*") + 2).toLowerCase())) {
									basket.put(cDish, Integer.parseInt(cuDish.substring(0, cuDish.indexOf("*") - 1)));
								}
							}
						}
						Order order = new Order(cUser, basket);
						server.addOrder(order);
					}
				}
			} else if (answer.startsWith("remove")) {
				String[] cLine = answer.split(",");
				for(Order cOrder : server.getOrders()) {
					if(cLine[1].toString().equals(cOrder.getName())) {
						server.removeOrder(cOrder);
					}
				}
			}
			return answer;	
		}
		catch(Exception e) {}
		return null;
	}
}
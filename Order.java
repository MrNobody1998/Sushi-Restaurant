package comp1206.sushi.common;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class Order extends ModelSer implements Serializable {

	private static final long serialVersionUID = 8684072411778537687L;
	private String status;
	private User user;
	private Map<Dish, Number> map ;

	public Order(User user,Map<Dish, Number> map) {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/YYYY HH:mm:ss");  
		LocalDateTime now = LocalDateTime.now();  
		this.name = dtf.format(now);
		this.user = user;
		this.map = map;
		this.status = "Waiting";
	}
	
	public Map<Dish, Number> getMap() {
		return map;
	}

	public User getUser() {
		return user;
	}

	public Number getDistance() {
		return this.getUser().getPostcode().getDistance();
	}

	@Override
	public String getName() {
		return this.name;
	}

	public String getStatus() {
		return status;
	}

	public synchronized void setStatus(String status) {
		notifyUpdate("status",this.status,status);
		this.status = status;
	}


}

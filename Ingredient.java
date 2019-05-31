package comp1206.sushi.common;

import java.io.Serializable;

public class Ingredient extends Model implements Serializable{

	private static final long serialVersionUID = -5511693561298377420L;
	private String name;
	private String unit;
	private Supplier supplier;
	private Number restockThreshold;
	private Number restockAmount;
	private Number weight;
	private boolean restocking;

	public Ingredient(String name, String unit, Supplier supplier, Number restockThreshold,
			Number restockAmount, Number weight) {
		this.setName(name);
		this.setUnit(unit);
		this.setSupplier(supplier);
		this.setRestockThreshold(restockThreshold);
		this.setRestockAmount(restockAmount);
		this.setWeight(weight);
		this.restocking = false;
	}

	public synchronized boolean isRestocking() {
		return restocking;
	}

	public void setRestocking(boolean restocking) {
		this.restocking = restocking;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public Number getRestockThreshold() {
		return restockThreshold;
	}

	public void setRestockThreshold(Number restockThreshold) {
		this.restockThreshold = restockThreshold;
	}

	public Number getRestockAmount() {
		return restockAmount;
	}

	public void setRestockAmount(Number restockAmount) {
		this.restockAmount = restockAmount;
	}

	public Number getWeight() {
		return weight;
	}

	public void setWeight(Number weight) {
		this.weight = weight;
	}
}

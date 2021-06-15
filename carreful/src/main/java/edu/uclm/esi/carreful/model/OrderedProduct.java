package edu.uclm.esi.carreful.model;

public class OrderedProduct {
	private Product product;
	private double amount;
	
	public OrderedProduct(Product product, double amount) {
		this.product = product;
		this.amount = amount;
	}

	public void addAmount(double amount) {
		this.amount+=amount;
	}
	
	public void subAmount(double amount) {
		this.amount-=amount;
	}
	
	
	public double getAmount() {
		return amount;
	}
	
	public String getName() {
		return this.product.getNombre();
	}
	public double getPrecio() {
		return this.product.getPrecio();
	}

	
	public Product getProduct() {
		return this.product;
	}

}

package edu.uclm.esi.carreful.model;

import java.util.Collection;
import java.util.HashMap;

public class Carrito {
	private HashMap<String, OrderedProduct> products;
	private OrderedProduct orderedProduct;
	
	public Carrito() {
		this.products = new HashMap<>();
	}

	public void add(Product product, double amount) {
		OrderedProduct orderedProduct = this.products.get(product.getNombre());
		if (orderedProduct==null) {
			orderedProduct = new OrderedProduct(product, amount);
			this.products.put(product.getNombre(), orderedProduct);
		} else {
			orderedProduct.addAmount(amount);
		}
	}
	
	public void subtract(Product product, double amount) {
		OrderedProduct orderedProduct = this.products.get(product.getNombre());
		orderedProduct.subAmount(amount);
	}
	
	public void remove(Product product) {
		orderedProduct = this.products.get(product.getNombre());
		this.products.remove(orderedProduct.getName());
	}
	
	public double getAmount(Product product) {
		OrderedProduct orderedProduct = this.products.get(product.getNombre());
		return orderedProduct.getAmount();
	}

	public Collection<OrderedProduct> getProducts() {
		return products.values();
	}
}

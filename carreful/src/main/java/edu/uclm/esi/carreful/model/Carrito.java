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
		OrderedProduct ordered = this.products.get(product.getNombre());
		if (ordered==null) {
			ordered = new OrderedProduct(product, amount);
			this.products.put(product.getNombre(), ordered);
		} else {
			ordered.addAmount(amount);
			//hola
		}
	}
	
	public void subtract(Product product, double amount) {
		OrderedProduct ordered = this.products.get(product.getNombre());
		ordered.subAmount(amount);
	}
	
	public void remove(Product product) {
		OrderedProduct ordered = this.products.get(product.getNombre());
		this.products.remove(ordered.getName());
	}
	
	public double getAmount(Product product) {
		OrderedProduct ordered = this.products.get(product.getNombre());
		return ordered.getAmount();
	}

	public Collection<OrderedProduct> getProducts() {
		return products.values();
	}


}

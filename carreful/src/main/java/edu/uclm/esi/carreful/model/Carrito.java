package edu.uclm.esi.carreful.model;


import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;





public class Carrito implements Serializable{
	private static final long serialVersionUID = 1L;
	private HashMap<String, OrderedProduct> products;
	private Cupon cuponDescuento;

	
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
	
	public OrderedProduct getOrdered(String nombre) {
		return products.get(nombre);
		
	}

	public Cupon getCuponDescuento() {
		return cuponDescuento;
	}

	public void setCuponDescuento(Cupon cuponDescuento) {
		this.cuponDescuento = cuponDescuento;
	}



}

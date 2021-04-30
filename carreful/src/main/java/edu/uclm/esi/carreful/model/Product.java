package edu.uclm.esi.carreful.model;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Product {
	@Id @Column(length = 36)
	private String id;
	private String nombre;
	private double precio;
	private int cantidad;
	private boolean congelado;

	@ManyToOne
	private Categoria categoria;

	public Product() {
		this.id = UUID.randomUUID().toString();
		this.cantidad = 1;
	}

	public Product(String nombre, double precio) {
		this();
		this.nombre = nombre;
		this.precio = precio;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public double getPrecio() {
		return precio;
	}

	public void setPrecio(double precio) {
		this.precio = precio;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}
	public int getCantidad() {
		return cantidad;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

	public boolean getCongelado() {
		return congelado;
	}

	public void setCongelado(boolean congelado) {
		this.congelado = congelado;
	}
}

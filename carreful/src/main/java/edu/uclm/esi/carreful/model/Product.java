package edu.uclm.esi.carreful.model;

import java.util.Base64;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity

public class Product {
	@Id @Column(length=36)
	private String id;
	private String nombre;
	private String precio;
	@ManyToOne
	private Categoria categoria;

	
	public String getNombre() {
		return nombre;
	}
	public String getPrecio() {
		return precio;
	}
	public String getId() {
		return id;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public void setPrecio(String precio) {
		this.precio = precio;
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
	
}

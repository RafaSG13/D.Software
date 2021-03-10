package edu.uclm.esi.carreful.model;

import java.util.Base64;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity

public class Product {
	@Id
	private String nombre;
	private String precio;
	private String codigo;
	
	public String getNombre() {
		return nombre;
	}
	public String getPrecio() {
		return precio;
	}
	public String getCodigo() {
		return codigo;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public void setPrecio(String precio) {
		this.precio = precio;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
}

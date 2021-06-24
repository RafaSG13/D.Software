package edu.uclm.esi.carreful.model;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.ws.rs.DefaultValue;

@Entity
public class Categoria {
	@Id @Column(length = 36)
	private String id;
	private String nombre;
	@Column(nullable = false) @DefaultValue("0")
	private int numeroDeProductos;
	
	public Categoria() {
		this.id = UUID.randomUUID().toString();
	}
	
	public String getNombre() {
		return nombre;
	}
	
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getNumeroDeProductos() {
		return numeroDeProductos;
	}

	public void setNumeroDeProductos(int numeroDeProductos) {
		this.numeroDeProductos = numeroDeProductos;
	}

	
}

package edu.uclm.esi.carreful.model;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Categoria {
	@Id @Column (length= 36)
	private String id;
	private String nombre;
	private int numeroDeProductos;

	public Categoria() {
		this.id=UUID.randomUUID().toString();
		// TODO Auto-generated constructor stub
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public int getNumeroDeProductos() {
		return numeroDeProductos;
	}

	public void setNumeroDeProductos(int numeroDeProductos) {
		this.numeroDeProductos = numeroDeProductos;
	}

}

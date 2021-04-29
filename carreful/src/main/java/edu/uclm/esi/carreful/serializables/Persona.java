package edu.uclm.esi.carreful.serializables;

import java.io.Serializable;

public class Persona implements Serializable{	

	private static final long serialVersionUID = 1L;
	
	
	private String nif;
	private int edad;
	private Coche coche;
	

	Persona(String nif, int edad){
		this.nif=nif;
		this.edad=edad;
	}
	public void print() {
		System.out.println(nif+": "+edad+" años, tiene un coche: Marca = "+ coche.getMarca());
	}
	public Coche getCoche() {
		return coche;
	}
	public void setCoche(Coche coche) {
		this.coche = coche;
	}
}	

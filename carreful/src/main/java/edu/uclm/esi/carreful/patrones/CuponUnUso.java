package edu.uclm.esi.carreful.patrones;

import java.util.Date;

import javax.persistence.Entity;


import edu.uclm.esi.carreful.model.Cupon;

@Entity
public class CuponUnUso extends Cupon{
	private boolean usado;
	
	public CuponUnUso(Date fechaInicio, Date fechaFin, double descuento, String tipoDescuento, boolean usado) {
		super(fechaInicio,fechaFin,descuento,tipoDescuento);
		this.usado=usado;
		
	}
	
	public CuponUnUso() {
		super();
	}

	public boolean isUsado() {
		return usado;
	}

	public void setUsado(boolean usado) {
		this.usado = usado;
	}
	
	@Override
	public void usarCupon(String email) {
		usado=true;
	}


	
}

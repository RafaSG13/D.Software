package edu.uclm.esi.carreful.Patrones;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

import edu.uclm.esi.carreful.model.Cupon;

@Entity

public class CuponUnUso extends Cupon{
	private boolean usado;
	
	public CuponUnUso(String codigo, Date fechaInicio, Date fechaFin, double descuento, String tipoDescuento, boolean usado) {
		super(fechaInicio,fechaFin,descuento,tipoDescuento);
		this.usado=usado;
		
	}

	public boolean isUsado() {
		return usado;
	}

	public void setUsado(boolean usado) {
		this.usado = usado;
	}

	@Override
	public void usarCupon() {
		
		usado=true;
	}

	
}

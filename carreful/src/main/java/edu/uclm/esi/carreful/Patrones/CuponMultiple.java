package edu.uclm.esi.carreful.Patrones;

import java.util.Date;

import javax.persistence.Entity;

import edu.uclm.esi.carreful.model.Cupon;
@Entity
public class CuponMultiple extends Cupon{
	String usuario;
	int vecesUsado;
	
	public CuponMultiple(String codigo, Date fechaInicio, Date fechaFin, double descuento, String tipoDescuento, String usuario) {
		super(fechaInicio,fechaFin,descuento,tipoDescuento);
		this.usuario=usuario;
		
	}

	@Override
	public void usarCupon() {
		System.out.println("Estoy sinedo usado, Cupon Multiple");
		
	}
	
}

package edu.uclm.esi.carreful.Patrones;

import java.util.Date;
import edu.uclm.esi.carreful.model.Cupon;

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

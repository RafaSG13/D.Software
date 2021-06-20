package edu.uclm.esi.carreful.Patrones;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.Entity;

import edu.uclm.esi.carreful.dao.UserDao;
import edu.uclm.esi.carreful.model.Cupon;

@Entity
public class CuponUnUsuario extends Cupon{
	

	public CuponUnUsuario(String codigo, Date fechaInicio, Date fechaFin, double descuento, String tipoDescuento, String usuario) {
		super(fechaInicio,fechaFin,descuento,tipoDescuento);

	}
	
	public CuponUnUsuario() {
		super();
	}
	
	@Override
	public void usarCupon() {
		System.out.println("Estoy siendo usado, Cupon Un uso por Usuario");
		
	}

	@Override
	public void usarCupon(String email) {
		System.out.println("Estoy siendo usado, Cupon Un uso por Usuario");
		
	}


}



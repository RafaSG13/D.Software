package edu.uclm.esi.carreful.Patrones;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.Entity;

import edu.uclm.esi.carreful.dao.UserDao;
import edu.uclm.esi.carreful.model.Cupon;

@Entity
public class CuponUnUsuario extends Cupon{
	private String usuario;

	public CuponUnUsuario(String codigo, Date fechaInicio, Date fechaFin, double descuento, String tipoDescuento, String usuario) {
		super(fechaInicio,fechaFin,descuento,tipoDescuento);
		this.usuario= usuario;

	}
	
	public CuponUnUsuario() {
		super();
	}
	
	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	@Override
	public void usarCupon() {
		System.out.println("Estoy siendo usado, Cupon Un uso por Usuario");
		
	}

	@Override
	public void usarCupon(String email) {
		usuario= usuario+ ", "+ email;
		System.out.println("Estoy siendo usado, Cupon Un uso por Usuario");
		
	}

	public boolean contieneUsuario(String email) {
		ArrayList<String> usuarios = new ArrayList<String>(Arrays.asList(usuario.split(", ")));
		if (usuarios.contains(email)) return true;
		return false;
	}


}



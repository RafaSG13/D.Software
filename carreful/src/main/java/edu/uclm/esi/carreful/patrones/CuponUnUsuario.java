package edu.uclm.esi.carreful.patrones;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import javax.persistence.Entity;
import org.springframework.http.HttpStatus;
import edu.uclm.esi.carreful.exceptions.CarrefulException;
import edu.uclm.esi.carreful.model.Cupon;

@Entity
public class CuponUnUsuario extends Cupon{
	private String usuario;

	public CuponUnUsuario(Date fechaInicio, Date fechaFin, double descuento, String tipoDescuento, String usuario) {
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
	public void usarCupon(String email) {
		usuario= usuario+ ","+ email;
	}

	public boolean contieneUsuario(String email) throws CarrefulException {
		if (usuario== null) {
			usuario = "";
			return false;
		}
		ArrayList<String> usuarios = new ArrayList<>(Arrays.asList(usuario.split(",")));
		if (usuarios.contains(email)) throw new CarrefulException(HttpStatus.FORBIDDEN,"Esta intentando utilizar un cupon que ya esta registrado");
		return false;
	}


}



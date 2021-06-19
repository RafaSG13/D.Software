package edu.uclm.esi.carreful.Patrones;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import edu.uclm.esi.carreful.dao.CuponDao;
import edu.uclm.esi.carreful.dao.CuponUnUsuarioDao;
import edu.uclm.esi.carreful.dao.UserDao;
import edu.uclm.esi.carreful.exceptions.CarrefulException;
import edu.uclm.esi.carreful.model.Cupon;


public class CuponUnUsuario extends Cupon{
	
	String usuario;
	public CuponUnUsuario(String codigo, Date fechaInicio, Date fechaFin, double descuento, String tipoDescuento, String usuario) {
		super(fechaInicio,fechaFin,descuento,tipoDescuento);
		this.usuario=usuario;
		
	}

	@Override
	public void usarCupon(String correo) {
		try {
		if(correo==null) throw new CarrefulException(HttpStatus.NOT_ACCEPTABLE,"No se ha introducido ningun correo para registrar como contacto del cupon");
		ArrayList<String> usuarios=(ArrayList<String>) Arrays.asList(usuario.split(", "));
		if (usuarios.contains(correo)) throw new CarrefulException(HttpStatus.NOT_ACCEPTABLE,"Este usuario ya ha utilizado este cupon");;
		usuario = usuario +", " + correo;
		System.out.println("Estoy siendo usado, Cupon Un uso por Usuario por el usuario "+ correo);
		
		}catch(CarrefulException e) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
		}
		
		
		
		
		
		
		
		
	}


}



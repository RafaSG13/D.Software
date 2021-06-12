package edu.uclm.esi.carreful.Patrones;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import edu.uclm.esi.carreful.dao.UserDao;


public class CuponUnUsuario extends TipoCupon{
	private HashMap<String, Float> usuarios;

	public CuponUnUsuario() {
		usuarios= new HashMap<String, Float>();
		
		}
}



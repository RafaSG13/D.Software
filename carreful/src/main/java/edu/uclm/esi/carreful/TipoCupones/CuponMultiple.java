package edu.uclm.esi.carreful.TipoCupones;

import java.util.HashMap;

public class CuponMultiple extends TipoCupon{
	private int limiteUsos;
	private HashMap<String, Float> usuarios;
	
	public CuponMultiple(int usos) {
		setLimiteUsos(usos);
	}

	public int getLimiteUsos() {
		return limiteUsos;
	}

	public void setLimiteUsos(int limiteUsos) {
		this.limiteUsos = limiteUsos;
	}

	public HashMap<String, Float> getUsuarios() {
		return usuarios;
	}

	public void setUsuarios(HashMap<String, Float> usuarios) {
		this.usuarios = usuarios;
	}


	
}

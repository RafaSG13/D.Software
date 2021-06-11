package edu.uclm.esi.carreful.Patrones;

public class CuponUnUso extends TipoCupon{
	private boolean usado;
	
	public CuponUnUso() {
		usado=false;
	}

	public boolean isUsado() {
		return usado;
	}

	public void setUsado(boolean usado) {
		this.usado = usado;
	}

	
}

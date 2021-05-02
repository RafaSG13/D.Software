package edu.uclm.esi.carreful.auxiliares;

public abstract class TipoPedido {
	private String estado;
	
	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public TipoPedido() {}

	public abstract double AñadirGastosEnvio(double precioPedido);
	
}


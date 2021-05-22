package edu.uclm.esi.carreful.auxiliares;

public class Recoger extends TipoPedido{

	@Override
	public double AñadirGastosEnvio(double precioPedido) {
		return precioPedido;
	}

}

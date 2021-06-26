package edu.uclm.esi.carreful.patrones;

import java.util.Date;

import javax.persistence.Entity;

import edu.uclm.esi.carreful.model.Cupon;
@Entity
public class CuponMultiple extends Cupon{
	int vecesUsado;
	
	public CuponMultiple(Date fechaInicio, Date fechaFin, double descuento, String tipoDescuento) {
		super(fechaInicio,fechaFin,descuento,tipoDescuento);
		this.vecesUsado=0;
	}
	
	public CuponMultiple() {
		super();
	}
	
	@Override
	public void usarCupon(String email) {
			vecesUsado++;
	}

	public int getVecesUsado() {
		return vecesUsado;
	}

	public void setVecesUsado(int vecesUsado) {
		this.vecesUsado = vecesUsado;
	}
	
}

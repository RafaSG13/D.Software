package edu.uclm.esi.carreful.patrones;

import java.util.Date;

public class RangoDeFechas {
	private Date fechaInicio;
	private Date fechaFin;
	
	public RangoDeFechas(Date inicio, Date fin) {
		this.fechaInicio = inicio;
		this.fechaFin = fin;
		if(!rangoCorrecto()) {
			this.fechaInicio = fin;
			this.fechaFin = inicio;
		}
	}
	
	public boolean rangoCorrecto() {
		return fechaInicio.before(fechaFin);

	}
	public boolean comprobarValidez(Date fechaActual) {
		return !(fechaActual.after(fechaFin) || fechaActual.before(fechaInicio));

		
	}

	public Date getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public Date getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}
}

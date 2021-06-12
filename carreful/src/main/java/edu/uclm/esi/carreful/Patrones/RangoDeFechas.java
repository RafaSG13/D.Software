package edu.uclm.esi.carreful.Patrones;

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
		if(fechaInicio.after(fechaFin))
			return false;
		else
			return true;
	}
	public boolean comprobarValidez(Date fechaActual) {
		if(fechaActual.after(fechaFin) || fechaActual.before(fechaInicio))
			return false;
		else 
			return true;
		
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

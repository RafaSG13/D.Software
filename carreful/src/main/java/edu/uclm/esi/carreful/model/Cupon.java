package edu.uclm.esi.carreful.model;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Transient;
import edu.uclm.esi.carreful.Patrones.RangoDeFechas;


@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)

public abstract class Cupon {
	@Id @Column(length = 36)
	protected String codigo;
	
	protected Date fechaInicio;
	protected Date fechaFin;
	protected double descuento;
	protected String tipoDescuento;
	
	@Transient
	protected RangoDeFechas rango;
	
	protected Cupon() {
		super();
		this.codigo =  UUID.randomUUID().toString();
	}
	
	protected Cupon(Date fechaInicio, Date fechaFin, double descuento, String tipoDescuento) {
		this();
		this.descuento = descuento;
		this.tipoDescuento = tipoDescuento;
		this.rango = new RangoDeFechas(fechaInicio , fechaFin);
		this.fechaInicio = rango.getFechaInicio();
		this.fechaFin = rango.getFechaFin();
	}
	
	public abstract void usarCupon(String email);
	
	public String getCodigo() {
		return codigo;
	}
	public Date getFechaInicio() {
		return fechaInicio;
	}
	public Date getFechaFin() {
		return fechaFin;
	}
	public double getDescuento() {
		return descuento;
	}
	public String getTipoDescuento() {
		return tipoDescuento;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}
	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}
	public void setDescuento(double descuento) {
		this.descuento = descuento;
	}
	public void setTipoDescuento(String tipoDescuento) {
		this.tipoDescuento = tipoDescuento;
	}
	
	
	public RangoDeFechas getRango() {
		this.rango = new RangoDeFechas(fechaInicio , fechaFin);
		return rango;
	}
}

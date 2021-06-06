package edu.uclm.esi.carreful.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import edu.uclm.esi.carreful.TipoCupones.TipoCupon;

@Entity
public abstract class Cupon {
	@Id @Column(length = 36)
	private String codigo;
	private Date fechaInicio;
	private Date fechaFin;
	private double descuento;
	private boolean tipoDescuento;
	@Transient
	private TipoCupon tipo; 
	
	
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
	public boolean isTipoDescuento() {
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
	public void setTipoDescuento(boolean tipoDescuento) {
		this.tipoDescuento = tipoDescuento;
	}
	
	
}

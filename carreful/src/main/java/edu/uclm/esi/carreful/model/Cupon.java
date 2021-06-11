package edu.uclm.esi.carreful.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.springframework.http.HttpStatus;



import edu.uclm.esi.carreful.Patrones.RangoDeFechas;
import edu.uclm.esi.carreful.Patrones.TipoCupon;

@Entity
public class Cupon {
	@Id @Column(length = 36)
	private String codigo;
	private Date fechaInicio;
	private Date fechaFin;
	private double descuento;
	private String tipoDescuento;
	//@Transient
	private String tipo;
	
	@Transient
	private RangoDeFechas rango;
	
	
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
		this.rango = new RangoDeFechas(this.fechaInicio , this.fechaFin);
		return rango;
	}
}

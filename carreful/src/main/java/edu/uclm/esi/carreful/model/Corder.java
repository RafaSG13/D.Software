package edu.uclm.esi.carreful.model;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.springframework.data.annotation.Transient;

import edu.uclm.esi.carreful.auxiliares.TipoPedido;

@Entity
public class Corder {
	@Id @Column(length = 36)
	private String id;
	private double precioTotal;
	private String state;
	private String pedido;
	@Transient @OneToOne
	private TipoPedido tipo;
	 
	public Corder() {
		this.id = UUID.randomUUID().toString();
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public double getPrecioTotal() {
		return precioTotal;
	}
	public void setPrecioTotal(double precioTotal) {
		this.precioTotal = precioTotal;
	}

	public String getState() {
		return state;
	}
	
	public void setState(String state) {
		this.state = state;
	}

	public String getPedido() {
		return pedido;
	}

	public void setPedido(String pedido) {
		this.pedido = pedido;
	}

	public TipoPedido getTipo() throws InstantiationException, IllegalAccessException {
		return tipo.getClass().newInstance();
	}

	public void setTipo(TipoPedido tipo) throws InstantiationException, IllegalAccessException {
		this.tipo = tipo.getClass().newInstance();
	}
}

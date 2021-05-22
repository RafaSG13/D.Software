package edu.uclm.esi.carreful.auxiliares;

public class A_Domicilio extends TipoPedido{
	private String email_usuario;
	private String direccion;

	@Override
	public double AñadirGastosEnvio(double precioPedido) {
		return precioPedido+3.45;
	}
	public A_Domicilio(String email,String direccion) {
		super();
		this.email_usuario=email;
		this.direccion= direccion;
		
	}
	
	public A_Domicilio() {}
	
	
	public String getEmail_usuario() {
		return email_usuario;
	}
	public String getDireccion() {
		return direccion;
	}
	public void setEmail_usuario(String email_usuario) {
		this.email_usuario = email_usuario;
	}
	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}
	
}

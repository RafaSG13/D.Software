package edu.uclm.esi.carreful.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

import org.apache.commons.codec.digest.DigestUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class User {
	@Id @Column(length = 80)
	private String email;
	@Lob
	private String pwd;
	@Lob
	private String picture;
	private boolean rol;
	
	public User() { /*Metodo vacio para que spring/hibernate pueda instanciar el objeto*/}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}


	@JsonIgnore
	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = DigestUtils.sha512Hex(pwd);
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}
	
	public boolean getRol() {
		return rol;
	}

	public void setRol(boolean rol) {
		this.rol = rol;
	}
}

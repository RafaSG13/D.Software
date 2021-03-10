package edu.uclm.esi.carreful.model;

import javax.persistence.*;


import org.apache.commons.codec.digest.DigestUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class User {
	@Id @Column(length= 80)
	private String email;
	@Lob
	private String pwd;
	@Lob
	private String picture;
	
	public User() {
	}

	public String getEmail() {
		return email;
	}

	@JsonIgnore
	public String getPwd() {
		return pwd;
	}

	public String getPicture() {
		return picture;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	
	public void setPwd(String pwd) {
		this.pwd=DigestUtils.sha512Hex(pwd);
		this.pwd = pwd;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}
	
	
	
}

package edu.uclm.esi.carreful.tokens;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Token {
	@Id
	@Column(length = 36)
	private String id;
	private String email;
	private long date;
	private boolean used;
	
	public Token(String email) {
		this.id= UUID.randomUUID().toString();
		this.email=email;
		this.date=System.currentTimeMillis();
		
	}
	public Token() {}
	
	
	public String getId() {
		return id;
	}
	public String getEmail() {
		return email;
	}
	public long getDate() {
		return date;
	}
	public boolean isUsed() {
		return used;
	}
	public void setId(String id) {
		this.id = id;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public void setDate(long date) {
		this.date = date;
	}
	public void setUsed(boolean used) {
		this.used = used;
	}
}

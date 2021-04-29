package edu.uclm.esi.carreful.exceptions;

import org.springframework.http.HttpStatus;

public class CarrefulLoginException extends CarrefulException {

	private static final long serialVersionUID = 1L;

	public CarrefulLoginException() {
		super(HttpStatus.UNAUTHORIZED, "Credenciales Invalidos");
	}

}

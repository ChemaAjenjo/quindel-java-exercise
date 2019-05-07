package com.chemaajenjo.quindel.exception;

/* Excepción para caso de conflicto en una busqueda */

public class QuindelDocumentConflictException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private final String message;

	public QuindelDocumentConflictException(final String message) {
		super(message);
		this.message = message;
	}

	public String getId() {
		return message;
	}

}

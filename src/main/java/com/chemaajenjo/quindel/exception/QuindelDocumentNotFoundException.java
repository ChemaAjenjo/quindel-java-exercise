package com.chemaajenjo.quindel.exception;

/* /* Excepción para una busqueda no satisfactoria */

public class QuindelDocumentNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private final String id;

	public QuindelDocumentNotFoundException(final String id) {
		super("Documento " + id + " no encontrado.");
		this.id = id;
	}

	public String getId() {
		return id;
	}

}

package com.chemaajenjo.quindel.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.chemaajenjo.quindel.error.ApiError;
import com.chemaajenjo.quindel.exception.QuindelDocumentConflictException;
import com.chemaajenjo.quindel.exception.QuindelDocumentNoContentException;
import com.chemaajenjo.quindel.exception.QuindelDocumentNotFoundException;

/* Manejador de excepciones */

@ControllerAdvice
public class QuindelControllerAdvice extends ResponseEntityExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(QuindelControllerAdvice.class);

	/**
	 * Control de un documento o busqueda no encontrada
	 * 
	 * @param {@link QuindelDocumentNotFoundException}
	 * @return {@link ResponseEntity}
	 */
	@ExceptionHandler(QuindelDocumentNotFoundException.class)
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	public ResponseEntity<?> handleQuindelDocumentNotFoundException(QuindelDocumentNotFoundException e) {
		logger.error("QuindelDocumentNotFoundException handler executed");
		return error(new ApiError(HttpStatus.NOT_FOUND, e.getMessage()));
	}

	/**
	 * Control en la obtención de un documento o busqueda que produce un conflicto
	 * p.e. buscar un numero de linea no existente en un documento
	 * 
	 * @param {@link QuindelDocumentConflictException}
	 * @return {@link ResponseEntity}
	 */
	@ExceptionHandler(QuindelDocumentConflictException.class)
	@ResponseStatus(value = HttpStatus.CONFLICT)
	public ResponseEntity<?> handleQuindelDocumentConflictException(QuindelDocumentConflictException e) {
		logger.error("QuindelDocumentConflictException handler executed");
		return error(new ApiError(HttpStatus.CONFLICT, e.getMessage()));
	}

	/**
	 * Control para la obtención de una busqueda no satisfactoria
	 * 
	 * @param {@link QuindelDocumentNoContentException}
	 * @return {@link ResponseEntity}
	 */
	@ExceptionHandler(QuindelDocumentNoContentException.class)
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	public ResponseEntity<?> handleQuindelDocumentNoContentException(QuindelDocumentNoContentException e) {
		logger.error("QuindelDocumentNoContentException handler executed");
		return error(new ApiError(HttpStatus.NO_CONTENT));
	}

	/**
	 * Control de cualquier tipo de excepción
	 * 
	 * @param {@link Exception}
	 * @return {@link ResponseEntity}
	 */
	@ExceptionHandler({ Exception.class })
	public ResponseEntity<Object> handleAll(Exception ex) {
		return error(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Error occurred", ex));
	}

	private ResponseEntity<Object> error(ApiError apiError) {
		return new ResponseEntity<>(apiError, apiError.getStatus());
	}

}

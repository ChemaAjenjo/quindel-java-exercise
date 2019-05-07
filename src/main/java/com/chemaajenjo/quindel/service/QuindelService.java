package com.chemaajenjo.quindel.service;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.chemaajenjo.quindel.model.QuindelDocument;

/* Interface de la logica de negocio */

@Service
public interface QuindelService {

	/**
	 * Obtención del numero de lineas de un documento
	 * 
	 * @param id - Identificador del documento
	 * @return {@link Integer} - numero de lineas del documento
	 * @throws Exception
	 */
	public Integer getDocumentSize(String id) throws Exception;

	/**
	 * Recuperar una línea
	 * 
	 * @param id    - Identificador del documento
	 * @param index - Numero de linea a obtener
	 * @return {@link String} - Contenido de la linea solicitada
	 * @throws Exception
	 */
	public String getLine(String id, Integer index) throws Exception;

	/**
	 * Añadir una línea al final del documento
	 * 
	 * @param id   - Identificador del documento
	 * @param data - Contenido a añadir
	 * @throws Exception
	 */
	public void addLine(String id, String data) throws Exception;

	/**
	 * Modificar una linea
	 * 
	 * @param file - contenido a añadir
	 * @throws Exception
	 */
	public void updateLine(QuindelDocument file) throws Exception;

	/**
	 * Recuperar todo el texto de un documento
	 * 
	 * @param id - Identificador del documento
	 * @return {@link String} - contenido del documento
	 * @throws Exception
	 */
	public String getDocumentContent(String id) throws Exception;

	/**
	 * Buscar dentro del documento las lineas que contienen una palabra dada.
	 * 
	 * @param id    - Identificador del documento
	 * @param query - Palabra a buscar
	 * @Return {@link List<QuindelDocument>} - lista documentos y lineas que
	 *         contienen la palabra buscada
	 * @throws IOException
	 */
	public List<QuindelDocument> findLineByWord(String id, String query) throws IOException;

	/**
	 * Buscar dentro de los documentos las lineas que contienen una palabra dada.
	 * Busca dentro de todos los documentos de la bbdd
	 * 
	 * @param query - Palabra a buscar
	 * @Return {@link List<QuindelDocument>} - lista documentos y lineas que
	 *         contienen la palabra buscada
	 * @throws Exception
	 */
	public List<QuindelDocument> findLineByWord(String query) throws IOException;

	/**
	 * Cualquier otra funcionalidad: Creación de un documento
	 * 
	 * @param file - Documento a crear
	 * @throws Exception
	 */
	public void storageDocument(QuindelDocument file);
}

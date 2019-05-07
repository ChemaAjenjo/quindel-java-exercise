package com.chemaajenjo.quindel.controller;

import java.util.List;

import javax.validation.constraints.Min;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.chemaajenjo.quindel.model.QuindelDocument;
import com.chemaajenjo.quindel.service.QuindelService;

@RestController
@RequestMapping("/quindel-api/document")
@Validated // incluyo la validación para los pathvariable
public class QuindelController {

	private static Logger logger = LoggerFactory.getLogger(QuindelController.class);

	@Autowired
	private QuindelService service;

	/**
	 * Recuperar todo el texto de un documento
	 * 
	 * @param id - Identificador del documento
	 * @return {@link ResponseEntity<String>} - Contenido del documento
	 * @throws Exception
	 */
	@GetMapping("/{id}")
	public ResponseEntity<String> getDocumentContent(@PathVariable("id") String id) throws Exception {
		return new ResponseEntity<String>(service.getDocumentContent(id), HttpStatus.OK);
	}

	//
	/**
	 * Recuperar el numero de líneas del documento
	 * 
	 * @param id - Identificador del documento
	 * @return {@link ResponseEntity<Integer>} - Numero de lineas del documento
	 * @throws Exception
	 */
	@GetMapping("/{id}/lines")
	public ResponseEntity<Integer> getDocumentSize(@PathVariable String id) throws Exception {
		return new ResponseEntity<Integer>(service.getDocumentSize(id), HttpStatus.OK);
	}

	/**
	 * Recuperar una línea
	 * 
	 * @param id    - Identificador del documento
	 * @param index - Numero de linea a obtener
	 * @return {@link ResponseEntity<String>} - Contenido de la linea solicitada
	 * @throws Exception
	 */
	@GetMapping("/{id}/line/{index}")
	public ResponseEntity<String> getLine(@PathVariable("id") String id, @PathVariable("index") @Min(1) Integer index)
			throws Exception {
		return new ResponseEntity<String>(service.getLine(id, index), HttpStatus.OK);
	}

	/**
	 * Añadir una línea al final del documento
	 * 
	 * @param id   - Identificador del documento
	 * @param file - Contenido a añadir
	 * @return {@link ResponseEntity<HttpStatus>} - Estado de la operación
	 * @throws Exception
	 */
	@PutMapping("/{id}/line")
	public ResponseEntity<HttpStatus> addLine(@PathVariable("id") String id, @RequestBody QuindelDocument file)
			throws Exception {

		// seteo los valores por si se da el caso de que no vengan informados en el body
		file.setId(id);
		service.addLine(file.getId(), file.getData());

		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * Modificar una linea
	 * 
	 * @param id    - Identificador del documento
	 * @param file  - Contenido a añadir
	 * @param index - Linea a modificar
	 * @return {@link ResponseEntity<HttpStatus>} - Estado de la operación
	 * @throws Exception
	 */
	@PutMapping("/{id}/line/{index}")
	public ResponseEntity<HttpStatus> updateLine(@PathVariable("id") String id, @RequestBody QuindelDocument file,
			@PathVariable("index") @Min(1) Integer index) throws Exception {

		// seteo los valores por si se da el caso de que no vengan informados en el body
		file.setId(id);
		file.setIndex(index);
		service.updateLine(file);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * Soportar la edición de múltiples documentos: Añadir una fila al final del
	 * documento
	 * 
	 * @param fileList - Listado de ficheros a modificar
	 * @param type     - tipo de modificacion (add = añadir al final, update =
	 *                 actualizar)
	 * @return {@link ResponseEntity<HttpStatus>} - Estado de la operación
	 * @throws Exception
	 */
	@RequestMapping(value = "/line", params = { "type=add" }, method = RequestMethod.PUT)
	public ResponseEntity<HttpStatus> addLineToDocuments(@RequestBody List<QuindelDocument> fileList) throws Exception {

		for (QuindelDocument file : fileList) {
			service.addLine(file.getId(), file.getData());
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * Soportar la edición de múltiples documentos: Modificar una linea
	 * 
	 * @param fileList - Listado de ficheros a modificar
	 * @param type     - tipo de modificacion (add = añadir al final, update =
	 *                 actualizar)
	 * @return {@link ResponseEntity<HttpStatus>} - Estado de la operación
	 * @throws Exception
	 */
	@RequestMapping(value = "/line", params = { "type=update" }, method = RequestMethod.PUT)
	public ResponseEntity<HttpStatus> updateLineToDocument(@RequestBody List<QuindelDocument> fileList)
			throws Exception {

		for (QuindelDocument file : fileList)
			service.updateLine(file);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * Buscar dentro del documento las lineas que contienen una palabra dada.
	 * 
	 * @param id    - Identificador del documento
	 * @param query - Palabra a buscar
	 * @return {@link ResponseEntity<List<QuindelDocument>>} - lista documentos y
	 *         lineas que contienen la palabra buscada
	 * @throws Exception
	 */
	@GetMapping("/{id}/search")
	public ResponseEntity<List<QuindelDocument>> findLineByWord(@PathVariable("id") String id, String query)
			throws Exception {
		return new ResponseEntity<List<QuindelDocument>>(service.findLineByWord(id, query), HttpStatus.OK);
	}

	/**
	 * Buscar dentro de los documentos las lineas que contienen una palabra dada.
	 * Busca dentro de todos los documentos de la bbdd
	 * 
	 * @param query - Palabra a buscar
	 * @return {@link ResponseEntity<List<QuindelDocument>>} - lista documentos y
	 *         lineas que contienen la palabra buscada
	 * @throws Exception
	 */
	@GetMapping("/search")
	public ResponseEntity<List<QuindelDocument>> findLineByWord(String query) throws Exception {
		return new ResponseEntity<List<QuindelDocument>>(service.findLineByWord(query), HttpStatus.OK);
	}

	/**
	 * Cualquier otra funcionalidad: Creación de un documento
	 * 
	 * @param file - Documento a crear
	 * @return {@link ResponseEntity<HttpStatus>} - Estado de la operación
	 * @throws Exception
	 */
	@PostMapping("/save")
	public ResponseEntity<HttpStatus> saveDocument(@RequestBody QuindelDocument file) throws Exception {
		service.storageDocument(file);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}
}

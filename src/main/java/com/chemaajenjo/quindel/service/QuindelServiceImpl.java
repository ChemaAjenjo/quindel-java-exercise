package com.chemaajenjo.quindel.service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.chemaajenjo.quindel.exception.QuindelDocumentConflictException;
import com.chemaajenjo.quindel.exception.QuindelDocumentNoContentException;
import com.chemaajenjo.quindel.exception.QuindelDocumentNotFoundException;
import com.chemaajenjo.quindel.model.QuindelDocument;
import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;

/* Implementación de la logica de negocio */

@Service
public class QuindelServiceImpl implements QuindelService {

	private static Logger logger = LoggerFactory.getLogger(QuindelServiceImpl.class);

	@Autowired // inyeccion del bean gridfsTemplate declarado en MongoDBConfig.java
	GridFsTemplate gridFsTemplate;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.chemaajenjo.quindel.service.QuindelService#getDocumentSize(java.lang.
	 * String)
	 */
	@Override
	public Integer getDocumentSize(String id) throws Exception {
		return (int) (long) getContent(id).lines().count();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.chemaajenjo.quindel.service.QuindelService#getLine(java.lang.String,
	 * java.lang.Integer)
	 */
	@Override
	public String getLine(String id, Integer index) throws Exception {

		// controlo que la linea buscada no sea mayor que el numero de lineas del
		// documento
		if (index <= getDocumentSize(id)) {
			return getContent(id).lines().collect(Collectors.toList()).get(index - 1);
		} else {
			throw new QuindelDocumentConflictException(
					"Se ha excedido el numero de lineas del documento, acorte la busqueda.");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.chemaajenjo.quindel.service.QuindelService#addLine(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void addLine(String id, String data) {
		try {

			// Busco el documento el la base de datos para obtener el contenido
			GridFSFile gridFsFile = gridFsTemplate.findOne(Query.query(Criteria.where("filename").is(id)));
			GridFsResource gridFSResource = gridFsTemplate.getResource(gridFsFile);

			InputStream inputStream = gridFSResource.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, Charset.defaultCharset()));

			StringBuilder buffer = new StringBuilder();
			String line = null;
			// Vuelco el contennido a un StringBuilder
			while ((line = br.readLine()) != null) {
				buffer.append(line);
				buffer.append('\n');
			}
			// añado al final la linea que nos llega por parametro
			buffer.append(data);

			// Borro el documento antiguo de la base de datos
			gridFsTemplate.delete(Query.query(Criteria.where("filename").is(id)));
			// Almaceno el documento modificado
			gridFsTemplate.store(new ByteArrayInputStream(buffer.toString().getBytes()), gridFsFile.getFilename(),
					gridFsFile.getMetadata());

		} catch (IllegalArgumentException e) {
			logger.error(e.getMessage());
			throw new QuindelDocumentNotFoundException(id);
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.chemaajenjo.quindel.service.QuindelService#updateLine(com.chemaajenjo.
	 * quindel.model.QuindelDocument)
	 */
	@Override
	public void updateLine(QuindelDocument file) throws Exception {

		try {
			// Busco el documento el la base de datos para obtener el contenido
			GridFSFile gridFsFile = gridFsTemplate.findOne(Query.query(Criteria.where("filename").is(file.getId())));
			GridFsResource gridFSResource = gridFsTemplate.getResource(gridFsFile);

			InputStream inputStream = gridFSResource.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, Charset.defaultCharset()));

			StringBuilder buffer = new StringBuilder();

			// controlo si la linea buscada está dentro del rango
			if (file.getIndex() <= getDocumentSize(file.getId())) {

				// convierto el contenido del documento en una lista
				List<String> content = br.lines().collect(Collectors.toList());
				// modifico la linea de la lista por la nueva linea
				content.set(file.getIndex() - 1, file.getData());

				// almaceno de nuevo el contenido del fichero en un StringBuilder
				for (String line : content) {
					buffer.append(line + "\n");
				}

				// Borro el documento antiguo de la base de datos
				gridFsTemplate.delete(Query.query(Criteria.where("filename").is(file.getId())));
				// Almaceno el documento modificado
				gridFsTemplate.store(new ByteArrayInputStream(buffer.toString().getBytes()), gridFsFile.getFilename(),
						gridFsFile.getMetadata());
			} else {
				throw new QuindelDocumentConflictException(
						"Se ha excedido el numero de lineas del documento, acorte la busqueda.");
			}
		} catch (IllegalArgumentException e) {
			logger.error(e.getMessage());
			throw new QuindelDocumentNotFoundException(file.getId());
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.chemaajenjo.quindel.service.QuindelService#getDocumentContent(java.lang.
	 * String)
	 */
	@Override
	public String getDocumentContent(String id) {
		StringBuilder buffer = new StringBuilder();
		// obtengo el contenido del fichero y lo vuelco a una lista para recorrerla con
		// un forEach y de esta forma almacenarlo en un StringBuilder para devolver el
		// contenido del documento
		getContent(id).lines().collect(Collectors.toList()).stream().forEach(line -> buffer.append(line + "\n"));
		return buffer.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.chemaajenjo.quindel.service.QuindelService#storageDocument(com.
	 * chemaajenjo.quindel.model.QuindelDocument)
	 */
	@Override
	public void storageDocument(QuindelDocument file) {
		// Creo los metadatos del documento
		DBObject metaData = new BasicDBObject();
		metaData.put("type", "data");

		// Vuelco la información a un input stream
		InputStream inputStream = new ByteArrayInputStream(file.getData().getBytes(Charset.defaultCharset()));

		// almaceno el contenido del documento en la bbdd
		gridFsTemplate.store(inputStream, file.getId().toString(), MediaType.TEXT_PLAIN_VALUE, metaData);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.chemaajenjo.quindel.service.QuindelService#findLineByWord(java.lang.
	 * String, java.lang.String)
	 */
	@Override
	public List<QuindelDocument> findLineByWord(String id, String query) throws IOException {

		List<QuindelDocument> documentList = new ArrayList<QuindelDocument>();

		// obtengo el contenido del documento
		GridFSFile gridFsFile = gridFsTemplate.findOne(Query.query(Criteria.where("filename").is(id)));
		GridFsResource gridFSResource = gridFsTemplate.getResource(gridFsFile);

		InputStream inputStream = gridFSResource.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, Charset.defaultCharset()));

		// recorro el stream del contenido del documento buscando las lineas que
		// contengan la palabra buscada
		br.lines().filter(line -> line.contains(query)).forEach((line) -> {
			// Para cada linea que contiene la linea almaceno los datos en la lista
			QuindelDocument file = new QuindelDocument();
			file.setData(line);
			file.setId(gridFsFile.getFilename());
			documentList.add(file);
		});

		// Si la lista esta vacia creo la excepción QuindelDocumentNoContentException
		// para forzar un NO_CONTENT
		if (documentList.isEmpty())
			throw new QuindelDocumentNoContentException();

		return documentList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.chemaajenjo.quindel.service.QuindelService#findLineByWord(java.lang.
	 * String)
	 */
	@Override
	public List<QuindelDocument> findLineByWord(String query) throws IOException {

		List<QuindelDocument> documentList = new ArrayList<QuindelDocument>();
		// busco todos los documentos de la bbbdd
		GridFSFindIterable gridFsFindIterable = gridFsTemplate.find(new Query());

		// para cada documento
		gridFsFindIterable.forEach((Block<GridFSFile>) (gridFsFile) -> {
			try {
				// busco si contiene la palabra buscada y lo almaceno en una lista
				documentList.addAll(findLineByWord(gridFsFile.getFilename(), query));
			} catch (IOException e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			}
		});

		// Si la lista esta vacia creo la excepción QuindelDocumentNoContentException
		// para forzar un NO_CONTENT
		if (documentList.isEmpty())
			throw new QuindelDocumentNoContentException();

		return documentList;

	}

	/* private methods */

	/**
	 * Obtención del contenido del documento
	 * 
	 * @param id - Identificador del documento
	 * @return {@link BufferedReader} - BufferedReader con el contenido del
	 *         documento
	 */
	private BufferedReader getContent(String id) {
		try {
			// obtengo el documento
			GridFSFile gridFsFile = gridFsTemplate.findOne(Query.query(Criteria.where("filename").is(id)));
			GridFsResource gridFSResource = gridFsTemplate.getResource(gridFsFile);

			// obtengo el recurso del documento
			InputStream inputStream = gridFSResource.getInputStream();

			// devuelvo el contenido del documento
			return new BufferedReader(new InputStreamReader(inputStream, Charset.defaultCharset()));

		} catch (IllegalArgumentException e) {
			logger.error(e.getMessage());
			throw new QuindelDocumentNotFoundException(id);
		} catch (IOException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

}

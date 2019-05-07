package com.chemaajenjo.quindel.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import com.mongodb.MongoClient;

/* Configuración de acceso a MongoDB */

@Configuration
public class MongoDbConfig extends AbstractMongoConfiguration {

	// las propiedades de conexión se cargan desde el fichero de configuración
	// application.yml

	// nombre de la base de datos
	@Value("${mongo.database}")
	private String database;

	// nombre de la maquina
	@Value("${mongo.hostname}")
	private String host;

	// puerto de conexión
	@Value("${mongo.port}")
	private Integer port;

	@Override
	public MongoClient mongoClient() {
		return new MongoClient(host, port);
	}

	@Override
	protected String getDatabaseName() {
		return database;
	}

	// se crea un bean de plantilla de gridFS para acceder a los documentos de la
	// bbdd
	@Bean
	public GridFsTemplate gridFsTemplate() throws Exception {
		return new GridFsTemplate(mongoDbFactory(), mappingMongoConverter());
	}

}

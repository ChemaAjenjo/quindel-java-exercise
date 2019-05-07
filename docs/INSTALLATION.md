# Instalación

### Configuración previa a la instalación

**IMPORTANTE:** Se da por hecho que el entorno donde se desplegará la aplicación tiene instalado [Maven](https://maven.apache.org/download.cgi), [MongoDB](https://docs.mongodb.com/manual/installation/) y [Git](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git).

1. Descargar el proyecto desde [github](https://github.com/ChemaAjenjo/quindel-java-exercise.git)

2. No será necesario inicializar la base de datos, esta se creará despues de la inserción del primer documento. Unicamente será necesario configurar el archivo ``src/main/resources/application.yml`` para apuntar al servidor donde esté montada la base de datos. En este caso será en un servicio que correrá en la propia maquina donde se ejecute el proyecto. Para insertar el primer documento ejecutar el siguiente comando una vez este ejecutandose:
```
curl -X POST -H "Content-Type:application/json" -d" {\"id\":\"3000\",\"data\":\"line1\nline2\"}" http://localhost:9090/quindel-api/document/save
```

## Instalación

Situandonos en la raiz del proyecto, ejecutamos:

	mvn clean install

Una vez termine podemos optar por ejecutar el proyecto de dos maneras diferentes:

	java -jar tarjet/quindel-java-exercise-0.0.1-SNAPSHOT.jar
	
o bien mediante Maven:

	mvn spring-boot:run
	

### Ejecución (desde Windows)

1. Inserción de documento y creación de la BBDD: 
```
curl -X POST -H "Content-Type:application/json" -d" {\"id\":\"3000\",\"data\":\"line3\nline2\"}" http://localhost:9090/quindel-api/document/save
```
2. Recuperar el numero de líneas del documento
```
curl -X GET http://localhost:9090/quindel-api/document/3000/lines
```
3. Recuperar una línea
```
curl -X GET http://localhost:9090/quindel-api/document/3000/lines
```
4. Añadir una línea al final del documento
```
curl -X PUT -H "Content-Type:application/json" -d "{\"data\":\"new final line\"}' http://localhost:9090/quindel-api/document/3000/line
```
5. Modificar una linea:
```
curl -X PUT -H "Content-Type:application/json" -d "{\"data\":\"line1\"}" http://localhost:9090/quindel-api/document/3000/line/1 
```
6. Recuperar todo el texto del documento:
```	
curl -X GET http://localhost:9090/quindel-api/document/3000
```
7. Soportar la edición de múltiples documentos

- Añadir una línea al final del documento
```
curl -X PUT -H "Content-Type:application/json" -d "[{\"id\": \"3000\",\"data\": \"endfile\"},{\"id\": \"2001\",\"data\": \"endfile\"}]" "http://localhost:9090/quindel-api/document/line?type=add"
```
- Modificar una linea
```
curl -X PUT -H "Content-Type:application/json" -d '[{\"id\": \"3000\",\"data\": \"linea 1\",\"index\" : 1},{\"id\": \"2000\",\"data\": \"linea 2\",\"index\" : 2}]' 'http://localhost:9090/quindel-api/document/line?type=update'
```
8. Buscar dentro del documento(s) las lineas que contienen una palabra dada.
	
- En un unico documento
```
curl -X GET "http://localhost:9090/quindel-api/document/3000/search?query=new"
```
- En todos los documentos
```
curl -X GET "http://localhost:9090/quindel-api/document/search?query=new"
```	
	

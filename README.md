# Ejercicio de programación

## Objetivo

   El objetivo del ejercicio es desarrollar un API (REST, GraphQL, etc., a elección del candidato) que emule un editor de textos orientado a líneas.


## Descripción

   La API modelara un documento y soportara las siguiente acciones:

- [x] Recuperar el numero de líneas del documento: el API tiene que proveer un mecanismo por el cual recuperar el número de líneas total que tiene el documento.

  ``curl -X GET http://localhost:9090/quindel-api/document/3000/lines``

- [x] Recuperar una línea: dado un número de linea, tiene que existir un mecanismo para recuperar el texto de dicha línea.

  ``curl -X GET http://localhost:9090/quindel-api/document/3000/lines``

- [x] Añadir una línea al final del documento: dado un texto, tiene que existir un mecanismo para añadirlo al final del documento como una línea nueva.

  ``curl -X PUT -H "Content-Type:application/json" -d "{\"data\":\"new final line\"}' http://localhost:9090/quindel-api/document/3000/line``

- [x] Modificar una linea: dado un número de línea y un texto, tiene que existir un mecanismo para substituir el contenido de dicha línea por el texto dado.

  ``curl -X PUT -H "Content-Type:application/json" -d "{\"data\":\"line1\"}" http://localhost:9090/quindel-api/document/3000/line/1``

- [x] Recuperar todo el texto del documento: tiene que existir un mecanismo que sea capaz de devolver todo el contenido del documento resultado de concatenar todas las líneas por orden. 

  ``curl -X GET http://localhost:9090/quindel-api/document/3000``

El candidato tendrá que añadir además soporte para dos de las siguientes funcionalidades (a elegir):

- [x] Soportar la edición de múltiples documentos.

  - Añadir una línea al final del documento: 
  
    ``curl -X PUT -H "Content-Type:application/json" -d "[{\"id\": \"3000\",\"data\": \"endfile\"},{\"id\": \"2001\",\"data\": \"endfile\"}]" 	"http://localhost:9090/quindel-api/document/line?type=add"``
  
  - Modificar una linea: 
  
    ``curl -X PUT -H "Content-Type:application/json" -d '[{\"id\": \"3000\",\"data\": \"linea 1\",\"index\" : 1},{\"id\": \"2000\",\"data\": \"linea 2\",\"index\" : 2}]' 'http://localhost:9090/quindel-api/document/line?type=update'``

- [x] Buscar dentro del documento(s) las lineas que contienen una palabra dada.

  - En un único documento:
  
    ``curl -X GET "http://localhost:9090/quindel-api/document/3000/search?query=new"``
  
  - En todos los documentos:
  
    ``curl -X GET "http://localhost:9090/quindel-api/document/search?query=new"``	

- [x] Cualquier otra funcionalidad que quiera proponer el candidato: 
  
  - Creación de un documento:
  
    ``curl -X POST -H "Content-Type:application/json" -d" {\"id\":\"3000\",\"data\":\"line3\nline2\"}" http://localhost:9090/quindel-api/document/save``

## Condiciones del desarrollo

   Las única condición del desarrollo, es que este habrá de realizarse en Java.

   Podrá utilizarse cualquier framework de programación con el que el candidato se sienta cómodo y cualquier paquete de Java que necesite.

   Como almacenamiento de datos, el candidato podrá utilizar el mecanismo que le resulte mas rápido para el desarrollo (ficheros, una base de datos embebida, MySQL, PostgreSQL, MongoDB, etc.).

   En cualquier caso, cualquier software necesario para la compilación o ejecución del proyecto habrá de ser Open Source, estar disponible a través de repositorios públicos (de cualquier tipo) y ser de fácil instalación.


## Entregables

   Como resultado de la realización del ejercicio, habrán de entregarse los siguientes ítems:

- [x] Documento de instalación: habrá de incluirse un documento donde se detallen los pasos a seguir para compilar el programa con todas sus dependencias (se recomienda utilizar alguna herramienta como Maven para automatizar el proceso).

   Veáse: [Documento de instalación](docs/INSTALLATION.md)

- [x] Documento de diseño: habrá de incluirse un documento donde se explique el diseño de la aplicación y las decisiones tomadas a lo largo del desarrollo, los motivos y las alternativas consideradas
     De haberse introducido alguna limitación arbitraría en la aplicación, explicarla y dar alguna idea de como se podría eliminar.
     Notese que hablamos de decisiones sobre el diseño. Decisiones menores de programación es mejor incluirlas directamente como comentarios en el código.
     
   Veáse: [Documento de diseño](docs/DESIGN.md)

- [x] Código fuente del programa (no es necesario que este comentado).

- [x] Batería de pruebas: sin necesidad de ser exhaustiva, ha de incluirse algún mecanismo que permita comprobar la funcionalidad principales del API desarrollado.

   Veáse: [Colección de Postman](https://raw.githubusercontent.com/ChemaAjenjo/quindel-java-exercise/develop/docs/quindel-java-exercise.postman_collection.json?token=ABHUQUYLNTIAKIIUJI2UCN24ZGUC4)

- [x] En caso de utilizarse una base de datos externa, habrá de incluirse un script SQL (o similar) que permita inicializar la base de datos.

> **IMPORTANTE** Al tratarse de una base de datos mongoDB no existe script para inicializar la base de datos, unicamente será necesario configurar el archivo ``src/main/resources/application.yml`` para apuntar al servidor donde esté montada la base de datos. En este caso será en un servicio que correrá en la propia maquina donde se ejecute el proyecto.


## Evaluación

   Cuestiones que se tendrán en cuenta a la hora de evaluar el ejercicio por orden de importancia:

   - Documento de diseño y justificación de decisiones.

   - Calidad del código, valorándose positivamente la simplicidad y claridad del mismo, la consistencia y el uso idiomático del
     lenguaje Java, sus librerías y cualquier framework utilizado en el desarrollo.

     En el caso de que el candidato haya tenido que tomar decisiones sobre aspectos controvertidos (p.ej. utilizar un bucle explicito o streams), puede añadir algunas anotaciones en el código, para que quede constancia de que conoce las alternativas.

   - Corrección y robustez de la aplicación.

   - Eficiencia del código.

   - Complejidad de las funcionalidades opcionales seleccionadas.

   Una vez evaluado el ejercicio, de ser satisfactorio el resultado, se realizara una nueva entrevista al candidato donde se le podrá pedir que comente o explique distintos aspectos del ejercicio.


## Cuestiones adicionales

   Si por razones de tiempo, el candidato no pudiese implementar toda la funcionalidad requerida, podría reducir el alcance del ejercicio eliminando algunas partes, pero ha de explicar como desarrollaría las partes omitidas (¡y no elija solo las tareas más fáciles!).

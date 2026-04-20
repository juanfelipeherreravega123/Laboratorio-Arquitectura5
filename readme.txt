Laboratorio JPA - Actividad
===========================

Efecto de @OneToMany en la entidad Competitor sobre la tabla Producto
----------------------------------------------------------------------

Al agregar la anotacion @OneToMany(cascade=ALL, mappedBy="competitor") en la entidad
Competitor, JPA establece una relacion bidireccional uno-a-muchos entre Competitor y
Producto. El efecto directo sobre la tabla PRODUCTO en la base de datos es que dicha
tabla recibe una columna adicional llamada COMPETITOR_ID (una llave foranea / foreign key)
que referencia el ID de la tabla COMPETITOR. Esto es consecuencia de la anotacion
@ManyToOne que ya existia en la entidad Producto: cuando EclipseLink genera el esquema
con la estrategia "drop-and-create", crea automaticamente esa columna de clave foranea en
PRODUCTO para mantener la relacion de propiedad (el lado "many" siempre posee la clave
foranea). El mappedBy="competitor" en Competitor le indica a JPA que la columna
propietaria de la relacion esta en la entidad Producto (en el campo competitor), por lo
que no se genera ninguna tabla intermedia. En resumen, @OneToMany provoca que la tabla
PRODUCTO tenga un campo COMPETITOR_ID que permite saber a que concursante pertenece
cada producto.

Instrucciones de uso
--------------------
1. Abrir Derby en NetBeans: Services -> Databases -> jdbc:derby://localhost:1527/sample
   - Usuario: app / Contrasena: app
2. Ejecutar Main.java (Run File) para crear las tablas automaticamente.
3. Endpoints disponibles en http://localhost:8080/competitors:
   - GET  /competitors/get          -> lista todos los competidores
   - POST /competitors/add          -> crea un competidor (JSON body)
   - POST /competitors/login        -> login con email y password (JSON body)

Ejemplo POST /competitors/add:
{
  "name": "Laura",
  "surname": "Gomez",
  "address": "Calle 123",
  "email": "laura.gomez@gmail.com",
  "password": "1234",
  "age": 20,
  "telephone": "7659675",
  "cellphone": "3002345436",
  "city": "Bogota",
  "country": "Colombia"
}

Ejemplo POST /competitors/login:
{
  "email": "laura.gomez@gmail.com",
  "password": "1234"
}

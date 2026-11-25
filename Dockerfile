# Imagen del backend de LogiTrack para el despliegue en Render (Capitulo 8).
# Render no ofrece un entorno nativo de Java, por eso el servicio se publica como Docker.

# Etapa 1: compilar el .jar con Maven y JDK 21
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -B dependency:go-offline
COPY src ./src
RUN mvn -q -B clean package -DskipTests

# Etapa 2: ejecutar solo con el JRE (imagen mas liviana)
FROM eclipse-temurin:21-jre
WORKDIR /app
# Zona horaria de Lima: la validacion "fecha de salida no puede estar en el pasado"
# compara con el reloj del servidor, que en la nube esta en UTC (incidencia I-05).
ENV TZ=America/Lima
COPY --from=build /app/target/logitrack-backend.jar app.jar
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java -Duser.timezone=America/Lima -Xmx384m -jar app.jar"]

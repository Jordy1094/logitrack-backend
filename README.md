# LogiTrack Backend

API REST de LogiTrack: gestión de flota y asignación de rutas para TransRapido.
Desarrollada con Spring Boot 3 (Java 21), Spring Data JPA, Spring Security con JWT y MySQL, tal como se documenta en el Capítulo 5 del informe.

## Requisitos

- JDK 21
- Maven 3.9+
- MySQL 8 (corriendo en local)

## Base de datos

Crear la base de datos vacía antes de arrancar la aplicación:

```sql
CREATE DATABASE logitrack;
```

Las tablas las crea automáticamente Hibernate (`ddl-auto=update`) la primera vez que se ejecuta el proyecto.

Revisar `src/main/resources/application.properties` y ajustar `spring.datasource.username` / `spring.datasource.password` si tu MySQL local no usa `root/root`.

## Instalación y ejecución

```bash
mvn clean install
mvn spring-boot:run
```

La API queda disponible en `http://localhost:8080`.

## Cuentas de prueba

La Tabla 34 del informe documenta el registro de conductores (dni, nombres, apellidos, nroLicencia, teléfono), pero no un endpoint para crear la primera cuenta de usuario (correo/contraseña). Para poder probar `POST /api/auth/login` y los endpoints protegidos desde el primer arranque, el proyecto crea automáticamente tres cuentas de prueba (solo si la tabla `usuario` está vacía):

| Rol            | Correo                   | Contraseña      |
|----------------|---------------------------|-----------------|
| ADMINISTRADOR  | admin@logitrack.com       | Admin123!       |
| SUPERVISOR     | supervisor@logitrack.com  | Supervisor123!  |
| CONDUCTOR      | conductor@logitrack.com   | Conductor123!   |

La cuenta CONDUCTOR queda vinculada a un conductor de prueba (DNI 10000001, Luis Ramos) para poder probar `GET /api/rutas/mia`.

Cambia estas credenciales (o elimina `DataSeeder.java`) antes de cualquier entorno que no sea de pruebas.

## Probar con Postman

1. `POST /api/auth/login` con uno de los correos de la tabla anterior y copiar el `token` de la respuesta.
2. En las demás peticiones, agregar el encabezado `Authorization: Bearer <token>`.
3. Los casos sugeridos en la Tabla 37 del informe (login válido/inválido, crear ruta con camión disponible/no disponible, placa repetida, dar de baja un camión en ruta, etc.) se pueden reproducir contra `http://localhost:8080`.

## Endpoints

Los 17 endpoints están documentados en las Tablas 34 a 36 del informe (`/api/auth`, `/api/camiones`, `/api/conductores`, `/api/rutas`, `/api/mantenimientos`, `/api/dashboard`).


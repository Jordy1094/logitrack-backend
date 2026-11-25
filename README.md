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

## Endpoints y documentación Swagger

Los endpoints están documentados en las Tablas 34 a 36 del informe (`/api/auth`, `/api/camiones`, `/api/conductores`, `/api/rutas`, `/api/mantenimientos`, `/api/dashboard`). En la integración con Angular (Capítulo 6) se agregó `GET /api/rutas?estado=EN_CURSO`, que usa el Dashboard para listar y finalizar las rutas en curso.

Con el backend en ejecución, la documentación interactiva (Capítulo 7) está en:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

Para probar un endpoint protegido: ejecutar `POST /api/auth/login`, copiar el `token`, pulsar **Authorize** y pegarlo.

## Pruebas unitarias

```bash
mvn test
```

`RutaServiceTest` y `MantenimientoServiceTest` prueban con JUnit 5 y Mockito las reglas de negocio principales (asignación de ruta con camión o conductor ocupado, finalización, ingreso y cierre de mantenimiento) sin necesidad de base de datos.

## Variables de entorno (despliegue, Capítulo 8)

| Variable | Uso | Valor local por defecto |
|---|---|---|
| `DB_URL` | URL JDBC de MySQL | `jdbc:mysql://localhost:3306/logitrack` |
| `DB_USERNAME` / `DB_PASSWORD` | Credenciales de MySQL | `root` / `root` |
| `DDL_AUTO` | `update` en el primer despliegue, luego `validate` | `update` |
| `JWT_SECRET` | Clave de firma del token (mínimo 32 caracteres) | clave de desarrollo |
| `JWT_EXPIRATION_MS` | Vigencia del token (RNF-01: 8 horas) | `28800000` |
| `CORS_ALLOWED_ORIGINS` | Dominios del frontend separados por coma | `http://localhost:4200` |
| `PORT` | Puerto (Render lo asigna solo) | `8080` |

## Despliegue en Render

El repositorio incluye un `Dockerfile` (Render no tiene entorno nativo de Java):

1. Crear la base MySQL en Railway y copiar host, puerto, usuario, contraseña y nombre de la base.
2. En Render: **New → Web Service**, conectar este repositorio, *Runtime: Docker*, plan *Free*.
3. Cargar las variables de entorno de la tabla anterior. `DB_URL` queda así:
   `jdbc:mysql://<host>:<puerto>/<base>?useSSL=true&requireSSL=false&serverTimezone=America/Lima`
4. Al terminar el despliegue, la API queda en `https://<servicio>.onrender.com` (HTTPS con certificado de Render).


# great-travel

Este READMEse encuentra disponible [Inges EN](./README.md)

**great-travel**  Es una API de backend diseñada para una plataforma 
de viajes que permite a los usuarios reservar vuelos, hoteles y tours.
Incluye funciones como autenticación de usuarios, búsqueda de vuelos y 
hoteles, reserva de reservas y gestión de tours, entre otras. 
Desarrollada con Spring Boot, la API también incluye autenticación
basada en JWT, almacenamiento en caché con Redis y documentación completa
de la API con Swagger.

---

## Tecnologías utilizadas

| Herramienta           | Uso principal                          |
|-----------------------|----------------------------------------|
| Java 17               | Lenguaje principal                     |
| Spring Boot 3         | Framework backend                      |
| Spring Web            | Exposición de API REST                 |
| Spring Data JPA       | Persistencia y consultas con Hibernate |
| Spring Security + JWT | Autenticación y autorización           |
| Redis + Redisson      | Cache de productos                     |
| PostgreSQL            | Base de datos relacional               |
| H2                    | Base de datos en memoria para testing  |
| Swagger (OpenAPI)     | Documentación interactiva de la API    |
| Docker Compose        | Contenedores para MySQL y Redis        |
| JUnit, Mockito        | Pruebas unitarias y de integración     |
| Lombok                | Reducción del código repetitivo        | 


---
## Funcionalidades

- `Autenticación basada en JWT` (registro e inicio de sesión)
- `Gestión de usuarios` (creación, paginación, actualización, asignación de roles)
- `Gestión de vuelos` (búsqueda, filtrado por precio, origen/destino, etc.)
- `Gestión de hoteles` (búsqueda, filtrado por precio, valoración, etc.)
- `Gestión de reservas` (creación, actualización, eliminación de reservas)
- `Gestión de tours` (creación de tours a partir de vuelos y hoteles)
- `Caché de Redis` para mejorar el rendimiento
- `Control de acceso basado en roles` (Administrador, Cliente)
- `Documentación de la API de Swagger` para facilitar las pruebas


---
## Estructura del proyecto

- `api/` — Controladores, rutas de la API y DTOs
- `config/` — Configuraciones generales (Swagger, seguridad, etc)
- `domain/` — Entidades y repositorios para la base de datos
- `infrastructure/` — interfaces y lógica de negocio
- `resources/` — Archivos de configuración
- `util/` — CLase de utileria roles, excepciones personalizadas, etc.
- `test/` — Pruebas unitarias

### Arquitectura
El proyecto está diseñado por una arquitectura por capas,
inspirada en los principios de Clean Architecture. Aquí,
las responsabilidades se dividen de manera clara entre los controladores
(API), servicios (lógica de negocio)
, dominio (entidades y repositorios) y configuración.
Esta estructura no solo mejora la mantenibilidad y escalabilidad,
sino que también hace que las pruebas unitarias sean mucho más sencillas de
implementar.

---
##  Instalación local

### 1. Requisitos previos

- Java 17 instalado
- Docker y Docker Compose
- Maven

### 2. Clonar el repositorio
git clone https://github.com/alejandrorivera22/great-travel.git
cd great_travel

### 3. Levantar PostgreSQL y REDIS
docker-compose up -d

### 4. Compilar y correr la aplicación
- ./mvnw clean install
- ./mvnw spring-boot:run

### 5. Accede a la API en:
- http://localhost:8080/great_travel

### 6. Accede a documentación Swagger UI:
- http://localhost:8080/great_travel/swagger-ui/index.html

---
## Usuarios predefinidos para pruebas

Estos usuarios están precargados en la base de datos (`data.sql`)
y permiten simular autenticación y autorización
según los distintos roles disponibles en el sistema.

| Rol      | Username    | Contraseña       |
|----------|-------------|------------------|
| Admin    | `admin`     | `adminpassword`  |
| Customer | `john_doe ` | `password123`    |

> Las contraseñas están encriptadas con BCrypt.
> Se indican aquí solo para prueba en entorno local.

---

### Cómo probar autenticación JWT en Swagger

1. Accede a Swagger UI (`http://localhost:8080/great_travel/swagger-ui/index.html`) en tu navegador.
2. Ve a POST /auth/login y autentícate con alguno de los usuarios mencionados.
3. Copia el token JWT que se encuentra en la propiedad token de la respuesta.
4. Haz clic en el botón **"Authorize"** (el ícono de candado).
5. Pega el token.

##  Autor

**Alejandro Rivera**
- [![LinkedIn](https://img.shields.io/badge/LinkedIn-Connect-blue?logo=linkedin)](https://www.linkedin.com/in/alejandro-rivera-verdayes-443895375/)
- [![GitHub](https://img.shields.io/badge/GitHub-000?style=for-the-badge&logo=github&logoColor=white)](https://github.com/alejandrorivera22)
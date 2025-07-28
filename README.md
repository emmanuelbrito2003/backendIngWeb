# 🏢 Sistema de Empleados - Backend API

API REST desarrollada con Spring Boot para la gestión de empleados con autenticación JWT y roles diferenciados.

## 🚀 Tecnologías

- **Java 17+**
- **Spring Boot 3.4.1**
- **MongoDB Atlas**
- **Spring Security + JWT**
- **Maven**

## ⚙️ Configuración Previa

### Requisitos

- Java 17 o superior
- Maven 3.6+
- Cuenta de MongoDB Atlas

### Variables de Entorno

```properties
# application.properties
spring.data.mongodb.uri=mongodb+srv://usuario:password@cluster.mongodb.net/empleados_db
app.jwt.secret=tuClaveSecreta
app.jwt.expiration=86400000
```

## 🔧 Instalación y Ejecución

### 1. Clonar el proyecto

```bash
git clone https://github.com/emmanuelbrito2003/backendIngWeb
cd empleados-backend
```

### 2. Configurar base de datos

- Crear cluster en MongoDB Atlas
- Actualizar connection string en `application.properties`

### 3. Ejecutar aplicación

```bash
# Instalar dependencias y ejecutar
mvn spring-boot:run

# O compilar y ejecutar JAR
mvn clean package
java -jar target/backend-api-0.0.1-SNAPSHOT.jar
```

La API estará disponible en: **http://localhost:8080**

## 🧪 Ejecutar Pruebas

```bash
mvn test
```

## 📊 Endpoints Principales

### Autenticación

- **POST** `/api/auth/login` - Login

### Empleados (Requiere autenticación)

- **GET** `/api/empleados` - Listar empleados (Solo ADMIN)
- **POST** `/api/empleados` - Crear empleado (Solo ADMIN)
- **PUT** `/api/empleados/{id}` - Actualizar empleado
- **DELETE** `/api/empleados/{id}` - Eliminar empleado (Solo ADMIN)
- **GET** `/api/empleados/{id}` - Obtener empleado por ID



## 👥 Usuarios de Prueba

| Rol | Email | Contraseña |
|-----|-------|------------|
| **Admin** | admin@empresa.com | admin123 |
| **Empleado** | empleado@empresa.com | empleado123 |

## 🏗️ Estructura del Proyecto

```
src/main/java/com/empleados/backend_api/
├── config/          # Configuraciones (JWT, Security)
├── controller/      # Controladores REST  
├── dto/            # Data Transfer Objects
├── model/          # Entidades MongoDB
├── repository/     # Repositorios
├── service/        # Lógica de negocio
└── BackendApiApplication.java
```

## 🔐 Seguridad

- **Autenticación JWT**
- **Roles: ADMIN y EMPLEADO**
- **CORS configurado**

## 📝 Documentación de la API

### Autenticación

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "admin@empresa.com",
  "password": "admin123"
}
```

**Respuesta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "email": "admin@empresa.com",
  "role": "ADMIN"
}
```

### Empleados

#### Crear Empleado (ADMIN)
```http
POST /api/empleados
Authorization: Bearer <token>
Content-Type: application/json

{
  "nombre": "Juan Pérez",
  "email": "juan.perez@empresa.com",
  "password": "password123",
  "rol": "EMPLEADO",
  "departamento": "Desarrollo",
  "salario": 50000
}
```

#### Actualizar Empleado
```http
PUT /api/empleados/{id}
Authorization: Bearer <token>
Content-Type: application/json

{
  "nombre": "Juan Pérez Actualizado",
  "departamento": "QA",
  "salario": 55000
}
```

## 🚀 Despliegue

### Railway
```

### Problemas Comunes

1. **Error de conexión a MongoDB**
   - Verificar connection string
   - Comprobar credenciales
   - Verificar IP whitelist en MongoDB Atlas

2. **Error de JWT**
   - Verificar `app.jwt.secret` en `application.properties`
   - Comprobar formato del token en headers

3. **Error de CORS**
   - Verificar configuración en `WebConfig.java`
   - Comprobar origen de las peticiones



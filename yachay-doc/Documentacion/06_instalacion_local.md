# 06. Instalacion local

## Requisitos

- Java 17 compatible para build local. Java 21 recomendado para entrega formal.
- Node.js compatible con Angular 21.
- npm.
- MySQL 8 o compatible.
- PowerShell en Windows.

## Base de datos

Crear la base:

```sql
CREATE DATABASE IF NOT EXISTS yachay CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

## Configuracion backend

Copiar plantilla local:

```powershell
cd C:\E-specter\yachay\yachay-backend
Copy-Item src\main\resources\application-local.example.yaml src\main\resources\application-local.yaml
```

Editar:

```txt
src/main/resources/application-local.yaml
```

Configurar usuario y password de MySQL local. Este archivo esta ignorado por Git y no debe subirse.

Tambien se recomienda definir un `JWT_SECRET` largo para firmar tokens locales. Spring Boot usa el valor de `application-local.yaml` o las variables de entorno del sistema.

## Ejecutar backend

```powershell
cd C:\E-specter\yachay\yachay-backend
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=local"
```

Build:

```powershell
.\mvnw.cmd clean package -DskipTests "-Djava.version=17"
```

## Ejecutar frontend

```powershell
cd C:\E-specter\yachay\yachay-frontend
npm install
npm start
```

Build:

```powershell
npm run build
```

## Usuarios demo

- Admin: `admin@yachay.edu.pe` / `Admin123456`
- Docente: `docente1@yachay.edu.pe` / `Docente123456`
- Alumno: `alumno1@yachay.edu.pe` / `Alumno123456`

## Pruebas rapidas

Login:

```txt
http://localhost:4200/login
```

API:

```txt
http://localhost:8080/api/admin/postulaciones
```

Los endpoints `/admin/**`, `/docente/**` y `/alumno/**` requieren JWT. Primero iniciar sesion desde el frontend o hacer un `POST /api/auth/login` y enviar `Authorization: Bearer <jwt>`.

XLSX:

```txt
http://localhost:8080/api/admin/reportes/postulaciones.xlsx
```

PDF:

```txt
http://localhost:8080/api/admin/documentos/postulacion/1/pdf
http://localhost:8080/api/admin/documentos/alumno/{id}/pdf
```

El `{id}` de alumno debe existir en `student_profiles`. Si no existe, la API devuelve 404.

## Archivos que no se suben

- `yachay-backend/src/main/resources/application-local.yaml`
- `.env`
- `run-backend.ps1`
- `target/`
- `node_modules/`
- `dist/`
- logs
- credenciales reales

## Problemas comunes

- Si MySQL no esta activo, el backend no conecta.
- Si el puerto 8080 esta ocupado, cerrar el proceso anterior o cambiar `server.port`.
- Si el PDF de alumno devuelve 404, usar un ID real de alumno.
- Si Angular no descarga archivos, revisar CORS, token JWT y permisos de rol en consola.

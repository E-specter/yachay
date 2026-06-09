# Yachay Backend

Backend Spring Boot del campus virtual Yachay. Expone una API REST para autenticacion, gestion academica, reportes XLSX, correo, documentos PDF preparados e integraciones externas controladas.

## Stack

- Java 21 recomendado.
- Maven Wrapper.
- Spring Boot.
- Spring Web MVC.
- Spring Data JPA.
- MySQL.
- Bean Validation.
- Spring Mail.
- Apache POI para exportacion XLSX.

## Arquitectura

El backend forma parte de la arquitectura por N capas del sistema:

```txt
Usuario
  ↓
Capa de Presentacion
Angular 21 + TailwindCSS v4
  ↓
Capa de Seguridad
JWT + Guards en Angular / Spring Security en Backend
  ↓
Capa de Aplicacion
Controllers REST en Spring Boot
  ↓
Capa de Logica de Negocio
Services
  ↓
Capa de Acceso a Datos
Repositories / JPA
  ↓
Capa de Datos
MySQL
```

Dentro del backend, los paquetes separan controllers, servicios, modelos, repositorios y configuracion para mantener responsabilidades claras.

## Configuracion

La configuracion base vive en:

```txt
src/main/resources/application.yaml
```

Ese archivo usa placeholders de Spring Boot para leer variables externas:

```yaml
DB_URL
DB_USERNAME
DB_PASSWORD
JWT_SECRET
JWT_EXPIRATION
MAIL_HOST
MAIL_PORT
MAIL_USERNAME
MAIL_PASSWORD
MAIL_FROM
ILOVEPDF_PUBLIC_KEY
ILOVEPDF_SECRET_KEY
WHATSAPP_ENABLED
WHATSAPP_TOKEN
WHATSAPP_PHONE_NUMBER_ID
```

Spring Boot no carga archivos `.env` automaticamente como Laravel. Para produccion se deben usar variables de entorno del sistema, del panel del servidor, Docker, CI/CD o del proveedor cloud.

## Desarrollo Local

Para trabajar localmente sin exponer credenciales:

1. Copiar la plantilla:

```powershell
Copy-Item src\main\resources\application-local.example.yaml src\main\resources\application-local.yaml
```

2. Editar `src/main/resources/application-local.yaml` con tu usuario y password local de MySQL.

3. Ejecutar el backend con el perfil `local`:

```powershell
cd C:\E-specter\yachay\yachay-backend
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=local"
```

`application-local.yaml` esta ignorado por Git y no debe subirse al repositorio.

## Servidor

En servidor no se usa `application-local.yaml`. Se definen variables de entorno y se ejecuta el jar:

```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/yachay?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Lima"
$env:DB_USERNAME="root"
$env:DB_PASSWORD="PASSWORD_SEGURO"
$env:JWT_SECRET="SECRETO_LARGO_Y_SEGURO"
$env:JWT_EXPIRATION="86400000"
$env:MAIL_HOST="smtp.gmail.com"
$env:MAIL_PORT="587"
$env:MAIL_USERNAME="correo@dominio.com"
$env:MAIL_PASSWORD="APP_PASSWORD"
$env:MAIL_FROM="notificaciones@yachay.edu.pe"
$env:ILOVEPDF_PUBLIC_KEY="PUBLIC_KEY"
$env:ILOVEPDF_SECRET_KEY="SECRET_KEY"
$env:WHATSAPP_ENABLED="false"
$env:WHATSAPP_TOKEN=""
$env:WHATSAPP_PHONE_NUMBER_ID=""

java -jar target\backend-0.0.1-SNAPSHOT.jar
```

En Linux o un servicio cloud, las mismas variables se configuran desde el entorno del proceso.

## MySQL

Crear la base si no existe:

```sql
CREATE DATABASE IF NOT EXISTS yachay CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Revisar tablas:

```sql
USE yachay;
SHOW TABLES;
SELECT COUNT(*) FROM auth_users;
```

La aplicacion usa `spring.jpa.hibernate.ddl-auto=update` para desarrollo. En una version productiva se recomienda migraciones controladas.

## Endpoints Principales

Auth:

- `POST /api/auth/login`
- `POST /api/auth/forgot-password`
- `POST /api/auth/reset-password`

Reportes XLSX:

- `GET /api/admin/reportes/alumnos.xlsx`
- `GET /api/admin/reportes/docentes.xlsx`
- `GET /api/admin/reportes/usuarios.xlsx`
- `GET /api/admin/reportes/cursos.xlsx`
- `GET /api/admin/reportes/postulaciones.xlsx`
- `GET /api/admin/reportes/notas.xlsx`

Correo:

- `POST /api/admin/notificaciones/email/test`

WhatsApp mock:

- `POST /api/admin/notificaciones/whatsapp/test`

Documentos PDF preparados:

- `POST /api/admin/documentos/postulacion/{id}/pdf`
- `POST /api/admin/documentos/alumno/{id}/pdf`

## Correo

El servicio de correo usa `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD` y `MAIL_FROM`. Si usuario o password estan vacios, responde con un mensaje controlado y no detiene el backend.

## I Love PDF

La integracion queda preparada con `ILOVEPDF_PUBLIC_KEY` e `ILOVEPDF_SECRET_KEY`. Si faltan, los endpoints responden con un mensaje controlado indicando que la integracion no esta configurada.

## WhatsApp Mock

`WHATSAPP_ENABLED=false` mantiene la integracion en modo simulado. Si se activa, se deben configurar `WHATSAPP_TOKEN` y `WHATSAPP_PHONE_NUMBER_ID`.

## Build

```powershell
cd C:\E-specter\yachay\yachay-backend
.\mvnw.cmd clean package -DskipTests "-Djava.version=17"
```

Para entrega formal se recomienda compilar y ejecutar con JDK 21.

## Archivos Que No Deben Subirse

- `src/main/resources/application-local.yaml`
- `.env`
- `run-backend.ps1`
- `target/`
- `*.log`
- cualquier archivo con passwords, tokens, JWT secrets o API keys reales

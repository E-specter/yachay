# Yachay

Yachay es un campus virtual para el Colegio Manuel Gonzales Prada. El proyecto se organiza en frontend Angular, backend Spring Boot y documentacion tecnica para entregas academicas.

## Estructura

```txt
C:\E-specter\yachay
|-- yachay-frontend   Angular 21 + TailwindCSS v4
|-- yachay-backend    Spring Boot + JPA + MySQL
|-- yachay-doc        Documentacion, PlantUML y guias de entrega
`-- yachay-db         Recursos locales de base de datos si se requieren
```

## Arquitectura oficial

```txt
Usuario
  |
  v
Capa de Presentacion
Angular 21 + TailwindCSS v4
  |
  v
Capa de Seguridad
JWT + Guards en Angular / Spring Security en Backend
  |
  v
Capa de Aplicacion
Controllers REST en Spring Boot
  |
  v
Capa de Logica de Negocio
Services
  |
  v
Capa de Acceso a Datos
Repositories / JPA
  |
  v
Capa de Datos
MySQL
```

MySQL es la base oficial del proyecto. No se usa SQLite en esta fase.

## Como iniciar MySQL y preparar la base de datos

Verificar MySQL:

```powershell
mysql --version
```

Iniciar MySQL en Windows si esta instalado como servicio:

```powershell
net start MySQL80
```

Si el servicio tiene otro nombre, revisar `services.msc`.

Ingresar a MySQL:

```powershell
mysql -u root -p
```

Crear la base manualmente, aunque el backend tambien puede crearla:

```sql
CREATE DATABASE IF NOT EXISTS yachay CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
SHOW DATABASES;
USE yachay;
```

Levantar backend con variables en PowerShell:

```powershell
cd C:\E-specter\yachay\yachay-backend
$env:DB_URL="jdbc:mysql://localhost:3306/yachay?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Lima"
$env:DB_USERNAME="root"
$env:DB_PASSWORD="TU_PASSWORD_MYSQL"
.\mvnw.cmd spring-boot:run
```

Al iniciar, Hibernate crea o actualiza tablas y el seeder carga datos iniciales si no existen.

Revisar tablas y conteos:

```sql
SHOW TABLES;
SELECT COUNT(*) FROM usuarios;
SELECT COUNT(*) FROM alumnos;
SELECT COUNT(*) FROM docentes;
SELECT COUNT(*) FROM cursos;
```

Los nombres reales de tablas pueden variar segun las entidades presentes en el backend actual.

## Credenciales de prueba

- `admin@yachay.edu.pe` / `Admin123456`
- `docente1@yachay.edu.pe` / `Docente123456`
- `alumno1@yachay.edu.pe` / `Alumno123456`

## Ejecutar frontend

```powershell
cd C:\E-specter\yachay\yachay-frontend
npm install
npm start
```

## Ejecutar backend

```powershell
cd C:\E-specter\yachay\yachay-backend
.\mvnw.cmd spring-boot:run
```

## Avance 3

El Avance 3 agrega configuracion por variables de entorno, correo SMTP, reportes XLSX, preparacion de documentos PDF con I Love PDF y mock controlado de WhatsApp.

La guia tecnica de APIs esta en [yachay-doc/apis-avance-3.md](yachay-doc/apis-avance-3.md).

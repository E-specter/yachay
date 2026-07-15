# Yachay

Yachay es un campus virtual para el Colegio Manuel Gonzales Prada. El proyecto integra frontend Angular, backend Spring Boot, MySQL, administracion academica, admision escolar, reportes XLSX y generacion local de PDF.

## Estructura

```txt
C:\E-specter\yachay
|-- yachay-frontend   Angular 21 + TailwindCSS v4
|-- yachay-backend    Spring Boot + JPA + MySQL
|-- yachay-doc        Documentacion oficial, diagramas y material tecnico
|-- yachay-db         Recursos locales de base de datos
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
JWT firmado en backend / Guards e interceptor en Angular
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

MySQL es la base oficial de Avance 3.

## Tecnologias

- Angular 21.
- TailwindCSS v4.
- Spring Boot 4.
- JPA / Hibernate.
- MySQL.
- Apache POI para XLSX.
- OpenPDF para PDF local.
- BCrypt para passwords.
- JJWT para tokens firmados.

## Ejecutar backend

```powershell
cd yachay-backend
Copy-Item src\main\resources\application-local.example.yaml src\main\resources\application-local.yaml
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=local"
```

`application-local.yaml` debe contener las credenciales locales de MySQL y no debe subirse a Git.

## Ejecutar frontend

```powershell
cd yachay-frontend
npm install
npm start
```

## Credenciales demo

- `admin@yachay.edu.pe` / `Admin123456`
- `docente1@yachay.edu.pe` / `Docente123456`
- `alumno1@yachay.edu.pe` / `Alumno123456`

## Funcionalidades Avance 3

- Login real con usuarios en MySQL.
- DataSeeder idempotente.
- Roles de administrador, docente, alumno y apoderado.
- Panel administrativo.
- CRUD administrativo principal.
- Postulaciones reales.
- Reportes XLSX reales.
- PDF local de alumno y postulacion.
- Correo SMTP y WhatsApp en modo controlado.

## Documentacion tecnica

La documentacion oficial vive en [yachay-doc](yachay-doc/README.md).

Documentos principales:

- [Arquitectura general](yachay-doc/Documentacion/01_arquitectura_general.md)
- [Backend arquitectura](yachay-doc/Documentacion/02_backend_arquitectura.md)
- [Frontend arquitectura](yachay-doc/Documentacion/03_frontend_arquitectura.md)
- [Base de datos](yachay-doc/Documentacion/04_base_de_datos.md)
- [Endpoints API](yachay-doc/Documentacion/05_endpoints_api.md)
- [Seguridad JWT](yachay-doc/Documentacion/08_seguridad_jwt.md)
- [Reportes y PDF](yachay-doc/Documentacion/09_reportes_y_pdf.md)

## Builds

Backend:

```powershell
cd C:\E-specter\yachay\yachay-backend
.\mvnw.cmd clean package -DskipTests "-Djava.version=17"
```

Frontend:

```powershell
cd C:\E-specter\yachay\yachay-frontend
npm run build
```

## Seguridad de archivos

No subir:

- `yachay-backend/src/main/resources/application-local.yaml`
- `.env`
- tokens
- passwords reales
- logs
- `target/`
- `node_modules/`
- `dist/`

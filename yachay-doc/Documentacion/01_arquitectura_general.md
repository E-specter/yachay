# 01. Arquitectura general

## Proposito

Yachay es un campus virtual para el Colegio Manuel Gonzales Prada. El sistema cubre autenticacion, admision escolar, administracion academica, gestion de alumnos, docentes, cursos, secciones, tareas, notas, comunicados, calendario academico, notificaciones persistidas, reportes XLSX y generacion local de PDF.

## Arquitectura por N capas

```txt
Usuario
  |
  v
Capa de Presentacion
Angular 21 + TailwindCSS v4
  |
  v
Capa de Seguridad
JWT firmado en backend / Guards e interceptor JWT en Angular
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

La arquitectura oficial usa MySQL como capa de datos del Avance 3.

## Componentes principales

- Frontend: Angular 21, TailwindCSS v4, rutas lazy, componentes standalone, SSR y zoneless.
- Backend: Spring Boot, JPA, MySQL, Maven Wrapper, validacion, correo, XLSX y PDF local.
- Seguridad: Spring Security valida JWT firmado y aplica permisos por rol.
- Base de datos: MySQL con tablas de identidad, admision y dominio academico.
- Reportes: Apache POI genera XLSX desde datos reales.
- PDF: OpenPDF genera fichas de alumno y postulacion desde MySQL.
- Calendario: eventos academicos filtrados por rol y curso.
- Notificaciones: avisos persistidos por usuario, con campana en frontend.
- Integraciones: correo SMTP y WhatsApp en modo controlado. I Love PDF queda como integracion futura, no como dependencia del PDF basico.

## Modulos funcionales

- Auth: login real contra usuarios en MySQL.
- Identity: usuarios, roles, perfiles, alumnos, docentes y colegio.
- Admissions: postulaciones reales y decisiones de administracion.
- Academic: materias, cursos, secciones, tareas, notas, comunicados y calendario/eventos academicos.
- Report: reportes XLSX administrativos.
- Document: documentos PDF locales.
- Notification: notificaciones persistidas, correo SMTP y WhatsApp en modo controlado.

## Estado Avance 3

Funciona:

- Login real con credenciales seed.
- JWT firmado validado por backend.
- Panel admin con datos desde API.
- CRUD administrativo principal.
- Postulaciones reales.
- Reportes XLSX.
- PDF local para alumno y postulacion.
- Calendario academico por rol.
- Campana y bandeja de notificaciones persistidas.
- Build backend y frontend.

Pendiente tecnico:

- Mover logica de algunos controllers admin a services de aplicacion.
- Completar endpoints reales para dashboards docente/alumno.

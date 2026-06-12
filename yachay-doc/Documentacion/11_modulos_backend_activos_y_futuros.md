# 11. Modulos backend activos y futuros

## Objetivo

Este documento aclara que paquetes forman parte productiva del backend y que modulos quedan como alcance futuro. La documentacion oficial vive en `yachay-doc`; el arbol `src/main/java` debe contener codigo Java productivo.

## Paquetes productivos

- `auth`: login, BCrypt, JWT firmado y filtro de autenticacion.
- `config`: seguridad, CORS y DataSeeder.
- `identity`: usuarios, roles, perfiles, colegio y endpoints de identidad.
- `academic`: materias, cursos, secciones, tareas, notas, comunicados y calendario.
- `admissions`: postulaciones y decisiones administrativas.
- `document`: generacion local de PDF con OpenPDF e integracion futura con I Love PDF.
- `report`: exportacion XLSX con Apache POI.
- `notification`: notificaciones persistidas, correo SMTP y WhatsApp controlado.

## Paquetes documentales retirados del codigo

Estos paquetes existian solo como carpetas con `readme.md` vacio, sin clases Java ni referencias desde el codigo:

- `attendance`
- `calendar`
- `common`
- `communication`
- `contents`
- `notifications`
- `resources`
- `shared`

Se retiraron del arbol Java para evitar confundir paquetes activos con alcance futuro.

## Alcance futuro documentado

- Asistencia.
- Contenidos academicos.
- Recursos educativos.
- Mensajeria y comunicacion avanzada.
- Servicios compartidos transversales mas formales.

Cuando alguno de esos modulos entre al alcance real, debe crearse con entidades, DTOs, services, controllers y repositories correspondientes.

## Nota sobre identity

`identity` usa una estructura mas cercana a arquitectura hexagonal:

- `domain`: entidades, repositorios y excepciones.
- `application`: DTOs, ports y services.
- `infrastructure`: controllers/adapters REST y configuracion.

El sistema completo no es hexagonal puro. La arquitectura general del proyecto es por N capas, con principios hexagonales aplicados principalmente en `identity`.

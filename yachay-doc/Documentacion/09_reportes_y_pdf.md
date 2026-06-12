# 09. Reportes XLSX y PDF

## Reportes XLSX

Yachay genera reportes administrativos en formato Excel usando Apache POI. Los reportes leen datos reales desde MySQL y se descargan desde el panel administrador con JWT.

Endpoints principales:

- `GET /api/admin/reportes/alumnos.xlsx`
- `GET /api/admin/reportes/docentes.xlsx`
- `GET /api/admin/reportes/usuarios.xlsx`
- `GET /api/admin/reportes/cursos.xlsx`
- `GET /api/admin/reportes/postulaciones.xlsx`
- `GET /api/admin/reportes/notas.xlsx`

Componentes:

- Angular `ReportService`.
- Backend `ReportController`.
- Backend `ExcelReportService`.
- Repositories JPA de usuarios, alumnos, docentes, cursos, postulaciones y notas.

## PDF local

Yachay genera documentos PDF reales en backend usando OpenPDF. La generacion basica no depende de servicios externos.

Endpoints:

- `GET /api/admin/documentos/alumno/{id}/pdf`
- `GET /api/admin/documentos/postulacion/{id}/pdf`

Contenido de ficha de alumno:

- Encabezado institucional: Yachay Campus Virtual y Colegio Manuel Gonzales Prada.
- Seccion de datos personales.
- Seccion de datos academicos.
- Seccion de estado del registro.
- Tablas de dos columnas `Campo | Valor`.
- Pie de pagina con fecha/hora de generacion y nota informativa.

Contenido de ficha de postulacion:

- Encabezado institucional: Yachay Campus Virtual y Colegio Manuel Gonzales Prada.
- Seccion de datos del postulante.
- Seccion de datos del apoderado.
- Seccion de informacion de admision.
- Tablas de dos columnas `Campo | Valor`.
- Pie de pagina con fecha/hora de generacion y nota informativa.

## Implementacion PDF

`DocumentService` mantiene una implementacion simple con metodos privados para:

- encabezado del documento
- secciones
- tablas campo/valor
- filas
- pie de pagina

Esto permite explicar el codigo con claridad durante la exposicion sin introducir nuevas librerias ni patrones complejos.

## Seguridad

Los endpoints XLSX y PDF estan bajo `/api/admin/**`, por lo tanto requieren:

```txt
Authorization: Bearer <jwt>
```

Solo usuarios con rol `ADMINISTRADOR` pueden generar o descargar estos archivos.

## Integraciones futuras

I Love PDF queda como integracion futura para procesos avanzados de documentos. La generacion base de fichas del Avance 3 se hace localmente con OpenPDF.

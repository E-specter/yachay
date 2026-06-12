# Yachay Doc

Documentacion oficial del proyecto Yachay para el Avance 3. Esta carpeta concentra los documentos tecnicos, diagramas y materiales de exposicion del campus virtual del Colegio Manuel Gonzales Prada.

## Organizacion

```txt
yachay-doc
|-- README.md
|-- apis-avance-3.md
|-- Documentacion
|   |-- 01_arquitectura_general.md
|   |-- 02_backend_arquitectura.md
|   |-- 03_frontend_arquitectura.md
|   |-- 04_base_de_datos.md
|   |-- 05_endpoints_api.md
|   |-- 06_instalacion_local.md
|   |-- 07_revision_tecnica_avance_3.md
|   |-- 08_seguridad_jwt.md
|   |-- 09_reportes_y_pdf.md
|   |-- 10_siguiente_fase_calendario_notificaciones.md
|   `-- 11_modulos_backend_activos_y_futuros.md
|-- Diagramas
|   |-- arquitectura.drawio
|   `-- arquitectura_avance_3.drawio
`-- Bloc
    `-- Bosquejos.excalidraw.png
```

## Documentos principales

- `01_arquitectura_general.md`: vision por N capas del sistema.
- `02_backend_arquitectura.md`: paquetes, patrones, entidades, repositorios, services y seguridad.
- `03_frontend_arquitectura.md`: estructura Angular, layouts, guards, interceptor y services.
- `04_base_de_datos.md`: tablas principales de MySQL y relaciones.
- `05_endpoints_api.md`: endpoints reales del Avance 3.
- `06_instalacion_local.md`: pasos para ejecutar backend y frontend.
- `07_revision_tecnica_avance_3.md`: hallazgos positivos, pendientes y recomendaciones.
- `08_seguridad_jwt.md`: JWT firmado, claims, rutas protegidas y permisos por rol.
- `09_reportes_y_pdf.md`: XLSX con Apache POI y PDF local con OpenPDF.
- `10_siguiente_fase_calendario_notificaciones.md`: calendario y notificaciones reales por rol.
- `11_modulos_backend_activos_y_futuros.md`: paquetes productivos y modulos futuros retirados del arbol Java.

## Diagramas

- `Diagramas/arquitectura_avance_3.drawio`: diagrama oficial actualizado para la exposicion.
- `Diagramas/arquitectura.drawio`: diagrama previo conservado como referencia historica.

## Estado Avance 3

El sistema documentado incluye:

- Frontend Angular 21 con TailwindCSS v4.
- Backend Spring Boot con JPA y MySQL.
- Login real con BCrypt.
- JWT firmado y validado por backend.
- Guards e interceptor JWT en Angular.
- Panel administrativo con CRUD principal.
- Postulaciones reales.
- Reportes XLSX reales.
- PDF local real con OpenPDF.
- Calendario academico real por rol.
- Notificaciones persistidas por usuario.
- DataSeeder idempotente.

## Pendientes recomendados

- Extraer logica administrativa desde controladores hacia services de aplicacion.
- Completar endpoints reales para dashboards docente/alumno.
- Agregar Flyway o Liquibase para migraciones de base de datos.
- Completar endpoints reales para dashboards docente/alumno.
- Extraer logica administrativa restante desde controladores hacia services.

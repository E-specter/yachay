# 10. Calendario y notificaciones

## Objetivo

Esta fase convierte las vistas visuales de calendario y notificaciones en funcionalidades conectadas a MySQL, filtradas por rol y protegidas con JWT.

## Calendario academico

Estado implementado:

- El backend expone calendario para administrador, docente y alumno.
- El administrador puede crear eventos desde `/admin/calendario`.
- El docente y el alumno consultan eventos de solo lectura.
- Los eventos se filtran por rol, curso y matricula cuando corresponde.
- La creacion de un evento puede generar notificaciones para el publico objetivo.

Endpoints:

- `GET /api/admin/calendario`
- `GET /api/admin/calendario/semana`
- `GET /api/admin/calendario/mes`
- `POST /api/admin/calendario`
- `GET /api/docente/calendario`
- `GET /api/docente/calendario/semana`
- `GET /api/docente/calendario/mes`
- `GET /api/alumno/calendario`
- `GET /api/alumno/calendario/semana`
- `GET /api/alumno/calendario/mes`

Reglas:

- Admin ve todos los eventos.
- Docente ve eventos generales, eventos para docentes y eventos asociados a sus cursos.
- Alumno ve eventos generales, eventos para alumnos y eventos asociados a sus cursos matriculados.

## Notificaciones

Estado implementado:

- Existe tabla `yachay_notifications`.
- Cada notificacion pertenece a un usuario.
- La campana del layout carga notificaciones reales.
- Se puede marcar una notificacion como leida o marcar todas.
- El DataSeeder crea notificaciones iniciales por rol sin duplicar.
- La creacion de tareas, notas, comunicados, decisiones de admision y eventos registra avisos reales cuando hay destinatarios.

Endpoints:

- `GET /api/admin/notificaciones`
- `PATCH /api/admin/notificaciones/{id}/leido`
- `PATCH /api/admin/notificaciones/leidas`
- `GET /api/docente/notificaciones`
- `PATCH /api/docente/notificaciones/{id}/leido`
- `PATCH /api/docente/notificaciones/leidas`
- `GET /api/alumno/notificaciones`
- `PATCH /api/alumno/notificaciones/{id}/leido`
- `PATCH /api/alumno/notificaciones/leidas`

Correo SMTP y WhatsApp se mantienen como canales adicionales configurables.

## Frontend

- `/admin/calendario`: listado de eventos y formulario de creacion.
- `/docente/calendario`: agenda docente de solo lectura.
- `/alumno/calendario`: agenda del alumno de solo lectura.
- `/admin/notificaciones`: bandeja administrativa e integraciones controladas.
- `/docente/notificaciones`: bandeja docente.
- `/alumno/notificaciones`: bandeja del alumno.

Los layouts por rol cargan notificaciones reales en la campana superior. El contador se calcula con notificaciones no leidas.

## Mejoras tecnicas siguientes

- Mover logica administrativa desde controladores a services.
- Agregar Flyway o Liquibase para versionar cambios de base de datos.
- Agregar pruebas automatizadas para seguridad JWT, reportes, documentos, calendario y notificaciones.
- Completar endpoints reales para dashboard docente y alumno.
- Consolidar paquetes documentales o futuros dentro de la arquitectura real.

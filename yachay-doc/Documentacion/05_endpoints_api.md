# 05. Endpoints API

Base local:

```txt
http://localhost:8080/api
```

## Auth

- `POST /auth/login`
- `POST /auth/forgot-password`
- `POST /auth/reset-password`

El login devuelve un JWT firmado y datos basicos del usuario:

```json
{
  "token": "header.payload.signature",
  "user": {
    "id": 1,
    "email": "admin@yachay.edu.pe",
    "nombres": "Administrador",
    "apellidos": "Yachay",
    "role": "ADMINISTRADOR",
    "displayName": "Administrador Yachay",
    "roles": ["ADMINISTRADOR"]
  }
}
```

## Seguridad de endpoints

- Publicos: `POST /auth/login`, `POST /auth/forgot-password`, `POST /auth/reset-password`, `POST /admisiones`.
- Protegidos con JWT: todos los endpoints bajo `/admin/**`, `/docente/**` y `/alumno/**`.
- Permisos: `/admin/**` requiere `ADMINISTRADOR`, `/docente/**` requiere `DOCENTE`, `/alumno/**` requiere `ALUMNO`.

Para consumir endpoints protegidos se debe enviar:

```txt
Authorization: Bearer <jwt>
```

## Admin - usuarios

- `GET /admin/usuarios`
- `POST /admin/usuarios`
- `PATCH /admin/usuarios/{id}/estado`
- `PATCH /admin/usuarios/{id}/reset-password`

## Admin - alumnos

- `GET /admin/alumnos`
- `GET /admin/alumnos/{id}`
- `POST /admin/alumnos`
- `PATCH /admin/alumnos/{id}/estado`

## Admin - docentes

- `GET /admin/docentes`
- `GET /admin/docentes/{id}`
- `POST /admin/docentes`
- `PATCH /admin/docentes/{id}/estado`

## Admin - cursos

- `GET /admin/cursos`
- `GET /admin/cursos/{id}`
- `POST /admin/cursos`
- `PATCH /admin/cursos/{id}/estado`

Auxiliares:

- `GET /admin/materias`
- `GET /admin/anios-academicos`

## Admin - secciones

- `GET /admin/secciones`
- `POST /admin/secciones`
- `PATCH /admin/secciones/{id}/estado`

## Admin - tareas

- `GET /admin/tareas`
- `POST /admin/tareas`
- `PATCH /admin/tareas/{id}/estado`

## Admin - notas

- `GET /admin/notas`
- `POST /admin/notas`
- `PATCH /admin/notas/{id}/estado`

## Admin - comunicados

- `GET /admin/comunicados`
- `POST /admin/comunicados`
- `PATCH /admin/comunicados/{id}/estado`

## Calendario academico

Admin:

- `GET /admin/calendario`
- `GET /admin/calendario/semana`
- `GET /admin/calendario/mes`
- `POST /admin/calendario`

Docente:

- `GET /docente/calendario`
- `GET /docente/calendario/semana`
- `GET /docente/calendario/mes`

Alumno:

- `GET /alumno/calendario`
- `GET /alumno/calendario/semana`
- `GET /alumno/calendario/mes`

Reglas:

- Admin ve todos los eventos.
- Docente ve eventos generales, eventos para docentes y eventos asociados a sus cursos.
- Alumno ve eventos generales, eventos para alumnos y eventos asociados a sus cursos matriculados.
- `POST /admin/calendario` crea el evento y genera notificaciones segun curso o publico objetivo.

## Admin - postulaciones

- `GET /admin/postulaciones`
- `GET /admin/postulaciones/{id}`
- `PATCH /admin/postulaciones/{id}/aceptar`
- `PATCH /admin/postulaciones/{id}/rechazar`

## Publico - admisiones

- `POST /admisiones`

## Reportes XLSX

- `GET /admin/reportes/alumnos.xlsx`
- `GET /admin/reportes/docentes.xlsx`
- `GET /admin/reportes/usuarios.xlsx`
- `GET /admin/reportes/cursos.xlsx`
- `GET /admin/reportes/postulaciones.xlsx`
- `GET /admin/reportes/notas.xlsx`

## Documentos PDF

- `GET /admin/documentos/alumno/{id}/pdf`
- `GET /admin/documentos/postulacion/{id}/pdf`

Los endpoints GET devuelven `application/pdf` con descarga.

Compatibilidad I Love PDF futura:

- `POST /admin/documentos/alumno/{id}/pdf`
- `POST /admin/documentos/postulacion/{id}/pdf`

## Notificaciones

- `GET /admin/notificaciones`
- `PATCH /admin/notificaciones/{id}/leido`
- `PATCH /admin/notificaciones/leidas`
- `GET /docente/notificaciones`
- `PATCH /docente/notificaciones/{id}/leido`
- `PATCH /docente/notificaciones/leidas`
- `GET /alumno/notificaciones`
- `PATCH /alumno/notificaciones/{id}/leido`
- `PATCH /alumno/notificaciones/leidas`
- `POST /admin/notificaciones/email/test`
- `POST /admin/notificaciones/whatsapp/test`

Cada usuario solo recibe sus propias notificaciones. El frontend usa estos endpoints para la campana del header y las paginas `/admin/notificaciones`, `/docente/notificaciones` y `/alumno/notificaciones`.

WhatsApp se mantiene en modo controlado salvo que se configuren credenciales reales.

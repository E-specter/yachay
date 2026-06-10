# APIs Avance 3

Este documento resume las APIs e integraciones reales del Avance 3 de Yachay.

## Base local

```txt
http://localhost:8080/api
```

## Configuracion

El backend usa configuracion externa estandar de Spring Boot mediante `application.yaml`, `application-local.yaml` y variables de entorno.

Variables principales:

```txt
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

Spring Boot no carga `.env` automaticamente. En desarrollo local se usa `application-local.yaml`; en servidor se usan variables de entorno.

## Auth y seguridad

Login:

```txt
POST /api/auth/login
```

El login devuelve un JWT firmado con JJWT. Claims principales:

- `sub`
- `userId`
- `roles`
- `iat`
- `exp`

Rutas protegidas:

- `/api/admin/**` requiere `ADMINISTRADOR`.
- `/api/docente/**` requiere `DOCENTE`.
- `/api/alumno/**` requiere `ALUMNO`.

Los endpoints protegidos requieren:

```txt
Authorization: Bearer <jwt>
```

## MySQL

Base oficial:

```sql
CREATE DATABASE IF NOT EXISTS yachay CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

El DataSeeder crea datos iniciales idempotentes:

- roles
- colegio
- usuarios demo
- docentes
- alumnos
- materias
- cursos
- secciones
- tareas
- notas
- comunicados
- eventos de calendario
- postulaciones

## Reportes XLSX

Los reportes usan Apache POI y leen datos reales desde MySQL.

```txt
GET /api/admin/reportes/alumnos.xlsx
GET /api/admin/reportes/docentes.xlsx
GET /api/admin/reportes/usuarios.xlsx
GET /api/admin/reportes/cursos.xlsx
GET /api/admin/reportes/postulaciones.xlsx
GET /api/admin/reportes/notas.xlsx
```

Estos endpoints estan protegidos por JWT y rol `ADMINISTRADOR`.

## Calendario academico

El calendario usa eventos persistidos en MySQL y filtra la respuesta segun usuario autenticado.

```txt
GET /api/admin/calendario
GET /api/admin/calendario/semana
GET /api/admin/calendario/mes
POST /api/admin/calendario

GET /api/docente/calendario
GET /api/docente/calendario/semana
GET /api/docente/calendario/mes

GET /api/alumno/calendario
GET /api/alumno/calendario/semana
GET /api/alumno/calendario/mes
```

Admin ve todos los eventos, docente ve eventos generales y de sus cursos, y alumno ve eventos generales y de sus cursos matriculados.

## Notificaciones persistidas

Las notificaciones se guardan en `yachay_notifications` y cada usuario solo consulta sus propios avisos.

```txt
GET /api/admin/notificaciones
PATCH /api/admin/notificaciones/{id}/leido
PATCH /api/admin/notificaciones/leidas

GET /api/docente/notificaciones
PATCH /api/docente/notificaciones/{id}/leido
PATCH /api/docente/notificaciones/leidas

GET /api/alumno/notificaciones
PATCH /api/alumno/notificaciones/{id}/leido
PATCH /api/alumno/notificaciones/leidas
```

El frontend consume estos endpoints desde la campana del header y desde las paginas completas de notificaciones por rol.

## PDF local

Los documentos PDF se generan localmente con OpenPDF.

```txt
GET /api/admin/documentos/alumno/{id}/pdf
GET /api/admin/documentos/postulacion/{id}/pdf
```

El PDF de alumno usa datos reales del perfil del estudiante. El PDF de postulacion usa datos reales de `yachay_admission_applications`.

I Love PDF queda como integracion futura para procesos avanzados, pero no es necesario para generar las fichas basicas del Avance 3.

## Correo

Endpoint de prueba controlada:

```txt
POST /api/admin/notificaciones/email/test
```

Payload:

```json
{
  "to": "correo@ejemplo.com",
  "subject": "Prueba Yachay",
  "message": "Este es un correo de prueba del sistema."
}
```

Si falta configuracion SMTP, el backend responde con mensaje controlado.

## WhatsApp en modo controlado

Endpoint:

```txt
POST /api/admin/notificaciones/whatsapp/test
```

Payload:

```json
{
  "to": "+51999999999",
  "message": "Mensaje de prueba Yachay"
}
```

Si `WHATSAPP_ENABLED=false`, el backend responde que el canal esta desactivado por configuracion.

## Flujo recomendado para demostracion

1. Ejecutar MySQL y backend con perfil local.
2. Iniciar sesion como administrador.
3. Verificar token JWT real.
4. Entrar al panel administrador.
5. Consultar postulaciones reales.
6. Descargar `postulaciones.xlsx`.
7. Descargar una ficha PDF de postulacion.
8. Descargar una ficha PDF de alumno.
9. Crear un evento de calendario como administrador.
10. Revisar la campana de notificaciones por rol.
11. Mostrar permisos por rol: alumno o docente no acceden a `/api/admin/**`.

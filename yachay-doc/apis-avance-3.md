# APIs Avance 3

Este documento resume las integraciones preparadas para el Avance 3 de Yachay.

## 1. Variables de entorno

El backend usa variables de entorno para evitar credenciales en codigo.

```env
DB_HOST=localhost
DB_PORT=3306
DB_NAME=yachay
DB_USERNAME=root
DB_PASSWORD=
DB_URL=jdbc:mysql://localhost:3306/yachay?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Lima

JWT_SECRET=change_me_in_local
JWT_EXPIRATION=86400000

MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=
MAIL_PASSWORD=
MAIL_FROM=notificaciones@yachay.edu.pe

ILOVEPDF_PUBLIC_KEY=
ILOVEPDF_SECRET_KEY=

WHATSAPP_ENABLED=false
WHATSAPP_TOKEN=
WHATSAPP_PHONE_NUMBER_ID=
```

Spring Boot no carga automaticamente `.env`; se recomienda definir variables en PowerShell o en el IDE.

## 2. MySQL

```powershell
mysql --version
net start MySQL80
mysql -u root -p
```

```sql
CREATE DATABASE IF NOT EXISTS yachay CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
SHOW DATABASES;
USE yachay;
SHOW TABLES;
```

```powershell
cd C:\E-specter\yachay\yachay-backend
$env:DB_URL="jdbc:mysql://localhost:3306/yachay?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Lima"
$env:DB_USERNAME="root"
$env:DB_PASSWORD="TU_PASSWORD_MYSQL"
.\mvnw.cmd spring-boot:run
```

## 3. Correo

Endpoint:

```txt
POST /api/admin/notificaciones/email/test
```

Payload:

```json
{
  "to": "correo@ejemplo.com",
  "subject": "Prueba Yachay",
  "message": "Este es un correo de prueba del campus virtual."
}
```

Si faltan `MAIL_USERNAME` o `MAIL_PASSWORD`, el backend responde con mensaje controlado.

## 4. Reportes XLSX

```txt
GET /api/admin/reportes/alumnos.xlsx
GET /api/admin/reportes/docentes.xlsx
GET /api/admin/reportes/usuarios.xlsx
GET /api/admin/reportes/cursos.xlsx
GET /api/admin/reportes/postulaciones.xlsx
GET /api/admin/reportes/notas.xlsx
```

Estado:

- Alumnos: datos reales desde `StudentProfileRepository`.
- Docentes: datos reales desde `TeacherProfileRepository`.
- Usuarios: datos reales desde `UserRepository`.
- Cursos: salida preparada hasta completar entidad/repositorio academico.
- Postulaciones: salida preparada hasta completar entidad/repositorio de admisiones.
- Notas: salida preparada hasta completar entidad/repositorio de calificaciones.

## 5. I Love PDF

```txt
POST /api/admin/documentos/postulacion/{id}/pdf
POST /api/admin/documentos/alumno/{id}/pdf
```

Si faltan `ILOVEPDF_PUBLIC_KEY` o `ILOVEPDF_SECRET_KEY`, el backend responde: `I Love PDF no esta configurado. Configure las llaves en .env.`

## 6. WhatsApp mock

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

Si `WHATSAPP_ENABLED=false`, devuelve: `WhatsApp desactivado por configuracion.`

## 7. Seguridad

Las variables sensibles quedan fuera del codigo. El frontend mantiene `authGuard`, `roleGuard` e interceptor JWT. Los endpoints administrativos estan preparados para ser consumidos desde el panel administrador.

## 8. Que mostrar en el video

1. MySQL activo y base `yachay`.
2. Backend levantando con variables de entorno.
3. Login admin.
4. Descarga de `alumnos.xlsx`.
5. Vista `/admin/notificaciones`.
6. Prueba de correo con respuesta controlada.
7. Prueba de WhatsApp mock.
8. Boton `Ficha PDF` o `Generar PDF` mostrando respuesta de I Love PDF no configurado.

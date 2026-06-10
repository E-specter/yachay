# 02. Arquitectura backend

## Stack

- Java 17 compatible para build local y Java 21 recomendado.
- Spring Boot 4.
- Spring Web MVC.
- Spring Data JPA.
- MySQL.
- Bean Validation.
- Spring Mail.
- Apache POI para XLSX.
- OpenPDF para PDF local.

## Paquetes principales

```txt
edu.yachay.backend
|-- auth
|-- identity
|-- academic
|-- admissions
|-- notification
|-- document
|-- report
|-- config
`-- BackendApplication.java
```

## Responsabilidades

- `auth`: login, DTOs de autenticacion, generacion/validacion JWT y validacion de password contra MySQL.
- `identity`: usuarios, roles, colegio, perfiles, alumnos, docentes y controladores de identidad.
- `academic`: cursos, materias, secciones, tareas, notas, comunicados, calendario real por rol y endpoints admin academicos.
- `admissions`: postulaciones, DTOs y endpoints de aceptacion/rechazo.
- `document`: generacion de PDF local e integracion futura con I Love PDF.
- `report`: exportacion XLSX.
- `notification`: notificaciones persistidas por usuario, correo SMTP y WhatsApp en modo controlado.
- `config`: CORS, Spring Security, DataSeeder y reglas de autorizacion.

## Capas y patrones

- Controller Pattern: controladores REST exponen endpoints HTTP.
- DTO Pattern: los endpoints usan requests/responses en lugar de exponer entidades cuando aplica.
- Repository Pattern: Spring Data JPA concentra acceso a datos.
- Service Layer: presente en auth, identity, document, notification y report.
- Dependency Injection: constructores inyectan repositorios y servicios.
- Global Exception Handler: centraliza respuestas de error.
- Data Seeder idempotente: carga roles, usuarios demo, colegio, datos academicos y postulaciones sin duplicar.

## Entidades relevantes

Identidad:

- `User`
- `Role`
- `Profile`
- `StudentProfile`
- `TeacherProfile`
- `GuardianProfile`
- `School`

Academico:

- `AcademicYear`
- `Subject`
- `Course`
- `SchoolSection`
- `Enrollment`
- `AcademicTask`
- `GradeRecord`
- `Announcement`
- `CalendarEvent`
- `Notification`

Admision:

- `AdmissionApplication`

## Repositorios

Los repositorios viven principalmente en:

- `identity/domain/repositories`
- `academic/domain/repositories`
- `admissions/domain/repositories`
- `notification/domain/repositories`

Permiten `findAll`, busquedas por email/codigo y operaciones CRUD por entidad.

## DTOs

Los DTOs estan en:

- `auth/dto`
- `identity/application/dtos`
- `admissions/dto`
- records internos de controladores admin para respuestas especificas del frontend.

## Hallazgos tecnicos

- `identity` tiene una estructura mas cercana a arquitectura hexagonal: application ports, services, domain, repositories e infrastructure adapters.
- `academic` y `admissions` estan mas orientados a una estructura por dominio simple.
- `AdminAcademicController` y `AdminIdentityReadController` usan repositorios directamente. Funciona para Avance 3, pero la siguiente mejora debe mover esa logica a services de aplicacion.
- Hay paquetes con solo `readme.md`: `attendance`, `calendar`, `common`, `communication`, `contents`, `notifications`, `resources`, `shared`. No estan vacios, pero son documentales o futuros. Se conservan por trazabilidad de arquitectura, aunque conviene mover su contenido a `docs` o implementarlos cuando entren al alcance.
- `notification` contiene codigo real de notificaciones persistidas, correo y WhatsApp. El paquete documental `notifications` debe consolidarse en una fase de limpieza.

## Calendario y notificaciones

- `CalendarController` expone endpoints para `/admin/calendario`, `/docente/calendario` y `/alumno/calendario`.
- El administrador consulta todos los eventos y puede crear eventos.
- El docente consulta eventos generales, eventos para docentes y eventos asociados a sus cursos.
- El alumno consulta eventos generales, eventos para alumnos y eventos asociados a sus cursos matriculados.
- `PersistentNotificationService` centraliza creacion, lectura y marcado de notificaciones.
- `NotificationController` y `UserNotificationController` exponen la bandeja por rol.
- `DataSeeder` crea notificaciones iniciales sin duplicar.

## Seguridad

El backend valida credenciales reales con BCrypt contra MySQL y emite JWT firmado con JJWT. El token se valida en cada request protegida mediante `JwtAuthFilter`, que lee `Authorization: Bearer <token>`, verifica firma, expiracion y usuario en MySQL, y registra la autenticacion en `SecurityContext`.

Claims principales del JWT:

- `sub`: email del usuario.
- `userId`: identificador del usuario.
- `roles`: roles normalizados del usuario.
- `iat`: fecha de emision.
- `exp`: fecha de expiracion.

Reglas de acceso:

- Publicos: `POST /auth/login`, `POST /auth/forgot-password`, `POST /auth/reset-password`, `POST /admisiones`.
- Admin: `/admin/**` requiere `ADMINISTRADOR`.
- Docente: `/docente/**` requiere `DOCENTE`.
- Alumno: `/alumno/**` requiere `ALUMNO`.
- Descargas XLSX y PDF bajo `/admin/**` siguen protegidas y funcionan cuando Angular envia el JWT.

## Configuracion

- `application.yaml`: configuracion base con placeholders.
- `application-local.example.yaml`: plantilla local sin secretos reales.
- `application-local.yaml`: archivo local ignorado por Git.
- `.gitignore`: protege `.env`, logs, `target`, `application-local.yaml` y scripts locales.

## Recomendacion

Mantener el comportamiento actual para la presentacion, pero planificar una fase tecnica para:

1. Extraer logica admin de controllers a services.
2. Consolidar paquetes documentales.
3. Agregar pruebas de servicios criticos.

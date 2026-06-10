# 04. Base de datos

## Motor

La base oficial del Avance 3 es MySQL.

Nombre sugerido:

```txt
yachay
```

El backend usa JPA/Hibernate con `ddl-auto=update` para desarrollo. En produccion se recomienda usar migraciones versionadas.

## Grupos de tablas

### Identidad y seguridad

- `auth_users`: usuarios del sistema.
- `roles`: roles disponibles.
- `user_roles`: relacion usuario-rol.
- `profiles`: datos personales comunes.
- `student_profiles`: datos academicos del alumno.
- `teacher_profiles`: datos laborales/docentes.
- `guardian_profiles`: apoderados o responsables.
- `schools`: colegio o institucion.

### Admision

- `yachay_admission_applications`: postulaciones publicas revisadas por administracion.

Campos principales:

- postulante
- apoderado
- telefono y correo de apoderado
- nivel
- grado
- estado
- observaciones
- fechas de registro y actualizacion

### Academico

- `yachay_academic_years`: anios academicos.
- `yachay_subjects`: materias.
- `yachay_courses`: cursos por materia, docente, grado y seccion.
- `yachay_sections`: secciones/aulas.
- `yachay_enrollments`: matriculas o vinculacion alumno-curso.
- `yachay_academic_tasks`: tareas, proyectos y evaluaciones.
- `yachay_grade_records`: calificaciones.
- `yachay_announcements`: comunicados.
- `yachay_calendar_events`: eventos academicos.
- `yachay_notifications`: notificaciones persistidas por usuario.

## Relaciones principales

- `auth_users` 1 a 1 `profiles`.
- `profiles` 1 a 1 `student_profiles`, `teacher_profiles` o `guardian_profiles`.
- `roles` muchos a muchos con `auth_users`.
- `schools` agrupa alumnos, docentes, secciones y comunicados.
- `teachers` se vinculan a cursos.
- `students` se vinculan a cursos por `enrollments`.
- `courses` agrupan tareas, notas, comunicados y eventos.
- `auth_users` recibe notificaciones mediante `yachay_notifications`.
- `calendar_events` puede vincularse a cursos, alumnos o docentes segun el caso.

## DataSeeder

El seeder carga datos iniciales idempotentes:

- roles
- colegio MGP
- admin demo
- docentes demo
- alumnos demo
- materias
- anio academico
- cursos
- secciones
- tareas
- notas
- comunicados
- calendario/eventos
- notificaciones por rol
- postulaciones

Credenciales demo:

- `admin@yachay.edu.pe` / `Admin123456`
- `docente1@yachay.edu.pe` / `Docente123456`
- `alumno1@yachay.edu.pe` / `Alumno123456`

Las passwords se guardan con BCrypt.

## Tablas posiblemente obsoletas

No se eliminaron tablas desde esta revision. Si existen tablas antiguas con nombres como `usuarios`, `alumnos`, `docentes` o `cursos` sin prefijo y sin relacion con las entidades actuales, deben revisarse manualmente antes de borrar porque pueden contener datos historicos.

## Consideraciones

- No borrar datos productivos sin respaldo.
- No cambiar nombres de tabla si el backend ya esta desplegado.
- Mantener MySQL como base oficial del proyecto.
- Migrar a Flyway o Liquibase en una siguiente fase para controlar cambios de esquema.

# 03. Arquitectura frontend

## Stack

- Angular 21.
- Componentes standalone.
- Rutas lazy.
- TailwindCSS v4.
- Reactive Forms.
- HttpClient con `withFetch()`.
- SSR habilitado.
- Zoneless.
- Guards e interceptor funcionales.

## Estructura

```txt
src/app
|-- core
|   |-- guards
|   |-- interceptors
|   |-- config
|   |-- models
|   `-- services
|-- features
|   |-- admin
|   |-- admission
|   |-- auth
|   |-- student
|   `-- teacher
`-- shared
    `-- components
```

## Core

`core` contiene elementos transversales:

- `AuthService`: login, token, usuario autenticado, logout y persistencia segura para SSR.
- `api.config.ts`: centraliza la URL base de la API.
- `authInterceptor`: agrega `Authorization: Bearer <token>` cuando existe token y cierra sesion si el backend responde 401.
- `authGuard`: protege rutas autenticadas.
- `roleGuard`: separa `/admin`, `/docente` y `/alumno`.
- models: interfaces y tipos del contrato frontend/backend.
- services: llamadas HTTP a la API.

## Features

- `auth`: login, recuperar password y reset.
- `admission`: formulario publico de admision.
- `admin`: layout admin y paginas de gestion.
- `student`: layout y portal alumno.
- `teacher`: layout y portal docente.

## Shared

Componentes reutilizables:

- `app-icon`
- `empty-state`
- `field-error`
- `page-header`
- `quick-action-card`
- `section-card`
- `stat-card`
- `status-badge`

Estos componentes evitan repetir UI base. Aun hay pantallas admin con formularios locales grandes; se recomienda extraer un componente modal/form comun si crece el CRUD.

## Servicios HTTP

Servicios principales:

- `auth`
- `user`
- `student`
- `teacher`
- `course`
- `section`
- `homework`
- `grade`
- `announcement`
- `admission`
- `report`
- `document`
- `notification`
- `calendar`

Los modulos admin consumen datos reales usando `API_URL` desde `src/app/core/config/api.config.ts`. Los errores se muestran con mensajes controlados y se registran detalles en consola.

## Layouts por rol

- `AdminLayout`: sidebar, navbar, campana de notificaciones reales, perfil, configuracion y logout.
- `TeacherLayout`: navegacion docente, calendario, notificaciones y perfil.
- `StudentLayout`: navegacion alumno, calendario, notificaciones y perfil.

Los layouts no deben contener logica de negocio; solo navegacion, estado visual y sesion.

## Rutas principales

- Publicas: `/login`, `/register`, `/forgot-password`, `/reset-password`.
- Admin: `/admin/dashboard`, `/admin/usuarios`, `/admin/alumnos`, `/admin/docentes`, `/admin/cursos`, `/admin/secciones`, `/admin/tareas`, `/admin/notas`, `/admin/calendario`, `/admin/comunicados`, `/admin/postulaciones`, `/admin/notificaciones`, `/admin/configuracion`.
- Docente: `/docente/dashboard`, `/docente/cursos`, `/docente/alumnos`, `/docente/tareas`, `/docente/notas`, `/docente/calendario`, `/docente/comunicados`, `/docente/notificaciones`, `/docente/perfil`.
- Alumno: `/alumno/dashboard`, `/alumno/calendario`, `/alumno/cursos`, `/alumno/tareas`, `/alumno/notas`, `/alumno/comunicados`, `/alumno/notificaciones`, `/alumno/perfil`.

## Hallazgos tecnicos

- La estructura general `core/shared/features` es correcta.
- Las rutas usan lazy loading y guards por rol.
- El admin ya consume API real para gestion principal, reportes y PDF.
- El calendario de alumno, docente y administrador consume API real protegida por JWT.
- La campana de los layouts consume notificaciones persistidas y permite marcar avisos como leidos.
- Las paginas docente/alumno aun pueden requerir endpoints reales completos para dashboards y otras vistas no relacionadas al calendario.
- `API_URL` esta centralizado para evitar endpoints duplicados en servicios.
- Algunos componentes de pagina contienen bastante logica de formularios; para una siguiente fase se recomienda extraer modales reutilizables.

## Buenas practicas aplicadas

- No se usan NgModules.
- No se usa Bootstrap.
- Se usan `inject()` y signals.
- Se evita `localStorage` directo sin validacion de navegador.
- Los archivos de `public` se sirven desde raiz.
- Las descargas XLSX/PDF usan `Blob` y descarga controlada.

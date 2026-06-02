# Yachay Frontend

Frontend Angular 21 del campus virtual Yachay para el Colegio Manuel Gonzales Prada.

## Stack

- Angular 21 con componentes standalone.
- TailwindCSS v4.
- Angular Router con rutas lazy.
- Reactive Forms.
- HttpClient configurado con `withFetch()`.
- Interceptors y guards funcionales.
- SSR habilitado y modo zoneless.

## Arquitectura por N capas

Yachay sigue una arquitectura por N capas. El frontend pertenece a la capa de presentacion y aplica proteccion de rutas en la capa de seguridad del cliente.

```txt
Usuario
  |
  v
Capa de Presentacion
Angular 21 + TailwindCSS v4
  |
  v
Capa de Seguridad
JWT + Guards en Angular / Spring Security en Backend
  |
  v
Capa de Aplicacion
Controllers REST en Spring Boot
  |
  v
Capa de Logica de Negocio
Services
  |
  v
Capa de Acceso a Datos
Repositories / JPA
  |
  v
Capa de Datos
MySQL
```

SQLite puede seguir usandose en desarrollo local, pero la arquitectura oficial del proyecto queda preparada para MySQL en la capa de datos.

## Layouts por rol

Los layouts de `admin`, `teacher` y `student` organizan la navegacion principal y el acceso a las funcionalidades del campus. Cada layout incluye:

- sidebar colapsable en desktop
- menu lateral responsive en movil
- navbar superior sticky
- notificaciones
- avatar y menu de usuario
- cierre de sesion desde el dropdown

El componente `AppIcon` centraliza los iconos SVG reutilizables del sistema. No se usan librerias externas de iconos.

## Dashboard de campus

Los dashboards toman como referencia patrones comunes de plataformas educativas: calendario academico, proximas tareas, cursos en formato card, actividad reciente, comunicados y accesos rapidos. El dashboard del alumno prioriza tareas, cursos, calendario y actividad reciente antes que metricas simples.

## Paleta visual

```txt
#99BFF2  sky-soft
#F0F1F2  cloud
#80BF84  green-soft
#734432  brown
#26110F  ink-dark
```

La paleta busca una interfaz mas calida, moderna y educativa. Los colores anteriores se mantienen como compatibilidad, pero los layouts principales usan esta base visual.

## Estructura

```txt
src/app
|-- core
|   |-- guards
|   |-- interceptors
|   |-- models
|   `-- services
|-- features
|   |-- admission
|   |-- admin
|   |-- auth
|   |-- student
|   `-- teacher
|-- shared
|   `-- components
|-- app.config.ts
|-- app.routes.ts
|-- app.ts
`-- app.html
```

`core` contiene elementos transversales como autenticacion, modelos, interceptors y servicios HTTP. `features` agrupa las pantallas por dominio funcional. `shared` contiene componentes visuales reutilizables como `AppIcon`, tarjetas de metricas, secciones, badges de estado, estados vacios y accesos rapidos.

## Auth

`AuthService` centraliza la autenticacion del usuario, el manejo del token JWT y la sesion actual con signals. Como el proyecto tiene SSR, los accesos a `localStorage` se hacen solo cuando Angular esta en navegador.

Despues del login, el frontend redirige por rol:

- `ADMINISTRADOR` a `/admin/dashboard`
- `DOCENTE` a `/docente/dashboard`
- `ALUMNO` a `/alumno/dashboard`

## Rutas principales

- `/login`
- `/register`
- `/admin/dashboard`
- `/docente/dashboard`
- `/alumno/dashboard`

Cada modulo protegido usa `authGuard`. Mas adelante se puede agregar un `roleGuard` para reforzar autorizacion por rol.

## Ejecutar

```powershell
cd C:\GitHub\yachay\yachay-frontend
npm install
npm start
```

## Build

```powershell
npm run build
```

## Criterios del frontend

- No usar NgModules.
- No usar Bootstrap.
- Mantener componentes standalone.
- Usar signals para estado local.
- Usar `inject()` en servicios y componentes.
- Mantener compatibilidad SSR.

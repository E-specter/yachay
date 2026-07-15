# Cierre funcional y preparación para producción — Yachay

Fecha de cierre técnico: 14 de julio de 2026  
Ruta única auditada: `C:\E-specter - copia (2)\yachay`  
Stack verificado: Angular SSR, Spring Boot, Java 17 y MySQL 8.0.42.

## Dictamen

El código funcional visible queda cerrado: no se detectaron botones sin handler, formularios simulados, servicios Angular con `of(...)`, `DEV_USERS`, retardos para simular backend ni mensajes visibles de “preparado”, “simulado” o “próximamente”. Los CRUD administrativos, los portales docente/alumno, JWT, calendario, notificaciones, PDF local y XLSX están conectados a API y persistencia reales.

La salida a producción requiere todavía aplicar la migración versionada y proporcionar credenciales/variables del entorno. SMTP queda funcional con configuración externa; WhatsApp e I Love PDF se declaran honestamente como no operativos hasta disponer de proveedor y credenciales reales.

## Matriz de evidencia funcional

Los únicos estados usados son `FUNCIONAL`, `FUNCIONAL CON CONFIGURACIÓN EXTERNA`, `PENDIENTE` y `RETIRADO`.

| Módulo | Acción | Frontend | Endpoint | Backend | Tabla | Resultado | Evidencia | Estado |
|---|---|---|---|---|---|---|---|---|
| Autenticación | Login, JWT, recordar sesión y logout | `/login`, layouts | `POST /auth/login` | `AuthController`, `AuthService`, `JwtService` | `auth_users`, `roles`, `user_roles` | Sesiones por rol y cierre real | Login de tres roles; credencial incorrecta 401; ruta ajena 403; smoke visual | FUNCIONAL |
| Recuperación | Solicitar y consumir token de un solo uso | `/forgot-password`, `/reset-password` | `POST /auth/forgot-password`, `POST /auth/reset-password` | `AuthService` | `yachay_password_reset_tokens` | Token hash, vencimiento y uso único | `AuthServiceTest` 2/2 | FUNCIONAL CON CONFIGURACIÓN EXTERNA |
| Configuración | Leer, guardar y restaurar datos institucionales | `/admin/configuracion` | `GET/PUT /admin/configuracion`, `POST /restaurar` | `SystemConfigurationController/Service` | `yachay_system_settings`, `schools` | Persistencia y restauración verificadas; secretos no editables | Smoke API con restauración del valor original y smoke visual | FUNCIONAL |
| Usuarios | Ver, crear, editar, activar/desactivar, rol y reset | `/admin/usuarios` | `GET/POST/PUT/PATCH /admin/usuarios/**` | `AdminIdentityReadController` | `auth_users`, `profiles`, `roles`, `user_roles` | BCrypt, duplicados y estado persistente | Listado real (61); cambio de estado persistido y restaurado | FUNCIONAL |
| Alumnos | CRUD, estado, ficha PDF y XLSX | `/admin/alumnos` | `/admin/alumnos/**`, `/admin/documentos/alumno/{id}/pdf`, `/admin/reportes/alumnos.xlsx` | Identidad, `DocumentService`, `ExcelReportService` | `auth_users`, `profiles`, `student_profiles`, `schools` | Alta transaccional y reportes por ID real | Listado 35; PDF 1650 bytes; XLSX 5910 bytes | FUNCIONAL |
| Docentes | CRUD, estado y XLSX | `/admin/docentes` | `/admin/docentes/**`, `/admin/reportes/docentes.xlsx` | Identidad y reportes | `auth_users`, `profiles`, `teacher_profiles` | Usuario/perfil docente con BCrypt | Listado real (15), endpoints y build | FUNCIONAL |
| Cursos | CRUD, relaciones, estado y XLSX | `/admin/cursos` | `/admin/cursos/**`, `/admin/reportes/cursos.xlsx` | `AdminAcademicController`, repositorios académicos | `yachay_courses`, `yachay_subjects`, `yachay_academic_years`, `yachay_sections` | Relaciones JPA reales y validación de duplicado | Listado real (664), endpoint XLSX y build | FUNCIONAL |
| Secciones | CRUD, capacidad, tutor y estado | `/admin/secciones` | `/admin/secciones/**` | `AdminAcademicController` | `yachay_sections`, `schools`, `teacher_profiles` | Capacidad positiva y unicidad por año/grado/nombre | Listado real (55), API y build | FUNCIONAL |
| Tareas | CRUD, estado, curso, fechas y avisos | `/admin/tareas`, `/docente/tareas` | `/admin/tareas/**`, `/docente/tareas/**` | `AdminAcademicController`, `PortalService` | `yachay_academic_tasks`, `yachay_notifications` | Persistencia y notificación sin duplicado de edición | Listado real (169), portales y pruebas de servicio | FUNCIONAL |
| Entregas | Entregar tarea y revisar entregas propias | `/alumno/tarea-detalle`, `/docente/tareas` | `POST /alumno/tareas/{id}/entrega`, `GET /docente/tareas/{id}/entregas` | `PortalService` | `yachay_homework_submissions` | Propiedad por JWT y una entrega por alumno/tarea | `PortalServiceTest` y build | FUNCIONAL |
| Notas | CRUD, rango, estado, notificación y XLSX | `/admin/notas`, `/docente/notas`, `/alumno/notas` | `/admin/notas/**`, `/docente/notas/**`, `/alumno/notas` | Académico y `PortalService` | `yachay_grade_records`, `yachay_notifications` | Unicidad alumno/curso/bimestre y visibilidad por rol | Listado real (735), portales y API | FUNCIONAL |
| Comunicados | CRUD, publicación y destinatarios | `/admin/comunicados`, portales | `/admin/comunicados/**`, `/docente/comunicados/**`, `/alumno/comunicados/**` | Académico y `PortalService` | `yachay_announcements`, `yachay_announcement_reads`, `yachay_notifications` | Lectura y alcance según JWT/curso | Listado real (5), portales y API | FUNCIONAL |
| Admisión pública | Registrar postulaciones sin mock | `/register` | `POST /admisiones` | `PublicAdmissionController` | `yachay_admission_applications`, `yachay_notifications` | Validación, conflicto e inserción transaccional | Endpoint real y build | FUNCIONAL |
| Postulaciones | Ver, aceptar, rechazar y asignar sección | `/admin/postulaciones` | `GET/PATCH /admin/postulaciones/**` | `AdmissionDecisionService` | Admisiones, usuarios y perfiles | Aceptar crea alumno; rechazo exige motivo; sección seleccionable | `AdmissionDecisionServiceTest` 2/2; listado real (15); build del modal validado | FUNCIONAL |
| Postulaciones | PDF y XLSX | `/admin/postulaciones` | `/admin/documentos/postulacion/{id}/pdf`, `/admin/reportes/postulaciones.xlsx` | Documento y reportes | `yachay_admission_applications` | Archivos con datos reales | PDF 1667 bytes y XLSX vía API | FUNCIONAL |
| Calendario admin | Listar, filtrar, crear, editar y archivar | `/admin/calendario` | `/admin/calendario`, `/semana`, `/mes`, `/{id}/archivar` | `CalendarController` | `yachay_calendar_events`, `yachay_notifications` | Persistencia, validación temporal y refresco | 676 eventos reales; API y build | FUNCIONAL |
| Calendario docente | Generales y sólo cursos propios | `/docente/calendario` | `GET /docente/calendario/**` | `CalendarController` | `yachay_calendar_events`, `yachay_courses` | Filtro derivado del docente JWT | 5 eventos en smoke; `/admin` devuelve 403 | FUNCIONAL |
| Calendario alumno | Generales, curso/sección propios | `/alumno/calendario` | `GET /alumno/calendario/**` | `CalendarController` | Calendario, cursos y matrículas | No incluye eventos de curso ajeno | 14 eventos; 0 de curso ajeno; smoke visual | FUNCIONAL |
| Notificaciones | Listar, contador, leer una/todas | Campanas y `/notificaciones` por rol | `/admin|docente|alumno/notificaciones/**` | Controllers de notificación y servicio persistente | `yachay_notifications` | Lectura por propietario y contador sincronizado al instante | Smoke visual: contador 3→2→1; API de tres roles | FUNCIONAL |
| Portal docente | Dashboard, cursos, alumnos, tareas, notas, comunicados y perfil | `/docente/**` | `/docente/**` | `TeacherPortalController`, `PortalService` | Tablas académicas y de identidad | Todos los datos derivados del docente JWT | Dashboard: 1 curso, 6 alumnos, 1 tarea, 7 notas; 403 a admin | FUNCIONAL |
| Portal alumno | Dashboard, cursos, tareas, entregas, notas, comunicados y perfil | `/alumno/**` | `/alumno/**` | `StudentPortalController`, `PortalService` | Tablas académicas y de identidad | Todos los datos derivados del alumno JWT | Smoke API/visual completo; 403 a admin | FUNCIONAL |
| PDF local | Fichas de alumno y postulación | Botones de descarga | `/admin/documentos/**/pdf` | `DocumentService` con OpenPDF | Datos de alumno/admisión | PDF binario real | 1650 y 1667 bytes | FUNCIONAL |
| XLSX | Alumnos, docentes, usuarios, cursos, notas y postulaciones | Botones de descarga | `/admin/reportes/*.xlsx` | `ExcelReportService` con Apache POI | Repositorios reales | Sin plantillas ni filas simuladas | XLSX de alumnos 5910 bytes y endpoints verificados | FUNCIONAL |
| SMTP | Recuperación y avisos de admisión | Configuración/acciones reales | JavaMail | `EmailService` | No aplica | Implementado; no se hizo envío externo sin credenciales confirmadas | Validación de configuración; test externo omitido de forma segura | FUNCIONAL CON CONFIGURACIÓN EXTERNA |
| WhatsApp | Prueba controlada | `/admin/notificaciones` | `POST /admin/notificaciones/whatsapp/test` | `WhatsappService` | No aplica | Devuelve configuración pendiente y nunca simula “enviado” | Código y configuración revisados | PENDIENTE |
| I Love PDF | Integración remota opcional | Sin dependencia para PDF básico | Cliente externo | `IlovePdfClient` | No aplica | Separado de OpenPDF; no declarado operativo | Sin credenciales/validación externa | PENDIENTE |
| Producción | Variables, CORS, seed y esquema | `environment.production.ts` usa `/api` | Perfil `prod` | Spring config/security | MySQL | Secretos obligatorios; `ddl-auto=validate`; seed false | Configuración y build revisados | FUNCIONAL CON CONFIGURACIÓN EXTERNA |

## Inventario final de botones y acciones

- Total revisado: **183 acciones visibles**.
- Composición: **135 botones normales**, **20 botones submit** y **28 enlaces/navegaciones**.
- Cobertura por familia: **99 admin**, **38 docente**, **34 alumno** y **12 público/auth/compartido**.
- Etiquetas `<button>` presentes: **155**.
- Botones sin `(click)` ni submit asociado: **0**.
- Submit sin `(ngSubmit)` localizado: **0**.
- Acciones retiradas: **ninguna**. “Asignar sección” se conservó porque ahora abre un formulario real y persiste la sección seleccionada.

### Acciones que estaban incompletas y fueron corregidas

- Configuración era principalmente visual: ahora lee, guarda y restaura datos persistidos.
- CRUD admin de usuarios, alumnos, docentes, cursos, secciones, tareas, notas y comunicados: formularios, edición y estados quedaron conectados a API/MySQL.
- Postulaciones: aceptar/rechazar/asignar dejó de ser un cambio superficial; usa servicio transaccional. Rechazo solicita motivo y asignación solicita sección.
- Portales docente/alumno: se retiraron datos académicos locales y se conectaron a endpoints identificados por JWT.
- Calendario: edición/archivo y filtros por propietario quedaron reales; se corrigió la fuga de eventos de cursos ajenos.
- Campanas: el contador global ahora se sincroniza inmediatamente al marcar leído desde la página.
- “Mantener sesión iniciada”: pasó de decorativo a elegir `localStorage` o `sessionStorage`.
- Reportes: se retiraron alertas y plantillas; ahora descargan y muestran errores en la interfaz.
- Dashboard docente: la métrica fija de pendientes fue reemplazada por cálculo de datos API.

## Cambios por capa

- Controllers creados/corregidos: configuración, admisión pública/decisión, portales docente/alumno, académico, calendario, notificaciones, identidad, documentos y reportes.
- Services creados/corregidos: `SystemConfigurationService`, `AdmissionDecisionService`, `PortalService`, autenticación/reset, notificación persistente, WhatsApp, documentos y Excel; services Angular equivalentes con `HttpClient`.
- Entidades/DTO: ajustes en escuela, perfil, alumno, curso, calendario y admisión; nuevas entidades de configuración, reset, entrega de tarea y lectura de comunicado; DTOs de portal y admisión.
- Repositories: consultas por usuario/curso/propietario y nuevos repositorios de configuración, tokens, entregas y lecturas.
- Arquitectura: se mantiene Controller → Service → Repository → MySQL en flujos multi-entidad y `@Transactional` en aceptación/rechazo, entregas y configuración. Los CRUD simples existentes permanecen consolidados en controllers para evitar una reescritura de riesgo.

## Mocks y datos hardcodeados

- Barrido final: 0 coincidencias productivas para `DEV_USERS`, tokens simulados, `of(...)`, `setTimeout(...)` de simulación, `window.alert`, “preparado”, “simulado”, “próximamente” o “revisa la consola”.
- Los arrays que permanecen son navegación, opciones de formulario y tonos visuales; no representan datos funcionales.
- Los datos demo sólo existen en `DataSeeder`, limitado a perfil `local`, condicionado por `app.seed.enabled=true` e idempotente.
- En producción `app.seed.enabled=false`; el arranque no crea ni modifica datos demo.

## Separación local y producción

- `application.yaml`: sin fallback de contraseña MySQL ni secreto JWT conocido; seed desactivado.
- `application-local.example.yaml`: plantilla sin secretos reales; el archivo local privado no se modificó ni se expuso.
- `application-prod.yaml`: variables obligatorias, CORS explícito, SQL log limitado, `open-in-view=false`, `ddl-auto=validate`, seed false y errores sin stack trace.
- Angular desarrollo usa el API local; producción usa ruta relativa `/api`; áreas autenticadas se renderizan en cliente para conservar sesión compatible con SSR.
- Migración: `yachay-db/migrations/V20260714__functional_closure.sql`, aditiva y sin `DROP`.

## Informe de tablas

Tablas activas con entidad/repositorio o uso JPA: `auth_users`, `roles`, `profiles`, `schools`, `student_profiles`, `teacher_profiles`, `guardian_profiles`, `yachay_academic_years`, `yachay_subjects`, `yachay_sections`, `yachay_courses`, `yachay_enrollments`, `yachay_academic_tasks`, `yachay_homework_submissions`, `yachay_grade_records`, `yachay_announcements`, `yachay_announcement_reads`, `yachay_calendar_events`, `yachay_notifications`, `yachay_admission_applications`, `yachay_system_settings` y `yachay_password_reset_tokens`.

- Sin entidad propia: `user_roles`, tabla asociativa esperada del `ManyToMany` usuario–rol.
- Posiblemente antiguas: no se identificaron candidatas en entidades, repositorios ni migraciones del repositorio. No se eliminó ninguna tabla física.
- Duplicadas: no se encontraron dos entidades mapeando la misma tabla.
- No utilizadas: ninguna de las 22 tablas de entidad del código quedó sin repository/flujo; cualquier tabla externa al repositorio debe clasificarse con un inventario DBA antes de producción.

## Pruebas y builds finales

| Comprobación | Resultado |
|---|---|
| `mvnw clean test -Djava.version=17` | BUILD SUCCESS — 8 pruebas, 0 fallos, 0 errores |
| `mvnw clean package -DskipTests -Djava.version=17` | BUILD SUCCESS — JAR ejecutable generado |
| Arranque `local` | Perfil local escuchó en 8080, conectó MySQL y devolvió 401 correcto a login inválido; proceso detenido |
| `npm test -- --watch=false` | 1 archivo, 2 pruebas, 0 fallos |
| `npm run build` | Build Angular browser + SSR exitoso; 5 rutas públicas prerenderizadas |
| `npm audit --omit=dev` | 0 vulnerabilidades de producción |
| Barrido estático | 0 mocks/mensajes prohibidos; 0 botones sin handler |

Pruebas agregadas: contexto Spring aislado en H2, `AdmissionDecisionServiceTest`, `AuthServiceTest`, `SystemConfigurationServiceTest` y `PortalServiceTest`. La conexión local a MySQL se validó además mediante arranque y smoke API; H2 sólo se usa en tests y no sustituye MySQL.

## Smoke por rol y datos temporales

- Administrador: login, dashboard, configuración, listados CRUD, persistencia/restauración de configuración y estado, reportes PDF/XLSX, postulaciones, calendario, notificaciones y logout. El smoke API acumuló 41 aserciones correctas.
- Docente: dashboard y módulos reales; 1 curso, 6 alumnos, 1 tarea y 7 notas; acceso a `/admin` bloqueado con 403.
- Alumno: dashboard, cursos, tareas, notas, comunicados, calendario, notificaciones y perfil; 14 eventos autorizados y 0 de curso ajeno; acceso admin bloqueado.
- Registros temporales creados: ninguno. No se eliminó información del usuario. Los cambios reversibles de configuración/estado se restauraron. Durante la prueba solicitada se marcaron como leídas dos notificaciones existentes del alumno.

## Archivos y alcance

Se modificaron 113 archivos versionados existentes y se agregaron archivos de código/configuración/prueba/migración necesarios. Las áreas principales son:

- `yachay-backend/src/main/java/edu/yachay/backend/{academic,admissions,auth,config,document,identity,notification,portal,report}`
- `yachay-backend/src/main/resources` y `src/test`
- `yachay-frontend/src/app/{core,features}`, `src/environments`, configuración Vitest y paquetes
- `yachay-db/migrations` y README de base de datos
- Este cierre y los tres Markdown finales autorizados

No se recreó ni modificó el Word. No se hizo commit ni push. Todo el trabajo se realizó exclusivamente en `C:\E-specter - copia (2)\yachay`.

## Pendientes reales antes del despliegue

1. Respaldar MySQL y aplicar una sola vez la migración versionada; luego arrancar `prod` con `ddl-auto=validate`.
2. Proporcionar todas las variables obligatorias en el gestor de secretos/entorno y configurar el proxy `/api`.
3. Validar un envío SMTP controlado cuando existan credenciales aprobadas.
4. Implementar/probar un proveedor HTTP real de WhatsApp antes de habilitarlo.
5. Validar I Love PDF sólo si se decide usarlo; OpenPDF ya cubre el PDF local.
6. Commit, push y despliegue quedan fuera de alcance por instrucción expresa.

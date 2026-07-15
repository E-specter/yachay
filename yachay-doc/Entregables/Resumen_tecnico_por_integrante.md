# Resumen técnico por integrante — Yachay

Abrir en VS Code la raíz oficial:

`C:\E-specter - copia (2)\yachay`

## Eduard — Diseño, arquitectura, patrones e integraciones

**Objetivo:** Explicar cómo se organiza Yachay, qué patrones realmente aparecen y cuál es el estado comprobado de cada integración.

**Orden:**

1. Abrir la raíz oficial en VS Code.
2. Mostrar la separación frontend/backend/MySQL.
3. Explicar N capas y la aproximación hexagonal de identity.
4. Recorrer configuración Angular y rutas lazy.
5. Mostrar XLSX y PDF locales.
6. Cerrar con el estado real de SMTP, WhatsApp e I Love PDF.

**Carpetas:**

- `C:\E-specter - copia (2)\yachay\yachay-backend\src\main\java\edu\yachay\backend`
- `C:\E-specter - copia (2)\yachay\yachay-frontend\src\app`
- `C:\E-specter - copia (2)\yachay\yachay-doc`

**Archivos:** `BackendApplication.java`, `app.config.ts`, `app.routes.ts`, `ReportController.java`, `ExcelReportService.java`, `DocumentService.java`, `GlobalExceptionHandler.java`, `pom.xml`, `application.yaml`

**Mini demo:** Abrir app.config.ts, una rama lazy de app.routes.ts, ReportController.studentsReport y DocumentService.createPdf; descargar un XLSX y un PDF ya validados.

**Guion hablado (4–6 minutos):**

Buenos días. Yo explicaré el diseño técnico general de Yachay. En Visual Studio Code abro únicamente la raíz C:\E-specter - copia (2)\yachay. Desde allí se observan cuatro áreas: yachay-frontend, yachay-backend, yachay-db y yachay-doc. La definición correcta es que Yachay usa principalmente arquitectura por N capas, organizada también por dominios funcionales. La capa de presentación es Angular; la seguridad del cliente usa guards e interceptor; en backend Spring Security y JwtAuthFilter validan la petición; después vienen controllers REST, services, repositories JPA y MySQL.

No afirmamos que todo el sistema sea hexagonal puro. El módulo identity sí se aproxima a esos principios porque separa domain, application e infrastructure, e incluye puertos de entrada como UserServicePort. En cambio, otros módulos son más directos y algunos controllers administrativos todavía concentran lógica. Esta diferencia es una limitación reconocida y también demuestra que la documentación describe el código real.

En frontend muestro app.config.ts. Allí Angular registra detección zoneless, router, HttpClient con withFetch e authInterceptor. Después abro app.routes.ts y señalo loadComponent, authGuard y roleGuard. Esto explica el lazy loading y la separación por rol. La carpeta core contiene comportamiento transversal: config, guards, interceptor, modelos y services HTTP. shared contiene componentes visuales reutilizables como app-icon, empty-state, page-header y status-badge. features contiene páginas y layouts por auth, admission, admin, teacher y student.

Para los patrones abro ReportController y ExcelReportService. El controller representa HTTP y delega; el service usa Apache POI para generar XLSX en memoria. Apache POI es una librería Java, no una API web. Luego muestro DocumentService: OpenPDF crea el PDF local sin Internet. El endpoint devuelve bytes y Angular los recibe como Blob. I Love PDF está preparado mediante IlovePdfClient, pero no participa en el flujo actual; por eso se clasifica como integración futura.

Finalmente explico integraciones. MySQL está validado con Connector/J, Hibernate y JPA. SMTP está implementado y configurable, pero no se validó un envío externo durante este cierre. WhatsApp está desactivado por defecto y WhatsappService solo devuelve un estado controlado; no contiene una llamada HTTP saliente a Cloud API. Los builds backend y frontend pasaron, el backend inició con Java 17 y MySQL 8.0.42, y se validaron XLSX y PDF. Mi idea final es: Yachay tiene capas claras y flujos reales demostrables, pero mantiene límites explícitos para no presentar preparación técnica como operación completa.

**Fragmentos asignados:** 6.
**Preguntas asignadas:** 12.

## Jesús — Login, autenticación, autorización, Spring Security y JWT

**Objetivo:** Recorrer el flujo completo desde Reactive Form hasta el SecurityContext y distinguir controles de frontend y backend.

**Orden:**

1. Validación del formulario Angular.
2. POST /api/auth/login.
3. Búsqueda de User y BCrypt.
4. Generación de claims y firma JWT.
5. Persistencia de sesión en Angular.
6. Bearer automático.
7. JwtAuthFilter y reglas 401/403.

**Carpetas:**

- `C:\E-specter - copia (2)\yachay\yachay-backend\src\main\java\edu\yachay\backend\auth`
- `C:\E-specter - copia (2)\yachay\yachay-backend\src\main\java\edu\yachay\backend\config`
- `C:\E-specter - copia (2)\yachay\yachay-frontend\src\app\core`
- `C:\E-specter - copia (2)\yachay\yachay-frontend\src\app\features\auth\pages\login`

**Archivos:** `AuthController.java`, `AuthService.java`, `LoginRequest.java`, `LoginResponse.java`, `JwtService.java`, `JwtAuthFilter.java`, `SecurityConfig.java`, `User.java`, `UserRepository.java`, `auth.ts`, `login.ts`, `auth interceptor`, `auth guard`, `role guard`, `app.routes.ts`

**Mini demo:** Login correcto, abrir Network sin mostrar la contraseña, comprobar Bearer, intentar /admin sin JWT (401) y con DOCENTE (403).

**Guion hablado (4–6 minutos):**

Mi parte es el flujo de seguridad completo. Comienzo en login.ts. El componente usa Reactive Forms; si el formulario es inválido marca los controles y no envía nada. Cuando es válido llama a AuthService.login con email y password. AuthService Angular usa HttpClient para POST /api/auth/login. El interceptor todavía no agrega Bearer porque en ese momento no existe token.

En backend abro AuthController. Este recibe LoginRequest validado y delega en AuthService.login. En AuthService se normaliza el correo, UserRepository busca en auth_users y BCryptPasswordEncoder.matches compara la contraseña ingresada con encrypted_password. BCrypt no descifra; aplica el algoritmo y verifica el hash con su salt. Si falla, se responde 401 sin decir si el correo o la contraseña era incorrecto. Si funciona, se actualiza lastSignInAt, se resuelven roles y JwtService genera el token.

En JwtService señalo cinco claims. sub contiene el correo; userId el identificador; roles las autoridades; iat la emisión y exp la expiración. El token se firma con una clave HMAC derivada del secret. Está firmado, no cifrado, por eso nunca guardamos contraseñas en sus claims. Angular recibe LoginResponse, persiste token y usuario en localStorage solo cuando está en navegador, algo importante porque el proyecto usa SSR.

En cada petición posterior authInterceptor clona la request y agrega Authorization: Bearer. En el backend JwtAuthFilter extrae el token, valida firma y expiración, busca al usuario y crea una Authentication con authorities. SecurityConfig define sesión STATELESS y aplica ADMINISTRADOR a /admin/**, DOCENTE a /docente/** y ALUMNO a /alumno/**. También deja públicas las operaciones de login, recuperación preparada y admisión.

Los guards de Angular mejoran navegación. authGuard evita entrar sin token local y roleGuard redirige según el rol. Pero no son la seguridad definitiva porque el navegador puede manipularse. El backend vuelve a validar todo. La evidencia práctica fue: sin JWT, un endpoint admin devolvió 401; con un token DOCENTE intentando acceder a admin devolvió 403. Esa diferencia es central: 401 significa que no existe autenticación válida; 403 que sí existe, pero no tiene permiso.

Si el profesor pregunta por expiración, JwtService.parseClaims rechaza el token y el interceptor elimina la sesión local ante 401. Si pregunta por SQL Injection, el login usa un método derivado de Spring Data, no concatena SQL. Como limitación, forgot-password y reset-password actualmente responden 204 pero no implementan todavía el ciclo completo de token de recuperación. Mi cierre es que la seguridad está distribuida en responsabilidades complementarias: formulario, service, interceptor y guards en Angular; filtro, contexto y reglas de autorización en Spring.

**Fragmentos asignados:** 6.
**Preguntas asignadas:** 15.

## Chelsea — MySQL, scripts, JPA, Hibernate, entidades y DataSeeder

**Objetivo:** Explicar cómo las entidades reales se mapean a MySQL y qué evidencia existe de conexión, relaciones e idempotencia.

**Orden:**

1. Mostrar placeholders sin abrir application-local.yaml.
2. Explicar Connector/J, JPA y Hibernate.
3. Abrir User y Course.
4. Señalar PK, FK y relaciones.
5. Abrir un repository.
6. Explicar ensureUser idempotente.
7. Aclarar la ausencia actual de scripts versionados en yachay-db.

**Carpetas:**

- `C:\E-specter - copia (2)\yachay\yachay-backend\src\main\resources`
- `C:\E-specter - copia (2)\yachay\yachay-backend\src\main\java\edu\yachay\backend\identity\domain`
- `C:\E-specter - copia (2)\yachay\yachay-backend\src\main\java\edu\yachay\backend\academic\domain`
- `C:\E-specter - copia (2)\yachay\yachay-db`

**Archivos:** `application.yaml`, `application-local.example.yaml`, `pom.xml`, `User.java`, `Profile.java`, `StudentProfile.java`, `TeacherProfile.java`, `Course.java`, `UserRepository.java`, `DataSeeder.java`, `JpaConfig.java`, `yachay-db/README.md`

**Mini demo:** Mostrar MySQL 8.0.42 conectado y las tablas actuales; abrir application.yaml y dos entidades sin revelar credenciales locales.

**Guion hablado (4–6 minutos):**

Yo explicaré la persistencia. No abriré application-local.yaml porque contiene configuración local ignorada por Git. Abro application.yaml y muestro placeholders DB_URL, DB_USERNAME y DB_PASSWORD. Eso permite que el mismo artefacto funcione con variables del entorno. La URL oficial usa MySQL y el driver es com.mysql.cj.jdbc.Driver. En la validación real Hibernate se conectó a MySQL 8.0.42.

Luego diferencio tecnologías. JDBC es la conexión de bajo nivel; MySQL Connector/J es el driver. JPA o Jakarta Persistence es la especificación que define anotaciones y contratos. Hibernate es la implementación que interpreta las entidades y genera SQL. Spring Data JPA agrega repositories como UserRepository. Por eso no son sinónimos: cada uno ocupa una capa distinta.

Abro User.java. @Entity y @Table enlazan la clase con auth_users. @Id y @GeneratedValue definen la clave primaria. encrypted_password guarda el hash BCrypt. Después muestro Course.java, donde @ManyToOne y @JoinColumn representan claves foráneas hacia schools, academic year, subject y teacher profile. FetchType.LAZY evita traer relaciones completas si no se usan. También señalo Profile, StudentProfile y TeacherProfile para explicar que credenciales, identidad personal y datos académicos se separan.

En UserRepository muestro que JpaRepository aporta CRUD y que findByEmail o findByRolesName generan consultas parametrizadas. Eso reduce SQL manual y evita concatenaciones inseguras. Sin embargo, cualquier consulta nativa futura debe seguir usando parámetros. Los services operan dentro de transacciones porque open-in-view está desactivado.

DataSeeder implementa CommandLineRunner. En ensureUser primero busca por correo. Si existe actualiza datos controlados y conserva la identidad; si no, crea. También revisa el hash y agrega roles sobre un Set. Para cursos, matrículas, tareas, notas y notificaciones hay verificaciones exists antes de guardar. Por eso se describe como idempotente: ejecutar varias veces produce un estado equivalente y no duplica las semillas identificadas.

Las tablas actuales mapeadas incluyen auth_users, roles, user_roles, profiles, student_profiles, teacher_profiles, guardian_profiles, schools y las tablas yachay_ de admisión, año, materias, cursos, secciones, matrículas, tareas, notas, comunicados, calendario y notificaciones. La limitación importante es documental: yachay-db solo contiene un README y no tiene scripts SQL versionados actuales. El esquema de desarrollo se mantiene con ddl-auto=update, lo cual sirve localmente pero no reemplaza Flyway o Liquibase. Mi conclusión es que la persistencia real está validada, pero la migración reproducible de producción queda pendiente.

**Fragmentos asignados:** 6.
**Preguntas asignadas:** 12.

## Sayuri — Roles, usuarios, alumnos, docentes y módulo administrador

**Objetivo:** Demostrar el flujo de creación administrativa y la separación entre credenciales, perfil personal y perfil académico.

**Orden:**

1. Diferenciar User y Profile.
2. Mostrar Role y user_roles.
3. Abrir el formulario de usuarios.
4. Seguir UserService Angular.
5. Abrir createUser backend.
6. Señalar BCrypt y repositories.
7. Volver a loadUsers y la tabla.

**Carpetas:**

- `C:\E-specter - copia (2)\yachay\yachay-backend\src\main\java\edu\yachay\backend\identity`
- `C:\E-specter - copia (2)\yachay\yachay-frontend\src\app\features\admin`
- `C:\E-specter - copia (2)\yachay\yachay-frontend\src\app\core\services`

**Archivos:** `User.java`, `Role.java`, `Profile.java`, `StudentProfile.java`, `TeacherProfile.java`, `UserRepository.java`, `RoleRepository.java`, `ProfileRepository.java`, `UserService.java`, `StudentProfileService.java`, `TeacherProfileService.java`, `AdminIdentityReadController.java`, `admin-layout.ts`, `usuarios.ts`, `alumnos.ts`, `docentes.ts`

**Mini demo:** Abrir Usuarios, mostrar el formulario sin crear datos innecesarios, explicar POST /api/admin/usuarios y refresco de la lista.

**Guion hablado (4–6 minutos):**

Mi tema es el módulo administrador para roles, usuarios, alumnos y docentes. Primero explico el modelo. User representa acceso: correo, hash y roles. Role se relaciona mediante user_roles. Profile contiene nombres, fecha de nacimiento, avatar y estado. StudentProfile añade colegio, código, grado, sección y matrícula. TeacherProfile añade empleado, especialidad y contratación. Separar estas responsabilidades evita llenar una sola tabla con campos que no corresponden a todos.

En Angular abro admin-layout.ts y muestro el menú administrativo. La rama /admin en app.routes.ts exige authGuard, roleGuard y el rol ADMINISTRADOR. Ocultar menús mejora la experiencia, pero Spring Security también bloquea /admin/**. Después abro usuarios.ts. El formulario es reactivo y usa Validators. createUser marca controles, arma el DTO, activa el signal saving y llama a UserService.

UserService Angular centraliza las URLs. createUser hace POST /api/admin/usuarios. authInterceptor agrega el JWT. En backend AdminIdentityReadController.createUser recibe CreateAdminUserRequest, resuelve el Role, codifica la contraseña con BCrypt, guarda User y responde 201. Para alumno, el flujo crea User, Profile y StudentProfile dentro de la misma transacción. Para docente crea User, Profile y TeacherProfile. Si falla una operación, la transacción evita un registro incompleto.

Después del éxito Angular no inventa una fila: ejecuta loadUsers y vuelve a consultar GET /api/admin/usuarios. Así la tabla refleja la base de datos. El manejo de errores separa mensaje, loading y saving con signals. Para estado se usa PATCH porque se cambia una parte concreta del recurso.

En identity también existe una estructura cercana a puertos y adaptadores. UserService implementa UserServicePort, aplica regla de correo único y usa IdentityMapper para retornar UserDTO. StudentProfileService valida que Profile y School existan y que el código no se repita. Los repositories ocultan la persistencia JPA.

Como límite real, algunos services Angular declaran operaciones PUT para edición completa que no tienen un mapping administrativo equivalente en los controllers auditados. Por eso la creación, listado y cambio de estado son demostrables, mientras que no se debe prometer un CRUD completo para cada pantalla. La demostración recomendada es mostrar usuarios o alumnos, seguir el código desde el formulario hasta el repository y explicar la respuesta. Mi cierre es que el módulo separa acceso, identidad y rol, protege la ruta en dos capas y actualiza la interfaz desde respuestas reales del backend.

**Fragmentos asignados:** 6.
**Preguntas asignadas:** 12.

## Cristhina — Calendario, notificaciones persistidas, SMTP y WhatsApp

**Objetivo:** Demostrar filtrado por identidad/rol, persistencia de avisos y estado real de los canales externos.

**Orden:**

1. Crear o listar eventos.
2. Mostrar filtros de docente/alumno.
3. Explicar usuario obtenido del JWT.
4. Mostrar creación persistente de notificaciones.
5. Explicar isRead/readAt.
6. Mostrar campana y bandeja.
7. Cerrar con SMTP configurable y WhatsApp controlado.

**Carpetas:**

- `C:\E-specter - copia (2)\yachay\yachay-backend\src\main\java\edu\yachay\backend\academic`
- `C:\E-specter - copia (2)\yachay\yachay-backend\src\main\java\edu\yachay\backend\notification`
- `C:\E-specter - copia (2)\yachay\yachay-frontend\src\app\core\services`
- `C:\E-specter - copia (2)\yachay\yachay-frontend\src\app\features`

**Archivos:** `CalendarController.java`, `CalendarEvent.java`, `CalendarEventRepository.java`, `Notification.java`, `NotificationRepository.java`, `PersistentNotificationService.java`, `NotificationController.java`, `UserNotificationController.java`, `calendar.ts`, `notification.ts`, `admin-layout.ts`, `teacher-layout.ts`, `student-layout.ts`

**Mini demo:** Listar calendario y notificaciones, marcar una como leída y explicar por qué WhatsApp no se presenta como envío operativo.

**Guion hablado (4–6 minutos):**

Yo explicaré calendario y notificaciones. Comienzo en la página de calendario administrativo. CalendarService Angular usa HttpClient y construye rutas para ADMINISTRADOR, DOCENTE y ALUMNO. Para crear, envía POST /api/admin/calendario. El interceptor agrega Bearer y SecurityConfig exige ADMINISTRADOR.

En CalendarController.createEvent el DTO se valida. Si fechaFin no es posterior a fechaInicio responde 400. El curso es opcional porque existen eventos generales, por ejemplo feriados o comunicados. Se construye CalendarEvent, CalendarEventRepository guarda en yachay_calendar_events y notifyCalendarEvent crea notificaciones internas según audience y curso.

Para docente y alumno no se devuelve todo. El usuario se obtiene desde Authentication, que fue creado por JwtAuthFilter. El docente ve eventos generales para DOCENTE y eventos de cursos que enseña. El alumno obtiene StudentProfile, consulta EnrollmentRepository y forma un conjunto de cursos matriculados. Después conserva eventos generales, personales o de esos cursos. Así el alcance de datos se controla en backend.

Notification es una entidad real en yachay_notifications. Guarda recipient_user_id, title, body, type, link_url, is_read, created_at y read_at. markAsRead actualiza booleano y fecha. PersistentNotificationService lista por usuario, cuenta no leídas y comprueba que una notificación pertenezca al usuario antes de modificar. createForUser evita duplicados por destinatario, título y cuerpo, importante para el DataSeeder.

UserNotificationController atiende rutas de docente y alumno; NotificationController atiende admin. No recibe userId del navegador: toma currentUser desde el principal autenticado. Angular NotificationService expone list, markAsRead y markAllAsRead. Los layouts calculan notificationCount y muestran la campana; las páginas ofrecen la bandeja.

Diferencio cuatro conceptos. Un comunicado es contenido académico publicado; una notificación interna es un aviso persistido por usuario; SMTP es el canal de correo mediante JavaMailSender; WhatsApp es una preparación controlada. EmailService solo envía si existen usuario y contraseña de correo; durante el cierre no se validó envío externo, por eso se clasifica configurado pero no validado. WhatsappService, aun activado, no ejecuta una llamada HTTP: devuelve que la Cloud API está preparada. Por tanto no se presenta como operativa.

La evidencia validada es que calendario y notificaciones respondieron para roles, y que 401/403 protegen los prefijos. Mi cierre es que los avisos internos sí son persistentes y demostrables; correo depende de infraestructura externa y WhatsApp permanece como integración futura controlada.

**Fragmentos asignados:** 7.
**Preguntas asignadas:** 12.

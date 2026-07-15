# Preguntas del profesor — Yachay

## 15 preguntas generales

### G1. ¿Qué arquitectura usa Yachay?

- **Respuesta breve:** N capas organizada por dominios.
- **Respuesta desarrollada:** Angular presenta; guards e interceptor preparan la petición; Spring Security valida; controllers delegan; services aplican reglas; repositories persisten en MySQL. Identity se aproxima a puertos y adaptadores, pero el sistema completo no es hexagonal puro.
- **Evidencia/archivo:** app.routes.ts, SecurityConfig.java y paquete identity

### G2. ¿Qué diferencia hay entre DTO y entidad?

- **Respuesta breve:** El DTO es contrato; la entidad representa persistencia.
- **Respuesta desarrollada:** Un DTO controla campos de entrada/salida y validación. Una entidad contiene mapeo ORM y relaciones. Separarlos evita exponer hashes, relaciones lazy o detalles internos.
- **Evidencia/archivo:** LoginRequest.java, LoginResponse.java y User.java

### G3. ¿Qué diferencia hay entre JPA e Hibernate?

- **Respuesta breve:** JPA especifica; Hibernate implementa.
- **Respuesta desarrollada:** Jakarta Persistence define anotaciones y contratos. Hibernate interpreta esas anotaciones, mantiene el contexto de persistencia y genera SQL para MySQL.
- **Evidencia/archivo:** pom.xml y entidades domain/models

### G4. ¿Qué diferencia hay entre guard y filtro JWT?

- **Respuesta breve:** El guard navega; el filtro protege la API.
- **Respuesta desarrollada:** El guard se ejecuta en Angular y puede ser manipulado. JwtAuthFilter se ejecuta en el servidor, valida firma/expiración y establece el SecurityContext.
- **Evidencia/archivo:** core/guards y JwtAuthFilter.java

### G5. ¿Autenticación y autorización son lo mismo?

- **Respuesta breve:** No.
- **Respuesta desarrollada:** Autenticación confirma quién es el usuario mediante credenciales. Autorización decide qué puede hacer usando roles y reglas de rutas.
- **Evidencia/archivo:** AuthService.java y SecurityConfig.java

### G6. ¿Cómo reducen SQL Injection?

- **Respuesta breve:** Con repositories y parámetros.
- **Respuesta desarrollada:** Los métodos derivados de Spring Data generan consultas parametrizadas. No se concatenan correos o IDs en SQL. Consultas nativas futuras deben mantener parámetros.
- **Evidencia/archivo:** UserRepository.java

### G7. ¿Por qué BCrypt?

- **Respuesta breve:** Porque genera hashes lentos con salt.
- **Respuesta desarrollada:** BCrypt dificulta ataques por fuerza bruta y evita almacenar contraseñas reversibles. matches verifica sin descifrar.
- **Evidencia/archivo:** AuthService.java y SecurityConfig.passwordEncoder

### G8. ¿Qué es CORS?

- **Respuesta breve:** Política de orígenes permitidos.
- **Respuesta desarrollada:** SecurityConfig permite los orígenes locales 4200, métodos y headers requeridos. CORS es una política del navegador; no reemplaza autenticación.
- **Evidencia/archivo:** SecurityConfig.corsConfigurationSource

### G9. ¿Qué es un Blob?

- **Respuesta breve:** Una respuesta binaria manejada por Angular.
- **Respuesta desarrollada:** ReportService y DocumentService Angular usan responseType blob, crean un enlace temporal y descargan XLSX o PDF sin convertirlos a JSON.
- **Evidencia/archivo:** core/services/report.ts y document.ts

### G10. ¿Apache POI es una API externa?

- **Respuesta breve:** No, es una librería Java.
- **Respuesta desarrollada:** Se ejecuta dentro del backend y crea XSSFWorkbook en memoria. No necesita credenciales ni red externa.
- **Evidencia/archivo:** ExcelReportService.java y pom.xml

### G11. ¿OpenPDF necesita Internet?

- **Respuesta breve:** No para el flujo actual.
- **Respuesta desarrollada:** DocumentService instancia Document, PdfWriter y PdfPTable localmente y entrega byte[].
- **Evidencia/archivo:** DocumentService.java

### G12. ¿Qué estado tiene SMTP?

- **Respuesta breve:** Implementado y configurable, no validado externamente.
- **Respuesta desarrollada:** EmailService usa JavaMailSender si las credenciales están presentes. Este cierre no envió correo externo para no afirmar operación sin evidencia.
- **Evidencia/archivo:** EmailService.java y application.yaml

### G13. ¿Qué estado tienen WhatsApp e I Love PDF?

- **Respuesta breve:** Preparación futura/controlada.
- **Respuesta desarrollada:** WhatsappService no contiene cliente HTTP saliente. IlovePdfClient solo comprueba claves y devuelve mensajes; el PDF real usa OpenPDF.
- **Evidencia/archivo:** WhatsappService.java e IlovePdfClient.java

### G14. ¿Qué significan 401, 403, 404 y 500?

- **Respuesta breve:** No autenticado, prohibido, no encontrado y error interno.
- **Respuesta desarrollada:** SecurityConfig produce 401/403 de seguridad; controllers/services usan 404 cuando no existe el recurso; GlobalExceptionHandler normaliza errores, y 500 representa una falla no controlada.
- **Evidencia/archivo:** SecurityConfig.java y GlobalExceptionHandler.java

### G15. ¿Qué ocurre si MySQL está caído?

- **Respuesta breve:** El backend no completa la conexión ni los flujos JPA.
- **Respuesta desarrollada:** Hikari y Hibernate fallan al crear el EntityManagerFactory o las operaciones retornan error. La demo debe iniciar/verificar MySQL antes de Spring Boot.
- **Evidencia/archivo:** application.yaml y salida de arranque validada

## Preguntas para Eduard (12)

### E1. ¿Por qué N capas?

- **Respuesta breve:** Separa responsabilidades y facilita explicar/probar cada flujo.
- **Respuesta desarrollada:** Controllers traducen HTTP, services aplican reglas y repositories persisten. Esta separación reduce acoplamiento aunque algunos controllers administrativos aún concentran lógica.
- **Evidencia/archivo:** paquetes academic, identity, report y notification

### E2. ¿Qué parte se aproxima a hexagonal?

- **Respuesta breve:** Identity.
- **Respuesta desarrollada:** Identity separa domain, application e infrastructure y define ports/inputs. No hay esa misma estructura uniforme en todos los módulos.
- **Evidencia/archivo:** identity/application/ports y identity/infrastructure

### E3. ¿Por qué no es hexagonal puro?

- **Respuesta breve:** Porque el patrón no es transversal.
- **Respuesta desarrollada:** Academic y admissions tienen controllers que acceden directamente a repositories y no todos los adaptadores están detrás de puertos.
- **Evidencia/archivo:** AdminAcademicController.java y AdmissionApplicationController.java

### E4. ¿Controller y service hacen lo mismo?

- **Respuesta breve:** No.
- **Respuesta desarrollada:** El controller maneja HTTP, status y DTO; el service concentra caso de uso o generación. Cuando un controller concentra reglas se reconoce como deuda.
- **Evidencia/archivo:** ReportController.java y ExcelReportService.java

### E5. ¿Qué es dependency injection?

- **Respuesta breve:** El contenedor entrega dependencias.
- **Respuesta desarrollada:** Constructores reciben services/repositories y Spring crea el grafo. Facilita sustitución y evita instanciación dispersa.
- **Evidencia/archivo:** constructores de controllers y services

### E6. ¿Qué ventaja tiene lazy loading?

- **Respuesta breve:** Reduce carga inicial y separa rutas.
- **Respuesta desarrollada:** loadComponent importa cada página/layout cuando se navega, lo que produce chunks lazy verificados en el build.
- **Evidencia/archivo:** app.routes.ts y salida npm run build

### E7. ¿Por qué core no contiene páginas?

- **Respuesta breve:** Porque aloja comportamiento transversal.
- **Respuesta desarrollada:** Config, guards, interceptor, modelos y services deben ser reutilizables e independientes de una pantalla concreta.
- **Evidencia/archivo:** src/app/core

### E8. ¿Qué diferencia hay entre core y shared?

- **Respuesta breve:** Core es comportamiento; shared es UI reutilizable.
- **Respuesta desarrollada:** shared contiene app-icon, empty-state, field-error, page-header y tarjetas; core concentra autenticación y HTTP.
- **Evidencia/archivo:** src/app/core y src/app/shared

### E9. ¿Cómo se genera XLSX?

- **Respuesta breve:** Con Apache POI en memoria.
- **Respuesta desarrollada:** Repositories cargan datos, ExcelReportService escribe celdas en XSSFWorkbook, ReportController devuelve byte[] y Angular descarga Blob.
- **Evidencia/archivo:** ExcelReportService.java y report.ts

### E10. ¿Cómo se genera PDF?

- **Respuesta breve:** Con OpenPDF local.
- **Respuesta desarrollada:** DocumentService consulta la entidad, compone una tabla A4 y DocumentController configura Content-Type y filename.
- **Evidencia/archivo:** DocumentService.java y DocumentController.java

### E11. ¿Qué hace GlobalExceptionHandler?

- **Respuesta breve:** Unifica errores JSON.
- **Respuesta desarrollada:** Captura not found, conflict, validación, ResponseStatusException y errores generales para evitar respuestas inconsistentes.
- **Evidencia/archivo:** GlobalExceptionHandler.java

### E12. ¿Qué integración externa está plenamente validada?

- **Respuesta breve:** MySQL; no SMTP/WhatsApp/I Love PDF.
- **Respuesta desarrollada:** MySQL 8.0.42 se conectó. POI/OpenPDF son librerías locales. SMTP quedó configurable; WhatsApp e I Love PDF no ejecutan el flujo operativo.
- **Evidencia/archivo:** arranque y services de integración

## Preguntas para Jesús (15)

### J1. ¿Dónde se valida primero el login?

- **Respuesta breve:** En el Reactive Form.
- **Respuesta desarrollada:** Angular evita solicitudes incompletas, pero el backend vuelve a validar LoginRequest y credenciales.
- **Evidencia/archivo:** login.ts y LoginRequest.java

### J2. ¿Cómo se verifica la contraseña?

- **Respuesta breve:** Con BCrypt.matches.
- **Respuesta desarrollada:** Se compara la entrada con encrypted_password; no se descifra ni se retorna el hash.
- **Evidencia/archivo:** AuthService.login

### J3. ¿Qué contiene el JWT?

- **Respuesta breve:** sub, userId, roles, iat y exp.
- **Respuesta desarrollada:** JwtService agrega claims mínimos y firma. El contenido puede decodificarse, por eso no incluye secretos.
- **Evidencia/archivo:** JwtService.generateToken

### J4. ¿Cómo se firma?

- **Respuesta breve:** Con una SecretKey HMAC.
- **Respuesta desarrollada:** La clave se deriva del jwt.secret y JJWT usa signWith. parseClaims verifica con la misma clave.
- **Evidencia/archivo:** JwtService.java

### J5. ¿Qué pasa si modifican roles en el token?

- **Respuesta breve:** La firma deja de ser válida.
- **Respuesta desarrollada:** JwtAuthFilter rechaza parseClaims y no establece Authentication; la petición protegida termina en 401.
- **Evidencia/archivo:** JwtService.parseClaims y JwtAuthFilter

### J6. ¿Qué pasa cuando expira?

- **Respuesta breve:** Se rechaza y Angular cierra sesión.
- **Respuesta desarrollada:** JJWT valida exp; el backend responde 401 y authInterceptor elimina token/usuario y navega a login.
- **Evidencia/archivo:** JwtService.java y auth interceptor

### J7. ¿Por qué backend valida si hay guards?

- **Respuesta breve:** Porque el cliente no es confiable.
- **Respuesta desarrollada:** Un usuario puede llamar la API sin Angular. Solo Spring Security controla el recurso real.
- **Evidencia/archivo:** SecurityConfig y guards

### J8. ¿Cuáles son las rutas públicas?

- **Respuesta breve:** Login, recuperación preparada y admisión.
- **Respuesta desarrollada:** SecurityConfig permite POST /auth/login, forgot-password, reset-password y /admisiones, además de OPTIONS.
- **Evidencia/archivo:** SecurityConfig.securityFilterChain

### J9. ¿Qué diferencia hay entre 401 y 403?

- **Respuesta breve:** 401 sin autenticación; 403 sin permiso.
- **Respuesta desarrollada:** Se validó 401 sin JWT y 403 con DOCENTE contra admin.
- **Evidencia/archivo:** SecurityConfig y smoke tests

### J10. ¿Qué hace el interceptor?

- **Respuesta breve:** Agrega Bearer y reacciona a 401.
- **Respuesta desarrollada:** Clona la petición, incorpora Authorization y limpia localStorage si la sesión es inválida.
- **Evidencia/archivo:** core/interceptors/auth.ts

### J11. ¿Qué hace authGuard?

- **Respuesta breve:** Comprueba sesión local.
- **Respuesta desarrollada:** Si no hay token retorna UrlTree a /login. Es navegación, no protección de datos.
- **Evidencia/archivo:** core/guards/auth.ts

### J12. ¿Qué hace roleGuard?

- **Respuesta breve:** Compara rol y ruta.
- **Respuesta desarrollada:** Lee data.roles y redirige al dashboard del rol si no coincide.
- **Evidencia/archivo:** core/guards/role.ts

### J13. ¿Cómo se controla cada prefijo?

- **Respuesta breve:** Con hasAuthority.
- **Respuesta desarrollada:** ADMINISTRADOR protege /admin/**, DOCENTE /docente/** y ALUMNO /alumno/**.
- **Evidencia/archivo:** SecurityConfig.java

### J14. ¿Dónde se guarda el token?

- **Respuesta breve:** En localStorage solo en navegador.
- **Respuesta desarrollada:** AuthService usa isPlatformBrowser para no acceder durante SSR y mantiene un signal de usuario.
- **Evidencia/archivo:** core/services/auth.ts

### J15. ¿Recuperación de contraseña está completa?

- **Respuesta breve:** No, está parcial.
- **Respuesta desarrollada:** Los endpoints devuelven 204 pero no existe generación/validación de token ni cambio persistido en ese flujo.
- **Evidencia/archivo:** AuthController.java

## Preguntas para Chelsea (12)

### C1. ¿Qué base oficial usa Yachay?

- **Respuesta breve:** MySQL.
- **Respuesta desarrollada:** application.yaml usa jdbc:mysql y Connector/J; la conexión validada fue MySQL 8.0.42.
- **Evidencia/archivo:** application.yaml y pom.xml

### C2. ¿Qué es Connector/J?

- **Respuesta breve:** El driver JDBC de MySQL.
- **Respuesta desarrollada:** Permite que Hikari/Hibernate abran conexiones Java contra MySQL.
- **Evidencia/archivo:** pom.xml

### C3. ¿Qué hace ddl-auto=update?

- **Respuesta breve:** Ajusta esquema en desarrollo.
- **Respuesta desarrollada:** Hibernate compara mappings y crea/actualiza objetos; no aporta migraciones versionadas ni rollback confiable.
- **Evidencia/archivo:** application.yaml

### C4. ¿Por qué no abrir application-local.yaml?

- **Respuesta breve:** Puede contener secretos locales.
- **Respuesta desarrollada:** Está ignorado por Git y solo debe existir en cada equipo. La exposición usa application-local.example.yaml.
- **Evidencia/archivo:** .gitignore y application-local.example.yaml

### C5. ¿Qué tabla guarda roles?

- **Respuesta breve:** roles y la unión user_roles.
- **Respuesta desarrollada:** User mantiene una relación many-to-many; la tabla intermedia conecta user_id y role_id.
- **Evidencia/archivo:** User.java y Role.java

### C6. ¿Qué diferencia User de Profile?

- **Respuesta breve:** Acceso frente a identidad personal.
- **Respuesta desarrollada:** User contiene correo/hash/roles; Profile nombres y estado; perfiles especializados añaden datos académicos.
- **Evidencia/archivo:** User.java y Profile.java

### C7. ¿Qué es una clave foránea?

- **Respuesta breve:** Referencia a otra fila/tabla.
- **Respuesta desarrollada:** JoinColumn en Course materializa school_id, academic_year_id, subject_id y teacher_id.
- **Evidencia/archivo:** Course.java

### C8. ¿Qué es @ManyToOne?

- **Respuesta breve:** Muchas entidades apuntan a una relacionada.
- **Respuesta desarrollada:** Muchos cursos pueden pertenecer al mismo colegio, año, materia o docente.
- **Evidencia/archivo:** Course.java

### C9. ¿Qué aporta JpaRepository?

- **Respuesta breve:** CRUD, paginación y consultas derivadas.
- **Respuesta desarrollada:** UserRepository hereda save/find/delete y agrega búsquedas tipadas por email/roles.
- **Evidencia/archivo:** UserRepository.java

### C10. ¿Cómo es idempotente DataSeeder?

- **Respuesta breve:** Busca/existe antes de guardar.
- **Respuesta desarrollada:** ensureUser reutiliza por email y otras semillas consultan exists por claves naturales.
- **Evidencia/archivo:** DataSeeder.java

### C11. ¿Existen scripts actuales en yachay-db?

- **Respuesta breve:** No; solo README.
- **Respuesta desarrollada:** La carpeta oficial fue revisada y no contiene SQL versionado. El archivo backend/doc/tablas_mysql.sql es legado y contradice nombres actuales.
- **Evidencia/archivo:** yachay-db/README.md

### C12. ¿Qué falta para producción?

- **Respuesta breve:** Migraciones y pruebas de esquema.
- **Respuesta desarrollada:** Flyway o Liquibase permitirían versionar cambios, reproducir ambientes y revisar rollback.
- **Evidencia/archivo:** Limitaciones documentadas

## Preguntas para Sayuri (12)

### S1. ¿Qué es Role?

- **Respuesta breve:** Una autoridad asignable a User.
- **Respuesta desarrollada:** Los nombres normalizados ADMINISTRADOR, DOCENTE y ALUMNO controlan rutas y menús.
- **Evidencia/archivo:** Role.java, AuthService.java y SecurityConfig.java

### S2. ¿Cómo se guarda user_roles?

- **Respuesta breve:** Como relación many-to-many.
- **Respuesta desarrollada:** User.roles usa una tabla de unión; un usuario puede tener varias authorities.
- **Evidencia/archivo:** User.java

### S3. ¿Qué diferencia hay entre StudentProfile y TeacherProfile?

- **Respuesta breve:** Datos académicos por tipo.
- **Respuesta desarrollada:** StudentProfile guarda código/grado/sección/matrícula; TeacherProfile empleado/especialidad/contratación.
- **Evidencia/archivo:** StudentProfile.java y TeacherProfile.java

### S4. ¿Cómo se crea un usuario admin?

- **Respuesta breve:** Formulario -> service -> POST -> controller -> repositories.
- **Respuesta desarrollada:** Angular valida, el interceptor añade JWT, el controller cifra la contraseña y responde 201; luego la tabla se recarga.
- **Evidencia/archivo:** usuarios.ts, user.ts y AdminIdentityReadController.java

### S5. ¿Dónde se aplica BCrypt?

- **Respuesta breve:** Antes de guardar User.
- **Respuesta desarrollada:** AdminIdentityReadController usa PasswordEncoder y AuthService verifica con matches.
- **Evidencia/archivo:** AdminIdentityReadController.java

### S6. ¿Por qué usar DTO?

- **Respuesta breve:** Para controlar contrato y campos.
- **Respuesta desarrollada:** CreateAdminUserRequest no expone la entidad completa; AdminUserResponse omite encrypted_password.
- **Evidencia/archivo:** DTOs internos de AdminIdentityReadController

### S7. ¿Cómo se maneja un correo repetido?

- **Respuesta breve:** Se detecta conflicto.
- **Respuesta desarrollada:** El repository comprueba existencia y el flujo responde error en lugar de violar la restricción única.
- **Evidencia/archivo:** UserRepository y controllers/services identity

### S8. ¿Cómo se actualiza la tabla Angular?

- **Respuesta breve:** Con loadUsers tras el éxito.
- **Respuesta desarrollada:** No depende de una copia optimista; vuelve a obtener GET /admin/usuarios.
- **Evidencia/archivo:** usuarios.ts

### S9. ¿Qué protege el menú?

- **Respuesta breve:** Layout y rutas; backend protege datos.
- **Respuesta desarrollada:** navItems organiza UI, roleGuard limita navegación y SecurityConfig exige la authority.
- **Evidencia/archivo:** admin-layout.ts, app.routes.ts y SecurityConfig.java

### S10. ¿Qué hace PATCH de estado?

- **Respuesta breve:** Cambia una propiedad parcial.
- **Respuesta desarrollada:** El endpoint no reemplaza toda la entidad; actualiza activo/inactivo y devuelve la representación.
- **Evidencia/archivo:** AdminIdentityReadController.java y user.ts

### S11. ¿Es CRUD completo todo el módulo?

- **Respuesta breve:** No en todos los recursos.
- **Respuesta desarrollada:** Listar, crear y cambiar estado están implementados; algunos PUT declarados en frontend no tienen mapping administrativo equivalente.
- **Evidencia/archivo:** services Angular y controllers backend

### S12. ¿Qué parte de identity es hexagonal?

- **Respuesta breve:** Puertos, services, dominio y adapters.
- **Respuesta desarrollada:** UserServicePort define entrada, UserService implementa caso de uso, modelos/repositories están en domain y controllers en infrastructure.
- **Evidencia/archivo:** paquete identity

## Preguntas para Cristhina (12)

### C1. ¿Cómo se crea un evento?

- **Respuesta breve:** POST admin con DTO validado.
- **Respuesta desarrollada:** CalendarController valida fechas, resuelve curso, guarda CalendarEvent y genera notificaciones.
- **Evidencia/archivo:** CalendarController.createEvent

### C2. ¿Cómo sabe el backend quién consulta?

- **Respuesta breve:** Por Authentication.
- **Respuesta desarrollada:** JwtAuthFilter establece como principal el User validado; el controller no confía en un userId enviado.
- **Evidencia/archivo:** CalendarController.currentUser

### C3. ¿Cómo filtra al docente?

- **Respuesta breve:** Por audience y cursos asignados.
- **Respuesta desarrollada:** Compara el teacher profile del curso con el perfil autenticado.
- **Evidencia/archivo:** CalendarController.filterTeacherEvents

### C4. ¿Cómo filtra al alumno?

- **Respuesta breve:** Por audience, destinatario y matrícula.
- **Respuesta desarrollada:** EnrollmentRepository obtiene course IDs y el filtro conserva eventos relacionados.
- **Evidencia/archivo:** CalendarController.filterStudentEvents

### C5. ¿Qué es una notificación persistida?

- **Respuesta breve:** Una fila por usuario en MySQL.
- **Respuesta desarrollada:** Tiene destinatario, contenido, tipo, enlace, leído y fechas; sobrevive recargas y sesiones.
- **Evidencia/archivo:** Notification.java

### C6. ¿Cómo se marca una?

- **Respuesta breve:** PATCH y verificación de propietario.
- **Respuesta desarrollada:** El service busca el ID, compara recipient.id con userId autenticado y ejecuta markAsRead.
- **Evidencia/archivo:** PersistentNotificationService.markAsRead

### C7. ¿Cómo se marcan todas?

- **Respuesta breve:** Carga las del usuario y actualiza no leídas.
- **Respuesta desarrollada:** El service filtra, marca y ejecuta saveAll; Angular actualiza lista/contador.
- **Evidencia/archivo:** PersistentNotificationService.markAllAsRead

### C8. ¿Cómo evita duplicados?

- **Respuesta breve:** exists por destinatario, título y cuerpo.
- **Respuesta desarrollada:** createForUser retorna antes de save si ya existe la misma combinación.
- **Evidencia/archivo:** NotificationRepository y PersistentNotificationService

### C9. ¿Comunicado y notificación son iguales?

- **Respuesta breve:** No.
- **Respuesta desarrollada:** Announcement es contenido publicado; Notification es un aviso individual con isRead/readAt y enlace.
- **Evidencia/archivo:** Announcement.java y Notification.java

### C10. ¿SMTP está operativo?

- **Respuesta breve:** Implementado/configurable, no validado externamente.
- **Respuesta desarrollada:** EmailService usa JavaMailSender solo si hay configuración; no se realizó envío real en este cierre.
- **Evidencia/archivo:** EmailService.java

### C11. ¿WhatsApp envía mensajes?

- **Respuesta breve:** No en el estado actual.
- **Respuesta desarrollada:** WhatsappService valida banderas/credenciales y devuelve estado, pero no contiene cliente HTTP hacia Cloud API.
- **Evidencia/archivo:** WhatsappService.java

### C12. ¿Qué muestra la campana?

- **Respuesta breve:** Notificaciones persistidas y conteo no leído.
- **Respuesta desarrollada:** Los layouts llaman NotificationService, conservan signals y calculan notificationCount.
- **Evidencia/archivo:** admin-layout.ts, teacher-layout.ts y student-layout.ts

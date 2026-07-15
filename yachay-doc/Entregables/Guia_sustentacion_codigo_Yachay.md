# Guía de sustentación técnica del código — Yachay

**Fecha de cierre documental:** 14 de julio de 2026

**Raíz oficial:** `C:\E-specter - copia (2)\yachay`

**Integrantes:** Eduard, Jesús, Chelsea, Sayuri y Cristhina

## Índice

1. Descripción, problema, solución y alcance
2. Evidencia validada y estado funcional
3. Arquitectura y patrones
4. Backend, frontend y MySQL
5. Seguridad
6. Flujos GET, POST y Blob
7. Capítulos por integrante
8. Ruta de demo
9. Preguntas generales
10. Limitaciones, conclusiones, bibliografía y rúbrica

## 1. Descripción, problema, solución y alcance

Yachay es una plataforma académica para el Colegio Manuel Gonzales Prada. Integra autenticación por roles, administración de identidad, admisión, gestión académica, calendario, notificaciones persistidas y descargas XLSX/PDF.

### Problema identificado

La información académica y de acceso requiere una interfaz única con separación de responsabilidades, control de permisos y persistencia centralizada. La sustentación también necesita evidenciar qué flujos son reales y cuáles continúan parciales.

### Solución propuesta

Angular 21 presenta rutas y formularios; Spring Boot 4 expone REST y aplica seguridad; Spring Data JPA/Hibernate persiste en MySQL; Apache POI y OpenPDF generan documentos locales. El diseño mantiene módulos por rol y dominio.

### Alcance real

El cierre valida login, roles, administración principal, admisión pública, calendario, notificaciones, XLSX y PDF. No convierte en reales las páginas docentes/alumno que aún usan arreglos locales, ni presenta SMTP/WhatsApp/I Love PDF como integraciones plenamente operativas.

## 2. Evidencia validada y estado funcional

| Elemento | Estado | Evidencia |
| --- | --- | --- |
| Backend | Implementada y validada | Maven clean package con Java release 17: BUILD SUCCESS; 117 fuentes después de la corrección. |
| Frontend | Implementada y validada | npm run build completó Angular/SSR y prerenderizó 39 rutas. |
| Spring Boot + MySQL | Implementada y validada | Arranque con perfil local y conexión a MySQL 8.0.42. |
| Login y roles | Implementada y validada | ADMINISTRADOR, DOCENTE y ALUMNO autenticaron; 401 sin JWT y 403 DOCENTE contra admin. |
| Admisión pública | Implementada y validada | POST /api/admisiones respondió 201; el registro de prueba se visualizó en admin y fue eliminado. |
| Calendario | Implementada y validada | GET por admin/docente/alumno respondió; creación y filtros existen en código. |
| Notificaciones | Implementada y validada | Listados persistidos respondieron por rol; existen marcar una/todas. |
| XLSX | Implementada y validada | Firmas PK correctas; cursos y notas contienen 664 y 735 filas de datos locales respectivamente. |
| PDF | Implementada y validada | Descarga respondió con firma %PDF y DocumentService usa OpenPDF local. |
| SMTP | Configurada pero no validada | EmailService y JavaMailSender existen; no se ejecutó envío externo. |
| WhatsApp | Integración futura/controlada | No existe llamada HTTP saliente; desactivado por defecto. |
| I Love PDF | Integración futura | IlovePdfClient solo verifica configuración; no interviene en el PDF actual. |
| Módulos docente/alumno | Implementada parcialmente | Calendario y notificaciones son reales; varias páginas de cursos/tareas/notas/comunicados usan arreglos locales y sus endpoints de portal devuelven 404. |
| Recuperación de contraseña | Implementada parcialmente | Endpoints 204 sin flujo persistente de token/cambio de clave. |
| Scripts SQL versionados | No encontrada | yachay-db solo contiene README; ddl-auto=update mantiene desarrollo. |

## 3. Arquitectura oficial

> Yachay utiliza principalmente una arquitectura por N capas, organizada también por dominios funcionales. El módulo identity presenta una separación cercana a principios de arquitectura hexagonal mediante domain, application e infrastructure, pero el sistema completo no se considera una implementación hexagonal pura.

```text
Usuario
  ↓
Angular 21 + TailwindCSS v4
  ↓  Guards + authInterceptor
Spring Security + JwtAuthFilter
  ↓
Controllers REST
  ↓
Services
  ↓
Repositories JPA / Hibernate
  ↓
MySQL
```

### Responsabilidad de cada capa

| Elemento | Responsabilidad | Ejemplo real |
| --- | --- | --- |
| Controller | Contrato HTTP, status, DTO y delegación. | AuthController, ReportController, CalendarController |
| Service | Caso de uso, regla o generación. | AuthService, ExcelReportService, PersistentNotificationService |
| Repository | Acceso tipado a persistencia. | UserRepository, CalendarEventRepository |
| Entidad | Estado persistente, columnas y relaciones. | User, Course, Notification |
| DTO | Entrada/salida controlada y validable. | LoginRequest, LoginResponse, CreateCalendarEventRequest |

### Patrones comprobados

| Patrón | Evidencia |
| --- | --- |
| Controller Pattern | Controllers REST con mappings. |
| Service Layer | AuthService, ExcelReportService, DocumentService. |
| Repository Pattern | Interfaces JpaRepository. |
| DTO Pattern | records y DTOs de identity/auth/academic. |
| Dependency Injection | Constructores e inject() de Angular. |
| Configuration Pattern | SecurityConfig, appConfig, api.config.ts. |
| Global Exception Handler | GlobalExceptionHandler con @RestControllerAdvice. |
| Idempotent Seeder | DataSeeder + exists/find antes de save. |
| Guard Pattern | authGuard y roleGuard. |
| Interceptor Pattern | authInterceptor. |
| Angular Service Pattern | Services HTTP bajo core/services. |

## 4. Estructura técnica

### Backend

`auth` concentra login/JWT; `config` seguridad y semillas; `identity` separa domain/application/infrastructure; `academic` reúne cursos, secciones, tareas, notas, comunicados y calendario; `admissions`, `report`, `document` y `notification` completan sus dominios. No todos usan el mismo nivel de separación.

### Frontend: core, shared y features

- `core`: configuración global, guards, interceptor, modelos y services HTTP. No contiene páginas porque sus capacidades son transversales.
- `shared`: piezas visuales reutilizables: app-icon, empty-state, field-error, page-header, quick-action-card, section-card, stat-card y status-badge.
- `features`: auth, admission, admin, teacher y student; contiene layouts y páginas lazy/standalone.

Angular usa componentes standalone, Reactive Forms, signals, HttpClient, withFetch(), SSR y detección zoneless, confirmados en código/configuración.

### MySQL, JPA e Hibernate

Las entidades actuales mapean `auth_users`, `roles`, `user_roles`, `profiles`, `student_profiles`, `teacher_profiles`, `guardian_profiles`, `schools`, `yachay_admission_applications`, `yachay_academic_years`, `yachay_subjects`, `yachay_courses`, `yachay_sections`, `yachay_enrollments`, `yachay_academic_tasks`, `yachay_grade_records`, `yachay_announcements`, `yachay_calendar_events` y `yachay_notifications`.

`application.yaml` usa MySQL y `ddl-auto=update` para desarrollo. `application-local.yaml` existe solo localmente y está ignorado. La carpeta `yachay-db` no contiene scripts SQL actuales; por ello la reproducibilidad por migraciones se marca pendiente.

## 5. Seguridad JWT y BCrypt

Flujo: Login Reactive Form → AuthService Angular → POST /api/auth/login → AuthController → AuthService → UserRepository → BCrypt.matches → JwtService → LoginResponse → localStorage (solo navegador) → authInterceptor → JwtAuthFilter → SecurityConfig. Guards y menús mejoran navegación, mientras que el backend impone la autorización real.

## 6. Tres flujos completos

### GET real — listar usuarios

`Usuarios.loadUsers()` → `UserService.list()` → GET `/api/admin/usuarios` → authInterceptor → `AdminIdentityReadController.getUsers()` → `UserRepository.findAll()` → `auth_users`/roles/perfiles → JSON → signal de la tabla.

### POST real — admisión pública

`Register.enviarPostulacion()` → `AdmissionService.createAdmission()` → POST `/api/admisiones` → `PublicAdmissionController.create()` → `AdmissionApplicationRepository.saveAll()` → `yachay_admission_applications` → 201 `CreateAdmissionResponse` → mensaje de éxito. El smoke creó y luego eliminó el registro temporal.

### Blob real — XLSX y PDF

`ReportService.downloadCursos()` → GET `/api/admin/reportes/cursos.xlsx` → `ReportController.coursesReport()` → `ExcelReportService.buildCoursesReport()` → repositories → Apache POI → byte[] → Blob → descarga. Para PDF: `DocumentService` Angular → `DocumentController.downloadAdmissionPdf()` → `DocumentService.buildAdmissionPdf()` → OpenPDF → byte[] → Blob.

## 7. Capítulos por integrante

## Eduard — Diseño, arquitectura, patrones e integraciones

**Objetivo.** Explicar cómo se organiza Yachay, qué patrones realmente aparecen y cuál es el estado comprobado de cada integración.

### Orden de exposición

1. Abrir la raíz oficial en VS Code.
2. Mostrar la separación frontend/backend/MySQL.
3. Explicar N capas y la aproximación hexagonal de identity.
4. Recorrer configuración Angular y rutas lazy.
5. Mostrar XLSX y PDF locales.
6. Cerrar con el estado real de SMTP, WhatsApp e I Love PDF.

### Carpetas a abrir

- `C:\E-specter - copia (2)\yachay\yachay-backend\src\main\java\edu\yachay\backend`
- `C:\E-specter - copia (2)\yachay\yachay-frontend\src\app`
- `C:\E-specter - copia (2)\yachay\yachay-doc`

### Archivos a preparar en pestañas

`BackendApplication.java`, `app.config.ts`, `app.routes.ts`, `ReportController.java`, `ExcelReportService.java`, `DocumentService.java`, `GlobalExceptionHandler.java`, `pom.xml`, `application.yaml`

### Fragmentos reales

#### Eduard 1. Punto de arranque de Spring Boot

- **Archivo:** `C:\E-specter - copia (2)\yachay\yachay-backend\src\main\java\edu\yachay\backend\BackendApplication.java`
- **Método/elemento:** `main`
- **Líneas aproximadas:** 1–12

```java
package edu.yachay.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

```

**Qué hace.** La clase raíz delega el arranque del contenedor a Spring Boot y fija el paquete base de escaneo.

**Lectura línea por línea / puntos clave.**

- @SpringBootApplication combina configuración, auto-configuración y escaneo.
- SpringApplication.run crea el ApplicationContext y levanta Tomcat.

**Patrón:** Configuration Pattern y Dependency Injection.

**Conexión entre capas:** Desde este paquete se descubren controllers, services, repositories y configuraciones.

**Frase para exponer:** “Este archivo no contiene negocio: inicia el contenedor que ensambla las capas.”

**Pregunta probable:** ¿Por qué la clase principal está en el paquete edu.yachay.backend?

#### Eduard 2. Configuración transversal de Angular

- **Archivo:** `C:\E-specter - copia (2)\yachay\yachay-frontend\src\app\app.config.ts`
- **Método/elemento:** `appConfig`
- **Líneas aproximadas:** 18–36

```typescript
export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZonelessChangeDetection(),

    provideRouter(
      routes,
      withComponentInputBinding(),
      withInMemoryScrolling({
        scrollPositionRestoration: 'enabled',
      }),
    ),

    provideHttpClient(
      withFetch(),
      withInterceptors([authInterceptor]),
    ),
  ],
};
```

**Qué hace.** Registra router, detección zoneless y HttpClient con fetch e interceptor funcional.

**Lectura línea por línea / puntos clave.**

- provideZonelessChangeDetection evita depender de Zone.js.
- provideRouter conecta las rutas lazy.
- provideHttpClient(withFetch(), withInterceptors(...)) centraliza el transporte HTTP.

**Patrón:** Configuration Pattern e Interceptor Pattern.

**Conexión entre capas:** Toda llamada de un service Angular pasa por esta configuración antes de llegar al backend.

**Frase para exponer:** “Aquí se ensamblan las capacidades globales del frontend, no las páginas.”

**Pregunta probable:** ¿Qué aporta withFetch() y por qué el interceptor se registra aquí?

#### Eduard 3. Rutas lazy y separación por rol

- **Archivo:** `C:\E-specter - copia (2)\yachay\yachay-frontend\src\app\app.routes.ts`
- **Método/elemento:** `routes: rama admin`
- **Líneas aproximadas:** 43–59

```typescript
    path: 'admin',
    loadComponent: () =>
      import('./features/admin/layout/admin-layout/admin-layout').then(
        (m) => m.AdminLayout,
      ),
    canActivate: [authGuard, roleGuard],
    data: { roles: ['ADMINISTRADOR'] },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'dashboard',
      },
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./features/admin/pages/dashboard/dashboard').then(
```

**Qué hace.** La rama administrativa carga su layout bajo demanda y aplica autenticación y rol antes de abrir sus hijas.

**Lectura línea por línea / puntos clave.**

- loadComponent implementa lazy loading.
- canActivate ejecuta authGuard y roleGuard.
- data.roles declara ADMINISTRADOR como política de navegación.

**Patrón:** Guard Pattern y Lazy Loading.

**Conexión entre capas:** El control visual se complementa con SecurityConfig en backend; no reemplaza la autorización del servidor.

**Frase para exponer:** “Angular guía la navegación; Spring Security sigue siendo la autoridad final.”

**Pregunta probable:** ¿Por qué no basta con ocultar el menú administrativo?

#### Eduard 4. Endpoint de reporte XLSX

- **Archivo:** `C:\E-specter - copia (2)\yachay\yachay-backend\src\main\java\edu\yachay\backend\report\ReportController.java`
- **Método/elemento:** `studentsReport`
- **Líneas aproximadas:** 23–31

```java
    }

    @GetMapping("/alumnos.xlsx")
    public ResponseEntity<byte[]> studentsReport() {
        return xlsx("alumnos.xlsx", excelReportService.buildStudentsReport());
    }

    @GetMapping("/docentes.xlsx")
    public ResponseEntity<byte[]> teachersReport() {
```

**Qué hace.** El controller expone un GET administrativo y delega la generación binaria al service.

**Lectura línea por línea / puntos clave.**

- @GetMapping fija /admin/reportes/alumnos.xlsx sobre el mapping de clase.
- ResponseEntity<byte[]> expresa una respuesta binaria.
- ExcelReportService concentra Apache POI.

**Patrón:** Controller Pattern y Service Layer.

**Conexión entre capas:** Angular ReportService solicita el Blob y el interceptor agrega el JWT.

**Frase para exponer:** “El controller traduce HTTP; el service conoce cómo construir el archivo.”

**Pregunta probable:** ¿Apache POI es una API externa?

#### Eduard 5. Construcción común de workbook

- **Archivo:** `C:\E-specter - copia (2)\yachay\yachay-backend\src\main\java\edu\yachay\backend\report\ExcelReportService.java`
- **Método/elemento:** `createWorkbook`
- **Líneas aproximadas:** 225–243

```java
    private byte[] createWorkbook(String sheetName, List<String> columns, SheetWriter writer) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet(sheetName);
            CellStyle headerStyle = headerStyle(workbook);
            writer.write(sheet, headerStyle);

            for (int index = 0; index < columns.size(); index++) {
                sheet.autoSizeColumn(index);
            }

            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException exception) {
            throw new IllegalStateException("No se pudo generar el reporte XLSX.", exception);
        }
    }

    private void writeHeader(Sheet sheet, CellStyle headerStyle, List<String> headers) {
        Row row = sheet.createRow(0);
```

**Qué hace.** Crea un XSSFWorkbook en memoria, escribe la hoja, ajusta columnas y devuelve bytes.

**Lectura línea por línea / puntos clave.**

- try-with-resources cierra Workbook y ByteArrayOutputStream.
- SheetWriter desacopla el contenido particular del ciclo de creación.
- workbook.write serializa el XLSX.

**Patrón:** Service Layer y Template Method local.

**Conexión entre capas:** Los repositories entregan entidades; este método las transforma en filas descargables.

**Frase para exponer:** “Apache POI trabaja localmente y el resultado viaja como Blob a Angular.”

**Pregunta probable:** ¿Por qué el método devuelve byte[] en vez de guardar un archivo en el servidor?

#### Eduard 6. Generación local de PDF

- **Archivo:** `C:\E-specter - copia (2)\yachay\yachay-backend\src\main\java\edu\yachay\backend\document\DocumentService.java`
- **Método/elemento:** `createPdf`
- **Líneas aproximadas:** 100–122

```java
    private byte[] createPdf(String title, PdfTableWriter tableWriter) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 48, 48, 48, 48);
            PdfWriter.getInstance(document, outputStream);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, new Color(13, 13, 13));
            Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 10, new Color(90, 90, 90));

            Paragraph heading = new Paragraph(title, titleFont);
            heading.setAlignment(Element.ALIGN_CENTER);
            heading.setSpacingAfter(8);
            document.add(heading);

            Paragraph subtitle = new Paragraph("Colegio Manuel Gonzales Prada", subtitleFont);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            subtitle.setSpacingAfter(24);
            document.add(subtitle);

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{32, 68});
            tableWriter.write(table);
```

**Qué hace.** OpenPDF crea el documento A4 en memoria; no requiere servicio web ni I Love PDF.

**Lectura línea por línea / puntos clave.**

- PdfWriter vincula Document con el flujo de bytes.
- document.open habilita la escritura.
- PdfPTable prepara la ficha de dos columnas.

**Patrón:** Service Layer y Strategy local mediante PdfTableWriter.

**Conexión entre capas:** DocumentController entrega los bytes; DocumentService consulta la entidad y compone la ficha.

**Frase para exponer:** “El PDF básico es local con OpenPDF; I Love PDF no participa en este flujo.”

**Pregunta probable:** ¿OpenPDF necesita Internet para generar la ficha?

### Mini demo

Abrir app.config.ts, una rama lazy de app.routes.ts, ReportController.studentsReport y DocumentService.createPdf; descargar un XLSX y un PDF ya validados.

### Errores que debe evitar

- Afirmar que todo el sistema es hexagonal puro.
- Llamar API externa a Apache POI u OpenPDF.
- Presentar I Love PDF como parte del PDF actual.
- Afirmar que WhatsApp envía realmente.

### Guion hablado (4–6 minutos)

Buenos días. Yo explicaré el diseño técnico general de Yachay. En Visual Studio Code abro únicamente la raíz C:\E-specter - copia (2)\yachay. Desde allí se observan cuatro áreas: yachay-frontend, yachay-backend, yachay-db y yachay-doc. La definición correcta es que Yachay usa principalmente arquitectura por N capas, organizada también por dominios funcionales. La capa de presentación es Angular; la seguridad del cliente usa guards e interceptor; en backend Spring Security y JwtAuthFilter validan la petición; después vienen controllers REST, services, repositories JPA y MySQL.

No afirmamos que todo el sistema sea hexagonal puro. El módulo identity sí se aproxima a esos principios porque separa domain, application e infrastructure, e incluye puertos de entrada como UserServicePort. En cambio, otros módulos son más directos y algunos controllers administrativos todavía concentran lógica. Esta diferencia es una limitación reconocida y también demuestra que la documentación describe el código real.

En frontend muestro app.config.ts. Allí Angular registra detección zoneless, router, HttpClient con withFetch e authInterceptor. Después abro app.routes.ts y señalo loadComponent, authGuard y roleGuard. Esto explica el lazy loading y la separación por rol. La carpeta core contiene comportamiento transversal: config, guards, interceptor, modelos y services HTTP. shared contiene componentes visuales reutilizables como app-icon, empty-state, page-header y status-badge. features contiene páginas y layouts por auth, admission, admin, teacher y student.

Para los patrones abro ReportController y ExcelReportService. El controller representa HTTP y delega; el service usa Apache POI para generar XLSX en memoria. Apache POI es una librería Java, no una API web. Luego muestro DocumentService: OpenPDF crea el PDF local sin Internet. El endpoint devuelve bytes y Angular los recibe como Blob. I Love PDF está preparado mediante IlovePdfClient, pero no participa en el flujo actual; por eso se clasifica como integración futura.

Finalmente explico integraciones. MySQL está validado con Connector/J, Hibernate y JPA. SMTP está implementado y configurable, pero no se validó un envío externo durante este cierre. WhatsApp está desactivado por defecto y WhatsappService solo devuelve un estado controlado; no contiene una llamada HTTP saliente a Cloud API. Los builds backend y frontend pasaron, el backend inició con Java 17 y MySQL 8.0.42, y se validaron XLSX y PDF. Mi idea final es: Yachay tiene capas claras y flujos reales demostrables, pero mantiene límites explícitos para no presentar preparación técnica como operación completa.

### Preguntas para Eduard

**1. ¿Por qué N capas?**

Respuesta breve: Separa responsabilidades y facilita explicar/probar cada flujo.

Respuesta desarrollada: Controllers traducen HTTP, services aplican reglas y repositories persisten. Esta separación reduce acoplamiento aunque algunos controllers administrativos aún concentran lógica.

Evidencia/archivo: paquetes academic, identity, report y notification

**2. ¿Qué parte se aproxima a hexagonal?**

Respuesta breve: Identity.

Respuesta desarrollada: Identity separa domain, application e infrastructure y define ports/inputs. No hay esa misma estructura uniforme en todos los módulos.

Evidencia/archivo: identity/application/ports y identity/infrastructure

**3. ¿Por qué no es hexagonal puro?**

Respuesta breve: Porque el patrón no es transversal.

Respuesta desarrollada: Academic y admissions tienen controllers que acceden directamente a repositories y no todos los adaptadores están detrás de puertos.

Evidencia/archivo: AdminAcademicController.java y AdmissionApplicationController.java

**4. ¿Controller y service hacen lo mismo?**

Respuesta breve: No.

Respuesta desarrollada: El controller maneja HTTP, status y DTO; el service concentra caso de uso o generación. Cuando un controller concentra reglas se reconoce como deuda.

Evidencia/archivo: ReportController.java y ExcelReportService.java

**5. ¿Qué es dependency injection?**

Respuesta breve: El contenedor entrega dependencias.

Respuesta desarrollada: Constructores reciben services/repositories y Spring crea el grafo. Facilita sustitución y evita instanciación dispersa.

Evidencia/archivo: constructores de controllers y services

**6. ¿Qué ventaja tiene lazy loading?**

Respuesta breve: Reduce carga inicial y separa rutas.

Respuesta desarrollada: loadComponent importa cada página/layout cuando se navega, lo que produce chunks lazy verificados en el build.

Evidencia/archivo: app.routes.ts y salida npm run build

**7. ¿Por qué core no contiene páginas?**

Respuesta breve: Porque aloja comportamiento transversal.

Respuesta desarrollada: Config, guards, interceptor, modelos y services deben ser reutilizables e independientes de una pantalla concreta.

Evidencia/archivo: src/app/core

**8. ¿Qué diferencia hay entre core y shared?**

Respuesta breve: Core es comportamiento; shared es UI reutilizable.

Respuesta desarrollada: shared contiene app-icon, empty-state, field-error, page-header y tarjetas; core concentra autenticación y HTTP.

Evidencia/archivo: src/app/core y src/app/shared

**9. ¿Cómo se genera XLSX?**

Respuesta breve: Con Apache POI en memoria.

Respuesta desarrollada: Repositories cargan datos, ExcelReportService escribe celdas en XSSFWorkbook, ReportController devuelve byte[] y Angular descarga Blob.

Evidencia/archivo: ExcelReportService.java y report.ts

**10. ¿Cómo se genera PDF?**

Respuesta breve: Con OpenPDF local.

Respuesta desarrollada: DocumentService consulta la entidad, compone una tabla A4 y DocumentController configura Content-Type y filename.

Evidencia/archivo: DocumentService.java y DocumentController.java

**11. ¿Qué hace GlobalExceptionHandler?**

Respuesta breve: Unifica errores JSON.

Respuesta desarrollada: Captura not found, conflict, validación, ResponseStatusException y errores generales para evitar respuestas inconsistentes.

Evidencia/archivo: GlobalExceptionHandler.java

**12. ¿Qué integración externa está plenamente validada?**

Respuesta breve: MySQL; no SMTP/WhatsApp/I Love PDF.

Respuesta desarrollada: MySQL 8.0.42 se conectó. POI/OpenPDF son librerías locales. SMTP quedó configurable; WhatsApp e I Love PDF no ejecutan el flujo operativo.

Evidencia/archivo: arranque y services de integración


## Jesús — Login, autenticación, autorización, Spring Security y JWT

**Objetivo.** Recorrer el flujo completo desde Reactive Form hasta el SecurityContext y distinguir controles de frontend y backend.

### Orden de exposición

1. Validación del formulario Angular.
2. POST /api/auth/login.
3. Búsqueda de User y BCrypt.
4. Generación de claims y firma JWT.
5. Persistencia de sesión en Angular.
6. Bearer automático.
7. JwtAuthFilter y reglas 401/403.

### Carpetas a abrir

- `C:\E-specter - copia (2)\yachay\yachay-backend\src\main\java\edu\yachay\backend\auth`
- `C:\E-specter - copia (2)\yachay\yachay-backend\src\main\java\edu\yachay\backend\config`
- `C:\E-specter - copia (2)\yachay\yachay-frontend\src\app\core`
- `C:\E-specter - copia (2)\yachay\yachay-frontend\src\app\features\auth\pages\login`

### Archivos a preparar en pestañas

`AuthController.java`, `AuthService.java`, `LoginRequest.java`, `LoginResponse.java`, `JwtService.java`, `JwtAuthFilter.java`, `SecurityConfig.java`, `User.java`, `UserRepository.java`, `auth.ts`, `login.ts`, `auth interceptor`, `auth guard`, `role guard`, `app.routes.ts`

### Fragmentos reales

#### Jesús 1. Envío del formulario de login

- **Archivo:** `C:\E-specter - copia (2)\yachay\yachay-frontend\src\app\features\auth\pages\login\login.ts`
- **Método/elemento:** `login`
- **Líneas aproximadas:** 33–52

```typescript
  login(): void {
    this.submitted.set(true);
    this.errorMessage.set('');
    this.form.markAllAsTouched();

    if (this.form.invalid) return;

    this.loading.set(true);

    this.authService.login(this.form.getRawValue()).subscribe({
      next: (response) => {
        this.loading.set(false);
        this.router.navigateByUrl(this.homeRouteByRole(response.user.role));
      },
      error: (error: unknown) => {
        this.loading.set(false);
        this.errorMessage.set(this.loginErrorMessage(error));
      },
    });
  }
```

**Qué hace.** La página valida el Reactive Form, llama al AuthService y redirige según el rol recibido.

**Lectura línea por línea / puntos clave.**

- markAllAsTouched activa mensajes de validación.
- getRawValue produce LoginRequest.
- subscribe separa éxito y error sin guardar la contraseña.

**Patrón:** Reactive Forms y Service Pattern Angular.

**Conexión entre capas:** El componente no arma URLs; delega HTTP a core/services/auth.ts.

**Frase para exponer:** “La página captura datos y estado visual; el service administra la sesión.”

**Pregunta probable:** ¿Dónde se guarda el token después de un login correcto?

#### Jesús 2. Autenticación con BCrypt y JWT

- **Archivo:** `C:\E-specter - copia (2)\yachay\yachay-backend\src\main\java\edu\yachay\backend\auth\AuthService.java`
- **Método/elemento:** `login`
- **Líneas aproximadas:** 29–42

```java
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email().trim().toLowerCase())
                .orElseThrow(() -> invalidCredentials());

        if (!passwordEncoder.matches(request.password(), user.getEncryptedPassword())) {
            throw invalidCredentials();
        }

        user.setLastSignInAt(LocalDateTime.now());
        userRepository.save(user);

        List<String> roles = resolveRoles(user);
        return new LoginResponse(jwtService.generateToken(user, roles), toAuthUser(user, roles));
    }
```

**Qué hace.** Busca el usuario normalizando el correo, compara el hash BCrypt y emite token más usuario autenticado.

**Lectura línea por línea / puntos clave.**

- findByEmail consulta mediante repository.
- passwordEncoder.matches compara sin descifrar el hash.
- generateToken incorpora identidad y roles.

**Patrón:** Service Layer, Repository Pattern y DTO Pattern.

**Conexión entre capas:** AuthController recibe LoginRequest y devuelve LoginResponse; UserRepository llega a auth_users.

**Frase para exponer:** “Nunca se compara ni se almacena una contraseña en texto plano.”

**Pregunta probable:** ¿BCrypt descifra la contraseña guardada?

#### Jesús 3. Claims y firma del JWT

- **Archivo:** `C:\E-specter - copia (2)\yachay\yachay-backend\src\main\java\edu\yachay\backend\auth\JwtService.java`
- **Método/elemento:** `generateToken`
- **Líneas aproximadas:** 31–45

```java
    public String generateToken(User user, List<String> roles) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plusMillis(expirationMillis);

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("userId", user.getId())
                .claim("roles", roles)
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiresAt))
                .signWith(signingKey)
                .compact();
    }

    public Claims parseClaims(String token) {
```

**Qué hace.** Construye el JWT con subject, userId, roles, fecha de emisión, expiración y firma HMAC.

**Lectura línea por línea / puntos clave.**

- subject usa el correo.
- roles se guarda como claim para reconstruir authorities.
- signWith impide aceptar tokens modificados.

**Patrón:** Service Pattern y Token-based Authentication.

**Conexión entre capas:** El frontend conserva el token; JwtAuthFilter vuelve a verificarlo en cada petición protegida.

**Frase para exponer:** “El JWT está firmado, no cifrado; no debe contener secretos.”

**Pregunta probable:** ¿Qué significan sub, userId, roles, iat y exp?

#### Jesús 4. Filtro de autenticación por petición

- **Archivo:** `C:\E-specter - copia (2)\yachay\yachay-backend\src\main\java\edu\yachay\backend\auth\JwtAuthFilter.java`
- **Método/elemento:** `doFilterInternal`
- **Líneas aproximadas:** 50–74

```java
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorization.substring(7);

        try {
            String email = jwtService.extractSubject(token);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                User user = userRepository.findByEmail(email)
                        .orElseThrow(() -> new JwtService.InvalidJwtException("Usuario del token no existe.", null));

                if (jwtService.isValidForUser(token, user)) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            user,
                            null,
```

**Qué hace.** Extrae Bearer, valida claims, carga User y crea la Authentication del SecurityContext.

**Lectura línea por línea / puntos clave.**

- Sin encabezado Bearer continúa sin autenticar.
- parseClaims detecta firma inválida o expiración.
- SecurityContextHolder hace visible el usuario a controllers y reglas.

**Patrón:** Filter Chain Pattern.

**Conexión entre capas:** Se ejecuta antes de UsernamePasswordAuthenticationFilter por configuración explícita.

**Frase para exponer:** “El guard evita navegación; este filtro protege realmente la API.”

**Pregunta probable:** ¿Qué ocurre cuando el token está vencido?

#### Jesús 5. Políticas HTTP y roles

- **Archivo:** `C:\E-specter - copia (2)\yachay\yachay-backend\src\main\java\edu\yachay\backend\config\SecurityConfig.java`
- **Método/elemento:** `securityFilterChain`
- **Líneas aproximadas:** 37–59

```java
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) ->
                                writeSecurityError(response, HttpServletResponse.SC_UNAUTHORIZED,
                                        "Unauthorized", "Autenticacion requerida o token JWT invalido."))
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                writeSecurityError(response, HttpServletResponse.SC_FORBIDDEN,
                                        "Forbidden", "No tienes permisos para acceder a este recurso."))
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(HttpMethod.POST,
                                "/auth/login",
                                "/auth/forgot-password",
                                "/auth/reset-password",
                                "/admisiones"
                        ).permitAll()
                        .requestMatchers("/admin/**").hasAuthority("ADMINISTRADOR")
                        .requestMatchers("/docente/**").hasAuthority("DOCENTE")
```

**Qué hace.** Declara API stateless, errores 401/403, rutas públicas y autoridades por prefijo.

**Lectura línea por línea / puntos clave.**

- CSRF se desactiva para la API stateless con Bearer.
- SessionCreationPolicy.STATELESS evita sesión HTTP.
- hasAuthority protege admin, docente y alumno.

**Patrón:** Configuration Pattern y Chain of Responsibility.

**Conexión entre capas:** Las authorities provienen de los roles reconstruidos por JwtAuthFilter.

**Frase para exponer:** “401 significa no autenticado; 403 significa autenticado sin autoridad.”

**Pregunta probable:** ¿Qué rutas son públicas y por qué?

#### Jesús 6. Interceptor JWT de Angular

- **Archivo:** `C:\E-specter - copia (2)\yachay\yachay-frontend\src\app\core\interceptors\auth.ts`
- **Método/elemento:** `authInterceptor`
- **Líneas aproximadas:** 8–31

```typescript
export const authInterceptor: HttpInterceptorFn = (request, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const token = authService.getToken();

  if (!token) {
    return next(request);
  }

  const authRequest = request.clone({
    setHeaders: {
      Authorization: `Bearer ${token}`,
    },
  });

  return next(authRequest).pipe(
    catchError((error: unknown) => {
      if (error instanceof HttpErrorResponse && error.status === 401) {
        authService.logout();
        void router.navigateByUrl('/login');
      }

      return throwError(() => error);
    }),
```

**Qué hace.** Clona las peticiones con Authorization Bearer y cierra la sesión local ante 401.

**Lectura línea por línea / puntos clave.**

- getToken no accede a localStorage durante SSR.
- request.clone preserva inmutabilidad.
- catchError redirige al login si la sesión dejó de ser válida.

**Patrón:** Interceptor Pattern.

**Conexión entre capas:** Se registra en app.config.ts y actúa sobre todos los services HttpClient.

**Frase para exponer:** “El interceptor automatiza el encabezado; no autoriza por sí solo.”

**Pregunta probable:** ¿Por qué se clona la petición HTTP?

### Mini demo

Login correcto, abrir Network sin mostrar la contraseña, comprobar Bearer, intentar /admin sin JWT (401) y con DOCENTE (403).

### Errores que debe evitar

- Decir que el JWT está cifrado.
- Confundir guard con seguridad del backend.
- Confundir 401 con 403.
- Mostrar JWT secret o contraseñas.

### Guion hablado (4–6 minutos)

Mi parte es el flujo de seguridad completo. Comienzo en login.ts. El componente usa Reactive Forms; si el formulario es inválido marca los controles y no envía nada. Cuando es válido llama a AuthService.login con email y password. AuthService Angular usa HttpClient para POST /api/auth/login. El interceptor todavía no agrega Bearer porque en ese momento no existe token.

En backend abro AuthController. Este recibe LoginRequest validado y delega en AuthService.login. En AuthService se normaliza el correo, UserRepository busca en auth_users y BCryptPasswordEncoder.matches compara la contraseña ingresada con encrypted_password. BCrypt no descifra; aplica el algoritmo y verifica el hash con su salt. Si falla, se responde 401 sin decir si el correo o la contraseña era incorrecto. Si funciona, se actualiza lastSignInAt, se resuelven roles y JwtService genera el token.

En JwtService señalo cinco claims. sub contiene el correo; userId el identificador; roles las autoridades; iat la emisión y exp la expiración. El token se firma con una clave HMAC derivada del secret. Está firmado, no cifrado, por eso nunca guardamos contraseñas en sus claims. Angular recibe LoginResponse, persiste token y usuario en localStorage solo cuando está en navegador, algo importante porque el proyecto usa SSR.

En cada petición posterior authInterceptor clona la request y agrega Authorization: Bearer. En el backend JwtAuthFilter extrae el token, valida firma y expiración, busca al usuario y crea una Authentication con authorities. SecurityConfig define sesión STATELESS y aplica ADMINISTRADOR a /admin/**, DOCENTE a /docente/** y ALUMNO a /alumno/**. También deja públicas las operaciones de login, recuperación preparada y admisión.

Los guards de Angular mejoran navegación. authGuard evita entrar sin token local y roleGuard redirige según el rol. Pero no son la seguridad definitiva porque el navegador puede manipularse. El backend vuelve a validar todo. La evidencia práctica fue: sin JWT, un endpoint admin devolvió 401; con un token DOCENTE intentando acceder a admin devolvió 403. Esa diferencia es central: 401 significa que no existe autenticación válida; 403 que sí existe, pero no tiene permiso.

Si el profesor pregunta por expiración, JwtService.parseClaims rechaza el token y el interceptor elimina la sesión local ante 401. Si pregunta por SQL Injection, el login usa un método derivado de Spring Data, no concatena SQL. Como limitación, forgot-password y reset-password actualmente responden 204 pero no implementan todavía el ciclo completo de token de recuperación. Mi cierre es que la seguridad está distribuida en responsabilidades complementarias: formulario, service, interceptor y guards en Angular; filtro, contexto y reglas de autorización en Spring.

### Preguntas para Jesús

**1. ¿Dónde se valida primero el login?**

Respuesta breve: En el Reactive Form.

Respuesta desarrollada: Angular evita solicitudes incompletas, pero el backend vuelve a validar LoginRequest y credenciales.

Evidencia/archivo: login.ts y LoginRequest.java

**2. ¿Cómo se verifica la contraseña?**

Respuesta breve: Con BCrypt.matches.

Respuesta desarrollada: Se compara la entrada con encrypted_password; no se descifra ni se retorna el hash.

Evidencia/archivo: AuthService.login

**3. ¿Qué contiene el JWT?**

Respuesta breve: sub, userId, roles, iat y exp.

Respuesta desarrollada: JwtService agrega claims mínimos y firma. El contenido puede decodificarse, por eso no incluye secretos.

Evidencia/archivo: JwtService.generateToken

**4. ¿Cómo se firma?**

Respuesta breve: Con una SecretKey HMAC.

Respuesta desarrollada: La clave se deriva del jwt.secret y JJWT usa signWith. parseClaims verifica con la misma clave.

Evidencia/archivo: JwtService.java

**5. ¿Qué pasa si modifican roles en el token?**

Respuesta breve: La firma deja de ser válida.

Respuesta desarrollada: JwtAuthFilter rechaza parseClaims y no establece Authentication; la petición protegida termina en 401.

Evidencia/archivo: JwtService.parseClaims y JwtAuthFilter

**6. ¿Qué pasa cuando expira?**

Respuesta breve: Se rechaza y Angular cierra sesión.

Respuesta desarrollada: JJWT valida exp; el backend responde 401 y authInterceptor elimina token/usuario y navega a login.

Evidencia/archivo: JwtService.java y auth interceptor

**7. ¿Por qué backend valida si hay guards?**

Respuesta breve: Porque el cliente no es confiable.

Respuesta desarrollada: Un usuario puede llamar la API sin Angular. Solo Spring Security controla el recurso real.

Evidencia/archivo: SecurityConfig y guards

**8. ¿Cuáles son las rutas públicas?**

Respuesta breve: Login, recuperación preparada y admisión.

Respuesta desarrollada: SecurityConfig permite POST /auth/login, forgot-password, reset-password y /admisiones, además de OPTIONS.

Evidencia/archivo: SecurityConfig.securityFilterChain

**9. ¿Qué diferencia hay entre 401 y 403?**

Respuesta breve: 401 sin autenticación; 403 sin permiso.

Respuesta desarrollada: Se validó 401 sin JWT y 403 con DOCENTE contra admin.

Evidencia/archivo: SecurityConfig y smoke tests

**10. ¿Qué hace el interceptor?**

Respuesta breve: Agrega Bearer y reacciona a 401.

Respuesta desarrollada: Clona la petición, incorpora Authorization y limpia localStorage si la sesión es inválida.

Evidencia/archivo: core/interceptors/auth.ts

**11. ¿Qué hace authGuard?**

Respuesta breve: Comprueba sesión local.

Respuesta desarrollada: Si no hay token retorna UrlTree a /login. Es navegación, no protección de datos.

Evidencia/archivo: core/guards/auth.ts

**12. ¿Qué hace roleGuard?**

Respuesta breve: Compara rol y ruta.

Respuesta desarrollada: Lee data.roles y redirige al dashboard del rol si no coincide.

Evidencia/archivo: core/guards/role.ts

**13. ¿Cómo se controla cada prefijo?**

Respuesta breve: Con hasAuthority.

Respuesta desarrollada: ADMINISTRADOR protege /admin/**, DOCENTE /docente/** y ALUMNO /alumno/**.

Evidencia/archivo: SecurityConfig.java

**14. ¿Dónde se guarda el token?**

Respuesta breve: En localStorage solo en navegador.

Respuesta desarrollada: AuthService usa isPlatformBrowser para no acceder durante SSR y mantiene un signal de usuario.

Evidencia/archivo: core/services/auth.ts

**15. ¿Recuperación de contraseña está completa?**

Respuesta breve: No, está parcial.

Respuesta desarrollada: Los endpoints devuelven 204 pero no existe generación/validación de token ni cambio persistido en ese flujo.

Evidencia/archivo: AuthController.java


## Chelsea — MySQL, scripts, JPA, Hibernate, entidades y DataSeeder

**Objetivo.** Explicar cómo las entidades reales se mapean a MySQL y qué evidencia existe de conexión, relaciones e idempotencia.

### Orden de exposición

1. Mostrar placeholders sin abrir application-local.yaml.
2. Explicar Connector/J, JPA y Hibernate.
3. Abrir User y Course.
4. Señalar PK, FK y relaciones.
5. Abrir un repository.
6. Explicar ensureUser idempotente.
7. Aclarar la ausencia actual de scripts versionados en yachay-db.

### Carpetas a abrir

- `C:\E-specter - copia (2)\yachay\yachay-backend\src\main\resources`
- `C:\E-specter - copia (2)\yachay\yachay-backend\src\main\java\edu\yachay\backend\identity\domain`
- `C:\E-specter - copia (2)\yachay\yachay-backend\src\main\java\edu\yachay\backend\academic\domain`
- `C:\E-specter - copia (2)\yachay\yachay-db`

### Archivos a preparar en pestañas

`application.yaml`, `application-local.example.yaml`, `pom.xml`, `User.java`, `Profile.java`, `StudentProfile.java`, `TeacherProfile.java`, `Course.java`, `UserRepository.java`, `DataSeeder.java`, `JpaConfig.java`, `yachay-db/README.md`

### Fragmentos reales

#### Chelsea 1. Conexión MySQL y JPA

- **Archivo:** `C:\E-specter - copia (2)\yachay\yachay-backend\src\main\resources\application.yaml`
- **Método/elemento:** `spring.datasource / spring.jpa`
- **Líneas aproximadas:** 1–18

```yaml
spring:
  application:
    name: backend
  datasource:
    url: ${DB_URL:jdbc:mysql://localhost:3306/yachay?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Lima}
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:}
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        format_sql: true
    show-sql: false
    open-in-view: false
  mail:
    host: ${MAIL_HOST:mail.tudominio.com}
```

**Qué hace.** Configura MySQL mediante variables de entorno, Hibernate update para desarrollo y open-in-view desactivado.

**Lectura línea por línea / puntos clave.**

- DB_URL, DB_USERNAME y DB_PASSWORD admiten configuración externa.
- ddl-auto=update sincroniza el esquema de desarrollo.
- open-in-view=false obliga a resolver relaciones dentro de transacciones.

**Patrón:** Externalized Configuration.

**Conexión entre capas:** MySQL Connector/J crea la conexión y Spring Data JPA administra repositories.

**Frase para exponer:** “La configuración sensible se reemplaza en el perfil local ignorado, no en Git.”

**Pregunta probable:** ¿Por qué ddl-auto=update no es suficiente para producción?

#### Chelsea 2. Dependencias de persistencia y MySQL

- **Archivo:** `C:\E-specter - copia (2)\yachay\yachay-backend\pom.xml`
- **Método/elemento:** `dependencies JPA/Hibernate/MySQL`
- **Líneas aproximadas:** 42–66

```xml
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-mail</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-security</artifactId>
		</dependency>

		<!-- Spring Data JPA (incluye jakarta.persistence y transacciones) -->
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-jpa</artifactId>
		</dependency>

		<!-- Si quieres declarar explícitamente la API de Jakarta Persistence -->
		<dependency>
			<groupId>jakarta.persistence</groupId>
			<artifactId>jakarta.persistence-api</artifactId>
		</dependency>

		<!-- Si quieres declarar explícitamente soporte de transacciones Spring -->
		<dependency>
			<groupId>org.springframework</groupId>
			<artifactId>spring-tx</artifactId>
```

**Qué hace.** Declara Spring Data JPA, Hibernate y MySQL Connector/J como piezas de persistencia.

**Lectura línea por línea / puntos clave.**

- spring-boot-starter-data-jpa integra repositories y transacciones.
- hibernate-core implementa Jakarta Persistence.
- mysql-connector-j es el driver JDBC en runtime.

**Patrón:** Dependency Management.

**Conexión entre capas:** Las anotaciones de entidades se traducen a SQL ejecutado contra MySQL.

**Frase para exponer:** “JPA es la especificación; Hibernate es la implementación utilizada.”

**Pregunta probable:** ¿Cuál es la diferencia entre JPA e Hibernate?

#### Chelsea 3. Entidad de autenticación y relación de roles

- **Archivo:** `C:\E-specter - copia (2)\yachay\yachay-backend\src\main\java\edu\yachay\backend\identity\domain\models\User.java`
- **Método/elemento:** `mapeo User`
- **Líneas aproximadas:** 9–32

```java
@Table(name = "auth_users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", unique = true, nullable = false, length = 255)
    private String email;

    @Column(name = "phone", unique = true, length = 20)
    private String phone;

    @Column(name = "display_name", length = 255)
    private String displayName;

    @Builder.Default
    @Column(name = "provider_type", length = 50)
    private String providerType = "local";
```

**Qué hace.** Mapea User a auth_users y define identidad, correo y contraseña cifrada.

**Lectura línea por línea / puntos clave.**

- @Entity/@Table fijan el modelo persistente.
- @Id y @GeneratedValue definen la clave primaria.
- encrypted_password almacena el hash BCrypt, no el texto original.

**Patrón:** Entity Pattern y ORM.

**Conexión entre capas:** UserRepository opera sobre esta entidad y AuthService la usa en el login.

**Frase para exponer:** “La entidad representa persistencia; LoginResponse es el contrato expuesto.”

**Pregunta probable:** ¿Por qué no se retorna User directamente al frontend?

#### Chelsea 4. Relaciones del curso

- **Archivo:** `C:\E-specter - copia (2)\yachay\yachay-backend\src\main\java\edu\yachay\backend\academic\domain\models\Course.java`
- **Método/elemento:** `mapeo Course`
- **Líneas aproximadas:** 8–31

```java

@Entity
@Table(name = "yachay_courses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_year_id", nullable = false)
    private AcademicYear academicYear;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
```

**Qué hace.** Mapea yachay_courses y sus claves foráneas hacia colegio, año, materia y docente.

**Lectura línea por línea / puntos clave.**

- @ManyToOne expresa muchas filas de curso por entidad relacionada.
- @JoinColumn nombra cada FK real.
- FetchType.LAZY evita cargar toda la relación sin necesidad.

**Patrón:** Entity Pattern y Association Mapping.

**Conexión entre capas:** CourseRepository consulta; AdminAcademicController construye respuestas y ExcelReportService genera XLSX.

**Frase para exponer:** “La FK está en la tabla del curso porque es el lado muchos de la relación.”

**Pregunta probable:** ¿Qué efecto tiene FetchType.LAZY?

#### Chelsea 5. Repository derivado de Spring Data

- **Archivo:** `C:\E-specter - copia (2)\yachay\yachay-backend\src\main\java\edu\yachay\backend\identity\domain\repositories\UserRepository.java`
- **Método/elemento:** `UserRepository`
- **Líneas aproximadas:** 1–17

```java
package edu.yachay.backend.identity.domain.repositories;

import edu.yachay.backend.identity.domain.models.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph(attributePaths = {"roles", "profile"})
    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);

    boolean existsByEmail(String email);
```

**Qué hace.** Extiende JpaRepository y declara consultas derivadas por nombre de método.

**Lectura línea por línea / puntos clave.**

- JpaRepository aporta CRUD sin SQL manual.
- findByEmail se traduce a consulta parametrizada.
- findByRolesName resuelve la relación de roles.

**Patrón:** Repository Pattern.

**Conexión entre capas:** Services dependen del contrato de repository y Hibernate genera el SQL.

**Frase para exponer:** “El repository separa persistencia de reglas de aplicación.”

**Pregunta probable:** ¿Cómo ayuda Spring Data a reducir riesgo de SQL Injection?

#### Chelsea 6. DataSeeder idempotente para usuarios

- **Archivo:** `C:\E-specter - copia (2)\yachay\yachay-backend\src\main\java\edu\yachay\backend\config\DataSeeder.java`
- **Método/elemento:** `ensureUser`
- **Líneas aproximadas:** 372–396

```java
    private User ensureUser(SeedUser seed) {
        User user = userRepository.findByEmail(seed.email())
                .orElseGet(() -> User.builder()
                        .email(seed.email())
                        .displayName(seed.displayName())
                        .providerType("local")
                        .encryptedPassword(passwordEncoder.encode(seed.password()))
                        .emailConfirmedAt(LocalDateTime.now())
                        .roles(new java.util.HashSet<>())
                        .build());

        user.setDisplayName(seed.displayName());
        user.setProviderType("local");
        if (user.getEmailConfirmedAt() == null) {
            user.setEmailConfirmedAt(LocalDateTime.now());
        }
        if (!passwordMatches(seed.password(), user.getEncryptedPassword())) {
            user.setEncryptedPassword(passwordEncoder.encode(seed.password()));
        }
        if (user.getRoles() == null) {
            user.setRoles(new java.util.HashSet<>());
        }
        user.getRoles().add(seed.role());

        return userRepository.save(user);
```

**Qué hace.** Busca por correo antes de crear, actualiza datos controlados y evita duplicar roles.

**Lectura línea por línea / puntos clave.**

- findByEmail decide crear o reutilizar.
- passwordMatches evita reescribir el hash si no cambió.
- Set<Role> y add mantienen asociación sin duplicado lógico.

**Patrón:** Idempotent Seeder.

**Conexión entre capas:** Se ejecuta al iniciar y usa repositories JPA sobre MySQL.

**Frase para exponer:** “Idempotente significa que repetir el arranque conserva un estado equivalente.”

**Pregunta probable:** ¿Cómo demuestra el código que el seeder es idempotente?

### Mini demo

Mostrar MySQL 8.0.42 conectado y las tablas actuales; abrir application.yaml y dos entidades sin revelar credenciales locales.

### Errores que debe evitar

- Mostrar application-local.yaml.
- Afirmar que yachay-db contiene scripts actuales.
- Confundir JPA con la base de datos.
- Defender ddl-auto=update como estrategia productiva.

### Guion hablado (4–6 minutos)

Yo explicaré la persistencia. No abriré application-local.yaml porque contiene configuración local ignorada por Git. Abro application.yaml y muestro placeholders DB_URL, DB_USERNAME y DB_PASSWORD. Eso permite que el mismo artefacto funcione con variables del entorno. La URL oficial usa MySQL y el driver es com.mysql.cj.jdbc.Driver. En la validación real Hibernate se conectó a MySQL 8.0.42.

Luego diferencio tecnologías. JDBC es la conexión de bajo nivel; MySQL Connector/J es el driver. JPA o Jakarta Persistence es la especificación que define anotaciones y contratos. Hibernate es la implementación que interpreta las entidades y genera SQL. Spring Data JPA agrega repositories como UserRepository. Por eso no son sinónimos: cada uno ocupa una capa distinta.

Abro User.java. @Entity y @Table enlazan la clase con auth_users. @Id y @GeneratedValue definen la clave primaria. encrypted_password guarda el hash BCrypt. Después muestro Course.java, donde @ManyToOne y @JoinColumn representan claves foráneas hacia schools, academic year, subject y teacher profile. FetchType.LAZY evita traer relaciones completas si no se usan. También señalo Profile, StudentProfile y TeacherProfile para explicar que credenciales, identidad personal y datos académicos se separan.

En UserRepository muestro que JpaRepository aporta CRUD y que findByEmail o findByRolesName generan consultas parametrizadas. Eso reduce SQL manual y evita concatenaciones inseguras. Sin embargo, cualquier consulta nativa futura debe seguir usando parámetros. Los services operan dentro de transacciones porque open-in-view está desactivado.

DataSeeder implementa CommandLineRunner. En ensureUser primero busca por correo. Si existe actualiza datos controlados y conserva la identidad; si no, crea. También revisa el hash y agrega roles sobre un Set. Para cursos, matrículas, tareas, notas y notificaciones hay verificaciones exists antes de guardar. Por eso se describe como idempotente: ejecutar varias veces produce un estado equivalente y no duplica las semillas identificadas.

Las tablas actuales mapeadas incluyen auth_users, roles, user_roles, profiles, student_profiles, teacher_profiles, guardian_profiles, schools y las tablas yachay_ de admisión, año, materias, cursos, secciones, matrículas, tareas, notas, comunicados, calendario y notificaciones. La limitación importante es documental: yachay-db solo contiene un README y no tiene scripts SQL versionados actuales. El esquema de desarrollo se mantiene con ddl-auto=update, lo cual sirve localmente pero no reemplaza Flyway o Liquibase. Mi conclusión es que la persistencia real está validada, pero la migración reproducible de producción queda pendiente.

### Preguntas para Chelsea

**1. ¿Qué base oficial usa Yachay?**

Respuesta breve: MySQL.

Respuesta desarrollada: application.yaml usa jdbc:mysql y Connector/J; la conexión validada fue MySQL 8.0.42.

Evidencia/archivo: application.yaml y pom.xml

**2. ¿Qué es Connector/J?**

Respuesta breve: El driver JDBC de MySQL.

Respuesta desarrollada: Permite que Hikari/Hibernate abran conexiones Java contra MySQL.

Evidencia/archivo: pom.xml

**3. ¿Qué hace ddl-auto=update?**

Respuesta breve: Ajusta esquema en desarrollo.

Respuesta desarrollada: Hibernate compara mappings y crea/actualiza objetos; no aporta migraciones versionadas ni rollback confiable.

Evidencia/archivo: application.yaml

**4. ¿Por qué no abrir application-local.yaml?**

Respuesta breve: Puede contener secretos locales.

Respuesta desarrollada: Está ignorado por Git y solo debe existir en cada equipo. La exposición usa application-local.example.yaml.

Evidencia/archivo: .gitignore y application-local.example.yaml

**5. ¿Qué tabla guarda roles?**

Respuesta breve: roles y la unión user_roles.

Respuesta desarrollada: User mantiene una relación many-to-many; la tabla intermedia conecta user_id y role_id.

Evidencia/archivo: User.java y Role.java

**6. ¿Qué diferencia User de Profile?**

Respuesta breve: Acceso frente a identidad personal.

Respuesta desarrollada: User contiene correo/hash/roles; Profile nombres y estado; perfiles especializados añaden datos académicos.

Evidencia/archivo: User.java y Profile.java

**7. ¿Qué es una clave foránea?**

Respuesta breve: Referencia a otra fila/tabla.

Respuesta desarrollada: JoinColumn en Course materializa school_id, academic_year_id, subject_id y teacher_id.

Evidencia/archivo: Course.java

**8. ¿Qué es @ManyToOne?**

Respuesta breve: Muchas entidades apuntan a una relacionada.

Respuesta desarrollada: Muchos cursos pueden pertenecer al mismo colegio, año, materia o docente.

Evidencia/archivo: Course.java

**9. ¿Qué aporta JpaRepository?**

Respuesta breve: CRUD, paginación y consultas derivadas.

Respuesta desarrollada: UserRepository hereda save/find/delete y agrega búsquedas tipadas por email/roles.

Evidencia/archivo: UserRepository.java

**10. ¿Cómo es idempotente DataSeeder?**

Respuesta breve: Busca/existe antes de guardar.

Respuesta desarrollada: ensureUser reutiliza por email y otras semillas consultan exists por claves naturales.

Evidencia/archivo: DataSeeder.java

**11. ¿Existen scripts actuales en yachay-db?**

Respuesta breve: No; solo README.

Respuesta desarrollada: La carpeta oficial fue revisada y no contiene SQL versionado. El archivo backend/doc/tablas_mysql.sql es legado y contradice nombres actuales.

Evidencia/archivo: yachay-db/README.md

**12. ¿Qué falta para producción?**

Respuesta breve: Migraciones y pruebas de esquema.

Respuesta desarrollada: Flyway o Liquibase permitirían versionar cambios, reproducir ambientes y revisar rollback.

Evidencia/archivo: Limitaciones documentadas


## Sayuri — Roles, usuarios, alumnos, docentes y módulo administrador

**Objetivo.** Demostrar el flujo de creación administrativa y la separación entre credenciales, perfil personal y perfil académico.

### Orden de exposición

1. Diferenciar User y Profile.
2. Mostrar Role y user_roles.
3. Abrir el formulario de usuarios.
4. Seguir UserService Angular.
5. Abrir createUser backend.
6. Señalar BCrypt y repositories.
7. Volver a loadUsers y la tabla.

### Carpetas a abrir

- `C:\E-specter - copia (2)\yachay\yachay-backend\src\main\java\edu\yachay\backend\identity`
- `C:\E-specter - copia (2)\yachay\yachay-frontend\src\app\features\admin`
- `C:\E-specter - copia (2)\yachay\yachay-frontend\src\app\core\services`

### Archivos a preparar en pestañas

`User.java`, `Role.java`, `Profile.java`, `StudentProfile.java`, `TeacherProfile.java`, `UserRepository.java`, `RoleRepository.java`, `ProfileRepository.java`, `UserService.java`, `StudentProfileService.java`, `TeacherProfileService.java`, `AdminIdentityReadController.java`, `admin-layout.ts`, `usuarios.ts`, `alumnos.ts`, `docentes.ts`

### Fragmentos reales

#### Sayuri 1. Creación administrativa de usuario

- **Archivo:** `C:\E-specter - copia (2)\yachay\yachay-backend\src\main\java\edu\yachay\backend\identity\infrastructure\adapters\inputs\AdminIdentityReadController.java`
- **Método/elemento:** `createUser`
- **Líneas aproximadas:** 59–72

```java
    @PostMapping("/usuarios")
    @Transactional
    public ResponseEntity<AdminUserResponse> createUser(@Valid @RequestBody CreateAdminUserRequest request) {
        User user = createBaseUser(
                request.email(),
                resolvePassword(request.passwordTemporal(), request.password()),
                request.nombres(),
                request.apellidos(),
                request.rol()
        );

        setProfileActive(user, request.activo());
        return ResponseEntity.status(HttpStatus.CREATED).body(toAdminUser(user));
    }
```

**Qué hace.** Valida el DTO, resuelve el rol, aplica BCrypt y devuelve 201 con una respuesta administrativa.

**Lectura línea por línea / puntos clave.**

- @PostMapping expone /admin/usuarios.
- roleRepository valida que el rol exista.
- passwordEncoder.encode persiste un hash.

**Patrón:** Controller Pattern y DTO Pattern.

**Conexión entre capas:** Angular UserService envía el POST; UserRepository guarda auth_users y user_roles.

**Frase para exponer:** “La contraseña se transforma antes de alcanzar la base de datos.”

**Pregunta probable:** ¿Por qué el controller devuelve 201 y no 200?

#### Sayuri 2. Creación administrativa de alumno

- **Archivo:** `C:\E-specter - copia (2)\yachay\yachay-backend\src\main\java\edu\yachay\backend\identity\infrastructure\adapters\inputs\AdminIdentityReadController.java`
- **Método/elemento:** `createStudent`
- **Líneas aproximadas:** 110–134

```java
    @PostMapping("/alumnos")
    @Transactional
    public ResponseEntity<AdminStudentResponse> createStudent(@Valid @RequestBody CreateStudentRequest request) {
        if (studentProfileRepository.existsByStudentCode(request.codigo())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El codigo de estudiante ya existe.");
        }

        User user = createBaseUser(
                request.email(),
                resolvePassword(request.passwordTemporal(), request.password()),
                request.nombres(),
                request.apellidos(),
                "ALUMNO"
        );

        School school = defaultSchool();
        Profile profile = user.getProfile();
        StudentProfile student = StudentProfile.builder()
                .profile(profile)
                .school(school)
                .studentCode(request.codigo())
                .gradeLevel(parseGradeLevel(request.grado()))
                .section(request.seccion())
                .enrollmentDate(request.fechaMatricula() != null ? request.fechaMatricula() : LocalDate.now())
                .build();
```

**Qué hace.** Construye User, Profile y StudentProfile dentro de una transacción para mantener consistencia.

**Lectura línea por línea / puntos clave.**

- Se valida correo y código antes de persistir.
- El usuario recibe rol ALUMNO.
- Profile separa datos personales de credenciales.

**Patrón:** Transaction Script controlado y Aggregate Assembly.

**Conexión entre capas:** Tres repositories escriben auth_users, profiles y student_profiles.

**Frase para exponer:** “User responde acceso; Profile identidad; StudentProfile información académica.”

**Pregunta probable:** ¿Qué pasaría si falla la última escritura de la transacción?

#### Sayuri 3. Servicio de aplicación para User

- **Archivo:** `C:\E-specter - copia (2)\yachay\yachay-backend\src\main\java\edu\yachay\backend\identity\application\services\UserService.java`
- **Método/elemento:** `createUser`
- **Líneas aproximadas:** 27–49

```java
    public UserDTO createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceConflictException("El email '" + request.getEmail() + "' ya esta registrado");
        }

        if (request.getPhone() != null && userRepository.existsByPhone(request.getPhone())) {
            throw new ResourceConflictException("El telefono '" + request.getPhone() + "' ya esta registrado");
        }

        User user = User.builder()
                .email(request.getEmail())
                .phone(request.getPhone())
                .displayName(request.getFirstName() + " " + request.getLastName())
                .encryptedPassword(passwordEncoder.encode(request.getPassword()))
                .build();

        if (request.getRoleNames() != null && !request.getRoleNames().isEmpty()) {
            Set<Role> roles = roleRepository.findByNameIn(request.getRoleNames());
            user.setRoles(roles);
        }

        User savedUser = userRepository.save(user);

```

**Qué hace.** Implementa el puerto de entrada, controla conflictos y transforma entidad a DTO.

**Lectura línea por línea / puntos clave.**

- existsByEmail aplica regla de unicidad antes de guardar.
- IdentityMapper evita exponer la entidad.
- @Transactional delimita la unidad de trabajo.

**Patrón:** Application Service y Port/Adapter en identity.

**Conexión entre capas:** Controller depende de UserServicePort; repository es el adaptador de persistencia disponible.

**Frase para exponer:** “Identity se aproxima a hexagonal porque separa puerto, servicio, dominio y adaptador de entrada.”

**Pregunta probable:** ¿Qué ventaja aporta UserServicePort?

#### Sayuri 4. Servicio de perfil de alumno

- **Archivo:** `C:\E-specter - copia (2)\yachay\yachay-backend\src\main\java\edu\yachay\backend\identity\application\services\StudentProfileService.java`
- **Método/elemento:** `createStudentProfile`
- **Líneas aproximadas:** 32–55

```java
    public StudentProfileDTO createStudentProfile(CreateStudentProfileRequest request) {
        Profile profile = profileRepository.findByUserId(parseUserId(request.getUserId()))
                .orElseThrow(() -> new ResourceNotFoundException("Perfil para usuario " + request.getUserId() + " no encontrado"));

        School school = schoolRepository.findById(request.getSchoolId())
                .orElseThrow(() -> new ResourceNotFoundException("Escuela con ID " + request.getSchoolId() + " no encontrada"));

        if (request.getStudentCode() != null && studentProfileRepository.existsByStudentCode(request.getStudentCode())) {
            throw new ResourceConflictException("El codigo de estudiante '" + request.getStudentCode() + "' ya existe");
        }

        StudentProfile studentProfile = StudentProfile.builder()
                .profile(profile)
                .school(school)
                .studentCode(request.getStudentCode())
                .gradeLevel(request.getGradeLevel())
                .section(request.getSection())
                .enrollmentDate(request.getEnrollmentDate())
                .build();

        StudentProfile savedStudentProfile = studentProfileRepository.save(studentProfile);
        return mapper.toStudentProfileDTO(savedStudentProfile);
    }

```

**Qué hace.** Valida dependencias y unicidad de perfil/código antes de crear StudentProfile.

**Lectura línea por línea / puntos clave.**

- Profile y School deben existir.
- Los métodos exists evitan relaciones duplicadas.
- IdentityMapper retorna StudentProfileDTO.

**Patrón:** Application Service y Repository Pattern.

**Conexión entre capas:** El DTO de entrada llega desde controller y termina en student_profiles con FKs válidas.

**Frase para exponer:** “El service concentra reglas; el repository concentra acceso a datos.”

**Pregunta probable:** ¿Por qué se validan Profile y School antes del save?

#### Sayuri 5. Service HTTP de usuarios en Angular

- **Archivo:** `C:\E-specter - copia (2)\yachay\yachay-frontend\src\app\core\services\user.ts`
- **Método/elemento:** `list / createUser / updateStatus`
- **Líneas aproximadas:** 21–42

```typescript
  list(): Observable<AdminUser[]> {
    return this.getUsers();
  }

  getUsers(): Observable<AdminUser[]> {
    return this.http.get<AdminUser[]>(`${API_URL}/admin/usuarios`);
  }

  createUser(payload: CreateAdminUserRequest): Observable<AdminUser> {
    return this.http.post<AdminUser>(`${API_URL}/admin/usuarios`, payload);
  }

  updateUser(id: number, payload: UpdateAdminUserRequest): Observable<AdminUser> {
    return this.http.put<AdminUser>(`${API_URL}/admin/usuarios/${id}`, payload);
  }

  updateStatus(id: number, payload: UpdateAdminUserStatusRequest): Observable<AdminUser> {
    return this.http.patch<AdminUser>(`${API_URL}/admin/usuarios/${id}/estado`, payload);
  }

  resetPassword(id: number): Observable<ResetAdminUserPasswordResponse> {
    return this.http.patch<ResetAdminUserPasswordResponse>(
```

**Qué hace.** Centraliza GET, POST y PATCH del módulo de usuarios bajo API_URL.

**Lectura línea por línea / puntos clave.**

- list obtiene la tabla.
- createUser envía CreateAdminUserRequest.
- updateStatus cambia estado sin reemplazar todo el recurso.

**Patrón:** Service Pattern Angular.

**Conexión entre capas:** El interceptor añade JWT y AdminIdentityReadController procesa las rutas.

**Frase para exponer:** “La página administra interacción; el service conoce el contrato HTTP.”

**Pregunta probable:** ¿Por qué se usa PATCH para el estado?

#### Sayuri 6. Formulario y recarga de lista

- **Archivo:** `C:\E-specter - copia (2)\yachay\yachay-frontend\src\app\features\admin\pages\usuarios\usuarios.ts`
- **Método/elemento:** `createUser`
- **Líneas aproximadas:** 71–95

```typescript
  createUser(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.saving.set(true);
    this.errorMessage.set('');
    this.successMessage.set('');

    const raw = this.form.getRawValue();
    this.userService.createUser({
      nombres: raw.nombres,
      apellidos: raw.apellidos,
      email: raw.email,
      passwordTemporal: raw.passwordTemporal,
      rol: raw.rol as AdminUserRole,
      activo: raw.activo,
    }).subscribe({
      next: () => {
        this.saving.set(false);
        this.modalOpen.set(false);
        this.successMessage.set('Registro creado correctamente.');
        this.loadUsers();
      },
```

**Qué hace.** Valida el formulario, adapta nombres al DTO, consume el POST y recarga la tabla al completar.

**Lectura línea por línea / puntos clave.**

- form.invalid bloquea solicitudes incompletas.
- saving signal controla el botón.
- loadUsers vuelve a sincronizar la UI con el backend.

**Patrón:** Reactive Forms y Observer mediante Observable.

**Conexión entre capas:** Usuarios -> UserService -> HttpClient -> interceptor -> controller -> repositories -> MySQL.

**Frase para exponer:** “Después del 201 no se inventa una fila local: se vuelve a consultar el servidor.”

**Pregunta probable:** ¿Cómo se refleja el nuevo usuario en la tabla?

### Mini demo

Abrir Usuarios, mostrar el formulario sin crear datos innecesarios, explicar POST /api/admin/usuarios y refresco de la lista.

### Errores que debe evitar

- Decir que User contiene todos los datos académicos.
- Exponer contraseña temporal.
- Confundir roles con rutas.
- Afirmar que todos los PUT declarados en services tienen endpoint backend equivalente.

### Guion hablado (4–6 minutos)

Mi tema es el módulo administrador para roles, usuarios, alumnos y docentes. Primero explico el modelo. User representa acceso: correo, hash y roles. Role se relaciona mediante user_roles. Profile contiene nombres, fecha de nacimiento, avatar y estado. StudentProfile añade colegio, código, grado, sección y matrícula. TeacherProfile añade empleado, especialidad y contratación. Separar estas responsabilidades evita llenar una sola tabla con campos que no corresponden a todos.

En Angular abro admin-layout.ts y muestro el menú administrativo. La rama /admin en app.routes.ts exige authGuard, roleGuard y el rol ADMINISTRADOR. Ocultar menús mejora la experiencia, pero Spring Security también bloquea /admin/**. Después abro usuarios.ts. El formulario es reactivo y usa Validators. createUser marca controles, arma el DTO, activa el signal saving y llama a UserService.

UserService Angular centraliza las URLs. createUser hace POST /api/admin/usuarios. authInterceptor agrega el JWT. En backend AdminIdentityReadController.createUser recibe CreateAdminUserRequest, resuelve el Role, codifica la contraseña con BCrypt, guarda User y responde 201. Para alumno, el flujo crea User, Profile y StudentProfile dentro de la misma transacción. Para docente crea User, Profile y TeacherProfile. Si falla una operación, la transacción evita un registro incompleto.

Después del éxito Angular no inventa una fila: ejecuta loadUsers y vuelve a consultar GET /api/admin/usuarios. Así la tabla refleja la base de datos. El manejo de errores separa mensaje, loading y saving con signals. Para estado se usa PATCH porque se cambia una parte concreta del recurso.

En identity también existe una estructura cercana a puertos y adaptadores. UserService implementa UserServicePort, aplica regla de correo único y usa IdentityMapper para retornar UserDTO. StudentProfileService valida que Profile y School existan y que el código no se repita. Los repositories ocultan la persistencia JPA.

Como límite real, algunos services Angular declaran operaciones PUT para edición completa que no tienen un mapping administrativo equivalente en los controllers auditados. Por eso la creación, listado y cambio de estado son demostrables, mientras que no se debe prometer un CRUD completo para cada pantalla. La demostración recomendada es mostrar usuarios o alumnos, seguir el código desde el formulario hasta el repository y explicar la respuesta. Mi cierre es que el módulo separa acceso, identidad y rol, protege la ruta en dos capas y actualiza la interfaz desde respuestas reales del backend.

### Preguntas para Sayuri

**1. ¿Qué es Role?**

Respuesta breve: Una autoridad asignable a User.

Respuesta desarrollada: Los nombres normalizados ADMINISTRADOR, DOCENTE y ALUMNO controlan rutas y menús.

Evidencia/archivo: Role.java, AuthService.java y SecurityConfig.java

**2. ¿Cómo se guarda user_roles?**

Respuesta breve: Como relación many-to-many.

Respuesta desarrollada: User.roles usa una tabla de unión; un usuario puede tener varias authorities.

Evidencia/archivo: User.java

**3. ¿Qué diferencia hay entre StudentProfile y TeacherProfile?**

Respuesta breve: Datos académicos por tipo.

Respuesta desarrollada: StudentProfile guarda código/grado/sección/matrícula; TeacherProfile empleado/especialidad/contratación.

Evidencia/archivo: StudentProfile.java y TeacherProfile.java

**4. ¿Cómo se crea un usuario admin?**

Respuesta breve: Formulario -> service -> POST -> controller -> repositories.

Respuesta desarrollada: Angular valida, el interceptor añade JWT, el controller cifra la contraseña y responde 201; luego la tabla se recarga.

Evidencia/archivo: usuarios.ts, user.ts y AdminIdentityReadController.java

**5. ¿Dónde se aplica BCrypt?**

Respuesta breve: Antes de guardar User.

Respuesta desarrollada: AdminIdentityReadController usa PasswordEncoder y AuthService verifica con matches.

Evidencia/archivo: AdminIdentityReadController.java

**6. ¿Por qué usar DTO?**

Respuesta breve: Para controlar contrato y campos.

Respuesta desarrollada: CreateAdminUserRequest no expone la entidad completa; AdminUserResponse omite encrypted_password.

Evidencia/archivo: DTOs internos de AdminIdentityReadController

**7. ¿Cómo se maneja un correo repetido?**

Respuesta breve: Se detecta conflicto.

Respuesta desarrollada: El repository comprueba existencia y el flujo responde error en lugar de violar la restricción única.

Evidencia/archivo: UserRepository y controllers/services identity

**8. ¿Cómo se actualiza la tabla Angular?**

Respuesta breve: Con loadUsers tras el éxito.

Respuesta desarrollada: No depende de una copia optimista; vuelve a obtener GET /admin/usuarios.

Evidencia/archivo: usuarios.ts

**9. ¿Qué protege el menú?**

Respuesta breve: Layout y rutas; backend protege datos.

Respuesta desarrollada: navItems organiza UI, roleGuard limita navegación y SecurityConfig exige la authority.

Evidencia/archivo: admin-layout.ts, app.routes.ts y SecurityConfig.java

**10. ¿Qué hace PATCH de estado?**

Respuesta breve: Cambia una propiedad parcial.

Respuesta desarrollada: El endpoint no reemplaza toda la entidad; actualiza activo/inactivo y devuelve la representación.

Evidencia/archivo: AdminIdentityReadController.java y user.ts

**11. ¿Es CRUD completo todo el módulo?**

Respuesta breve: No en todos los recursos.

Respuesta desarrollada: Listar, crear y cambiar estado están implementados; algunos PUT declarados en frontend no tienen mapping administrativo equivalente.

Evidencia/archivo: services Angular y controllers backend

**12. ¿Qué parte de identity es hexagonal?**

Respuesta breve: Puertos, services, dominio y adapters.

Respuesta desarrollada: UserServicePort define entrada, UserService implementa caso de uso, modelos/repositories están en domain y controllers en infrastructure.

Evidencia/archivo: paquete identity


## Cristhina — Calendario, notificaciones persistidas, SMTP y WhatsApp

**Objetivo.** Demostrar filtrado por identidad/rol, persistencia de avisos y estado real de los canales externos.

### Orden de exposición

1. Crear o listar eventos.
2. Mostrar filtros de docente/alumno.
3. Explicar usuario obtenido del JWT.
4. Mostrar creación persistente de notificaciones.
5. Explicar isRead/readAt.
6. Mostrar campana y bandeja.
7. Cerrar con SMTP configurable y WhatsApp controlado.

### Carpetas a abrir

- `C:\E-specter - copia (2)\yachay\yachay-backend\src\main\java\edu\yachay\backend\academic`
- `C:\E-specter - copia (2)\yachay\yachay-backend\src\main\java\edu\yachay\backend\notification`
- `C:\E-specter - copia (2)\yachay\yachay-frontend\src\app\core\services`
- `C:\E-specter - copia (2)\yachay\yachay-frontend\src\app\features`

### Archivos a preparar en pestañas

`CalendarController.java`, `CalendarEvent.java`, `CalendarEventRepository.java`, `Notification.java`, `NotificationRepository.java`, `PersistentNotificationService.java`, `NotificationController.java`, `UserNotificationController.java`, `calendar.ts`, `notification.ts`, `admin-layout.ts`, `teacher-layout.ts`, `student-layout.ts`

### Fragmentos reales

#### Cristhina 1. Creación de evento académico

- **Archivo:** `C:\E-specter - copia (2)\yachay\yachay-backend\src\main\java\edu\yachay\backend\academic\CalendarController.java`
- **Método/elemento:** `createEvent`
- **Líneas aproximadas:** 69–93

```java
    @PostMapping("/admin/calendario")
    @Transactional
    public ResponseEntity<CalendarEventResponse> createEvent(@Valid @RequestBody CreateCalendarEventRequest request) {
        if (!request.fechaFin().isAfter(request.fechaInicio())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fecha fin debe ser posterior a la fecha inicio.");
        }

        Course course = request.cursoId() != null
                ? courseRepository.findById(request.cursoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Curso no encontrado."))
                : null;

        CalendarEvent event = CalendarEvent.builder()
                .course(course)
                .title(request.titulo())
                .description(request.descripcion())
                .eventType(request.tipo())
                .audience(request.publicoObjetivo() != null ? request.publicoObjetivo() : "TODOS")
                .eventDate(request.fechaInicio().toLocalDate())
                .dayOfWeek(spanishDay(request.fechaInicio().getDayOfWeek()))
                .startTime(request.fechaInicio().toLocalTime())
                .endTime(request.fechaFin().toLocalTime())
                .build();

        CalendarEvent saved = calendarEventRepository.save(event);
```

**Qué hace.** Valida el rango horario, resuelve el curso opcional y construye CalendarEvent.

**Lectura línea por línea / puntos clave.**

- @Valid aplica Bean Validation al DTO.
- Una fecha fin inválida produce 400.
- El audience por defecto es TODOS.

**Patrón:** Controller Pattern y DTO Pattern.

**Conexión entre capas:** CalendarService Angular envía el POST; repository persiste y luego se crean notificaciones.

**Frase para exponer:** “La validación temporal se ejecuta antes de guardar.”

**Pregunta probable:** ¿Por qué el curso puede ser null?

#### Cristhina 2. Filtrado de calendario por matrícula

- **Archivo:** `C:\E-specter - copia (2)\yachay\yachay-backend\src\main\java\edu\yachay\backend\academic\CalendarController.java`
- **Método/elemento:** `filterStudentEvents`
- **Líneas aproximadas:** 148–164

```java
    private List<CalendarEvent> filterStudentEvents(User user) {
        StudentProfile student = studentForUser(user);
        Set<Integer> enrolledCourseIds = new HashSet<>();
        enrollmentRepository.findByStudent_Id(student.getId()).stream()
                .map(Enrollment::getCourse)
                .filter(course -> course != null && course.getId() != null)
                .map(Course::getId)
                .forEach(enrolledCourseIds::add);

        return calendarEventRepository.findAll().stream()
                .filter(event -> isGeneralFor("ALUMNO", event)
                        || (event.getStudent() != null && event.getStudent().getId().equals(student.getId()))
                        || (event.getCourse() != null && enrolledCourseIds.contains(event.getCourse().getId())))
                .toList();
    }

    private boolean isGeneralFor(String role, CalendarEvent event) {
```

**Qué hace.** Obtiene cursos matriculados y combina eventos generales, personales y por curso.

**Lectura línea por línea / puntos clave.**

- EnrollmentRepository entrega matrículas del alumno.
- Set<Integer> permite pertenencia eficiente.
- El filtro conserva eventos visibles para ese usuario.

**Patrón:** Authorization by Data Scope.

**Conexión entre capas:** El principal proviene del JWT; Profile y StudentProfile identifican al alumno.

**Frase para exponer:** “No todos los alumnos reciben el mismo calendario: se filtra por audiencia y matrícula.”

**Pregunta probable:** ¿Cómo se evita mostrar un evento de otro curso?

#### Cristhina 3. Estado leído persistente

- **Archivo:** `C:\E-specter - copia (2)\yachay\yachay-backend\src\main\java\edu\yachay\backend\notification\domain\models\Notification.java`
- **Método/elemento:** `markAsRead`
- **Líneas aproximadas:** 45–58

```java
    private LocalDateTime readAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (read == null) {
            read = false;
        }
    }

    public void markAsRead() {
        read = true;
        readAt = LocalDateTime.now();
    }
```

**Qué hace.** La entidad guarda is_read y read_at; el método de dominio cambia ambos de forma coherente.

**Lectura línea por línea / puntos clave.**

- @PrePersist inicializa createdAt y read.
- markAsRead cambia el booleano.
- readAt registra cuándo ocurrió la acción.

**Patrón:** Entity Behavior.

**Conexión entre capas:** PersistentNotificationService invoca el método y NotificationRepository actualiza MySQL.

**Frase para exponer:** “La campana refleja un estado persistido, no solo una variable del navegador.”

**Pregunta probable:** ¿Qué diferencia hay entre isRead y readAt?

#### Cristhina 4. Creación idempotente de notificación

- **Archivo:** `C:\E-specter - copia (2)\yachay\yachay-backend\src\main\java\edu\yachay\backend\notification\PersistentNotificationService.java`
- **Método/elemento:** `createForUser`
- **Líneas aproximadas:** 65–83

```java
    public void createForUser(User recipient, String title, String body, String type, String linkUrl) {
        if (recipient == null || recipient.getId() == null) {
            return;
        }

        if (notificationRepository.existsByRecipient_IdAndTitleAndBody(recipient.getId(), title, body)) {
            return;
        }

        notificationRepository.save(Notification.builder()
                .recipient(recipient)
                .title(title)
                .body(body)
                .type(type != null ? type : "INFO")
                .linkUrl(linkUrl)
                .read(false)
                .build());
    }

```

**Qué hace.** Evita duplicados por destinatario, título y cuerpo antes de persistir.

**Lectura línea por línea / puntos clave.**

- Valida recipient e id.
- existsBy... corta una repetición del seeder o evento.
- Builder fija type, linkUrl y read=false.

**Patrón:** Service Layer e Idempotency Guard.

**Conexión entre capas:** CalendarController y DataSeeder reutilizan el mismo servicio persistente.

**Frase para exponer:** “La deduplicación permite ejecutar el seeder varias veces sin multiplicar avisos iguales.”

**Pregunta probable:** ¿Qué criterio usa para considerar duplicada una notificación?

#### Cristhina 5. Endpoints por usuario autenticado

- **Archivo:** `C:\E-specter - copia (2)\yachay\yachay-backend\src\main\java\edu\yachay\backend\notification\UserNotificationController.java`
- **Método/elemento:** `list / markAsRead / markAllAsRead`
- **Líneas aproximadas:** 25–45

```java
    @GetMapping({"/docente/notificaciones", "/alumno/notificaciones"})
    @Transactional(readOnly = true)
    public ResponseEntity<List<UserNotificationResponse>> list(Authentication authentication) {
        return ResponseEntity.ok(persistentNotificationService.listForUser(currentUser(authentication).getId()));
    }

    @PatchMapping({"/docente/notificaciones/{id}/leido", "/alumno/notificaciones/{id}/leido"})
    public ResponseEntity<UserNotificationResponse> markAsRead(
            @PathVariable Long id,
            Authentication authentication
    ) {
        return ResponseEntity.ok(persistentNotificationService.markAsRead(currentUser(authentication).getId(), id));
    }

    @PatchMapping({"/docente/notificaciones/leidas", "/alumno/notificaciones/leidas"})
    public ResponseEntity<Void> markAllAsRead(Authentication authentication) {
        persistentNotificationService.markAllAsRead(currentUser(authentication).getId());
        return ResponseEntity.noContent().build();
    }

    private User currentUser(Authentication authentication) {
```

**Qué hace.** Comparte endpoints para docente y alumno y siempre toma el usuario desde Authentication.

**Lectura línea por línea / puntos clave.**

- GET lista solo notificaciones del principal.
- PATCH por id verifica propiedad en el service.
- PATCH plural marca todas las del usuario.

**Patrón:** Controller Pattern y Current-user Context.

**Conexión entre capas:** JwtAuthFilter construye Authentication; el controller extrae User y delega.

**Frase para exponer:** “El userId no llega en el body: se obtiene del token validado.”

**Pregunta probable:** ¿Por qué no se recibe userId desde Angular?

#### Cristhina 6. Consumo de calendario desde Angular

- **Archivo:** `C:\E-specter - copia (2)\yachay\yachay-frontend\src\app\core\services\calendar.ts`
- **Método/elemento:** `list / week / month`
- **Líneas aproximadas:** 18–34

```typescript
  list(role: CalendarRole): Observable<AcademicCalendarEvent[]> {
    return this.http.get<AcademicCalendarEvent[]>(
      `${API_URL}/${this.rolePath(role)}/calendario`,
    );
  }

  week(role: CalendarRole): Observable<AcademicCalendarEvent[]> {
    return this.http.get<AcademicCalendarEvent[]>(
      `${API_URL}/${this.rolePath(role)}/calendario/semana`,
    );
  }

  month(role: CalendarRole): Observable<AcademicCalendarEvent[]> {
    return this.http.get<AcademicCalendarEvent[]>(
      `${API_URL}/${this.rolePath(role)}/calendario/mes`,
    );
  }
```

**Qué hace.** Construye la ruta según rol y ofrece consultas general, semanal y mensual.

**Lectura línea por línea / puntos clave.**

- rolePath traduce ADMINISTRADOR, DOCENTE y ALUMNO.
- HttpClient tipa la respuesta.
- Cada método conserva un contrato Observable.

**Patrón:** Service Pattern Angular.

**Conexión entre capas:** Las páginas por rol llaman al mismo service; el backend aplica filtros diferentes.

**Frase para exponer:** “Un único service evita duplicar URLs en tres páginas.”

**Pregunta probable:** ¿Dónde se decide qué calendario recibe cada rol?

#### Cristhina 7. Consumo de notificaciones desde Angular

- **Archivo:** `C:\E-specter - copia (2)\yachay\yachay-frontend\src\app\core\services\notification.ts`
- **Método/elemento:** `list / markAsRead / markAllAsRead`
- **Líneas aproximadas:** 41–59

```typescript
  list(role: UserRole): Observable<UserNotification[]> {
    return this.http.get<UserNotification[]>(
      `${API_URL}/${this.rolePath(role)}/notificaciones`,
    );
  }

  markAsRead(role: UserRole, id: number): Observable<UserNotification> {
    return this.http.patch<UserNotification>(
      `${API_URL}/${this.rolePath(role)}/notificaciones/${id}/leido`,
      {},
    );
  }

  markAllAsRead(role: UserRole): Observable<UserNotification[]> {
    return this.http.patch<UserNotification[]>(
      `${API_URL}/${this.rolePath(role)}/notificaciones/leidas`,
      {},
    );
  }
```

**Qué hace.** Expone listado y operaciones PATCH para la campana y la bandeja.

**Lectura línea por línea / puntos clave.**

- list obtiene el arreglo persistido.
- markAsRead actualiza una notificación.
- markAllAsRead sincroniza el contador global.

**Patrón:** Service Pattern Angular.

**Conexión entre capas:** Layouts y páginas comparten este service; el interceptor aporta Bearer.

**Frase para exponer:** “La UI no modifica el contador a ciegas: espera la respuesta del backend.”

**Pregunta probable:** ¿Cómo se sincroniza la campana al marcar todas como leídas?

### Mini demo

Listar calendario y notificaciones, marcar una como leída y explicar por qué WhatsApp no se presenta como envío operativo.

### Errores que debe evitar

- Confundir comunicado con notificación interna.
- Afirmar envío real de WhatsApp.
- Enviar un correo externo durante la exposición sin preparación.
- Decir que userId viene del frontend.

### Guion hablado (4–6 minutos)

Yo explicaré calendario y notificaciones. Comienzo en la página de calendario administrativo. CalendarService Angular usa HttpClient y construye rutas para ADMINISTRADOR, DOCENTE y ALUMNO. Para crear, envía POST /api/admin/calendario. El interceptor agrega Bearer y SecurityConfig exige ADMINISTRADOR.

En CalendarController.createEvent el DTO se valida. Si fechaFin no es posterior a fechaInicio responde 400. El curso es opcional porque existen eventos generales, por ejemplo feriados o comunicados. Se construye CalendarEvent, CalendarEventRepository guarda en yachay_calendar_events y notifyCalendarEvent crea notificaciones internas según audience y curso.

Para docente y alumno no se devuelve todo. El usuario se obtiene desde Authentication, que fue creado por JwtAuthFilter. El docente ve eventos generales para DOCENTE y eventos de cursos que enseña. El alumno obtiene StudentProfile, consulta EnrollmentRepository y forma un conjunto de cursos matriculados. Después conserva eventos generales, personales o de esos cursos. Así el alcance de datos se controla en backend.

Notification es una entidad real en yachay_notifications. Guarda recipient_user_id, title, body, type, link_url, is_read, created_at y read_at. markAsRead actualiza booleano y fecha. PersistentNotificationService lista por usuario, cuenta no leídas y comprueba que una notificación pertenezca al usuario antes de modificar. createForUser evita duplicados por destinatario, título y cuerpo, importante para el DataSeeder.

UserNotificationController atiende rutas de docente y alumno; NotificationController atiende admin. No recibe userId del navegador: toma currentUser desde el principal autenticado. Angular NotificationService expone list, markAsRead y markAllAsRead. Los layouts calculan notificationCount y muestran la campana; las páginas ofrecen la bandeja.

Diferencio cuatro conceptos. Un comunicado es contenido académico publicado; una notificación interna es un aviso persistido por usuario; SMTP es el canal de correo mediante JavaMailSender; WhatsApp es una preparación controlada. EmailService solo envía si existen usuario y contraseña de correo; durante el cierre no se validó envío externo, por eso se clasifica configurado pero no validado. WhatsappService, aun activado, no ejecuta una llamada HTTP: devuelve que la Cloud API está preparada. Por tanto no se presenta como operativa.

La evidencia validada es que calendario y notificaciones respondieron para roles, y que 401/403 protegen los prefijos. Mi cierre es que los avisos internos sí son persistentes y demostrables; correo depende de infraestructura externa y WhatsApp permanece como integración futura controlada.

### Preguntas para Cristhina

**1. ¿Cómo se crea un evento?**

Respuesta breve: POST admin con DTO validado.

Respuesta desarrollada: CalendarController valida fechas, resuelve curso, guarda CalendarEvent y genera notificaciones.

Evidencia/archivo: CalendarController.createEvent

**2. ¿Cómo sabe el backend quién consulta?**

Respuesta breve: Por Authentication.

Respuesta desarrollada: JwtAuthFilter establece como principal el User validado; el controller no confía en un userId enviado.

Evidencia/archivo: CalendarController.currentUser

**3. ¿Cómo filtra al docente?**

Respuesta breve: Por audience y cursos asignados.

Respuesta desarrollada: Compara el teacher profile del curso con el perfil autenticado.

Evidencia/archivo: CalendarController.filterTeacherEvents

**4. ¿Cómo filtra al alumno?**

Respuesta breve: Por audience, destinatario y matrícula.

Respuesta desarrollada: EnrollmentRepository obtiene course IDs y el filtro conserva eventos relacionados.

Evidencia/archivo: CalendarController.filterStudentEvents

**5. ¿Qué es una notificación persistida?**

Respuesta breve: Una fila por usuario en MySQL.

Respuesta desarrollada: Tiene destinatario, contenido, tipo, enlace, leído y fechas; sobrevive recargas y sesiones.

Evidencia/archivo: Notification.java

**6. ¿Cómo se marca una?**

Respuesta breve: PATCH y verificación de propietario.

Respuesta desarrollada: El service busca el ID, compara recipient.id con userId autenticado y ejecuta markAsRead.

Evidencia/archivo: PersistentNotificationService.markAsRead

**7. ¿Cómo se marcan todas?**

Respuesta breve: Carga las del usuario y actualiza no leídas.

Respuesta desarrollada: El service filtra, marca y ejecuta saveAll; Angular actualiza lista/contador.

Evidencia/archivo: PersistentNotificationService.markAllAsRead

**8. ¿Cómo evita duplicados?**

Respuesta breve: exists por destinatario, título y cuerpo.

Respuesta desarrollada: createForUser retorna antes de save si ya existe la misma combinación.

Evidencia/archivo: NotificationRepository y PersistentNotificationService

**9. ¿Comunicado y notificación son iguales?**

Respuesta breve: No.

Respuesta desarrollada: Announcement es contenido publicado; Notification es un aviso individual con isRead/readAt y enlace.

Evidencia/archivo: Announcement.java y Notification.java

**10. ¿SMTP está operativo?**

Respuesta breve: Implementado/configurable, no validado externamente.

Respuesta desarrollada: EmailService usa JavaMailSender solo si hay configuración; no se realizó envío real en este cierre.

Evidencia/archivo: EmailService.java

**11. ¿WhatsApp envía mensajes?**

Respuesta breve: No en el estado actual.

Respuesta desarrollada: WhatsappService valida banderas/credenciales y devuelve estado, pero no contiene cliente HTTP hacia Cloud API.

Evidencia/archivo: WhatsappService.java

**12. ¿Qué muestra la campana?**

Respuesta breve: Notificaciones persistidas y conteo no leído.

Respuesta desarrollada: Los layouts llaman NotificationService, conservan signals y calculan notificationCount.

Evidencia/archivo: admin-layout.ts, teacher-layout.ts y student-layout.ts


## 8. Ruta de demostración (máximo 10 minutos)

| Tiempo | Responsable | Pantalla | Acción | Archivo | Riesgo | Plan alternativo |
| --- | --- | --- | --- | --- | --- | --- |
| 0:00–0:40 | Eduard | Terminal | Iniciar/verificar MySQL y backend; indicar Java 17. | application.yaml / BackendApplication.java | Puerto ocupado o JAVA_HOME | Mostrar log de arranque validado. |
| 0:40–1:10 | Eduard | Terminal | Iniciar frontend con npm start. | app.config.ts | Puerto 4200 | Usar build ya generado y capturas. |
| 1:10–2:00 | Jesús | Login | Login ADMINISTRADOR y explicar Reactive Form/JWT. | login.ts / AuthService.java | Credencial mal escrita | Usar sesión previamente abierta. |
| 2:00–2:40 | Sayuri | Layout admin | Mostrar menú y protección por rol. | admin-layout.ts / app.routes.ts | Carga lenta | Abrir archivos en VS Code. |
| 2:40–3:40 | Sayuri | Usuarios o alumnos | Listar y seguir GET; opcional explicar formulario sin crear basura. | usuarios.ts / AdminIdentityReadController.java | Error de red | Mostrar JSON guardado/captura. |
| 3:40–4:20 | Chelsea | MySQL | Mostrar tablas actuales sin credenciales. | User.java / Course.java | Cliente SQL no abre | Mostrar entidades y log MySQL 8.0.42. |
| 4:20–5:05 | Eduard | Postulaciones | Mostrar listado y recordar POST público 201 validado. | PublicAdmissionController.java | No crear registro nuevo | Usar listado existente. |
| 5:05–5:50 | Eduard | Reportes | Descargar XLSX de cursos o notas. | ExcelReportService.java / report.ts | Descarga bloqueada | Abrir XLSX de respaldo preparado antes. |
| 5:50–6:35 | Eduard | Documentos | Descargar PDF de postulación. | DocumentService.java / document.ts | ID inexistente | Abrir PDF de respaldo. |
| 6:35–7:35 | Cristhina | Calendario | Mostrar eventos y filtrado por rol. | CalendarController.java / calendar.ts | Muchos registros | Filtrar o mostrar semana. |
| 7:35–8:25 | Cristhina | Notificaciones | Mostrar campana, bandeja y marcar leída. | PersistentNotificationService.java | No hay no leídas | Explicar con una fila existente. |
| 8:25–9:10 | Jesús | Seguridad | Logout, login DOCENTE/ALUMNO y explicar 403 al intentar admin. | SecurityConfig.java / roleGuard | No forzar URL en vivo | Mostrar evidencia de 401/403. |
| 9:10–10:00 | Todos | Cierre | Limitaciones y conclusión sin exagerar integraciones. | Matriz de rúbrica | Tiempo | Eduard resume estados en 30 segundos. |

## 9. Preguntas generales

### 1. ¿Qué arquitectura usa Yachay?

**Breve:** N capas organizada por dominios.

**Desarrollada:** Angular presenta; guards e interceptor preparan la petición; Spring Security valida; controllers delegan; services aplican reglas; repositories persisten en MySQL. Identity se aproxima a puertos y adaptadores, pero el sistema completo no es hexagonal puro.

**Evidencia:** app.routes.ts, SecurityConfig.java y paquete identity

### 2. ¿Qué diferencia hay entre DTO y entidad?

**Breve:** El DTO es contrato; la entidad representa persistencia.

**Desarrollada:** Un DTO controla campos de entrada/salida y validación. Una entidad contiene mapeo ORM y relaciones. Separarlos evita exponer hashes, relaciones lazy o detalles internos.

**Evidencia:** LoginRequest.java, LoginResponse.java y User.java

### 3. ¿Qué diferencia hay entre JPA e Hibernate?

**Breve:** JPA especifica; Hibernate implementa.

**Desarrollada:** Jakarta Persistence define anotaciones y contratos. Hibernate interpreta esas anotaciones, mantiene el contexto de persistencia y genera SQL para MySQL.

**Evidencia:** pom.xml y entidades domain/models

### 4. ¿Qué diferencia hay entre guard y filtro JWT?

**Breve:** El guard navega; el filtro protege la API.

**Desarrollada:** El guard se ejecuta en Angular y puede ser manipulado. JwtAuthFilter se ejecuta en el servidor, valida firma/expiración y establece el SecurityContext.

**Evidencia:** core/guards y JwtAuthFilter.java

### 5. ¿Autenticación y autorización son lo mismo?

**Breve:** No.

**Desarrollada:** Autenticación confirma quién es el usuario mediante credenciales. Autorización decide qué puede hacer usando roles y reglas de rutas.

**Evidencia:** AuthService.java y SecurityConfig.java

### 6. ¿Cómo reducen SQL Injection?

**Breve:** Con repositories y parámetros.

**Desarrollada:** Los métodos derivados de Spring Data generan consultas parametrizadas. No se concatenan correos o IDs en SQL. Consultas nativas futuras deben mantener parámetros.

**Evidencia:** UserRepository.java

### 7. ¿Por qué BCrypt?

**Breve:** Porque genera hashes lentos con salt.

**Desarrollada:** BCrypt dificulta ataques por fuerza bruta y evita almacenar contraseñas reversibles. matches verifica sin descifrar.

**Evidencia:** AuthService.java y SecurityConfig.passwordEncoder

### 8. ¿Qué es CORS?

**Breve:** Política de orígenes permitidos.

**Desarrollada:** SecurityConfig permite los orígenes locales 4200, métodos y headers requeridos. CORS es una política del navegador; no reemplaza autenticación.

**Evidencia:** SecurityConfig.corsConfigurationSource

### 9. ¿Qué es un Blob?

**Breve:** Una respuesta binaria manejada por Angular.

**Desarrollada:** ReportService y DocumentService Angular usan responseType blob, crean un enlace temporal y descargan XLSX o PDF sin convertirlos a JSON.

**Evidencia:** core/services/report.ts y document.ts

### 10. ¿Apache POI es una API externa?

**Breve:** No, es una librería Java.

**Desarrollada:** Se ejecuta dentro del backend y crea XSSFWorkbook en memoria. No necesita credenciales ni red externa.

**Evidencia:** ExcelReportService.java y pom.xml

### 11. ¿OpenPDF necesita Internet?

**Breve:** No para el flujo actual.

**Desarrollada:** DocumentService instancia Document, PdfWriter y PdfPTable localmente y entrega byte[].

**Evidencia:** DocumentService.java

### 12. ¿Qué estado tiene SMTP?

**Breve:** Implementado y configurable, no validado externamente.

**Desarrollada:** EmailService usa JavaMailSender si las credenciales están presentes. Este cierre no envió correo externo para no afirmar operación sin evidencia.

**Evidencia:** EmailService.java y application.yaml

### 13. ¿Qué estado tienen WhatsApp e I Love PDF?

**Breve:** Preparación futura/controlada.

**Desarrollada:** WhatsappService no contiene cliente HTTP saliente. IlovePdfClient solo comprueba claves y devuelve mensajes; el PDF real usa OpenPDF.

**Evidencia:** WhatsappService.java e IlovePdfClient.java

### 14. ¿Qué significan 401, 403, 404 y 500?

**Breve:** No autenticado, prohibido, no encontrado y error interno.

**Desarrollada:** SecurityConfig produce 401/403 de seguridad; controllers/services usan 404 cuando no existe el recurso; GlobalExceptionHandler normaliza errores, y 500 representa una falla no controlada.

**Evidencia:** SecurityConfig.java y GlobalExceptionHandler.java

### 15. ¿Qué ocurre si MySQL está caído?

**Breve:** El backend no completa la conexión ni los flujos JPA.

**Desarrollada:** Hikari y Hibernate fallan al crear el EntityManagerFactory o las operaciones retornan error. La demo debe iniciar/verificar MySQL antes de Spring Boot.

**Evidencia:** application.yaml y salida de arranque validada

## 10. Limitaciones comprobadas

- Varias páginas docentes/alumno usan datos locales; sus services de portal apuntan a endpoints no implementados y devolvieron 404.
- Forgot/reset password responde 204, sin ciclo completo de recuperación.
- Algunos controllers administrativos concentran reglas y repositories.
- Cobertura automatizada mínima: un context test, no una suite amplia.
- `ddl-auto=update` es apropiado solo para desarrollo; no hay Flyway/Liquibase.
- `yachay-db` no incluye scripts SQL actuales y `PlantUML` no existe en la documentación revisada.
- SMTP depende de infraestructura externa y no fue validado.
- WhatsApp no realiza envío HTTP real; I Love PDF no participa.
- No existe evidencia de despliegue productivo.

## 11. Conclusiones

Yachay compila, inicia y demuestra flujos reales de seguridad, administración, admisión, calendario, notificaciones y documentos. La exposición debe apoyarse en esos flujos validados y reconocer los módulos aún parciales. La definición por N capas con dominios funcionales es precisa; identity aporta una aproximación a puertos y adaptadores sin convertir todo el sistema en hexagonal puro.

## 12. Bibliografía técnica oficial

- Angular: https://angular.dev/overview
- TypeScript: https://www.typescriptlang.org/docs/
- Tailwind CSS: https://tailwindcss.com/docs/installation/using-postcss
- Java SE 17: https://docs.oracle.com/en/java/javase/17/
- Spring Boot: https://docs.spring.io/spring-boot/index.html
- Spring Security: https://docs.spring.io/spring-security/reference/index.html
- Spring Data JPA: https://docs.spring.io/spring-data/jpa/reference/
- Jakarta Persistence: https://jakarta.ee/specifications/persistence/
- Hibernate ORM: https://docs.hibernate.org/orm/current/introduction/html_single/
- MySQL 8.0: https://dev.mysql.com/doc/refman/8.0/en/
- JJWT: https://github.com/jwtk/jjwt
- JWT RFC 7519: https://datatracker.ietf.org/doc/html/rfc7519
- Apache POI Spreadsheet: https://poi.apache.org/components/spreadsheet/
- OpenPDF: https://github.com/LibrePDF/OpenPDF
- PlantUML: https://plantuml.com/

## 13. Matriz de cumplimiento de la rúbrica

| N.º | Criterio | Evidencia | Archivo | Demostración | Estado | Observación |
| --- | --- | --- | --- | --- | --- | --- |
| 1 | Documentación y evidencias | Siete entregables, descripción, arquitectura, evidencia, límites, conclusiones y bibliografía. | yachay-doc/Entregables | Abrir Word, índice, validaciones y limitaciones. | CUMPLIDO | El Word fue generado y sometido a render visual. |
| 2 | Funcionalidad completa | Login/roles/admin/admisión/calendario/notificaciones/XLSX/PDF validados. | frontend features y controllers backend | Recorrer la demo de 10 minutos. | PARCIAL | Páginas de docente/alumno aún contienen datos locales y faltan endpoints de portal. |
| 3 | Seguridad con roles y menús | Spring Security, BCrypt, JWT, filtro, guards, interceptor y menús por layout. | SecurityConfig.java, JwtAuthFilter.java, guards, layouts | Mostrar 401 y 403 ya validados. | CUMPLIDO | No mostrar secretos. |
| 4 | Creación y consumo de API | GET usuarios, POST admisiones/usuarios/calendario y descargas Blob. | services Angular + controllers/services/repositories | Seguir GET, POST y Blob de extremo a extremo. | CUMPLIDO | Algunos métodos PUT del frontend carecen de endpoint equivalente, sin afectar los flujos seleccionados. |
| 5 | Uso de APIs o integraciones | MySQL validado; SMTP configurable; WhatsApp/I Love PDF limitados; POI/OpenPDF locales. | application.yaml, notification, report, document | Descargar XLSX/PDF y explicar estados. | PARCIAL | No presentar SMTP, WhatsApp ni I Love PDF como operación completa. |
| 6 | Trabajo grupal | Responsabilidades, archivos, guiones y preguntas para cinco integrantes. | Resumen_tecnico_por_integrante.md | Cada integrante abre sus rutas y ejecuta mini demo. | PARCIAL | La guía acredita reparto; no se evaluó historial individual de commits. |
| 7 | Spring Security individual | Login, roles, JWT, filtro, 401/403 y rutas protegidas. | capítulo de Jesús | Login y acceso denegado por rol. | CUMPLIDO | Forgot/reset permanece parcial y se declara. |
| 8 | Spring con API | Endpoints Spring Boot consumidos por HttpClient con JSON y Blob. | controllers, services Angular y api.config.ts | GET usuarios, POST admisión y descarga XLSX/PDF. | CUMPLIDO | Los flujos demostrados tienen evidencia real. |

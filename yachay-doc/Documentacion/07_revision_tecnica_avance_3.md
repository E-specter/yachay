# 07. Revision tecnica Avance 3

## Estado actual

El proyecto funciona con:

- Backend Spring Boot + MySQL.
- Login real contra usuarios en MySQL.
- DataSeeder idempotente.
- Roles reales.
- Panel administrativo.
- Postulaciones reales.
- Reportes XLSX reales.
- CRUD administrativos principales.
- PDF local real con OpenPDF para alumnos y postulaciones.
- JWT real firmado y validado por backend.
- Calendario academico real por rol.
- Notificaciones persistidas por usuario.
- Frontend Angular 21 con TailwindCSS v4.

## Revision backend

Correcto:

- Separacion por paquetes principales.
- Repositorios JPA por dominio.
- DTOs en auth, identity y admissions.
- Services en auth, identity, notification, document y report.
- DataSeeder con passwords BCrypt.
- MySQL como unica base de datos oficial.
- PDF local sin depender de I Love PDF.
- Seguridad backend con JJWT, filtro JWT y autorizacion por rol.
- Endpoints de calendario protegidos para admin, docente y alumno.
- Notificaciones persistidas en MySQL y filtradas por usuario autenticado.

Hallazgos:

- `AdminAcademicController` y `AdminIdentityReadController` concentran logica que deberia migrarse a services.
- Algunos paquetes solo contienen `readme.md` y representan modulos futuros.
- Existe `notification` con codigo real y `notifications` documental. Conviene consolidar.

## Revision frontend

Correcto:

- Arquitectura `core/shared/features`.
- Guards funcionales `authGuard` y `roleGuard`.
- Interceptor para `Authorization`.
- Layouts por rol.
- Rutas lazy.
- Services HTTP por dominio.
- Descargas XLSX/PDF por `Blob`.
- Admin consume API real en modulos principales.
- `API_URL` centralizado en `core/config/api.config.ts`.
- Interceptor JWT agrega `Authorization` y redirige a login ante 401.
- Campana de notificaciones conectada a endpoints reales por rol.
- Vistas de calendario por rol conectadas a backend.

Hallazgos:

- Algunas paginas admin contienen modales y formularios extensos en el componente.
- Las vistas docente/alumno aun pueden requerir endpoints reales completos por rol.

## Carpetas sin uso o documentales

No se encontraron carpetas vacias.

Paquetes backend con solo documentacion:

- `attendance`
- `calendar`
- `common`
- `communication`
- `contents`
- `notifications`
- `resources`
- `shared`

Decision: se conservan para no romper trazabilidad de arquitectura, pero se documentan como paquetes futuros o documentales. En una fase posterior se deben implementar o retirar del arbol `src/main/java` y mover su contenido a `docs`.

## Archivos eliminados

No se eliminaron archivos en esta revision porque no habia carpetas vacias y los paquetes documentales pueden tener valor para la presentacion de arquitectura.

## Archivos reutilizados

- Los README existentes se complementan con la carpeta `docs`.
- `application-local.example.yaml` queda como plantilla segura.
- `application-local.yaml` queda como archivo local ignorado por Git.

## Seguridad y configuracion

Corregido o verificado:

- MySQL es la base oficial.
- `application-local.yaml` esta ignorado.
- `.env` esta ignorado.
- Se agrego `.gitignore` raiz para reforzar proteccion de archivos locales.
- OpenPDF genera PDF sin requerir llaves externas.
- JWT firmado protege rutas backend por rol.

Pendiente:

- Reemplazar secretos locales reales si existieran en archivos no ignorados.

## Tablas o entidades posiblemente obsoletas

No se borraron tablas. Si la base tiene tablas antiguas no asociadas a entidades actuales, deben revisarse antes de eliminar:

- tablas sin prefijo `yachay_`
- tablas de pruebas antiguas
- tablas duplicadas de alumnos/docentes/cursos

## Documentacion creada

- `yachay-doc/Documentacion/01_arquitectura_general.md`
- `yachay-doc/Documentacion/02_backend_arquitectura.md`
- `yachay-doc/Documentacion/03_frontend_arquitectura.md`
- `yachay-doc/Documentacion/04_base_de_datos.md`
- `yachay-doc/Documentacion/05_endpoints_api.md`
- `yachay-doc/Documentacion/06_instalacion_local.md`
- `yachay-doc/Documentacion/07_revision_tecnica_avance_3.md`
- `yachay-doc/Documentacion/08_seguridad_jwt.md`
- `yachay-doc/Documentacion/09_reportes_y_pdf.md`
- `yachay-doc/Documentacion/10_siguiente_fase_calendario_notificaciones.md`

## Validacion esperada

Comandos:

```powershell
cd C:\E-specter\yachay\yachay-backend
.\mvnw.cmd clean package -DskipTests "-Djava.version=17"

cd C:\E-specter\yachay\yachay-frontend
npm run build
```

## Siguiente fase sugerida

1. Extraer logica admin a services.
2. Completar endpoints reales para dashboard docente/alumno.
3. Ordenar modulos futuros y documentales.
4. Agregar pruebas automatizadas para seguridad y reportes.

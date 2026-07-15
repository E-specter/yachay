# Checklist final del proyecto Yachay

Fecha: 14 de julio de 2026  
Ruta validada: `C:\E-specter - copia (2)\yachay`

Leyenda: `[x]` validado, `[~]` requiere configuración externa, `[ ]` pendiente de despliegue.

## Funcionalidad

- [x] 183 acciones visibles inventariadas: 155 botones y 28 enlaces.
- [x] 0 botones sin `(click)` o submit asociado; 0 submit sin `(ngSubmit)`.
- [x] Sin mocks funcionales, `DEV_USERS`, `of(...)`, demoras simuladas ni alertas decorativas.
- [x] Login, JWT, sesión persistente/no persistente, logout, 401 y 403.
- [x] Recuperación de contraseña con token hash, vencimiento y un solo uso.
- [x] Configuración institucional real; secretos no visibles/editables.
- [x] CRUD y estados de usuarios, alumnos, docentes, cursos, secciones, tareas, notas y comunicados.
- [x] Aceptar postulación crea/vincula alumno transaccionalmente.
- [x] Rechazar solicita y persiste motivo; asignar permite elegir sección.
- [x] Portales docente y alumno consumen endpoints reales derivados del JWT.
- [x] Calendario admin/docente/alumno con filtros de propiedad.
- [x] Notificaciones persistidas, lectura individual/masiva y contador sincronizado.
- [x] PDF local OpenPDF y XLSX Apache POI con datos reales.
- [x] Postulación pública usa API y MySQL.

## Producción y seguridad

- [x] Java 17 verificado.
- [x] MySQL 8.0.42 verificado; H2 limitado al perfil de tests.
- [x] `application.yaml` sin fallback de contraseña DB ni JWT conocido.
- [x] `application-local.example.yaml` sin secretos reales; configuración local privada preservada.
- [x] `application-prod.yaml` con variables obligatorias, CORS explícito, seed false y `ddl-auto=validate`.
- [x] DataSeeder sólo en perfil local, habilitación explícita e idempotencia.
- [x] Angular producción usa `/api`; áreas privadas usan render cliente compatible con sesión SSR.
- [x] Migración SQL aditiva y versionada; no contiene `DROP`.
- [x] Sin stack trace al frontend en producción.
- [x] `npm audit --omit=dev`: 0 vulnerabilidades.
- [~] SMTP implementado; falta prueba con credenciales externas aprobadas.
- [~] WhatsApp deshabilitado y declarado pendiente hasta implementar/probar proveedor real.
- [~] I Love PDF separado y pendiente; OpenPDF permanece operativo.

## Validación

- [x] Backend tests: 8/8, 0 fallos, Java 17.
- [x] Backend package: BUILD SUCCESS, JAR ejecutable.
- [x] Spring Boot local: inicia, conecta MySQL, responde seguridad y se detiene limpiamente.
- [x] Frontend tests: 2/2, 0 fallos.
- [x] Frontend browser + SSR build: exitoso.
- [x] Smoke API: 41 aserciones correctas.
- [x] Smoke visual admin/docente/alumno, configuración, permisos, calendario, notificaciones y logout.
- [x] Cambios reversibles de prueba restaurados; no se crearon registros temporales ni se borraron datos del usuario.

## Documentación y entrega

- [x] `Cierre_funcional_produccion.md` creado.
- [x] Matriz de rúbrica y ruta de demo actualizadas.
- [x] Word existente preservado sin regeneración.
- [x] Inventarios temporales eliminados del entregable.
- [ ] Aplicar migración tras backup en el servidor de producción.
- [ ] Configurar secretos, proxy `/api`, SMTP y dominio/CORS definitivos.
- [ ] Commit y push: no ejecutados por instrucción expresa.

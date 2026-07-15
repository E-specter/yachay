# Matriz de cumplimiento de rúbrica — Yachay

Fecha de corte: 14 de julio de 2026

| Criterio | Implementación/evidencia | Resultado |
|---|---|---|
| Arquitectura web completa | Angular → service `HttpClient` → Spring Controller/Service/Repository → MySQL → refresco por signals | Cumple |
| Seguridad | BCrypt, JWT, expiración, guards, interceptor, 401/403, CORS configurable y rutas por rol | Cumple |
| Autenticación | Login real, recordar sesión, logout, recuperación con token hash/vencimiento/uso único | Cumple |
| Administración | CRUD real de usuarios, alumnos, docentes, cursos, secciones, tareas, notas y comunicados | Cumple |
| Configuración | GET/PUT/restore persistidos; secretos excluidos de UI | Cumple |
| Admisión | Registro público, aceptación transaccional, rechazo con motivo, asignación de sección, PDF/XLSX | Cumple |
| Portal docente | Dashboard, cursos, alumnos, tareas, entregas, notas, comunicados, calendario, notificaciones y perfil desde JWT | Cumple |
| Portal alumno | Dashboard, cursos, tareas/entrega, notas, comunicados/lectura, calendario, notificaciones y perfil desde JWT | Cumple |
| Autorización de datos | Docente/alumno no envían un ID libre para su identidad; servicios validan propiedad y rol | Cumple |
| Calendario | CRUD/archivo admin y lecturas filtradas por docente/alumno; 0 eventos de curso ajeno en smoke | Cumple |
| Notificaciones | Persistencia en `yachay_notifications`, contador no leído, leer una/todas y sincronización de layouts | Cumple |
| Reportes | OpenPDF local y Apache POI XLSX con datos de repositorios reales | Cumple |
| Manejo de errores | Respuestas 400/401/403/404/409/500 y mensajes UI; sin stack trace en prod | Cumple |
| Producción | Variables obligatorias, `/api` relativo, CORS explícito, seed false, `ddl-auto=validate`, logs SQL limitados | Cumple con configuración externa |
| Base de datos | 22 tablas de entidad activas, `user_roles` asociativa, migración SQL aditiva/versionada, sin DROP | Cumple |
| Calidad frontend | 183 acciones revisadas, 0 sin handler, 0 servicios mock/mensajes simulados | Cumple |
| Pruebas | Backend 8/8, frontend 2/2, 41 aserciones API y smoke visual por rol | Cumple |
| Build | JAR Java 17 y Angular browser/SSR generados correctamente | Cumple |
| Dependencias | `npm audit --omit=dev`: 0 vulnerabilidades; librerías oficiales preservadas | Cumple |
| SMTP | JavaMail configurable; no se usaron credenciales no confirmadas | Cumple con configuración externa |
| WhatsApp | No simula envíos; permanece deshabilitado/configuración pendiente | Pendiente externo |
| I Love PDF | Separado de PDF local y no declarado operativo | Pendiente opcional |
| Documentación | Cierre, checklist, matriz y demo alineados con el estado comprobado; Word preservado | Cumple |

## Resultado global

El alcance evaluable del proyecto queda implementado y verificable. Los únicos pendientes no funcionales son despliegue/secretos, la prueba SMTP con credenciales aprobadas y las integraciones opcionales externas de WhatsApp e I Love PDF. No se hizo commit ni push.

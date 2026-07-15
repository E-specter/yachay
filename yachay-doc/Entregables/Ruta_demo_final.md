# Ruta de demo final — Yachay

Duración sugerida: 10 minutos. No proyectar secretos, contraseñas, tokens ni el archivo local privado.

## Preparación

1. Iniciar MySQL y confirmar que la migración del cierre ya fue aplicada en el entorno usado para la demo.
2. Abrir dos terminales sin mostrar variables sensibles.
3. Backend:

```powershell
cd "C:\E-specter - copia (2)\yachay\yachay-backend"
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=local"
```

4. Frontend:

```powershell
cd "C:\E-specter - copia (2)\yachay\yachay-frontend"
npm start
```

5. Tener preparadas tres cuentas locales autorizadas, sin escribir sus claves frente al público.

## Guion de 10 minutos

| Tiempo | Rol | Demostración | Evidencia a señalar |
|---|---|---|---|
| 0:00–0:45 | Presentador | Login administrador y dashboard | JWT, métricas de MySQL y menú por rol |
| 0:45–1:25 | Administrador | Configuración: editar un dato no sensible, guardar y restaurar | GET/PUT/restore real; secretos ausentes |
| 1:25–2:25 | Administrador | Usuarios/alumnos: abrir crear/editar, estado y reportes | Formularios reactivos, BCrypt, PDF y XLSX |
| 2:25–3:15 | Administrador | Cursos/secciones/tareas/notas/comunicados | Relaciones reales y persistencia; no mocks |
| 3:15–4:25 | Administrador | Postulaciones: ver modal de aceptar, rechazo con motivo y asignación de sección | Flujo transaccional; no confirmar si no se desea alterar datos |
| 4:25–5:15 | Administrador | Calendario: filtros, crear/editar/archivar | MySQL, validación de fechas y avisos |
| 5:15–6:00 | Administrador | Notificaciones: marcar una como leída | Contador cambia inmediatamente |
| 6:00–7:20 | Docente | Login, dashboard, cursos, alumnos, tareas, notas y comunicados | Datos derivados del docente JWT; intentar `/admin` devuelve al portal docente/403 API |
| 7:20–8:40 | Alumno | Login, cursos, tareas, notas, calendario y comunicados | Sólo datos del alumno; entrega real y lectura de comunicado |
| 8:40–9:20 | Alumno | Notificaciones, perfil y logout | Lectura persistida, contador y limpieza de sesión |
| 9:20–10:00 | Presentador | Cierre técnico | Java 17, MySQL, tests 8/8 + 2/2, builds y pendientes externos honestos |

## Mensajes clave

- “La identidad de docente/alumno proviene del JWT; cambiar un ID en la URL no permite consultar datos ajenos.”
- “OpenPDF genera el PDF local y Apache POI genera XLSX con datos de repositorio.”
- “El DataSeeder sólo se habilita explícitamente en `local`; producción arranca con seed desactivado.”
- “Producción usa variables obligatorias, CORS explícito, `/api` relativo y `ddl-auto=validate`.”
- “SMTP requiere credenciales del entorno; WhatsApp e I Love PDF no se presentan como operativos.”

## Plan de contingencia

- Si falla una integración externa, no reintentar en vivo: explicar su estado de configuración externa.
- Si el entorno de demo no permite modificar datos, abrir los modales y mostrar la evidencia automatizada del cierre.
- Si MySQL no inicia, mostrar el último resultado de tests/build y corregir el servicio; no cambiar a SQLite/H2 para la demo.
- No borrar registros existentes. Si se crea un registro de demostración, usar un identificador inequívoco y retirarlo sólo mediante el procedimiento autorizado.

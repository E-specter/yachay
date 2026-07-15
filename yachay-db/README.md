# yachay-db

Las migraciones incrementales para una base Yachay existente están en `migrations/`.

Para producción:

1. Respaldar MySQL.
2. Aplicar los scripts pendientes una sola vez y en orden.
3. Iniciar el backend con el perfil `prod`; Hibernate usa `ddl-auto: validate` y no altera datos.

El archivo `V20260714__functional_closure.sql` agrega únicamente columnas y tablas del cierre funcional; no elimina registros.


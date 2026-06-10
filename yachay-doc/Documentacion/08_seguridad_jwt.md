# 08. Seguridad JWT

## Objetivo

Yachay usa autenticacion real con usuarios almacenados en MySQL, passwords protegidos con BCrypt y JWT firmado para proteger los endpoints internos del sistema.

## Flujo de login

1. Angular envia credenciales a `POST /api/auth/login`.
2. `AuthController` delega la validacion a `AuthService`.
3. `AuthService` busca el usuario en MySQL y valida el password con BCrypt.
4. `JwtService` genera un token firmado.
5. Angular guarda el token y el usuario autenticado de forma compatible con SSR.
6. `authInterceptor` agrega `Authorization: Bearer <token>` a las peticiones protegidas.

## Claims del JWT

- `sub`: correo del usuario.
- `userId`: identificador interno.
- `roles`: lista de roles normalizados.
- `iat`: fecha de emision.
- `exp`: fecha de expiracion.

## Reglas de acceso backend

- `POST /api/auth/login`: publico.
- `POST /api/auth/forgot-password`: publico.
- `POST /api/auth/reset-password`: publico.
- `POST /api/admisiones`: publico.
- `/api/admin/**`: requiere `ADMINISTRADOR`.
- `/api/docente/**`: requiere `DOCENTE`.
- `/api/alumno/**`: requiere `ALUMNO`.

## Componentes involucrados

- `JwtService`: genera y valida tokens firmados.
- `JwtAuthFilter`: valida `Authorization: Bearer <token>` en cada request protegida.
- `SecurityConfig`: define CORS, rutas publicas, rutas protegidas y permisos por rol.
- `UserRepository`: carga usuario, roles y perfil desde MySQL.
- `authInterceptor`: adjunta el token en Angular y redirige al login ante `401`.
- `authGuard` y `roleGuard`: protegen rutas del cliente por autenticacion y rol.

## Respuestas esperadas

- Sin token en endpoint protegido: `401 Unauthorized`.
- Token invalido o expirado: `401 Unauthorized`.
- Token valido sin rol requerido: `403 Forbidden`.
- Token valido con rol correcto: respuesta normal del endpoint.

## Notas de seguridad

El secreto JWT se configura mediante `JWT_SECRET` en `application.yaml`, `application-local.yaml` o variables de entorno del servidor. No debe subirse a GitHub ningun secreto real.

# Ejemplos de Solicitudes HTTP - Módulo Identity

## Configuración Base

- **Base URL**: `http://localhost:8080/api/v1`
- **Content-Type**: `application/json`

---

## 1. GESTIÓN DE ROLES

### Crear Rol

```bash
curl -X POST http://localhost:8080/api/v1/roles \
  -H "Content-Type: application/json" \
  -d '{
    "name": "ADMIN",
    "description": "Administrador del sistema"
  }'
```

### Obtener todos los roles

```bash
curl -X GET http://localhost:8080/api/v1/roles
```

### Obtener rol por ID

```bash
curl -X GET http://localhost:8080/api/v1/roles/1
```

### Actualizar rol

```bash
curl -X PUT http://localhost:8080/api/v1/roles/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "ADMIN",
    "description": "Administrador actualizado"
  }'
```

### Eliminar rol

```bash
curl -X DELETE http://localhost:8080/api/v1/roles/1
```

---

## 2. GESTIÓN DE ESCUELAS

### Crear escuela

```bash
curl -X POST http://localhost:8080/api/v1/schools \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Colegio Yachay",
    "code": "YCHAY001",
    "address": "Calle Principal 123",
    "phone": "123-456-7890",
    "logoUrl": "https://ejemplo.com/logo.png",
    "isActive": true
  }'
```

### Obtener todas las escuelas

```bash
curl -X GET http://localhost:8080/api/v1/schools
```

### Obtener escuela por código

```bash
curl -X GET http://localhost:8080/api/v1/schools/code/YCHAY001
```

---

## 3. GESTIÓN DE USUARIOS

### Crear usuario

```bash
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{
    "email": "juan.perez@ejemplo.com",
    "password": "Segura123!",
    "phone": "+34612345678",
    "firstName": "Juan",
    "lastName": "Pérez",
    "avatarUrl": "https://ejemplo.com/avatar.jpg",
    "roleNames": ["STUDENT"]
  }'
```

### Obtener usuario por ID

```bash
curl -X GET http://localhost:8080/api/v1/users/550e8400-e29b-41d4-a716-446655440000
```

### Obtener usuario por email

```bash
curl -X GET http://localhost:8080/api/v1/users/email/juan.perez@ejemplo.com
```

### Obtener todos los usuarios

```bash
curl -X GET http://localhost:8080/api/v1/users
```

### Actualizar usuario

```bash
curl -X PUT http://localhost:8080/api/v1/users/550e8400-e29b-41d4-a716-446655440000 \
  -H "Content-Type: application/json" \
  -d '{
    "email": "juan.perez.nuevo@ejemplo.com",
    "firstName": "Juan Carlos",
    "lastName": "Pérez García"
  }'
```

### Asignar rol a usuario

```bash
curl -X POST http://localhost:8080/api/v1/users/550e8400-e29b-41d4-a716-446655440000/roles/TEACHER
```

### Remover rol de usuario

```bash
curl -X DELETE http://localhost:8080/api/v1/users/550e8400-e29b-41d4-a716-446655440000/roles/TEACHER
```

### Obtener usuarios por rol

```bash
curl -X GET http://localhost:8080/api/v1/users/roles/STUDENT
```

### Eliminar usuario

```bash
curl -X DELETE http://localhost:8080/api/v1/users/550e8400-e29b-41d4-a716-446655440000
```

---

## 4. GESTIÓN DE PERFILES

### Obtener perfil por usuario

```bash
curl -X GET http://localhost:8080/api/v1/profiles/user/550e8400-e29b-41d4-a716-446655440000
```

### Obtener perfil por ID

```bash
curl -X GET http://localhost:8080/api/v1/profiles/1
```

### Obtener todos los perfiles

```bash
curl -X GET http://localhost:8080/api/v1/profiles
```

### Actualizar perfil

```bash
curl -X PUT http://localhost:8080/api/v1/profiles/user/550e8400-e29b-41d4-a716-446655440000 \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Juan",
    "lastName": "Pérez",
    "dateOfBirth": "1995-06-15",
    "avatarUrl": "https://ejemplo.com/nuevo-avatar.jpg"
  }'
```

---

## 5. GESTIÓN DE PERFILES DE ESTUDIANTE

### Crear perfil de estudiante

```bash
curl -X POST http://localhost:8080/api/v1/student-profiles \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "studentCode": "EST001",
    "gradeLevel": 10,
    "section": "A",
    "enrollmentDate": "2026-01-15",
    "schoolId": 1
  }'
```

### Obtener perfil de estudiante

```bash
curl -X GET http://localhost:8080/api/v1/student-profiles/1
```

### Obtener perfil de estudiante por código

```bash
curl -X GET http://localhost:8080/api/v1/student-profiles/code/EST001
```

### Obtener todos los perfiles de estudiante

```bash
curl -X GET http://localhost:8080/api/v1/student-profiles
```

### Actualizar perfil de estudiante

```bash
curl -X PUT http://localhost:8080/api/v1/student-profiles/1 \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "studentCode": "EST002",
    "gradeLevel": 11,
    "section": "B",
    "enrollmentDate": "2026-01-15",
    "schoolId": 1
  }'
```

---

## 6. GESTIÓN DE PERFILES DE DOCENTE

### Crear perfil de docente

```bash
curl -X POST http://localhost:8080/api/v1/teacher-profiles \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "550e8400-e29b-41d4-a716-446655440001",
    "employeeId": "DOC001",
    "specialization": "Matemáticas",
    "hireDate": "2020-08-01",
    "schoolId": 1
  }'
```

### Obtener perfil de docente

```bash
curl -X GET http://localhost:8080/api/v1/teacher-profiles/1
```

### Obtener perfil de docente por ID de empleado

```bash
curl -X GET http://localhost:8080/api/v1/teacher-profiles/employee/DOC001
```

### Actualizar perfil de docente

```bash
curl -X PUT http://localhost:8080/api/v1/teacher-profiles/1 \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "550e8400-e29b-41d4-a716-446655440001",
    "employeeId": "DOC001",
    "specialization": "Matemáticas Avanzadas",
    "hireDate": "2020-08-01",
    "schoolId": 1
  }'
```

---

## 7. GESTIÓN DE PERFILES DE APODERADO

### Crear perfil de apoderado

```bash
curl -X POST http://localhost:8080/api/v1/guardian-profiles \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "550e8400-e29b-41d4-a716-446655440002",
    "relationship": "Padre",
    "occupation": "Ingeniero"
  }'
```

### Obtener perfil de apoderado

```bash
curl -X GET http://localhost:8080/api/v1/guardian-profiles/1
```

### Obtener todos los perfiles de apoderado

```bash
curl -X GET http://localhost:8080/api/v1/guardian-profiles
```

### Asignar estudiante a apoderado

```bash
curl -X POST http://localhost:8080/api/v1/guardian-profiles/1/students/1
```

### Remover estudiante de apoderado

```bash
curl -X DELETE http://localhost:8080/api/v1/guardian-profiles/1/students/1
```

---

## RESPUESTAS DE ERROR

### 404 - Recurso no encontrado

```json
{
  "timestamp": "2026-05-12T10:35:00",
  "status": 404,
  "error": "Recurso No Encontrado",
  "message": "Usuario con ID xxx no encontrado",
  "path": "/api/v1/users/xxx"
}
```

### 409 - Conflicto de recurso

```json
{
  "timestamp": "2026-05-12T10:35:00",
  "status": 409,
  "error": "Conflicto de Recurso",
  "message": "El email 'juan@ejemplo.com' ya está registrado",
  "path": "/api/v1/users"
}
```

### 400 - Error de validación

```json
{
  "timestamp": "2026-05-12T10:35:00",
  "status": 400,
  "error": "Error de Validación",
  "message": "Validación de argumentos fallida",
  "validationErrors": {
    "email": "Email debe ser válido",
    "password": "Contraseña debe tener mínimo 8 caracteres"
  },
  "path": "/api/v1/users"
}
```

---

## NOTAS IMPORTANTES

1. **Autenticación**: Actualmente, los endpoints no requieren autenticación. Se recomienda implementar JWT en futuras
   versiones.

2. **Validación**: Los DTOs incluyen validaciones con `jakarta.validation`. Asegúrate de cumplir con los requisitos.

3. **Transacciones**: Todas las operaciones son transaccionales y garantizan consistencia de datos.

4. **IDs de Usuario**: Son UUID (formato: `550e8400-e29b-41d4-a716-446655440000`).

5. **Códigos de Estudiante/Empleado**: Deben ser únicos en el sistema.

6. **Fechas**: Formato ISO 8601 (YYYY-MM-DD para fechas, ISO 8601 completo para timestamps).

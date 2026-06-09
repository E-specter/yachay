# Módulo Identity - Servicios de Usuarios, Roles y Perfiles

## Descripción General

El módulo **Identity** es responsable de gestionar la autenticación, autorización y perfiles de usuarios del sistema
Yachay. Este módulo implementa la arquitectura hexagonal y proporciona servicios para:

- **Gestión de Usuarios**: Creación, actualización, eliminación de usuarios
- **Gestión de Roles**: Creación y asignación de roles a usuarios
- **Gestión de Perfiles**: Perfiles genéricos y específicos (estudiante, docente, apoderado)
- **Gestión de Escuelas**: Registro de instituciones educativas

## Arquitectura

El módulo sigue la arquitectura hexagonal (puertos y adaptadores) con la siguiente estructura:

```
identity/
├── domain/                          # Capa de dominio
│   ├── models/                     # Entidades JPA
│   │   ├── User.java              # Usuario
│   │   ├── Role.java              # Rol
│   │   ├── Profile.java           # Perfil unificado
│   │   ├── StudentProfile.java    # Perfil de estudiante
│   │   ├── TeacherProfile.java    # Perfil de docente
│   │   ├── GuardianProfile.java   # Perfil de apoderado
│   │   └── School.java            # Escuela
│   ├── repositories/              # Interfaces de repositorios (puertos de salida)
│   └── exceptions/                # Excepciones personalizadas
├── application/                    # Capa de aplicación
│   ├── dtos/                      # Data Transfer Objects
│   │   ├── UserDTO, CreateUserRequest, UpdateUserRequest
│   │   ├── RoleDTO, RoleRequest
│   │   ├── ProfileDTO
│   │   ├── StudentProfileDTO, CreateStudentProfileRequest
│   │   ├── TeacherProfileDTO, CreateTeacherProfileRequest
│   │   ├── GuardianProfileDTO, CreateGuardianProfileRequest
│   │   └── SchoolDTO, SchoolRequest
│   ├── ports/                     # Interfaces de puertos
│   │   └── inputs/               # Puertos de entrada (servicios)
│   └── services/                 # Implementación de servicios de aplicación
│       ├── UserService
│       ├── RoleService
│       ├── ProfileService
│       ├── StudentProfileService
│       ├── TeacherProfileService
│       ├── GuardianProfileService
│       ├── SchoolService
│       └── IdentityMapper         # Mapeo entre entidades y DTOs
└── infrastructure/               # Capa de infraestructura
    ├── adapters/
    │   └── inputs/              # Adaptadores REST (controladores)
    │       ├── UserController
    │       ├── RoleController
    │       ├── ProfileController
    │       ├── StudentProfileController
    │       ├── TeacherProfileController
    │       ├── GuardianProfileController
    │       ├── SchoolController
    │       ├── GlobalExceptionHandler
    │       └── ErrorResponse
    └── config/                  # Configuración
        └── JpaConfig.java
```

## Entidades Principales

### User

Representa un usuario del sistema en la tabla compatible con MySQL `auth_users`.

- **Campos**: id (Long autoincremental), email, phone, displayName, encryptedPassword, confirmaciones, roles
- **Relaciones**: 1-1 con Profile, N-M con Role

### Role

Representa un rol en el sistema (Admin, Docente, Estudiante, etc.).

- **Campos**: id, name, description
- **Relaciones**: N-M con User

### Profile

Perfil unificado de usuario con información personal.

- **Campos**: id, firstName, lastName, dateOfBirth, avatarUrl, isActive
- **Relaciones**: 1-1 con User, 1-1 con StudentProfile, 1-1 con TeacherProfile, 1-1 con GuardianProfile

### StudentProfile

Perfil específico de estudiante.

- **Campos**: studentCode, gradeLevel, section, enrollmentDate
- **Relaciones**: 1-1 con Profile, N-1 con School, N-M con GuardianProfile

### TeacherProfile

Perfil específico de docente.

- **Campos**: employeeId, specialization, hireDate
- **Relaciones**: 1-1 con Profile, N-1 con School

### GuardianProfile

Perfil específico de apoderado/tutor.

- **Campos**: relationship, occupation
- **Relaciones**: 1-1 con Profile, N-M con StudentProfile

### School

Institución educativa.

- **Campos**: name, code, address, phone, logoUrl, isActive
- **Relaciones**: 1-N con StudentProfile, 1-N con TeacherProfile

## Endpoints REST API

### Gestión de Usuarios

```
POST   /api/v1/users                    # Crear usuario
PUT    /api/v1/users/{userId}          # Actualizar usuario
GET    /api/v1/users/{userId}          # Obtener usuario por ID
GET    /api/v1/users/email/{email}     # Obtener usuario por email
GET    /api/v1/users                   # Listar todos los usuarios
DELETE /api/v1/users/{userId}          # Eliminar usuario

POST   /api/v1/users/{userId}/roles/{roleName}    # Asignar rol a usuario
DELETE /api/v1/users/{userId}/roles/{roleName}    # Remover rol de usuario
GET    /api/v1/users/roles/{roleName}             # Listar usuarios por rol
```

### Gestión de Roles

```
POST   /api/v1/roles                   # Crear rol
PUT    /api/v1/roles/{roleId}          # Actualizar rol
GET    /api/v1/roles/{roleId}          # Obtener rol por ID
GET    /api/v1/roles/name/{name}       # Obtener rol por nombre
GET    /api/v1/roles                   # Listar todos los roles
DELETE /api/v1/roles/{roleId}          # Eliminar rol
```

### Gestión de Perfiles

```
GET    /api/v1/profiles/user/{userId}  # Obtener perfil por usuario
GET    /api/v1/profiles/{profileId}    # Obtener perfil por ID
GET    /api/v1/profiles                # Listar todos los perfiles
PUT    /api/v1/profiles/user/{userId}  # Actualizar perfil
DELETE /api/v1/profiles/{profileId}    # Eliminar perfil
```

### Gestión de Perfiles de Estudiante

```
POST   /api/v1/student-profiles                      # Crear perfil de estudiante
GET    /api/v1/student-profiles/{studentProfileId}  # Obtener perfil de estudiante
GET    /api/v1/student-profiles/code/{code}         # Obtener por código
GET    /api/v1/student-profiles                     # Listar todos
PUT    /api/v1/student-profiles/{studentProfileId}  # Actualizar
DELETE /api/v1/student-profiles/{studentProfileId}  # Eliminar
```

### Gestión de Perfiles de Docente

```
POST   /api/v1/teacher-profiles                      # Crear perfil de docente
GET    /api/v1/teacher-profiles/{teacherProfileId}  # Obtener perfil de docente
GET    /api/v1/teacher-profiles/employee/{id}       # Obtener por ID de empleado
GET    /api/v1/teacher-profiles                     # Listar todos
PUT    /api/v1/teacher-profiles/{teacherProfileId}  # Actualizar
DELETE /api/v1/teacher-profiles/{teacherProfileId}  # Eliminar
```

### Gestión de Perfiles de Apoderado

```
POST   /api/v1/guardian-profiles                      # Crear perfil de apoderado
GET    /api/v1/guardian-profiles/{guardianProfileId}  # Obtener perfil
GET    /api/v1/guardian-profiles                      # Listar todos
PUT    /api/v1/guardian-profiles/{guardianProfileId}  # Actualizar
DELETE /api/v1/guardian-profiles/{guardianProfileId}  # Eliminar

POST   /api/v1/guardian-profiles/{guardianId}/students/{studentId}      # Asignar estudiante
DELETE /api/v1/guardian-profiles/{guardianId}/students/{studentId}      # Remover estudiante
```

### Gestión de Escuelas

```
POST   /api/v1/schools                  # Crear escuela
PUT    /api/v1/schools/{schoolId}       # Actualizar escuela
GET    /api/v1/schools/{schoolId}       # Obtener escuela por ID
GET    /api/v1/schools/code/{code}      # Obtener escuela por código
GET    /api/v1/schools                  # Listar todas las escuelas
DELETE /api/v1/schools/{schoolId}       # Eliminar escuela
```

## DTOs Principales

### CreateUserRequest

```json
{
  "email": "usuario@ejemplo.com",
  "password": "contraseña123",
  "phone": "123456789",
  "firstName": "Juan",
  "lastName": "Pérez",
  "avatarUrl": "https://...",
  "roleNames": [
    "STUDENT",
    "PARENT"
  ]
}
```

### UserDTO (Respuesta)

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "email": "usuario@ejemplo.com",
  "phone": "123456789",
  "displayName": "Juan Pérez",
  "emailConfirmed": false,
  "phoneConfirmed": false,
  "roleNames": [
    "STUDENT"
  ],
  "profile": {
    ...
  },
  "createdAt": "2026-05-12T10:30:00",
  "updatedAt": "2026-05-12T10:30:00"
}
```

### CreateStudentProfileRequest

```json
{
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "studentCode": "STU001",
  "gradeLevel": 10,
  "section": "A",
  "enrollmentDate": "2026-01-15",
  "schoolId": 1
}
```

## Configuración de Base de Datos

El modulo utiliza MySQL con JPA/Hibernate. La configuracion se encuentra en `application.yaml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/yachay?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Lima
    username: root
    password:
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        format_sql: true
```

## Manejo de Excepciones

El módulo define dos excepciones personalizadas:

- **ResourceNotFoundException**: Lanzada cuando un recurso no es encontrado (HTTP 404)
- **ResourceConflictException**: Lanzada cuando hay conflictos en recursos duplicados (HTTP 409)

Las excepciones son capturadas por `GlobalExceptionHandler` que retorna respuestas JSON consistentes.

## Servicios de Aplicación

Todos los servicios implementan interfaces de puertos definidas en `application/ports/inputs/`:

- `UserServicePort`: Gestión de usuarios
- `RoleServicePort`: Gestión de roles
- `ProfileServicePort`: Gestión de perfiles
- `StudentProfileServicePort`: Gestión de perfiles de estudiante
- `TeacherProfileServicePort`: Gestión de perfiles de docente
- `GuardianProfileServicePort`: Gestión de perfiles de apoderado
- `SchoolServicePort`: Gestión de escuelas

## Mapeo de Entidades a DTOs

La clase `IdentityMapper` proporciona métodos para convertir entre entidades JPA y DTOs, facilitando la comunicación con
clientes (Angular).

## Uso desde Angular

### Ejemplo: Crear usuario

```typescript
const newUser = {
    email: 'nuevo@ejemplo.com',
    password: 'contraseña123',
    firstName: 'Carlos',
    lastName: 'López',
    roleNames: ['STUDENT']
};

this.http.post('/api/v1/users', newUser).subscribe(
    (response) => console.log('Usuario creado:', response),
    (error) => console.error('Error:', error)
);
```

### Ejemplo: Obtener usuario

```typescript
this.http.get('/api/v1/users/usuario@ejemplo.com').subscribe(
    (user) => console.log('Usuario:', user),
    (error) => console.error('Error:', error)
);
```

### Ejemplo: Asignar rol a usuario

```typescript
this.http.post('/api/v1/users/550e8400-e29b-41d4-a716-446655440000/roles/TEACHER', {})
    .subscribe(
        () => console.log('Rol asignado'),
        (error) => console.error('Error:', error)
    );
```

## Transacciones

Los servicios utilizan anotación `@Transactional` para garantizar consistencia en operaciones que afectan múltiples
entidades. Las operaciones de lectura utilizan `readOnly = true` para optimización.

## Patrones Implementados

- **Arquitectura Hexagonal**: Separación clara entre dominio, aplicación e infraestructura
- **CRUD**: Implementación completa de Create, Read, Update, Delete
- **DTO Pattern**: Uso de DTOs para comunicación con clientes
- **Mapper Pattern**: Conversión centralizada entre entidades y DTOs
- **Repository Pattern**: Abstracción del acceso a datos
- **Service Pattern**: Lógica de negocio centralizada en servicios
- **Exception Handling**: Manejo global de excepciones

## Extensiones Futuras

- [ ] Implementar autenticación con JWT
- [ ] Agregar logging detallado
- [ ] Implementar auditoría (quién, cuándo, qué cambió)
- [ ] Agregar caché para consultas frecuentes
- [ ] Implementar soft-delete para registros
- [ ] Agregar paginación en listados
- [ ] Implementar validaciones adicionales
- [ ] Agregar tests unitarios e integración

## Dependencias

- Spring Boot 4.0.6
- Spring Data JPA
- MySQL Connector/J
- Lombok
- Jakarta Validation

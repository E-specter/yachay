# yachay-backend
Backend de Yachay

- Java 21
- Maven
- Spring 4 + Spring Boot (Apache Tomcat/11.0.21)
- API Rest
- Arquitectura Hexagonal
- .yaml y .xml

# Arquitectura

- module(any service module)
    - application
        - dtos
        - ports
            - inputs
            - outputs
        - services
    - domain
        - exceptions
        - models
        - repositories
    - infrastructure
        - adapters
            - inputs
            - outputs
        - config

# Conexión

## Base de datos

- PostgreSQL

## Presentación

- Angular 21

# Módulos (src/java/edu/yachay/backend/...)

- academic:
- admissions:
- attendance:
- calendar:
- common:
- communication:
- contents:
- identity: It contains the entire service related to users, roles, and profiles.
- notifications:
- resourses:

# Patrones

- Builder
- Patrón CRUD
- Patrón DTO
- ...

# Agregados

- Uso de Enum
- Uso de Record
- Uso de Exception
- Uso de Interfases
- 
# Architecture: Connectors, Adapters and Principles

## Overview

This project follows an architecture inspired by Hexagonal / Clean Architecture:
- `news.domain.model` contains the pure domain.
- `news.application` contains use cases and application services.
- `news.interfaces.rest` contains inbound HTTP adapters.
- `news.infrastructure.persistence.jpa` contains outbound adapters for the database.

The domain layer has no dependencies on Spring or JPA.

## Main connectors and adapters

### 1. Inbound adapter

`FavoriteSourcesController`
- REST controller exposing `/api/v1/favorite-sources`.
- Accepts HTTP DTOs and delegates to application services.
- Validates query parameters and applies safe logging policies.
- Translates application layer results into `ResponseEntity`.

### 2. Application services

`FavoriteSourceCommandServiceImpl`
- Handles the `CreateFavoriteSourceCommand`.
- Orchestrates business validation and aggregate creation.
- Uses the `FavoriteSourceRepository` port to persist data.
- Handles persistence-specific errors (`DataIntegrityViolationException`) and converts them into application results.

`FavoriteSourceQueryServiceImpl`
- Executes domain queries.
- Uses the same `FavoriteSourceRepository` port to read data.
- Maintains read-only transactions.

These services are internal adapters that connect the controller to the domain.

### 3. Outbound port

`FavoriteSourceRepository`
- Domain interface that defines persistence operations.
- Methods:
  - `save(FavoriteSource favoriteSource)`
  - `findById(Long id)`
  - `findAllByNewsApiKey(NewsApiKey newsApiKey)`
  - `existsByNewsApiKeyAndSourceId(NewsApiKey newsApiKey, SourceId sourceId)`
  - `findByNewsApiKeyAndSourceId(NewsApiKey newsApiKey, SourceId sourceId)`
- Serves as the contract between business logic and infrastructure.

### 4. Outbound adapter

`FavoriteSourcePersistenceAdapter`
- Implements `FavoriteSourceRepository`.
- Converts the domain aggregate to a JPA entity before saving.
- Maps JPA entities back to domain aggregates when reading.
- Uses `SpringDataFavoriteSourceJpaRepository` internally.

### 5. Spring Data JPA repository

`SpringDataFavoriteSourceJpaRepository`
- Extends `JpaRepository<FavoriteSourceJpaEntity, Long>`.
- Defines domain-specific queries:
  - `findAllByNewsApiKey`
  - `existsByNewsApiKeyAndSourceId`
  - `findByNewsApiKeyAndSourceId`
- Acts as a technical data access adapter.

### 6. Mappers and converters

`FavoriteSourcePersistenceMapper`
- Maps between `FavoriteSource` and `FavoriteSourceJpaEntity`.
- Keeps the domain free of JPA dependencies.

`NewsApiKeyAttributeConverter` and `SourceIdAttributeConverter`
- Convert `NewsApiKey` and `SourceId` into persistable values for the database.
- Allow value objects to be used directly in JPA entities.

### 7. Assemblers (input/output)

`CreateFavoriteSourceCommandFromResourceAssembler`
- Converts the HTTP resource `CreateFavoriteSourceResource` into the application command `CreateFavoriteSourceCommand`.

`FavoriteSourceResourceFromEntityAssembler`
- Converts the domain aggregate `FavoriteSource` into `FavoriteSourceResource` for the API.

`ResponseEntityFromFavoriteSourceCommandResultAssembler`
- Translates `Result<FavoriteSource, FavoriteSourceCommandFailure>` into an HTTP `ResponseEntity`.
- Separates error handling from controller logic.

`ResponseEntityFromFavoriteSourceQueryResultAssembler`
- Translates query results into HTTP 200, 404 or 400 responses as appropriate.

## Design patterns used

- `Hexagonal Architecture` / `Ports and Adapters`
  - Domain interface `FavoriteSourceRepository` as a port.
  - Infrastructure adapters `FavoriteSourcePersistenceAdapter` and `SpringDataFavoriteSourceJpaRepository`.
  - Inbound adapters like `FavoriteSourcesController`.

- `Repository Pattern`
  - Data access abstraction in `FavoriteSourceRepository`.
  - Domain-specific repository implemented with JPA.

- `Command Pattern`
  - `CreateFavoriteSourceCommand` encapsulates use case input.

- `Query/Command segregation` (lightweight CQRS)
  - Separate services for commands and queries: `FavoriteSourceCommandServiceImpl` and `FavoriteSourceQueryServiceImpl`.

- `Assembler / Translator`
  - Converts between layers: HTTP ↔ Command/Query ↔ Domain ↔ DTO.

- `Value Object`
  - `NewsApiKey` and `SourceId` model value concepts with built-in validation.

- `Result Monad`
  - `Result<T, E>` encapsulates success/failure without using exceptions for control flow.

- `Factory / Static constructor`
  - `FavoriteSource.create(...)` and `FavoriteSource.rehydrate(...)` separate new instance creation from rehydration from persistence.

## Observed best practices

- Clear separation of responsibilities across layers.
- Domain independent from infrastructure and frameworks.
- Invariant validation within Value Objects and aggregates.
- Use of input/output DTOs to decouple the REST API from the domain.
- Explicit business error handling in the application layer (`Result.failure`, conflict detection).
- Transactional control in application services via `@Transactional`.
- Use of `MessageSource` for localized error messages.
- Sensitive-data-aware logging (masks `newsApiKey` in logs).
- Use of entity listeners and JPA auditing (`@CreatedDate`, `@LastModifiedDate`).
- Uniqueness constraints declared at the JPA entity level to ensure data integrity.
- Immutable approach with `record` and Value Objects.

## SOLID principles applied

### S — Single Responsibility Principle

- `FavoriteSourcesController`: exposes REST API and translates resources.
- `FavoriteSourceCommandServiceImpl`: manages the create favorite operation.
- `FavoriteSourceQueryServiceImpl`: manages favorite queries.
- `FavoriteSourcePersistenceAdapter`: translates between domain and persistence.
- `FavoriteSourcePersistenceMapper`: maps objects.

### O — Open/Closed Principle

- The domain module can be extended with new operations via new interfaces or commands without changing its core.
- Adding another persistence technology can be done by implementing another `FavoriteSourceRepository` without changing application logic.

### L — Liskov Substitution Principle

- `FavoriteSourcePersistenceAdapter` can substitute any implementation of `FavoriteSourceRepository`.
- Application services depend on the interface, not a concrete implementation.

### I — Interface Segregation Principle

- Small, specific interfaces: `FavoriteSourceRepository`, `FavoriteSourceCommandService`, `FavoriteSourceQueryService`.
- Controllers and services depend only on methods they actually use.

### D — Dependency Inversion Principle

- The application and domain layers depend on abstractions (`FavoriteSourceRepository`) rather than JPA details.
- Infrastructure implements those abstractions.
- Controllers depend on application services, not repositories directly.

## Key points for AI and documentation

- The `FavoriteSourceRepository` file is the core persistence contract.
- The `FavoriteSourcePersistenceAdapter` bridges the domain and Spring Data.
- `Assembler` classes document layer boundaries.
- The architecture is lightweight and modular: each package represents a clear role in the application.

## Conclusion

The project is designed to keep the domain isolated and testable, with well-delimited adapters in the interface and persistence layers. The use of ports/adapters, Value Objects, DTOs, and a result wrapper supports a solid, easily extensible design.

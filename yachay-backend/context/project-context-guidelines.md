# Lightweight guide: Project structure and how to extend it (for AIs and developers)

## Purpose

Short, machine- and human-readable document to detect architecture, extension points and provide templates for creating new modules (bounded contexts, adapters, ports).

## Signals an AI can use to infer structure

- Semantic package names: `domain`, `application`, `interfaces`, `infrastructure`, `shared`.
- Contracts / ports: interfaces under `domain.model.repositories` or `shared.*`.
- Adapters: classes in `infrastructure.*` implementing domain or shared interfaces.
- Controllers: classes under `interfaces.rest` exposing endpoints.
- JPA entities: classes in `infrastructure.persistence.jpa.entities` annotated with `@Entity`.
- Spring Data repositories: interfaces extending `JpaRepository`.
- Mappers / Assemblers: classes named `*Mapper` or `*Assembler` for layer translation.
- Value Objects: `record` or classes under `domain.model.valueobjects`.

## Package convention and where to add things

Recommended structure for new modules (example `xxx` = bounded context):

- `com.acme.catchup.platform.xxx.domain.model` — entities, value objects, ports/repositories
- `com.acme.catchup.platform.xxx.application` — commands, queries, services (use cases)
- `com.acme.catchup.platform.xxx.interfaces.rest` — controllers and HTTP resources
- `com.acme.catchup.platform.xxx.infrastructure` — adapters (persistence, notifications, storage)
- `com.acme.catchup.platform.xxx.infrastructure.persistence.jpa` — JPA entities, Spring Data repos, mappers
- `com.acme.catchup.platform.xxx.shared` — shared utilities if applicable

Creating a module follows separation of concerns (SRP) by package.

## Minimal module template (files and responsibilities)

- `domain.model.*`:
  - `Xxx.java` (aggregate/root) — domain logic and invariants
  - `valueobjects/*.java` — VOs with validation
  - `repositories/XxxRepository.java` — port (interface)
- `application/commands/*.java` and `application/queries/*.java`
- `application/internal/*ServiceImpl.java` — use case implementations
- `interfaces/rest/*Controller.java` — endpoints
- `interfaces/rest/resources/*Resource.java` — input/output DTOs
- `infrastructure/persistence/jpa/entities/*Entity.java` — JPA entities
- `infrastructure/persistence/jpa/repositories/*JpaRepository.java` — Spring Data
- `infrastructure/persistence/jpa/mappers/*Mapper.java` — domain ↔ jpa mapping
- `infrastructure/*Adapter.java` — adapter implementing ports

## Rules and patterns for AIs generating modules

1. Always create a port (interface) in `domain` for each external dependency.
2. Implement the adapter in `infrastructure` and inject it with Spring (`@Component`).
3. Avoid Spring/JPA references in `domain`.
4. Create Assembler/Mapper to cross boundaries (do not expose JPA entities outside `infrastructure`).
5. Use Value Objects to validate input and protect invariants.
6. Register configuration properties in `application.properties` and map with `@ConfigurationProperties` if multiple keys exist.
7. Add unit tests for the domain (no framework) and integration tests for adapters (slice tests).

## Quick example: Port + Adapter (skeleton)

Port (domain):

```java
package com.acme.catchup.platform.xxx.domain.model.repositories;

import com.acme.catchup.platform.xxx.domain.model.aggregates.Xxx;
import java.util.Optional;

public interface XxxRepository {
    Xxx save(Xxx entity);
    Optional<Xxx> findById(Long id);
}
```

Adapter (infrastructure):

```java
package com.acme.catchup.platform.xxx.infrastructure.persistence.jpa;

import org.springframework.stereotype.Component;
import com.acme.catchup.platform.xxx.domain.model.repositories.XxxRepository;

@Component
public class XxxPersistenceAdapter implements XxxRepository {
    private final SpringDataXxxJpaRepository repo;
    public XxxPersistenceAdapter(SpringDataXxxJpaRepository repo) { this.repo = repo; }
    @Override public Xxx save(Xxx e) { /* map and repo.save */ }
    @Override public Optional<Xxx> findById(Long id) { /* repo.findById.map(mapper) */ }
}
```

## Controller and application service template

- Controller: validate input, build command, call application service, use assembler for response.
- Application service: orchestrate, apply `@Transactional`, use ports/repositories and publish events if needed.

## Events and notifications integration

- For asynchronous flows, publish an application event (`ApplicationEvent` or event bus) from the application service.
- Create an `infrastructure.notifications.EventListenerAdapter` that consumes the event and calls `NotificationService`.
- Keep idempotency and retry policies in mind.

## Documents and file management (quick guidelines)

- Add `DocumentStorage` and `DocumentMetadataRepository` ports in `domain`.
- Implement `LocalFileSystemStorageAdapter` in `infrastructure` for dev and `S3StorageAdapter` for prod.
- Always use `InputStream`/`OutputStream` streaming in controllers (do not load files fully into memory).

## Configuration, profiles and conditional beans

- Register adapters by profile: `@Profile("dev")` for `LocalFileSystemStorageAdapter`, `@Profile("prod")` for `S3StorageAdapter`.
- Use `@ConditionalOnProperty` to enable providers (e.g. `notification.provider=sendgrid`).

## Testing and QA

- Unit tests: pure `domain` — no Spring.
- Integration tests: use `@DataJpaTest` for repos, `@SpringBootTest` for full flows.
- Mock external adapters (Mail, S3, etc.) and use contract tests when integrating providers.

## Mandatory checklist when adding a new module

- [ ] Create port(s) in `domain` for any external dependency
- [ ] Implement adapter(s) in `infrastructure` and register the bean
- [ ] Add mappers/assemblers to cross layers
- [ ] Add domain unit tests
- [ ] Add integration tests for persistent adapters
- [ ] Document routes and configuration in `context/` (add an entry for the new module)
- [ ] Add properties in `application.properties` and an example in `application-dev.properties`

## Metadata for AIs (discovery mechanism)

In `context/` each module can add a small YAML/MD file with fields like:

```yaml
name: "notifications"
packageRoot: "com.acme.catchup.platform.notifications"
boundedContext: "notifications"
ports:
  - NotificationService
adapters:
  - SmtpNotificationAdapter
endpoints:
  - /api/v1/notifications
```

This helps AIs quickly find extension points and generate consistent code.

## Example: quickly generate a new `notifications` module

1. Create packages following the template.
2. Add `NotificationService` in `domain`.
3. Add `NotificationCommand` and `NotificationCommandHandler` in `application`.
4. Implement `SmtpNotificationAdapter` in `infrastructure` and annotate with `@Profile("prod")`.
5. Add `NotificationController` under `interfaces.rest` if an API is needed.
6. Document in `context/notifications.md` and add YAML metadata.

---

Generated file to help AIs and developers extend the system without being tied to existing modules.

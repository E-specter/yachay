---
name: "catchup-context-documentation"
description: "Document and extend the CatchUp platform project context for AI discovery and module creation"
trigger: "document module|extend context|add module documentation|create module spec|update context knowledge|module discovery"
applyTo: "context/*.md"
---

# SKILL: CatchUp Project Context Documentation & Extension

## Purpose

This skill enables IAs and developers to document new bounded contexts, services, adapters, and ports in a consistent way. It serves as the single source of truth for project architecture, making it discoverable by language models and ensuring coherent module generation.

## When to use this skill

- Adding a new bounded context or domain module
- Documenting a new adapter (persistence, notification, storage, API gateway)
- Creating a new application service or command/query handler
- Adding a new REST endpoint or controller
- Extending the architectural guidelines with new patterns
- Auditing existing modules for documentation compliance

## How to document a new module

### Step 1: Create module metadata file

In `context/`, add a file named `modules-{bounded-context-name}.md` with this structure:

```markdown
# Module: {Bounded Context Name}

## Overview
- **Package root:** `com.acme.catchup.platform.{name}`
- **Purpose:** (1-2 sentences describing what this module does)
- **Status:** [active|alpha|deprecated]
- **Owner:** (team or responsible person)

## Ports (Interfaces)

### Port: {PortName}
- **Location:** `domain.model.{something}` or `shared.{something}`
- **Purpose:** (what abstraction does it provide)
- **Methods:**
  - `methodOne(param: Type): ReturnType` — description
  - `methodTwo(param: Type): ReturnType` — description
- **Implementations:** (where it's implemented)

## Adapters (Implementations)

### Adapter: {AdapterName}
- **Location:** `infrastructure.{layer}`
- **Implements:** (which port)
- **Technology:** (Spring, JPA, SMTP, S3, etc.)
- **Lifecycle:** (singleton, request-scoped, etc.)
- **Configuration:**
  - Property: `app.{name}.{property}` → (environment variable mapping)
  - Profile: `@Profile("{dev|prod}")`

## Commands & Queries

### Command: {CommandName}
- **Location:** `application.commands`
- **Handled by:** (service class name)
- **Input:** (DTO or record fields)
- **Output:** `Result<{SuccessType}, {FailureType}>`
- **Transaction:** (read/write/readonly)
- **Invariants:** (business rules protected)

### Query: {QueryName}
- **Location:** `application.queries`
- **Handled by:** (service class name)
- **Input:** (query parameters)
- **Output:** (return type)
- **Transaction:** readonly

## REST Endpoints

- `POST /api/v1/{resource}` → {CommandHandler}
- `GET /api/v1/{resource}/{id}` → {QueryHandler}
- `GET /api/v1/{resource}?param=value` → {QueryHandler}

## Event Publishing

- **Domain events:** (if any)
- **Application events:** (if any)
- **Listeners:** (adapters that consume events)

## Testing strategy

- **Unit tests:** `{ModuleName}DomainTest` (no Spring)
- **Integration tests:** `{ModuleName}IntegrationTest` (Spring slices or `@SpringBootTest`)
- **Mocks:** (external providers to mock)

## Configuration (application.properties example)

```properties
# {Module Name} Configuration
{app.module.property}={default-value}
{app.module.property-desc}={default-value}
```

## Dependencies (Maven pom.xml)

```xml
<!-- Include new dependencies added for this module -->
<dependency>
    <groupId>org.example</groupId>
    <artifactId>library-name</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Known limitations

- (list any constraints or future work)

## Related modules

- (other modules this one depends on or interacts with)
```

### Step 2: Add module to architecture diagram

Update [architecture-connectors-adapters.md](architecture-connectors-adapters.md) with a new section listing the module under its relevant layer.

### Step 3: Add patterns and guidelines

If the module introduces new patterns, add them to [project-context-guidelines.md](project-context-guidelines.md) in the "Design patterns and recommendations" section.

### Step 4: Update metadata registry

Add an entry to a top-level `context/MODULES.yaml` file:

```yaml
modules:
  - name: notifications
    boundedContext: notifications
    packageRoot: com.acme.catchup.platform.notifications
    ports:
      - name: NotificationService
        location: shared.notifications
    adapters:
      - name: SmtpNotificationAdapter
        technology: Spring Mail
        profiles:
          - prod
      - name: LoggingNotificationAdapter
        technology: Spring Logging
        profiles:
          - dev
    endpoints:
      - POST /api/v1/notifications
      - GET /api/v1/notifications/{id}
    status: active
    dependencies:
      - shared
  
  - name: documents
    boundedContext: documents
    packageRoot: com.acme.catchup.platform.documents
    ports:
      - name: DocumentStorage
        location: shared.documents
      - name: DocumentMetadataRepository
        location: documents.domain.repositories
    adapters:
      - name: LocalFileSystemStorageAdapter
        technology: File IO
        profiles:
          - dev
      - name: S3StorageAdapter
        technology: AWS S3
        profiles:
          - prod
    endpoints:
      - POST /api/v1/documents/upload
      - GET /api/v1/documents/{id}
      - DELETE /api/v1/documents/{id}
    status: alpha
    dependencies:
      - shared
```

## Extending the context: workflow

1. **Analyze** existing modules using the discovery mechanism.
2. **Design** the new module following `project-context-guidelines.md`.
3. **Document** the module using the template above.
4. **Update** architecture and guidelines documents.
5. **Register** the module in `MODULES.yaml`.
6. **Generate** code stubs using the documented interfaces.
7. **Test** and iterate.

## AI discovery algorithm

An AI reading `context/` should:

1. Parse `project-context-guidelines.md` to understand package structure and patterns.
2. Parse `MODULES.yaml` to enumerate existing modules and ports.
3. Parse `architecture-connectors-adapters.md` to understand layer boundaries.
4. Parse `modules-*.md` files for specific module details.
5. Parse `notifications-documents-feasibility.md` for integration patterns.
6. Infer missing modules and suggest documentation for new ones.
7. Generate code following the established conventions.

## Checklist for module documentation completeness

- [ ] Module metadata file created (`modules-{name}.md`)
- [ ] All ports documented with methods and purpose
- [ ] All adapters listed with technology and configuration
- [ ] Commands and queries listed with transaction scope
- [ ] REST endpoints mapped to handlers
- [ ] Configuration properties documented in `application.properties` format
- [ ] Dependencies added to documentation (if new)
- [ ] Module registered in `MODULES.yaml`
- [ ] Architecture guide updated with new module section
- [ ] Testing strategy described
- [ ] Known limitations listed
- [ ] Related modules cross-referenced

## File locations reference

| What | Where |
|------|-------|
| Architecture overview | `context/architecture-connectors-adapters.md` |
| Extensibility guide | `context/project-context-guidelines.md` |
| Feasibility studies | `context/notifications-documents-feasibility.md` |
| Module specs | `context/modules-{name}.md` |
| Registry | `context/MODULES.yaml` |
| Implementation | `src/main/java/com/acme/catchup/platform/{name}/` |
| Tests | `src/test/java/com/acme/catchup/platform/{name}/` |

## Example: documenting a new `audit` module

Following this skill, document the audit module:

1. Create `context/modules-audit.md` with ports: `AuditEventRepository`, `AuditService`.
2. List adapters: `DatabaseAuditAdapter` (writes to DB), `EventPublisherAuditAdapter` (publishes events).
3. Document commands: `LogAuditEventCommand`.
4. Document queries: `GetAuditEventsQuery`.
5. Document endpoint: `GET /api/v1/audit-logs`.
6. Add to `MODULES.yaml`.
7. Update `architecture-connectors-adapters.md` with a "Audit Layer" section.
8. Update `project-context-guidelines.md` with audit patterns.

---

## Next steps for using this skill

1. When generating a new module, invoke this skill first to document the intended structure.
2. Use the documented structure as input for code generation.
3. Keep module documentation synchronized with implementation.
4. Periodically audit modules against this checklist.

---

Generated automatically. Last updated: 2026-06-09.

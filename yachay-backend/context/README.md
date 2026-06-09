# CatchUp Platform – Project Context & Knowledge Base

**Quick reference for understanding and extending the CatchUp platform architecture.**

---

## 📖 Documentation Index

### Essential Architecture Docs

| Document | Purpose | Audience |
|----------|---------|----------|
| [architecture-connectors-adapters.md](architecture-connectors-adapters.md) | Core architecture: ports, adapters, SOLID principles applied to the `news` module | Architects, Senior Devs, IAs |
| [project-context-guidelines.md](project-context-guidelines.md) | How to create new modules and extend the system; templates and patterns | All Developers, IAs |
| [SKILL-context-documentation.md](SKILL-context-documentation.md) | Step-by-step guide to document new modules; enforces consistency | IAs, Documentation Owners |
| [MODULES.yaml](MODULES.yaml) | Centralized registry of all bounded contexts, ports, adapters, endpoints | IAs, Module Discovery |

### Feasibility & Integration Studies

| Document | Purpose |
|----------|---------|
| [notifications-documents-feasibility.md](notifications-documents-feasibility.md) | Design patterns and implementation roadmap for notifications (email, in-app) and document management |

---

## 🚀 Quick Start: How to Use This Knowledge Base

### For IAs / Code Generators

1. **Start here:** Read [project-context-guidelines.md](project-context-guidelines.md) — understand the package structure and architectural rules.
2. **Find existing modules:** Parse [MODULES.yaml](MODULES.yaml) to discover ports, adapters, and endpoints.
3. **Understand the example:** Study [architecture-connectors-adapters.md](architecture-connectors-adapters.md) to see how the `news` module implements the architecture.
4. **Document new modules:** Follow the checklist in [SKILL-context-documentation.md](SKILL-context-documentation.md).
5. **Generate code:** Use documented interfaces as input; follow port/adapter patterns from guidelines.

### For Developers (Manual Implementation)

1. **First time?** Read [project-context-guidelines.md](project-context-guidelines.md) to understand where to place code.
2. **Adding a new module?** Follow the mandatory checklist in [SKILL-context-documentation.md](SKILL-context-documentation.md).
3. **Need a reference?** Check [architecture-connectors-adapters.md](architecture-connectors-adapters.md) for the `FavoriteSource` module patterns.
4. **Extending notifications or documents?** See [notifications-documents-feasibility.md](notifications-documents-feasibility.md).

### For Architects / Tech Leads

1. **Review module compliance:** Check [MODULES.yaml](MODULES.yaml) for documented modules.
2. **Audit architecture:** Use the SOLID principles checklist in [architecture-connectors-adapters.md](architecture-connectors-adapters.md).
3. **Plan new features:** Use [notifications-documents-feasibility.md](notifications-documents-feasibility.md) and [project-context-guidelines.md](project-context-guidelines.md).

---

## 📋 Module Status at a Glance

| Module | Status | Docs | Code |
|--------|--------|------|------|
| **news** (Favorite Sources) | ✅ Active | [MODULES.yaml](MODULES.yaml#L8-L110) | `src/main/java/com/acme/catchup/platform/news/` |
| **shared** (Infrastructure) | ✅ Active | [MODULES.yaml](MODULES.yaml#L112-L136) | `src/main/java/com/acme/catchup/platform/shared/` |
| **notifications** | 🔶 Alpha | [feasibility.md](notifications-documents-feasibility.md) | To be created |
| **documents** | 🔶 Alpha | [feasibility.md](notifications-documents-feasibility.md) | To be created |
| **audit** | 📅 Planned | To be created | – |
| **users** | 📅 Planned | To be created | – |

---

## 🏗️ Architecture Overview (One-Liner)

**Hexagonal Architecture with Ports & Adapters:** Domain layer (pure, framework-free) connected to infrastructure via interface contracts. Application services orchestrate. HTTP controllers translate. Adapters plug in (JPA, SMTP, S3, etc.) without changing domain logic.

---

## 🔑 Key Concepts

### Ports (Interfaces)
Domain-level contracts that define what infrastructure services must do.  
Example: `FavoriteSourceRepository` — the domain says "I need to save/fetch aggregates," the infrastructure adapts.

### Adapters (Implementations)
Infrastructure classes implementing port interfaces.  
Example: `FavoriteSourcePersistenceAdapter` — adapts JPA to the repository port.

### Value Objects
Immutable domain concepts with built-in validation.  
Example: `NewsApiKey`, `SourceId` — ensure data is valid at construction time.

### Result Monad
Error handling without exceptions for control flow.  
Example: `Result<FavoriteSource, FavoriteSourceCommandFailure>` — success or application-level failure.

### Bounded Contexts
Modules that own their own domain, services, and infrastructure.  
Example: `news` module owns favorite-sources logic; `notifications` would own email/in-app logic.

---

## 📚 File Locations Reference

```
context/
├── README.md                                    ← You are here
├── architecture-connectors-adapters.md          ← Architecture patterns & SOLID
├── project-context-guidelines.md                ← How to extend the system
├── SKILL-context-documentation.md               ← Document new modules (for IAs)
├── MODULES.yaml                                 ← Module registry
├── notifications-documents-feasibility.md       ← Feasibility & integration guide
└── modules-{bounded-context}.md                 ← (Future) Per-module docs

src/main/java/com/acme/catchup/platform/
├── news/                                        ← (Existing) News/Favorites module
│   ├── domain/model/                            ← Domain: aggregates, VOs, ports
│   ├── application/                             ← Application: commands, queries, services
│   ├── infrastructure/persistence/jpa/          ← Adapters: JPA entities, mappers
│   └── interfaces/rest/                         ← Inbound: controllers, resources
├── shared/                                      ← (Existing) Shared utilities (Result, etc.)
├── notifications/                               ← (Planned) Email, in-app messages
├── documents/                                   ← (Planned) Document storage & mgmt
└── users/                                       ← (Planned) User mgmt & auth
```

---

## ✅ Checklist: Creating a New Module

Before coding, ensure:

- [ ] Module metadata documented in `context/modules-{name}.md`
- [ ] All ports (interfaces) listed with methods
- [ ] All adapters (implementations) listed with technology stack
- [ ] Commands and queries documented
- [ ] REST endpoints mapped
- [ ] Configuration properties defined
- [ ] Module registered in [MODULES.yaml](MODULES.yaml)
- [ ] Architecture docs updated
- [ ] Testing strategy planned
- [ ] Code follows [project-context-guidelines.md](project-context-guidelines.md) conventions

---

## 🤖 How IAs Use This Knowledge Base

1. **Parse MODULES.yaml** → Discover existing modules and their structure.
2. **Read project-context-guidelines.md** → Learn package conventions and SOLID rules.
3. **Infer module ports** → Extract interfaces from documentation.
4. **Generate stubs** → Create domain models, application services, adapters.
5. **Implement adapters** → Connect to external systems (databases, APIs, file systems).
6. **Generate tests** → Create unit tests (domain), integration tests (adapters).
7. **Document** → Add module metadata back to [MODULES.yaml](MODULES.yaml).

**Result:** Consistent, discoverable, maintainable modules that fit the architecture.

---

## 📖 Glossary

| Term | Definition |
|------|-----------|
| **Adapter** | Infrastructure class implementing a domain port interface |
| **Bounded Context** | Independent module with its own domain, services, and database schema |
| **Command** | Request to change state (create, update, delete) |
| **Port** | Domain interface defining a contract for external services |
| **Query** | Request to read state without side effects |
| **Result<T, E>** | Generic type wrapping success (T) or application failure (E) |
| **Value Object** | Immutable domain concept identified by its attributes, not ID |

---

## 🔗 External Resources

- Hexagonal Architecture: https://alistair.cockburn.us/hexagonal-architecture/
- Domain-Driven Design: https://martinfowler.com/bliki/DomainDrivenDesign.html
- SOLID Principles: https://en.wikipedia.org/wiki/SOLID
- Spring Boot Hexagonal Patterns: https://spring.io/guides

---

## 💬 Contributing to the Knowledge Base

When adding a new module:

1. Create `context/modules-{name}.md` following the template in [SKILL-context-documentation.md](SKILL-context-documentation.md).
2. Update [MODULES.yaml](MODULES.yaml) with module metadata.
3. Update this README if adding new patterns or architectural decisions.
4. Ensure code follows [project-context-guidelines.md](project-context-guidelines.md).

---

## 📞 Questions?

- **Architecture questions:** See [architecture-connectors-adapters.md](architecture-connectors-adapters.md)
- **How to extend?** See [project-context-guidelines.md](project-context-guidelines.md)
- **Module-specific?** Check [MODULES.yaml](MODULES.yaml)
- **Documentation process?** See [SKILL-context-documentation.md](SKILL-context-documentation.md)

---

**Last updated:** 2026-06-09  
**For AI-driven development:** Follow the [SKILL](SKILL-context-documentation.md) when creating new modules.

# Feasibility: Notifications, Email and Document Management

## Quick summary

- Yes: the existing architecture (Hexagonal / Ports & Adapters) makes it easy to add notification/email services and document management modules without changing the domain.
- Recommendation: add new ports (interfaces) in the domain/application layer and implement adapters in `infrastructure` (SMTP, cloud providers, storage). Keep controllers/application services responsible for orchestration.

## Notifications and email integration (high-level)

1. Where to place components:
   - Port (domain / application): `com.acme.catchup.platform.shared.notifications.NotificationService` (or per bounded context `news.application.notifications`).
   - Implementations (adapters): `news.infrastructure.notifications.SmtpNotificationAdapter`, `news.infrastructure.notifications.SendgridAdapter`, `shared.infrastructure.notifications.EventPublisherAdapter`.
   - Events: for asynchronous flows, publish domain or application events and subscribe from an adapter (e.g. Spring `ApplicationEventPublisher` or an external event bus).

2. Suggested interface (example):

```java
package com.acme.catchup.platform.shared.notifications;

public interface NotificationService {
    void sendEmail(EmailMessage message);
    void sendInAppNotification(String userId, NotificationPayload payload);
}

record EmailMessage(String to, String subject, String bodyHtml, String bodyText) {}
```

3. Implementations and libraries:
   - SMTP: Spring Boot `spring-boot-starter-mail` + `JavaMailSender`.
   - Providers: SendGrid, Mailgun, AWS SES — use their SDKs or HTTP APIs.
   - Asynchrony: dispatch in background with `@Async`, `@TransactionalEventListener`, or use message queues (RabbitMQ, Kafka).

4. Operational recommendations:
   - Retries and DLQ for failed deliveries.
   - Idempotency (use messageId or deduplication keys when retrying).
   - Templates: Thymeleaf or Mustache for HTML emails.
   - Configuration via `application-*.properties` and `@ConfigurationProperties`.

## Document management and access (PDF, XLSX, DOCX, MD)

1. Where to place components:
   - New bounded context or package: `news.domain.model.documents` and `news.infrastructure.documents`.
   - Ports: `DocumentRepository` and/or `DocumentStorage` (for blobs) and `DocumentMetadataRepository` (for SQL metadata).
   - REST API: `DocumentController` in `interfaces.rest` for upload/download/listing.

2. Suggested interfaces:

```java
package com.acme.catchup.platform.shared.documents;

import java.io.InputStream;
import java.util.Optional;

public interface DocumentStorage {
    String store(String filename, InputStream content, String contentType);
    Optional<InputStream> retrieve(String storageId);
    void delete(String storageId);
}

public interface DocumentMetadataRepository {
    DocumentMetadata save(DocumentMetadata meta);
    Optional<DocumentMetadata> findById(String id);
}
```

3. Storage options:
   - Local filesystem (dev/testing): `LocalFileSystemStorageAdapter`.
   - Cloud blob: AWS S3, Azure Blob Storage, Google Cloud Storage — provider-specific adapters using SDKs.
   - Database: only for metadata; avoid storing large files as BLOBs in production.

4. Libraries for processing:
   - PDF: Apache PDFBox or iText (license considerations) for extraction/manipulation.
   - XLSX / DOCX: Apache POI for read/write.
   - Markdown: commonmark-java or flexmark-java to render/convert to HTML.
   - PDF conversion: external services (LibreOffice headless microservice) or specialized libraries.

5. Security and access control:
   - Authentication and authorization on endpoints (e.g. Spring Security).
   - Pre-signed URLs for direct blob access in cloud providers.
   - Antivirus scanning or MIME/size validation on uploads.

6. Streaming and memory:
   - Always use streaming (`InputStream`) for upload/download to avoid loading large files into memory.
   - Enforce maximum file size limits and use chunked uploads for very large files.

## Design patterns and recommendations

- Create small, focused ports (Dependency Inversion).
- Implement concrete adapters in `infrastructure` and register beans conditionally by profile.
- Optionally use event-driven flows: publish events (e.g. `FavoriteSourceCreated`) from application services and let an adapter send notifications.
- Separate blob storage from metadata storage (Repository + Storage Adapter pattern).
- Use DTOs and Assemblers for HTTP ↔ domain boundaries.

## Example flow (create favorite + notify)

1. `FavoriteSourceCommandServiceImpl` creates the aggregate and persists via `FavoriteSourceRepository`.
2. It publishes an application event `FavoriteSourceCreated`.
3. A `NotificationAdapter` listens to the event and calls `NotificationService.sendInAppNotification` or `sendEmail`.

(Alternatively, call `NotificationService` directly inside the application service for synchronous flows.)

## Non-technical / operational requirements

- Configure SMTP/provider credentials in `application-prod.properties` and bind them to `@ConfigurationProperties`.
- Add monitoring for deliveries (logs, metrics, error tracking).
- Define retention and cleanup policies for stored documents.

## Minimal steps to implement (quick)

1. Add interfaces in `shared` or the `news` bounded context:
   - `NotificationService` and `DocumentStorage`.
2. Create basic adapters:
   - `SmtpNotificationAdapter` (dev: console/logging, prod: JavaMailSender).
   - `LocalFileSystemStorageAdapter` (dev) and `S3StorageAdapter` (prod).
3. Add REST controllers for upload/download and endpoints for notifications if needed.
4. Add unit and integration tests for upload/download flows and email sending (mock external providers).

## Risks and considerations

- Licensing: iText has a restrictive license; prefer Apache PDFBox if a permissive license is required.
- Scalability: local storage is not suitable for production with multiple instances.
- Security: validate and scan uploaded files to prevent execution of malicious content.

## Next steps I can do for you

- Generate stubs for interfaces and adapters (Java files) and a `README.md` with test commands.
- Create a PR with the initial structure and sample tests.


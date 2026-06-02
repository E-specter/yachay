package edu.yachay.backend.identity.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Configuración de JPA para el módulo de identity.
 */
@Configuration
@EnableJpaRepositories(basePackages = "edu.yachay.backend.identity.domain.repositories")
public class JpaConfig {
    // Las configuraciones principales están en application.yaml
}

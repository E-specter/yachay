package edu.yachay.backend.identity.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = {
        "edu.yachay.backend.identity.domain.repositories",
        "edu.yachay.backend.academic.domain.repositories",
        "edu.yachay.backend.admissions.domain.repositories",
        "edu.yachay.backend.notification.domain.repositories"
})
public class JpaConfig {
}
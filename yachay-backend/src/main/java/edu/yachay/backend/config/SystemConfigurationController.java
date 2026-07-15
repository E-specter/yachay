package edu.yachay.backend.config;

import edu.yachay.backend.config.SystemConfigurationService.ConfigurationRequest;
import edu.yachay.backend.config.SystemConfigurationService.ConfigurationResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/configuracion")
public class SystemConfigurationController {
    private final SystemConfigurationService service;

    public SystemConfigurationController(SystemConfigurationService service) {
        this.service = service;
    }

    @GetMapping
    public ConfigurationResponse get() {
        return service.get();
    }

    @PutMapping
    public ConfigurationResponse update(@Valid @RequestBody ConfigurationRequest request) {
        return service.update(request);
    }

    @PostMapping("/restaurar")
    public ConfigurationResponse restore() {
        return service.restore();
    }
}

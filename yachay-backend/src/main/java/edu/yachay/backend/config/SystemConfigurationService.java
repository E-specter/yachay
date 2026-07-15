package edu.yachay.backend.config;

import edu.yachay.backend.identity.domain.models.School;
import edu.yachay.backend.identity.domain.repositories.SchoolRepository;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class SystemConfigurationService {
    private static final String SCHOOL_CODE = "MGP";
    private static final String VERSION_KEY = "visible_version";
    private final SchoolRepository schoolRepository;
    private final SystemSettingRepository settingRepository;
    private final boolean whatsappEnabled;
    private final String mailHost;

    public SystemConfigurationService(SchoolRepository schoolRepository, SystemSettingRepository settingRepository,
                                      @Value("${whatsapp.enabled:false}") boolean whatsappEnabled,
                                      @Value("${spring.mail.host:}") String mailHost) {
        this.schoolRepository = schoolRepository;
        this.settingRepository = settingRepository;
        this.whatsappEnabled = whatsappEnabled;
        this.mailHost = mailHost;
    }

    @Transactional(readOnly = true)
    public ConfigurationResponse get() {
        return response(school());
    }

    @Transactional
    public ConfigurationResponse update(ConfigurationRequest request) {
        School school = school();
        school.setName(request.nombreInstitucion().trim());
        school.setEmail(blank(request.correoInstitucional()));
        school.setPhone(blank(request.telefono()));
        school.setAddress(blank(request.direccion()));
        school.setLogoUrl(blank(request.logoUrl()));
        saveSetting(VERSION_KEY, request.versionVisible());
        return response(schoolRepository.save(school));
    }

    @Transactional
    public ConfigurationResponse restore() {
        School school = school();
        school.setName("Colegio Manuel Gonzales Prada");
        school.setEmail(null);
        school.setPhone(null);
        school.setAddress(null);
        school.setLogoUrl(null);
        saveSetting(VERSION_KEY, "1.0");
        return response(schoolRepository.save(school));
    }

    private ConfigurationResponse response(School school) {
        return new ConfigurationResponse(school.getName(), school.getEmail(), school.getPhone(), school.getAddress(),
                school.getLogoUrl(), setting(VERSION_KEY, "1.0"), !mailHost.isBlank() && !mailHost.contains("tudominio"),
                whatsappEnabled);
    }

    private School school() {
        return schoolRepository.findByCode(SCHOOL_CODE)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Institucion no encontrada."));
    }

    private String setting(String key, String fallback) {
        return settingRepository.findById(key).map(SystemSetting::getValue).filter(value -> !value.isBlank()).orElse(fallback);
    }

    private void saveSetting(String key, String value) {
        SystemSetting setting = settingRepository.findById(key).orElseGet(() -> SystemSetting.builder().key(key).build());
        setting.setValue(value == null ? "" : value.trim());
        settingRepository.save(setting);
    }

    private String blank(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    public record ConfigurationRequest(@NotBlank String nombreInstitucion, @Email String correoInstitucional,
                                       String telefono, String direccion, String logoUrl,
                                       @NotBlank String versionVisible) {
    }

    public record ConfigurationResponse(String nombreInstitucion, String correoInstitucional, String telefono,
                                        String direccion, String logoUrl, String versionVisible,
                                        boolean correoConfigurado, boolean whatsappConfigurado) {
    }
}

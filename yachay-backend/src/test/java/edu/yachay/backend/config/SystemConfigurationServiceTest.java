package edu.yachay.backend.config;

import edu.yachay.backend.identity.domain.models.School;
import edu.yachay.backend.identity.domain.repositories.SchoolRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SystemConfigurationServiceTest {
    @Test
    void updatePersistsSchoolAndVisibleVersionWithoutCredentials() {
        SchoolRepository schools = mock(SchoolRepository.class);
        SystemSettingRepository settings = mock(SystemSettingRepository.class);
        School school = School.builder().id(1).code("MGP").name("Anterior").isActive(true).build();
        when(schools.findByCode("MGP")).thenReturn(Optional.of(school));
        when(schools.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(settings.findById("visible_version")).thenReturn(Optional.empty());
        when(settings.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        SystemConfigurationService service = new SystemConfigurationService(schools, settings, false, "");

        var response = service.update(new SystemConfigurationService.ConfigurationRequest(
                "Institución Yachay", "contacto@yachay.edu.pe", "999111222", "Lima", null, "2.0"));

        assertThat(response.nombreInstitucion()).isEqualTo("Institución Yachay");
        assertThat(response.correoConfigurado()).isFalse();
        assertThat(response.whatsappConfigurado()).isFalse();
        verify(schools).save(school);
        verify(settings).save(argThat(setting -> setting.getKey().equals("visible_version") && setting.getValue().equals("2.0")));
    }
}

package co.com.bancolombia.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.tags.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OpenApiConfigTest {

    private OpenAPI openAPI;

    @BeforeEach
    void setUp() {
        openAPI = new OpenApiConfig().customOpenAPI();
    }

    @Test
    void customOpenAPI_hasExpectedInfo() {
        Info info = openAPI.getInfo();
        assertThat(info).isNotNull();
        assertThat(info.getTitle()).isEqualTo("Franchise API");
        assertThat(info.getVersion()).isEqualTo("1.0.0");
        assertThat(info.getSummary()).contains("Reactive");
        assertThat(info.getDescription()).contains("Clean Architecture");
    }

    @Test
    void customOpenAPI_hasPragmaContact() {
        assertThat(openAPI.getInfo().getContact()).isNotNull();
        assertThat(openAPI.getInfo().getContact().getName()).isEqualTo("Pragma Software");
        assertThat(openAPI.getInfo().getContact().getEmail()).isEqualTo("info@pragma.com.co");
        assertThat(openAPI.getInfo().getContact().getUrl()).isEqualTo("https://pragma.com.co");
    }

    @Test
    void customOpenAPI_hasApache2License() {
        assertThat(openAPI.getInfo().getLicense()).isNotNull();
        assertThat(openAPI.getInfo().getLicense().getName()).isEqualTo("Apache 2.0");
        assertThat(openAPI.getInfo().getLicense().getUrl()).contains("apache.org");
    }

    @Test
    void customOpenAPI_hasLocalServer() {
        assertThat(openAPI.getServers()).hasSize(1);
        assertThat(openAPI.getServers().get(0).getUrl()).isEqualTo("http://localhost:8080");
        assertThat(openAPI.getServers().get(0).getDescription()).containsIgnoringCase("local");
    }

    @Test
    void customOpenAPI_hasThreeTopLevelTags() {
        List<Tag> tags = openAPI.getTags();
        assertThat(tags).hasSize(3);
        assertThat(tags).extracting(Tag::getName)
                .containsExactlyInAnyOrder("Franchises", "Branches", "Products");
    }

    @Test
    void customOpenAPI_tagsHaveDescriptions() {
        assertThat(openAPI.getTags())
                .allSatisfy(t -> assertThat(t.getDescription()).isNotBlank());
    }
}


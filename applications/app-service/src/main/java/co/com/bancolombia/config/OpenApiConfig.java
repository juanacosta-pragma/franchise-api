package co.com.bancolombia.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        Contact contact = new Contact();
        contact.setName("Pragma Software");
        contact.setUrl("https://pragma.com.co");
        contact.setEmail("info@pragma.com.co");

        License license = new License();
        license.setName("Apache 2.0");
        license.setUrl("https://www.apache.org/licenses/LICENSE-2.0.html");

        Info info = new Info()
                .title("Franchise API")
                .description("API para la gestión de franquicias, sucursales y productos")
                .version("1.0.0")
                .contact(contact)
                .license(license)
                .summary("API RESTful reactiva para gestionar franquicias");

        Server devServer = new Server()
                .url("http://localhost:8080")
                .description("Servidor de Desarrollo");

        List<Server> servers = new ArrayList<>();
        servers.add(devServer);

        return new OpenAPI()
                .info(info)
                .servers(servers);
    }
}


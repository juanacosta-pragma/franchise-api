package co.com.bancolombia;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.core.env.Environment;

@SpringBootApplication
@ConfigurationPropertiesScan
public class MainApplication {

    private static final Logger log = LoggerFactory.getLogger(MainApplication.class);

    public static void main(String[] args) {
        // ─── Sentinela de versión: si NO ves esta línea en CloudWatch ANTES del
        // banner de Spring Boot, la imagen que está corriendo NO contiene este
        // código (la build/push/deployment no se aplicó). ───
        System.out.println("##### BUILD-MARKER: franchise-api v-debug-mongo-uri-2026-06-11 #####");
        System.out.println("##### ENV SPRING_DATA_MONGODB_URI present? " +
                (System.getenv("SPRING_DATA_MONGODB_URI") != null) + " #####");
        System.out.println("##### ENV SPRING_DATA_MONGODB_URI length = " +
                (System.getenv("SPRING_DATA_MONGODB_URI") == null ? 0 : System.getenv("SPRING_DATA_MONGODB_URI").length()) + " #####");
        System.out.println("##### ENV SPRING_DATA_MONGODB_URI contains authSource=admin = " +
                (System.getenv("SPRING_DATA_MONGODB_URI") != null && System.getenv("SPRING_DATA_MONGODB_URI").contains("authSource=admin")) + " #####");

        SpringApplication.run(MainApplication.class, args);
    }

    /**
     * Imprime las variables de entorno y propiedades relevantes para Mongo
     * cuando la aplicación está lista. Útil para descartar problemas de
     * configuración (URI, database, override por env vars).
     */
    @org.springframework.context.annotation.Bean
    org.springframework.boot.CommandLineRunner mongoDebugRunner(Environment env) {
        return args -> {
            log.warn(">>>>>>>>>>>>>>>>>> ENV / PROPERTY DEBUG <<<<<<<<<<<<<<<<<<");
            log.warn("ENV  SPRING_DATA_MONGODB_URI      = {}", mask(System.getenv("SPRING_DATA_MONGODB_URI")));
            log.warn("ENV  SPRING_DATA_MONGODB_DATABASE = {}", System.getenv("SPRING_DATA_MONGODB_DATABASE"));
            log.warn("PROP spring.data.mongodb.uri      = {}", mask(env.getProperty("spring.data.mongodb.uri")));
            log.warn("PROP spring.data.mongodb.database = {}", env.getProperty("spring.data.mongodb.database"));
            log.warn(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        };
    }

    private static String mask(String uri) {
        if (uri == null) return "<null>";
        return uri.replaceAll("(mongodb(?:\\+srv)?://[^:/?#]+:)[^@]+(@)", "$1***$2");
    }
}
package co.com.bancolombia.mongo.config;

import co.com.bancolombia.mongo.branch.MongoDBBranchRepository;
import co.com.bancolombia.mongo.franchise.MongoDBFranchiseRepository;
import co.com.bancolombia.mongo.franchise.MongoRepositoryFranchiseAdapter;
import co.com.bancolombia.mongo.branch.MongoRepositoryBranchAdapter;
import org.reactivecommons.utils.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.mongodb.autoconfigure.MongoConnectionDetails;
import org.springframework.boot.mongodb.autoconfigure.MongoProperties;
import org.springframework.boot.mongodb.autoconfigure.PropertiesMongoConnectionDetails;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoConfig {

    private static final Logger log = LoggerFactory.getLogger(MongoConfig.class);

    @Bean
    public MongoDBSecret dbSecret(@Value("${spring.data.mongodb.uri}") String uri,
                                  @Value("${spring.data.mongodb.database:<not-set>}") String database) {
        // ─── DEBUG: imprimir la URI vigente con la contraseña enmascarada ───
        // Útil para confirmar en CloudWatch qué URI recibió realmente la task
        // (descarta dudas sobre Terraform, env vars o overrides).
        log.warn("============== MONGODB CONNECTION DEBUG ==============");
        log.warn("spring.data.mongodb.database = {}", database);
        log.warn("spring.data.mongodb.uri      = {}", maskUri(uri));
        log.warn("======================================================");

        return MongoDBSecret.builder()
                .uri(uri)
                .build();
    }

    @Bean
    public MongoConnectionDetails mongoProperties(MongoDBSecret secret, SslBundles sslBundles) {
        MongoProperties properties = new MongoProperties();
        properties.setUri(secret.getUri());
        return new PropertiesMongoConnectionDetails(properties, sslBundles);
    }

    @Bean
    @ConditionalOnProperty(name = "spring.data.mongodb.enabled", havingValue = "true")
    public MongoRepositoryFranchiseAdapter mongoRepositoryFranchiseAdapter(MongoDBFranchiseRepository repository, ObjectMapper mapper) {
        return new MongoRepositoryFranchiseAdapter(repository, mapper);
    }


    @Bean
    @ConditionalOnProperty(name = "spring.data.mongodb.enabled", havingValue = "true")
    public MongoRepositoryBranchAdapter mongoRepositoryBranchAdapter(MongoDBBranchRepository repository, ObjectMapper mapper) {
        return new MongoRepositoryBranchAdapter(repository, mapper);
    }

    /**
     * Sustituye la contraseña de la URI por '***' para no filtrarla en logs.
     * Acepta tanto 'mongodb://' como 'mongodb+srv://'.
     */
    private static String maskUri(String uri) {
        if (uri == null) {
            return "<null>";
        }
        // patrón: scheme://user:password@host...   -> reemplaza solo la password
        return uri.replaceAll("(mongodb(?:\\+srv)?://[^:/?#]+:)[^@]+(@)", "$1***$2");
    }
}


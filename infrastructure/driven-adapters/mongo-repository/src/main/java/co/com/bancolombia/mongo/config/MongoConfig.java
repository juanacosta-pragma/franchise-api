package co.com.bancolombia.mongo.config;

import co.com.bancolombia.mongo.branch.MongoDBBranchRepository;
import co.com.bancolombia.mongo.franchise.MongoDBFranchiseRepository;
import co.com.bancolombia.mongo.franchise.MongoRepositoryFranchiseAdapter;
import co.com.bancolombia.mongo.branch.MongoRepositoryBranchAdapter;
import org.reactivecommons.utils.ObjectMapper;
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

    @Bean
    public MongoDBSecret dbSecret(@Value("${spring.data.mongodb.uri}") String uri) {
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
}

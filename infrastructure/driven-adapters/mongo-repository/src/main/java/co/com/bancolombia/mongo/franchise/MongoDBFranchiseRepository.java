package co.com.bancolombia.mongo.franchise;

import co.com.bancolombia.model.franchise.Franchise;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface MongoDBFranchiseRepository extends ReactiveMongoRepository<Franchise, String>, ReactiveQueryByExampleExecutor<Franchise>, ReactiveCrudRepository<Franchise, String> {
}

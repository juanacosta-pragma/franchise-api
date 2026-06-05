package co.com.bancolombia.mongo.branch;

import co.com.bancolombia.model.branch.Branch;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface MongoDBBranchRepository extends ReactiveMongoRepository<Branch, String>, ReactiveQueryByExampleExecutor<Branch>, ReactiveCrudRepository<Branch, String> {
}

package co.com.bancolombia.mongo.franchise;

import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.franchise.gateways.FranchiseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class StubFranchiseRepository implements FranchiseRepository {
    private final MongoRepositoryFranchiseAdapter mongoRepositoryAdapter;

    @Override
    public Flux<Franchise> getAllFranchises() {
        return mongoRepositoryAdapter.getAllFranchises();
    }

    @Override
    public Mono<Franchise> findById(String id) {
        return mongoRepositoryAdapter.findById(id);
    }

    @Override
    public Mono<Franchise> save(Franchise franchise) {
        return mongoRepositoryAdapter.save(franchise);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return mongoRepositoryAdapter.deleteById(id);
    }
}



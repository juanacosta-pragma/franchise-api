package co.com.bancolombia.model.franchise.gateways;

import co.com.bancolombia.model.franchise.Franchise;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FranchiseRepository {
    Flux<Franchise> getAllFranchises();
    Mono<Franchise> findById(String id);
    Mono<Franchise> save(Franchise franchise);
    Mono<Void> deleteById(String id);
}

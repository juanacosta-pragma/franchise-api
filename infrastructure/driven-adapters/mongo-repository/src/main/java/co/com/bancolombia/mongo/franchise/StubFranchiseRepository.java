package co.com.bancolombia.mongo.franchise;

import co.com.bancolombia.mongo.exception.DatabaseException;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.franchise.gateways.FranchiseRepository;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
@Slf4j
public class StubFranchiseRepository implements FranchiseRepository {
    private final MongoRepositoryFranchiseAdapter mongoRepositoryAdapter;
    private final CircuitBreakerRegistry circuitBreakerRegistry;

    @Override
    public Flux<Franchise> getAllFranchises() {
        return mongoRepositoryAdapter.getAllFranchises()
            .transformDeferred(CircuitBreakerOperator.of(getCircuitBreaker()))
            .onErrorResume(this::handleFluxError);
    }

    @Override
    public Mono<Franchise> findById(String id) {
        return mongoRepositoryAdapter.findById(id)
            .transformDeferred(CircuitBreakerOperator.of(getCircuitBreaker()))
            .onErrorResume(ex -> handleMonoError(ex, "Getting by ID: " + id));
    }

    @Override
    public Mono<Franchise> save(Franchise franchise) {
        return mongoRepositoryAdapter.save(franchise)
            .transformDeferred(CircuitBreakerOperator.of(getCircuitBreaker()))
            .onErrorResume(ex -> handleMonoError(ex, "Save franchise: " + franchise.getId()));
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return mongoRepositoryAdapter.deleteById(id)
            .transformDeferred(CircuitBreakerOperator.of(getCircuitBreaker()))
            .onErrorResume(ex -> handleMonoVoidError(ex, "Deleted by ID: " + id));
    }

    private CircuitBreaker getCircuitBreaker() {
        return circuitBreakerRegistry.circuitBreaker("mongoCircuitBreaker");
    }

    private <T> Flux<T> handleFluxError(Throwable ex) {
        log.error("Franchise - MongoDB Service is unavailable. Detail: {}", ex.getMessage());
        return Flux.error(new DatabaseException("Database service temporarily unavailable", ex));
    }

    private Mono<Franchise> handleMonoError(Throwable ex, String context) {
        log.error("Franchise - {}. Detail: {}", context, ex.getMessage());
        return Mono.error(new DatabaseException("Database service temporarily unavailable", ex));
    }

    private Mono<Void> handleMonoVoidError(Throwable ex, String context) {
        log.error("Franchise - {}. Detail: {}", context, ex.getMessage());
        return Mono.error(new DatabaseException("Database service temporarily unavailable", ex));
    }
}



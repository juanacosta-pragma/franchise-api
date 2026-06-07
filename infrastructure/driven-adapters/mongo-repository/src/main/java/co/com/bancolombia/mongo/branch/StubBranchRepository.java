package co.com.bancolombia.mongo.branch;

import co.com.bancolombia.mongo.exception.DatabaseException;
import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.branch.gateways.BranchRepository;
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
public class StubBranchRepository implements BranchRepository {
    private final MongoRepositoryBranchAdapter mongoRepositoryAdapter;
    private final CircuitBreakerRegistry circuitBreakerRegistry;

    @Override
    public Flux<Branch> getAllBranches() {
        return mongoRepositoryAdapter.getAllBranches()
            .transformDeferred(CircuitBreakerOperator.of(getCircuitBreaker()))
            .onErrorResume(this::handleFluxError);
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
        log.error("Branch Error  - MongoDB is unavailable. Detail: {}", ex.getMessage());
        return Flux.error(new DatabaseException("Database service temporarily unavailable", ex));
    }

    private Mono<Branch> handleMonoError(Throwable ex, String context) {
        log.error("Branch Error - {}. Detail: {}", context, ex.getMessage());
        return Mono.error(new DatabaseException("Database service temporarily unavailable", ex));
    }

    private Mono<Void> handleMonoVoidError(Throwable ex, String context) {
        log.error("Branch Error - {}. Detail: {}", context, ex.getMessage());
        return Mono.error(new DatabaseException("Database service temporarily unavailable", ex));
    }
}

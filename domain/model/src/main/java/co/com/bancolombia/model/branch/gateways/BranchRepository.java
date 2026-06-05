package co.com.bancolombia.model.branch.gateways;

import co.com.bancolombia.model.branch.Branch;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BranchRepository {
    Flux<Branch> getAllBranches();
    Mono<Branch> findById(String id);
    Mono<Branch> save(Branch branch);
    Mono<Void> deleteById(String id);
}


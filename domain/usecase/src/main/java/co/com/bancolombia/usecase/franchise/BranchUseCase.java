package co.com.bancolombia.usecase.franchise;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.branch.gateways.BranchRepository;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.franchise.gateways.FranchiseRepository;
import co.com.bancolombia.usecase.franchise.exceptions.BranchNotFoundException;
import co.com.bancolombia.usecase.franchise.exceptions.FranchiseNotFoundException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;


@RequiredArgsConstructor
public class BranchUseCase {
    private final BranchRepository branchRepository;
    private final FranchiseRepository franchiseRepository;

    public Flux<Branch> getAllBranches() {
        return branchRepository.getAllBranches();
    }

    public Mono<Franchise> createFranchise(String franchiseId, Branch branch) {
        return findFranchise(franchiseId)
                .flatMap(franchise -> Flux.fromIterable(franchise.getBranches())
                        .concatWithValues(branch.withId(UUID.randomUUID().toString()))
                        .collectList()
                        .map(franchise::withBranches))
                .flatMap(franchiseRepository::save);
    }

    public Mono<Branch> getFranchiseById(String franchiseId, String branchId) {
        return findFranchise(franchiseId)
                .flatMapMany(franchise -> Flux.fromIterable(franchise.getBranches()))
                .filter(b -> branchId.equals(b.getId()))
                .next()
                .switchIfEmpty(Mono.error(new BranchNotFoundException("Branch not found with id: " + branchId)));
    }

    public Mono<Franchise> updateFranchiseName(String franchiseId, String branchId, String newName) {
        return findFranchise(franchiseId)
                .flatMap(franchise -> Flux.fromIterable(franchise.getBranches())
                        .filter(b -> branchId.equals(b.getId()))
                        .next()
                        .switchIfEmpty(Mono.error(new BranchNotFoundException("Branch not found with id: " + branchId)))
                        .thenMany(Flux.fromIterable(franchise.getBranches()))
                        .map(b -> branchId.equals(b.getId()) ? b.withName(newName) : b)
                        .collectList()
                        .map(franchise::withBranches))
                .flatMap(franchiseRepository::save);
    }

    public Mono<Void> deleteBranch(String id) {
        return branchRepository.deleteById(id);
    }

    private Mono<Franchise> findFranchise(String franchiseId) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new FranchiseNotFoundException("Franchise not found with id: " + franchiseId)));
    }
}
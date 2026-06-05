package co.com.bancolombia.usecase.franchise;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.branch.gateways.BranchRepository;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.franchise.gateways.FranchiseRepository;
import co.com.bancolombia.usecase.franchise.exceptions.BranchNotFoundException;
import co.com.bancolombia.usecase.franchise.exceptions.FranchiseNotFoundException;
import co.com.bancolombia.usecase.franchise.exceptions.ValidationException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.UUID;


@RequiredArgsConstructor
public class BranchUseCase {
    private final BranchRepository branchRepository;
    private final FranchiseRepository franchiseRepository;

    public Flux<Branch> getAllBranches() {
        return branchRepository.getAllBranches();
    }

    public Mono<Franchise> createFranchise(String franchiseId, Branch branch) {
        if (branch.getName() == null || branch.getName().isBlank()) {
            return Mono.error(new ValidationException("Branch name is required"));
        }

        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new FranchiseNotFoundException("Franchise not found with id: " + franchiseId)))
                .flatMap(franchise -> {
                    branch.setId(UUID.randomUUID().toString());
                    branch.setProducts(new ArrayList<>());
                    if (franchise.getBranches() == null) {
                        franchise.setBranches(new ArrayList<>());
                    }
                    franchise.getBranches().add(branch);
                    return franchiseRepository.save(franchise);
                });
    }

    public Mono<Branch> getFranchiseById(String franchiseId, String branchId) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new FranchiseNotFoundException("Franchise not found with id: " + franchiseId)))
                .flatMap(franchise -> {
                    if (franchise.getBranches() == null) {
                        return Mono.error(new BranchNotFoundException("Branch not found with id: " + branchId));
                    }
                    return franchise.getBranches().stream()
                            .filter(b -> b.getId().equals(branchId))
                            .findFirst()
                            .map(Mono::just)
                            .orElseGet(() -> Mono.error(new BranchNotFoundException("Branch not found with id: " + branchId)));
                });
    }

    public Mono<Franchise> updateFranchiseName(String franchiseId, String branchId, String newName) {
        if (newName == null || newName.isBlank()) {
            return Mono.error(new ValidationException("Branch name is required"));
        }
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new FranchiseNotFoundException("Franchise not found with id: " + franchiseId)))
                .flatMap(franchise -> {
                    if (franchise.getBranches() == null || franchise.getBranches().isEmpty()) {
                        return Mono.error(new BranchNotFoundException("Branch not found with id: " + branchId));
                    }

                    Branch branch = franchise.getBranches().stream()
                            .filter(b -> b.getId().equals(branchId))
                            .findFirst()
                            .orElseThrow(() -> new BranchNotFoundException("Branch not found with id: " + branchId));

                    branch.setName(newName);
                    return franchiseRepository.save(franchise);
                });
    }

    public Mono<Void> deleteBranch(String id) {
        return branchRepository.deleteById(id);
    }
}
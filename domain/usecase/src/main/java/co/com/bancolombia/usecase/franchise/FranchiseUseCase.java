package co.com.bancolombia.usecase.franchise;

import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.franchise.gateways.FranchiseRepository;
import co.com.bancolombia.usecase.franchise.exceptions.FranchiseNotFoundException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;


@RequiredArgsConstructor
public class FranchiseUseCase {
    private final FranchiseRepository franchiseRepository;

    public Flux<Franchise> getAllFranchises() {
        return franchiseRepository.getAllFranchises();
    }

    public Mono<Franchise> createFranchise(Franchise franchise) {
        return franchiseRepository.save(franchise.withBranches(List.of()));
    }

    public Mono<Franchise> getFranchiseById(String id) {
        return franchiseRepository.findById(id)
                .switchIfEmpty(Mono.error(new FranchiseNotFoundException("Franchise not found with id: " + id)));
    }

    public Mono<Franchise> updateFranchiseName(String id, String newName) {
        return franchiseRepository.findById(id)
                .switchIfEmpty(Mono.error(new FranchiseNotFoundException("Franchise not found with id: " + id)))
                .map(franchise -> franchise.withName(newName))
                .flatMap(franchiseRepository::save);
    }

    public Mono<Void> deleteFranchise(String id) {
        return franchiseRepository.deleteById(id);
    }
}
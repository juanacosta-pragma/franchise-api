package co.com.bancolombia.usecase.franchise;

import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.franchise.gateways.FranchiseRepository;
import co.com.bancolombia.usecase.franchise.exceptions.FranchiseNotFoundException;
import co.com.bancolombia.usecase.franchise.exceptions.ValidationException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;


@RequiredArgsConstructor
public class FranchiseUseCase {
    private final FranchiseRepository franchiseRepository;

    public Flux<Franchise> getAllFranchises() {
        return franchiseRepository.getAllFranchises();
    }

    public Mono<Franchise> createFranchise(Franchise franchise) {
        if (franchise.getName() == null || franchise.getName().isBlank()) {
            return Mono.error(new ValidationException("Franchise name is required"));
        }

        franchise.setBranches(new ArrayList<>());
        return franchiseRepository.save(franchise);
    }

    public Mono<Franchise> getFranchiseById(String id) {
        return franchiseRepository.findById(id)
                .switchIfEmpty(Mono.error(new FranchiseNotFoundException("Franchise not found with id: " + id)));
    }

    public Mono<Franchise> updateFranchiseName(String id, String newName) {
        if (newName == null || newName.isBlank()) {
            return Mono.error(new ValidationException("Franchise name is required"));
        }
        return franchiseRepository.findById(id)
                .switchIfEmpty(Mono.error(new FranchiseNotFoundException("Franchise not found with id: " + id)))
                .flatMap(franchise -> {
                    franchise.setName(newName);
                    return franchiseRepository.save(franchise);
                });
    }

    public Mono<Void> deleteFranchise(String id) {
        return franchiseRepository.deleteById(id);
    }
}
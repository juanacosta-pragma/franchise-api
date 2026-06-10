package co.com.bancolombia.api.franchise;

import co.com.bancolombia.api.franchise.dto.FranchiseRequest;
import co.com.bancolombia.api.franchise.dto.FranchiseResponse;
import co.com.bancolombia.api.franchise.mapper.FranchiseMapper;
import co.com.bancolombia.usecase.franchise.FranchiseUseCase;
import co.com.bancolombia.usecase.franchise.exceptions.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
@RequiredArgsConstructor
@Slf4j
public class FranchiseHandler {

    private final FranchiseUseCase franchiseUseCase;
    private final FranchiseMapper franchiseMapper;

    public Mono<ServerResponse> createFranchise(ServerRequest request) {
        return request.bodyToMono(FranchiseRequest.class)
                .doFirst(() -> log.info("Received request to create franchise"))
                .filter(franchiseRequest -> franchiseRequest.getName() != null && !franchiseRequest.getName().isBlank())
                .switchIfEmpty(Mono.error(new ValidationException("Franchise name is required")))
                .map(franchiseMapper::toModel)
                .flatMap(franchiseUseCase::createFranchise)
                .flatMap(franchise -> ServerResponse.created(URI.create("/franchises/" + franchise.getId()))
                        .bodyValue(franchiseMapper.toResponse(franchise)))
                .doOnError(error -> log.error("Error creating franchise", error));
    }

    public Mono<ServerResponse> getFranchiseById(ServerRequest request) {
        String id = request.pathVariable("id");
        return franchiseUseCase.getFranchiseById(id)
                .doFirst(() -> log.info("Received request to get franchise with id: {}", id))
                .flatMap(franchise -> ServerResponse.ok().bodyValue(franchiseMapper.toResponse(franchise)))
                .doOnError(error -> log.error("Error getting franchise", error));
    }

    public Mono<ServerResponse> getAllFranchises(ServerRequest request) {
        return ServerResponse.ok()
                .body(franchiseUseCase.getAllFranchises().map(franchiseMapper::toResponse), FranchiseResponse.class)
                .doFirst(() -> log.info("Received request to get all franchises"))
                .doOnError(error -> log.error("Error getting franchises", error));
    }

    public Mono<ServerResponse> updateFranchiseName(ServerRequest request) {
        String id = request.pathVariable("id");
        return request.bodyToMono(FranchiseRequest.class)
                .doFirst(() -> log.info("Received request to update franchise with id: {}", id))
                .filter(franchiseRequest -> franchiseRequest.getName() != null && !franchiseRequest.getName().isBlank())
                .switchIfEmpty(Mono.error(new ValidationException("Franchise name is required")))
                .flatMap(franchiseRequest -> franchiseUseCase.updateFranchiseName(id, franchiseRequest.getName()))
                .flatMap(franchise -> ServerResponse.ok().bodyValue(franchiseMapper.toResponse(franchise)))
                .doOnError(error -> log.error("Error updating franchise", error));
    }

    public Mono<ServerResponse> deleteFranchise(ServerRequest request) {
        String id = request.pathVariable("id");
        return franchiseUseCase.deleteFranchise(id)
                .doFirst(() -> log.info("Received request to delete franchise with id: {}", id))
                .then(ServerResponse.noContent().build())
                .doOnError(error -> log.error("Error deleting franchise", error));
    }
}

package co.com.bancolombia.api.franchise;

import co.com.bancolombia.api.branch.dto.BranchResponse;
import co.com.bancolombia.api.franchise.dto.FranchiseRequest;
import co.com.bancolombia.api.franchise.dto.FranchiseResponse;
import co.com.bancolombia.api.product.dto.ProductResponse;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.usecase.franchise.FranchiseUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Franchises", description = "Operations related to franchises")
public class FranchiseHandler {

    private final FranchiseUseCase franchiseUseCase;

    @Operation(summary = "Create a new franchise", description = "Creates a new franchise with the provided name")
    @ApiResponse(responseCode = "201", description = "Franchise created successfully")
    public Mono<ServerResponse> createFranchise(ServerRequest request) {
        log.info("Handler: Create franchise");
        return request.bodyToMono(FranchiseRequest.class)
                .flatMap(franchiseRequest -> {
                    Franchise franchise = Franchise.builder()
                            .name(franchiseRequest.getName())
                            .build();
                    return franchiseUseCase.createFranchise(franchise);
                })
                .flatMap(franchise -> ServerResponse.created(URI.create("/franchises/" + franchise.getId()))
                        .bodyValue(mapToResponse(franchise)))
                .doOnError(error -> log.error("Error creating franchise", error));
    }

    @Operation(summary = "Get franchise by ID", description = "Retrieves a franchise by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Franchise retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Franchise not found")
    })
    public Mono<ServerResponse> getFranchiseById(ServerRequest request) {
        String id = request.pathVariable("id");
        log.info("Handler: Get franchise by id: {}", id);
        return franchiseUseCase.getFranchiseById(id)
                .flatMap(franchise -> ServerResponse.ok().bodyValue(mapToResponse(franchise)))
                .doOnError(error -> log.error("Error getting franchise", error));
    }

    @Operation(summary = "Get all franchises", description = "Retrieves all registered franchises")
    @ApiResponse(responseCode = "200", description = "Franchises retrieved successfully")
    public Mono<ServerResponse> getAllFranchises(ServerRequest request) {
        log.info("Handler: Get all franchises");
        return ServerResponse.ok()
                .body(franchiseUseCase.getAllFranchises()/*.map(this::mapToResponse)*/, FranchiseResponse.class)
                .doOnError(error -> log.error("Error getting franchises", error));
    }

    @Operation(summary = "Update franchise name", description = "Updates the name of an existing franchise")
    @ApiResponse(responseCode = "200", description = "Franchise updated successfully")
    public Mono<ServerResponse> updateFranchiseName(ServerRequest request) {
        String id = request.pathVariable("id");
        log.info("Handler: Update franchise name for id: {}", id);
        return request.bodyToMono(FranchiseRequest.class)
                .flatMap(franchiseRequest -> franchiseUseCase.updateFranchiseName(id, franchiseRequest.getName()))
                .flatMap(franchise -> ServerResponse.ok().bodyValue(mapToResponse(franchise)))
                .doOnError(error -> log.error("Error updating franchise", error));
    }

    @Operation(summary = "Delete franchise", description = "Deletes a franchise by its ID")
    @ApiResponse(responseCode = "204", description = "Franchise deleted successfully")
    public Mono<ServerResponse> deleteFranchise(ServerRequest request) {
        String id = request.pathVariable("id");
        log.info("Handler: Delete franchise with id: {}", id);
        return franchiseUseCase.deleteFranchise(id)
                .then(ServerResponse.noContent().build())
                .doOnError(error -> log.error("Error deleting franchise", error));
    }

    private FranchiseResponse mapToResponse(Franchise franchise) {
        return FranchiseResponse.builder()
                .id(franchise.getId())
                .name(franchise.getName())
                .branches(franchise.getBranches() != null ?
                        franchise.getBranches().stream()
                                .map(branch -> new BranchResponse(
                                        branch.getId(),
                                        branch.getName(),
                                        branch.getProducts() != null ?
                                                branch.getProducts().stream()
                                                        .map(product -> new ProductResponse(
                                                                product.getId(),
                                                                product.getName(),
                                                                product.getStock()
                                                        ))
                                                        .toList()
                                        : java.util.Collections.emptyList()
                                ))
                                .toList()
                        : java.util.Collections.emptyList())
                .build();
    }
}

package co.com.bancolombia.api.branch;

import co.com.bancolombia.api.branch.dto.BranchRequest;
import co.com.bancolombia.api.branch.dto.BranchResponse;
import co.com.bancolombia.api.product.dto.ProductResponse;
import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.usecase.franchise.BranchUseCase;
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
public class BranchHandler {
    private final BranchUseCase branchUseCase;

    public Mono<ServerResponse> addBranch(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        log.info("Handler: Add branch to franchise: {}", franchiseId);
        return request.bodyToMono(BranchRequest.class)
            .flatMap(branchRequest -> {
                Branch branch = Branch.builder()
                    .name(branchRequest.getName())
                    .build();
                return branchUseCase.createFranchise(franchiseId, branch);
            })
            .flatMap(franchise -> ServerResponse.created(URI.create("/franchises/" + franchiseId + "/branches"))
                .bodyValue(franchise))
            .doOnError(error -> log.error("Error adding branch", error));
    }

    public Mono<ServerResponse> getBranch(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        String branchId = request.pathVariable("branchId");
        log.info("Handler: Get branch {} from franchise {}", branchId, franchiseId);
        return branchUseCase.getFranchiseById(franchiseId, branchId)
            .flatMap(branch -> ServerResponse.ok().bodyValue(mapToResponse(branch)))
            .doOnError(error -> log.error("Error getting branch", error));
    }

    public Mono<ServerResponse> updateBranchName(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        String branchId = request.pathVariable("branchId");
        log.info("Handler: Update branch name for branch {} in franchise {}", branchId, franchiseId);
        return request.bodyToMono(BranchRequest.class)
            .flatMap(branchRequest -> branchUseCase.updateFranchiseName(franchiseId, branchId, branchRequest.getName()))
            .flatMap(franchise -> ServerResponse.ok().bodyValue(franchise))
            .doOnError(error -> log.error("Error updating branch", error));
    }

    private BranchResponse mapToResponse(Branch branch) {
        return BranchResponse.builder()
            .id(branch.getId())
            .name(branch.getName())
            .products(branch.getProducts() != null ?
                branch.getProducts().stream()
                    .map(product -> new ProductResponse(
                        product.getId(),
                        product.getName(),
                        product.getStock()
                    ))
                    .toList()
                : java.util.Collections.emptyList())
            .build();
    }
}


package co.com.bancolombia.api.branch;

import co.com.bancolombia.api.branch.dto.BranchRequest;
import co.com.bancolombia.api.branch.mapper.BranchMapper;
import co.com.bancolombia.api.franchise.mapper.FranchiseMapper;
import co.com.bancolombia.usecase.franchise.BranchUseCase;
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
public class BranchHandler {
    private final BranchUseCase branchUseCase;
    private final BranchMapper branchMapper;
    private final FranchiseMapper franchiseMapper;

    public Mono<ServerResponse> addBranch(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        return request.bodyToMono(BranchRequest.class)
                .doFirst(() -> log.info("Received request to add branch to franchise: {}", franchiseId))
                .filter(branchRequest -> branchRequest.getName() != null && !branchRequest.getName().isBlank())
                .switchIfEmpty(Mono.error(new ValidationException("Branch name is required")))
                .map(branchMapper::toModel)
                .flatMap(branch -> branchUseCase.createFranchise(franchiseId, branch))
                .flatMap(franchise -> ServerResponse.created(URI.create("/franchises/" + franchiseId + "/branches"))
                        .bodyValue(franchiseMapper.toResponse(franchise)))
                .doOnError(error -> log.error("Error adding branch", error));
    }

    public Mono<ServerResponse> getBranch(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        String branchId = request.pathVariable("branchId");
        return branchUseCase.getFranchiseById(franchiseId, branchId)
                .doFirst(() -> log.info("Received request to get branch {} from franchise {}", branchId, franchiseId))
                .flatMap(branch -> ServerResponse.ok().bodyValue(branchMapper.toResponse(branch)))
                .doOnError(error -> log.error("Error getting branch", error));
    }

    public Mono<ServerResponse> updateBranchName(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        String branchId = request.pathVariable("branchId");
        return request.bodyToMono(BranchRequest.class)
                .doFirst(() -> log.info("Received request to update branch name for branch {} in franchise {}", branchId, franchiseId))
                .filter(branchRequest -> branchRequest.getName() != null && !branchRequest.getName().isBlank())
                .switchIfEmpty(Mono.error(new ValidationException("Branch name is required")))
                .flatMap(branchRequest -> branchUseCase.updateFranchiseName(franchiseId, branchId, branchRequest.getName()))
                .flatMap(franchise -> ServerResponse.ok().bodyValue(franchiseMapper.toResponse(franchise)))
                .doOnError(error -> log.error("Error updating branch", error));
    }
}

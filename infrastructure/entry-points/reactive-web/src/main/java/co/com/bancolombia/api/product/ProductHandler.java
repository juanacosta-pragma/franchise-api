package co.com.bancolombia.api.product;

import co.com.bancolombia.api.franchise.mapper.FranchiseMapper;
import co.com.bancolombia.api.product.dto.ProductRequest;
import co.com.bancolombia.api.product.dto.StockUpdateRequest;
import co.com.bancolombia.api.product.mapper.ProductMapper;
import co.com.bancolombia.usecase.franchise.ProductUseCase;
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
public class ProductHandler {
    private final ProductUseCase productUseCase;
    private final ProductMapper productMapper;
    private final FranchiseMapper franchiseMapper;

    public Mono<ServerResponse> addProduct(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        String branchId = request.pathVariable("branchId");
        return request.bodyToMono(ProductRequest.class)
                .doFirst(() -> log.info("Received request to add product to branch {} in franchise {}", branchId, franchiseId))
                .filter(productRequest -> productRequest.getName() != null && !productRequest.getName().isBlank())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Product name is required")))
                .map(productMapper::toModel)
                .flatMap(product -> productUseCase.create(franchiseId, branchId, product))
                .flatMap(franchise -> ServerResponse.created(
                                URI.create("/franchises/" + franchiseId + "/branches/" + branchId + "/products"))
                        .bodyValue(franchiseMapper.toResponse(franchise)))
                .doOnError(error -> log.error("Error adding product", error));
    }

    public Mono<ServerResponse> getProduct(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        String branchId = request.pathVariable("branchId");
        String productId = request.pathVariable("productId");
        return productUseCase.getProductByIdUseCase(franchiseId, branchId, productId)
                .doFirst(() -> log.info("Received request to get product {} from branch {} in franchise {}", productId, branchId, franchiseId))
                .flatMap(product -> ServerResponse.ok().bodyValue(productMapper.toResponse(product)))
                .doOnError(error -> log.error("Error getting product", error));
    }

    public Mono<ServerResponse> deleteProduct(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        String branchId = request.pathVariable("branchId");
        String productId = request.pathVariable("productId");
        return productUseCase.deleteProductFromBranchUseCase(franchiseId, branchId, productId)
                .doFirst(() -> log.info("Received request to delete product {} from branch {} in franchise {}", productId, branchId, franchiseId))
                .flatMap(franchise -> ServerResponse.ok().bodyValue(franchiseMapper.toResponse(franchise)))
                .doOnError(error -> log.error("Error deleting product", error));
    }

    public Mono<ServerResponse> updateProductStock(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        String branchId = request.pathVariable("branchId");
        String productId = request.pathVariable("productId");
        return request.bodyToMono(StockUpdateRequest.class)
                .doFirst(() -> log.info("Received request to update product stock {} from branch {} in franchise {}", productId, branchId, franchiseId))
                .flatMap(stockUpdateRequest -> productUseCase.updateProductStockUseCase(franchiseId, branchId, productId, stockUpdateRequest.getStock()))
                .flatMap(franchise -> ServerResponse.ok().bodyValue(franchiseMapper.toResponse(franchise)))
                .doOnError(error -> log.error("Error updating product stock", error));
    }

    public Mono<ServerResponse> updateProductName(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        String branchId = request.pathVariable("branchId");
        String productId = request.pathVariable("productId");
        return request.bodyToMono(ProductRequest.class)
                .doFirst(() -> log.info("Received request to update product name {} from branch {} in franchise {}", productId, branchId, franchiseId))
                .flatMap(productRequest -> productUseCase.updateProductNameUseCase(franchiseId, branchId, productId, productRequest.getName()))
                .flatMap(franchise -> ServerResponse.ok().bodyValue(franchiseMapper.toResponse(franchise)))
                .doOnError(error -> log.error("Error updating product name", error));
    }

    public Mono<ServerResponse> getHighestStockProduct(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        String branchId = request.pathVariable("branchId");
        return productUseCase.getHighestStockProductByBranchUseCase(franchiseId, branchId)
                .doFirst(() -> log.info("Received request to get highest stock product from branch {} in franchise {}", branchId, franchiseId))
                .flatMap(product -> ServerResponse.ok().bodyValue(productMapper.toResponse(product)))
                .doOnError(error -> log.error("Error getting highest stock product", error));
    }
}

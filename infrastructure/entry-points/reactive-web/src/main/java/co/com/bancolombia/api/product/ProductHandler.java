package co.com.bancolombia.api.product;

import co.com.bancolombia.api.product.dto.ProductRequest;
import co.com.bancolombia.api.product.dto.ProductResponse;
import co.com.bancolombia.api.product.dto.StockUpdateRequest;
import co.com.bancolombia.model.product.Product;
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

    public Mono<ServerResponse> addProduct(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        String branchId = request.pathVariable("branchId");
        log.info("Handler: Add product to branch {} in franchise {}", branchId, franchiseId);
        return request.bodyToMono(ProductRequest.class)
                .flatMap(productRequest -> {
                    Product product = Product.builder()
                            .name(productRequest.getName())
                            .stock(productRequest.getStock())
                            .build();
                    return productUseCase.create(franchiseId, branchId, product);
                })
                .flatMap(franchise -> ServerResponse.created(
                                URI.create("/franchises/" + franchiseId + "/branches/" + branchId + "/products"))
                        .bodyValue(franchise))
                .doOnError(error -> log.error("Error adding product", error));
    }

    public Mono<ServerResponse> getProduct(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        String branchId = request.pathVariable("branchId");
        String productId = request.pathVariable("productId");
        log.info("Handler: Get product {} from branch {} in franchise {}", productId, branchId, franchiseId);
        return productUseCase.getProductByIdUseCase(franchiseId, branchId, productId)
                .flatMap(product -> ServerResponse.ok().bodyValue(mapToResponse(product)))
                .doOnError(error -> log.error("Error getting product", error));
    }

    public Mono<ServerResponse> deleteProduct(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        String branchId = request.pathVariable("branchId");
        String productId = request.pathVariable("productId");
        log.info("Handler: Delete product {} from branch {} in franchise {}", productId, branchId, franchiseId);
        return productUseCase.deleteProductFromBranchUseCase(franchiseId, branchId, productId)
                .flatMap(franchise -> ServerResponse.ok().bodyValue(franchise))
                .doOnError(error -> log.error("Error deleting product", error));
    }

    public Mono<ServerResponse> updateProductStock(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        String branchId = request.pathVariable("branchId");
        String productId = request.pathVariable("productId");
        log.info("Handler: Update product stock {} from branch {} in franchise {}", productId, branchId, franchiseId);
        return request.bodyToMono(StockUpdateRequest.class)
                .flatMap(stockUpdateRequest -> productUseCase.updateProductStockUseCase(franchiseId, branchId, productId, stockUpdateRequest.getStock()))
                .flatMap(franchise -> ServerResponse.ok().bodyValue(franchise))
                .doOnError(error -> log.error("Error updating product stock", error));
    }

    public Mono<ServerResponse> updateProductName(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        String branchId = request.pathVariable("branchId");
        String productId = request.pathVariable("productId");
        log.info("Handler: Update product name {} from branch {} in franchise {}", productId, branchId, franchiseId);
        return request.bodyToMono(ProductRequest.class)
                .flatMap(productRequest -> productUseCase.updateProductNameUseCase(franchiseId, branchId, productId, productRequest.getName()))
                .flatMap(franchise -> ServerResponse.ok().bodyValue(franchise))
                .doOnError(error -> log.error("Error updating product name", error));
    }

    public Mono<ServerResponse> getHighestStockProduct(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        String branchId = request.pathVariable("branchId");
        log.info("Handler: Get highest stock product from branch {} in franchise {}", branchId, franchiseId);
        return productUseCase.getHighestStockProductByBranchUseCase(franchiseId, branchId)
                .flatMap(product -> ServerResponse.ok().bodyValue(mapToResponse(product)))
                .doOnError(error -> log.error("Error getting highest stock product", error));
    }

    private ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .stock(product.getStock())
                .build();
    }
}



package co.com.bancolombia.usecase.franchise;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.branch.gateways.BranchRepository;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.franchise.gateways.FranchiseRepository;
import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.usecase.franchise.exceptions.BranchNotFoundException;
import co.com.bancolombia.usecase.franchise.exceptions.FranchiseNotFoundException;
import co.com.bancolombia.usecase.franchise.exceptions.ProductNotFoundException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.function.Function;
import java.util.function.UnaryOperator;


@RequiredArgsConstructor
public class ProductUseCase {
    private final BranchRepository branchRepository;
    private final FranchiseRepository franchiseRepository;

    public Flux<Branch> getAllBranches() {
        return branchRepository.getAllBranches();
    }

    public Mono<Franchise> create(String franchiseId, String branchId, Product product) {
        return findFranchise(franchiseId)
                .flatMap(franchise -> findBranch(franchise, branchId)
                        .then(rebuildBranches(franchise, branchId, branch -> appendProduct(branch, product))))
                .flatMap(franchiseRepository::save);
    }

    public Mono<Product> getProductByIdUseCase(String franchiseId, String branchId, String productId) {
        return findFranchise(franchiseId)
                .flatMap(franchise -> findBranch(franchise, branchId))
                .flatMap(branch -> findProduct(branch, productId));
    }

    public Mono<Franchise> deleteProductFromBranchUseCase(String franchiseId, String branchId, String productId) {
        return findFranchise(franchiseId)
                .flatMap(franchise -> findBranch(franchise, branchId)
                        .flatMap(branch -> findProduct(branch, productId))
                        .then(rebuildBranches(franchise, branchId, branch -> removeProduct(branch, productId))))
                .flatMap(franchiseRepository::save);
    }

    public Mono<Franchise> updateProductStockUseCase(String franchiseId, String branchId, String productId, Long newStock) {
        return updateProduct(franchiseId, branchId, productId, p -> p.withStock(newStock));
    }

    public Mono<Franchise> updateProductNameUseCase(String franchiseId, String branchId, String productId, String newName) {
        return updateProduct(franchiseId, branchId, productId, p -> p.withName(newName));
    }

    public Mono<Product> getHighestStockProductByBranchUseCase(String franchiseId, String branchId) {
        return findFranchise(franchiseId)
                .flatMap(franchise -> findBranch(franchise, branchId))
                .flatMapMany(branch -> Flux.fromIterable(branch.getProducts()))
                .reduce((a, b) -> a.getStock() >= b.getStock() ? a : b)
                .switchIfEmpty(Mono.error(new ProductNotFoundException("No products found in branch")));
    }

    // --- private reactive helpers ---

    private Mono<Franchise> findFranchise(String franchiseId) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new FranchiseNotFoundException("Franchise not found with id: " + franchiseId)));
    }

    private Mono<Branch> findBranch(Franchise franchise, String branchId) {
        return Flux.fromIterable(franchise.getBranches())
                .filter(b -> branchId.equals(b.getId()))
                .next()
                .switchIfEmpty(Mono.error(new BranchNotFoundException("Branch not found with id: " + branchId)));
    }

    private Mono<Product> findProduct(Branch branch, String productId) {
        return Flux.fromIterable(branch.getProducts())
                .filter(p -> productId.equals(p.getId()))
                .next()
                .switchIfEmpty(Mono.error(new ProductNotFoundException("Product not found with id: " + productId)));
    }

    private Mono<Franchise> updateProduct(String franchiseId, String branchId, String productId, UnaryOperator<Product> transform) {
        return findFranchise(franchiseId)
                .flatMap(franchise -> findBranch(franchise, branchId)
                        .flatMap(branch -> findProduct(branch, productId))
                        .then(rebuildBranches(franchise, branchId,
                                branch -> updateProductInBranch(branch, productId, transform))))
                .flatMap(franchiseRepository::save);
    }

    private Mono<Franchise> rebuildBranches(Franchise franchise, String branchId, Function<Branch, Mono<Branch>> branchTransform) {
        return Flux.fromIterable(franchise.getBranches())
                .concatMap(b -> branchId.equals(b.getId()) ? branchTransform.apply(b) : Mono.just(b))
                .collectList()
                .map(franchise::withBranches);
    }

    private Mono<Branch> appendProduct(Branch branch, Product product) {
        Product newProduct = product.withId(UUID.randomUUID().toString());
        return Flux.fromIterable(branch.getProducts())
                .concatWithValues(newProduct)
                .collectList()
                .map(branch::withProducts);
    }

    private Mono<Branch> removeProduct(Branch branch, String productId) {
        return Flux.fromIterable(branch.getProducts())
                .filter(p -> !productId.equals(p.getId()))
                .collectList()
                .map(branch::withProducts);
    }

    private Mono<Branch> updateProductInBranch(Branch branch, String productId, UnaryOperator<Product> transform) {
        return Flux.fromIterable(branch.getProducts())
                .map(p -> productId.equals(p.getId()) ? transform.apply(p) : p)
                .collectList()
                .map(branch::withProducts);
    }
}
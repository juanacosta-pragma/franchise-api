package co.com.bancolombia.usecase.franchise;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.branch.gateways.BranchRepository;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.franchise.gateways.FranchiseRepository;
import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.usecase.franchise.exceptions.BranchNotFoundException;
import co.com.bancolombia.usecase.franchise.exceptions.FranchiseNotFoundException;
import co.com.bancolombia.usecase.franchise.exceptions.ProductNotFoundException;
import co.com.bancolombia.usecase.franchise.exceptions.ValidationException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.UUID;


@RequiredArgsConstructor
public class ProductUseCase {
    private final BranchRepository branchRepository;
    private final FranchiseRepository franchiseRepository;

    public Flux<Branch> getAllBranches() {
        return branchRepository.getAllBranches();
    }

    public Mono<Franchise> create(String franchiseId, String branchId, Product product) {
        if (product.getName() == null || product.getName().isBlank()) {
            return Mono.error(new ValidationException("Product name is required"));
        }
        if (product.getStock() == null || product.getStock() < 0) {
            return Mono.error(new ValidationException("Product stock must be non-negative"));
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

                    product.setId(UUID.randomUUID().toString());
                    if (branch.getProducts() == null) {
                        branch.setProducts(new ArrayList<>());
                    }
                    branch.getProducts().add(product);
                    return franchiseRepository.save(franchise);
                });
    }

    public Mono<Product> getProductByIdUseCase(String franchiseId, String branchId, String productId) {
        return (Mono<Product>) franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new FranchiseNotFoundException("Franchise not found with id: " + franchiseId)))
                .flatMap(franchise -> {
                    if (franchise.getBranches() == null || franchise.getBranches().isEmpty()) {
                        return Mono.error(new BranchNotFoundException("Branch not found with id: " + branchId));
                    }

                    return franchise.getBranches().stream()
                            .filter(b -> b.getId().equals(branchId))
                            .findFirst()
                            .map(branch -> {
                                if (branch.getProducts() == null || branch.getProducts().isEmpty()) {
                                    return Mono.error(new ProductNotFoundException("Product not found with id: " + productId));
                                }

                                return branch.getProducts().stream()
                                        .filter(p -> p.getId().equals(productId))
                                        .findFirst()
                                        .map(Mono::just)
                                        .orElseGet(() -> Mono.error(new ProductNotFoundException("Product not found with id: " + productId)));
                            })
                            .orElseGet(() -> Mono.error(new BranchNotFoundException("Branch not found with id: " + branchId)));
                });
    }

    public Mono<Franchise> deleteProductFromBranchUseCase(String franchiseId, String branchId, String productId) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new FranchiseNotFoundException("Franchise not found with id: " + franchiseId)))
                .flatMap(franchise -> {
                    if (franchise.getBranches() == null || franchise.getBranches().isEmpty()) {
                        return Mono.error(new BranchNotFoundException("Branch not found with id: " + branchId));
                    }

                    var branch = franchise.getBranches().stream()
                            .filter(b -> b.getId().equals(branchId))
                            .findFirst()
                            .orElseThrow(() -> new BranchNotFoundException("Branch not found with id: " + branchId));

                    if (branch.getProducts() == null || branch.getProducts().isEmpty()) {
                        return Mono.error(new ProductNotFoundException("Product not found with id: " + productId));
                    }

                    boolean removed = branch.getProducts().removeIf(p -> p.getId().equals(productId));
                    if (!removed) {
                        return Mono.error(new ProductNotFoundException("Product not found with id: " + productId));
                    }

                    return franchiseRepository.save(franchise);
                });
    }

    public Mono<Franchise> updateProductStockUseCase(String franchiseId, String branchId, String productId, Long newStock) {
        if (newStock == null || newStock < 0) {
            return Mono.error(new ValidationException("Product stock must be non-negative"));
        }

        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new FranchiseNotFoundException("Franchise not found with id: " + franchiseId)))
                .flatMap(franchise -> {
                    if (franchise.getBranches() == null || franchise.getBranches().isEmpty()) {
                        return Mono.error(new BranchNotFoundException("Branch not found with id: " + branchId));
                    }

                    var branch = franchise.getBranches().stream()
                            .filter(b -> b.getId().equals(branchId))
                            .findFirst()
                            .orElseThrow(() -> new BranchNotFoundException("Branch not found with id: " + branchId));

                    if (branch.getProducts() == null || branch.getProducts().isEmpty()) {
                        return Mono.error(new ProductNotFoundException("Product not found with id: " + productId));
                    }

                    Product product = branch.getProducts().stream()
                            .filter(p -> p.getId().equals(productId))
                            .findFirst()
                            .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));

                    product.setStock(newStock);
                    return franchiseRepository.save(franchise);
                });
    }

    public Mono<Franchise> updateProductNameUseCase(String franchiseId, String branchId, String productId, String newName) {
        if (newName == null || newName.isBlank()) {
            return Mono.error(new ValidationException("Product name is required"));
        }

        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new FranchiseNotFoundException("Franchise not found with id: " + franchiseId)))
                .flatMap(franchise -> {
                    if (franchise.getBranches() == null || franchise.getBranches().isEmpty()) {
                        return Mono.error(new BranchNotFoundException("Branch not found with id: " + branchId));
                    }

                    var branch = franchise.getBranches().stream()
                            .filter(b -> b.getId().equals(branchId))
                            .findFirst()
                            .orElseThrow(() -> new BranchNotFoundException("Branch not found with id: " + branchId));

                    if (branch.getProducts() == null || branch.getProducts().isEmpty()) {
                        return Mono.error(new ProductNotFoundException("Product not found with id: " + productId));
                    }

                    Product product = branch.getProducts().stream()
                            .filter(p -> p.getId().equals(productId))
                            .findFirst()
                            .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));

                    product.setName(newName);
                    return franchiseRepository.save(franchise);
                });
    }

    public Mono<Product> getHighestStockProductByBranchUseCase (String franchiseId, String branchId) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new FranchiseNotFoundException("Franchise not found with id: " + franchiseId)))
                .flatMap(franchise -> {
                    if (franchise.getBranches() == null || franchise.getBranches().isEmpty()) {
                        return Mono.error(new BranchNotFoundException("Branch not found with id: " + branchId));
                    }

                    var branch = franchise.getBranches().stream()
                            .filter(b -> b.getId().equals(branchId))
                            .findFirst()
                            .orElseThrow(() -> new BranchNotFoundException("Branch not found with id: " + branchId));

                    if (branch.getProducts() == null || branch.getProducts().isEmpty()) {
                        return Mono.error(new ProductNotFoundException("No products found in branch"));
                    }

                    Product highestStockProduct = branch.getProducts().stream()
                            .max(Comparator.comparing(Product::getStock))
                            .orElseThrow(() -> new ProductNotFoundException("No products found in branch"));

                    return Mono.just(highestStockProduct);
                });
    }
}
package co.com.bancolombia.usecase.franchise;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.branch.gateways.BranchRepository;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.franchise.gateways.FranchiseRepository;
import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.usecase.franchise.exceptions.BranchNotFoundException;
import co.com.bancolombia.usecase.franchise.exceptions.FranchiseNotFoundException;
import co.com.bancolombia.usecase.franchise.exceptions.ProductNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductUseCaseTest {

    @Mock
    private BranchRepository branchRepository;

    @Mock
    private FranchiseRepository franchiseRepository;

    @InjectMocks
    private ProductUseCase useCase;

    private Product p1;
    private Product p2;
    private Branch branchWithProducts;
    private Branch otherBranch;
    private Franchise franchise;

    @BeforeEach
    void setUp() {
        p1 = Product.builder().id("P1").name("Apple").stock(10L).build();
        p2 = Product.builder().id("P2").name("Banana").stock(20L).build();
        branchWithProducts = Branch.builder().id("B1").name("Centro")
                .products(List.of(p1, p2)).build();
        otherBranch = Branch.builder().id("B2").name("Norte").build();
        franchise = Franchise.builder().id("F1").name("Pizza")
                .branches(List.of(branchWithProducts, otherBranch)).build();
    }

    private void mockFindAndSave() {
        when(franchiseRepository.findById("F1")).thenReturn(Mono.just(franchise));
        when(franchiseRepository.save(any(Franchise.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));
    }

    // -------- getAllBranches --------

    @Test
    void getAllBranches_delegates() {
        when(branchRepository.getAllBranches()).thenReturn(Flux.just(branchWithProducts));

        StepVerifier.create(useCase.getAllBranches())
                .expectNext(branchWithProducts)
                .verifyComplete();
    }

    // -------- create --------

    @Test
    void create_appendsProductWithGeneratedId() {
        mockFindAndSave();
        Product newProduct = Product.builder().name("Cherry").stock(5L).build();

        StepVerifier.create(useCase.create("F1", "B1", newProduct))
                .assertNext(saved -> {
                    Branch updatedB1 = saved.getBranches().stream()
                            .filter(b -> "B1".equals(b.getId())).findFirst().orElseThrow();
                    assertThat(updatedB1.getProducts()).hasSize(3);
                    Product appended = updatedB1.getProducts().get(2);
                    assertThat(appended.getId()).isNotNull();
                    assertThat(appended.getName()).isEqualTo("Cherry");
                })
                .verifyComplete();
    }

    @Test
    void create_branchNotFound_emitsError() {
        when(franchiseRepository.findById("F1")).thenReturn(Mono.just(franchise));

        StepVerifier.create(useCase.create("F1", "MISSING",
                        Product.builder().name("X").stock(1L).build()))
                .expectError(BranchNotFoundException.class)
                .verify();
    }

    @Test
    void create_franchiseNotFound_emitsError() {
        when(franchiseRepository.findById("F1")).thenReturn(Mono.empty());

        StepVerifier.create(useCase.create("F1", "B1",
                        Product.builder().name("X").stock(1L).build()))
                .expectError(FranchiseNotFoundException.class)
                .verify();
    }

    // -------- getProductByIdUseCase --------

    @Test
    void getProductByIdUseCase_existing_returns() {
        when(franchiseRepository.findById("F1")).thenReturn(Mono.just(franchise));

        StepVerifier.create(useCase.getProductByIdUseCase("F1", "B1", "P1"))
                .expectNext(p1)
                .verifyComplete();
    }

    @Test
    void getProductByIdUseCase_productNotFound_emitsError() {
        when(franchiseRepository.findById("F1")).thenReturn(Mono.just(franchise));

        StepVerifier.create(useCase.getProductByIdUseCase("F1", "B1", "MISSING"))
                .expectError(ProductNotFoundException.class)
                .verify();
    }

    @Test
    void getProductByIdUseCase_branchNotFound_emitsError() {
        when(franchiseRepository.findById("F1")).thenReturn(Mono.just(franchise));

        StepVerifier.create(useCase.getProductByIdUseCase("F1", "MISSING", "P1"))
                .expectError(BranchNotFoundException.class)
                .verify();
    }

    @Test
    void getProductByIdUseCase_franchiseNotFound_emitsError() {
        when(franchiseRepository.findById("F1")).thenReturn(Mono.empty());

        StepVerifier.create(useCase.getProductByIdUseCase("F1", "B1", "P1"))
                .expectError(FranchiseNotFoundException.class)
                .verify();
    }

    // -------- deleteProductFromBranchUseCase --------

    @Test
    void deleteProduct_existing_removesAndSaves() {
        mockFindAndSave();

        StepVerifier.create(useCase.deleteProductFromBranchUseCase("F1", "B1", "P1"))
                .assertNext(saved -> {
                    Branch updated = saved.getBranches().stream()
                            .filter(b -> "B1".equals(b.getId())).findFirst().orElseThrow();
                    assertThat(updated.getProducts()).hasSize(1);
                    assertThat(updated.getProducts().get(0).getId()).isEqualTo("P2");
                })
                .verifyComplete();
    }

    @Test
    void deleteProduct_productNotFound_emitsError() {
        when(franchiseRepository.findById("F1")).thenReturn(Mono.just(franchise));

        StepVerifier.create(useCase.deleteProductFromBranchUseCase("F1", "B1", "MISSING"))
                .expectError(ProductNotFoundException.class)
                .verify();
    }

    @Test
    void deleteProduct_branchNotFound_emitsError() {
        when(franchiseRepository.findById("F1")).thenReturn(Mono.just(franchise));

        StepVerifier.create(useCase.deleteProductFromBranchUseCase("F1", "MISSING", "P1"))
                .expectError(BranchNotFoundException.class)
                .verify();
    }

    @Test
    void deleteProduct_franchiseNotFound_emitsError() {
        when(franchiseRepository.findById("F1")).thenReturn(Mono.empty());

        StepVerifier.create(useCase.deleteProductFromBranchUseCase("F1", "B1", "P1"))
                .expectError(FranchiseNotFoundException.class)
                .verify();
    }

    // -------- updateProductStockUseCase --------

    @Test
    void updateProductStock_existing_updates() {
        mockFindAndSave();

        StepVerifier.create(useCase.updateProductStockUseCase("F1", "B1", "P1", 99L))
                .assertNext(saved -> {
                    Product updated = saved.getBranches().get(0).getProducts().stream()
                            .filter(p -> "P1".equals(p.getId())).findFirst().orElseThrow();
                    assertThat(updated.getStock()).isEqualTo(99L);
                })
                .verifyComplete();
    }

    @Test
    void updateProductStock_productNotFound_emitsError() {
        when(franchiseRepository.findById("F1")).thenReturn(Mono.just(franchise));

        StepVerifier.create(useCase.updateProductStockUseCase("F1", "B1", "MISSING", 99L))
                .expectError(ProductNotFoundException.class)
                .verify();
    }

    // -------- updateProductNameUseCase --------

    @Test
    void updateProductName_existing_updates() {
        mockFindAndSave();

        StepVerifier.create(useCase.updateProductNameUseCase("F1", "B1", "P1", "Renamed"))
                .assertNext(saved -> {
                    Product updated = saved.getBranches().get(0).getProducts().stream()
                            .filter(p -> "P1".equals(p.getId())).findFirst().orElseThrow();
                    assertThat(updated.getName()).isEqualTo("Renamed");
                })
                .verifyComplete();
    }

    @Test
    void updateProductName_productNotFound_emitsError() {
        when(franchiseRepository.findById("F1")).thenReturn(Mono.just(franchise));

        StepVerifier.create(useCase.updateProductNameUseCase("F1", "B1", "MISSING", "X"))
                .expectError(ProductNotFoundException.class)
                .verify();
    }

    @Test
    void updateProductName_branchNotFound_emitsError() {
        when(franchiseRepository.findById("F1")).thenReturn(Mono.just(franchise));

        StepVerifier.create(useCase.updateProductNameUseCase("F1", "MISSING", "P1", "X"))
                .expectError(BranchNotFoundException.class)
                .verify();
    }

    // -------- getHighestStockProductByBranchUseCase --------

    @Test
    void getHighestStockProduct_returnsMax() {
        when(franchiseRepository.findById("F1")).thenReturn(Mono.just(franchise));

        StepVerifier.create(useCase.getHighestStockProductByBranchUseCase("F1", "B1"))
                .expectNext(p2)
                .verifyComplete();
    }

    @Test
    void getHighestStockProduct_emptyBranch_emitsProductNotFound() {
        when(franchiseRepository.findById("F1")).thenReturn(Mono.just(franchise));

        StepVerifier.create(useCase.getHighestStockProductByBranchUseCase("F1", "B2"))
                .expectError(ProductNotFoundException.class)
                .verify();
    }

    @Test
    void getHighestStockProduct_branchNotFound_emitsError() {
        when(franchiseRepository.findById("F1")).thenReturn(Mono.just(franchise));

        StepVerifier.create(useCase.getHighestStockProductByBranchUseCase("F1", "MISSING"))
                .expectError(BranchNotFoundException.class)
                .verify();
    }

    @Test
    void getHighestStockProduct_franchiseNotFound_emitsError() {
        when(franchiseRepository.findById("F1")).thenReturn(Mono.empty());

        StepVerifier.create(useCase.getHighestStockProductByBranchUseCase("F1", "B1"))
                .expectError(FranchiseNotFoundException.class)
                .verify();
    }

    @Test
    void create_capturesSavedFranchise() {
        mockFindAndSave();
        useCase.create("F1", "B1", Product.builder().name("X").stock(1L).build()).block();

        ArgumentCaptor<Franchise> captor = ArgumentCaptor.forClass(Franchise.class);
        verify(franchiseRepository).save(captor.capture());
        assertThat(captor.getValue().getBranches().get(0).getProducts()).hasSize(3);
    }
}


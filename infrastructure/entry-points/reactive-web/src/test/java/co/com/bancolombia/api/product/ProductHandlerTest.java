package co.com.bancolombia.api.product;

import co.com.bancolombia.api.franchise.dto.FranchiseResponse;
import co.com.bancolombia.api.franchise.mapper.FranchiseMapper;
import co.com.bancolombia.api.product.dto.ProductRequest;
import co.com.bancolombia.api.product.dto.ProductResponse;
import co.com.bancolombia.api.product.dto.StockUpdateRequest;
import co.com.bancolombia.api.product.mapper.ProductMapper;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.usecase.franchise.ProductUseCase;
import co.com.bancolombia.usecase.franchise.exceptions.ProductNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductHandlerTest {

    @Mock
    private ProductUseCase useCase;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private FranchiseMapper franchiseMapper;

    @InjectMocks
    private ProductHandler handler;

    private Product product;
    private ProductResponse productResponse;
    private Franchise franchise;
    private FranchiseResponse franchiseResponse;

    @BeforeEach
    void setUp() {
        product = Product.builder().id("P1").name("Apple").stock(10L).build();
        productResponse = ProductResponse.builder().id("P1").name("Apple").stock(10L).build();
        franchise = Franchise.builder().id("F1").name("Pizza").build();
        franchiseResponse = FranchiseResponse.builder().id("F1").name("Pizza").build();
    }

    @Test
    void addProduct_validRequest_returnsCreated() {
        ProductRequest req = ProductRequest.builder().name("Apple").stock(10L).build();
        when(productMapper.toModel(any(ProductRequest.class))).thenReturn(product);
        when(useCase.create(anyString(), anyString(), any(Product.class)))
                .thenReturn(Mono.just(franchise));
        when(franchiseMapper.toResponse(franchise)).thenReturn(franchiseResponse);

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("franchiseId", "F1")
                .pathVariable("branchId", "B1")
                .body(Mono.just(req));

        StepVerifier.create(handler.addProduct(request))
                .assertNext(r -> assertThat(r.statusCode()).isEqualTo(HttpStatus.CREATED))
                .verifyComplete();
    }

    @Test
    void addProduct_blankName_emitsIllegalArgument() {
        ProductRequest req = ProductRequest.builder().name("  ").stock(1L).build();
        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("franchiseId", "F1")
                .pathVariable("branchId", "B1")
                .body(Mono.just(req));

        StepVerifier.create(handler.addProduct(request))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void getProduct_existing_returnsOk() {
        when(useCase.getProductByIdUseCase("F1", "B1", "P1")).thenReturn(Mono.just(product));
        when(productMapper.toResponse(product)).thenReturn(productResponse);

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("franchiseId", "F1")
                .pathVariable("branchId", "B1")
                .pathVariable("productId", "P1")
                .build();

        StepVerifier.create(handler.getProduct(request))
                .assertNext(r -> assertThat(r.statusCode()).isEqualTo(HttpStatus.OK))
                .verifyComplete();
    }

    @Test
    void getProduct_notFound_propagatesError() {
        when(useCase.getProductByIdUseCase("F1", "B1", "P1"))
                .thenReturn(Mono.error(new ProductNotFoundException("missing")));

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("franchiseId", "F1")
                .pathVariable("branchId", "B1")
                .pathVariable("productId", "P1")
                .build();

        StepVerifier.create(handler.getProduct(request))
                .expectError(ProductNotFoundException.class)
                .verify();
    }

    @Test
    void deleteProduct_returnsOk() {
        when(useCase.deleteProductFromBranchUseCase(anyString(), anyString(), anyString()))
                .thenReturn(Mono.just(franchise));
        when(franchiseMapper.toResponse(franchise)).thenReturn(franchiseResponse);

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("franchiseId", "F1")
                .pathVariable("branchId", "B1")
                .pathVariable("productId", "P1")
                .build();

        StepVerifier.create(handler.deleteProduct(request))
                .assertNext(r -> assertThat(r.statusCode()).isEqualTo(HttpStatus.OK))
                .verifyComplete();
    }

    @Test
    void updateProductStock_returnsOk() {
        StockUpdateRequest stockReq = StockUpdateRequest.builder().stock(99L).build();
        when(useCase.updateProductStockUseCase(anyString(), anyString(), anyString(), anyLong()))
                .thenReturn(Mono.just(franchise));
        when(franchiseMapper.toResponse(franchise)).thenReturn(franchiseResponse);

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("franchiseId", "F1")
                .pathVariable("branchId", "B1")
                .pathVariable("productId", "P1")
                .body(Mono.just(stockReq));

        StepVerifier.create(handler.updateProductStock(request))
                .assertNext(r -> assertThat(r.statusCode()).isEqualTo(HttpStatus.OK))
                .verifyComplete();
    }

    @Test
    void updateProductName_returnsOk() {
        ProductRequest nameReq = ProductRequest.builder().name("New").build();
        when(useCase.updateProductNameUseCase(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Mono.just(franchise));
        when(franchiseMapper.toResponse(franchise)).thenReturn(franchiseResponse);

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("franchiseId", "F1")
                .pathVariable("branchId", "B1")
                .pathVariable("productId", "P1")
                .body(Mono.just(nameReq));

        StepVerifier.create(handler.updateProductName(request))
                .assertNext(r -> assertThat(r.statusCode()).isEqualTo(HttpStatus.OK))
                .verifyComplete();
    }

    @Test
    void getHighestStockProduct_returnsOk() {
        when(useCase.getHighestStockProductByBranchUseCase("F1", "B1")).thenReturn(Mono.just(product));
        when(productMapper.toResponse(product)).thenReturn(productResponse);

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("franchiseId", "F1")
                .pathVariable("branchId", "B1")
                .build();

        StepVerifier.create(handler.getHighestStockProduct(request))
                .assertNext(r -> assertThat(r.statusCode()).isEqualTo(HttpStatus.OK))
                .verifyComplete();
    }

    @Test
    void getHighestStockProduct_noProducts_propagatesError() {
        when(useCase.getHighestStockProductByBranchUseCase("F1", "B1"))
                .thenReturn(Mono.error(new ProductNotFoundException("none")));

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("franchiseId", "F1")
                .pathVariable("branchId", "B1")
                .build();

        StepVerifier.create(handler.getHighestStockProduct(request))
                .expectError(ProductNotFoundException.class)
                .verify();
    }
}


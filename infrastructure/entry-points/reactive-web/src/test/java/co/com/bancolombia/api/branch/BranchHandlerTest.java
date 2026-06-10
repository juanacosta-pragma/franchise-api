package co.com.bancolombia.api.branch;

import co.com.bancolombia.api.branch.dto.BranchRequest;
import co.com.bancolombia.api.branch.dto.BranchResponse;
import co.com.bancolombia.api.branch.mapper.BranchMapper;
import co.com.bancolombia.api.franchise.dto.FranchiseResponse;
import co.com.bancolombia.api.franchise.mapper.FranchiseMapper;
import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.usecase.franchise.BranchUseCase;
import co.com.bancolombia.usecase.franchise.exceptions.BranchNotFoundException;
import co.com.bancolombia.usecase.franchise.exceptions.ValidationException;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BranchHandlerTest {

    @Mock
    private BranchUseCase useCase;

    @Mock
    private BranchMapper branchMapper;

    @Mock
    private FranchiseMapper franchiseMapper;

    @InjectMocks
    private BranchHandler handler;

    private Branch branch;
    private Franchise franchise;
    private FranchiseResponse franchiseResponse;

    @BeforeEach
    void setUp() {
        branch = Branch.builder().id("B1").name("Centro").build();
        franchise = Franchise.builder().id("F1").name("Pizza").build();
        franchiseResponse = FranchiseResponse.builder().id("F1").name("Pizza").build();
    }

    @Test
    void addBranch_validRequest_returnsCreated() {
        BranchRequest req = BranchRequest.builder().name("Centro").build();
        when(branchMapper.toModel(any(BranchRequest.class))).thenReturn(branch);
        when(useCase.createFranchise(anyString(), any(Branch.class))).thenReturn(Mono.just(franchise));
        when(franchiseMapper.toResponse(any(Franchise.class))).thenReturn(franchiseResponse);

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("franchiseId", "F1")
                .body(Mono.just(req));

        StepVerifier.create(handler.addBranch(request))
                .assertNext(r -> assertThat(r.statusCode()).isEqualTo(HttpStatus.CREATED))
                .verifyComplete();
    }

    @Test
    void addBranch_blankName_emitsValidationException() {
        BranchRequest req = BranchRequest.builder().name("").build();
        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("franchiseId", "F1")
                .body(Mono.just(req));

        StepVerifier.create(handler.addBranch(request))
                .expectError(ValidationException.class)
                .verify();
    }

    @Test
    void getBranch_existing_returnsOk() {
        when(useCase.getFranchiseById("F1", "B1")).thenReturn(Mono.just(branch));
        when(branchMapper.toResponse(branch))
                .thenReturn(BranchResponse.builder().id("B1").name("Centro").build());

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("franchiseId", "F1")
                .pathVariable("branchId", "B1")
                .build();

        StepVerifier.create(handler.getBranch(request))
                .assertNext(r -> assertThat(r.statusCode()).isEqualTo(HttpStatus.OK))
                .verifyComplete();
    }

    @Test
    void getBranch_notFound_propagatesError() {
        when(useCase.getFranchiseById("F1", "B1"))
                .thenReturn(Mono.error(new BranchNotFoundException("missing")));

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("franchiseId", "F1")
                .pathVariable("branchId", "B1")
                .build();

        StepVerifier.create(handler.getBranch(request))
                .expectError(BranchNotFoundException.class)
                .verify();
    }

    @Test
    void updateBranchName_validRequest_returnsOk() {
        BranchRequest req = BranchRequest.builder().name("Renamed").build();
        when(useCase.updateFranchiseName(anyString(), anyString(), anyString()))
                .thenReturn(Mono.just(franchise));
        when(franchiseMapper.toResponse(franchise)).thenReturn(franchiseResponse);

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("franchiseId", "F1")
                .pathVariable("branchId", "B1")
                .body(Mono.just(req));

        StepVerifier.create(handler.updateBranchName(request))
                .assertNext(r -> assertThat(r.statusCode()).isEqualTo(HttpStatus.OK))
                .verifyComplete();
    }

    @Test
    void updateBranchName_blankName_emitsValidationException() {
        BranchRequest req = BranchRequest.builder().name("   ").build();
        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("franchiseId", "F1")
                .pathVariable("branchId", "B1")
                .body(Mono.just(req));

        StepVerifier.create(handler.updateBranchName(request))
                .expectError(ValidationException.class)
                .verify();
    }
}


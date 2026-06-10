package co.com.bancolombia.api.franchise;

import co.com.bancolombia.api.franchise.dto.FranchiseRequest;
import co.com.bancolombia.api.franchise.dto.FranchiseResponse;
import co.com.bancolombia.api.franchise.mapper.FranchiseMapper;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.usecase.franchise.FranchiseUseCase;
import co.com.bancolombia.usecase.franchise.exceptions.FranchiseNotFoundException;
import co.com.bancolombia.usecase.franchise.exceptions.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.EntityResponse;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FranchiseHandlerTest {

    @Mock
    private FranchiseUseCase useCase;

    @Mock
    private FranchiseMapper mapper;

    @InjectMocks
    private FranchiseHandler handler;

    private Franchise franchise;
    private FranchiseResponse response;

    @BeforeEach
    void setUp() {
        franchise = Franchise.builder().id("1").name("Pizza").build();
        response = FranchiseResponse.builder().id("1").name("Pizza").build();
    }

    @Test
    void createFranchise_validRequest_returnsCreated() {
        FranchiseRequest req = FranchiseRequest.builder().name("Pizza").build();
        when(mapper.toModel(any(FranchiseRequest.class))).thenReturn(franchise);
        when(useCase.createFranchise(any())).thenReturn(Mono.just(franchise));
        when(mapper.toResponse(any())).thenReturn(response);

        MockServerRequest request = MockServerRequest.builder().body(Mono.just(req));

        StepVerifier.create(handler.createFranchise(request))
                .assertNext(r -> {
                    assertThat(r.statusCode()).isEqualTo(HttpStatus.CREATED);
                    assertThat(((EntityResponse<?>) r).entity()).isEqualTo(response);
                })
                .verifyComplete();
    }

    @Test
    void createFranchise_blankName_emitsValidationException() {
        FranchiseRequest req = FranchiseRequest.builder().name("  ").build();
        MockServerRequest request = MockServerRequest.builder().body(Mono.just(req));

        StepVerifier.create(handler.createFranchise(request))
                .expectError(ValidationException.class)
                .verify();
    }

    @Test
    void createFranchise_nullName_emitsValidationException() {
        FranchiseRequest req = FranchiseRequest.builder().name(null).build();
        MockServerRequest request = MockServerRequest.builder().body(Mono.just(req));

        StepVerifier.create(handler.createFranchise(request))
                .expectError(ValidationException.class)
                .verify();
    }

    @Test
    void getFranchiseById_existing_returnsOk() {
        when(useCase.getFranchiseById("1")).thenReturn(Mono.just(franchise));
        when(mapper.toResponse(franchise)).thenReturn(response);

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("id", "1").build();

        StepVerifier.create(handler.getFranchiseById(request))
                .assertNext(r -> {
                    assertThat(r.statusCode()).isEqualTo(HttpStatus.OK);
                    assertThat(((EntityResponse<?>) r).entity()).isEqualTo(response);
                })
                .verifyComplete();
    }

    @Test
    void getFranchiseById_notFound_propagatesError() {
        when(useCase.getFranchiseById("X"))
                .thenReturn(Mono.error(new FranchiseNotFoundException("not found")));

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("id", "X").build();

        StepVerifier.create(handler.getFranchiseById(request))
                .expectError(FranchiseNotFoundException.class)
                .verify();
    }

    @Test
    void getAllFranchises_returnsOkFlux() {
        when(useCase.getAllFranchises()).thenReturn(Flux.just(franchise));

        MockServerRequest request = MockServerRequest.builder().build();

        StepVerifier.create(handler.getAllFranchises(request))
                .assertNext(r -> assertThat(r.statusCode()).isEqualTo(HttpStatus.OK))
                .verifyComplete();
    }

    @Test
    void updateFranchiseName_returnsOk() {
        FranchiseRequest req = FranchiseRequest.builder().name("Renamed").build();
        when(useCase.updateFranchiseName(anyString(), anyString())).thenReturn(Mono.just(franchise));
        when(mapper.toResponse(franchise)).thenReturn(response);

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("id", "1")
                .body(Mono.just(req));

        StepVerifier.create(handler.updateFranchiseName(request))
                .assertNext(r -> assertThat(r.statusCode()).isEqualTo(HttpStatus.OK))
                .verifyComplete();
    }

    @Test
    void deleteFranchise_returnsNoContent() {
        when(useCase.deleteFranchise("1")).thenReturn(Mono.empty());

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("id", "1").build();

        StepVerifier.create(handler.deleteFranchise(request))
                .assertNext(r -> assertThat(r.statusCode()).isEqualTo(HttpStatus.NO_CONTENT))
                .verifyComplete();
    }

    @Test
    void deleteFranchise_useCaseError_propagates() {
        when(useCase.deleteFranchise("1")).thenReturn(Mono.error(new RuntimeException("boom")));

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("id", "1").build();

        StepVerifier.create(handler.deleteFranchise(request))
                .expectErrorMessage("boom")
                .verify();
    }

    @SuppressWarnings("unused")
    private ServerResponse cast(ServerResponse r) { return r; } // keep import used
}


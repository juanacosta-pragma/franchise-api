package co.com.bancolombia.usecase.franchise;

import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.franchise.gateways.FranchiseRepository;
import co.com.bancolombia.usecase.franchise.exceptions.FranchiseNotFoundException;
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
class FranchiseUseCaseTest {

    @Mock
    private FranchiseRepository franchiseRepository;

    @InjectMocks
    private FranchiseUseCase useCase;

    @Test
    void getAllFranchises_returnsRepositoryFlux() {
        Franchise f1 = Franchise.builder().id("1").name("A").build();
        Franchise f2 = Franchise.builder().id("2").name("B").build();
        when(franchiseRepository.getAllFranchises()).thenReturn(Flux.just(f1, f2));

        StepVerifier.create(useCase.getAllFranchises())
                .expectNext(f1, f2)
                .verifyComplete();

        verify(franchiseRepository).getAllFranchises();
    }

    @Test
    void getAllFranchises_empty_completesEmpty() {
        when(franchiseRepository.getAllFranchises()).thenReturn(Flux.empty());

        StepVerifier.create(useCase.getAllFranchises())
                .verifyComplete();
    }

    @Test
    void createFranchise_clearsBranches_andSaves() {
        Franchise input = Franchise.builder().name("New").build();
        Franchise saved = Franchise.builder().id("1").name("New").branches(List.of()).build();
        when(franchiseRepository.save(any(Franchise.class))).thenReturn(Mono.just(saved));

        StepVerifier.create(useCase.createFranchise(input))
                .expectNext(saved)
                .verifyComplete();

        ArgumentCaptor<Franchise> captor = ArgumentCaptor.forClass(Franchise.class);
        verify(franchiseRepository).save(captor.capture());
        assertThat(captor.getValue().getBranches()).isEmpty();
        assertThat(captor.getValue().getName()).isEqualTo("New");
    }

    @Test
    void createFranchise_propagatesRepositoryError() {
        Franchise input = Franchise.builder().name("Bad").build();
        when(franchiseRepository.save(any(Franchise.class)))
                .thenReturn(Mono.error(new RuntimeException("db down")));

        StepVerifier.create(useCase.createFranchise(input))
                .expectErrorMessage("db down")
                .verify();
    }

    @Test
    void getFranchiseById_existing_returnsFranchise() {
        Franchise f = Franchise.builder().id("1").name("A").build();
        when(franchiseRepository.findById("1")).thenReturn(Mono.just(f));

        StepVerifier.create(useCase.getFranchiseById("1"))
                .expectNext(f)
                .verifyComplete();
    }

    @Test
    void getFranchiseById_notFound_emitsFranchiseNotFoundException() {
        when(franchiseRepository.findById("X")).thenReturn(Mono.empty());

        StepVerifier.create(useCase.getFranchiseById("X"))
                .expectErrorSatisfies(err -> {
                    assertThat(err).isInstanceOf(FranchiseNotFoundException.class);
                    assertThat(err.getMessage()).contains("X");
                })
                .verify();
    }

    @Test
    void updateFranchiseName_existing_updatesAndSaves() {
        Franchise existing = Franchise.builder().id("1").name("Old").build();
        Franchise updatedSaved = Franchise.builder().id("1").name("New").build();
        when(franchiseRepository.findById("1")).thenReturn(Mono.just(existing));
        when(franchiseRepository.save(any(Franchise.class))).thenReturn(Mono.just(updatedSaved));

        StepVerifier.create(useCase.updateFranchiseName("1", "New"))
                .expectNext(updatedSaved)
                .verifyComplete();

        ArgumentCaptor<Franchise> captor = ArgumentCaptor.forClass(Franchise.class);
        verify(franchiseRepository).save(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("New");
    }

    @Test
    void updateFranchiseName_notFound_emitsError() {
        when(franchiseRepository.findById("X")).thenReturn(Mono.empty());

        StepVerifier.create(useCase.updateFranchiseName("X", "New"))
                .expectError(FranchiseNotFoundException.class)
                .verify();
    }

    @Test
    void deleteFranchise_delegatesToRepository() {
        when(franchiseRepository.deleteById("1")).thenReturn(Mono.empty());

        StepVerifier.create(useCase.deleteFranchise("1"))
                .verifyComplete();

        verify(franchiseRepository).deleteById("1");
    }

    @Test
    void deleteFranchise_propagatesError() {
        when(franchiseRepository.deleteById("X"))
                .thenReturn(Mono.error(new RuntimeException("fail")));

        StepVerifier.create(useCase.deleteFranchise("X"))
                .expectErrorMessage("fail")
                .verify();
    }
}


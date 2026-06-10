package co.com.bancolombia.usecase.franchise;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.branch.gateways.BranchRepository;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.franchise.gateways.FranchiseRepository;
import co.com.bancolombia.usecase.franchise.exceptions.BranchNotFoundException;
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
class BranchUseCaseTest {

    @Mock
    private BranchRepository branchRepository;

    @Mock
    private FranchiseRepository franchiseRepository;

    @InjectMocks
    private BranchUseCase useCase;

    private Franchise franchiseWith(Branch... branches) {
        return Franchise.builder().id("F1").name("Pizza")
                .branches(List.of(branches))
                .build();
    }

    @Test
    void getAllBranches_delegates() {
        Branch b = Branch.builder().id("B1").name("X").build();
        when(branchRepository.getAllBranches()).thenReturn(Flux.just(b));

        StepVerifier.create(useCase.getAllBranches())
                .expectNext(b)
                .verifyComplete();
    }

    @Test
    void createFranchise_appendsBranchWithGeneratedId_andSaves() {
        Branch input = Branch.builder().name("Centro").build();
        Franchise existing = franchiseWith(Branch.builder().id("OLD").name("A").build());
        when(franchiseRepository.findById("F1")).thenReturn(Mono.just(existing));
        when(franchiseRepository.save(any(Franchise.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(useCase.createFranchise("F1", input))
                .assertNext(saved -> {
                    assertThat(saved.getBranches()).hasSize(2);
                    Branch newBranch = saved.getBranches().get(1);
                    assertThat(newBranch.getId()).isNotNull();
                    assertThat(newBranch.getName()).isEqualTo("Centro");
                })
                .verifyComplete();

        ArgumentCaptor<Franchise> captor = ArgumentCaptor.forClass(Franchise.class);
        verify(franchiseRepository).save(captor.capture());
        assertThat(captor.getValue().getBranches()).hasSize(2);
    }

    @Test
    void createFranchise_appendsToEmptyBranches() {
        Branch input = Branch.builder().name("Centro").build();
        Franchise existing = Franchise.builder().id("F1").name("Pizza").build(); // branches default empty list
        when(franchiseRepository.findById("F1")).thenReturn(Mono.just(existing));
        when(franchiseRepository.save(any(Franchise.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(useCase.createFranchise("F1", input))
                .assertNext(saved -> assertThat(saved.getBranches()).hasSize(1))
                .verifyComplete();
    }

    @Test
    void createFranchise_franchiseNotFound_emitsError() {
        when(franchiseRepository.findById("X")).thenReturn(Mono.empty());

        StepVerifier.create(useCase.createFranchise("X", Branch.builder().name("N").build()))
                .expectError(FranchiseNotFoundException.class)
                .verify();
    }

    @Test
    void getFranchiseById_existing_returnsBranch() {
        Branch target = Branch.builder().id("B1").name("Centro").build();
        Franchise franchise = franchiseWith(target, Branch.builder().id("B2").name("Norte").build());
        when(franchiseRepository.findById("F1")).thenReturn(Mono.just(franchise));

        StepVerifier.create(useCase.getFranchiseById("F1", "B1"))
                .expectNext(target)
                .verifyComplete();
    }

    @Test
    void getFranchiseById_branchNotFound_emitsBranchNotFoundException() {
        Franchise franchise = franchiseWith(Branch.builder().id("B2").name("Norte").build());
        when(franchiseRepository.findById("F1")).thenReturn(Mono.just(franchise));

        StepVerifier.create(useCase.getFranchiseById("F1", "B1"))
                .expectError(BranchNotFoundException.class)
                .verify();
    }

    @Test
    void getFranchiseById_franchiseNotFound_emitsFranchiseNotFoundException() {
        when(franchiseRepository.findById("F1")).thenReturn(Mono.empty());

        StepVerifier.create(useCase.getFranchiseById("F1", "B1"))
                .expectError(FranchiseNotFoundException.class)
                .verify();
    }

    @Test
    void updateFranchiseName_existingBranch_updatesNameAndSaves() {
        Branch b1 = Branch.builder().id("B1").name("Old").build();
        Branch b2 = Branch.builder().id("B2").name("Other").build();
        Franchise franchise = franchiseWith(b1, b2);
        when(franchiseRepository.findById("F1")).thenReturn(Mono.just(franchise));
        when(franchiseRepository.save(any(Franchise.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(useCase.updateFranchiseName("F1", "B1", "New"))
                .assertNext(saved -> {
                    Branch updated = saved.getBranches().stream()
                            .filter(b -> "B1".equals(b.getId())).findFirst().orElseThrow();
                    assertThat(updated.getName()).isEqualTo("New");
                    Branch other = saved.getBranches().stream()
                            .filter(b -> "B2".equals(b.getId())).findFirst().orElseThrow();
                    assertThat(other.getName()).isEqualTo("Other");
                })
                .verifyComplete();
    }

    @Test
    void updateFranchiseName_branchNotFound_emitsBranchNotFoundException() {
        Franchise franchise = franchiseWith(Branch.builder().id("B2").name("Other").build());
        when(franchiseRepository.findById("F1")).thenReturn(Mono.just(franchise));

        StepVerifier.create(useCase.updateFranchiseName("F1", "B1", "New"))
                .expectError(BranchNotFoundException.class)
                .verify();
    }

    @Test
    void updateFranchiseName_franchiseNotFound_emitsError() {
        when(franchiseRepository.findById("F1")).thenReturn(Mono.empty());

        StepVerifier.create(useCase.updateFranchiseName("F1", "B1", "New"))
                .expectError(FranchiseNotFoundException.class)
                .verify();
    }

    @Test
    void deleteBranch_delegates() {
        when(branchRepository.deleteById("B1")).thenReturn(Mono.empty());

        StepVerifier.create(useCase.deleteBranch("B1"))
                .verifyComplete();

        verify(branchRepository).deleteById("B1");
    }
}


package co.com.bancolombia.mongo.helper;

import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.mongo.franchise.MongoDBFranchiseRepository;
import co.com.bancolombia.mongo.franchise.MongoRepositoryFranchiseAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class AdapterOperationsTest {

    @Mock
    private MongoDBFranchiseRepository repository;

    @Mock
    private ObjectMapper objectMapper;

    private MongoRepositoryFranchiseAdapter adapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(objectMapper.map(any(), any())).thenAnswer(inv -> inv.getArgument(0));
        adapter = new MongoRepositoryFranchiseAdapter(repository, objectMapper);
    }

    @Test
    void getAllFranchises_delegatesToFindAll() {
        Franchise f = Franchise.builder().id("1").name("A").build();
        when(repository.findAll()).thenReturn(Flux.just(f));

        StepVerifier.create(adapter.getAllFranchises())
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void deleteById_delegates() {
        when(repository.deleteById("key")).thenReturn(Mono.empty());

        StepVerifier.create(adapter.deleteById("key"))
                .verifyComplete();
    }
}

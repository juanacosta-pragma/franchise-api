package co.com.bancolombia.mongo.branch;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.branch.gateways.BranchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class StubBranchRepository implements BranchRepository {
    private final MongoRepositoryBranchAdapter mongoRepositoryAdapter;

    @Override
    public Flux<Branch> getAllBranches() {
        return mongoRepositoryAdapter.getAllBranches();
    }


    @Override
    public Mono<Branch> findById(String id) {
        return mongoRepositoryAdapter.findById(id);
    }

    @Override
    public Mono<Branch> save(Branch branch) {
        return mongoRepositoryAdapter.save(branch);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return mongoRepositoryAdapter.deleteById(id);
    }
}



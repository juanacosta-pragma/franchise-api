package co.com.bancolombia.mongo.branch;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.mongo.helper.AdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Flux;

public class MongoRepositoryBranchAdapter extends AdapterOperations<Branch, Branch, String, MongoDBBranchRepository> {

    public MongoRepositoryBranchAdapter(MongoDBBranchRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Branch.class));
    }

    public Flux<Branch> getAllBranches() {
        return super.findAll();
    }


}


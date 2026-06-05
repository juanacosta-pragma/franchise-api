package co.com.bancolombia.mongo.franchise;

import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.mongo.franchise.MongoDBFranchiseRepository;
import co.com.bancolombia.mongo.helper.AdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Flux;

public class MongoRepositoryFranchiseAdapter extends AdapterOperations<Franchise, Franchise, String, MongoDBFranchiseRepository> {

    public MongoRepositoryFranchiseAdapter(MongoDBFranchiseRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Franchise.class));
    }

    public Flux<Franchise> getAllFranchises() {
        return super.findAll();
    }


}


package co.com.bancolombia.r2dbc;


import co.com.bancolombia.model.capacity.BootcampCapacity;
import co.com.bancolombia.model.capacity.gateways.BootcampCapacityRepository;
import co.com.bancolombia.r2dbc.entity.BootcampCapacityEntity;
import co.com.bancolombia.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.List;

@Repository
public class BootcampCapacityReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        BootcampCapacity,
        BootcampCapacityEntity,
        Long,
        BootcampCapacityReactiveRepository> implements BootcampCapacityRepository {

    public BootcampCapacityReactiveRepositoryAdapter(BootcampCapacityReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, BootcampCapacity.class));
    }

    public Flux<BootcampCapacity> saveAllBootcampCapacity(List<BootcampCapacity> bootcampCapacity) {
        return super.saveAllEntities(Flux.fromIterable(bootcampCapacity));
    }
}

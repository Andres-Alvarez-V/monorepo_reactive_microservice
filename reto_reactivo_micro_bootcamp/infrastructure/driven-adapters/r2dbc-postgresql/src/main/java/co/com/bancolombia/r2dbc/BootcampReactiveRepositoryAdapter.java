package co.com.bancolombia.r2dbc;

import co.com.bancolombia.model.bootcamp.Bootcamp;
import co.com.bancolombia.model.bootcamp.gateways.BootcampRepository;
import co.com.bancolombia.r2dbc.entity.BootcampEntity;
import co.com.bancolombia.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;

@Repository
public class BootcampReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        Bootcamp,
        BootcampEntity,
        Long,
        BootcampReactiveRepository
> implements BootcampRepository {
    public BootcampReactiveRepositoryAdapter(BootcampReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Bootcamp.class));
    }



}

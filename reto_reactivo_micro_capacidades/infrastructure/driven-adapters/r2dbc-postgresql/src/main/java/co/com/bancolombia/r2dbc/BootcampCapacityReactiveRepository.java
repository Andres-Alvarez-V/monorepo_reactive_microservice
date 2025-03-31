package co.com.bancolombia.r2dbc;

import co.com.bancolombia.r2dbc.entity.BootcampCapacityEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface BootcampCapacityReactiveRepository extends ReactiveCrudRepository<BootcampCapacityEntity, Long>, ReactiveQueryByExampleExecutor<BootcampCapacityEntity> {
}
